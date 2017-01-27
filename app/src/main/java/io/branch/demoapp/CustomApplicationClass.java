package io.branch.demoapp;

import android.app.Application;

import io.branch.referral.Branch;

/**
 * Created by Evan Groth on 11/8/16.
 */

public class CustomApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Branch.getAutoInstance(this);
    }
}
