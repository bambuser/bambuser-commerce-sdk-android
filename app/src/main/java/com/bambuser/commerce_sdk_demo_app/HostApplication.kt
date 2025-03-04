package com.bambuser.commerce_sdk_demo_app

import android.app.Application
import com.bambuser.social_commerce_sdk.BamBuserSDK
import com.bambuser.social_commerce_sdk.data.OrganizationServer

class HostApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Need to initialize the SDK before using it
        BamBuserSDK.initialize(
            applicationContext = this,
            organizationServer = OrganizationServer.US,
        )
    }
}