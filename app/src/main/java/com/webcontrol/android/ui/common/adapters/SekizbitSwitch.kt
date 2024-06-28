package com.webcontrol.android.ui.common.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatRatingBar
import java.util.*

interface OnSelectedChangeListener : EventListener {
    fun onSelectedChange(sender: SekizbitSwitch?)
}

class SekizbitSwitch(container: View) {
    private val buttons: Array<Button?> = arrayOfNulls(3)
    private val ratingBars: Array<AppCompatRatingBar?> = arrayOfNulls(1)
    var checkedIndex = 0
    private val that: SekizbitSwitch = this
    private val listeners: MutableList<OnSelectedChangeListener> = ArrayList()

    init {
        var btnCounter = 0
        var i = 0
        var j = 0
        while (i < (container as ViewGroup).childCount && j < 3) {
            val nextChild = container.getChildAt(i)
            if (nextChild is Button) {
                buttons[j] = nextChild
                j++
                btnCounter = j
            }else
                if(nextChild is AppCompatRatingBar) {
                    ratingBars[j] = nextChild
                    j++
                }
            i++
        }
        val customSwitchListener = View.OnClickListener { view ->
            if(buttons[0] != null && buttons[1] != null && buttons[2] != null) {
                if (view as Button === buttons[0]) {
                    buttons[0]!!.isSelected = true
                    buttons[1]!!.isSelected = false
                    buttons[2]!!.isSelected = false
                    checkedIndex = 0
                } else if(view as Button === buttons[1]) {
                    buttons[0]!!.isSelected = false
                    buttons[1]!!.isSelected = true
                    buttons[2]!!.isSelected = false
                    checkedIndex = 1
                }
                else {
                    buttons[0]!!.isSelected = false
                    buttons[1]!!.isSelected = false
                    buttons[2]!!.isSelected = true
                    checkedIndex = 2
                }
            }
            else if(buttons[0] != null && buttons[1] != null) {
                if (view as Button === buttons[0]) {
                    buttons[0]!!.isSelected = true
                    buttons[1]!!.isSelected = false
                    checkedIndex = 0
                } else {
                    buttons[0]!!.isSelected = false
                    buttons[1]!!.isSelected = true
                    checkedIndex = 1
                }
            }

            for (hl in listeners) {
                hl.onSelectedChange(that)
            }
        }

        val customSwitchListenerRatingBar = View.OnClickListener { view->
            if(view as AppCompatRatingBar === ratingBars[0])
                ratingBars[0]!!.rating.toInt()
        }

        if(buttons[0] != null && buttons[1] != null && buttons[2] != null){
            buttons[0]!!.setOnClickListener(customSwitchListener)
            buttons[1]!!.setOnClickListener(customSwitchListener)
            buttons[2]!!.setOnClickListener(customSwitchListener)
        }
        else if(buttons[0] != null && buttons[1] != null){
            buttons[0]!!.setOnClickListener(customSwitchListener)
            buttons[1]!!.setOnClickListener(customSwitchListener)
        }


        if(ratingBars[0] != null)
            ratingBars[0]!!.setOnClickListener(customSwitchListenerRatingBar)
    }

    fun setOnChangeListener(l: OnSelectedChangeListener?) {
        listeners.clear()
        if (l != null) {
            listeners.add(l)
        }
    }

    fun setSelected(index: Int) {
        buttons[index]!!.isSelected = true
    }

    fun cleanSelection() {
        if(buttons[0] != null && buttons[1] != null &&  buttons[2] != null){
            buttons[0]!!.isSelected = false
            buttons[1]!!.isSelected = false
            buttons[2]!!.isSelected = false
        }
        else if(buttons[0] != null && buttons[1] != null){
            buttons[0]!!.isSelected = false
            buttons[1]!!.isSelected = false
        }
        else
            if (ratingBars[0] != null)
                ratingBars[0]?.rating  = 0.0f
    }
}
