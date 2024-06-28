package com.webcontrol.android.ui.common.widgets

import com.webcontrol.android.ui.common.enums.SettingOption
import com.webcontrol.core.common.TypeFactory
import com.webcontrol.core.common.Visitable

data class SettingItem(
    val option: SettingOption,
    val title: String,
    val description: String,
    val icon: Int
) : Visitable<SettingItem> {
    override fun type(typeFactory: TypeFactory<SettingItem>): Int {
        return typeFactory.type(this)
    }
}
