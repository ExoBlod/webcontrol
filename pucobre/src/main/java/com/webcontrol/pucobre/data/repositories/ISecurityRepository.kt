package com.webcontrol.pucobre.data.repositories

interface ISecurityRepository {
    suspend fun getToken(workerId: String): String
}