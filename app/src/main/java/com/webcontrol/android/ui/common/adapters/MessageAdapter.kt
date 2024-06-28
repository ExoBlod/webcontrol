package com.webcontrol.android.ui.common.adapters

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.SparseBooleanArray
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.webcontrol.android.R
import com.webcontrol.android.data.db.entity.Message
import com.webcontrol.android.util.FlipAnimator.flipView
import com.webcontrol.android.util.SharedUtils.fechaPretty
import com.webcontrol.android.util.SharedUtils.stringToDate
import com.webcontrol.android.util.SharedUtils.wCDate
import kotlinx.android.synthetic.main.message_list_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val mContext: Context,
    messages: MutableList<Message>,
    listener: MessageAdapterListener
) : RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {
    private var messages: MutableList<MessageWrapper>
    private val listener: MessageAdapterListener
    val selectedItems: SparseBooleanArray
    private val animationItemsIndex: SparseBooleanArray
    private var reverseAllAnimations = false

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), OnLongClickListener {

        var lblRemitente: TextView = view.from
        var lblFecha: TextView = view.timestamp
        var lblPrimary: TextView = view.txt_primary
        var lblSecondary: TextView = view.txt_secondary
        var icon_text: TextView = view.icon_text
        var iconFront: FrameLayout = view.icon_front
        var iconBack: FrameLayout = view.icon_back
        var messageContainer: ConstraintLayout = view.message_container
        var iconContainer: RelativeLayout = view.icon_container
        var imgProfile: ImageView = view.icon_profile
        var iconImp: ImageView = view.icon_star
        var layoutIconStar: View = view.layout_iconstar

        override fun onLongClick(view: View): Boolean {
            listener.onRowLongClicked(adapterPosition)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            return true
        }

        init {
            view.setOnLongClickListener(this)
        }
    }

    fun setData(messages: MutableList<Message>) {
        this.messages = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            messages.removeIf { message -> message.estado == 0 }
        }

        for (message in messages) {
            if (message.estado != 0) {
                this.messages.add(MessageWrapper(message))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_list_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = messages[position]
        if (message.fecha == null)
            message.fecha = wCDate
        if (message.hora == null)
            message.hora = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date()).toString()
        val remitente = message.remitente
        holder.lblRemitente.text = remitente ?: "-"
        holder.lblPrimary.text =
            if (message.asunto == null) "Sin Asunto" else message.asunto!!.replace(
                "<[^>]*>".toRegex(),
                ""
            )
        holder.lblSecondary.text = message.mensaje.replace("<[^>]*>".toRegex(), "")
        holder.lblFecha.text = fechaPretty(message.fecha!!, message.hora!!)
        holder.icon_text.text =
            if (message.asunto.isNullOrEmpty()) "S" else message.asunto!!.substring(0, 1)
                .toUpperCase()
        holder.itemView.isActivated = selectedItems[position, false]
        applyReadStatus(holder, message)
        applyImportant(holder, message)
        applyIconAnimation(holder, position)
        applyProfilePicture(holder, message)
        applyClickEvents(holder, position)
    }

    private fun applyClickEvents(holder: MyViewHolder, position: Int) {
        holder.iconContainer.setOnClickListener { listener.onIconClicked(position) }
        holder.layoutIconStar.setOnClickListener { v: View? ->
            listener.onIconImportantClicked(
                position
            )
        }
        holder.messageContainer.setOnClickListener { v: View? ->
            listener.onMessageRowClicked(position)
        }
        holder.messageContainer.setOnLongClickListener { view: View ->
            listener.onRowLongClicked(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            true
        }
    }

    private fun applyProfilePicture(holder: MyViewHolder, message: MessageWrapper) {
        holder.imgProfile.setImageResource(R.drawable.bg_circle)
        holder.imgProfile.setColorFilter(message.color)
        holder.icon_text.visibility = View.VISIBLE
    }

    private fun applyIconAnimation(holder: MyViewHolder, position: Int) {
        if (selectedItems[position, false]) {
            holder.iconFront.visibility = View.GONE
            resetIconYAxis(holder.iconBack)
            holder.iconBack.visibility = View.VISIBLE
            holder.iconBack.alpha = 1f
            if (currentSelectedIndex == position) {
                flipView(mContext, holder.iconBack, holder.iconFront, true)
                resetCurrentIndex()
            }
        } else {
            holder.iconBack.visibility = View.GONE
            resetIconYAxis(holder.iconFront)
            holder.iconFront.visibility = View.VISIBLE
            holder.iconFront.alpha = 1f
            if (reverseAllAnimations && animationItemsIndex[position, false] || currentSelectedIndex == position) {
                flipView(mContext, holder.iconBack, holder.iconFront, false)
                resetCurrentIndex()
            }
        }
    }

    fun animateTo(models: List<Message>) {
        applyAndAnimateRemovals(models)
        applyAndAnimateAdditions(models)
        applyAndAnimateMovedItems(models)
    }

    private fun applyAndAnimateRemovals(newModels: List<Message>) {
        for (i in messages.indices.reversed()) {
            val model: MessageWrapper = messages[i]
            removeItem(i)
        }
    }

    private fun applyAndAnimateAdditions(newModels: List<Message>) {
        var i = 0
        val count = newModels.size
        while (i < count) {
            val model: Message = newModels[i]
            if (!messages.contains(MessageWrapper(model))) {
                addItem(i, model)
            }
            i++
        }
    }

    private fun applyAndAnimateMovedItems(newModels: List<Message>) {
        for (toPosition in newModels.indices.reversed()) {
            val model = MessageWrapper(newModels[toPosition])
            val fromPosition = messages.indexOf(model)
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition)
            }
        }
    }

    private fun removeItem(position: Int) {
        messages.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun addItem(position: Int, model: Message) {
        messages.add(position, MessageWrapper(model))
        notifyItemInserted(position)
    }

    private fun moveItem(fromPosition: Int, toPosition: Int) {
        val model = messages.removeAt(fromPosition)
        messages.add(toPosition, model)
        notifyItemMoved(fromPosition, toPosition)
    }

    private fun resetIconYAxis(view: View?) {
        if (view!!.rotationY != 0f) {
            view.rotationY = 0f
        }
    }

    fun resetAnimationIndex() {
        reverseAllAnimations = false
        animationItemsIndex.clear()
    }

    override fun getItemId(position: Int): Long {
        return messages[position].id.toLong()
    }

    private fun applyImportant(holder: MyViewHolder, message: MessageWrapper) {
        if (message.isImportant) {
            holder.iconImp.setImageDrawable(
                ContextCompat.getDrawable(
                    mContext,
                    R.drawable.ic_star_black_24dp
                )
            )
            holder.iconImp.setColorFilter(
                ContextCompat.getColor(
                    mContext,
                    R.color.icon_tint_selected
                )
            )
        } else {
            holder.iconImp.setImageDrawable(
                ContextCompat.getDrawable(
                    mContext,
                    R.drawable.ic_star_border_black_24dp
                )
            )
            holder.iconImp.setColorFilter(
                ContextCompat.getColor(
                    mContext,
                    R.color.icon_tint_normal
                )
            )
        }
    }

    private fun applyReadStatus(holder: MyViewHolder, message: MessageWrapper) {
        if (message.isLeido) {
            holder.lblRemitente.setTypeface(null, Typeface.NORMAL)
            holder.lblPrimary.setTypeface(null, Typeface.NORMAL)
            holder.lblFecha.setTypeface(null, Typeface.NORMAL)
            holder.lblRemitente.setTextColor(ContextCompat.getColor(mContext, R.color.message))
            holder.lblPrimary.setTextColor(ContextCompat.getColor(mContext, R.color.message))
            holder.lblFecha.setTextColor(ContextCompat.getColor(mContext, R.color.message))
        } else {
            holder.lblRemitente.setTypeface(null, Typeface.BOLD)
            holder.lblPrimary.setTypeface(null, Typeface.BOLD)
            holder.lblFecha.setTypeface(null, Typeface.BOLD)
            holder.lblRemitente.setTextColor(ContextCompat.getColor(mContext, R.color.from))
            holder.lblPrimary.setTextColor(ContextCompat.getColor(mContext, R.color.subject))
            holder.lblFecha.setTextColor(ContextCompat.getColor(mContext, R.color.subject))
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun toggleSelection(pos: Int) {
        currentSelectedIndex = pos
        if (selectedItems[pos, false]) {
            selectedItems.delete(pos)
            animationItemsIndex.delete(pos)
        } else {
            selectedItems.put(pos, true)
            animationItemsIndex.put(pos, true)
        }
        notifyItemChanged(pos)
    }

    fun clearSelections() {
        reverseAllAnimations = true
        selectedItems.clear()
        notifyDataSetChanged()
    }

    val selectedItemCount: Int
        get() = selectedItems.size()

    fun getSelectedItems(): List<Int> {
        val items: MutableList<Int> = ArrayList(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    fun removeData(position: Int) {
        messages.removeAt(position)
        resetCurrentIndex()
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }

    interface MessageAdapterListener {
        fun onIconClicked(position: Int)
        fun onIconImportantClicked(position: Int)
        fun onMessageRowClicked(position: Int)
        fun onRowLongClicked(position: Int)
    }

    companion object {
        private var currentSelectedIndex = -1
    }

    init {
        this.messages = ArrayList()
        messages.sortWith(Comparator { s1, s2 ->
            stringToDate(s2.fecha, s2.hora).compareTo(
                stringToDate(s1.fecha, s1.hora)
            )
        })
        for (message in messages) {
            this.messages.add(MessageWrapper(message))
        }
        this.listener = listener
        selectedItems = SparseBooleanArray()
        animationItemsIndex = SparseBooleanArray()
    }
}