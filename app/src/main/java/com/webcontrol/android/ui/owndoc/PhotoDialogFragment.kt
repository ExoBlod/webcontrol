package com.webcontrol.android.ui.owndoc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.request.RequestOptions
import com.webcontrol.android.R
import com.webcontrol.android.databinding.FragmentDialogPhotoBinding
import com.webcontrol.android.util.GlideApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dialog_photo.view.*

@AndroidEntryPoint
class PhotoDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentDialogPhotoBinding
    companion object {

        const val TAG = "SimpleDialog"

        private const val KEY_BITMAP = "KEY_BITMAP"
        private const val KEY_TITLE = "KEY_TITLE"

        fun newInstance(bitmap: ByteArray, title: String): PhotoDialogFragment {
            val args = Bundle()
            args.putByteArray(KEY_BITMAP, bitmap)
            args.putString(KEY_TITLE, title)
            val fragment = PhotoDialogFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
        setupClickListeners(view)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupView(view: View) {
        val ba= arguments?.getByteArray(KEY_BITMAP)
        val title = arguments?.getString(KEY_TITLE)

        view.tvTitlePhoto.text = title

        GlideApp.with(this)
            .load(ba)
            .placeholder(R.drawable.ic_account_circle_materialgrey_240dp)
            .apply(RequestOptions.fitCenterTransform())
            //.apply(RequestOptions.circleCropTransform())
            .into(view.imShowPhoto!!)
    }

    private fun setupClickListeners(view: View) {
        view.btnCloseWindows.setOnClickListener {
            dialog!!.dismiss()
        }
    }

}