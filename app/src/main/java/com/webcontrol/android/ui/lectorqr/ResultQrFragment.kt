package com.webcontrol.android.ui.lectorqr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.webcontrol.android.R
import com.webcontrol.android.databinding.FragmentResultQrBinding
import com.webcontrol.android.ui.lectorqr.LectorQrFragment
import com.webcontrol.android.ui.lectorqr.ResultQrFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultQrFragment : Fragment() {
    private lateinit var binding: FragmentResultQrBinding
    private var value: String? = null

    companion object {
        private const val argValue = "value"

        fun newInstance(value : String?): ResultQrFragment {
            val args = Bundle()
            val fragment = ResultQrFragment()
            args.putString(argValue, value)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        value = this.requireArguments().getString(argValue)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentResultQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnURL.visibility = View.GONE
        binding.lblValueQR.text = value
        val subValue = value!!.substring(0, 4)
        if (subValue.compareTo("http") == 0) {
            binding.btnURL.visibility = View.VISIBLE
        }
        binding.btnURL.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(value))
            startActivity(intent)
        }
        binding.btnBack.setOnClickListener {
            binding.btnURL.visibility = View.GONE
            val transaction : FragmentTransaction? = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.nav_host_fragment_content_main, LectorQrFragment())
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
    }
}