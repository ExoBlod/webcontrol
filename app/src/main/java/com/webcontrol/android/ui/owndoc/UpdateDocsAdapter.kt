package com.webcontrol.android.ui.owndoc

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.data.network.dto.OwnDocs
import com.webcontrol.android.util.correctFormatDate
import com.webcontrol.android.util.correctState
import kotlinx.android.synthetic.main.row_competencia.view.*

enum class StateUpdateDoc(val state:String){
    APROBADO("SI"),
    EN_REVISION("NO"),
    RECHAZADO("RE")
}

enum class Color(val rgb: Int) {
    RED(R.color.red),
    GREEN(R.color.green),
    YELLOW(R.color.colorCredential_bg)
}

class UpdateDocsAdapter (
    private val dataSet: List<OwnDocs>,
    private val activity: Activity,
    private val onClickContainer: (OwnDocs) -> Unit,
    ) :
    RecyclerView.Adapter<UpdateDocsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtDocType: TextView
        var txtDescription: TextView
        var txtStateDoc: TextView
        var btnViewDetails: ImageView

        init {
            txtDocType = view.findViewById(R.id.txtDocType)
            txtDescription = view.findViewById(R.id.txtDescription)
            txtStateDoc = view.findViewById(R.id.txtStateDoc)
            btnViewDetails = view.findViewById(R.id.btn_view_details)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_own_doc, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val updateDoc = dataSet[position]

        viewHolder.txtDocType.text = updateDoc.NOMBRE
        viewHolder.txtDescription.text = updateDoc.FECHASUBE.correctFormatDate()
        viewHolder.txtStateDoc.text = updateDoc.VALIDADO.correctState()

        viewHolder.btnViewDetails.setOnClickListener {
            onClickContainer.invoke(updateDoc)
        }

        when (updateDoc.VALIDADO){
            StateUpdateDoc.APROBADO.state -> viewHolder.txtStateDoc.setTextColor(ContextCompat.getColor(activity,Color.GREEN.rgb))
            StateUpdateDoc.EN_REVISION.state -> viewHolder.txtStateDoc.setTextColor(ContextCompat.getColor(activity, Color.YELLOW.rgb))
            StateUpdateDoc.RECHAZADO.state -> viewHolder.txtStateDoc.setTextColor(ContextCompat.getColor(activity,Color.RED.rgb))
        }
    }

    override fun getItemCount() = dataSet.size
}