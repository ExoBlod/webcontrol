package com.webcontrol.android.ui.messages

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.util.keyIterator
import androidx.core.view.MenuHost
import androidx.core.view.MenuItemCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.db.AppDataBase
import com.webcontrol.android.data.db.entity.Message
import com.webcontrol.android.data.db.entity.MessageHistory
import com.webcontrol.android.data.network.ApiResponse
import com.webcontrol.android.databinding.FragmentMensajesBinding
import com.webcontrol.android.ui.MainActivity
import com.webcontrol.android.ui.common.adapters.MessageAdapter
import com.webcontrol.android.ui.common.adapters.MessageAdapter.MessageAdapterListener
import com.webcontrol.android.util.RestClient.buildL
import com.webcontrol.android.util.SharedUtils.getRandomMaterialColor
import com.webcontrol.android.util.SharedUtils.getUsuarioId
import com.webcontrol.android.util.SharedUtils.isOnline
import com.webcontrol.android.util.SharedUtils.showMessage
import com.webcontrol.android.util.SharedUtils.showToast
import com.webcontrol.android.workers.SyncMessageState
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal
import java.util.*

@AndroidEntryPoint
class MensajesFragment : Fragment(), SearchView.OnQueryTextListener, MessageAdapterListener,
    IOnBackPressed {
    private var IdEmpresa: String? = ""
    private lateinit var binding: FragmentMensajesBinding
    var messageListState: Parcelable? = null
    var mSearchViewQuery: String? = null
    private var actionMode: ActionMode? = null
    private var actionModeCallback: ActionModeCallback? = null
    private var adapter: MessageAdapter? = null
    private var messageList: List<Message>? = null
    private var messageListFilter: List<Message>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private val selectedMessages: List<Message>
    private var mHasInstanceState = false
    private var mMessageLiveData: LiveData<List<Message>>? = null
    var animation: LayoutAnimationController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Inbox"
    }

    private val onNewMessage: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            reload()
        }
    }
    private val observer: Observer<List<Message>> = Observer<List<Message>> { messages ->
        binding.messageView.visibility = View.GONE
        messageList = messages
        messageListFilter = messageList
        adapter!!.setData(messageList!!.toMutableList())
        adapter!!.notifyDataSetChanged()
        if (messageList!!.isEmpty()) {
            showMessage(binding.messageView, R.drawable.ic_message, "No se encontraron registros")
        }
    }
    private var TAG: String = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMensajesBinding.inflate(inflater, container, false)
        TAG = MensajesFragment::class.java.simpleName
        if (arguments != null) {
            IdEmpresa = requireArguments().getString(ARG_EMPRESA)
        }
        mHasInstanceState = false
        layoutManager = LinearLayoutManager(getContext())
        binding.rvClientes.layoutManager = layoutManager
        animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.slide_out)
        binding.rvClientes.itemAnimator = DefaultItemAnimator()
        val divider = DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider))
        binding.rvClientes.addItemDecoration(divider)
        binding.rvClientes.adapter = null
        actionModeCallback = ActionModeCallback()
        messageList = ArrayList()
        setHasOptionsMenu(true)
        binding.loader.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorAccent,
            R.color.colorPrimaryDark
        )
        binding.loader.setOnRefreshListener {
            fetch()
            binding.rvClientes.layoutAnimation = animation
        }
        val filter = IntentFilter("com.webcontrol.webcontrolop.NEW_MESSAGE")
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(onNewMessage, filter)
        mMessageLiveData = if (IdEmpresa.equals("TODAS", ignoreCase = true)) App.db.messageDao()
            .getAllVisible(getUsuarioId(getContext())) else App.db.messageDao()
            .getAllByEmpresa(getUsuarioId(getContext()), IdEmpresa)
        adapter =
            MessageAdapter(requireContext(), messageList!!.toMutableList(), this@MensajesFragment)
        binding.rvClientes.adapter = adapter
        if (savedInstanceState != null) {
            mHasInstanceState = true
            messageListState = savedInstanceState.getParcelable(MESSAGE_LIST_STATE)
            adapter!!.setData(messageList!!.toMutableList())
            fetch()
            if (messageList!!.isEmpty()) {
                showMessage(binding.messageView, R.drawable.ic_message, "No se encontraron registros")
            }
        } else {
            fetch()
        }
        binding.rvClientes.layoutAnimation = animation
        mMessageLiveData!!.observe(viewLifecycleOwner, observer)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMenuProvider()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (messageListState!=null){
            messageListState = layoutManager!!.onSaveInstanceState()
        }
        outState.putParcelable(MESSAGE_LIST_STATE, messageListState)
    }

    override fun onResume() {
        super.onResume()
        if (actionMode != null) actionMode!!.finish()
        mHasInstanceState = false
        if (messageListState != null) layoutManager!!.onRestoreInstanceState(messageListState)
        if (false) {
            MaterialTapTargetPrompt.Builder(requireActivity())
                .setTarget(binding.rvClientes.getChildAt(0))
                .setPrimaryText("Visualiza tus mensajes")
                .setSecondaryText("Presiona sobre el mensaje que deseas leer para abrirlo")
                .setPromptFocal(RectanglePromptFocal())
                .setBackgroundColour(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                .setPromptStateChangeListener { prompt, state ->
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                        //todo set pref to true
                    }
                }
                .show()
        }
        if (false) {
            MaterialTapTargetPrompt.Builder(requireActivity())
                .setTarget(R.id.action_search)
                .setIcon(R.drawable.ic_search_black)
                .setPrimaryText("Filtra mensajes")
                .setSecondaryText("Busca un mensaje de la lista")
                .setBackgroundColour(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                .setPromptStateChangeListener { prompt, state ->
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                    }
                }
                .show()
        }
    }

    fun reload() {
        if (adapter != null) adapter!!.notifyDataSetChanged()
    }

    fun fetch() {
        if (isOnline(requireContext())) sync() else Toast.makeText(
            getContext(),
            "Conectese a internet para actualizar sus mensajes",
            Toast.LENGTH_LONG
        )
    }

    fun sync() {
        binding.loader.isRefreshing = true
        val syncEstados = OneTimeWorkRequest.Builder(SyncMessageState::class.java).build()
        WorkManager.getInstance().enqueue(syncEstados)
        WorkManager.getInstance().getWorkInfoByIdLiveData(syncEstados.id)
            .observe(viewLifecycleOwner, Observer { workInfo: WorkInfo? ->
                if (workInfo != null && workInfo.state.isFinished) {
                    val resultado = workInfo.outputData.getString("resultado")
                    if (resultado != null && resultado == "ok") {
                        val idSync = App.db.messageDao().getMaxIdSync(getUsuarioId(getContext()))
                        val api = buildL()
                        val call = api.getMessages(getUsuarioId(getContext()), idSync)
                        call.enqueue(object : Callback<ApiResponse<List<Message>>?> {
                            override fun onResponse(
                                call: Call<ApiResponse<List<Message>>?>,
                                response: Response<ApiResponse<List<Message>>?>
                            ) {
                                binding.loader.isRefreshing = false
                                if (response.isSuccessful) {
                                    val result = response.body()
                                    if (result != null && result.isSuccess) {
                                        for (i in result.data.indices) {
                                            val msg = result.data[i]
                                            val exist =
                                                App.db.messageDao().getOne(msg.id.toString())
                                            if (exist != null) App.db.messageDao().updateMessage(
                                                msg.id,
                                                msg.estado,
                                                msg.isImportant,
                                                msg.idSync
                                            ) else {
                                                msg.color =
                                                    getRandomMaterialColor(requireContext(), "400")
                                                App.db.messageDao().insert(msg)
                                            }
                                        }
                                        if (messageList != null && messageList!!.isEmpty()) {
                                            showMessage(
                                                binding.messageView,
                                                R.drawable.ic_message,
                                                "No se encontraron registros"
                                            )
                                        }
                                    }
                                } else {
                                    showToast(requireContext(), response.message())
                                }
                            }

                            override fun onFailure(
                                call: Call<ApiResponse<List<Message>>?>,
                                t: Throwable
                            ) {
                                binding.loader.isRefreshing = false
                                showToast(requireContext(), TAG + "sync()" + t.message)
                            }
                        })
                    } else {
                        binding.loader.isRefreshing = false
                        showToast(requireContext(), "Error al sincronizar, intente nuevamente")
                    }
                }
            })
    }

    private fun setMenuProvider() {
        val menuHost = requireActivity() as MenuHost

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                if (adapter != null && adapter!!.selectedItems.size() > 0) {
                    menuInflater.inflate(R.menu.selected_message_menu, menu)
                    return
                } else {
                    menuInflater.inflate(R.menu.menu_search, menu)
                    val item = menu.findItem(R.id.action_search)
                    val searchView = MenuItemCompat.getActionView(item) as SearchView
                    searchView.queryHint = "Buscar"
                    searchView.setOnQueryTextListener(this@MensajesFragment)
                    if (mSearchViewQuery != null && mSearchViewQuery!!.isNotEmpty()) {
                        searchView.setQuery(mSearchViewQuery, false)
                        searchView.isIconified = false
                        searchView.clearFocus()
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val id = menuItem.itemId
                if (id == R.id.action_search) {
                    Toast.makeText(
                        requireActivity().applicationContext,
                        "Search...",
                        Toast.LENGTH_SHORT
                    ).show()
                    return true
                } else if (id == R.id.action_important) {
                    if (menuItem.title == "Filtrar") {
                        menuItem.setIcon(R.drawable.ic_star_full_yellow)
                        menuItem.title = ""
                        val filteredModelList = filter(messageList, "", 2)
                        messageListFilter = filteredModelList
                        adapter!!.animateTo(filteredModelList)
                        binding.rvClientes.scrollToPosition(0)
                    } else {
                        menuItem.setIcon(R.drawable.ic_star_full_white)
                        menuItem.title = "Filtrar"
                        val filteredModelList = filter(messageList, "", 1)
                        messageListFilter = filteredModelList
                        adapter!!.animateTo(filteredModelList)
                        binding.rvClientes.scrollToPosition(0)
                    }
                    return true
                }
                return false
            }
        }, viewLifecycleOwner)
    }

    override fun onQueryTextChange(query: String): Boolean {
        val filteredModelList = filter(messageList, query, 1)
        messageListFilter = filteredModelList
        mSearchViewQuery = query
        adapter!!.animateTo(filteredModelList)
        binding.rvClientes.scrollToPosition(0)
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    private fun filter(models: List<Message>?, query: String, filter: Int): List<Message> {
        var query = query
        query = query.toLowerCase()
        val filteredModelList: MutableList<Message> = ArrayList()
        when (filter) {
            1 -> for (model in models!!) {
                var text = ""
                text += model.rut
                text += model.fecha
                text += model.mensaje!!.toLowerCase()
                if (text.contains(query)) {
                    filteredModelList.add(model)
                }
            }

            2 -> for (model in models!!) {
                if (model.isImportant) {
                    filteredModelList.add(model)
                }
            }

            else -> {
            }
        }
        return filteredModelList
    }

    private fun deleteMessages() {
        adapter!!.resetAnimationIndex()
        val selectedItemPositions = adapter!!.selectedItems
        for (i in selectedItemPositions.keyIterator()) {
            val message = App.db.messageDao().getOne("" + messageList!![i].id)
            if (message != null) {
                deleteMessage(message)
                //adapter!!.removeData(i)
            } else {
                showToast(requireContext(), "No se ha podido eliminar este mensaje")
            }
        }
        adapter!!.notifyDataSetChanged()
    }

    private fun deleteMessage(message: Message) {
        val api = buildL()
        val call: Call<ApiResponse<Any>> = api.messageDeleted(message)
        message.estado = 3
        val messageHistory = MessageHistory()
        messageHistory.actionId = 1
        messageHistory.messageId = message.id
        AppDataBase.getInstance(activity).messageHistoryDao().insert(messageHistory)
        call.enqueue(object : Callback<ApiResponse<Any>?> {
            override fun onResponse(
                call: Call<ApiResponse<Any>?>,
                response: Response<ApiResponse<Any>?>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        AppDataBase.getInstance(activity).messageDao()
                            .updateEstadoSync(message.id, true)
                    }
                } else {
                    showToast(requireContext(), response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>?>, t: Throwable) {
                showToast(requireContext(), TAG + " deleteMessage() " + t.message)
            }
        })
        AppDataBase
            .getInstance(activity)
            .messageDao()
            .update(message)
        AppDataBase.getInstance(activity).messageDao().updateEstadoSync(message.id, false)
    }

    override fun onIconClicked(position: Int) {
        if (actionMode == null) {
            actionMode = requireActivity().startActionMode(actionModeCallback)
        }
        toggleSelection(position)
    }

    override fun onIconImportantClicked(position: Int) {
        val message = messageList!![position]
        message.isImportant = !message.isImportant
        AppDataBase.getInstance(activity).messageDao().update(message)
        AppDataBase.getInstance(activity).messageDao().updateImportanteSync(message.id, false)
        // todo fix next line
        //messageList!![position] = message
        updateMessageImportantOnServer(message)
    }

    private fun updateMessageImportantOnServer(message: Message) {
        val api = buildL()
        val call: Call<ApiResponse<Any>> = api.messageImportant(message)
        call.enqueue(object : Callback<ApiResponse<Any>?> {
            override fun onResponse(
                call: Call<ApiResponse<Any>?>,
                response: Response<ApiResponse<Any>?>
            ) {
                App.db.messageDao().updateImportanteSync(message.id, true)
            }

            override fun onFailure(call: Call<ApiResponse<Any>?>, t: Throwable) {
                showToast(requireContext(), TAG + " updateMessageImportantOnServer() " + t.message)
            }
        })
    }

    override fun onMessageRowClicked(position: Int) {
        if (adapter!!.selectedItemCount > 0) {
            enableActionMode(position)
        } else {
            val intent = Intent(context, DetalleActivity::class.java)
            intent.putExtra("MESSAGE_ID", messageListFilter!![position].id.toString())
            startActivity(intent)
            adapter!!.notifyItemChanged(position)
        }
    }

    override fun onRowLongClicked(position: Int) {
        enableActionMode(position)
    }

    private fun enableActionMode(position: Int) {
        if (actionMode == null) {
            actionMode = requireActivity().startActionMode(actionModeCallback)
        }
        toggleSelection(position)
    }

    private fun toggleSelection(position: Int) {
        adapter!!.toggleSelection(position)
        val count = adapter!!.selectedItemCount
        if (count == 0) {
            actionMode!!.finish()
        } else {
            actionMode!!.title = count.toString()
            actionMode!!.subtitle = "seleccionados"
            actionMode!!.invalidate()
        }
    }

    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)
            binding.loader!!.isEnabled = false
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_delete -> {
                    deleteMessages()
                    mode.finish()
                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            adapter!!.clearSelections()
            binding.loader!!.isEnabled = true
            actionMode = null
            binding.rvClientes.post { adapter!!.resetAnimationIndex() }
        }
    }

    override fun onBackPressed(): Boolean {
        (activity as MainActivity?)!!.confirmSessionAbandon()
        return false
    }

    companion object {
        const val ARG_EMPRESA = "empresa"
        private const val MESSAGE_LIST_STATE = "message_list_state"

        /** */
        fun newInstance(): MensajesFragment {
            return MensajesFragment()
        }

        fun newInstance(param: String?): MensajesFragment {
            val fragment = MensajesFragment()
            val args = Bundle()
            args.putString(ARG_EMPRESA, param)
            fragment.arguments = args
            return fragment
        }
    }

    /** */
    init {
        selectedMessages = ArrayList()
    }
}