package com.webcontrol.android.ui.covid.declaracion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.webcontrol.android.R
import com.webcontrol.android.ui.covid.TestRecyclerViewAdapter
import kotlinx.android.synthetic.main.row_dj_answer.view.*

class DJOMAdapter: TestRecyclerViewAdapter<Pair<String,Boolean>>() {

    companion object {
        private const val TAG = "DJOMAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_dj_answer, parent, false), ItemClickListener())
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myHolder = holder as MyViewHolder
        val item = getItem(position)
        myHolder.btnListener.updatePosition(position)
        item?.let {
            myHolder.btnListener.updateResult(it.first)
        }
        myHolder.setUpView(respuesta = getItem(position))
    }

    inner class MyViewHolder(view: View, listener: ItemClickListener): RecyclerView.ViewHolder(view) {

        private val btnAnswer: MaterialButton = view.btn_answer
        val btnListener: ItemClickListener = listener

        init {
            btnAnswer.setOnClickListener(btnListener)
        }

        fun setUpView(respuesta: Pair<String,Boolean>?) {
            respuesta?.let {
                btnAnswer.text = it.first
                btnAnswer.isChecked = it.second

            }
        }
    }

    inner class ItemClickListener: View.OnClickListener {

        private var position: Int = 0
        private var result: String? = ""

        fun updatePosition(_position: Int){
            position = _position
        }

        fun updateResult(_result: String?){
            result = _result
        }

        override fun onClick(v: View?) {
            (v as MaterialButton).isChecked = false
            djItemClickListener?.onItemClick(position, v, result!!)
        }


    }
}