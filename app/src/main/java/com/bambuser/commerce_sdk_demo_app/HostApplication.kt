package com.bambuser.commerce_sdk_demo_app

import android.app.Application
import com.bambuser.social_commerce_sdk.BambuserSDK
import com.bambuser.social_commerce_sdk.data.OrganizationServer

class HostApplication: Application() {
    // You can have multiple instances of the SDK
    lateinit var globalBambuserSDK: BambuserSDK
    lateinit var euBambuserSDK: BambuserSDK

    override fun onCreate() {
        super.onCreate()
        globalBambuserSDK = BambuserSDK(
            applicationContext = this,
            organizationServer = OrganizationServer.US,
        )

        euBambuserSDK = BambuserSDK(
            applicationContext = this,
            organizationServer = OrganizationServer.EU,
        )
    }
}