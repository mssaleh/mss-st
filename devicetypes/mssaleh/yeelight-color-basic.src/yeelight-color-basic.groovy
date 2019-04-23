
metadata {
	definition (name: "Yeelight Color Basic", namespace: "mssaleh", author: "Simon Tether", mnmn: "SmartThings", vid: "generic-rgbw-color-bulb") {
		capability "Switch Level"
		capability "Color Control"
		capability "Color Temperature"
		capability "Switch"
        	capability "Relay Switch"
		capability "Refresh"
		capability "Actuator"
		capability "Sensor"

		command "reset"
        	command "refresh"
        
        	attribute "colorName", "string"  //Needed for Colour Shortcuts
        
        	command "coolWhite"
        	command "warmWhite"
		command "daylight"
		command "red"
        	command "green"
        	command "blue"
        	command "cyan"
        	command "magenta"
        	command "orange"
        	command "purple"
        	command "yellow"
        	command "pink"
        	command "police"
        	command "alarm"
        	command "colourcycle"
        	command "halt"
        
	}

	simulator {
	}
	
    //Tiles on Device Panel
	standardTile("switch", "device.switch", width: 1, height: 1, canChangeIcon: true) {
		state "on", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#00a0dc", nextState:"turningOff"
		state "off", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
		state "turningOn", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#00a0dc", nextState:"turningOff"
		state "turningOff", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
	}
	standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
		state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
	}
	standardTile("reset", "device.reset", inactiveLabel: false, decoration: "flat") {
		state "default", label:"Reset Color", action:"reset", icon:"st.lights.philips.hue-single"
	}
	controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 2, inactiveLabel: false, range:"(0..100)") {
		state "level", action:"switch level.setLevel"
	}
	controlTile("rgbSelector", "device.color", "color", height: 3, width: 3, inactiveLabel: false) {
		state "color", action:"setColor"
	}
	valueTile("level", "device.level", inactiveLabel: false, decoration: "flat") {
		state "level", label: 'Level ${currentValue}%'
	}
	controlTile("colorTempControl", "device.colorTemperature", "slider", height: 1, width: 1, inactiveLabel: false, range:"(2700..6500)") {
		state "colorTemperature", action:"setColorTemperature"
	}
	valueTile("hue", "device.hue", inactiveLabel: false, decoration: "flat") {
		state "hue", label: 'Hue ${currentValue}   '
	}
////////////////////
        standardTile("coolWhite", "device.coolWhite", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offcoolWhite", label:"cool white", action:"softwhite", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "oncoolWhite", label:"cool white", action:"softwhite", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFF1E0"
        }
        standardTile("daylight", "device.daylight", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offdaylight", label:"daylight", action:"daylight", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "ondaylight", label:"daylight", action:"daylight", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFFFFB"
        }
        standardTile("warmwhite", "device.warmwhite", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offwarmwhite", label:"warm white", action:"warmwhite", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onwarmwhite", label:"warm white", action:"warmwhite", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFF4E5"
        }
        standardTile("red", "device.red", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offred", label:"red", action:"red", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onred", label:"red", action:"red", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF0000"
        }
        standardTile("green", "device.green", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offgreen", label:"green", action:"green", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "ongreen", label:"green", action:"green", icon:"st.illuminance.illuminance.bright", backgroundColor:"#00FF00"
        }
        standardTile("blue", "device.blue", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offblue", label:"blue", action:"blue", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onblue", label:"blue", action:"blue", icon:"st.illuminance.illuminance.bright", backgroundColor:"#0000FF"
        }
        standardTile("cyan", "device.cyan", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offcyan", label:"cyan", action:"cyan", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "oncyan", label:"cyan", action:"cyan", icon:"st.illuminance.illuminance.bright", backgroundColor:"#00FFFF"
        }
        standardTile("magenta", "device.magenta", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offmagenta", label:"magenta", action:"magenta", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onmagenta", label:"magenta", action:"magenta", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF00FF"
        }
        standardTile("orange", "device.orange", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offorange", label:"orange", action:"orange", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onorange", label:"orange", action:"orange", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF6600"
        }
        standardTile("purple", "device.purple", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offpurple", label:"purple", action:"purple", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onpurple", label:"purple", action:"purple", icon:"st.illuminance.illuminance.bright", backgroundColor:"#BF00FF"
        }
        standardTile("yellow", "device.yellow", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offyellow", label:"yellow", action:"yellow", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onyellow", label:"yellow", action:"yellow", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFFF00"
        }
////////////////////
	main(["switch"])
	details(["switch", "levelSliderControl", "rgbSelector", "reset", "colorTempControl", "refresh", 
    	"coolWhite", "warmWhite", "daylight", "red", "green", "blue", "orange", "yellow", "cyan", "magenta", "pink", "purple", 
        "police", "alarm", "colourcycle", "halt"])
}

	//Settings Page
    preferences {
    	input name: "DeviceLocalLan", type: "string", title:"Yeelight IP Address", description:"Enter Bulb's IP address", defaultValue:"", required: true, displayDuringSetup: true
        input name: "defaultONLevel", type: "number", title: "Default ON Level:", description:"Enter Default ON Level", defaultValue:75, range: "1..100", required: false, displayDuringSetup: true
        input name: "coolWhiteValue", type: "number", title: "Cool White Value (K):", description:"Enter Cool White Value", defaultValue:6500, range: "1700..6500", required: false, displayDuringSetup: true
        input name: "warmWhiteValue", type: "number", title: "Warm White Value (K):", description:"Enter Warm White Value", defaultValue:3000, range: "1700..6500", required: false, displayDuringSetup: true       
        input name: "dayWhiteValue", type: "number", title: "Daylight White Value (K):", description:"Enter Daylight White Value", defaultValue:4500, range: "1700..6500", required: false, displayDuringSetup: true
	}

def installed()  {
	log.debug "installed"
    	initialize()
}

def updated()  {
	log.debug "updated"
    	initialize()
}

//Parse incoming from Yeelight -----NOT WORKING-----
def parse(String description) {
	log.debug "Response '${description}'"
}

//Reset Yeelight to White, 4000K
def reset() {
	log.debug "reset"
    	//getProp()
    	delayBetween([
		on(),
        	setColor("red":255, "hex":"#FFFFFF", "blue":255, "saturation":100.0, "hue":0.0, "green":255, "alpha":1.0),
        	setColorTemperature(4000)
    	], 300)
}

//Send Command to Yeelight
def transmit(yeelightCommand) {
    	def String ipaddr = DeviceLocalLan
    	def String hexIp = ipaddr.tokenize('.').collect {
        String.format('%02X', it.toInteger())
    	}.join()
    	def String myNetworkID = "${hexIp}:D893"
	device.deviceNetworkId = myNetworkID
    	log.debug "network ID: " + myNetworkID
	def transmittedData = new physicalgraph.device.HubAction(yeelightCommand, physicalgraph.device.Protocol.LAN, myNetworkID)
	log.debug "Sent " + transmittedData
    	sendHubCommand(transmittedData)
}

//Get Properties of Yeelight
def getProp() {
	log.debug "getProp"
	transmit("""{"id":1,"method":"get_prop","params":["power", "bright", "ct", "rgb", "name"]}\r\n""")
}

//Turn Yeelight ON
def on() {
	//getProp()
    	//delayBetween([
        	transmit("""{"id": 1, "method": "set_power", "params":["on", "smooth", 500]}\r\n""") //,
		//transmit("""{"id": 1, "method": "set_bright", "params":[${defaultONLevel}, "smooth", 100]}\r\n""")
    	//], 300)
    	sendEvent(name: "switch", value: "on")
    	//sendEvent(name: "level", value: defaultONLevel)
}

//Turn Yeelight OFF
def off() {
	//getProp()
	transmit("""{"id": 1, "method": "set_power", "params":["off", "smooth", 500]}\r\n""")
    	sendEvent(name: "switch", value: "off")
}

//Check if Yeelight is off and turn on if it is - called from setLevel, setColor and setColorTemp
def powerCheck() {
	def powerState = device.currentValue("switch")
    	if (powerState == "off") {
    		transmit("""{"id": 1, "method": "set_power", "params":["on", "sudden"]}\r\n""")
        	sendEvent(name: "switch", value: "on")
    }
}

//Set Yeelight Dim Level
def setLevel(level) {
	//getProp()
    	powerCheck()
	if(level < 2) {
		off()
        	sendEvent(name: "level", value: 0)
    }
    	else {
   		transmit("""{"id": 1, "method": "set_bright", "params":[$level, "smooth", 100]}\r\n""")
        	sendEvent(name: "level", value: level)
    }
}

//Set Yeelight Colour
def setColor(value) {
	//getProp()
    	powerCheck()
	def result = []
    	//transmit("""{"id": 1, "method": "set_power", "params":["on", "smooth", 500]}\r\n""")
    	def red = value.red
    	def green = value.green
    	def blue = value.blue
	log.debug "setColor: ${value}"
    	def rgb = (red*65536)+(green*256)+blue
    	transmit("""{"id": 1, "method": "set_rgb", "params":[${rgb}, "smooth", 500]}\r\n""")
	if(value.hue) sendEvent(name: "hue", value: value.hue)
	if(value.hex) sendEvent(name: "color", value: value.hex)
	if(value.switch) sendEvent(name: "switch", value: value.switch)
	if(value.saturation) sendEvent(name: "saturation", value: value.saturation)
}

//Set Yeelight Colour Temperature
def setColorTemperature(kelvin) {
	//getProp()
    	powerCheck()
	if(kelvin > 6500) kelvin = 6500
    	log.debug "setColorTemperature: ${kelvin}K"
	transmit("""{"id": 1, "method": "set_ct_abx", "params":[${kelvin}, "smooth", 500]}\r\n""")
    	sendEvent(name: "colorTemperature", value: kelvin)
}

// Colour Shortcuts
def coolWhite() {
    	setColor("red":255, "hex":"#FFFFFF", "blue":255, "saturation":100.0, "hue":0.0, "green":255, "alpha":1.0)
    	setColorTemperature(coolWhiteValue)
}
def warmWhite() {
	setColor("red":255, "hex":"#FFFFFF", "blue":255, "saturation":100.0, "hue":0.0, "green":255, "alpha":1.0)
    	setColorTemperature(warmWhiteValue)
}
def daylight() {
	setColor("red":255, "hex":"#FFFFFF", "blue":255, "saturation":100.0, "hue":0.0, "green":255, "alpha":1.0)
    	setColorTemperature(dayWhiteValue)
}
def red() {
	setColor("red":255, "hex":"#FF0000", "blue":0, "saturation":100.0, "hue":0.0, "green":0, "alpha":1.0)
}
def green() {
	setColor("red":0, "hex":"#00FF00", "blue":0, "saturation":100.0, "hue":0.0, "green":255, "alpha":1.0)
}
def blue() {
	setColor("red":0, "hex":"#0000FF", "blue":255, "saturation":100.0, "hue":0.0, "green":0, "alpha":1.0)
}
def cyan() {
	setColor("red":0, "hex":"#00FFFF", "blue":255, "saturation":100.0, "hue":0.0, "green":255, "alpha":1.0)
}
def magenta() {
	setColor("red":255, "hex":"#FF00FF", "blue":255, "saturation":100.0, "hue":0.0, "green":0, "alpha":1.0)
}
def orange() {
	setColor("red":255, "hex":"#FFA500", "blue":0, "saturation":100.0, "hue":0.0, "green":69, "alpha":1.0)
}
def purple() {
	setColor("red":128, "hex":"#800080", "blue":128, "saturation":100.0, "hue":0.0, "green":0, "alpha":1.0)
}
def yellow() {
	setColor("red":255, "hex":"#FFFF00", "blue":0, "saturation":100.0, "hue":0.0, "green":255, "alpha":1.0)
}
def pink() {
	setColor("red":255, "hex":"#FFC0CB", "blue":203, "saturation":100.0, "hue":0.0, "green":192, "alpha":1.0)
}

//Colour Flow Presets
def police() {
	transmit("""{"id":1,"method":"start_cf","params":[0,0,"300,1,16711680,1,50,1,222,1,50,1,3355647,100,50,1,222,1,50,1,16711680,100,50,1,222,1,50,1,3355647,100,50,1,222,1"]}\r\n""")
}
def alarm() {
	transmit("""{"id":1,"method":"start_cf","params":[0, 0, "500, 1, 255, 100, 500, 1, 16711680,100"]}\r\n""")
}
def colourcycle() {
	transmit("""{"id":1,"method":"start_cf","params":[0, 0, "5000, 1, 255, 100, 5000, 1, 65280,100, 5000, 1, 16711680,100"]}\r\n""")
}
def halt() {
	transmit("""{"id":1,"method":"stop_cf","params":[]}\r\n""")
}
