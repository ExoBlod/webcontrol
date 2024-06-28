package com.webcontrol.android.ui.common.adapters

import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.CheckListTest_Detalle

class StarRecyclerViewAdapter(val listener: CheckListAdapterListener) : RecyclerView.Adapter<StarRecyclerViewAdapter.StarViewHolder>() {
    private val TAG = javaClass.simpleName
    private var mAllRepositories: List<CheckListTest_Detalle>? = null
    private var buttonsDisabled: Boolean = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StarViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.row_question_star, parent, false)
        return StarViewHolder(view,listener)
    }

    override fun onBindViewHolder(holder: StarViewHolder, position: Int) {
        holder.bind(mAllRepositories!![position],buttonsDisabled)
    }

    override fun getItemCount(): Int {
        return if (mAllRepositories == null) 0 else mAllRepositories!!.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setmAllRepositories(repositories: List<CheckListTest_Detalle>?) {
        mAllRepositories = repositories
        notifyDataSetChanged()
    }
    class StarViewHolder(itemView: View, val listener: CheckListAdapterListener) : RecyclerView.ViewHolder(itemView){
        // Find all the views of the list item
        private val mTitleName: TextView = itemView.findViewById(R.id.lblTestTitle)
        private val mRating: RatingBar = itemView.findViewById(R.id.rating)
        private val mRatingScale: TextView = itemView.findViewById(R.id.mRatingScale)
        init{}
        // Show the data in the views
        fun bind(item: CheckListTest_Detalle,buttonsDisabled: Boolean) {
            val name = item.descripcion
            mTitleName.text = name
            val respuestas:MutableList<String> = item.respuestas!!.split(",").toMutableList()
            val respuestasIndices:MutableList<String> =
                    item.respuestaSeleccionada!!.split(",").map { it.trim() }.toMutableList()
            mRating.numStars = respuestas.size
            mRating.stepSize = 1.0f
            respuestas[0] = respuestas[0].substring(1)
            respuestas[respuestas.lastIndex] = respuestas.last().substring(0,respuestas.last().length-1)
            respuestasIndices[0] = respuestasIndices[0].substring(1)
            respuestasIndices[respuestasIndices.lastIndex] = respuestasIndices.last().substring(0,respuestasIndices.last().length-1)

            if (!buttonsDisabled) {
                applyClickEvents(item)
            } else {
                mRating.setIsIndicator(true)
                val itemBuscar = item.groupId.toString()
                val indiceRes = respuestasIndices.indexOfFirst { it == itemBuscar }
                mRating.rating = (indiceRes+1).toFloat()
                mRatingScale.text = respuestas[indiceRes]
            }
        }

        private fun applyClickEvents(item: CheckListTest_Detalle) {
            val respuestas:MutableList<String> = item.respuestas!!.split(",").toMutableList()
            val respuestasIndices:MutableList<String> =
                item.respuestaSeleccionada!!.split(",").map { it.trim() }.toMutableList()

            respuestas[0] = respuestas[0].substring(1)
            respuestas[respuestas.lastIndex] = respuestas.last().substring(0,respuestas.last().length-1)
            respuestasIndices[0] = respuestasIndices[0].substring(1)
            respuestasIndices[respuestasIndices.lastIndex] = respuestasIndices.last().substring(0,respuestasIndices.last().length-1)
            mRating.onRatingBarChangeListener =
                    RatingBar.OnRatingBarChangeListener { ratingBar, v, b ->
                        mRatingScale.text = java.lang.String.valueOf(v)
                        when (ratingBar.rating.toInt()) {
                            1 -> mRatingScale.text = respuestas[0]
                            2 -> mRatingScale.text = respuestas[1]
                            3 -> mRatingScale.text = respuestas[2]
                            4 -> mRatingScale.text = respuestas[3]
                            5 -> mRatingScale.text = respuestas[4]
                            else -> mRatingScale.text = ""
                        }
                        item.estado = ratingBar.rating.toInt()
                        item.groupId = respuestasIndices[ratingBar.rating.toInt() - 1].trim().toInt()
                        listener.onClickButtonSI(item)
                    }
        }
    }
}
