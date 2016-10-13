cordova-plugin-lightsensor
=========================

This plugin can be use to get intensity level that falls in to the device.
The API supports to one time call and repeated calls which will directed to callback functions.

A successful response receive as a JSON, {"intensity":5}
The value that get from `reading` object is in Lux (measurement unit)


Installation
--------------

<code> cordova plugin add https://github.com/DeshanKTD/cordova-plugin-lightsensor </code>

Methods
-------
- window.plugin.lightsensor.getReading
- window.plugin.lightsensor.watchReadings
- window.plugin.lightsensor.stop


window.plugin.lightsensor.getReading
-----------------------------------

This method get a single reading from the lightsensor sensor

<pre>
<code>
	window.plugin.lightsensor.getReading(
	    function success(reading){
	      console.log(JSON.stringify(reading)); 
	      alert(JSON.stringify(reading));
	      // Output: {"intensity": 25}
	    }, 
	    function error(message){
	     console.log(message);
	    }
  	)
  </code>
</pre>


 `reading` object properties
 - `intensity`




window.plugin.lightsensor.watchReadings
----------------------------------

This helps get reapeated readings from the lightsensor sensor.

<pre>
	<code>
		window.plugin.lightsensor.watchReadings(
		    function success(reading){
		      console.log(JSON.stringify(reading));
		      alert(JSON.stringify(reading)); 
		      // Output: {"intensity": 25}
		    }, 
		    function error(message){
		     console.log(message);
		    }
		  )
	</code>
</pre>


 `reading` object properties
 - `intensity`




window.plugin.lightsensor.stop
----------------------------

Stops getting readings from the lightsensor sensor.
<pre>
	<code>
	  window.plugin.lightsensor.stop([watchID])
	</code>
</pre>

Supported Platforms
--------------------

- Android
