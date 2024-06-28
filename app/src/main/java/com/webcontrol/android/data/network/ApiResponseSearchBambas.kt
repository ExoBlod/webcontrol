package com.webcontrol.android.data.network

data class ApiResponseSearchBambas (
    val id:Int=0,
    val personId:Int=0,
    val workerId:String="",
    val supervisorName:String = "",
    val supervisorLastName:String = "",
    val isSignature:Int=0,
    val inspectionQuery: Boolean=false,
    val toHistory: Boolean = false,
    val filter:Boolean = false,
    val credentialQuery:Boolean = false,
    var isSearching: Boolean = false,
    var isDriver: Boolean? = false
    )