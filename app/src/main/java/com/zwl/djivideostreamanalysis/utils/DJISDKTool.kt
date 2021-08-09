package com.zwl.djivideostreamanalysis.utils

import dji.sdk.products.Aircraft
import dji.sdk.sdkmanager.DJISDKManager

object DJISDKTool {
    fun aircraftOrNull(): Aircraft? = DJISDKManager.getInstance().product as? Aircraft
}