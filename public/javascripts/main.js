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

      var row1 = document.createElement("div");

      var row1Col1= document.createElement("div");
      row1Col1.style.display = "inline";
      row1Col1.style.float = "left";
      row1Col1.style.padding = "2px";
      row1Col1.style.color = "SteelBlue";
      var posterList = document.createTextNode(post.source_);
      row1Col1.appendChild(posterList);
      row1.appendChild(row1Col1);

      var row1Col2 = document.createElement("div");
      row1Col2.style.display = "inline";
      row1Col2.style.float = "right";
      row1Col2.style.padding = "2px";
      row1Col2.style.color = "LightSteelBlue";
      var time = document.createTextNode(post.timestamp_);
      row1Col2.appendChild(time);
      row1.appendChild(row1Col2);
      postObj.appendChild(row1);

      var row2 = document.createElement("div");
      var row2Col1 = document.createElement("div");
      if(post.imgLink_ != "N/A") {

        // if there is an image in the post
        var bk = document.createElement("br");
        row2.appendChild(bk);

        var imgLink = document.createElement("img");
        imgLink.src = post.imgLink_;
        imgLink.height = "160";
        row2Col1.appendChild(imgLink);
        row2Col1.style.padding = "10px";
        row2Col1.style.textAlign = "center";
        row2.appendChild(row2Col1);
        postObj.appendChild(row2);

        var row3 = document.createElement("div");
        var row3Col1 = document.createElement("div");
        row3Col1.style.height = "62px";
        row3Col1.style.overflow = "hidden";
        var textList = document.createTextNode(post.text_);
        row3Col1.appendChild(textList);
        row3.appendChild(row3Col1);
        postObj.appendChild(row3);

        var row4 = document.createElement("div");
        row4.style.height = "25px";
        row4.style.overflow = "hidden";
        if(post.hashtag_[0] != "N/A") {
          for (var j = 0; j < post.hashtag_.length; j++) {
            var row4Col = document.createElement("div");
            row4Col.style.color = "SteelBlue";
            row4Col.style.display = "inline";

            var hashtag = document.createTextNode(post.hashtag_[j] + "  ");
            row4Col.appendChild(hashtag);
            row4.appendChild(row4Col);
          }
        }
        postObj.appendChild(row4);

      } else {

        // if the post has no image

        var row3 = document.createElement("div");
        var bk = document.createElement("br");
        row3.appendChild(bk);

        var row3Col1 = document.createElement("div");
        row3Col1.style.fontSize = "150%";
        row3Col1.style.paddingTop = "20px";
        var textList = document.createTextNode(post.text_);
        row3Col1.appendChild(textList);
        row3.appendChild(row3Col1);
        postObj.appendChild(row3);

        var row4 = document.createElement("div");
        row4.style.paddingTop = "10px";
        if(post.hashtag_[0] != "N/A") {
          for (var j = 0; j < post.hashtag_.length; j++) {
            var row4Col = document.createElement("div");
            row4Col.style.color = "SteelBlue";
            row4Col.style.display = "inline";
            row4Col.style.fontSize = "200%";
            var hashtag = document.createTextNode(post.hashtag_[j] + "  ");
            row4Col.appendChild(hashtag);
            row4.appendChild(row4Col);
          }
        }
        postObj.appendChild(row4);
      }

      var row5 = document.createElement("div");
      row5.style.position = "absolute";
      row5.style.bottom = "0";
      row5.style.paddingBottom = "20px";

      var row5Col1 = document.createElement("div");
      row5Col1.style.display = "inline";
      row5Col1.style.padding = "10px";
      var likes = document.createTextNode("Likes: " + post.numLikes_);
      row5Col1.appendChild(likes);
      row5.appendChild(row5Col1);

      var row5Col2 = document.createElement("div");
      row5Col2.style.display = "inline";
      row5Col2.style.padding = "10px";
      var shares = document.createTextNode("Shares: " + post.numShares_);
      row5Col2.appendChild(shares);
      row5.appendChild(row5Col2);

      var row5Col3 = document.createElement("div");
      row5Col3.style.display = "inline";
      row5Col3.style.padding = "10px";
      var comments = document.createTextNode("Comments: " + post.numComments_);
      row5Col3.appendChild(comments);
      row5.appendChild(row5Col3);
      postObj.appendChild(row5);

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
