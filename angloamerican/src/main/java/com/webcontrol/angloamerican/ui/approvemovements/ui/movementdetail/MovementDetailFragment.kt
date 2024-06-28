package com.webcontrol.angloamerican.ui.approvemovements.ui.movementdetail

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.model.ApproveMovementRequest
import com.webcontrol.angloamerican.data.model.DenyMovementRequest
import com.webcontrol.angloamerican.databinding.FragmentMovementDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovementDetailFragment(activity: FragmentActivity): DialogFragment() {
    private lateinit var binding: FragmentMovementDetailBinding
    private var dismissListener: DialogDismissListener? = null
    val viewModel = ViewModelProvider(activity)[MovementDetailViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.AppTheme_AppBarOverlay)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovementDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeMovementStates()
        addButtonsListener()
        val batchId = arguments?.getString("batchId")
        if(!batchId.isNullOrEmpty()){
            viewModel.getMovementDetail(batchId)
        } else {
            Toast.makeText(activity, getString(R.string.parameter_not_found), Toast.LENGTH_LONG).show()
        }
    }

    private fun observeMovementStates() {
        observeMovementDetail()
        observeApproveMovementRequest()
        observeDenyMovementRequest()
    }

    private fun addButtonsListener() {
        binding.approveButton.setOnClickListener {
            val movement = ApproveMovementRequest(
                idPass = binding.batchName.text.toString().toInt(),
                plate = binding.plate.text.toString(),
                divisionCode = binding.divisionId.text.toString(),
                passType = binding.passType.text.toString(),
                startDate = binding.startBatchDate.text.toString(),
                endDate = binding.endBatchDate.text.toString(),
                approverUser = binding.approverUser.text.toString()
            )
            viewModel.approveMovement(movement)
        }

        binding.denyButton.setOnClickListener {
            askForAdditionalCommentDialog(activity as Context)
        }
    }

    private fun askForAdditionalCommentDialog(context: Context){
        val dialogBinding = layoutInflater.inflate(com.webcontrol.angloamerican.R.layout.popup_add_comment_on_deny_movement, null)
        val confirmDialog = Dialog(context)
        val denyButton = dialogBinding.findViewById<Button>(com.webcontrol.angloamerican.R.id.denyButton)
        val editTextView = dialogBinding.findViewById<EditText>(com.webcontrol.angloamerican.R.id.editTextMessage)
        with(confirmDialog) {
            setContentView(dialogBinding)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            denyButton.setOnClickListener {
                val message = editTextView.text.toString()
                sendDenyMovementRequest(message)
                dismiss()
            }
            show()
        }
    }

    private fun sendDenyMovementRequest(message: String) {
        val movement = DenyMovementRequest(
            idPass = binding.batchName.text.toString().toInt(),
            approverUser = binding.approverUser.text.toString(),
            rejectReason = message
        )
        viewModel.denyMovement(movement)
    }

    private fun showSuccessNotificationOnDenyMovement(){
        val dialogBinding = layoutInflater.inflate(com.webcontrol.angloamerican.R.layout.popup_deny_movement_success, null)
        val successDialog = Dialog(activity as Context)
        val yesBtn = dialogBinding.findViewById<Button>(com.webcontrol.angloamerican.R.id.btnOK)
        with(successDialog) {
            setContentView(dialogBinding)
            setCancelable(true)
            setOnCancelListener {
                dismiss()
            }
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
            yesBtn.setOnClickListener {
                dismiss()
                closeMovement()
            }
        }
    }

    private fun observeMovementDetail(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movementDetail.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    MovementDetailUIEvent.ShowLoading -> {
                        showLoading(true)
                    }
                    MovementDetailUIEvent.HideLoading -> {
                        showLoading(false)
                    }
                    MovementDetailUIEvent.Error -> {}
                    is MovementDetailUIEvent.Success -> {
                        println(event.movement.toString())
                        event.movement.let {
                            binding.batchName.text = it.batchNumber.toString()
                            binding.issueDate.text = it.creationDate
                            binding.startBatchDate.text = it.startDate
                            binding.endBatchDate.text = it.endDate
                            binding.approverUser.text= it.approverUserId
                            binding.approverUserName.text = it.approverUserName
                            binding.companyName.text = it.sponsorName
                            binding.passType.text = it.passType
                            binding.ostlt.text = it.ostlt
                            binding.divisionId.text = it.divisionId
                            binding.divisionName.text = it.division
                            binding.plate.text = it.batchRUT
                        }
                    }
                }
            }
        }
    }

    private fun observeDenyMovementRequest(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.denyMovement.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    DenyMovementUIEvent.ShowLoading -> {
                        showLoading(true)
                    }
                    DenyMovementUIEvent.HideLoading -> {
                        showLoading(false)
                    }
                    DenyMovementUIEvent.Error -> {
                        Toast.makeText(requireContext(), getString(R.string.error_denying_movement), Toast.LENGTH_LONG).show()
                    }
                    is DenyMovementUIEvent.Success -> {
                        showSuccessNotificationOnDenyMovement()
                    }
                }
            }
        }
    }

    private fun observeApproveMovementRequest(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.approveMovement.flowWithLifecycle(lifecycle).collectLatest { event ->
                when (event) {
                    ApproveMovementUIEvent.ShowLoading -> {
                        showLoading(true)
                    }
                    ApproveMovementUIEvent.HideLoading -> {
                        showLoading(false)
                    }
                    is ApproveMovementUIEvent.Error -> {
                        Toast.makeText(requireContext(), getString(R.string.error_showing_movement, event.message), Toast.LENGTH_LONG).show()
                    }
                    is ApproveMovementUIEvent.Success -> {
                        showSuccessNotificationOnDenyMovement()
                    }
                }
            }
        }
    }

    companion object{
        fun newInstance(batchId: String? = null, activity: FragmentActivity): MovementDetailFragment{
            val fragment = MovementDetailFragment(activity)
            val bundle = bundleOf("batchId" to batchId)
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun showLoading(isShowing: Boolean, text: String = "Cargando"){
        if(isShowing){
            binding.clProgressBar.visibility = View.VISIBLE
            binding.tvProgressBar.text = text
        } else {
            binding.clProgressBar.visibility = View.GONE
        }
    }

    private fun closeMovement(){
        parentFragmentManager.beginTransaction().remove(this@MovementDetailFragment).commit()
        parentFragmentManager.popBackStack()
    }

    fun setDismissListener(listener: DialogDismissListener) {
        dismissListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDialogDismissed()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissListener = null
    }
}

interface DialogDismissListener {
    fun onDialogDismissed()
}