// source: https://raw.githubusercontent.com/stjohnjohnson/smartthings-mqtt-bridge/master/devicetypes/stj/mqtt-bridge.src/mqtt-bridge.groovy

import groovy.json.JsonSlurper
import groovy.json.JsonOutput

metadata {
    definition (name: "ST-MQTT Bifrost Device", namespace: "mssaleh", author: "Mohammed Saleh") {
        capability "Notification"
    }

    preferences {
        input("ip", "string",
            title: "MQTT Bridge IP Address",
            description: "MQTT Bridge IP Address",
            required: true,
            displayDuringSetup: true
        )
        input("port", "string",
            title: "MQTT Bridge Port",
            description: "MQTT Bridge Port",
            required: true,
            displayDuringSetup: true
        )
        input("mac", "string",
            title: "MQTT Bridge MAC Address",
            description: "MQTT Bridge MAC Address",
            required: true,
            displayDuringSetup: true
        )
    }

    simulator {}

    tiles {
        valueTile("basic", "device.ip", width: 3, height: 2) {
            state("basic", label:'OK')
        }
        main "basic"
    }
}

// Store the MAC address as the device ID so that it can talk to SmartThings
def setNetworkAddress() {
    // Setting Network Device Id
    def hex = "$settings.mac".toUpperCase().replaceAll(':', '')
    if (device.deviceNetworkId != "$hex") {
        device.deviceNetworkId = "$hex"
        log.debug "Device Network Id set to ${device.deviceNetworkId}"
    }
}

// Parse events from the Bridge
def parse(String description) {
    setNetworkAddress()

    log.debug "Parsing '${description}'"
    def msg = parseLanMessage(description)

    return createEvent(name: "message", value: new JsonOutput().toJson(msg.data))
}

// Send message to the Bridge
def deviceNotification(message) {
    if (device.hub == null)
    {
        log.error "Hub is null, must set the hub in the device settings so we can get local hub IP and port"
        return
    }

    log.debug "Sending '${message}' to device"
    setNetworkAddress()

    def slurper = new JsonSlurper()
    def parsed = slurper.parseText(message)

    if (parsed.path == '/subscribe') {
        parsed.body.callback = device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
    }

    def headers = [:]
    headers.put("HOST", "$ip:$port")
    headers.put("Content-Type", "application/json")

    def hubAction = new physicalgraph.device.HubAction(
        method: "POST",
        path: parsed.path,
        headers: headers,
        body: parsed.body
    )
    hubAction
}
