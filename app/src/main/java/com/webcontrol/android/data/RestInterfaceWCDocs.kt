package com.webcontrol.android.data

import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.data.network.dto.InsertDocDTO
import com.webcontrol.android.data.network.dto.OwnDocs
import com.webcontrol.android.data.network.dto.PrincipalOwnDocs
import com.webcontrol.android.data.network.dto.TypeOwnDocs
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface RestInterfaceWCDocs {

    @POST("docs_personales/saveDoc")
    @Multipart
    fun insertFileOwnDocs(
        @Part("ID") id: String,
        @Part("CARA") cara: Int,
        @Part ARCHIVO: MultipartBody.Part
    ): Call<ApiResponseAnglo<String>>

    @POST("docs_personales/InsertDocs_Personales")
    fun insertOwnDocs(@Body ownDocs: InsertDocDTO): Call<Any>

    @GET("mandantes/getmandantes")
    fun getPrincipals(): Call<List<PrincipalOwnDocs>>

    @GET("docs_personales/GetDocs_Personales/{rut}")
    fun getOwnDocs(@Path("rut") rut: String): Call<List<OwnDocs>>

    @GET("docs_personales/GetDocs_Personales_ByID/{id}")
    fun getOwnDocsById(@Path("id") id: String): Call<List<OwnDocs>>

    @GET("tipo_docs_personales/GetTipo_Docs_Personales")
    fun getTypeOwnDocs(): Call<List<TypeOwnDocs>>

}