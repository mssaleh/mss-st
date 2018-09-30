metadata {
    definition (name: "Aqara Wired Double Switch", namespace: "mssaleh", author: "Mohammed Saleh", vid: "generic-switch") {
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Switch"
        capability "Temperature Measurement"

        command "on2"
        command "off2"

        attribute "switch2","ENUM", ["on","off"]

        attribute "lastCheckin", "string"
    }

    // simulator metadata
    simulator {
        // status messages
        status "on": "on/off: 1"
        status "off": "on/off: 0"

        // reply messages
        reply "zcl on-off on": "on/off: 1"
        reply "zcl on-off off": "on/off: 0"
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
            }



           	tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
		   	}
        }

        standardTile("switch2", "device.switch2", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: '${name}', action: "on2", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: '${name}', action: "off2", icon: "st.switches.switch.on", backgroundColor: "#79b821"
		}

        valueTile("temperature", "device.temperature", width: 2, height: 2) {
			state("temperature", label:'${currentValue}°',
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
			)
		}
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        main (["switch", "switch2", "temperature"])
        details(["switch", "switch2", "temperature", "refresh"])
    }
}

// Parse incoming device messages to generate events
def parse(String description) {
   log.debug "Parsing '${description}'"
   def value = zigbee.parse(description)?.text
   log.debug "Parse: $value"
   Map map = [:]

   if (description?.startsWith('catchall:')) {
		map = parseCatchAllMessage(description)
	}
	else if (description?.startsWith('read attr -')) {
		map = parseReportAttributeMessage(description)
	}
    else if (description?.startsWith('on/off: ')){
    	def resultMap = zigbee.getKnownDescription(description)
   		log.debug "${resultMap}"

        map = parseCustomMessage(description)
    }

	log.debug "Parse returned $map"
    //  send event for heartbeat
    def now = new Date()
    sendEvent(name: "lastCheckin", value: now)

	def results = map ? createEvent(map) : null
	return results;
}

private Map parseCatchAllMessage(String description) {
	Map resultMap = [:]
	def cluster = zigbee.parse(description)
	log.debug cluster

    if (cluster.clusterId == 0x0006 && cluster.command == 0x01){
    	def onoff = cluster.data[-1]
        if (onoff == 1)
        	resultMap = createEvent(name: "switch", value: "on")
        else if (onoff == 0)
            resultMap = createEvent(name: "switch", value: "off")
    }

	return resultMap
}

private Map parseReportAttributeMessage(String description) {
	Map descMap = (description - "read attr - ").split(",").inject([:]) { map, param ->
		def nameAndValue = param.split(":")
		map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
	}
	//log.debug "Desc Map: $descMap"

	Map resultMap = [:]

	if (descMap.cluster == "0001" && descMap.attrId == "0020") {
		resultMap = getBatteryResult(convertHexToInt(descMap.value / 2))
 	}
    if (descMap.cluster == "0002" && descMap.attrId == "0000") {
		resultMap = createEvent(name: "temperature", value: zigbee.parseHATemperatureValue("temperature: " + (convertHexToInt(descMap.value) / 2), "temperature: ", getTemperatureScale()), unit: getTemperatureScale())
		log.debug "Temperature Hex convert to ${resultMap.value}%"
    }
    else if (descMap.cluster == "0008" && descMap.attrId == "0000") {
    	resultMap = createEvent(name: "switch", value: "off")
    }
	return resultMap
}

def off() {
    log.debug "off()"
	sendEvent(name: "switch", value: "off")
	"st cmd 0x${device.deviceNetworkId} 2 6 0 {}"
}

def on() {
   log.debug "on()"
	sendEvent(name: "switch", value: "on")
	"st cmd 0x${device.deviceNetworkId} 2 6 1 {}"
}

def off2() {
    log.debug "off2()"
	sendEvent(name: "switch2", value: "off")
	"st cmd 0x${device.deviceNetworkId} 3 6 0 {}"
}

def on2() {
   log.debug "on2()"
	sendEvent(name: "switch2", value: "on")
	"st cmd 0x${device.deviceNetworkId} 3 6 1 {}"
}

def refresh() {
	log.debug "refreshing"
    [
        "st rattr 0x${device.deviceNetworkId} 1 6 0", "delay 500",
        "st rattr 0x${device.deviceNetworkId} 1 6 0", "delay 250",
        "st rattr 0x${device.deviceNetworkId} 1 2 0", "delay 250",
        "st rattr 0x${device.deviceNetworkId} 1 1 0", "delay 250",
        "st rattr 0x${device.deviceNetworkId} 1 0 0"
    ]
}

private Map parseCustomMessage(String description) {
	def result
	if (description?.startsWith('on/off: ')) {
    	if (description == 'on/off: 0')
    		result = createEvent(name: "switch", value: "off")
    	else if (description == 'on/off: 1')
    		result = createEvent(name: "switch", value: "on")
	}

    return result
}

private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}
