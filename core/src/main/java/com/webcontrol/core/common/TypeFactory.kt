package com.webcontrol.core.common

interface TypeFactory<T> {
    fun type(item: T): Int
}
