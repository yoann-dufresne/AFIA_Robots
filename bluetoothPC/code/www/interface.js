
var submit = document.getElementById("submit");
var widthTxt = document.getElementById("width");
var heightTxt = document.getElementById("height");
var line = document.getElementById("line");
var col = document.getElementById("col");

submit.onclick = function () {
  var command = "INIT;";
  var main = $("input[name=main]:checked").val();

  command += main + ";";

  command += widthTxt.value + ";";
  command += heightTxt.value + ";";

  command += line.value + ";";
  command += col.value + ";";
  command += $("input[name=dir]:checked").val();
  var url = '/init?command='+command + "&main="+main;
	$.get(url);
}