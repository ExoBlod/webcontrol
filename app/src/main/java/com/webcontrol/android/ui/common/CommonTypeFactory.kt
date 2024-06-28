package com.webcontrol.android.ui.common

import com.webcontrol.android.R
import com.webcontrol.android.ui.common.widgets.SettingItem
import com.webcontrol.core.common.TypeFactory

class SettingItemTypeFactory : TypeFactory<SettingItem> {
    override fun type(item: SettingItem): Int {
        return R.layout.row_setting
    }
}