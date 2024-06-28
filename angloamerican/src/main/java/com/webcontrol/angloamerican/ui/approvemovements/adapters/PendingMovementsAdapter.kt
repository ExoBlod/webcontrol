package com.webcontrol.angloamerican.ui.approvemovements.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.angloamerican.R
import com.webcontrol.angloamerican.data.model.Movement
import kotlinx.android.synthetic.main.item_movement.view.endDate
import kotlinx.android.synthetic.main.item_movement.view.movementDescription
import kotlinx.android.synthetic.main.item_movement.view.movementId
import kotlinx.android.synthetic.main.item_movement.view.ost
import kotlinx.android.synthetic.main.item_movement.view.ownerName
import kotlinx.android.synthetic.main.item_movement.view.startDate
import java.text.SimpleDateFormat
import java.util.Locale

class PendingMovementsAdapter(context: Context, private val listener: PendingMovementsListener):
    RecyclerView.Adapter<PendingMovementsAdapter.PendingMovementsViewHolder>() {

    var movements: List<Movement> = emptyList()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingMovementsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movement, parent, false)
        return PendingMovementsViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: PendingMovementsViewHolder, position: Int) {
        holder.bind(movements[position])
    }

    override fun getItemCount() = movements.size

    inner class PendingMovementsViewHolder(val view: View, val listener: PendingMovementsListener):
        RecyclerView.ViewHolder(view){

        fun bind(movement: Movement){
            val formattedStartDate = formatDate(movement.initialBatch)
            val formattedEndDate = formatDate(movement.finalBatch)

            with(view) {
                movementId.text = context.getString(R.string.item_movement_id, movement.batchId.toString())
                movementDescription.text = movement.description
                ost.text = context.getString(R.string.item_movement_ostlt, movement.ostlt)
                startDate.text = context.getString(R.string.item_movement_start_date, formattedStartDate)
                endDate.text = context.getString(R.string.item_movement_end_date, formattedEndDate)
                setOnClickListener { listener.onClickItem(movement) }
            }
        }
    }

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat(INPUT_PATTERN, Locale.getDefault())
        val outputFormat = SimpleDateFormat(OUTPUT_PATTERN, Locale.getDefault())

        val date = inputFormat.parse(inputDate)

        return date?.let { outputFormat.format(it) } ?: "Fecha inválida"
    }

    interface PendingMovementsListener{
        fun onClickItem(movement: Movement)
    }

    companion object{
        const val INPUT_PATTERN = "yyyyMMdd"
        const val OUTPUT_PATTERN = "dd/MM/yyyy"
    }
}