var argscheck = require('cordova/argscheck');
var utils = require('cordova/utils');
var exec = require('cordova/exec');
var timers = {};


var LightSensor = function(){

}


LightSensor.prototype = {
	getReading: function(onSuccessCallback,onErrorCallback){
		cordova.exec(onSuccessCallback,onErrorCallback,"LightSensor","getReading",[]);

	},

	watchReadings: function(onSuccessCallback, onErrorCallback){
		//start timer to get distance
		var LightSensor = this;
		var id = utils.createUUID();

		if (cordova.platformId === 'android') {
			timers[id] = window.setInterval(function() {
          		LightSensor.getReading(onSuccessCallback, onErrorCallback);
      		}, 40); // every 40 ms (25 fps)
		}
		else {
			cordova.exec(onSuccessCallback,onErrorCallback, "LightSensor","watchReadings",[]);
			return id;
		}
	},

	stop: function(watchID){
		if (cordova.platformId === 'android'){
			if(watchID){
				// stop a single watch
				window.clearInterval(timers[watchID]);
				delete timers.watchID;
			}
			else{
				// stop all timers
				for (var id in timers) {
					window.clearInterval(timers[id]);
					delete timers.id;
				}
			}
		}
		else cordova.exec(function() {}, function() {throw "Error stopping LightSensor"}, "LightSensor","stop",[]);

	}
}

module.exports  = new LightSensor();
