var net = require('net');

var client = new net.Socket();
client.connect(9999, '127.0.0.1', function() {
  console.log('Connected');
});

client.on('data', function(data) {
  console.log('Received: ' + data);
});

client.on('close', function() {
  console.log('Connection closed');
});


var getInfos = function(){
  console.log("query log");
  client.write("log");

}

setInterval(getInfos, 1000)


var quit = function(){
  client.write("stop");
  process.exit();
}

process.on('SIGINT', quit);