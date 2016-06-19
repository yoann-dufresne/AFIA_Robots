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


var robotState = {x:0, y:0, dir:"EAST"};
var directions = {"NORTH": 0, "EAST": 1, "SOUTH": 2, "WEST": 3};



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

app.get("/start", function(req, res){
  startRobot();
  res.send("started");
});

// TCP
var client = new net.Socket();
client.connect(9999, '127.0.0.1', function() {
  console.log('Connected');
});

client.on('data', function(datas) {
  console.log('Received: ' + datas);
  datas = JSON.parse(datas);

  if (datas == {})
    return;

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
  //client.write("stop");
  client.destroy()
  process.exit();
}

var processData = function(robot, messages){
  if (messages == undefined)
    return;

  for(var i=0; i < messages.length; i++){
    var message = messages[i];
    var arr = message.split(";");
    var command = arr[0];
    arr.shift();
    var arguments = arr;

    if (command === "UPDATE"){
      onUpdate(robot, arguments)
    }

    if (command == "DISCOVERED")
      onDiscovered(robot, arguments)
  }
}

process.on('SIGINT', quit);


// Model

var onUpdate = function(robotId, arguments){
  robotState.x = arguments[0];
  robotState.y = arguments[1];
  robotState.dir = arguments[2];

  north = arguments[3];
  east = arguments[4];
  south = arguments[5];
  west = arguments[6];

  table[robotState.x][robotState.y][0] = north;
  table[robotState.x][robotState.y][1] = east;
  table[robotState.x][robotState.y][2] = south;
  table[robotState.x][robotState.y][3] = west;

  console.log("discovered : ", robotState.x, robotState.y);
}

var onDiscovered = function(robotId, arguments){
  if (arguments == undefined)
    return

  while(true){
    var tmp =  arguments.splice(0, 4);
    if (tmp.length != 4)
      break;

    x = parseInt(tmp[0].toString());
    y = parseInt(tmp[1].toString());
    console.log("discovered : ", x, y);
    if(x == -1 || y == -1)
      continue

    if(x == height || y == width)
      continue

    direction = directions[tmp[2]];
    table[x][y][direction] = tmp[3].toLowerCase;
  }
}

var startRobot = function(){
  client.write("START");
}