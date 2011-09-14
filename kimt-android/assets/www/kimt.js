var isDrawn = false;

function draw(angle) {
    var canvas	= document.getElementById("drawPad");
    var context	= canvas.getContext("2d");
 	var drawBtn = document.getElementById("drawButton");
 	
	if (!isDrawn) {
	    var centerX = 140;
	    var centerY = 90;
	    var radius	= 35;
		
		context.rotate(angle);
	
	 	context.fillRect(50, 25, 75, 75);
	  	
	    context.beginPath();
	    context.arc(centerX, centerY, radius, 0, 2 * Math.PI, false);
	 
	    context.fillStyle = "#8ED6FF";
	    context.fill();
	    context.lineWidth = 5;
	    context.strokeStyle = "red";
	    context.stroke();
	  	
	  	drawBtn.innerHTML = "Reset";
  	} else {
  		clearCanvas(context, canvas);
  		
	  	drawBtn.innerHTML = "Draw";
  	}
  	
  	isDrawn = !isDrawn;
}

function clearCanvas(context, canvas) {

	// Clears the passed in canvas using the specified context.
	//	- http://stackoverflow.com/questions/2142535/how-to-clear-the-canvas-for-redrawing/4085780#4085780
	
	var w = canvas.width;
	  
	context.clearRect(0, 0, canvas.width, canvas.height);
	
	canvas.width = 1;
	canvas.width = w;
}

function rotateCanvas() {	
	if (isDrawn) {
		draw(0.125);
		draw(0.125);
	}
}