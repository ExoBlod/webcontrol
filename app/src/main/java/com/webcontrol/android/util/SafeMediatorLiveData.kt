package com.webcontrol.android.util

import androidx.lifecycle.MediatorLiveData

class SafeMediatorLiveData<T : Any>(initialValue: T) : MediatorLiveData<T>() {

    init {
        value = initialValue
    }

    override fun getValue(): T {
        return super.getValue()!!
    }
}