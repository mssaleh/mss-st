metadata {
	definition (name: "Contact Sensor Child Device", namespace: "mssaleh", author: "Eric Maycock") {
		capability "Contact Sensor"
		capability "Sensor"
	}

	tiles() {
		multiAttributeTile(name:"contact", type: "generic"){
			tileAttribute ("device.contact", key: "PRIMARY_CONTROL") {
				attributeState "open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13"
				attributeState "closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00a0dc"
            }
        }
	}

}
