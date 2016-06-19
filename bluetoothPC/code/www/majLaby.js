var canvas = document.getElementById("laby");
var ctx = canvas.getContext('2d');
var tileSize = 40;


var update = function (data) {
	data = JSON.parse (data);
	console.log (data.robot);

	canvas.width = data.width*tileSize;
	ctx.canvas.width = data.width*tileSize;
	canvas.height = data.height*tileSize;
	ctx.canvas.height = data.height*tileSize;

	for (var line=0 ; line<data.height ; line++) {
		for (var col=0 ; col<data.width ; col++) {
			var x = col*tileSize;
			var y = line*tileSize;

			// North
			ctx.beginPath();
			ctx.moveTo(x,y);
			ctx.lineTo(x+tileSize,y);
			ctx.closePath();

			if (data.laby[line][col][0] == "undiscovered")
				ctx.strokeStyle = '#0000ff';
			else if (data.laby[line][col][0] == "wall")
				ctx.strokeStyle = '#ff0000';
			else if (data.laby[line][col][0] == "empty")
				ctx.strokeStyle = '#ffffff';
			ctx.stroke();


			// East
			ctx.beginPath();
			ctx.moveTo(x,y);
			ctx.lineTo(x+tileSize,y);
			ctx.closePath();

			if (data.laby[line][col][0] == "undiscovered")
				ctx.strokeStyle = '#0000ff';
			else if (data.laby[line][col][0] == "wall")
				ctx.strokeStyle = '#ff0000';
			else if (data.laby[line][col][0] == "empty")
				ctx.strokeStyle = '#ffffff';
			ctx.stroke();


			// South
			ctx.beginPath();
			ctx.moveTo(x,y+);
			ctx.lineTo(x+tileSize,y);
			ctx.closePath();

			if (data.laby[line][col][0] == "undiscovered")
				ctx.strokeStyle = '#0000ff';
			else if (data.laby[line][col][0] == "wall")
				ctx.strokeStyle = '#ff0000';
			else if (data.laby[line][col][0] == "empty")
				ctx.strokeStyle = '#ffffff';
			ctx.stroke();


			// West
			ctx.beginPath();
			ctx.moveTo(x,y);
			ctx.lineTo(x+tileSize,y);
			ctx.closePath();

			if (data.laby[line][col][0] == "undiscovered")
				ctx.strokeStyle = '#0000ff';
			else if (data.laby[line][col][0] == "wall")
				ctx.strokeStyle = '#ff0000';
			else if (data.laby[line][col][0] == "empty")
				ctx.strokeStyle = '#ffffff';
			ctx.stroke();
		}
	}
};

window.setInterval (
	function () {$.get("/update", update)},
	1000
);