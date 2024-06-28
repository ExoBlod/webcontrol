package com.webcontrol.angloamerican.ui.checklistpreuso

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.navigation.fragment.NavHostFragment
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.databinding.ActivityCheckListPreUsoBinding
import com.webcontrol.angloamerican.ui.checklistpreuso.data.ContentPreUso
import com.webcontrol.angloamerican.utils.ConstantNameMenu.BUNDLE_NAME_INSPECTOR
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class CheckListPreUsoActivity : AppCompatActivity(), CameraRequestListener {

    private lateinit var binding: ActivityCheckListPreUsoBinding
    private val parentViewModel: CheckListPreUsoViewModel by viewModels()
    private lateinit var uri: Uri
    private lateinit var fragmentListener: FragmentListener
    private var cardPosition: Int = 0
    private var groupPosition: Int = 0

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        if (isSaved) {
            val listGroup = parentViewModel.uiListGroup.toMutableList()
            listGroup[groupPosition].questions.get(cardPosition).photo = uri.toString()
            parentViewModel.setListQuestions(listGroup)
            this.fragmentListener.updateFragmentList(cardPosition)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckListPreUsoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "CheckList Pre Uso"
        val intent = intent
        val supervisor = intent.getStringExtra(BUNDLE_NAME_INSPECTOR)?: ""
        val isConsultInspection = intent.getBooleanExtra("isConsultInspection",false)
        val workerId = intent.getStringExtra("workerId") ?: ""
        parentViewModel.setCheckListPreUso(ContentPreUso(workerId,isConsultInspection, supervisor))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        with(supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment) {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                val test = parentViewModel.isSearching
            }
        }
    }


    override fun requestCamera(groupPosition: Int, itemPosition: Int) {
        this.cardPosition = itemPosition
        this.groupPosition = groupPosition

        val photoFile = File.createTempFile(
            "IMG_",
            ".jpg",
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
        uri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            photoFile
        )
        takePicture.launch(uri)
    }

    fun setFragmentListener(listener: FragmentListener) {
        this.fragmentListener = listener
    }

    interface FragmentListener {
        fun updateFragmentList(catId: Int)
    }

    fun showLoading(isShowing: Boolean, text: String = "Cargando"){
        if(isShowing){
            binding.clProgressBar.visibility = View.VISIBLE
            binding.tvProgressBar.text = text
        } else {
            binding.clProgressBar.visibility = View.GONE
        }
    }
}

interface CameraRequestListener {
    fun requestCamera(groupPosition: Int, itemPosition: Int)
}
