package com.webcontrol.angloamerican.data.repositories

interface ISecurityRepository {
    suspend fun getToken(workerId: String): String
}