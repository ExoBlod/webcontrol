package com.webcontrol.android.ui.webview

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.databinding.FragmentWebviewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebViewFragment : Fragment(), IOnBackPressed {
    private lateinit var binding: FragmentWebviewBinding
    private lateinit var source: String

    companion object {
        private const val TAG = "WebViewFragment"
        private const val URL = "url"
        const val title="title"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title=requireArguments().getString(title)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWebviewBinding.inflate(inflater, container, false)

        //wvContainer = view.findViewById(R.id.webview)
        //progressBar = view.findViewById(R.id.webview_progressbar)
        source = requireArguments().getString(URL, "")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpWebView()
        loadUI()


    }

    private fun setUpWebView() {

        val webSettings = binding.webview.settings

        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        //webSettings.setAppCacheEnabled(true)
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT

        webSettings.setGeolocationEnabled(true)

        binding.webview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                binding.webviewProgressbar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                binding.webviewProgressbar.visibility = View.GONE
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                Log.e(TAG, "Error loading page: $error")
            }
        }

        binding.webview.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                binding.webviewProgressbar.progress = newProgress
            }
        }
    }

    private fun loadUI() {
        Log.e(TAG, "source: $source")
        binding.webview.loadUrl(source)
    }

    override fun onBackPressed(): Boolean {

        if (binding.webview.canGoBack())
            binding.webview.goBack()

        return false
    }
}