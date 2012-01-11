var gmailStop = {
  
  hiddenEmails: [],
  stopTime: 0, // The time stopPage is called
  originalEmailCount: 0, // User's current email count
  
  // Saved here to save some processing time. My comp is weak...
  contentEle: null,
  titleEle: null,
  inboxEle: null,
  
  /**
   * Just a cleaner way to get the elements we need
   */
  __init: function(timestamp) {
    
    this.contentEle = document.getElementById("canvas_frame").contentDocument;
    this.titleEle = document.getElementsByTagName("head")[0].getElementsByTagName("title")[0];
    this.inboxEle = this.contentEle.getElementsByClassName("TK")[0].getElementsByTagName("a")[0];
    this.contentEle = this.contentEle.getElementById(":rr");
    
    // Create a time for testing
    // var targetDate = new Date();
    // targetDate.setDate(targetDate.getDate() - 3);
    this.stopPage(timestamp);
  },
  
  /**
   * Hide all unread emails from now on
   */
  stopPage: function(stopTime) {
    var that = this,
        oldTitle = this.inboxEle.getAttribute("title");
    
    // Initialize some object variables
    this.stopTime = stopTime;
    this.originalEmailCount = parseInt(oldTitle.replace(/[^\d]+/, ""), 10);
    
    // Hide emails first
    this.hideEmails();
    this.originalEmailCount -= this.hiddenEmails.length;
    
    // From now on, all incoming emails are stopped until stopTime is updated
    // This has somedown sides, everytime we navigate away from inbox hideEmails is called
    // Sometimes this might actually be what we want though....
    this.contentEle.addEventListener("DOMNodeInserted", function() {
      // TODO: Somehow this is called multiple times each update
      //console.log('need to hide again'); 
      that.hideEmails();
    });
    
    // This tries to remodify inbox # after it changes
    this.inboxEle.addEventListener("DOMSubtreeModified", function() {
      console.log('DOMSubtreeModified fired');
      that.inboxEle.setAttribute("title", this.originalEmailCount);
      that.inboxEle.innerHTML = "Index (" +  this.originalEmailCount + ")";
    });
  },
  
  hideEmails: function() {
    var newEmails = this.contentEle.getElementsByClassName("zE"),
        i = 0,
        emailsCount = newEmails.length,
        emailTime;
    
    // Hide emails after timestamp
    for (i; i < emailsCount; i++) {
      // This convert this email's time into timestamp
      emailTime = newEmails[i].getElementsByClassName("xW")[0];
      emailTime = emailTime.getElementsByTagName("span")[0].getAttribute("title");
      emailTime = emailTime.replace(" at ", " ");
      emailTime = new Date(emailTime);
      
      // Remove any new emails equal or after given timestamp  
      if ( emailTime.getTime() >= this.stopTime && 
           newEmails[i].style.display !== "none" ) {
        
        newEmails[i].style.display = "none";

        // Add to our list of hidden emails
        if ( !this.containsEmail(newEmails[i]) ) {
          console.log('one email hidden');
          this.hiddenEmails.push(newEmails[i]);
        }
      }
    }
    console.log(this.hiddenEmails);
    
    // Make sure inbox count and title stays same
    console.log(this.inboxEle);
    this.inboxEle.setAttribute("title", this.originalEmailCount);
    this.inboxEle.innerHTML = "Index (" +  this.originalEmailCount + ")";

    // Make sure our page's header's number stays same
    this.titleEle.innerHTML = this.titleEle.innerHTML.replace(/\d+/, this.originalEmailCount);
  },
  
  /**
   * Show the emails that we have blocked
   */
  updateEmails: function() {
    var i = 0,
        max = this.hiddenEmails.length;
        
    this.stopTime = new Date().getTime(); // Set now as the newest stop time
    this.originalEmailCount += max; // Update new emails count
    
    // First redisplay all hiddenEmails
    console.log(this.hiddenEmails);
    for (i; i < max; i++) {
      this.hiddenEmails[i].style.display = "";
    }
    this.hiddenEmails = []; // No more hidden email
    
    // Update inbox count
    console.log('updating email...');
    console.log(this.inboxEle);
    this.inboxEle.setAttribute("title", this.originalEmailCount);
    this.inboxEle.innerHTML = "Index (" +  this.originalEmailCount +")";

    // Update page's header's number
    this.titleEle.innerHTML = this.titleEle.innerHTML.replace(/\(\d+\)/, this.originalEmailCount);
  },
  
  /**
   * Helper function for figuring our if an email node is included already
   */
  containsEmail: function(email) {
    var i = 0, max = this.hiddenEmails.length;
    for (i; i < max; i++) {
      if (this.hiddenEmails[i] === email) {
        return true;
      }
    }
    return false;
  }
};

/**
 * My proudest invention yet to load script after gmail finishes loading!
 */
function startScriptOnPage(timestamp, noWait) {
  gmailStop.stopTime = timestamp;
  if ( noWait ) {
    startGmailStop();
  } else {
    document.getElementsByTagName("html")[0].addEventListener("DOMNodeInserted", startGmailStop);
  }
  
  function startGmailStop() {
    console.log('time to start the crap! for time ' + gmailStop.stopTime);
    gmailStop.__init(gmailStop.stopTime);
    document.getElementsByTagName("html")[0].removeEventListener("DOMNodeInserted", startGmailStop);

    // A little trick to not show the page unless we processed it
    document.getElementById("canvas_frame").style.display = "inherit";
  } 
}