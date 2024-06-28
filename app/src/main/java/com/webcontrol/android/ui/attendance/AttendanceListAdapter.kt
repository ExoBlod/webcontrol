package com.webcontrol.android.ui.attendance

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.data.model.Attendance
import com.webcontrol.android.ui.covid.TestRecyclerViewAdapter
import com.webcontrol.android.util.SharedUtils
import kotlinx.android.synthetic.main.message_list_row.view.*

class AttendanceListAdapter: TestRecyclerViewAdapter<Attendance>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as ItemViewHolder
        itemHolder.setupView(getItem(position))
    }

    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        private val lblTitle: TextView = view.from
        private val lblSubtitle: TextView = view.txt_primary
        private val lblExtra: TextView = view.txt_secondary
        private val icon: TextView = view.icon_text
        private val imageProfile: ImageView = view.icon_profile
        private val iconBack: FrameLayout = view.icon_back
        private val iconStar: ImageView = view.icon_star

        init {
            view.setOnClickListener(this)
            lblTitle.setTypeface(null, Typeface.NORMAL)
            lblTitle.context.let {
                lblTitle.setTextColor(ContextCompat.getColor(it, R.color.message))
            }
            lblSubtitle.setTypeface(null, Typeface.NORMAL)
            lblSubtitle.context.let {
                lblSubtitle.setTextColor(ContextCompat.getColor(it, R.color.message))
            }
            imageProfile.setImageResource(R.drawable.bg_circle)

            iconBack.visibility = View.GONE
            icon.visibility = View.VISIBLE
            iconStar.visibility = View.GONE
        }

        fun setupView(attendance: Attendance?) {
            attendance?.let {
                if (attendance.inout == "INGRE") {
                    lblTitle.text = "INGRESO"
                    icon.text = "I"
                    imageProfile.context.let {
                        imageProfile.setColorFilter(ContextCompat.getColor(it, R.color.green))
                    }
                } else {
                    lblTitle.text = "SALIDA"
                    icon.text = "S"
                    imageProfile.context.let {
                        imageProfile.setColorFilter(ContextCompat.getColor(it, R.color.orange))
                    }
                }
                lblExtra.text = attendance.local
                icon.text = if (attendance.inout == "INGRE") "I" else "S"

                val formatDate = SharedUtils.parseStringDate(
                        attendance.date,"yyyyMMdd", "dd/MM/yyyy")
                lblSubtitle.text = "$formatDate ${attendance.time}"
            }
        }

        override fun onClick(v: View?) {
            itemClickListener?.onItemClick(adapterPosition, v)
        }
    }
}