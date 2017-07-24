package io.branch.demoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.TextView

/**
 * Created by Evan Groth on 11/8/16.
 */

class CustomOnboarding : Activity() {
    private var mReferredBy: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_onboarding)

        mReferredBy = findViewById(R.id.referredBy) as TextView
        mReferredBy!!.visibility = View.INVISIBLE

        val i = intent
        if (i != null) {
            var referredBy: String? = ""
            try {
                referredBy = i.getStringExtra("referredBy")
            } catch (e: RuntimeException) {
                referredBy = ""
            }

            if (referredBy != null && referredBy.length > 0) {
                mReferredBy!!.text = referredBy
            }
        }

        mReferredBy!!.post { revealView() }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }

    private fun revealView() {

        val myView = findViewById(R.id.referredBy)
        // get the center for the clipping circle
        val cx = myView.width / 2
        val cy = myView.height / 2

        // get the final radius for the clipping circle
        val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animator for this view (the start radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, finalRadius)

        // make the view visible and start the animation
        myView.visibility = View.VISIBLE
        anim.start()
    }

    companion object {

        private val TAG = CustomOnboarding::class.java.simpleName
    }
}
