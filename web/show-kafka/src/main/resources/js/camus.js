(function() {
  window.onload = function() {
    var xhttp = new XMLHttpRequest();
    xhttp.open('GET', '/camus');
    xhttp.setRequestHeader('Content-Type', 'application/json');
    xhttp.send();
    document.getElementById("content").innerHTML = xhttp.responseText;
  }
})();