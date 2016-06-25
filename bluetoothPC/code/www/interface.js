
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


var start0 = document.getElementById("start0");
var start1 = document.getElementById("start1");
var startButton = document.getElementById("startButton");

startButton.onclick = function(){
  if(start0.checked && start1.checked){
    var url="/start";
    $.get(url);
    return;
  }

  if(start0.checked){
    var url="/start?address=0";
    $.get(url);
    return;
  }

  if(start1.checked){
    var url="/start?address=1";
    $.get(url);
    return;
  }


}
