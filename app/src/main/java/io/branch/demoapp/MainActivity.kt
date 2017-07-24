package io.branch.demoapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import org.json.JSONException
import org.json.JSONObject

import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.referral.SharingHelper
import io.branch.referral.util.LinkProperties
import io.branch.referral.util.ShareSheetStyle

class MainActivity : AppCompatActivity() {
    private var mFab: FloatingActionButton? = null
    private var mReferredByET: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFab = findViewById(R.id.fab) as FloatingActionButton
        mFab!!.setOnClickListener {
            genLink(mReferredByET!!.text.toString())
            val shake = AnimationUtils.loadAnimation(baseContext, R.anim.shake)
            mFab!!.startAnimation(shake)
        }

        mReferredByET = findViewById(R.id.referredByET) as EditText
        mReferredByET!!.setOnEditorActionListener(TextView.OnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_NULL && keyEvent.action == KeyEvent.ACTION_DOWN) {
                genLink(mReferredByET!!.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
    }

    override fun onNewIntent(intent: Intent) {
        this.intent = intent
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        initBranch()
    }

    private fun initBranch() {
        val branch = Branch.getInstance()
        branch.initSession({ referringParams, error ->
            if (error == null) {
                var clickedBranchLink = false
                try {
                    clickedBranchLink = referringParams.getBoolean("+clicked_branch_link")
                } catch (e: JSONException) {
                }

                if (clickedBranchLink) {
                    // do stuff with Branch link data
                    try {
                        val referredBy = referringParams.getString("referredBy")
                        if (referredBy != null && referredBy.length > 0) {
                            val i = Intent(applicationContext, CustomOnboarding::class.java)
                            i.putExtra("referredBy", referredBy)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(i)
                        }
                    } catch (e: JSONException) {
                    }

                } else {
                    //do stuff without Branch link data
                }
            } else {
                // error occurred
                Log.e(TAG, error.message)
            }
        }, this.intent.data, this)
    }

    private fun genLink(name: String) {
        if (name.length == 0) {
            Toast.makeText(this, "Please enter a name for your referral link", Toast.LENGTH_LONG).show()
            return
        }

        val branchUniversalObject = BranchUniversalObject()
                .setCanonicalIdentifier("item/12345")
                .setTitle("My Content Title")
                .setContentDescription("My Content Description")
                .setContentImageUrl("https://example.com/mycontent-12345.png")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("referredBy", name)

        val linkProperties = LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .addControlParameter("\$desktop_url", "http://example.com/home")
                .addControlParameter("\$ios_url", "http://example.com/ios")

        val shareSheetStyle = ShareSheetStyle(this@MainActivity, "Check this out!", "This stuff is awesome: ")
                .setCopyUrlStyle(resources.getDrawable(android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                .setMoreOptionStyle(resources.getDrawable(android.R.drawable.ic_menu_search), "Show more")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .setAsFullWidthStyle(true)
                .setSharingTitle("Share To")

        branchUniversalObject.showShareSheet(this,
                linkProperties,
                shareSheetStyle,
                object : Branch.BranchLinkShareListener {
                    override fun onShareLinkDialogLaunched() {}

                    override fun onShareLinkDialogDismissed() {}

                    override fun onLinkShareResponse(sharedLink: String, sharedChannel: String, error: BranchError) {}

                    override fun onChannelSelected(channelName: String) {}
                }, object : Branch.IChannelProperties {
            override fun getSharingTitleForChannel(channel: String): String? {
                return if (channel.contains("Messaging"))
                    "title for SMS"
                else if (channel.contains("Slack"))
                    "title for slack"
                else if (channel.contains("Gmail")) "title for gmail" else null
            }

            override fun getSharingMessageForChannel(channel: String): String? {
                return if (channel.contains("Messaging"))
                    "message for SMS"
                else if (channel.contains("Slack"))
                    "message for slack"
                else if (channel.contains("Gmail")) "message for gmail" else null
            }
        })
    }

    companion object {

        private val TAG = MainActivity::class.java!!.getSimpleName()
    }
}
