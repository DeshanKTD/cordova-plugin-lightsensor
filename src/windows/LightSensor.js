


var cordova = require('cordova');
var lightSensor = Windows.Devices.Sensors.LightSensor;

module.exports = {
	getReading: function(win, loseX, args){
		var lightSensorReading = lightSensor.getCurrentReading();
	}
}