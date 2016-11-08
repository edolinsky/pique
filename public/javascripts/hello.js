if (window.console) {
  console.log("Welcome to your Play application's JavaScript!");
}

function requestPage(myURL) {

    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
            callback(xmlHttp.responseText);
    }
    xmlHttp.open("GET", myURL, true);
    xmlHttp.send(null);
}

function topFunction() {

    requestPage("/top");

    var all = document.getElementsByClassName("post_headline");

    for (var i=0, max=all.length; i < max; i++) {
         all[i].innerText = "Top Post";
    }
}

function trendingFunction() {

    requestPage("/trending");

    var all = document.getElementsByClassName("post_headline");

    for (var i=0, max=all.length; i < max; i++) {
         all[i].innerText = "Trending Post";
    }
}

function hashtagFunction(hashtag) {

    requestPage("/hashtag/" + hashtag);

    var all = document.getElementsByClassName("post_headline");

    for (var i=0, max=all.length; i < max; i++) {
         all[i].innerText = "Post Containing Hashtag";
    }
}
