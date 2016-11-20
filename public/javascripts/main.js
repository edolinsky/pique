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

      var tr1 = document.createElement("div");

      var td1a = document.createElement("div");
      td1a.style.display = "inline";
      td1a.style.float = "left";
      td1a.style.padding = "2px";
      td1a.style.color = "SteelBlue";
      var posterList = document.createTextNode(post.source_);
      td1a.appendChild(posterList);
      tr1.appendChild(td1a);

      var td1b = document.createElement("div");
      td1b.style.display = "inline";
      td1b.style.float = "right";
      td1b.style.padding = "2px";
      td1b.style.color = "LightSteelBlue";
      var time = document.createTextNode(post.timestamp_);
      td1b.appendChild(time);
      tr1.appendChild(td1b);
      postObj.appendChild(tr1);

      var tr4 = document.createElement("div");
      var td4a = document.createElement("div");
      if(post.imgLink_ != "N/A") {
        var bk = document.createElement("br");
        tr4.appendChild(bk);

        var imgLink = document.createElement("img");
        imgLink.src = post.imgLink_;
        imgLink.height = "160";
        td4a.appendChild(imgLink);
        td4a.style.padding = "10px";
        td4a.style.textAlign = "center";
        tr4.appendChild(td4a);
        postObj.appendChild(tr4);

        var tr2 = document.createElement("div");
        var td2a = document.createElement("div");
        td2a.style.height = "62px";
        td2a.style.overflow = "hidden";
        var textList = document.createTextNode(post.text_);
        td2a.appendChild(textList);
        tr2.appendChild(td2a);
        postObj.appendChild(tr2);

        var tr3 = document.createElement("div");
        tr3.style.height = "25px";
        tr3.style.overflow = "hidden";
        if(post.hashtag_[0] != "N/A") {
          for (var j = 0; j < post.hashtag_.length; j++) {
            var td3 = document.createElement("div");
            td3.style.color = "SteelBlue";
            td3.style.display = "inline";

            var hashtag = document.createTextNode(post.hashtag_[j] + "  ");
            td3.appendChild(hashtag);
            tr3.appendChild(td3);
          }
        }
        postObj.appendChild(tr3);

      } else {
        var blank = document.createTextNode(" ");
        td4a.appendChild(blank);
        tr4.appendChild(td4a);
        postObj.appendChild(tr4);

        var tr2 = document.createElement("div");
        var bk = document.createElement("br");
        tr2.appendChild(bk);
        var td2a = document.createElement("div");
        td2a.style.fontSize = "150%";
        var textList = document.createTextNode(post.text_);
        td2a.appendChild(textList);
        tr2.appendChild(td2a);
        postObj.appendChild(tr2);

        var tr3 = document.createElement("div");
        if(post.hashtag_[0] != "N/A") {
          for (var j = 0; j < post.hashtag_.length; j++) {
            var td3 = document.createElement("div");
            td3.style.color = "SteelBlue";
            td3.style.display = "inline";
            td3.style.fontSize = "200%";
            var hashtag = document.createTextNode(post.hashtag_[j] + "  ");
            td3.appendChild(hashtag);
            tr3.appendChild(td3);
          }
        }
        postObj.appendChild(tr3);
      }

      var tr5 = document.createElement("div");
      tr5.style.position = "absolute";
      tr5.style.bottom = "0";
      tr5.style.paddingBottom = "20px";

      var td5a = document.createElement("div");
      td5a.style.display = "inline";
      td5a.style.padding = "10px";
      var likes = document.createTextNode("Likes: " + post.numLikes_);
      td5a.appendChild(likes);
      tr5.appendChild(td5a);

      var td5b = document.createElement("div");
      td5b.style.display = "inline";
      td5b.style.padding = "10px";
      var shares = document.createTextNode("Shares: " + post.numShares_);
      td5b.appendChild(shares);
      tr5.appendChild(td5b);

      var td5c = document.createElement("div");
      td5c.style.display = "inline";
      td5c.style.padding = "10px";
      var comments = document.createTextNode("Comments: " + post.numComments_);
      td5c.appendChild(comments);
      tr5.appendChild(td5c);
      postObj.appendChild(tr5);

      // when the user clicks on the post it will bring them to the original
      var postLink = document.createElement("a");
      postLink.href = post.sourceLink_;
      postObj.appendChild(postLink);

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
