package com.webcontrol.android.ui.settings

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.Result
import dagger.hilt.android.AndroidEntryPoint
import me.dm7.barcodescanner.zxing.ZXingScannerView

@AndroidEntryPoint
class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private var mScannerView: ZXingScannerView? = null
    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)
    }

    public override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this)
        mScannerView!!.startCamera()
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()
    }

    override fun handleResult(result: Result) {
        Log.v("Barcode", result.text)
        Log.v("Barcode", result.barcodeFormat.toString())
        val codBar = result.text
        val intent = intent
        intent.putExtra("codBar", codBar)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}