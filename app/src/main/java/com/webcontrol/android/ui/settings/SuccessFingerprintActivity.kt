package com.webcontrol.android.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.webcontrol.android.databinding.ActivitySuccessFingerprintBinding
import com.webcontrol.android.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SuccessFingerprintActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySuccessFingerprintBinding
    private var fromSettings: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessFingerprintBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val extras = intent.extras

        if (extras != null) {
            fromSettings = extras.getBoolean("SETTINGS")
        }

        binding.buttonAction.setOnClickListener {
            if (!fromSettings) {
                val intent = Intent(this@SuccessFingerprintActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val result = Intent()
                result.putExtra("RESULT", true)
                setResult(Activity.RESULT_OK, result)
                finish()
            }
        }
    }
}
