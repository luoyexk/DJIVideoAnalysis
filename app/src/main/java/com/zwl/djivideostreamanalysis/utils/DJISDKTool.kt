package com.zwl.djivideostreamanalysis.utils

import dji.sdk.camera.Camera
import dji.sdk.products.Aircraft
import dji.sdk.sdkmanager.DJISDKManager

object DJISDKTool {
    val aircraft: Aircraft? get() = DJISDKManager.getInstance().product as? Aircraft
    val camera: Camera? get() = aircraft?.camera
}