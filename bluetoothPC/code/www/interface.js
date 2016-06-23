
var submit = document.getElementById("submit");
var widthTxt = document.getElementById("width");
var heightTxt = document.getElementById("height");
var line = document.getElementById("line");
var col = document.getElementById("col");

submit.onclick = function () {
	var command = "INIT;";

	command += $("input[name=main]:checked").val() + ";"

	command += widthTxt.value + ";";
	command += heightTxt.value + ";";

	command += line.value + ";";
	command += col.value + ";";
	command += $("input[name=dir]:checked").val()
	$.get('/init?command=' + command)
}