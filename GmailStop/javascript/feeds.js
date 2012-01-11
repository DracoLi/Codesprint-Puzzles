/**
 * Facebook share URL.
 */
var FB_SHARE_URL = "http://www.facebook.com/sharer.php?u=";

/**
 * Twitter share URL
 */
var TWITTER_SHARE_URL = "http://www.twitter.com/share?&url=";

/**
 * This specifies when a feed is considered old
 */
var daysUntilOld = 8;

/**
 * Utitlity prototype functions
 */
String.prototype.capitalize = function() {
    return this.charAt(0).toUpperCase() + this.slice(1);
}

window.storageManager = {
	cachedFeedsIdentifier: 'cachedFeeds',
	feedsStateIdentifier: 'feedsState',
	userCategoriesIdentifier: 'userCategories',
	
	setCachedFeeds: function(feeds) {
		window.localStorage.setItem(this.cachedFeedsIdentifier, JSON.stringify(feeds));
	},
	getCachedFeeds: function() {
		var feeds = window.localStorage.getItem(this.cachedFeedsIdentifier);
		return JSON.parse(feeds);
	},
	setUserCategories: function(categories) {
		window.localStorage.setItem(this.userCategoriesIdentifier, JSON.stringify(categories));
	},
	getUserCategories: function() {
		var userCategories = window.localStorage.getItem(this.userCategoriesIdentifier);
		return JSON.parse(userCategories);
	},
	setFeedsState: function(feedsState) {
		var jsonData = JSON.stringify(feedsState);
		window.localStorage.setItem(this.feedsStateIdentifier, jsonData);
	},
	setFeedState: function(feedID, isRead) {
		var allFeeds = this.getFeedsState() || new Object();
		if ( isRead ) {
			allFeeds[feedID] = 'read';
		}else if ( allFeeds[feedID] ) {
			delete allFeeds[feedID];
		}
		this.setFeedsState(allFeeds);
	},
	getFeedsState: function() {
		var jsonData = window.localStorage.getItem(this.feedsStateIdentifier);
		return jsonData ? JSON.parse(jsonData) : null;
	},
	getFeedState: function(feedID) {
		var allFeeds = this.getFeedsState();
		return allFeeds ? allFeeds[feedID] : null;
	}
}

window.feedsManager = {
	
	userCategories: window.storageManager.getUserCategories(),
	
	restoreSectionState: function() {
		var $feedsSections = $('#feeds section');
		
		this.refreshUserCategories();
		var userCategories = this.userCategories;
		
		// Get state of each category and set it
		$feedsSections.each(function(index) {
			var $targetFeeds = $(this).find('.sectionContent');
					sectionKey = ( $targetFeeds && $targetFeeds.length > 0 ) ? $(this).attr('id') : null;
			
			// Ignore empty categories.
			if ( $targetFeeds && sectionKey != null ) {
				var isOpen = userCategories[sectionKey].state ? true : false;
				isOpen ? $targetFeeds.show() : $targetFeeds.hide();
			}
		});
	},
	collapseAllFeeds: function() {
		var $feedsSections = $('#feeds section'),
				userCategories = this.userCategories;
		
		// Loop through all sections and hide them. Update userCategory
		$feedsSections.each(function(index) {
			var $targetFeeds = $(this).find('.sectionContent');
					sectionKey = ( $targetFeeds && $targetFeeds.length > 0 ) ? $(this).attr('id') : null;
			if ( sectionKey != null ) {
				$targetFeeds.hide();
				delete userCategories[sectionKey].state
			}
		});
		
		// Update categories
		window.storageManager.setUserCategories(userCategories);
	},
	refreshUserCategories: function() {
		this.userCategories = window.storageManager.getUserCategories();
	},
	updateSectionState: function(sectionKey, state) {
		if ( state ) {
			this.userCategories[sectionKey].state = state;
		}else {
			delete this.userCategories[sectionKey].state
		}
		window.storageManager.setUserCategories(this.userCategories);
	},
	updateSectionIndex: function(sectionKey, index) {
		this.userCategories[sectionKey].index = index;
		window.storageManager.setUserCategories(this.userCategories);
	},
	fetch_feed: function(url) {
		var that = this;
		
		// Fetch feeds according to user configurations
	  chrome.extension.sendRequest({'action': 'fetch_feed', 
																	'url': window.appHelper.constructIntializeURL()},
	    function(response) {
				var feeds = {};

				// Put this here for now...
				window.appHelper.constructSections();

				if ( response && response.length > 0 ) {
					// Cache new JSON response
					window.storageManager.setCachedFeeds(response);
					feeds = $.parseJSON(response);
				}else {
					// If we cannot get server feeds, we get cached
					feeds = window.storageManager.getCachedFeeds();
					feeds = $.parseJSON(feeds);
				}
				
				if ( feeds ) {
					// Display feeds - should be contained in a object
		      that.display_feeds(feeds);
				}
	    }
	  );
	},
	display_feeds: function(feeds) {
		var $feedsSection = $('#content #feeds'),
				newFeeds = new Array(),
				userCategories = window.storageManager.getUserCategories(),
				sectionHTML = '<ul class="sectionContent unstyled"></ul>'

		// Convert feeds into real array. Each element, 0 is key, 1 is data
		for (var key in feeds) { newFeeds.push([key, feeds[key]]); }
		
		for (var i = 0; i < newFeeds.length; i++) {
			// Get our variables for this category
			var oneKey = newFeeds[i][0],
					catFeeds = newFeeds[i][1],
					totalFeeds = catFeeds.length,
					thisCategory = userCategories[oneKey],
					$newSection = $feedsSection.find('#' + oneKey);
			
			// Skip empty sections
			if (totalFeeds == 0) continue;

			// Create new content section
			var $newContent = $newSection.append(sectionHTML).find('.sectionContent');

			// Add feeds to the new section
			totalFeeds = Math.min(parseInt(thisCategory['max']), totalFeeds);
			for (var j = 0; j < totalFeeds; j++) {
				var feed = catFeeds[j],
						state = window.storageManager.getFeedState(feed.id) || '';
						html = '<li class="feed ' + state + '" data-identifier="' + feed.id + '"> \
											<h5> \
												<a href="' + feed.link + '">' + feed.title + '</a> \
												<small class="time" data-timestamp="' + feed.pubDate + '"> \
												' + window.appHelper.facebookTime(feed.pubDate) + '</small> \
											</h5> \
									  </li>';
				$newContent.append(html);
			}
			
			// Update total feeds
			$newSection.find('.totalFeeds').html(totalFeeds);
		}
		
		// Restore state of sections - required after content is obtained
		window.feedsManager.restoreSectionState();
	}
}

window.appHelper = {
	/**
	 * Opens new window either of facebook or twitter.
	 * @param {String} id Specified whether to share news on Facebook or Twitter
	 * @param {String} url Contains URL of the News to be shared.
	 */
	openNewShareWindow: function(id, url) {
	  var newsUrl = url.substring(url.indexOf('&url=') + 5);
	  var openUrl;
	  switch (id) {
	    case 'fb':
	      openUrl = FB_SHARE_URL;
	      break;
	    case 'twitter':
	      openUrl = TWITTER_SHARE_URL;
	      break;
	  }
	  window.open(openUrl + newsUrl, '_blank', 'resizable=0, scrollbars=0, width=690, height=415');
	},

	/**
	 * Open up window for emails!
	 */
	executeMailto: function(email, subject, body) {
	  var action_url = "mailto:" + email + "?";
	  if (subject.length > 0)
	    action_url += "subject=" + encodeURIComponent(subject) + "&";

	  if (body.length > 0) {
	    action_url += "body=" + encodeURIComponent(body);
	  }
		chrome.tabs.getSelected(null, function(tab) {
			chrome.tabs.update(tab.id, { url: action_url });
		});
	},

	/**
	 * Construct the url to get content from server
	 */
	constructIntializeURL: function() {
		var userCategories = window.storageManager.getUserCategories(),
				requestUrl = 'http://commercefeeds.localhost/api/getFeeds.php?',
				firstTime = true;

		// Construct feed url according to user categories
		for (var category in userCategories) {
			if (!firstTime) { 
				requestUrl += "&"; 
			}else { 
				firstTime = false;
			}
			requestUrl += category + "=" + parseInt(userCategories[category]['max']);
		}
		return requestUrl;
	},
	
	constructFeedURL: function(feedId, feedCategory) {
		var requestUrl = 'http://commercefeeds.localhost/api/getFeed.php?';
		requestUrl += 'id=' + feedId;
		requestUrl += '&category=' + feedCategory;
	},

	constructSections: function() {
		var userCategories = window.storageManager.getUserCategories();
		userCategories = this.objectToArray(userCategories);
		
		// Sort the categories
		userCategories.sort(function (a, b) {
			var indexA = a[1]['index'],
					indexB = b[1]['index'];
			return indexA - indexB;
		});
		
		// Create sections from user's list
		var catHTML = "";	
		for (var i = 0; i < userCategories.length; i++) {
			var catContent = userCategories[i][1],
					catID = userCategories[i][0],
					catName = catContent['name'] || catID;

			catHTML += '<section id="' + catID + '">\
								 	<div class="sectionHeader noSelect"> \
										<h4>' + catName + '<small> \
										<span class="totalFeeds">No</span> Feeds (max \
										<span class="maxFeeds">' + catContent['max'] + '</span>)</small></h4> \
									</div> \
								</section>';
		}

		// Append our sections
		$('#content #feeds').append(catHTML);
	},

	// This method turns a timestamp into facebook like time. ex: 3hrs ago, 1 day ago
	facebookTime: function(timestamp) {
		var now = new Date(),
				feedTime = new Date(timestamp * 1000), // Since javascript's time is in miliseconds
				currentHour = now.getHours(),
				feedHour = feedTime.getHours(),
				currentMinutes = now.getMinutes(),
				feedMinutes = feedTime.getMinutes();

		// Figure out if this time is within 1 day
		if ( feedTime != now ) {
			// Output number of days since feed
			var dateDiff = this.getDateDifference(now, feedTime);
			return dateDiff + 'd ago';
		}

		// Figure out if this time is within hours
		if ( currentHour != feedHour ) {
			return (currentHour - feedHour) + 'hr ago';
		}

		// Figure out if this time is within mintues
		if ( currentMinutes != feedMinutes ) {
			return (currentMinutes - feedMinutes) + 'min ago';
		}

		// If days, hours, minutes all matches, then its now
		return 'now';
	},

	getDateDifference: function(newDate, oldDate) {
		var diff = newDate - oldDate,
				daysDiff = diff / 1000 / 60 / 60 / 24;

		return parseInt(daysDiff); // Round down
	},
	
	objectToArray: function(obj) {
		var array = [];
		for (var key in obj) {
		    if (obj.hasOwnProperty(key)) {
		      array.push([key, obj[key]]);
		    }
		}
		return array;
	}
}