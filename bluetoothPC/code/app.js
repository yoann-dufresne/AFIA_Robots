var net = require('net');

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