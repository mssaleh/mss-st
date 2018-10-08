import groovy.json.JsonSlurper
import groovy.util.XmlSlurper

metadata {
	definition (name: "Sonoff Basic Switch", namespace: "mssaleh", author: "Mohammed Saleh", ocfDeviceType: "oic.d.switch", vid: "generic-switch") {
		capability "Actuator"
		capability "Switch"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"

	}

	simulator {
	}

	preferences {
		section("Device Parameters"){
			input "switch_ip", "text", title: "Sonoff Basic IP address, which has to be static or reserved from the router", required: true
		}
	}

	tiles (scale: 2){
		multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", backgroundColor:"#00a0dc", icon: "st.switches.switch.on", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", backgroundColor:"#ffffff", icon: "st.switches.switch.off", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", backgroundColor:"#00a0dc", icon: "st.switches.switch.off", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", backgroundColor:"#ffffff", icon: "st.switches.switch.on", nextState:"turningOn"
			}
    }

		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		main(["switch"])
		details(["switch","refresh"])
  }
}

private def logging(message, level) {
    if (logLevel != "0"){
    switch (logLevel) {
       case "1":
          if (level > 1)
             log.debug "$message"
       break
       case "99":
          log.debug "$message"
       break
    }
    }
}

def parse(description) {
	//log.debug "Parsing: ${description}"
    def events = []
    def descMap = parseDescriptionAsMap(description)
    def body
    //log.debug "descMap: ${descMap}"
    if (descMap["body"]) body = new String(descMap["body"].decodeBase64())

    if (body && body != "") {

    if(body.startsWith("{") || body.startsWith("[")) {
    def slurper = new JsonSlurper()
    def result = slurper.parseText(body)

    log.debug "result: ${result}"

    if (result.containsKey("power")) {
        events << createEvent(name: "switch", value: result.power)
    }
    } else {
        //log.debug "Response is not JSON: $body"
    }
    }
    return events
}

def parseDescriptionAsMap(description) {
	description.split(",").inject([:]) { map, param ->
		def nameAndValue = param.split(":")

        if (nameAndValue.length == 2) map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
        else map += [(nameAndValue[0].trim()):""]
	}
}


def on() {
	log.debug "Executing on"
	def postRequest = new physicalgraph.device.HubAction(
      method: "GET",
			path: "/on",
			headers: [
			HOST: "${switch_ip}:80",
                'Content-Type': 'application/json',
                ]
			)
			sendHubCommand(postRequest)
			sendEvent(name: "switch", value: "on")
			log.debug "Executing ON"
			log.debug postRequest
}

def off() {
	log.debug "Executing off"
	def postRequest = new physicalgraph.device.HubAction(
      method: "GET",
			path: "/off",
			headers: [
			HOST: "${switch_ip}:80",
                'Content-Type': 'application/json',
                ]
			)
			sendHubCommand(postRequest)
			sendEvent(name: "switch", value: "off")
			log.debug "Executing OFF"
			log.debug postRequest
}

def refresh() {
	log.debug "Executing 'refresh'"
	def postRequest = new physicalgraph.device.HubAction(
      method: "GET",
			path: "/status",
			headers: [
			HOST: "${switch_ip}:80",
                'Content-Type': 'application/json',
                ]
			)
	sendHubCommand(postRequest)
	// sendEvent(name: "switch", value: "off")
	log.debug "Executing poll"
	log.debug postRequest
}

def poll() {
	log.debug "Executing 'poll'"
	def postRequest = new physicalgraph.device.HubAction(
      method: "GET",
			path: "/status",
			headers: [
			HOST: "${switch_ip}:80",
                'Content-Type': 'application/json',
                ]
			)
			sendHubCommand(postRequest)
			// sendEvent(name: "switch", value: "off")
			log.debug "Executing poll"
			log.debug postRequest
}
