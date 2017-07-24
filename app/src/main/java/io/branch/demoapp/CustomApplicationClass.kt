package io.branch.demoapp

import android.app.Application

import io.branch.referral.Branch

/**
 * Created by Evan Groth on 11/8/16.
 */

class CustomApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()
        Branch.enableLogging()
        Branch.getAutoInstance(this)
    }
}
