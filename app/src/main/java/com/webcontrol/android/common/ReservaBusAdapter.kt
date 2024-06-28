package com.webcontrol.android.common

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.angloamerican.data.ResponseReservaBus
import com.webcontrol.angloamerican.utils.Utils

class ReservaBusAdapter(movieModels: List<ResponseReservaBus>, context: Context, var listener: ReservaBusAdapterListener) :
    RecyclerView.Adapter<ReservaBusAdapter.MyViewHolder?>() {
    private var lastPosition = -1
    var row_index = -1
    private val movieModels: List<ResponseReservaBus>
    var context: Context

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_reserva_bus, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder( holder: MyViewHolder, position: Int) {
        val model: ResponseReservaBus = movieModels[position]

        holder.tvNameBus.text = model.patente
        holder.tvOrigenBus.text = model.source
        holder.tvDestinoBus.text = model.destiny
        holder.tvFechaBus.text = Utils.getNiceDate(model.date)
        holder.tvHoraBus.text = model.time
        holder.tvDuration.text = "${model.duration}Hrs"

        holder.clReservaBus2.setOnClickListener(View.OnClickListener {
            row_index = position
            notifyDataSetChanged()
            listener.OnRowItemClick(model)
        })
        if (row_index == position) {
            holder.clReservaBus2.setBackgroundColor(Color.parseColor("#C6E4E9"))
        } else {
            holder.clReservaBus2.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
        setAnimation(holder.itemView, position)
    }

    override fun getItemCount(): Int {
        return movieModels.size
    }

    inner class MyViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {

        var clReservaBus2: ConstraintLayout
        var tvNameBus: TextView
        var tvDuration: TextView
        var tvFechaBus: TextView
        var tvHoraBus: TextView
        var tvOrigenBus: TextView
        var tvDestinoBus: TextView
        var imageBus: ImageView

        init {

            clReservaBus2 = itemView.findViewById<ConstraintLayout>(R.id.clReservaBus2)
            tvNameBus = itemView.findViewById<TextView>(R.id.tvNameBus)
            tvDuration = itemView.findViewById<TextView>(R.id.tvDuration)
            tvFechaBus = itemView.findViewById<TextView>(R.id.tvFechaBus)
            tvHoraBus = itemView.findViewById<TextView>(R.id.tvHoraBus)
            tvOrigenBus = itemView.findViewById<TextView>(R.id.tvOrigenBus)
            tvDestinoBus = itemView.findViewById<TextView>(R.id.tvDestinoBus)
            imageBus = itemView.findViewById<ImageView>(R.id.ivBus)
        }
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            //TranslateAnimation anim = new TranslateAnimation(0,-1000,0,-1000);
            val anim = ScaleAnimation(
                0.0f,
                1.0f,
                0.0f,
                1.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            //anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            anim.setDuration(550) //to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim)
            lastPosition = position
        }
    }

    init {
        this.movieModels = movieModels
        this.context = context
    }

    interface ReservaBusAdapterListener {
        fun OnRowItemClick(reservaBus: ResponseReservaBus?)
    }
}