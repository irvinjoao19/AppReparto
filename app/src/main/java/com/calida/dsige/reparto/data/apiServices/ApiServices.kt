package com.calida.dsige.reparto.data.apiServices

import com.calida.dsige.reparto.data.local.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiServices {

    @Headers("Cache-Control: no-cache")
    @GET("Migration/GetLogin")
    fun getLogin(@Query("user") user: String,
                 @Query("password") password: String,
                 @Query("imei") imei: String,
                 @Query("version") version: String,
                 @Query("token") token: String): Call<Login>

    @Headers("Cache-Control: no-cache")
    @GET("Migration/MigracionAll")
    fun getMigration(@Query("operarioId") operarioId: Int, @Query("version") version: String): Call<Migration>

    @Headers("Cache-Control: no-cache")
    @GET("Migration/SincronizarObservadas")
    fun getObservadas(@Query("operarioId") operarioId: Int): Call<List<SuministroLectura>>


    @Headers("Cache-Control: no-cache")
    @GET("Migration/GetOperarios")
    fun getOperarios(): Call<List<Operario>>

    @Headers("Cache-Control: no-cache")
    @GET("Migration/UpdateOperario")
    fun updateOperario(@Query("operarioId") operarioId: Int,
                       @Query("lecturaManual") valor: Int): Call<Mensaje>

    @Headers("Cache-Control: no-cache")
    @GET("Migration/GetOperarioById")
    fun getOperarioById(@Query("operarioId") operarioId: Int): Observable<Mensaje>


    @POST("Migration/SaveRegistro")
    fun sendRegistroByOne(@Body registro: RequestBody): Call<Mensaje>

    @Multipart
    @POST("Migration/SavePhoto")
    fun sendPhoto(@Part file: MultipartBody.Part): Call<Mensaje>

    @GET("Migration/VerificarCorte")
    fun sendVerificarCorte(@Query("suministro") suministro: String): Call<Mensaje>


    // TODO  NUEVA VERSION 5.0.6

    @Headers("Cache-Control: no-cache")
    @POST("Migration/SaveNew")
    fun sendRegistroRx(@Body query: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("Migration/Photos")
    fun sendPhoto(@Body query: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("Migration/SaveRegistroCorteNew")
    fun sendRegistroCorteRx(@Body query: RequestBody): Observable<Mensaje>

    @POST("Migration/SaveEstadoMovil")
    fun saveEstadoMovil(@Body movil: RequestBody): Call<Mensaje>

    @POST("Migration/SaveOperarioGps")
    fun saveOperarioGps(@Body gps: RequestBody): Call<Mensaje>

    @GET("Migration/SincronizarCorteReconexion")
    fun syncCorteReconexion(@Query("operarioId") operarioId: Int): Call<Sincronizar>

    // TODO NUEVO INSPECCION

    //    @POST("Migration/InspeccionNew")
    @POST("Migration/InspeccionNewAsync")
    fun saveInspeccion(@Body inspeccion: RequestBody): Observable<Mensaje>

    // TODO BIG CLIENTES

    @Headers("Cache-Control: no-cache")
    @POST("Migration/SaveClienteNew")
    fun sendCliente(@Body query: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @GET("Migration/VerificateFileCliente")
    fun getVerificateFile(@Query("id") file: Int): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @GET("Migration/VerificateInspecciones")
    fun getVerificateInspecciones(@Query("operarioId") id: Int, @Query("fecha") fecha: String): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("Migration/SaveReparto")
    fun sendReparto(@Body query: RequestBody): Observable<Mensaje>
}