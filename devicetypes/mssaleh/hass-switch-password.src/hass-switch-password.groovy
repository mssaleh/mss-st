
preferences {
	section("HASS Parameters"){
		input "hass_ip", "text", title: "HA local IP", required: true
		input "hass_port", "text", title: "HA API port, usually 8123", required: true
		input "hass_api_password", "text", title: "HA api_password", required: true
        input "hass_entity_id", "text", title: "Entity ID e.g. switch.kitchen_light", required: true
	}
}

metadata {
	definition (name: "HASS Switch Password", namespace: "mssaleh", author: "Mohammed Saleh", vid: "generic-switch") {
			capability "Switch"
	}

	// simulator metadata
	simulator {
	}

	// UI tile definitions
	tiles {
		standardTile("button", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: 'Off', action: "switch.on", icon: "st.Home.home30", backgroundColor: "#ffffff", nextState: "on"
				state "on", label: 'On', action: "switch.off", icon: "st.Home.home30", backgroundColor: "#79b821", nextState: "off"
		}
		standardTile("offButton", "device.button", width: 1, height: 1, canChangeIcon: true) {
			state "default", label: 'Force Off', action: "switch.off", icon: "st.Home.home30", backgroundColor: "#ffffff"
		}
		standardTile("onButton", "device.switch", width: 1, height: 1, canChangeIcon: true) {
			state "default", label: 'Force On', action: "switch.on", icon: "st.Home.home30", backgroundColor: "#79b821"
		}
		main "button"
			details (["button","onButton","offButton"])
	}
}

def parse(String description) {
	log.debug(description)
}

def on() {
	def postRequest = new physicalgraph.device.HubAction(
         	method: "POST",
			path: "/api/services/switch/turn_on",
			headers: [
				HOST: "${hass_ip}:${hass_port}",
                'x-ha-access': "${hass_api_password}",
                ],
            body: ["entity_id":"${hass_entity_id}"]
			)
			sendHubCommand(postRequest)
			sendEvent(name: "switch", value: "on")
			log.debug "Executing ON"
			log.debug postRequest
}

def off() {
	def postRequest = new physicalgraph.device.HubAction(
         	method: "POST",
			path: "/api/services/switch/turn_off",
			headers: [
				HOST: "${hass_ip}:${hass_port}",
                'x-ha-access': "${hass_api_password}",
                ],
            body: ["entity_id":"${hass_entity_id}"]
			)
			sendHubCommand(postRequest)
			sendEvent(name: "switch", value: "off")
			log.debug "Executing OFF"
			log.debug postRequest
}

