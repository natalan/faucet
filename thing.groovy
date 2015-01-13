/**
 *  Delta Faucet Touch2o device type
 *
 *  Author: Andrei Zharov
 *  Date: 01-19-2014
 */

definition (name: "Delta Faucet Touch2o", namespace: "belmass", author: "Andrei Zharov") {
    capability "Switch"
}

metadata {
    // tile definitions
    tiles {
        standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
            state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
            state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
        }
        standardTile("indicator", "device.indicatorStatus", inactiveLabel: false, decoration: "flat") {
            state "when off", action:"indicator.indicatorWhenOn", icon:"st.indicators.lit-when-off"
            state "when on", action:"indicator.indicatorNever", icon:"st.indicators.lit-when-on"
            state "never", action:"indicator.indicatorWhenOff", icon:"st.indicators.never-lit"
        }
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }

        main "switch"
        details(["switch","refresh","indicator"])
    }
}

Map parse(String description) {
    def value = zigbee.parse(description)?.text

    log.debug "Received callback: " + description
    log.debug "Parsed value: " + value

    def linkText = getLinkText(device)
    def descriptionText = getDescriptionText(description, linkText, value)
    def handlerName = value
    def isStateChange = value != "ping"
    def displayed = value && isStateChange
    def device = null
    // find switch that called the cloud
    if (value in ["open", "close"]) {
        device = "switch"
    }

    def result = [
            value: value,
            name: device,
            handlerName: handlerName,
            linkText: linkText,
            descriptionText: descriptionText,
            isStateChange: isStateChange,
            displayed: displayed
    ]

    log.debug "Message received from the faucet controller: " + result
    result
}

def on() {
    log.debug "Turning on the faucet"
    zigbee.smartShield(text: "on").format()
}

def off() {
    log.debug "Turning off the faucet"
    zigbee.smartShield(text: "off").format()
}
