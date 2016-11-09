if (window.console) {
  console.log("Welcome to your Play application's JavaScript!");
}

function requestPage(myURL, callback, tag) {

    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
            callback(xmlHttp.response, tag);
    }
    xmlHttp.open("GET", myURL, true);
    xmlHttp.send(null);
}

function createElements(httpResponse, tag) {

  var all = document.getElementsByClassName("post_headline");

  for (var i=0, max=all.length; i < max; i++) {
       all[i].innerText = httpResponse;
  }
}
function topFunction() {
  var returnval = requestPage("/top", createElements, "Top Post");
}

function trendingFunction() {
  var returnval = requestPage("/trending", createElements, "Trending Post");
}

function hashtagFunction(hashtag) {
  var returnval = requestPage("/hashtag/" + hashtag, createElements, "Hashtag Post");
}
