metadata {
    definition (name: "NeoCoolcam Double Switch", namespace: "mssaleh", author: "Mohammed Saleh", vid: "generic-switch") {
        capability "Switch"
        capability "Relay Switch"
        capability "Polling"
        capability "Configuration"
        capability "Refresh"
        capability "Zw Multichannel"

        attribute "switch1", "string"
        attribute "switch2", "string"

        command "childOn"
        command "childOff"
        command "on1"
        command "off1"
        command "on2"
        command "off2"

        fingerprint deviceId: "0x1001", inClusters:"0x5E, 0x86, 0x72, 0x5A, 0x73, 0x85, 0x59, 0x8E, 0x60, 0x27, 0x25, 0x70"
	}

	simulator {
        status "on": "command: 2003, payload: FF"
        status "off": "command: 2003, payload: 00"

        // reply messages
        reply "2001FF,delay 100,2502": "command: 2503, payload: FF"
        reply "200100,delay 100,2502": "command: 2503, payload: 00"
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true) {
                tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                    attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
                    attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
                }
        }
        standardTile("switch1", "device.switch1",canChangeIcon: true, width: 3, height: 3) {
            state "on", label: "switch1", action: "off1", icon: "st.switches.switch.on", backgroundColor: "#79b821"
            state "off", label: "switch1", action: "on1", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
        }
        standardTile("switch2", "device.switch2",canChangeIcon: true, width: 3, height: 3) {
            state "on", label: "switch2", action: "off2", icon: "st.switches.switch.on", backgroundColor: "#79b821"
            state "off", label: "switch2", action: "on2", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
        }
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        standardTile("configure", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"configure", icon:"st.secondary.configure"
        }

        main(["switch","switch1", "switch2"])
        details(["switch","switch1","switch2","refresh","configure"])
    }


   preferences {
        def paragraph = "Configuration"
        input name: "param1", type: "number", range: "0..1", defaultValue: "1", required: false,
            title: paragraph + "\n\n" +
                   "Backlight Enable \n\n"
        input name: "param2", type: "number", range: "0..1", defaultValue: "1", required: false,
            title: paragraph + "\n\n" +
                   "Relay On Off indicate \n\n"
        input name: "param3", type: "number", range: "0..1", defaultValue: "1", required: false,
            title: paragraph + "\n\n" +
                   "Relay On Off Status Saved \n\n"
        input name: "param4", type: "number", range: "0..1", defaultValue: "1", required: false,
            title: paragraph + "\n\n" +
                   "Root Device Mapping \n\n"
    }
}

def parse(String description) {
    def result = []
    def cmd = zwave.parse(description)
    if (cmd) {
        result += zwaveEvent(cmd)
        log.debug "Parsed ${cmd} to ${result.inspect()}"
    } else {
        log.debug "Non-parsed event: ${description}"
    }
    return result
}


def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
	sendEvent(name: "switch", value: cmd.value ? "on" : "off", type: "digital")
    def result = []
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
    response(delayBetween(result, 1000)) // returns the result of reponse()
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd)
{
    sendEvent(name: "switch", value: cmd.value ? "on" : "off", type: "digital")
    def result = []
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
    response(delayBetween(result, 1000)) // returns the result of reponse()
}


def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCapabilityReport cmd)
{
    log.debug "multichannelv3.MultiChannelCapabilityReport $cmd"
    if (cmd.endPoint == 2 ) {
        def currstate = device.currentState("switch2").getValue()
        if (currstate == "on")
        	sendEvent(name: "switch2", value: "off", isStateChange: true, display: false)
        else if (currstate == "off")
        	sendEvent(name: "switch2", value: "on", isStateChange: true, display: false)
    }
    else if (cmd.endPoint == 1 ) {
        def currstate = device.currentState("switch1").getValue()
        if (currstate == "on")
        sendEvent(name: "switch1", value: "off", isStateChange: true, display: false)
        else if (currstate == "off")
        sendEvent(name: "switch1", value: "on", isStateChange: true, display: false)
    }
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
   def map = [ name: "switch$cmd.sourceEndPoint" ]
   def curswitch, curstate

   switch(cmd.commandClass) {
      case 32:
         if (cmd.parameter == [0]) {
            map.value = "off"
         }
         if (cmd.parameter == [255]) {
            map.value = "on"
         }
         createEvent(map)
         break
      case 37:
         if (cmd.parameter == [0]) {
            map.value = "off"
         }
         if (cmd.parameter == [255]) {
            map.value = "on"
         }
         curstate = map.value
         curswitch = cmd.sourceEndPoint
         break
    }

    //Now if there is a child device then send it a state update
    try {
        def childDevice = getChildDevices()?.find { it.deviceNetworkId == "$device.deviceNetworkId-sw${curswitch}"}
           if (childDevice)
              childDevice.sendEvent(name: "switch", value: curstate)
    } catch (e) {
        log.error "Couldn't find child device, probably doesn't exist and hence no problem: ${e}"
    }


//    childDevices?.each { childDevice ->
//    	//log.debug childDevice.deviceNetworkId //log the devices as you cycle through them
//        if (childDevice) {
//            if (childDevice.deviceNetworkId == "$device.deviceNetworkId-sw${curswitch}") {
//                childDevice.sendEvent(name: "switch", value: curstate)
//            }
//        }
//    }

    def events = [createEvent(map)]
    if (map.value == "on") {
            events += [createEvent([name: "switch", value: "on"])]
    } else {
         def allOff = true
         (1..2).each { n ->
             if (n != cmd.sourceEndPoint) {
                 if (device.currentState("switch${n}").value != "off") allOff = false
             }
         }
         if (allOff) {
             events += [createEvent([name: "switch", value: "off"])]
         }
    }
    events
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
    // This will capture any commands not handled by other instances of zwaveEvent
    // and is recommended for development so you can see every command the device sends
    return createEvent(descriptionText: "${device.displayName}: ${cmd}")
}

def zwaveEvent(physicalgraph.zwave.commands.switchallv1.SwitchAllReport cmd) {
   log.debug "SwitchAllReport $cmd"
}

def refresh() {
	def cmds = []
    cmds << zwave.manufacturerSpecificV2.manufacturerSpecificGet().format()
	cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
	delayBetween(cmds, 1000)
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
	def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
	log.debug "msr: $msr"
    updateDataValue("MSR", msr)
}

def poll() {
	def cmds = []
	cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
	delayBetween(cmds, 1000)
}

def configure() {
	log.debug "Executing 'configure'"
    delayBetween([
          zwave.configurationV1.configurationSet(parameterNumber:1, configurationValue:[param1.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:2, configurationValue:[param2.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:3, configurationValue:[param3.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:4, configurationValue:[param4.value]).format(),
          zwave.associationV2.associationSet(groupingIdentifier:1, nodeId:[zwaveHubNodeId]).format(),
          zwave.associationV2.associationSet(groupingIdentifier:2, nodeId:[zwaveHubNodeId]).format(),
          zwave.associationV2.associationSet(groupingIdentifier:3, nodeId:[zwaveHubNodeId]).format(),

    ])
}

/**
* Triggered when Done button is pushed on Preference Pane
*/
def updated()
{
	log.debug "Preferences have been changed. Attempting configure() and update"
    def cmds = configure()

    //Used to use 'if (!childDevices)' but that function no longer works reliably
	createChildDevices()

    response(cmds)
}

def childRefresh(String dni) {
    log.debug "childRefresh($dni)"
    refresh()
}
def childOn(String dni) {
    log.debug "childOn($dni)"
    def switchnum = "on${channelNumber(dni)}"
    "$switchnum"()
}
def childOff(String dni) {
    log.debug "childOff($dni)"
    def switchnum = "off${channelNumber(dni)}"
    "$switchnum"()
}

def on() {
   delayBetween([
        zwave.switchAllV1.switchAllOn().format(),
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format(),
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
    ], 1000)
}
def off() {
   delayBetween([
        zwave.switchAllV1.switchAllOff().format(),
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format(),
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
    ], 1000)
}

def on1() {
    delayBetween([
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:1, parameter:[255]).format(),
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    ], 1000)
}

def off1() {
    delayBetween([
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:1, parameter:[0]).format(),
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    ], 1000)
}

def on2() {
    delayBetween([
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:2, destinationEndPoint:2, commandClass:37, command:1, parameter:[255]).format(),
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:2, destinationEndPoint:2, commandClass:37, command:2).format()
    ], 1000)
}

def off2() {
    delayBetween([
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:2, destinationEndPoint:2, commandClass:37, command:1, parameter:[0]).format(),
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:2, destinationEndPoint:2, commandClass:37, command:2).format()
    ], 1000)
}

private channelNumber(String dni) {
	dni.split("-sw")[-1] as Integer
}

private void createChildDevices() {
	state.oldLabel = device.label
     try {
        for (i in 1..2) {
            try {
            	log.debug "Trying to create child switch if it doesn't already exist ${i}"
                def currentchild = getChildDevices()?.find { it.deviceNetworkId == "${device.deviceNetworkId}-sw${i}"}
                if (currentchild == null) {
					addChildDevice("erocm123", "Switch Child Device", "${device.deviceNetworkId}-sw${i}", device.hub.id,
						[completedSetup: true, name: "${device.displayName} (S${i})", label: "${device.displayName} (S${i})", isComponent: false])
                }
            } catch (e) {
                log.debug "Error adding child switch ${i}: ${e}"
            }
        }
    } catch (er) {
	    log.debug "Child creation failed for some reason: ${er}"
    }
}
