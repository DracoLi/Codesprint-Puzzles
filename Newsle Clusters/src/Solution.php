<?php

//$json = file_get_contents("input00.txt");
$json = fgets(STDIN);
$manager = new ArticlesManager();
$manager->populateArticles($json);
$manager->constructKeyWordsMatrix();
fwrite(STDOUT, printArray($manager->getClusters()));
exit(0);

/**
 * The main class for our solution
 * Note that this solution supports arbitrary article id in the json file
 */
class ArticlesManager {
  private $articles = array();
  private $keywordsMatrix = null;
  
  public function populateArticles($data) {
    $rawArticles = json_decode($data);
    foreach ($rawArticles as $oneArticle) {
      $id = $oneArticle->id;
      $title = $oneArticle->title;
      $content = $oneArticle->content;
      
      $article = new Article($id, $title, $content);
      $this->articles[] = $article;
    }
  }
  
  public function getClusters() {
    $clusters = array();
    $alreadyGrouped = array();
    
    $articleId = array_keys($this->keywordsMatrix);
    asort($articleId); // Make sure articles are sorted 
    $currentCluster = -1;
    for ($i = 0; $i < count($articleId); $i++) {
      $sourceId = $articleId[$i];
      
      // Create a new cluster if this is article is not grouped
      if ( !in_array($sourceId, $alreadyGrouped) ) {
        $currentCluster++;
        $clusters[$currentCluster] = array();
        $clusters[$currentCluster][] = $sourceId;
        $alreadyGrouped[] = $sourceId;
      }
      
      $matchesIds = array_keys($this->keywordsMatrix[$sourceId]);
      asort($matchesIds);
      for ($j = 0; $j < count($matchesIds); $j++) {
        $targetId = $matchesIds[$j];
        
        // Skip already grouped articles
        if ( in_array($targetId, $alreadyGrouped) ) {
          continue;
        }
        
        // Add all articles with more than 2 keyword matches to source cluster
        if ($this->keywordsMatrix[$sourceId][$targetId] >= 2) {
          $clusters[$currentCluster][] = $targetId;
          $alreadyGrouped[] = $targetId;
        }
      }
    }
    return $clusters;
  }
  
  /**
   * We construct a matrix array of 3 of matches for every article
   * This method's time complexity is at least O(n^2).
   */
  public function constructKeywordsMatrix() {
    $this->keywordsMatrix = array();
    
    // For every article id we have, we get its matches when compared to others
    $articlesIds = array_keys($this->articles);
    for ($i = 0; $i < count($articlesIds); $i++) {
      $sourceId = $articlesIds[$i];
      
      // Create container for this if null
      if ( !$this->keywordsMatrix[$sourceId] ) {
        $this->keywordsMatrix[$sourceId] = array();
      }
      
      // Scan source article with the rest of articles
      for ($j = $i + 1; $j < count($articlesIds); $j++) {
        $targetId = $articlesIds[$j];
        
        // Get matches for these two articles
        $sourceArticle = $this->articles[$sourceId];
        $targetArticle = $this->articles[$targetId];
        $matches = ArticlesManager::matchingKeywords($sourceArticle, $targetArticle);
        
        // Reduce the space of sparse matrixes by ignoring no matches
        if ( count($matches) == 0 ) continue;
        
        // Create container for the other matrix
        if ( !$this->keywordsMatrix[$targetId] ) {
          $this->keywordsMatrix[$targetId] = array();
        }
        
        // Fill in matrix, value is the amount of matches
        $matchesCount = count($matches);
        $this->keywordsMatrix[$sourceId][$targetId] = $matchesCount;
        $this->keywordsMatrix[$targetId][$sourceId] = $matchesCount;
      }
    }
  }
  
  /**
   * Find matches, runs in O(nlogn)
   */
  public static function matchingKeywords($a, $b) {
    $matches = array();
    for ($i = 0; $i < count($b->keywords); $i++) {
      if ( in_array($b->keywords[$i], $a->keywords) ) {
        $matches[] = $b->keywords[$i];
      }
    }
    return $matches;
  }
  
  /**
   * Print contents of the matrix
   */
  public function printKeywordsMatrix() {
    $articleIDs = array_keys($this->keywordsMatrix);
    asort($articleIDs);
    for ($i = 0; $i < count($articleIDs); $i++) {
      fwrite(STDOUT, "$i:");
      $thisArticle = $this->keywordsMatrix[$articleIDs[$i]];
      $thisIds = array_keys($thisArticle);
      for ($j = 0; $j < count($thisIds); $j++) {
        $targetId = $thisIds[$j];
        fwrite(STDOUT, $targetId . "->" . $thisArticle[$targetId] . " ");
      } 
      fwrite(STDOUT, "\n");
    }
  }
  
  public function printArticles() {
    foreach ($this->articles as $article) {
      fwrite(STDOUT, $article);
    }
  }
}

/**
 * This class represent one article.
 * However if we only using this class to figure out clusters, we can trim it down alot.
 */
class Article {
	public $keywords, $id, $title, $content;
	private $blackList;
	
	public function __construct($id, $title, $content) {
	  $this->id = $id;
		$this->title = $title;
		$this->content = $content;
		$this->blackList = array("however", "incase"); // Just an example
		$this->scanKeyWords();
	}
	
	private function scanKeyWords() {
	  $this->keywords = array();
	  
		// Simple scanner - get keywords from title according to length and blacklist
		$titleWords = preg_replace('/[^a-zA-Z\s+]/i', "", $this->title);
		$titleWords = explode(" ", trim($titleWords));
		foreach ($titleWords as $word) {
		  if ( strlen($word) >= 4 && !in_array($word, $this->blackList) ) {
		    $this->keywords[] = strtolower($word);
		  }
		}
		
		// Advanced scanner
	}
	
	/**
	 * Draco's little helper
	 */
	public function __toString()
  {
    $result = "id: " . $this->id . "\n";
    $result .= "title: " . $this->title . "\n";
    $result .= "content: " . $this->content . "\n";
    $result .= "keywords: " . print_r($this->keywords, true) . "\n\n";
    return $result;
  }
}

/**
 * Just a helper
 */
function printArray($data) {
  $output = "[";
  foreach ($data as $oneCluster) {
    $output .= "[";
    foreach ($oneCluster as $oneArticle) {
      $output .= "$oneArticle" . ", ";
    }
    $output = substr($output, 0, -2);
    $output .= "], ";
  }
  $output = substr($output, 0, -2);
  $output .= "]";
  return $output;
}

?>