package com.webcontrol.android.ui.onboarding

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.webcontrol.android.R
import com.webcontrol.android.databinding.ActivityLauncherBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.ui.login.LoginActivity
import com.webcontrol.android.ui.messages.DetalleActivity
import com.webcontrol.android.ui.videocall.VideoCallActivity
import com.webcontrol.android.util.SharedUtils.getRoomTwilio
import com.webcontrol.android.util.SharedUtils.getSession
import com.webcontrol.android.util.SharedUtils.isFirtRun
import com.webcontrol.android.util.SharedUtils.setRoomTwilio
import dagger.hilt.android.AndroidEntryPoint

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLauncherBinding
    private val mHideHandler = Handler()
    private var mContentView: View? = null
    private var mMessageId: String? = null
    private var roomName: String? = null
    private val mHidePart2Runnable = Runnable {
        mContentView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
    private val mShowPart2Runnable = Runnable {
        val actionBar = supportActionBar
        actionBar?.show()
    }
    private var mVisible = false
    private val mHideRunnable = Runnable { hide() }
    private val mDelayHideTouchListener = OnTouchListener { view, motionEvent ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContentView = findViewById(R.id.splash_screen_root)
        val extras = intent.extras
        if (extras != null) {
            mMessageId = extras.getString(MESSAGE_ID)
            roomName = extras.getString(ROOM_CONTENT_ID)
            Log.e("LaucherActivity", "Extras: $extras")
            saveRoomToShared(roomName)
        }
        mVisible = true
        mContentView!!.setOnClickListener(View.OnClickListener { toggle() })
    }

    private fun saveRoomToShared(roomName: String?) {
        setRoomTwilio(this, roomName)
    }

    override fun onNewIntent(intent: Intent) {
        Log.e("launcher activity", "New intent recibido")
        super.onNewIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        if (isFirtRun(this)) {
            startActivity(Intent(this, OnBoardingActivity::class.java))
            finish()
        } else {
            checkPermissions()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delayedHide(100)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        val actionBar = supportActionBar
        actionBar?.hide()
        mVisible = false
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @SuppressLint("InlinedApi")
    private fun show() {
        mContentView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        mVisible = true
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    private fun checkPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if (getSession(this@LauncherActivity)) {
                if (mMessageId != null && !mMessageId!!.isEmpty()) {
                    val intent: Intent //= new Intent(LauncherActivity.this, DetalleActivity.class);
                    if (!getRoomTwilio(applicationContext).isEmpty()) {
                        intent = Intent(this@LauncherActivity, VideoCallActivity::class.java)
                        intent.putExtra(TWILIO_ACCESS_TOKEN, "")
                        startActivityForResult(intent, 1)
                    } else {
                        intent = Intent(this@LauncherActivity, DetalleActivity::class.java)
                        intent.putExtra(DetalleActivity.MESSAGE_ID, mMessageId)
                        startActivityForResult(intent, 1)
                    }
                } else {
                    startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
                    finish()
                }
            } else {
                startActivity(Intent(this@LauncherActivity, LoginActivity::class.java))
                finish()
            }
        }
        else{
            Dexter.withContext(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        if (getSession(this@LauncherActivity)) {
                            if (mMessageId != null && !mMessageId!!.isEmpty()) {
                                val intent: Intent //= new Intent(LauncherActivity.this, DetalleActivity.class);
                                if (!getRoomTwilio(applicationContext).isEmpty()) {
                                    intent = Intent(this@LauncherActivity, VideoCallActivity::class.java)
                                    intent.putExtra(TWILIO_ACCESS_TOKEN, "")
                                    startActivityForResult(intent, 1)
                                } else {
                                    intent = Intent(this@LauncherActivity, DetalleActivity::class.java)
                                    intent.putExtra(DetalleActivity.MESSAGE_ID, mMessageId)
                                    startActivityForResult(intent, 1)
                                }
                            } else {
                                startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
                                finish()
                            }
                        } else {
                            startActivity(Intent(this@LauncherActivity, LoginActivity::class.java))
                            finish()
                        }
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        if (response!!.isPermanentlyDenied){
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:$packageName"))
                            startActivity(intent)
                            finish()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        MaterialDialog.Builder(this@LauncherActivity)
                            .title(R.string.app_name)
                            .content("El permiso es necesario para el correcto funcionamiento.")
                            .positiveText(android.R.string.ok)
                            .onPositive { dialog, which ->
                                dialog.dismiss()
                                token!!.continuePermissionRequest()
                            }
                            .negativeText(android.R.string.cancel)
                            .onNegative { dialog: MaterialDialog, which: DialogAction? ->
                                token!!.cancelPermissionRequest()
                                finish()
                            }
                            .autoDismiss(false)
                            .cancelable(false)
                            .show()
                    }

                }).check()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val intentMain = Intent(this@LauncherActivity, MainActivity::class.java)
            startActivity(intentMain)
            finish()
        }
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [.AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [.AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
        const val MESSAGE_ID = "message_id"
        const val TWILIO_ACCESS_TOKEN = "TWILIO_ACCESS_TOKEN"
        private const val ROOM_CONTENT_ID = "room"
    }
}