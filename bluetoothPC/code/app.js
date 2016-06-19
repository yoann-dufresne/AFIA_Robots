var net = require('net');
var express = require('express');



// Structures

var table;
var width = 23;
var height = 11;

var createTable = function (width, height) {
	table = [];
	for (var line=0 ; line<height ; line++) {
		table[line] = [];

		for (var col=0 ; col<width ; col++) {
			table[line][col] = ["undiscovered", "undiscovered", "undiscovered", "undiscovered"];
		}
	}
};
createTable (width, height);


var robotState = {x:0, y:0, dir:"east"};




// Serveur web


var app = express();
app.listen(8080);

app.use(express.static(__dirname+"/www"));


app.get('/update', function(req, res){
	var data = {};
	data.width = width;
	data.height = height;
	data.robot = robotState;
	data.laby = table;

	res.send(JSON.stringify(data));
});





// TCP
var client = new net.Socket();
client.connect(9999, '127.0.0.1', function() {
  console.log('Connected');
});

client.on('data', function(datas) {
  console.log('Received: ' + datas);
  datas = JSON.parse(datas);
  for(var robot in datas)
    var message = datas[robot];
    processData(robot, message);
});

client.on('close', function() {
  console.log('Connection closed');
});


var getInfos = function(){
  client.write("log");
}
setInterval(getInfos, 1000)

var quit = function(){
  client.write("stop");
  process.exit();
}

var processData = function(robot, messages){
  for(var i=0; i < messages.length; i++){
    var message = messages[i];
    var arr = message.split(";");
    var command = arr[0];
    arr.shift();
    var arguments = arr;
    console.log(command, arguments);
  }
}

process.on('SIGINT', quit);