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

//  Post {
//      string id = 1;
//      string timestamp = 2;

//      repeated string source = 3;
//      repeated string source_link = 4;

//      int32 popularity_score = 5;
//      int32 popularity_velocity = 6;

//      int32 num_comments = 7;
//      int32 num_shares = 8;
//      int32 num_likes = 9;
//      repeated string hashtag = 10;

//      repeated string text = 11;
//      repeated string img_link = 12;
//      repeated string ext_link = 13;
//  }

  var postList = JSON.parse(httpResponse);

  var page = document.getElementById("page");

  // clear out current posts if any exist on the page already
  if (document.contains(document.getElementById("container"))) {
      var container = document.getElementById("container");
      while (container.firstChild) {
          container.removeChild(container.firstChild);
      }
      container.remove();
  }

  var container = document.createElement("div");
  container.id = "container";
  container.className = "container";

  page.appendChild(container);

  for (var i = 0; i < postList.posts_.length; i++) {
      var post = postList.posts_[i];

      var postObj = document.createElement("div");
      postObj.id = "postObj";
      postObj.className = "postObj";

      var text = document.createTextNode(post.text_[0]);
      postObj.appendChild(text);

      if(i % 3 == 0) {
        // start a new row, create three columns for it
        var row = document.createElement("div");
        row.className = "row";

        container.appendChild(row);

        var col1 = document.createElement("div");
        col1.className = "col-sm-4";
        col1.id = "col" + i + "a";
        row.appendChild(col1);

        var col2 = document.createElement("div");
        col2.className = "col-sm-4";
        col2.id = "col" + i + "b";
        row.appendChild(col2);

        var col3 = document.createElement("div");
        col3.className = "col-sm-4";
        col3.id = "col" + i + "c";
        row.appendChild(col3);

        // put it in the first column
        col1.appendChild(postObj);

      } else if(i % 3 == 1) {

        var rowNum = i-1;

        // put it in the second column
        var col = document.getElementById("col" + rowNum + "b");
        col.appendChild(postObj);

      } else {

        var rowNum = i-2;

        // put it in the third column
        var col = document.getElementById("col" + rowNum + "c");
        col.appendChild(postObj);
      }

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
