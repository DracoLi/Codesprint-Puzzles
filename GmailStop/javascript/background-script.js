//firstTimeConfigurations();
var gmailStopper = {
  
  // Update interval
  interval: 60 * 1000,
  isRunning: false,
  lastUpdateTime: null,
  targetTabs: [],
  updatingTabs: [],
  
  // Used to fix a bug, hard to explain
  notRefreshHelper: false,
  
  /**
   * This starts gmailStopper on all gmail pages currently and monitors new tabs
   */
  startStopper: function() {
    var that = this;
    this.lastUpdateTime = new Date().getTime();
    
    // Start constant updates for all current tabs
    chrome.tabs.query({}, function (tabs) {
      var i = 0, tabsLen = tabs.length;
      for (i; i < tabsLen; i++) {
        tab = tabs[i];
        // somehow {title: "mail.google"} in query option don't work
        // Why chrome why???
        if ( that.isGmailUrl(tab.url) ) {
          console.log('one baby added.');
          that.targetTabs.push(tab.id);
          that.injectScript(tab.id, {updates: true, exists: true});
        }
      }
    });
    
    // Monitor future tabs
    chrome.tabs.onUpdated.addListener(function(tabId, changeInfo, tab) {
      var isTabGmail = that.isGmailUrl(tab.url),
          isNewGmail = that.isGmailUrl(changeInfo.url),
          existingIndex = that.targetTabs.indexOf(tab.id),
          isComplete = changeInfo.status == "complete";
          
      console.log("oldstatus: " + tab.status);
      console.log("newstatus: " + changeInfo.status);
      console.log("taburl: " + tab.url + " value: " + isTabGmail);
      console.log("changeInfo: " + changeInfo.url + " value: " + isNewGmail);
      console.log("existingIndex: " + existingIndex);
      
      // Handle not email or navigating away from gmail
      if ( !isTabGmail && !isNewGmail ) {
        if ( existingIndex >= 0 ) {
          // Navigated away from gmail
          that.targetTabs.splice(existingIndex, 1); 
          that.updatingTabs[tabId] = false;
          console.log('removes this gmail tab!');
          return;
        } else {
          // This is not a gmail page
          return;
        }
      }
      
      console.log(that.targetTabs);
      
      // Handles gmail first time
      if ( isTabGmail && isNewGmail && existingIndex < 0) {
        console.log('New gmail tab!');
        that.targetTabs.push(tab.id);
        that.injectScript(tab.id, {updates: true, exists: false});
        return;
      }
      
      // Handles gmail refresh
      if ( isTabGmail && changeInfo.url === undefined 
           && existingIndex >= 0 
           && isComplete && !that.notRefreshHelper ) {
        console.log('Gmail just refreshed!');
        that.injectScript(tab.id, {updates: false, exists: false});
      }
      
      // This is to fix a bug
      if ( isTabGmail && isNewGmail
           && existingIndex >= 0 
           && !isComplete ) {
        that.notRefreshHelper = !that.notRefreshHelper; // This is a trick to when when gmail refreshes
      }else {
        that.notRefreshHelper = false;
      }
    });
    
    // Listen to gmail tab removals
    chrome.tabs.onRemoved.addListener(function(tabId, removeInfo) {
      var existingIndex = that.targetTabs.indexOf(tabId);
      if ( existingIndex >= 0 ) {
        that.targetTabs.splice(existingIndex, 1); 
        that.updatingTabs[tabId] = false;
        console.log('removes this gmail tab!');
      }
    });
    
    this.isRunning = true;
  },
  
  /**
   * Removes stopper on all gmail pages currently and restart with new interval
   */
  updateInterval: function(newInterval) {
    this.interval = newInterval;
  },
  
  /**
   * Removes stopper on all pages, stop monitoring.
   */
  stopStopper: function() {
    this.targetTabs = [];
    this.isRunning = false;
  },
  
  injectScript: function(tabid, options) {
    var that = this,
        timeDiff = null,
        updates = options['updates'] || true,
        
        // Exists = true when user turns extension on while there are gmail open
        exists = options['exists'] || false; 
    
    // super-script starts stopper on that page,
    chrome.tabs.insertCSS(tabid, { code: "#canvas_frame { display: none; }" });
    console.log('css injected');
    chrome.tabs.executeScript(tabid, { file: "/javascript/super-script.js" }, function() {
      
      // Once our script is loaded we manually starts it
      chrome.tabs.executeScript(tabid, 
        { code: "startScriptOnPage(" + that.lastUpdateTime +", " + exists + ");" });
      
      // Start constant updates, needs to be in this closure
      // If interval changes, will only take effect next update loop
      var updateFunction = function () {
        
        // If this tab is no more, no need to update
        if ( that.targetTabs.indexOf(tabid) === -1) return;
        console.log('tab ' + tabid + " updated");
        
        // performs the update and set next update time
        chrome.tabs.executeScript(tabid, { code: "gmailStop.updateEmails();" });
        that.lastUpdateTime = new Date().getTime();
        setTimeout(updateFunction, that.interval);
      };
      
      // We need to make sure all gmail updates are in sync
      if ( !that.updatingTabs[tabid] ) {
        console.log('Register ' + tabid + " for update.");
        timeDiff = that.interval - (new Date().getTime() - that.lastUpdateTime);
        setTimeout(updateFunction, timeDiff);
        console.log(timeDiff);
        that.updatingTabs[tabid] = true;
      }
    });
  },
  
  /**
   * Helper to determine gmail url
   */
  isGmailUrl: function(url) {
    return /mail.google/i.test(url)
  },
  
  
};

function firstTimeConfigurations() {
  
}

function fetch_feed(url, callback) {
  
}

// Get feeds if our front-end popup view requests it
function onRequest(request, sender, callback) {
  if (request.action == 'fetch_feed') {
    fetch_feed(request.url, callback);
  }
}
chrome.extension.onRequest.addListener(onRequest);