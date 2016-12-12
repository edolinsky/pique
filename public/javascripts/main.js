if (window.console) {
  console.log("Welcome to your Play application's JavaScript!");
}

var numPages = 0;

window.onscroll = function() {
    var page = document.getElementById("page");
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
        // you're at the bottom of the page
        numPages++;
        var postType = document.getElementById("PostType");

        if (postType.textContent == "Top Posts") {
          topFunction(numPages);
        } else if (postType.textContent == "Trending Posts") {
          trendingFunction(numPages);
        } else {
          // it's a hashtag where postType contains the hashtag name
          hashtagFunction(postType.textContent, numPages);
        }

    }
};

function searchHashtagItem(hashtagItem) {
    hashtagFunction(hashtagItem, 0);
}

function search(input, event) {
  if (event.keyCode === 13) {
      event.preventDefault();
      hashtagFunction(input.value, 0);
  }
}

function requestPage(myURL, callback, tag) {

    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
        console.log(xmlHttp.readyState);
        if (xmlHttp.status == 200) {
          if(xmlHttp.readyState == 4) {
            callback(xmlHttp.response, tag);
          }
        }
        // TODO: if no content returned, display error message
    }
    xmlHttp.open("GET", myURL, false);
    xmlHttp.send(null);
}

function getFormattedDate(time) {
    var date = new Date(time);
    var str = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " " +  date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();

    return str;
}

function createPosts(post) {
  var postObj = document.createElement("div");
  postObj.id = "postObj";
  postObj.className = "postObj";

  var row1 = document.createElement("div");

  var row1Col2 = document.createElement("div");
  row1Col2.style.display = "inline";
  row1Col2.style.padding = "2px";
  row1Col2.style.color = "LightSteelBlue";
  var time = document.createTextNode(getFormattedDate(post.timestamp_));
  row1Col2.appendChild(time);
  row1.appendChild(row1Col2);

  var row1Col3 = document.createElement("div");
  row1Col3.style.display = "inline";
  row1Col3.style.float = "right";
  row1Col3.style.padding = "2px";
  var source_link = document.createElement("a");
  source_link.href = post.sourceLink_;
  source_link.style.textDecoration = "none";
  source_link.style.color = "LightSteelBlue";
  var source = document.createTextNode("Source");
  if(post.sourceLink_[0].includes("reddit")) {
    source.textContent = "Reddit";
  }
  else if(post.sourceLink_[0].includes("twitter")) {
    source.textContent = "Twitter";
  }
  else if(post.sourceLink_[0].includes("imgur")) {
    source.textContent = "Imgur";
  }
  source_link.appendChild(source);
  var link_img = document.createElement("img");
  link_img.src = "/assets/images/link_arrow.jpeg";
  link_img.height = "20";
  link_img.style.paddingLeft = "3px";
  source_link.appendChild(link_img);
  row1Col3.appendChild(source_link);
  row1.appendChild(row1Col3);
  postObj.appendChild(row1);

  var row6 = document.createElement("div");
  var row6Col1 = document.createElement("div");
  row6Col1.style.padding = "2px";
  row6Col1.style.paddingTop = "8px";
  row6Col1.style.textAlign = "left";
  row6Col1.style.color = "SteelBlue";
  row6Col1.style.fontSize = "110%";
  row6Col1.style.width = "300px";
  row6Col1.style.overflow = "hidden";
  var posterList = document.createTextNode(post.source_);
  row6Col1.appendChild(posterList);
  row6.appendChild(row6Col1);
  postObj.appendChild(row6);

  var row2 = document.createElement("div");
  var row2Col1 = document.createElement("div");
  if(post.imgLink_ != "N/A") {

    // if there is an image in the post
    var bk = document.createElement("br");
    row2.appendChild(bk);

    var imgLink = document.createElement("img");
    imgLink.src = post.imgLink_;
    imgLink.id = "postImage";
    imgLink.style.height = "160px";
    row2Col1.appendChild(imgLink);
    row2Col1.style.paddingBottom = "20px";
    row2Col1.style.textAlign = "center";
    row2Col1.style.max_width = "325px";
    row2.appendChild(row2Col1);
    postObj.appendChild(row2);

    var row3 = document.createElement("div");
    var row3Col1 = document.createElement("div");
    row3Col1.style.height = "62px";
    row3Col1.style.overflow = "hidden";
    row3Col1.style.width = "300px";
    row3Col1.style.overflow = "hidden";
    var textList = document.createTextNode(post.text_);
    row3Col1.appendChild(textList);
    row3.appendChild(row3Col1);
    postObj.appendChild(row3);

    var row4 = document.createElement("div");
    row4.style.height = "25px";
    row4.style.overflow = "hidden";
    row4.style.width = "300px";
    row4.style.overflow = "hidden";
    if(post.hashtag_[0] != "N/A") {
      for (var j = 0; j < post.hashtag_.length; j++) {
        var row4Col = document.createElement("div");
        row4Col.style.color = "SteelBlue";
        row4Col.style.display = "inline";
        row4Col.id = "hashtagItem";
        row4Col.style.paddingRight = "5px";
        var hashtagText = document.createTextNode(post.hashtag_[j]);
        row4Col.appendChild(hashtagText);
        row4Col.setAttribute("onclick", "searchHashtagItem(this.textContent)");
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
    row3Col1.style.width = "300px";
    row6Col1.style.overflow = "hidden";
    var textList = document.createTextNode(post.text_);
    row3Col1.appendChild(textList);
    row3.appendChild(row3Col1);
    postObj.appendChild(row3);

    var row4 = document.createElement("div");
    row4.style.paddingTop = "10px";
    row4.style.width = "300px";
    row4.style.overflow = "hidden";
    if(post.hashtag_[0] != "N/A") {
      for (var j = 0; j < post.hashtag_.length; j++) {
        var row4Col = document.createElement("div");
        row4Col.style.color = "SteelBlue";
        row4Col.style.display = "inline";
        row4Col.style.fontSize = "200%";
        row4Col.style.paddingRight = "5px";
        row4Col.id = "hashtagItem";
        var hashtag = post.hashtag_[j];
        var hashtagText = document.createTextNode(hashtag);
        row4Col.appendChild(hashtagText);
        row4Col.setAttribute("onclick", "searchHashtagItem(this.textContent)");
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
  var num_likes;
  if(post.numLikes_ >= 10000) {
    num_likes = Math.round(post.numLikes_/1000) +'k';
  }
  else {
    num_likes = post.numLikes_;
  }
  var likes = document.createTextNode("Likes: " + num_likes);
  row5Col1.appendChild(likes);
  row5.appendChild(row5Col1);

  var row5Col2 = document.createElement("div");
  row5Col2.style.display = "inline";
  row5Col2.style.padding = "10px";
  var num_shares;
  if(post.numShares_ >= 10000) {
    num_shares = Math.round(post.numShares_/1000) +'k';
  }
  else {
    num_shares = post.numShares_;
  }
  var shares = document.createTextNode("Shares: " + num_shares);
  row5Col2.appendChild(shares);
  row5.appendChild(row5Col2);

  var row5Col3 = document.createElement("div");
  row5Col3.style.display = "inline";
  row5Col3.style.padding = "10px";
  var num_comments;
  if(post.numComments_ >= 10000) {
    num_comments = Math.round(post.numComments_/1000) +'k';
  }
  else {
    num_comments = post.numComments_;
  }
  var comments = document.createTextNode("Comments: " + num_comments);
  row5Col3.appendChild(comments);
  row5.appendChild(row5Col3);
  postObj.appendChild(row5);

  return postObj;
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

  // make an invisible tag explaining which posts are displayed
  var postWrapper = document.createElement("div");
  postWrapper.style.visibility = "hidden";
  postWrapper.id = "PostType";
  var postType = document.createTextNode(tag);
  postWrapper.appendChild(postType);

  container.appendChild(postWrapper);

  for (var i = 0; i < postList.posts_.length; i++) {

      var post = postList.posts_[i];

      var postObj = createPosts(post);

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

      var postObj = createPosts(post);

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
    var returnval = requestPage("/top/" + pageNum, createElements, "Top Posts");
  }
  else {
    var returnval = requestPage("/top/" + pageNum, addElements, "Top Posts");
  }
}

function trendingFunction(pageNum) {
  if(pageNum == 0) {
    var returnval = requestPage("/trending/" + pageNum, createElements, "Trending Posts");
  }
  else {
    var returnval = requestPage("/trending/" + pageNum, addElements, "Trending Posts");
  }
}

function hashtagFunction(hashtag, pageNum) {
  if(pageNum == 0) {
    var returnval = requestPage("/hashtag/" + hashtag + "/" + pageNum, createElements, hashtag);
  }
  else {
    var returnval = requestPage("/hashtag/" + hashtag + "/" + pageNum, addElements, hashtag);
  }
}
