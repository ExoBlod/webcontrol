package com.webcontrol.core.common

interface Visitable<T> {
    fun type(typeFactory: TypeFactory<T>): Int
}