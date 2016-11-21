if (window.console) {
  console.log("Welcome to your Play application's JavaScript!");
}

var numPages = 0;

window.onscroll = function() {
    var page = document.getElementById("page");
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
        // you're at the bottom of the page
        numPages++;
        topFunction(numPages);
    }
};

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
      postObj.appendChild(tr1);

      var td1a = document.createElement("div");
      td1a.style.display = "inline";
      td1a.style.float = "left";
      td1a.style.padding = "2px";
      td1a.style.color = "SteelBlue";
      tr1.appendChild(td1a);
      var posterList = document.createTextNode(post.source_);
      td1a.appendChild(posterList);

      var td1b = document.createElement("div");
      td1b.style.display = "inline";
      td1b.style.float = "right";
      td1b.style.padding = "2px";
      td1b.style.color = "LightSteelBlue";
      tr1.appendChild(td1b);
      var time = document.createTextNode(post.timestamp_);
      td1b.appendChild(time);

      var tr2 = document.createElement("div");
      postObj.appendChild(tr2);
      var td2a = document.createElement("div");
      tr2.appendChild(td2a);
      var textList = document.createTextNode(post.text_);
      td2a.appendChild(textList);

      var tr3 = document.createElement("div");
      postObj.appendChild(tr3);
      for (var j = 0; j < post.hashtag_.length; j++) {
        var td3 = document.createElement("div");
        td3.style.color = "SteelBlue";
        td3.style.display = "inline";
        tr3.appendChild(td3);

        var hashtag = document.createTextNode(post.hashtag_[j] + "  ");
        td3.appendChild(hashtag);
      }

      var tr4 = document.createElement("div");
      postObj.appendChild(tr4);
      var td4a = document.createElement("div");
      tr4.appendChild(td4a);
      var imgList = document.createTextNode(post.imgLink_);
      td4a.appendChild(imgList);

      var tr5 = document.createElement("div");
      postObj.appendChild(tr5);
      var bk = document.createElement("br");
      tr5.appendChild(bk);

      var td5a = document.createElement("div");
      td5a.style.display = "inline";
      td5a.style.padding = "10px";
      tr5.appendChild(td5a);
      var likes = document.createTextNode("Likes: " + post.numLikes_);
      td5a.appendChild(likes);

      var td5b = document.createElement("div");
      td5b.style.display = "inline";
      td5b.style.padding = "10px";
      tr5.appendChild(td5b);
      var shares = document.createTextNode("Shares: " + post.numShares_);
      td5b.appendChild(shares);

      var td5c = document.createElement("div");
      td5c.style.display = "inline";
      td5c.style.padding = "10px";
      tr5.appendChild(td5c);
      var comments = document.createTextNode("Comments: " + post.numComments_);
      td5c.appendChild(comments);

      // when the user clicks on the post it will bring them to the original
      var postLink = document.createElement("a");
      postLink.href = post.sourceLink_;
      postObj.appendChild(postLink);

      if(i % 3 == 0) {

        // start a new row, create three columns for it
        var row = document.createElement("div");
        row.className = "row";

        var col1 = document.createElement("div");
        col1.className = "col-sm-4";
        // put it in the first column
        col1.appendChild(postObj);
        row.appendChild(col1);

        var col2 = document.createElement("div");
        col2.className = "col-sm-4";
        row.appendChild(col2);

        var col3 = document.createElement("div");
        col3.className = "col-sm-4";
        row.appendChild(col3);

        container.appendChild(row);

      } else if(i % 3 == 1) {
        // put it in the second column
        var row = container.lastElementChild;
        var lastCol = row.lastElementChild;
        var col = lastCol.previousSibling;
        col.appendChild(postObj);

      } else {
        // put it in the third column
        var row = container.lastElementChild;
        var col = row.lastElementChild;
        col.appendChild(postObj);
      }

  }
}

function addElements(httpResponse, tag) {

  var postList = JSON.parse(httpResponse);

  var container = document.getElementById("container");

  for (var i = 0; i < postList.posts_.length; i++) {

      var post = postList.posts_[i];

      var postObj = document.createElement("div");
      postObj.id = "postObj";
      postObj.className = "postObj";

      var tr1 = document.createElement("div");
      postObj.appendChild(tr1);

      var td1a = document.createElement("div");
      td1a.style.display = "inline";
      td1a.style.float = "left";
      td1a.style.padding = "2px";
      td1a.style.color = "SteelBlue";
      tr1.appendChild(td1a);
      var posterList = document.createTextNode(post.source_);
      td1a.appendChild(posterList);

      var td1b = document.createElement("div");
      td1b.style.display = "inline";
      td1b.style.float = "right";
      td1b.style.padding = "2px";
      td1b.style.color = "LightSteelBlue";
      tr1.appendChild(td1b);
      var time = document.createTextNode(post.timestamp_);
      td1b.appendChild(time);

      var tr2 = document.createElement("div");
      postObj.appendChild(tr2);
      var td2a = document.createElement("div");
      tr2.appendChild(td2a);
      var textList = document.createTextNode(post.text_);
      td2a.appendChild(textList);

      var tr3 = document.createElement("div");
      postObj.appendChild(tr3);
      for (var j = 0; j < post.hashtag_.length; j++) {
        var td3 = document.createElement("div");
        td3.style.color = "SteelBlue";
        td3.style.display = "inline";
        tr3.appendChild(td3);

        var hashtag = document.createTextNode(post.hashtag_[j] + "  ");
        td3.appendChild(hashtag);
      }

      var tr4 = document.createElement("div");
      postObj.appendChild(tr4);
      var td4a = document.createElement("div");
      tr4.appendChild(td4a);
      var imgList = document.createTextNode(post.imgLink_);
      td4a.appendChild(imgList);

      var tr5 = document.createElement("div");
      postObj.appendChild(tr5);
      var bk = document.createElement("br");
      tr5.appendChild(bk);

      var td5a = document.createElement("div");
      td5a.style.display = "inline";
      td5a.style.padding = "10px";
      tr5.appendChild(td5a);
      var likes = document.createTextNode("Likes: " + post.numLikes_);
      td5a.appendChild(likes);

      var td5b = document.createElement("div");
      td5b.style.display = "inline";
      td5b.style.padding = "10px";
      tr5.appendChild(td5b);
      var shares = document.createTextNode("Shares: " + post.numShares_);
      td5b.appendChild(shares);

      var td5c = document.createElement("div");
      td5c.style.display = "inline";
      td5c.style.padding = "10px";
      tr5.appendChild(td5c);
      var comments = document.createTextNode("Comments: " + post.numComments_);
      td5c.appendChild(comments);

      // when the user clicks on the post it will bring them to the original
      var postLink = document.createElement("a");
      postLink.href = post.sourceLink_;
      postObj.appendChild(postLink);

      var lastRow = container.lastElementChild;
      var lastCol = lastRow.lastElementChild;

      if(lastCol.hasChildNodes()) {

        // start a new row, create three columns for it
        var newRow = document.createElement("div");

        var col1 = document.createElement("div");
        col1.className = "col-sm-4";
        // put it in the first column
        col1.appendChild(postObj);
        newRow.appendChild(col1);

        var col2 = document.createElement("div");
        col2.className = "col-sm-4";
        newRow.appendChild(col2);

        var col3 = document.createElement("div");
        col3.className = "col-sm-4";
        newRow.appendChild(col3);

        container.appendChild(newRow);

      } else if(lastCol.previousSibling.hasChildNodes()) {
        // put it in last column
        lastCol.appendChild(postObj);

      } else {
        // put it in the second to last column
        lastCol.previousSibling.appendChild(postObj);
      }

  }
}

function topFunction(pageNum) {
  if(pageNum == 0) {
    var returnval = requestPage("/top/" + pageNum, createElements, "Top Post");
  }
  else {
    var returnval = requestPage("/top/" + pageNum, addElements, "Top Post");
  }
}

function trendingFunction(pageNum) {
  var returnval = requestPage("/trending/" + pageNum, createElements, "Trending Post");
}

function hashtagFunction() {
  var returnval = requestPage("/tophashtags", createElements, "Hashtag Post");
}
