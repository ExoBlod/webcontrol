package com.webcontrol.pucobre.data.repositories

import com.webcontrol.pucobre.data.model.WorkerCredential

interface ICredentialRepository {
    suspend fun getCredential(workerId: String): WorkerCredential?
}