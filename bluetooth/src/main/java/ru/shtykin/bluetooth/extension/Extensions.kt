package ru.shtykin.bluetooth.extension

import ru.shtykin.bluetooth.domain.entity.BtDevice

fun List<BtDevice>.filterBoundedDevice(boundedDevices: List<BtDevice>): List<BtDevice> =
    this.filter { onlineDevice ->
        boundedDevices.forEach { boundedDevice ->
            if (boundedDevice.mac == onlineDevice.mac) return@filter false
        }
        return@filter true
    }