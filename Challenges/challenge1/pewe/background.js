/* generic error handler */
function onError(error) {
  console.log(error);
}


function onrequest(req) {
  // This function will be called everytime the browser is about to send out an http or https request.
  // The req variable contains all information about the request.
  // If we return {}  the request will be performed, without any further changes
  // If we return {cancel:true} , the request will be cancelled.
  // If we return {requestHeaders:req.requestHeaders} , any modifications made to the requestHeaders (see below) are sent.

  //Create blacklist
  var blacklist = ["whatbrowser.org/javascripts/lib.min.js", "krxd","pagead", "doubleclick",
    "visualwebsiteoptimizer", "edigitalsurvey", "effectivemeasure"];

  //Block blacklisted items
  if(shouldBlock(req, blacklist)) {
    return {cancel:true};
  }


  // let's do something special if an image is loaded:
  if (req.type=="image") {
     console.log("Ooh, it's a picture!");
  }

  // log what file we're going to fetch:
  console.log("Loading: " + req.method + " " + req.url + " "+ req.type);

  hideBrowser(req.requestHeaders);
  removeCookies(req.requestHeaders);
  //printHeaders(req.requestHeaders);

  return {requestHeaders:req.requestHeaders};

}


// no need to change the following, it just makes sure that the above function is called whenever the browser wants to fetch a file
browser.webRequest.onBeforeSendHeaders.addListener(
  onrequest,
  {urls: ["<all_urls>"]},
  ["blocking", "requestHeaders"]
);

/** OWN FUNCTIONS **/

/*
* Print all headers.
*/
function printHeaders(headers) {
  console.log("Headers: ");
  for(var i = 0; i < headers.length; i++) {
    console.log("  Name: " + headers[i].name + ", value: " + headers[i].value);
  }

}

/*
* Return value of object which has name 'Host'.
*/
function getHost(headers) {
  for(var i = 0; i < headers.length; i++) {
    if(headers[i].name == "Host") {
      return headers[i].value;
    }
  }
  return undefined;
}

/*
* Set Coockie parameter to undefined.
*/
function removeCookies(headers) {
  updateHeaders(headers,"Cookie",undefined);
}

/*
* Set User-Agent header parameter to undefined.
*/
function hideBrowser(headers) {
  updateHeaders(headers,"User-Agent",undefined);
}

/*
* Update headers array, if an element has 'name' parameter
* which equals 'key' then the 'value' parameter is set to 'newValue'.
* Note: Assumes 'name' values are unique.
*/
function updateHeaders(headers,key,newValue) {
  for(var i = 0; i < headers.length; i++) {
    if(headers[i].name == key) {
      headers[i].value = newValue;
      return;
    }
  }
}

/*
* Check blacklist from local storage
*/
function checkBlackList(req, callback) {
  var result = callback(req);
  if(result) {
    return true;
  } else {
    return false;
  }
}

/*
* Check if the given url should be blocked according
* to the given blacklist, eg if it contains one of the blacklisted
* items.
*/
function shouldBlock(req, blacklist) {
  console.log("Blacklist: " + blacklist);
  for(var i = 0; i < blacklist.length; i++) {
    //console.log("Key " + key + " value " + result[key]);
    if(req.url.indexOf(blacklist[i]) != -1) {
      console.log("Blocking " + req.type + " at " + req.url + " as it contains \"" + blacklist[i] + "\"");
      return true;
    }
  }
  return false;
}
