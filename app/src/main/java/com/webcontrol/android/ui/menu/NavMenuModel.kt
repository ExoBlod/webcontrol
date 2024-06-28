package com.webcontrol.android.ui.menu

import android.os.Bundle
import androidx.fragment.app.Fragment

data class NavMenuModel(
    var index: Int,
    var menuTitle: String,
    var menuIconDrawable: Int,
    var subMenu: MutableList<SubMenuModel>? = null,
    var fragment: Fragment? = null,
    var destination: Destination? = null
)

data class SubMenuModel(
    var subMenuTitle: String,
    var fragment: Fragment? = null,
    var count: Int = 0,
    var destination: Destination? = null
)

data class Destination(
    var id: Int,
    var params: Bundle? = null,
    var idWorker: String? = null,
    var idEnterprise: String? = null,
    var filterRC: Int? = null
)