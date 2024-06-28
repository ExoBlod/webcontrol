package com.webcontrol.android.ui

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageInfo
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.text.Html
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.iid.FirebaseInstanceId
import com.webcontrol.android.App
import com.webcontrol.android.R
import com.webcontrol.android.data.IOnBackPressed
import com.webcontrol.android.data.db.entity.Message
import com.webcontrol.android.data.model.*
import com.webcontrol.android.data.model.sgscm.AuthenticateRequest
import com.webcontrol.android.data.model.sgscm.WorkerSgscmResponse
import com.webcontrol.android.data.network.*
import com.webcontrol.android.databinding.ActivityMainBinding
import com.webcontrol.android.ui.base.UpdateActivity
import com.webcontrol.android.ui.checklist.HistoricoCheckListFragment
import com.webcontrol.android.ui.covid.declaracion.MainDJFragment
import com.webcontrol.android.ui.menu.*
import com.webcontrol.android.ui.messages.MensajesFragment
import com.webcontrol.android.ui.newchecklist.NewCheckListActivity
import com.webcontrol.android.ui.onboarding.LauncherActivity
import com.webcontrol.android.ui.videocall.VideoCallActivity
import com.webcontrol.android.util.Companies
import com.webcontrol.android.util.ConstantNameMenu.APPROVE_MOVEMENTS
import com.webcontrol.android.util.ConstantNameMenu.BOOK_COURSES
import com.webcontrol.android.util.ConstantNameMenu.BUNDLE_FILTER_INSPECTOR
import com.webcontrol.android.util.ConstantNameMenu.BUNDLE_NAME_INSPECTOR
import com.webcontrol.android.util.ConstantNameMenu.BUNDLE_SEARCH_INSPECTOR
import com.webcontrol.android.util.ConstantNameMenu.BUNDLE_SIGNATURE
import com.webcontrol.android.util.ConstantNameMenu.BUNDLE_TO_HISTORY
import com.webcontrol.android.util.ConstantNameMenu.BUS_RESERVATION
import com.webcontrol.android.util.ConstantNameMenu.PRE_USO_CHECKLIST
import com.webcontrol.android.util.ConstantNameMenu.PRE_ACCESS_MINE
import com.webcontrol.android.util.ConstantNameMenu.QUERY_INSPECTION
import com.webcontrol.android.util.ConstantNameMenu.QUERY_INSPECTION_PRE_USO
import com.webcontrol.android.util.ConstantNameMenu.VEHICULAR_INSPECTION
import com.webcontrol.android.util.Constants.TOKEN_AUTHORIZATION_PHC
import com.webcontrol.android.util.Constants.TOKEN_BAMBAS
import com.webcontrol.android.util.Constants.TOKEN_COLLAHUASI
import com.webcontrol.android.util.Constants.TOKEN_PODEROSA
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.RestClient.buildCaserones
import com.webcontrol.android.util.RestClient.buildCdl
import com.webcontrol.android.util.RestClient.buildGf
import com.webcontrol.android.util.RestClient.buildKinross
import com.webcontrol.android.workers.AlarmReceiver
import com.webcontrol.android.workers.LocationService
import com.webcontrol.angloamerican.data.TOKEN_ANGLO
import com.webcontrol.angloamerican.ui.approvemovements.ui.ApproveMovementsActivity
import com.webcontrol.angloamerican.ui.bookcourses.BookCoursesActivity
import com.webcontrol.angloamerican.ui.preaccessmine.PreAccessMineActivity
import com.webcontrol.angloamerican.ui.checklistpreuso.CheckListPreUsoActivity
import com.webcontrol.angloamerican.ui.checklistpreuso.CheckListPreUsoViewModel
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.ApiResponseSearchAnglo
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.WorkerRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.WorkerResponse
import com.webcontrol.core.common.model.Error
import com.webcontrol.core.common.model.Success
import com.webcontrol.core.utils.LocalStorage
import com.webcontrol.core.utils.SharedUtils
import com.webcontrol.pucobre.data.TOKEN_PUCOBRE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.view.imgUser
import kotlinx.android.synthetic.main.activity_main.view.lblInfoUser
import kotlinx.android.synthetic.main.activity_main.view.lblInfoVersion
import kotlinx.android.synthetic.main.activity_main.view.loaderMain
import kotlinx.android.synthetic.main.nav_submenu_item.count
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

@AndroidEntryPoint
class MainActivity : UpdateActivity(), NavMenuAdapter.MenuItemClickListener {
    private lateinit var binding: ActivityMainBinding
    lateinit var toolbar: Toolbar
    internal var drawer: DrawerLayout? = null
    internal var menu: ArrayList<NavMenuModel>? = null
    private val drawableResId = try {
        R.drawable.ic_inbox_black_24dp
    } catch (e: Resources.NotFoundException) {
        R.drawable.ic_android_white_192dp
    }
    private var packageInfo: PackageInfo? = null

    private var navigationView: NavigationView? = null
    private var lblTitle: TextView? = null
    private var lblSubtitle: TextView? = null
    private var imgUser: ImageView? = null
    lateinit var navMenuDrawer: RecyclerView
    lateinit var adapter: NavMenuAdapter

    lateinit var loaderMain: SwipeRefreshLayout

    private val viewModel by viewModel<MainViewModel>()
    private val checkListPreUsoViewModel: CheckListPreUsoViewModel by viewModels()
    private val localStorage by inject<LocalStorage>()
    private val angloSecurityViewModel by viewModels<com.webcontrol.angloamerican.ui.security.SecurityViewModel>()
    private val pucobreSecurityViewModel by viewModels<com.webcontrol.pucobre.ui.security.SecurityViewModel>()

    private var mainNavMenuList = ArrayList<NavMenuModel>()
    private var mainMenuList = ArrayList<TitleMenu>()

    private var TAG: String? = null

    lateinit var locationManager: LocationManager
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    var locationService: LocationService? = null
    private var boundToLocationService = false

    private var listMenuBambas = ArrayList<SubMenuModel>()

    private var isAuthCapacitaciones = false
    private var isAuthLector = false
    private var apiResponseSearchBambas: ApiResponseSearchBambas? = null
    private var workerResponse: WorkerResponse? = null
    private var apiResponseSearchAnglo: ApiResponseSearchAnglo? = null
    private val driverAnglo : String = "SI"
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            boundToLocationService = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
            boundToLocationService = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)
        setToolbar()

        drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer!!.addDrawerListener(toggle)
        toggle.syncState()

        navigationView = binding.navView

        lblTitle = binding.navView.lblInfoUser
        lblSubtitle = binding.navView.lblInfoVersion
        imgUser = binding.navView.imgUser
        loaderMain = binding.navView.loaderMain

        navMenuDrawer = binding.mainNavMenuRecyclerview

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (!granted) {
                    Toast.makeText(
                        this,
                        getString(R.string.text_dialog_location_denied),
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    locationService?.requestLocationUpdates()
                }
            }

        try {
            var urlPhoto = "%suser/%s/foto"
            urlPhoto = String.format(
                urlPhoto,
                getString(R.string.ws_url_mensajeria),
                SharedUtils.getUsuarioId(this)
            )
            Glide.with(this)
                .load(urlPhoto)
                .placeholder(R.drawable.ic_account_circle_materialgrey_240dp)
                .error(R.drawable.ic_account_circle_materialgrey_240dp)
                .circleCrop()
                .into(imgUser!!)

            packageInfo = packageManager.getPackageInfo(packageName, 0)
            lblTitle!!.text = SharedUtils.getUsuario(this)
            lblSubtitle!!.text = packageInfo!!.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@MainActivity) { instanceIdResult ->
            val newToken = instanceIdResult.token
            Log.e("newToken", newToken)
        }

        if (SharedUtils.isOnline(applicationContext)) {
            syncMessages()
        } else {
            setNavigationDrawerMenu()
        }

        syncTokenAuthorization()
        syncTokenAuthorizationMc()

        loaderMain.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorAccent,
            R.color.colorPrimaryDark
        )
        loaderMain.setOnRefreshListener {
            //loadMenusPHC()
            loadMenuPHC()
            loadMenusAnglo()
            loadMenuAnglo()
            loadMenuKs()
            loadMenuYamana()
            loadMenuCaserones()
            loadMenuAnta()
            //loadMenuCDL()
            //loadMenuMC()
            loadMenuEA()
            loadMenuKinross()
            loadMenuBarrick()
            loadMenuGf()
            loadMenuSgscm()
            loadMenuCollahuasi()
            loadMenuBambas()
            loadMenuPoderosa()
            loadMenuPucobre()
        }
        TAG = MainActivity::class.java.simpleName
        scheduleAlarm()
        setupObservers()
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }

    private fun setupObservers() {
        observeTokenPHC()
        observeTokenBambas()
        observeTokenPoderosa()
        observeBambasSearchUser()
    }

    override fun onStart() {
        super.onStart()

        bindService(
            Intent(this, LocationService::class.java), serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        if (boundToLocationService) {
            unbindService(serviceConnection)
            boundToLocationService = false
        }

        super.onStop()
    }

    private fun scheduleAlarm() {
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        val pIntent: PendingIntent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pIntent = PendingIntent.getBroadcast(
                this, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            pIntent = PendingIntent.getBroadcast(
                this, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val firstMillis = System.currentTimeMillis()
        val alarm = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, firstMillis,
            120000, pIntent
        )
    }

    private fun setToolbar() {
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
    }

    private fun updateDrawerMenu() {
        reorderMenu()
        mainMenuList = getMenuList(mainNavMenuList)
        adapter = NavMenuAdapter(applicationContext, mainMenuList, this@MainActivity)
        navMenuDrawer.adapter = adapter
    }

    private fun reorderMenu() {
        mainNavMenuList = ArrayList(mainNavMenuList.sortedWith(compareBy { it.index }))
    }

    private fun setNavigationDrawerMenu() {
        loadFixedMenuItems()
        navMenuDrawer.layoutManager = LinearLayoutManager(this)
        adapter.selectedItemParent = menu!![0].menuTitle
        onMenuItemClick(adapter.selectedItemParent)

        observeTokenAnta()
        observeMenuAnta()
        workerBarrick()
        observeWorkerBarrick()
        observeWorkerPucobre()
        observeMenuBarrick()
        observeMenuCollahuasi()
        observeMenuAnglo()
        loadMenuAnglo()
        observeMenuPucobre()
        //loadMenusPHC()
        loadMenuPHC()
        loadMenusAnglo()
        //loadMenuGMP()
        loadMenuKs()
        loadMenuYamana()
        loadMenuCaserones()
        loadMenuAnta()
        //loadMenuCDL()
        //loadMenuMC()
        loadMenuEA()
        loadMenuKinross()
        loadMenuGf()
        loadMenuBarrick()
        loadMenuSgscm()
        loadMenuCollahuasi()
        loadMenuBambas()
        loadMenuPoderosa()
        loadMenuPucobre()
    }

    private fun loadMenuCollahuasi() {
        viewModel.getWorkerCollahuasi(SharedUtils.getUsuarioId(this))
    }

    private fun observeMenuCollahuasi() {
        viewModel.tokenCollahuasi.observe(this) {
            if (it != null) {
                localStorage[TOKEN_COLLAHUASI] = it
                viewModel.getWorkerCollahuasi(SharedUtils.getUsuarioId(this))
            }
        }
        viewModel.workerCollahuasi.observe(this) {
            if (it != null) {
                val listMenuCollahuasi = ArrayList<SubMenuModel>()
                listMenuCollahuasi.add(
                    SubMenuModel(
                        "Pre-Acceso",
                        destination = Destination(
                            R.id.controlPreaccesoFragment, bundleOf(
                                "CLIENT" to Companies.CH.valor,
                                "title" to "Pre-Acceso"
                            )
                        )

                    )
                )
                updateMenuCollahuasi(listMenuCollahuasi)
            }
        }
    }

    private fun loadMenuBarrick() {
        viewModel.getTokenBarrick(SharedUtils.getUsuarioId(this))

    }

    private fun workerBarrick() {
        viewModel.getWorkerBarrick(SharedUtils.getUsuarioId(this))
    }

    private fun observeWorkerBarrick() {
        viewModel.checkWorkerBarrick().observe(this) {
            isAuthCapacitaciones = it.isAuthCapacitacion
        }
    }

    private fun observeMenuBarrick() {
        viewModel.checkBarrickActive().observe(this) {
            if (it) {
                val listMenuBarrick = ArrayList<SubMenuModel>()
                listMenuBarrick.add(
                    SubMenuModel(
                        "Asistencia", destination = Destination(
                            R.id.attendanceHostFragment,
                            bundleOf("title" to "Asistencia")
                        )
                    )
                )
                listMenuBarrick.add(
                    SubMenuModel(
                        "Encuesta COVID19",
                        destination = Destination(
                            R.id.historicoCheckListFragment,
                            bundleOf(
                                HistoricoCheckListFragment.CHECKLIST_TYPE to "COV",
                                HistoricoCheckListFragment.CHECKLIST_NAME to "Encuesta COVID19",
                                HistoricoCheckListFragment.COMPANY_ID to Companies.BR.valor
                            )
                        )
                    )
                )
                listMenuBarrick.add(
                    SubMenuModel(
                        "Credenciales Barrick",
                        destination = Destination(
                            R.id.barrickCredentialFragment,
                            bundleOf(
                                "title" to "Credenciales Barrick",
                                "dialogShown" to false
                            )
                        )
                    )
                )
                if (isAuthCapacitaciones) {
                    listMenuBarrick.add(
                        SubMenuModel(
                            "Capacitaciones",
                            destination = Destination(
                                R.id.lectorQrManualFragment,
                                bundleOf("title" to "Capacitaciones",
                                                "argsCliente" to "Barrick")
                            )
                        )
                    )
                }
                updateMenuBarrick(listMenuBarrick)
            }
        }
    }

    private fun loadMenuAnta() {
        viewModel.getWorkerAnta(SharedUtils.getUsuarioId(this))
    }

    private fun observeTokenAnta() {
        viewModel.checkAntaToken().observe(this) {
            if (it != null) {
                if (it.isNotEmpty()) {
                    SharedUtils.setTokenAnta(this, it)
                    viewModel.getWorkerAnta(SharedUtils.getUsuarioId(this))
                }
            }
        }
    }

    private fun observeMenuAnta() {
        viewModel.checkWorkerAnta().observe(this, Observer {
            if (it != null) {
                SharedUtils.setAntaCompany(this, it.empresa)

                val listMenuAnta = ArrayList<SubMenuModel>()
                listMenuAnta.add(
                    SubMenuModel(
                        "Datos  Iniciales ",
                        destination = Destination(R.id.datosInicialesFragmentAntapaccay)
                    )
                )
                updateMenuAnta(listMenuAnta)
                getAntaWorkerControlInicial(listMenuAnta)
            }
        })
    }

    private fun loadMenuGMP() {
        observeMenuGmp()
        viewModel.getWorkerGMP(SharedUtils.getUsuarioId(this))
    }

    /*private fun loadMenuCDL() {
        getTelefonoCovCDL()
        observeMenuCDL()
        viewModel.getWorkerCDL(SharedUtils.getUsuarioId(this))
    }*/

    private fun loadMenuMC() {
        observeMenuMc()
        viewModel.getWorkerMc(SharedUtils.getUsuarioId(this))
    }

    private fun loadFixedMenuItems() {
        val empresas =
            App.db.messageDao().getDistinctEmpresas(SharedUtils.getUsuarioId(applicationContext))
        mainNavMenuList.add(
            NavMenuModel(0, "Mensajes", drawableResId,
                object : ArrayList<SubMenuModel>() {
                    init {
                        if (empresas != null) {
                            var total = 0
                            for (i in empresas.indices) {
                                add(
                                    SubMenuModel(
                                        empresas[i].nomEmpresa ?: "",
                                        count = empresas[i].numRegistros,
                                        destination = Destination(
                                            R.id.mensajesFragment,
                                            bundleOf(MensajesFragment.ARG_EMPRESA to empresas[i].rutEmpresa)
                                        )
                                    )
                                )
                                total += empresas[i].numRegistros
                            }
                            add(
                                0,
                                SubMenuModel(
                                    "Todos",
                                    count = total,
                                    destination = Destination(
                                        R.id.mensajesFragment,
                                        bundleOf(MensajesFragment.ARG_EMPRESA to "TODAS")
                                    )
                                )
                            )
                        }
                    }
                })
        )
        mainNavMenuList.add(
            NavMenuModel(
                1,
                "Exámenes",
                R.drawable.ic_exam,
                destination = Destination(R.id.examenesFragment)
            )
        )
        mainNavMenuList.add(
            NavMenuModel(7, "Utilitarios", R.drawable.ic_event_black_24dp,
                object : ArrayList<SubMenuModel>() {
                    init {
                        add(
                            0,
                            SubMenuModel(
                                "Lector QR",
                                destination = Destination(
                                    R.id.lectorQrFragment, bundleOf(
                                        "vista" to "DEFAULT",
                                        "atras" to false,
                                        "title" to "Lector QR"
                                    )
                                )
                            )
                        )
                    }
                })
        )
        mainNavMenuList.add(
            NavMenuModel(
                8,
                getString(R.string.own_document),
                R.drawable.ic_doc_own,
                destination = Destination(R.id.ownDocFragment)
            )
        )
        mainNavMenuList.add(
            NavMenuModel(
                9,
                "Ajustes",
                R.drawable.ic_settings,
                destination = Destination(R.id.settingsFragment2)
            )
        )
        mainNavMenuList.add(
            NavMenuModel(
                10,
                "Salir",
                R.drawable.ic_exit_to_app,
                fragment = Fragment()
            )
        )

        updateDrawerMenu()

    }

    private fun loadMenuKs() {
        getWorkerKS()
    }

    private fun getWorkerKS() {
        try {
            val api = RestClient.buildKs()
            val call = api.getWorker(SharedUtils.getUsuarioId(applicationContext))
            call.enqueue(object : Callback<WorkerSearchResultKs> {
                override fun onResponse(
                    call: Call<WorkerSearchResultKs>,
                    response: Response<WorkerSearchResultKs>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null) {
                            addMenuKS()
                        }
                    } else {
                        if (response.code() == 401) {
                            val tokenKsRequest = TokenKsRequest()
                            tokenKsRequest.workerId = SharedUtils.getUsuarioId(applicationContext)
                            getTokenKS(tokenKsRequest)
                        }
                    }
                }

                override fun onFailure(call: Call<WorkerSearchResultKs>, t: Throwable) {
                    Snackbar.make(
                        navMenuDrawer,
                        "No se pudo consultar su estado de conductor",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Reintentar") { loadMenuKs() }.show()
                }
            })
        } catch (ex: Exception) {
            Log.e(TAG, "getWorkerKS error : ${ex.message}", ex)
            Snackbar.make(
                navMenuDrawer,
                "No se pudo consultar su estado de conductor",
                Snackbar.LENGTH_LONG
            )
                .setAction("Reintentar") { loadMenuKs() }.show()
        }
    }

    private fun getTokenKS(tokenKsRequest: TokenKsRequest) {
        try {
            val api = RestClient.buildKs()
            val call = api.getToken(tokenKsRequest)
            call.enqueue(object : Callback<ApiResponseKs> {
                override fun onResponse(
                    call: Call<ApiResponseKs>,
                    response: Response<ApiResponseKs>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null) {
                            if (result.token.isNotEmpty()) {
                                SharedUtils.setTokenKS(applicationContext, result.token)
                                addMenuKS()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponseKs>, t: Throwable) {
                    SharedUtils.showToast(
                        applicationContext,
                        TAG + " syncTokenAuthorization() " + t.message
                    )
                }
            })
        } catch (ex: Exception) {
            SharedUtils.showToast(
                applicationContext,
                TAG + " syncTokenAuthorization() " + ex.message
            )
        }
    }

    private fun addMenuKS() {
        val menuKs = NavMenuModel(4, "K+S", R.drawable.ic_account_card_details,
            object : ArrayList<SubMenuModel>() {
                init {
                    add(
                        0,
                        SubMenuModel(
                            "Credenciales",
                            destination = Destination(R.id.ksCredentialFragment)
                        )
                    )
                }
            })
        if (!containMenu(menuKs.menuTitle)) {
            mainNavMenuList.add(menuKs)
            updateDrawerMenu()
        }
    }

    private fun loadMenuSgscm() {
        getWorkerSgscm()
    }

    private fun getWorkerSgscm() {
        try {
            val api = RestClient.buildSgscm()
            val call = api.getWorkerSgscm(SharedUtils.getUsuarioId(applicationContext))
            call.enqueue(object : Callback<ApiResponseAnglo<WorkerSgscmResponse?>> {
                override fun onFailure(
                    call: Call<ApiResponseAnglo<WorkerSgscmResponse?>>,
                    t: Throwable
                ) {
                    Log.e(TAG, "onFailure: ${t.message}")
                }

                override fun onResponse(
                    call: Call<ApiResponseAnglo<WorkerSgscmResponse?>>,
                    response: Response<ApiResponseAnglo<WorkerSgscmResponse?>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            addMenuSgscm()
                        }
                    } else {
                        if (response.code() == 401) {
                            val authenticateRequest = AuthenticateRequest()
                            authenticateRequest.workerId =
                                SharedUtils.getUsuarioId(applicationContext)
                            getTokenSgscm(authenticateRequest)
                        }
                    }
                }

            })
        } catch (e: Exception) {
            Log.e(TAG, "getWorkerSgscm: ${e.message}")
        }
    }

    private fun getTokenSgscm(authenticateRequest: AuthenticateRequest) {
        try {
            val api = RestClient.buildSgscm()
            val call = api.getTokenSgscm(authenticateRequest)
            call.enqueue(object : Callback<ApiResponseAnglo<String>> {
                override fun onResponse(
                    call: Call<ApiResponseAnglo<String>>,
                    response: Response<ApiResponseAnglo<String>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            if (result.data.isNotEmpty()) {
                                SharedUtils.setTokenSgmc(applicationContext, result.data)
                                addMenuSgscm()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "getTokenSgscm: ${e.message}")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun addMenuSgscm() {
        val menuSgcm = NavMenuModel(4, "SierraGorda", R.drawable.ic_account_card_details,
            object : ArrayList<SubMenuModel>() {
                init {
                    add(
                        0,
                        SubMenuModel(
                            "Credenciales SGSCM",
                            destination = Destination(R.id.sgscmCredentialFragment)
                        )
                    )
                }
            })
        if (!containMenu(menuSgcm.menuTitle)) {
            mainNavMenuList.add(menuSgcm)
            updateDrawerMenu()
        }
    }

    private fun loadMenuBambas() {
        viewModel.getTokenBambas(SharedUtils.getUsuarioId(applicationContext))
    }

    private fun loadMenuPoderosa() {
        viewModel.getTokenPoderosa(SharedUtils.getUsuarioId(applicationContext))
    }

    private fun loadMenuPHC() {
        viewModel.getTokenPHC()
    }

    private fun loadMenuPucobre() {
        pucobreSecurityViewModel.getToken(SharedUtils.getUsuarioId(this))
    }

    private fun observeWorkerPucobre() {
        viewModel.checkWorkerPucobreCredential().observe(this) {
            if (it != null) {
                isAuthLector = it.workerCredentialPucobre.autorizacionLector
            }
            val menu = ArrayList<SubMenuModel>()
            menu.add(
                SubMenuModel(
                    "Credencial Personal",
                    destination = Destination(
                        R.id.pucobreCredentialFragment,
                        bundleOf("title" to "Credencial Personal")
                    )
                )
            )
            if (isAuthLector) {
                menu.add(
                    SubMenuModel(
                        "Lector QR Pucobre",
                        destination = Destination(
                            R.id.lectorQrPucobreFragment,
                            bundleOf("title" to "Lector QR credencial")
                        )
                    )
                )
            }
            updateMenuPucobre(menu)
        }
    }

    private fun observeMenuPucobre() {
        pucobreSecurityViewModel.token.observe(this) { result ->
            when (result) {
                is Success -> {
                    localStorage[TOKEN_PUCOBRE] = result.data
                    Log.d("TOKEN PUCOBRE", result.data)
                    viewModel.getWorkerPucobre(
                        com.webcontrol.android.util.SharedUtils.getUsuarioId(
                            this
                        )
                    )
                }

                is Error -> {
                    Log.e("TOKEN PUCOBRE", result.error)
                }

                else -> {}
            }
        }
    }

    private fun observeTokenBambas() {
        if (SharedUtils.isOnline(applicationContext)) {
            viewModel.tokenBambas.observe(this) {
                if (it != null) {
                    localStorage[TOKEN_BAMBAS] = it
                    viewModel.searchBambas(SharedUtils.getUsuarioId(applicationContext))
                    addMenuBambas()
                }
            }
        } else {
            viewModel.searchBambas(SharedUtils.getUsuarioId(applicationContext))
        }

    }

    private fun observeTokenPoderosa() {
        viewModel.tokenPoderosa.observe(this) {
            if (it != null) {
                localStorage[TOKEN_PODEROSA] = it
                viewModel.searchPoderosa(SharedUtils.getUsuarioId(applicationContext))
                addMenuPoderosa()
            }
        }
    }

    private fun addMenuBambas() {
        listMenuBambas = ArrayList()
        listMenuBambas.add(
            0,
            SubMenuModel(
                "Credenciales    ",
                destination = Destination(
                    R.id.bambasCredentialFragment, bundleOf(
                        "SEARCH_MODE" to false,
                        "title" to "Credenciales"
                    )
                )
            )
        )
        updateMenuBambas(listMenuBambas)
    }

    private fun addMenuPoderosa() {
        val menuPoderosa = NavMenuModel(4, "La Poderosa", R.drawable.ic_account_card_details,
            object : ArrayList<SubMenuModel>() {
                init {
                    add(
                        0,
                        SubMenuModel(
                            "Credencial La Poderosa",
                            destination = Destination(
                                R.id.poderosaCredentialFragment,
                                bundleOf(
                                    "SEARCH_MODE" to false,
                                    "title" to "Credenciales"
                                )
                            )
                        )
                    )
                }
            })
        if (!containMenu(menuPoderosa.menuTitle)) {
            mainNavMenuList.add(menuPoderosa)
            updateDrawerMenu()
        }
    }

    private fun observeBambasSearchUser() {
        if (!SharedUtils.isOnline(applicationContext)) {
            listMenuBambas.add(
                0,
                SubMenuModel(
                    VEHICULAR_INSPECTION,
                    destination = Destination(
                        R.id.bambasCredentialFragment, bundleOf(
                            "SEARCH_MODE" to true,
                            "title" to VEHICULAR_INSPECTION,
                        )
                    )
                )
            )
            updateMenuBambas(listMenuBambas)
        }
        viewModel.workerBambas.observe(this) { worker ->
            worker?.let { workerBambas ->
                if (workerBambas.autorizadoConducir == "APROBADO") {
                    apiResponseSearchBambas?.let { workerSearchBambas ->
                        listMenuBambas.add(
                            SubMenuModel(
                                VEHICULAR_INSPECTION,
                                destination = Destination(
                                    R.id.bambasCredentialFragment, bundleOf(
                                        "SEARCH_MODE" to true,
                                        "title" to VEHICULAR_INSPECTION,
                                        BUNDLE_SIGNATURE to workerSearchBambas.isSignature,
                                        BUNDLE_NAME_INSPECTOR to "${workerSearchBambas.supervisorName} ${workerSearchBambas.supervisorLastName}",
                                        BUNDLE_SEARCH_INSPECTOR to workerSearchBambas.inspectionQuery,
                                        BUNDLE_TO_HISTORY to true
                                    )
                                )
                            )
                        )
                    }
                    updateMenuBambas(listMenuBambas)
                }
            }
        }
        viewModel.workerSearchBambas.observe(this) { dataBambas ->
            dataBambas?.let {
                apiResponseSearchBambas = dataBambas
                if (dataBambas.credentialQuery) {
                    listMenuBambas.add(
                        1,
                        SubMenuModel(
                            "Búsqueda Credenciales",
                            destination = Destination(
                                R.id.bambasCredentialFragment, bundleOf(
                                    "SEARCH_MODE" to true,
                                    "title" to "Búsqueda Credenciales"
                                )
                            )
                        )
                    )

                    if (dataBambas.inspectionQuery)
                        listMenuBambas.add(
                            SubMenuModel(
                                QUERY_INSPECTION,
                                destination = Destination(
                                    R.id.bambasCredentialFragment, bundleOf(
                                        "SEARCH_MODE" to true,
                                        "title" to QUERY_INSPECTION,
                                        BUNDLE_SIGNATURE to dataBambas.isSignature,
                                        BUNDLE_NAME_INSPECTOR to "${dataBambas.supervisorName} ${dataBambas.supervisorLastName}",
                                        BUNDLE_SEARCH_INSPECTOR to dataBambas.inspectionQuery,
                                        BUNDLE_FILTER_INSPECTOR to dataBambas.filter
                                    )
                                )
                            )
                        )
                } else {
                    if (dataBambas.inspectionQuery)
                        listMenuBambas.add(
                            SubMenuModel(
                                QUERY_INSPECTION,
                                destination = Destination(
                                    R.id.bambasCredentialFragment, bundleOf(
                                        "SEARCH_MODE" to true,
                                        "title" to QUERY_INSPECTION,
                                        BUNDLE_SIGNATURE to dataBambas.isSignature,
                                        BUNDLE_NAME_INSPECTOR to "${dataBambas.supervisorName} ${dataBambas.supervisorLastName}",
                                        BUNDLE_FILTER_INSPECTOR to dataBambas.filter
                                    )
                                )
                            )
                        )
                }
            }
            updateMenuBambas(listMenuBambas)
        }
    }

    private fun loadMenuYamana() {
        getWorkerYamana()
    }

    private fun getWorkerYamana() {
        try {
            val api = RestClient.buildYamana()
            val call = api.getWorker(SharedUtils.getUsuarioId(applicationContext))
            call.enqueue(object : Callback<ApiResponseAnglo<WorkerYamana?>> {
                override fun onFailure(call: Call<ApiResponseAnglo<WorkerYamana?>>, t: Throwable) {
                    Snackbar.make(
                        navMenuDrawer,
                        "Error al consultar sobre estado de funcionario",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Reintentar") { loadMenuYamana() }.show()
                }

                override fun onResponse(
                    call: Call<ApiResponseAnglo<WorkerYamana?>>,
                    response: Response<ApiResponseAnglo<WorkerYamana?>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            result.data?.let {
                                addMenuYamana()
                            }

                        }
                    } else {
                        if (response.code() == 401) {
                            val workerYamana = WorkerYamana()
                            workerYamana.rut = SharedUtils.getUsuarioId(applicationContext)
                            getTokenYamana(workerYamana)
                        }
                    }
                }

            })
        } catch (ex: Exception) {
            Snackbar.make(
                navMenuDrawer,
                "Error al consultar sobre estado de funcionario",
                Snackbar.LENGTH_LONG
            )
                .setAction("Reintentar") { loadMenuYamana() }.show()
        }
    }

    private fun getTokenYamana(workerYamana: WorkerYamana) {
        try {
            val api = RestClient.buildYamana()
            val call = api.getToken(workerYamana)
            call.enqueue(object : Callback<ApiResponseAnglo<String>> {
                override fun onResponse(
                    call: Call<ApiResponseAnglo<String>>,
                    response: Response<ApiResponseAnglo<String>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            if (result.data.isNotEmpty()) {
                                SharedUtils.setTokenYamana(applicationContext, result.data)
                                addMenuYamana()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                    SharedUtils.showToast(
                        applicationContext,
                        TAG + " syncTokenAuthorization() " + t.message
                    )
                }
            })
        } catch (ex: Exception) {
            SharedUtils.showToast(
                applicationContext,
                TAG + " syncTokenAuthorization() " + ex.message
            )
        }
    }

    private fun addMenuYamana() {
        val menuYamana = NavMenuModel(4, "Yamana", R.drawable.ic_account_card_details,
            object : ArrayList<SubMenuModel>() {
                init {
                    add(
                        0,
                        SubMenuModel(
                            "Credenciales  ",
                            destination = Destination(R.id.yamanaCredentialFragment)
                        )
                    )
                }
            })
        if (!containMenu(menuYamana.menuTitle)) {
            mainNavMenuList.add(menuYamana)
            updateDrawerMenu()
        }
    }

    private fun loadMenuCaserones() {
        getWorkerCaserones()
    }

    private fun getWorkerCaserones() {
        try {
            val api = RestClient.buildCaserones()
            val call = api.getWorker(SharedUtils.getUsuarioId(applicationContext))
            call.enqueue(object : Callback<ApiResponseAnglo<WorkerCaserones?>> {
                override fun onFailure(
                    call: Call<ApiResponseAnglo<WorkerCaserones?>>,
                    t: Throwable
                ) {
                    Snackbar.make(
                        navMenuDrawer,
                        "Error al consultar sobre estado de funcionario",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Reintentar") { loadMenuCaserones() }.show()
                }

                override fun onResponse(
                    call: Call<ApiResponseAnglo<WorkerCaserones?>>,
                    response: Response<ApiResponseAnglo<WorkerCaserones?>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            result.data?.let {
                                val workerCaserones = WorkerCaserones()
                                workerCaserones.rut = SharedUtils.getUsuarioId(applicationContext)
                                getTokenCaserones(workerCaserones)
                                addMenuCaserones()
                            }

                        }
                    } else {
                        if (response.code() == 401) {
                            val workerCaserones = WorkerCaserones()
                            workerCaserones.rut = SharedUtils.getUsuarioId(applicationContext)
                            getTokenCaserones(workerCaserones)
                        }
                    }
                }

            })
        } catch (ex: Exception) {
            Snackbar.make(
                navMenuDrawer,
                "Error al consultar sobre estado de funcionario",
                Snackbar.LENGTH_LONG
            )
                .setAction("Reintentar") { loadMenuCaserones() }.show()
        }
    }

    private fun getTokenCaserones(workerCaserones: WorkerCaserones) {
        try {
            val api = buildCaserones()
            val loginRequest = LoginRequest(workerCaserones.rut)
            val call = api.getToken(loginRequest)
            call.enqueue(object : Callback<ApiResponseAnglo<String>> {
                override fun onResponse(
                    call: Call<ApiResponseAnglo<String>>,
                    response: Response<ApiResponseAnglo<String>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            if (result.data.isNotEmpty()) {
                                SharedUtils.setTokenCaserones(applicationContext, result.data)
                                addMenuCaserones()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                    SharedUtils.showToast(
                        applicationContext,
                        TAG + " syncTokenAuthorization() " + t.message
                    )
                }
            })
        } catch (ex: Exception) {
            SharedUtils.showToast(
                applicationContext,
                TAG + " syncTokenAuthorization() " + ex.message
            )
        }
    }

    private fun addMenuCaserones() {
        val menuCaserones = NavMenuModel(4, "Caserones", R.drawable.ic_account_card_details,
            object : ArrayList<SubMenuModel>() {
                init {
                    add(
                        0,
                        SubMenuModel(
                            "Credenciales   ",
                            destination = Destination(R.id.caseronesCredentialFragment)
                        )
                    )
                    add(
                        1,
                        SubMenuModel(
                            "Encuesta COVID - 19 ",
                            destination = Destination(
                                R.id.historicoCheckListFragment,
                                bundleOf(
                                    HistoricoCheckListFragment.CHECKLIST_TYPE to "COV",
                                    HistoricoCheckListFragment.CHECKLIST_NAME to "Encuesta COVID - 19",
                                    HistoricoCheckListFragment.COMPANY_ID to Companies.CAS.valor
                                )
                            )
                        )
                    )
                }
            })
        if (!containMenu(menuCaserones.menuTitle)) {
            mainNavMenuList.add(menuCaserones)
            updateDrawerMenu()
        }
    }

    private fun loadMenuEA() {
        getWorkerEA()
    }

    private fun getWorkerEA() {
        try {
            val api = RestClient.buildEA()
            val call = api.getWorkerCredencial(SharedUtils.getUsuarioId(applicationContext))
            call.enqueue(object : Callback<ApiResponseAnglo<WorkerEA>> {
                override fun onFailure(call: Call<ApiResponseAnglo<WorkerEA>>, t: Throwable) {
                    Snackbar.make(
                        navMenuDrawer,
                        "No se pudo consultar su estado de conductor",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Reintentar") { loadMenuEA() }.show()
                }
                override fun onResponse(
                    call: Call<ApiResponseAnglo<WorkerEA>>,
                    response: Response<ApiResponseAnglo<WorkerEA>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            result.data?.let { workerEA ->
                                addMenuEA(workerEA)
                            }
                        }
                    } else {
                        if (response.code() == 401) {
                            val workerEA = WorkerEA()
                            workerEA.rut = SharedUtils.getUsuarioId(applicationContext)
                            getTokenEA(workerEA)
                        }
                    }
                }

            })
        } catch (ex: Exception) {
            Log.e(TAG, "getWorkerEA error : ${ex.message}", ex)
            Snackbar.make(
                navMenuDrawer,
                "No se pudo consultar su estado de trabajador",
                Snackbar.LENGTH_LONG
            )
                .setAction("Reintentar") { loadMenuEA() }.show()
        }
    }

    private fun addMenuEA(workerEA: WorkerEA) {
        val menuEA = NavMenuModel(4, "El Abra", R.drawable.ic_account_card_details,
            object : ArrayList<SubMenuModel>() {
                init {
                    add(
                        0,
                        SubMenuModel(
                            "Credencial Virtual",
                            destination = Destination(R.id.EACredentialFragment)
                        )
                    )
                    if (workerEA.autCurso == "SI") {
                        add(
                            1,
                            SubMenuModel(
                                "Estado de cursos",
                                destination = Destination(
                                    R.id.lectorQrManualFragment,
                                    bundleOf(
                                        "vista" to "EA",
                                        "atras" to false,
                                        "title" to "Estado de cursos",
                                        "argsCliente" to "EA"
                                    )
                                )
                            )
                        )
                    }
                }
            })
        if (!containMenu(menuEA.menuTitle)) {
            mainNavMenuList.add(menuEA)
            updateDrawerMenu()
        }
    }

    private fun getTokenEA(workerEA: WorkerEA) {
        try {
            val api = RestClient.buildEA()
            val call = api.getToken(workerEA)
            call.enqueue(object : Callback<ApiResponseAnglo<String>> {
                override fun onResponse(
                    call: Call<ApiResponseAnglo<String>>,
                    response: Response<ApiResponseAnglo<String>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            if (result.data.isNotEmpty()) {
                                SharedUtils.setTokenEA(applicationContext, result.data)
                                addMenuEA(workerEA)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                    SharedUtils.showToast(
                        applicationContext,
                        TAG + " syncTokenAuthorization() " + t.message
                    )
                }
            })
        } catch (ex: Exception) {
            SharedUtils.showToast(
                applicationContext,
                TAG + " syncTokenAuthorization() " + ex.message
            )
        }
    }

    private fun loadMenuGf() {
        getTelefonoCovGF()
        getWorkerGf()
    }

    private fun getWorkerGf() {
        try {
            val api = RestClient.buildGf()
            val call = api.getWorker(
                object : HashMap<String, String>() {
                    init {
                        put("WorkerId", SharedUtils.getUsuarioId(applicationContext))
                    }
                }
            )
            call.enqueue(object : Callback<ApiResponseAnglo<WorkerGoldfields>> {
                override fun onResponse(
                    call: Call<ApiResponseAnglo<WorkerGoldfields>>,
                    response: Response<ApiResponseAnglo<WorkerGoldfields>>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()!!.isSuccess) {
                            response.body()!!.data?.let {
                                addMenuGf()
                            }
                        }
                    } else {
                        if (response.code() == 401) {
                            getTokenGoldfields()
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponseAnglo<WorkerGoldfields>>,
                    t: Throwable
                ) {
                    loaderMain.isRefreshing = false
                    Snackbar.make(
                        navMenuDrawer,
                        "No se pudo consultar su estado de conductor",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Reintentar") { loadMenuGf() }.show()
                }
            })
        } catch (ex: Exception) {
            Log.e(TAG, "getWorkerGf error : ${ex.message}", ex)
            Snackbar.make(
                navMenuDrawer,
                "No se pudo consultar su estado de trabajador",
                Snackbar.LENGTH_LONG
            )
                .setAction("Reintentar") { loadMenuGf() }.show()
        }
    }

    private fun getTokenGoldfields() {
        try {
            val api = buildGf()
            val call = api.getToken(
                object : HashMap<String, String>() {
                    init {
                        put("WorkerId", SharedUtils.getUsuarioId(applicationContext))
                    }
                }
            )
            call.enqueue(object : Callback<ApiResponseAnglo<String>> {
                override fun onResponse(
                    call: Call<ApiResponseAnglo<String>>,
                    response: Response<ApiResponseAnglo<String>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            if (result.data.isNotEmpty()) {
                                SharedUtils.setTokenGoldfields(applicationContext, result.data)
                                addMenuGf()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                    SharedUtils.showToast(
                        applicationContext,
                        TAG + " syncTokenAuthorization() " + t.message
                    )
                }

            })
        } catch (ex: Exception) {
            SharedUtils.showToast(
                applicationContext,
                TAG + " syncTokenAuthorization() " + ex.message
            )
        }

    }

    private fun loadMenuKinross() {
        getTelefonoCovKRS()
        getWorkerKinross()
    }

    private fun getWorkerKinross() {
        try {
            val api = RestClient.buildKinross()
            val workerKinross = WorkerKinross()
            workerKinross.workerId = SharedUtils.getUsuarioId(applicationContext)
            val call = api.getWorker(workerKinross)
            call.enqueue(object : Callback<ApiResponseAnglo<WorkerKinross>> {
                override fun onFailure(call: Call<ApiResponseAnglo<WorkerKinross>>, t: Throwable) {
                    // empty on purpose
                }

                override fun onResponse(
                    call: Call<ApiResponseAnglo<WorkerKinross>>,
                    response: Response<ApiResponseAnglo<WorkerKinross>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            result.data?.let {
                                addMenuKinross()
                            }
                        }
                    } else {
                        if (response.code() == 401) {
                            getTokenKinross(workerKinross)
                        }
                    }
                }
            })
        } catch (ex: Exception) {
            Log.e(TAG, "getWorkerKS error : ${ex.message}", ex)
            Snackbar.make(
                navMenuDrawer,
                "No se pudo consultar su estado de conductor",
                Snackbar.LENGTH_LONG
            )
                .setAction("Reintentar") { loadMenuKinross() }.show()
        }
    }

    private fun getTokenKinross(workerKinross: WorkerKinross) {
        try {
            val api = RestClient.buildKinross()
            val call = api.getToken(workerKinross)
            call.enqueue(object : Callback<ApiResponseAnglo<String>> {
                override fun onResponse(
                    call: Call<ApiResponseAnglo<String>>,
                    response: Response<ApiResponseAnglo<String>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            if (result.data.isNotEmpty()) {
                                SharedUtils.setTokenKinross(applicationContext, result.data)
                                addMenuKinross()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                    SharedUtils.showToast(
                        applicationContext,
                        TAG + " syncTokenAuthorization() " + t.message
                    )
                }
            })
        } catch (ex: Exception) {
            SharedUtils.showToast(
                applicationContext,
                TAG + " syncTokenAuthorization() " + ex.message
            )
        }
    }

    private fun addMenuKinross() {
        val menuKinross = NavMenuModel(4, "Kinross", R.drawable.ic_account_card_details,
            object : ArrayList<SubMenuModel>() {
                init {
                    add(
                        0,
                        SubMenuModel(
                            "Vista Credenciales",
                            destination = Destination(
                                R.id.kinrossCredentialFragment,
                                bundleOf("title" to "Vista Credenciales")
                            )
                        )
                    )
                    add(
                        1,
                        SubMenuModel(
                            "Validacion Competencias",
                            destination = Destination(
                                R.id.lectorQrFragment,
                                bundleOf(
                                    "vista" to "KINROSS",
                                    "atras" to false,
                                    "title" to "Validacion Competencias"
                                )
                            )
                        )
                    )
                    add(
                        2,
                        SubMenuModel(
                            "Encuestas COVID - 19",
                            destination = Destination(
                                R.id.historicoCheckListFragment,
                                bundleOf(
                                    HistoricoCheckListFragment.CHECKLIST_TYPE to "COV",
                                    HistoricoCheckListFragment.CHECKLIST_NAME to "Encuesta COVID 19",
                                    HistoricoCheckListFragment.COMPANY_ID to Companies.KRS.valor
                                )
                            )
                        )
                    )
                    add(
                        3,
                        SubMenuModel(
                            "Fatiga - Somnolencia ",
                            destination = Destination(
                                R.id.historicoCheckListFragment,
                                bundleOf(
                                    HistoricoCheckListFragment.CHECKLIST_TYPE to "TFS",
                                    HistoricoCheckListFragment.CHECKLIST_NAME to "Fatiga y Somnolencia",
                                    HistoricoCheckListFragment.COMPANY_ID to Companies.KRS.valor
                                )
                            )
                        )
                    )
                }
            })
        if (!containMenu(menuKinross.menuTitle)) {
            mainNavMenuList.add(menuKinross)
            updateDrawerMenu()
        }
    }

    private fun getTelefonoCovKRS() {
        try {
            val api = buildKinross()
            val call = api.getTelefonoCov()

            call.enqueue(object : Callback<ApiResponseAnglo<String>> {
                override fun onResponse(
                    call: Call<ApiResponseAnglo<String>>,
                    response: Response<ApiResponseAnglo<String>>
                ) {
                    if (response.isSuccessful)
                        if (!response.body()!!.data.isNullOrBlank())
                            SharedUtils.setTelefonoCovidKRS(
                                this@MainActivity,
                                response.body()!!.data
                            )
                        else
                            SharedUtils.setTelefonoCovidKRS(this@MainActivity, "Sin Número")
                    else
                        Log.e("WCApp: ", "Error al obtener telefono Kinross")
                }

                override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                    Log.e("WCApp: ", "Ocurrió un error al obtener telefono: " + t.message)
                }
            })
        } catch (e: Exception) {
            Log.e("WCApp: ", "Ocurrió un error: " + e.message)
        }
    }

    private fun getTelefonoCovCAS() {
        try {
            val api = buildCaserones()
            val call = api.getTelefonoCov()

            call.enqueue(object : Callback<ApiResponseAnglo<String>> {
                override fun onResponse(
                    call: Call<ApiResponseAnglo<String>>,
                    response: Response<ApiResponseAnglo<String>>
                ) {
                    if (response.isSuccessful)
                        if (!response.body()!!.data.isNullOrBlank())
                            SharedUtils.setTelefonoCovidKRS(
                                this@MainActivity,
                                response.body()!!.data
                            )
                        else
                            SharedUtils.setTelefonoCovidKRS(this@MainActivity, "Sin Número")
                    else
                        Log.e("WCApp: ", "Error al obtener telefono Caserones")
                }

                override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                    Log.e("WCApp: ", "Ocurrió un error al obtener telefono: " + t.message)
                }
            })
        } catch (e: Exception) {
            Log.e("WCApp: ", "Ocurrió un error: " + e.message)
        }
    }

    private fun addMenuGf() {
        val menuGf = NavMenuModel(4, "Goldfields", R.drawable.ic_account_card_details,
            object : ArrayList<SubMenuModel>() {
                init {
                    add(
                        0,
                        SubMenuModel(
                            "Encuestas COVID-19 ",
                            destination = Destination(
                                R.id.historicoCheckListFragment,
                                bundleOf(
                                    HistoricoCheckListFragment.CHECKLIST_TYPE to "COV",
                                    HistoricoCheckListFragment.CHECKLIST_NAME to "Encuesta COVID-19",
                                    HistoricoCheckListFragment.COMPANY_ID to Companies.GF.valor
                                )
                            )
                        )
                    )
                    add(
                        1,
                        SubMenuModel(
                            "Credencial Goldfields",
                            destination = Destination(
                                R.id.goldfieldsCredentialFragment,
                                bundleOf("title" to "Credencial Goldfields")
                            )
                        )
                    )
                }
            })
        if (!containMenu(menuGf.menuTitle)) {
            mainNavMenuList.add(menuGf)
            updateDrawerMenu()
        }
    }

    private fun getTelefonoCovCDL() {
        try {
            val api = buildCdl()
            val call = api.getTelefonoCov()

            call.enqueue(object : Callback<ApiResponseAnglo<String>> {
                override fun onResponse(
                    call: Call<ApiResponseAnglo<String>>,
                    response: Response<ApiResponseAnglo<String>>
                ) {
                    if (response.isSuccessful)
                        if (!response.body()!!.data.isNullOrBlank())
                            SharedUtils.setTelefonoCovidCDL(
                                this@MainActivity,
                                response.body()!!.data
                            )
                        else
                            SharedUtils.setTelefonoCovidCDL(this@MainActivity, "Sin Número")
                    else
                        Log.e("WCApp: ", "Error al obtener telefono Cdl")
                }

                override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                    Log.e("WCApp: ", "Ocurrió un error al obtener telefono: " + t.message)
                }
            })
        } catch (e: Exception) {
            Log.e("WCApp: ", "Ocurrió un error: " + e.message)
        }
    }

    private fun getTelefonoCovGF() {
        try {
            val api = buildGf()
            val call = api.getTelefonoCov()

            call.enqueue(object : Callback<ApiResponseAnglo<String>> {
                override fun onResponse(
                    call: Call<ApiResponseAnglo<String>>,
                    response: Response<ApiResponseAnglo<String>>
                ) {
                    if (response.isSuccessful)
                        if (!response.body()!!.data.isNullOrBlank())
                            SharedUtils.setTelefonoCovidGF(
                                this@MainActivity,
                                response.body()!!.data
                            )
                        else
                            SharedUtils.setTelefonoCovidGF(this@MainActivity, "Sin Número")
                    else
                        Log.e("WCApp: ", "Error al obtener telefono Gf")
                }

                override fun onFailure(call: Call<ApiResponseAnglo<String>>, t: Throwable) {
                    Log.e("WCApp: ", "Ocurrió un error al obtener telefono: " + t.message)
                }
            })
        } catch (e: Exception) {
            Log.e("WCApp: ", "Ocurrió un error: " + e.message)
        }
    }

    private fun loadMenuAnglo() {
        angloSecurityViewModel.getToken(SharedUtils.getUsuarioId(this))
        val usuarioId = SharedUtils.getUsuarioId(applicationContext)
        val workerRequest = WorkerRequest(usuarioId)
        if (SharedUtils.isOnline(applicationContext)) {
            angloSecurityViewModel.token.observe(this) {
                if (it != null) {
                    checkListPreUsoViewModel.searchAnglo(workerRequest)
                }
            }
        } else {
            checkListPreUsoViewModel.searchAnglo(workerRequest)
        }
    }

    private fun observeMenuAnglo() {
        angloSecurityViewModel.token.observe(this) { result ->
            when (result) {
                is Success -> {
                    localStorage[TOKEN_ANGLO] = result.data
                    Log.d("TOKEN ANGLO", result.data)
                }

                is Error -> {
                    Log.e("TOKEN ANGLO", result.error)
                }

                else -> {}
            }
        }
    }

    private fun observeTokenPHC() {
        if (SharedUtils.isOnline(applicationContext)) {
            viewModel.tokenPHC.observe(this) {
                if (it != null) {
                    localStorage[TOKEN_AUTHORIZATION_PHC] = it
                    insertMenusPHC()
                }
            }
        } else {
            viewModel.getTokenPHC()
        }
    }

    private fun insertMenusPHC() {
        try {
            loaderMain.isRefreshing = true
            val api = RestClient.buildPHC()
            val call = api.getWorker(
                workerId = SharedUtils.getUsuarioId(applicationContext)
            )
            call.enqueue(object : Callback<ApiResponsePHC<WorkerPHC>> {
                override fun onFailure(call: Call<ApiResponsePHC<WorkerPHC>>, t: Throwable) {
                    loaderMain.isRefreshing = false
                    Snackbar.make(
                        navMenuDrawer,
                        "No se pudo consultar el estado del trabajador",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Reintentar") { insertMenusPHC() }.show()
                }

                override fun onResponse(
                    call: Call<ApiResponsePHC<WorkerPHC>>,
                    response: Response<ApiResponsePHC<WorkerPHC>>
                ) {
                    if (response.isSuccessful) {
                        val workerPHC = response.body()
                        if (workerPHC?.data != null) {
                            if (workerPHC.data.credencial == "SI") {
                                insertMenuPHC(workerPHC.data)
                            } else {
                                insertCredentialPHC()
                            }
                        } else
                            loaderMain.isRefreshing = false
                    } else
                        loaderMain.isRefreshing = false
                }
            })
        } catch (
            ex: Exception
        ) {
            loaderMain.isRefreshing = false
        }
    }

    private fun insertMenuPHC(workerPHC: WorkerPHC) {
        val manuPhc = NavMenuModel(4, "PHC", R.drawable.ic_account_card_details,
            object : ArrayList<SubMenuModel>() {
                init {
                    add(
                        0, SubMenuModel(
                            "Lector QR",
                            destination = Destination(
                                R.id.lectorQrPHCFragment,
                                bundleOf(
                                    "title" to "Lector QR credencial"
                                )
                            )
                        )
                    )
                    add(
                        1, SubMenuModel(
                            "Credencial PHC",
                            destination = Destination(
                                R.id.PHCCredentialFragment,
                                bundleOf(
                                    "title" to "Credencial PHC"
                                )
                            )
                        )
                    )
                }
            })
        if (!containMenu(manuPhc.menuTitle)) {
            mainNavMenuList.add(manuPhc)
            updateDrawerMenu()
        }
    }

    private fun insertCredentialPHC() {
        val manuPhc = NavMenuModel(4, "PHC", R.drawable.ic_account_card_details,
            object : ArrayList<SubMenuModel>() {
                init {
                    add(
                        0, SubMenuModel(
                            "Credencial PHC",
                            destination = Destination(
                                R.id.PHCCredentialFragment,
                                bundleOf(
                                    "title" to "Credencial PHC"
                                )
                            )
                        )
                    )
                }
            })
        if (!containMenu(manuPhc.menuTitle)) {
            mainNavMenuList.add(manuPhc)
            updateDrawerMenu()
        }
    }

    private fun loadMenusAnglo() {
        try {
            loaderMain.isRefreshing = true
            val api = RestClient.buildAnglo()
            val call = api.getWorker(
                object : HashMap<String, String>() {
                    init {
                        put("WorkerId", SharedUtils.getUsuarioId(applicationContext))
                    }
                }
            )
            call.enqueue(object : Callback<ApiResponseAnglo<WorkerAnglo>> {
                override fun onResponse(
                    call: Call<ApiResponseAnglo<WorkerAnglo>>,
                    response: Response<ApiResponseAnglo<WorkerAnglo>>
                ) {
                    if (response.isSuccessful) {
                        val workerAngloApiResponseAnglo = response.body()
                        if (workerAngloApiResponseAnglo!!.isSuccess) {
                            if (workerAngloApiResponseAnglo.data != null) {
                                SharedUtils.setIdCompany(
                                    applicationContext,
                                    workerAngloApiResponseAnglo.data.companiaId
                                )
                                setSharedWorkerInfo(workerAngloApiResponseAnglo)
                                insertMenuAnglo(workerAngloApiResponseAnglo.data)
                                if (workerAngloApiResponseAnglo.data.division != null && workerAngloApiResponseAnglo.data.division.contains(
                                        "QV"
                                    )
                                ) {
                                    loadMenuQuellaveco(workerAngloApiResponseAnglo.data)
                                } else
                                    loaderMain.isRefreshing = false
                            } else
                                loaderMain.isRefreshing = false
                        } else
                            loaderMain.isRefreshing = false
                    } else
                        loaderMain.isRefreshing = false
                }

                override fun onFailure(call: Call<ApiResponseAnglo<WorkerAnglo>>, t: Throwable) {
                    loaderMain.isRefreshing = false
                    Snackbar.make(
                        navMenuDrawer,
                        "No se pudo consultar su estado de conductor",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Reintentar") { loadMenusAnglo() }.show()
                }
            })
        } catch (ex: Exception) {
            loaderMain.isRefreshing = false
        }
    }

    private fun loadMenuQuellaveco(worker: WorkerAnglo) {
        val listMenuQuellaveco = ArrayList<SubMenuModel>()
        listMenuQuellaveco.add(
            SubMenuModel(
                "Encuestas",
                destination = Destination(
                    R.id.historicoCheckListFragment,
                    bundleOf(
                        HistoricoCheckListFragment.CHECKLIST_TYPE to "EQV",
                        HistoricoCheckListFragment.CHECKLIST_NAME to "Encuestas",
                    )
                )
            )
        )
        listMenuQuellaveco.add(
            SubMenuModel(
                "Datos Iniciales",
                destination = Destination(R.id.datosInicialesFragment)
            )
        )
        listMenuQuellaveco.add(
            SubMenuModel(
                "Reporte diario de Síntomas",
                destination = Destination(
                    R.id.historicoCheckListFragment,
                    bundleOf(
                        HistoricoCheckListFragment.CHECKLIST_TYPE to "DDS",
                        HistoricoCheckListFragment.CHECKLIST_NAME to "Sintomas y Contactos",
                    )
                )
            )
        )
        if (worker.isMandante) {
            listMenuQuellaveco.add(
                SubMenuModel(
                    "Reserva Cursos",
                    destination = Destination(
                        R.id.historialReservaCursoFragment,
                    )
                )
            )
        }
        listMenuQuellaveco.add(
            SubMenuModel(
                "Credencial Virtual",
                destination = Destination(
                    R.id.quellavecoCredentialVirtualFragment
                )
            )
        )
        listMenuQuellaveco.add(
            SubMenuModel(
                "Credencial Curso",
                destination = Destination(
                    R.id.quellavecoCredentialFragment, bundleOf("title" to "Credencial Curso")
                )
            )
        )
        listMenuQuellaveco.add(
            SubMenuModel(
                "Credencial Licencia",
                destination = Destination(
                    R.id.quellavecoCredentialLicenciaFragment
                )
            )
        )

        if(worker.isApproverUser){
            listMenuQuellaveco.add(
                SubMenuModel(
                    APPROVE_MOVEMENTS,
                    fragment = null
                )
            )
        }

        checkListPreUsoViewModel.workerSearchAnglo.observe(this) { dataAnglo ->
            dataAnglo?.let {
                if (it.isDriver == driverAnglo) {
                    listMenuQuellaveco.add(
                        SubMenuModel(
                            QUERY_INSPECTION_PRE_USO,
                            destination = Destination(
                                R.id.quellavecoCredentialLicenciaFragment,
                                bundleOf(
                                    "isConsultInspection" to true
                                ),
                                worker.id
                            )
                        )
                    )
                    listMenuQuellaveco.add(
                        SubMenuModel(
                            PRE_USO_CHECKLIST,
                            destination = Destination(
                                R.id.quellavecoCredentialLicenciaFragment,
                                bundleOf(
                                    "isConsultInspection" to false
                                ),
                                worker.id
                            )
                        )
                    )
                }
                checkListPreUsoViewModel.workerSearchAnglo.removeObservers(this)
            }
        }
        updateMenuQuellaveco(listMenuQuellaveco)

        getWorkerControlInicial(listMenuQuellaveco)
    }

    private fun insertMenuAnglo(worker: WorkerAnglo) {
        val menuAnglo = NavMenuModel(2, "AngloAmerican", R.drawable.ic_account_card_details,
            object : ArrayList<SubMenuModel>() {
                init {
                    add(
                        0, SubMenuModel(
                            "Resumen",
                            destination = Destination(
                                R.id.resumeFragment,
                                bundleOf(
                                    "title" to "Resumen"
                                )
                            )
                        )
                    )
                    add(
                        1,
                        SubMenuModel(
                            "Credencial",
                            destination = Destination(
                                R.id.angloamericanCredentialFragment
                            )
                        )

                    )
                    add(
                        2,
                        SubMenuModel(
                            "Encuesta de Satisfacción",
                            destination = Destination(
                                R.id.historicoEncuestasFragment,
                                bundleOf(
                                    "tipo_encuesta" to "ENCUESTA",
                                    "nombre_encuesta" to "Encuesta de Satisfacción",
                                    "indice_primario" to "0"
                                )
                            )
                        )
                    )
                    add(
                        3,
                        SubMenuModel(
                            "Fatiga",
                            destination = Destination(
                                R.id.historicoCheckListFragment,
                                bundleOf(
                                    HistoricoCheckListFragment.CHECKLIST_TYPE to "TFS",
                                    HistoricoCheckListFragment.CHECKLIST_NAME to "Fatiga",
                                )
                            )
                        )
                    )
                    add(
                        4,
                        SubMenuModel(
                            "Vehículos",
                            destination = Destination(
                                R.id.historicoCheckListFragment,
                                bundleOf(
                                    HistoricoCheckListFragment.CHECKLIST_TYPE to "TDV",
                                    HistoricoCheckListFragment.CHECKLIST_NAME to "Vehiculos",
                                )
                            )
                        )
                    )
                    add(
                        5,
                        SubMenuModel(
                            "Preacceso",
                            destination = Destination(
                                R.id.controlPreaccesoFragment,
                                bundleOf(
                                    "CLIENT" to Companies.ANGLO.valor,
                                    "title" to "Preacceso"
                                )
                            )
                        )
                    )
                    add(
                        6,
                        SubMenuModel(
                            BOOK_COURSES,
                            destination = Destination(
                                R.id.controlPreaccesoFragment,
                                bundleOf(
                                    "CLIENT" to Companies.ANGLO.valor,
                                    "title" to "Preacceso"
                                ),
                                worker.id,
                                worker.companiaId,
                                worker.filterRC
                            )
                        )
                    )

                    add(
                        7,
                        SubMenuModel(
                            BUS_RESERVATION,
                            destination = Destination(R.id.historialReservaBusFragment)
                        )
                    )


                    add(
                        SubMenuModel(
                            PRE_ACCESS_MINE,
                            destination = Destination(
                                R.id.historyPreaccesMine,
                                bundleOf(
                                    "CLIENT" to Companies.ANGLO.valor,
                                    "title" to PRE_ACCESS_MINE
                                ),
                                worker.id,
                            ),
                        )
                    )
                }
            })
        if (!containMenu(menuAnglo.menuTitle)) {
            mainNavMenuList.add(menuAnglo)
            updateDrawerMenu()
        }
    }

    private fun getAntaWorkerControlInicial(listMenuAnta: java.util.ArrayList<SubMenuModel>) {
        val api = RestClient.buildAnta()
        val call = api.getControlInicial(SharedUtils.getUsuarioId(applicationContext))

        call.enqueue(object : Callback<ApiResponseAnglo<ArrayList<ControlInicial>>> {
            override fun onFailure(
                call: Call<ApiResponseAnglo<ArrayList<ControlInicial>>>,
                t: Throwable
            ) {
                loaderMain.isRefreshing = false
                SharedUtils.showToast(
                    this@MainActivity,
                    "No se pudo obtener el histórico de control inicial."
                )
                Log.e(TAG, "getControlInicialResult error: ${t.message}")
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<ArrayList<ControlInicial>>>,
                response: Response<ApiResponseAnglo<ArrayList<ControlInicial>>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {

                        loaderMain.isRefreshing = false

                        val data = result.data
                        if (data.isNotEmpty()) {
                            val last = data[0]
                            // es 2do control inicial?
                            if (last.codControlInicialPadre != null &&
                                !last.fechaVisible.isNullOrEmpty()
                            ) {
                                if (last.positivo == null) { // es control inicial sin resultado ?
                                    if (SharedUtils.compareDays(
                                            last.fechaVisible!!,
                                            SharedUtils.wCDate,
                                            "yyyyMMdd"
                                        )!! <= 0
                                    ) { // es fecha válida?
                                        listMenuAnta.add(
                                            SubMenuModel(
                                                resources.getString(R.string.test_covid_title_anta),
                                                destination = Destination(
                                                    R.id.mainDJFragment, bundleOf(
                                                        "SecondDeclaration" to true,
                                                        "title" to resources.getString(R.string.test_covid_title_anta)
                                                    )
                                                )
                                            )
                                        )
                                        updateMenuAnta(listMenuAnta)
                                    } else {
                                        loaderMain.isRefreshing = false
                                        SharedUtils.showToast(
                                            this@MainActivity,
                                            "No disponible para llenar 2da declaración."
                                        )
                                    }
                                } else {
                                    listMenuAnta.add(
                                        SubMenuModel(
                                            resources.getString(R.string.test_covid_title_anta),
                                            destination = Destination(
                                                R.id.mainDJFragment, bundleOf(
                                                    "title" to resources.getString(R.string.test_covid_title_anta)
                                                )
                                            )
                                        )
                                    )
                                    updateMenuAnta(listMenuAnta)
                                }
                            } else if (last.codControlInicialPadre == null) {
                                if (!last.inicial.isNullOrEmpty() && last.inicial == "SI") {
                                    listMenuAnta.add(
                                        SubMenuModel(
                                            resources.getString(R.string.test_covid_title_anta),
                                            destination = Destination(
                                                R.id.mainDJFragment, bundleOf(
                                                    "title" to resources.getString(R.string.test_covid_title_anta)
                                                )
                                            )
                                        )
                                    )
                                    updateMenuAnta(listMenuAnta)
                                } else {
                                    loaderMain.isRefreshing = false
                                    SharedUtils.showToast(
                                        this@MainActivity,
                                        "Llene datos iniciales primero."
                                    )
                                }
                            }
                        }
                    } else {
                        loaderMain.isRefreshing = false
                        SharedUtils.showToast(
                            this@MainActivity,
                            "No se pudo obtener el histórico de control inicial."
                        )
                        Log.e(TAG, "getControlInicialResult result: $result")
                    }

                } else {
                    loaderMain.isRefreshing = false
                    //SharedUtils.showToast(this@MainActivity, "Ocurrio un error al consultar su control inicial.")
                    Log.e(TAG, "getControlInicialResult response: $response")
                }
            }
        })
    }

    private fun getWorkerControlInicial(listMenuQuellaveco: java.util.ArrayList<SubMenuModel>) {
        val api = RestClient.buildAnglo()
        val call = api.getControlInicial(SharedUtils.getUsuarioId(applicationContext), "null")

        call.enqueue(object : Callback<ApiResponseAnglo<ArrayList<ControlInicial>>> {
            override fun onFailure(
                call: Call<ApiResponseAnglo<ArrayList<ControlInicial>>>,
                t: Throwable
            ) {
                loaderMain.isRefreshing = false
                SharedUtils.showToast(
                    this@MainActivity,
                    "No se pudo obtener el histórico de control inicial."
                )
                Log.e(TAG, "getControlInicialResult error: ${t.message}")
            }

            override fun onResponse(
                call: Call<ApiResponseAnglo<ArrayList<ControlInicial>>>,
                response: Response<ApiResponseAnglo<ArrayList<ControlInicial>>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {

                        loaderMain.isRefreshing = false

                        val data = result.data
                        if (data.isNotEmpty()) {
                            val last = data[0]
                            // es 2do control inicial?
                            if (last.codControlInicialPadre != null &&
                                !last.fechaVisible.isNullOrEmpty()
                            ) {
                                if (last.positivo == null) { // es control inicial sin resultado ?
                                    if (SharedUtils.compareDays(
                                            last.fechaVisible!!,
                                            SharedUtils.wCDate,
                                            "yyyyMMdd"
                                        )!! <= 0
                                    ) { // es fecha válida?
                                        listMenuQuellaveco.add(
                                            SubMenuModel(
                                                resources.getString(R.string.test_covid_title_qv),
                                                destination = Destination(
                                                    R.id.mainDJFragment2, bundleOf(
                                                        MainDJFragment.AFFIDAVIT to true
                                                    )
                                                )
                                            )
                                        )
                                        updateMenuQuellaveco(listMenuQuellaveco)
                                    }
                                }
                            } else if (last.codControlInicialPadre == null) {
                                if (!last.inicial.isNullOrEmpty() && last.inicial == "SI") {
                                    listMenuQuellaveco.add(
                                        SubMenuModel(
                                            resources.getString(R.string.test_covid_title_qv),
                                            destination = Destination(
                                                R.id.mainDJFragment2
                                            )
                                        )
                                    )
                                    updateMenuQuellaveco(listMenuQuellaveco)
                                }
                            }
                        }
                    } else {
                        loaderMain.isRefreshing = false
                        SharedUtils.showToast(
                            this@MainActivity,
                            "No se pudo obtener el histórico de control inicial."
                        )
                        Log.e(TAG, "getControlInicialResult result: $result")
                    }

                } else {
                    loaderMain.isRefreshing = false
                    SharedUtils.showToast(
                        this@MainActivity,
                        "Ocurrio un error al consultar su control inicial."
                    )
                    Log.e(TAG, "getControlInicialResult response: $response")
                }
            }
        })
    }

    private fun updateMenuCollahuasi(listMenu: ArrayList<SubMenuModel>) {
        val navMenu = NavMenuModel(6, "Collahuasi", R.drawable.ic_account_card_details, listMenu)

        if (containMenu(navMenu.menuTitle))
            mainNavMenuList = mainNavMenuList.filter {
                it.menuTitle != navMenu.menuTitle
            } as ArrayList<NavMenuModel>

        mainNavMenuList.add(navMenu)
        updateDrawerMenu()
    }

    private fun updateMenuAnta(listMenuAnta: ArrayList<SubMenuModel>) {
        val navMenuAnta =
            NavMenuModel(6, "Antapaccay", R.drawable.ic_account_card_details, listMenuAnta)

        if (containMenu(navMenuAnta.menuTitle))
            mainNavMenuList = mainNavMenuList.filter {
                it.menuTitle != navMenuAnta.menuTitle
            } as ArrayList<NavMenuModel>

        mainNavMenuList.add(navMenuAnta)
        updateDrawerMenu()

    }

    private fun updateMenuQuellaveco(listMenuQuellaveco: ArrayList<SubMenuModel>) {
        val navMenuQuellaveco =
            NavMenuModel(3, "Quellaveco", R.drawable.ic_account_card_details, listMenuQuellaveco)

        if (containMenu(navMenuQuellaveco.menuTitle))
            mainNavMenuList = mainNavMenuList.filter {
                it.menuTitle != navMenuQuellaveco.menuTitle
            } as ArrayList<NavMenuModel>

        mainNavMenuList.add(navMenuQuellaveco)
        updateDrawerMenu()

    }

    private fun updateMenuBarrick(listMenuBarrick: ArrayList<SubMenuModel>) {
        val navMenuBarrick =
            NavMenuModel(4, "Barrick", R.drawable.ic_account_card_details, listMenuBarrick)

        if (containMenu(navMenuBarrick.menuTitle)) {
            mainNavMenuList = mainNavMenuList.filter {
                it.menuTitle != navMenuBarrick.menuTitle
            } as ArrayList<NavMenuModel>
        }
        mainNavMenuList.add(navMenuBarrick)
        updateDrawerMenu()
    }

    private fun updateMenuBambas(listMenuBambas: ArrayList<SubMenuModel>) {
        val navMenuBambas =
            NavMenuModel(4, "Las Bambas", R.drawable.ic_account_card_details, listMenuBambas)

        if (containMenu(navMenuBambas.menuTitle)) {
            mainNavMenuList = mainNavMenuList.filter {
                it.menuTitle != navMenuBambas.menuTitle
            } as ArrayList<NavMenuModel>
        }
        mainNavMenuList.add(navMenuBambas)
        updateDrawerMenu()
    }

    private fun updateMenuPucobre(listMenuPucobre: ArrayList<SubMenuModel>) {
        val navMenuPucobre =
            NavMenuModel(4, "Pucobre", R.drawable.ic_account_card_details, listMenuPucobre)

        if (containMenu(navMenuPucobre.menuTitle)) {
            mainNavMenuList = mainNavMenuList.filter {
                it.menuTitle != navMenuPucobre.menuTitle
            } as ArrayList<NavMenuModel>
        }
        mainNavMenuList.add(navMenuPucobre)
        updateDrawerMenu()
    }

    private fun observeMenuGmp() {
        viewModel.checkActive().observe(this, Observer {
            if (it) {
                val menuGmp = NavMenuModel(4, "Gmp", R.drawable.ic_account_card_details,
                    object : ArrayList<SubMenuModel>() {
                        init {
                            add(
                                0,
                                SubMenuModel(
                                    "Tamizaje",
                                    destination = Destination(
                                        R.id.historicoCheckListFragment,
                                        bundleOf(
                                            HistoricoCheckListFragment.CHECKLIST_TYPE to "TMZ",
                                            HistoricoCheckListFragment.CHECKLIST_NAME to "Tamizaje",
                                        )
                                    )
                                )
                            )
                        }
                    })
                if (!containMenu(menuGmp.menuTitle)) {
                    mainNavMenuList.add(menuGmp)
                    updateDrawerMenu()
                }
            }
        })
    }

    private fun observeMenuMc() {
        viewModel.MccheckActive().observe(this, Observer {
            if (it) {
                val menuGmp = NavMenuModel(4, "Mantos Cooper", R.drawable.ic_account_card_details,
                    object : ArrayList<SubMenuModel>() {
                        init {
                            add(
                                0,
                                SubMenuModel(
                                    "Encuesta COVID-19 Mc",
                                    destination = Destination(
                                        R.id.historicoCheckListFragment,
                                        bundleOf(
                                            HistoricoCheckListFragment.CHECKLIST_TYPE to "COV",
                                            HistoricoCheckListFragment.CHECKLIST_NAME to "Encuesta",
                                            HistoricoCheckListFragment.COMPANY_ID to Companies.MC.valor
                                        )
                                    )
                                )
                            )
                        }
                    })
                if (!containMenu(menuGmp.menuTitle)) {
                    mainNavMenuList.add(menuGmp)
                    updateDrawerMenu()
                }
            }
        })


    }

    /*private fun observeMenuCDL() {
        viewModel.CdlcheckActive().observe(this, Observer {
            if (it) {
                val menuGmp = NavMenuModel(4, "Candelaria", R.drawable.ic_account_card_details,
                    object : ArrayList<SubMenuModel>() {
                        init {
                            add(
                                0,
                                SubMenuModel(
                                    "Encuesta COVID-19",
                                    destination = Destination(
                                        R.id.historicoCheckListFragment,
                                        bundleOf(
                                            HistoricoCheckListFragment.CHECKLIST_TYPE to "COV",
                                            HistoricoCheckListFragment.CHECKLIST_NAME to "Encuesta",
                                            HistoricoCheckListFragment.COMPANY_ID to Companies.CDL.valor
                                        )
                                    )
                                )
                            )
                        }
                    })
                if (!containMenu(menuGmp.menuTitle)) {
                    mainNavMenuList.add(menuGmp)
                    updateDrawerMenu()
                }
            }
        })


    }*/


    private fun setSharedWorkerInfo(worker: ApiResponseAnglo<WorkerAnglo>) {
        SharedUtils.setWorkerDivision(this@MainActivity, worker.data.division)
        SharedUtils.setOSTCheckList(this@MainActivity, worker.data.ost)
        SharedUtils.setCompanyCheckList(this@MainActivity, worker.data.companiaId)
    }

    private fun containMenu(menuName: String): Boolean {
        if (mainNavMenuList.isNotEmpty()) {
            for (i in mainNavMenuList.indices) {
                if (mainNavMenuList[i].menuTitle.equals(menuName, ignoreCase = true))
                    return true
            }
        }
        return false
    }

    private fun getMenuList(menu: ArrayList<NavMenuModel>): ArrayList<TitleMenu> {
        val list = ArrayList<TitleMenu>()
        this.menu = menu
        for (i in menu.indices) {
            val subMenu = ArrayList<SubTitle>()
            if (menu[i].subMenu != null && menu[i].subMenu!!.size > 0) {
                for (j in menu[i].subMenu!!.indices) {
                    subMenu.add(
                        SubTitle(
                            menu[i].subMenu?.get(j)!!.subMenuTitle,
                            menu[i].subMenu?.get(j)!!.count
                        )
                    )
                }
            }
            list.add(TitleMenu(menu[i].menuTitle, subMenu, menu[i].menuIconDrawable))
        }
        return list
    }


    override fun onMenuItemClick(itemString: String) {
        parent@ for (i in menu!!.indices) {
            if (itemString == menu!![i].menuTitle) {
                if (menu!![i].menuTitle.equals("Salir", ignoreCase = true)) {
                    displaySignOutDialog()
                } else if (menu!![i].menuTitle.equals("Las Bambas", ignoreCase = true)) {
                    displayUpdateAppDialog()
                } else if (menu!![i].menuTitle.equals("Twilio", ignoreCase = true)) {
                    val intent = Intent(this, VideoCallActivity::class.java).apply {
                        putExtra(TWILIO_ACCESS_TOKEN, "")
                    }
                    startActivity(intent)
                } else {
                    if (menu!![i].fragment != null) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment_content_main, menu!![i].fragment!!)
                            .commitAllowingStateLoss()
                        title = SharedUtils.toTitleCase(menu!![i].menuTitle)
                    } else {
                        if (menu!![i].destination != null) {
                            val destination = menu!![i].destination!!
                            findNavController(R.id.nav_host_fragment_content_main).navigate(
                                destination.id,
                                destination.params
                            )
                            break@parent
                        } else {
                            if (menu!![i].subMenu != null && menu!![i].subMenu!!.size > 0) {
                                if (menu!![i].subMenu!![0].destination != null) {
                                    val destination = menu!![i].subMenu!![0].destination!!
                                    findNavController(R.id.nav_host_fragment_content_main).navigate(
                                        destination.id,
                                        destination.params
                                    )
                                    break@parent
                                } else {
                                    supportFragmentManager.beginTransaction()
                                        .replace(
                                            R.id.nav_host_fragment_content_main,
                                            menu!![i].subMenu!![0].fragment!!
                                        )
                                        .commitAllowingStateLoss()
                                    title =
                                        SharedUtils.toTitleCase(menu!![i].subMenu!![0].subMenuTitle)
                                }
                            }
                        }
                    }
                }
                break
            } else {
                if (menu!![i].subMenu != null) {
                    var shouldBreakParentLoop = false
                    child@ for (j in menu!![i].subMenu!!.indices) {
                        if (itemString == menu!![i].subMenu!![j].subMenuTitle) {
                            if (itemString == PRE_ACCESS_MINE) {
                                val destination = menu!![i].subMenu!![j].destination!!
                                val myIntent =
                                    Intent(this@MainActivity, PreAccessMineActivity::class.java)
                                myIntent.putExtra("workerId", destination.idWorker);
                                myIntent.putExtra("workerName", SharedUtils.getUsuario(this));
                                startActivity(myIntent)
                                break@child
                            } else if (itemString == BOOK_COURSES) {
                                val destination = menu!![i].subMenu!![j].destination!!
                                val myIntent =
                                    Intent(this@MainActivity, BookCoursesActivity::class.java)
                                myIntent.putExtra("workerId", destination.idWorker);
                                myIntent.putExtra("idEnterprise", destination.idEnterprise);
                                myIntent.putExtra("filterRC", destination.filterRC);
                                startActivity(myIntent)
                                break@child
                            } else if (itemString == PRE_USO_CHECKLIST || itemString == QUERY_INSPECTION_PRE_USO) {
                                val destination = menu!![i].subMenu!![j].destination!!
                                val bundle = destination.params
                                val myIntent =
                                    Intent(this@MainActivity, CheckListPreUsoActivity::class.java)
                                myIntent.putExtra(
                                    "isConsultInspection",
                                    bundle?.getBoolean("isConsultInspection")
                                )
                                myIntent.putExtra("workerId", destination.idWorker)
                                startActivity(myIntent)
                                break@child
                            } else if (itemString == APPROVE_MOVEMENTS){
                                val intent =
                                    Intent(this@MainActivity, ApproveMovementsActivity::class.java)
                                intent.putExtra("workerId", SharedUtils.getUsuarioId(this@MainActivity));
                                startActivity(intent)
                                break@child
                            } else if (itemString == VEHICULAR_INSPECTION) {
                                val destination = menu!![i].subMenu!![j].destination!!
                                val bundle = destination.params
                                val myIntent =
                                    Intent(this@MainActivity, NewCheckListActivity::class.java)
                                myIntent.putExtra(
                                    BUNDLE_SIGNATURE,
                                    bundle?.getInt(BUNDLE_SIGNATURE, -1)
                                )
                                myIntent.putExtra(
                                    BUNDLE_NAME_INSPECTOR,
                                    bundle?.getString(BUNDLE_NAME_INSPECTOR, "No")
                                )
                                myIntent.putExtra(
                                    BUNDLE_SEARCH_INSPECTOR,
                                    bundle?.getBoolean(BUNDLE_SEARCH_INSPECTOR)
                                )
                                myIntent.putExtra(
                                    BUNDLE_TO_HISTORY,
                                    bundle?.getBoolean(BUNDLE_TO_HISTORY)
                                )
                                startActivity(myIntent)
                                break@child
                            } else if (itemString == (QUERY_INSPECTION)) {
                                val destination = menu!![i].subMenu!![j].destination!!
                                val bundle = destination.params
                                val myIntent =
                                    Intent(this@MainActivity, NewCheckListActivity::class.java)
                                myIntent.putExtra(
                                    BUNDLE_SIGNATURE,
                                    bundle?.getInt(BUNDLE_SIGNATURE, -1)
                                )
                                myIntent.putExtra(
                                    BUNDLE_NAME_INSPECTOR,
                                    bundle?.getString(BUNDLE_NAME_INSPECTOR, "No")
                                )
                                myIntent.putExtra(
                                    BUNDLE_SEARCH_INSPECTOR,
                                    bundle?.getBoolean(BUNDLE_SEARCH_INSPECTOR)
                                )
                                myIntent.putExtra(
                                    BUNDLE_FILTER_INSPECTOR,
                                    bundle?.getBoolean(BUNDLE_FILTER_INSPECTOR)
                                )
                                startActivity(myIntent)
                                break@child
                            } else if (menu!![i].subMenu!![j].destination != null) {
                                val destination = menu!![i].subMenu!![j].destination!!
                                findNavController(R.id.nav_host_fragment_content_main).navigate(
                                    destination.id,
                                    destination.params
                                )
                                shouldBreakParentLoop = true
                                break@child
                            }
                            supportFragmentManager.beginTransaction()
                                .replace(
                                    R.id.nav_host_fragment_content_main,
                                    menu!![i].subMenu!![j].fragment!!
                                )
                                .commitAllowingStateLoss()
                            title = SharedUtils.toTitleCase(menu!![i].subMenu!![j].subMenuTitle)
                            shouldBreakParentLoop = true
                            break@child
                        }
                    }
                    if (shouldBreakParentLoop) {
                        break@parent
                    }
                }
            }
        }
        if (drawer != null) {
            drawer!!.closeDrawer(GravityCompat.START)
        }
    }

    fun confirmSessionAbandon() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else if (!drawer.isDrawerOpen(GravityCompat.START)) {
            displaySignOutDialog()
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout

        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        if (fragment !is IOnBackPressed || !(fragment as IOnBackPressed).onBackPressed()) {
            //super.onBackPressed();
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawer!!.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (!SharedUtils.getSession(this)) {
            startActivity(Intent(this, LauncherActivity::class.java))
            finish()
        }
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0)
            lblTitle!!.text = SharedUtils.getUsuario(this)
            lblSubtitle!!.text = packageInfo!!.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun displaySignOutDialog() {
        MaterialDialog.Builder(this)
            .title("Salir")
            .content("¿Desea cerrar la aplicacion?")
            .cancelable(false)
            .positiveText("SI")
            .onPositive { dialog, which ->
                FirebaseCrashlytics.getInstance().setUserId("")
                DetachFirebaseTask().execute()
                SharedUtils.setSession(this@MainActivity, false)
                finish()
            }
            .negativeText("NO")
            .show()
    }
    private fun displayUpdateAppDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_comunicate)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))

        var message = dialog.findViewById<TextView>(R.id.textMessage)
        message.text =Html.fromHtml(getString(R.string.contentComunicate))
        var btnCancel = dialog.findViewById<Button>(R.id.btn_press)
        btnCancel.visibility=View.VISIBLE
        btnCancel.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(APP_BAMBAS_URL)))
        }
        dialog.show()
    }

    private class DetachFirebaseTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg strings: String): String? {
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }

    private fun syncMessages() {
        val idSync = App.db.messageDao().getMaxIdSync(SharedUtils.getUsuarioId(applicationContext))
        val api = RestClient.buildL()
        val call = api.getMessages(SharedUtils.getUsuarioId(applicationContext), idSync)
        call.enqueue(object : Callback<ApiResponse<List<Message>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Message>>>,
                response: Response<ApiResponse<List<Message>>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        for (i in result.data.indices) {
                            val msg = result.data[i]
                            val exist = App.db.messageDao().getOne(msg.id.toString())
                            if (exist != null)
                                App.db.messageDao()
                                    .updateMessage(msg.id, msg.estado, msg.isImportant, msg.idSync)
                            else {
                                msg.color =
                                    SharedUtils.getRandomMaterialColor(applicationContext, "400")
                                App.db.messageDao().insert(msg)
                            }
                        }
                    }
                } else {
                    SharedUtils.showToast(applicationContext, response.message())
                }
                setNavigationDrawerMenu()
            }

            override fun onFailure(call: Call<ApiResponse<List<Message>>>, t: Throwable) {
                SharedUtils.showToast(applicationContext, TAG + " syncMessages() " + t.message)
                setNavigationDrawerMenu()
            }
        })
    }

    private fun syncTokenAuthorizationMc() {
        try {
            val api = RestClient.buildMc()
            val call = api.getNewToken(object : HashMap<String, String>() {
                init {
                    put("WorkerId", SharedUtils.getUsuarioId(applicationContext))
                }
            })
            call.enqueue(object : Callback<ApiResponseAnglo<Any>> {
                override fun onResponse(
                    call: Call<ApiResponseAnglo<Any>>,
                    response: Response<ApiResponseAnglo<Any>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            if (result.data != null && result.data.toString() !== "")
                                SharedUtils.setTokenAuthorizationMc(
                                    applicationContext,
                                    result.data.toString()
                                )
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponseAnglo<Any>>, t: Throwable) {
                    SharedUtils.showToast(
                        applicationContext,
                        TAG + " syncTokenAuthorizationMc() " + t.message
                    )
                }
            })
        } catch (ex: Exception) {
            SharedUtils.showToast(
                applicationContext,
                TAG + " syncTokenAuthorizationMc() " + ex.message
            )
        }
    }

    private fun syncTokenAuthorization() {
        try {
            val api = RestClient.buildAnglo()
            val call = api.getNewToken(object : HashMap<String, String>() {
                init {
                    put("WorkerId", SharedUtils.getUsuarioId(applicationContext))
                }
            })
            call.enqueue(object : Callback<ApiResponseAnglo<Any>> {
                override fun onResponse(
                    call: Call<ApiResponseAnglo<Any>>,
                    response: Response<ApiResponseAnglo<Any>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            if (result.data != null && result.data.toString() !== "")
                                SharedUtils.setTokenAuthorization(
                                    applicationContext,
                                    result.data.toString()
                                )
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponseAnglo<Any>>, t: Throwable) {
                    SharedUtils.showToast(
                        applicationContext,
                        TAG + " syncTokenAuthorization() " + t.message
                    )
                }
            })
        } catch (ex: Exception) {
            SharedUtils.showToast(
                applicationContext,
                TAG + " syncTokenAuthorization() " + ex.message
            )
        }
    }

    fun navigateTo(fragment: Fragment, title: String, index: Int) {
        try {
            if (index > 0) {
                menu!![index].fragment?.let {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, it)
                        .commit()
                }
                setTitle(SharedUtils.toTitleCase(menu!![index].menuTitle))
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, fragment)
                    .commit()
                setTitle(SharedUtils.toTitleCase(title))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCurrentVersion(): String {
        try {
            var pInfo = packageManager.getPackageInfo(packageName, 0)
            return pInfo.versionName
        } catch (e: Throwable) {
            e.printStackTrace()
            return ""
        }
    }


    companion object {
        const val TWILIO_ACCESS_TOKEN = "TWILIO_ACCESS_TOKEN"
        const val TAG = "MainActivity"
        private const val APP_BAMBAS_URL = "https://play.google.com/store/apps/details?id=com.wcbambas.android"

    }
}