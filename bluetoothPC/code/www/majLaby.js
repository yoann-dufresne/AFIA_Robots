var canvas = document.getElementById("laby");
var ctx = canvas.getContext('2d');
var tileSize = 40;

var globalData;

var update = function (data) {
	data = JSON.parse(data);
	globalData = data;
	data.robot.x = parseInt(data.robot.x);
	data.robot.y = parseInt(data.robot.y);

	var tmp_robot_x = data.robot.x;
	data.robot.x = data.robot.y;
	data.robot.y = tmp_robot_x;

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

			if (data.laby[line][col][0] == "Undiscovered")
				ctx.strokeStyle = '#D3D3D3';
			else if (data.laby[line][col][0] == "Wall")
				ctx.strokeStyle = '#ff0000';
			else if (data.laby[line][col][0] == "Empty")
				ctx.strokeStyle = '#000000';
			ctx.stroke();


			// East
			ctx.beginPath();
			ctx.moveTo(x+tileSize-1,y);
			ctx.lineTo(x+tileSize-1,y+tileSize);
			ctx.closePath();

			if (data.laby[line][col][1] == "Undiscovered")
				ctx.strokeStyle = '#D3D3D3';
			else if (data.laby[line][col][1] == "Wall")
				ctx.strokeStyle = '#ff0000';
			else if (data.laby[line][col][1] == "Empty")
				ctx.strokeStyle = '#000000';
			ctx.stroke();


			// South
			ctx.beginPath();
			ctx.moveTo(x,y+tileSize-1);
			ctx.lineTo(x+tileSize,y+tileSize-1);
			ctx.closePath();

			if (data.laby[line][col][2] == "Undiscovered")
				ctx.strokeStyle = '#D3D3D3';
			else if (data.laby[line][col][2] == "Wall")
				ctx.strokeStyle = '#ff0000';
			else if (data.laby[line][col][2] == "Empty")
				ctx.strokeStyle = '#000000';
			ctx.stroke();


			// West
			ctx.beginPath();
			ctx.moveTo(x,y);
			ctx.lineTo(x,y+tileSize);
			ctx.closePath();

			if (data.laby[line][col][3] == "Undiscovered")
				ctx.strokeStyle = '#D3D3D3';
			else if (data.laby[line][col][3] == "Wall")
				ctx.strokeStyle = '#ff0000';
			else if (data.laby[line][col][3] == "Empty")
				ctx.strokeStyle = '#000000';
			ctx.stroke();
		}
	}

	ctx.beginPath();
    ctx.arc((data.robot.x+0.5) * tileSize, (data.robot.y+0.5) * tileSize, 0.4*tileSize, 0, 2 * Math.PI, false);
    ctx.closePath();
    ctx.fillStyle = "#82C46C";
    ctx.fill();

	ctx.beginPath();
	if (data.robot.dir == "NORTH")
		ctx.rect((data.robot.x+0.4)*tileSize, (data.robot.y+0.1)*tileSize,0.2*tileSize,0.2*tileSize);
	else if (data.robot.dir == "EAST")
		ctx.rect((data.robot.x+0.7)*tileSize, (data.robot.y+0.4)*tileSize,0.2*tileSize,0.2*tileSize);
	else if (data.robot.dir == "SOUTH")
		ctx.rect((data.robot.x+0.4)*tileSize, (data.robot.y+0.7)*tileSize,0.2*tileSize,0.2*tileSize);
	else if (data.robot.dir == "WEST")
		ctx.rect((data.robot.x+0.1)*tileSize, (data.robot.y+0.4)*tileSize,0.2*tileSize,0.2*tileSize);
	else
		console.error("unknown direction : ", data.robot.dir)
	ctx.closePath();
	ctx.fillStyle ="#000000";
	ctx.fill();
};

/*window.setInterval (
	function () {$.get("/update", update)},
	300
);/**/
