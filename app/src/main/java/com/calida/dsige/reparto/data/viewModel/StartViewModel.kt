package com.calida.dsige.reparto.data.viewModel

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calida.dsige.reparto.data.apiServices.ApiServices
import com.calida.dsige.reparto.data.apiServices.ConexionRetrofit
import com.calida.dsige.reparto.data.dao.interfaces.*
import com.calida.dsige.reparto.data.dao.overMethod.*
import com.calida.dsige.reparto.data.local.*
import com.calida.dsige.reparto.helper.MessageError
import com.calida.dsige.reparto.helper.Util
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class StartViewModel : ViewModel() {
    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()

    lateinit var realm: Realm
    lateinit var photoImp: PhotoImplementation
    lateinit var registroImp: RegistroImplementation
    lateinit var suministroImp: SuministroImplementation
    lateinit var loginImp: LoginImplementation
    lateinit var servicioImp: ServicioImplementation
    lateinit var inspeccionImp: InspeccionImp
    lateinit var apiServices: ApiServices

    fun initialRealm() {
        realm = Realm.getDefaultInstance()
        photoImp = PhotoOver(realm)
        registroImp = RegistroOver(realm)
        suministroImp = SuministroOver(realm)
        loginImp = LoginOver(realm)
        servicioImp = ServicioOver(realm)
        inspeccionImp = InspeccionOver(realm)
        apiServices = ConexionRetrofit.api.create(ApiServices::class.java)
    }

    fun setError(s: String) {
        mensajeError.value = s
    }

    val error: LiveData<String>
        get() = mensajeError

    val success: LiveData<String>
        get() = mensajeSuccess

    fun getLogin(): Login {
        return loginImp.login!!
    }

    fun getServicio(): RealmResults<Servicio> {
        return servicioImp.servicioAll
    }

    fun getGoTrabajo(usuarioId: Int, fecha: String, state: Int, name: String): Registro? {
        return registroImp.getGoTrabajo(usuarioId, fecha, state, name)
    }

    fun getLecturaOnCount(activo: Int, type: Int): Long? {
        return suministroImp.getLecturaOnCount(activo, type)
    }

    fun getLecturaReclamoOnCount(activo: Int, type: String): Long? {
        return suministroImp.getLecturaReclamoOnCount(activo, type)
    }

    fun getResultRegistro(): RealmResults<Registro> {
        return registroImp.getAllRegistro(1)
    }

    fun getResultRegistroLiveData(): LiveData<RealmResults<Registro>> {
        return registroImp.getAllRegistroLiveData(1)
    }

    fun getResultPhoto(): RealmResults<Photo> {
        return photoImp.getPhotoAll(1)
    }

    fun getPhotoAllLiveData(): LiveData<RealmResults<Photo>> {
        return photoImp.getPhotoAllLiveData(1)
    }

    fun getSuministroReparto(): RealmResults<Reparto> {
        return suministroImp.getSuministroReparto(1)
    }


    fun sendSelfie(context: Context, state: Int, name: String) {
        var mensaje = ""
        val auditorias = registroImp.getSelfie(state, name)
        auditorias.flatMap { a ->
            val realm = Realm.getDefaultInstance()
            val registroImpRx: RegistroImplementation = RegistroOver(realm)
            val b = MultipartBody.Builder()
            val filePaths: ArrayList<String> = ArrayList()
            var tieneFoto = 0
            var estado = "1"

            for (p: Photo in a.photos!!) {
                if (p.rutaFoto.isNotEmpty()) {
                    val file = File(Util.getFolder(context), p.rutaFoto)
//                    val file = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + p.rutaFoto)
                    if (file.exists()) {
                        filePaths.add(file.toString())
                        tieneFoto++
                    } else {
                        registroImpRx.closePhotoEstado(0, p)
                        estado = "0"
                    }
                }
            }

            for (i in 0 until filePaths.size) {
                val file = File(filePaths[i])
                b.addFormDataPart("fotos", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
            }

            val r = registroImpRx.updateRegistroTienePhoto(tieneFoto, estado, a)
            val json = Gson().toJson(realm.copyFromRealm(r))
            Log.i("TAG", json)
            b.setType(MultipartBody.FORM)
            b.addFormDataPart("model", json)

            val requestBody = b.build()
            Observable.zip(Observable.just(r), apiServices.sendRegistroRx(requestBody), BiFunction<Registro, Mensaje, Mensaje> { registro, mensaje ->
                registroImpRx.closeOneRegistro(registro, 0)
                mensaje
            })
        }.subscribeOn(Schedulers.computation())
                .delay(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Mensaje> {

                    override fun onSubscribe(d: Disposable) {
                        Log.i("TAG", d.toString())
                    }

                    override fun onNext(t: Mensaje) {
                        mensaje = t.mensaje
                    }

                    override fun onError(t: Throwable) {
                        if (t is HttpException) {
                            val body = t.response().errorBody()
                            val errorConverter: Converter<ResponseBody, MessageError> = ConexionRetrofit.api.responseBodyConverter(MessageError::class.java, arrayOfNulls<Annotation>(0))
                            try {
                                val error = errorConverter.convert(body!!)
                                mensajeError.postValue(error.Message)
                            } catch (e: IOException) {
                                e.printStackTrace()
                                mensajeError.postValue(e.toString())
                            }
                        } else {
                            mensajeError.postValue(t.toString())
                        }
                    }

                    override fun onComplete() {
                        mensajeSuccess.postValue(mensaje)
                    }
                })
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }


    fun getPhotoIdentity(): Int {
        return photoImp.getPhotoIdentity()
    }

    fun getRegistroIdentity(): Int {
        return registroImp.getRegistroIdentity()
    }

    fun saveZonaPeligrosa(registro: Registro) {
        registroImp.saveZonaPeligrosa(registro)
    }

    fun getInspecciones(usuarioId: Int, i: Int, active: Int): RealmResults<Inspeccion> {
        return inspeccionImp.getInspecciones(usuarioId, i, active)
    }

    fun getInspecciones(usuarioId: Int, i: Int): RealmResults<Inspeccion> {
        return inspeccionImp.getInspecciones(usuarioId, i)
    }

    val servicioAll: RealmResults<Servicio>
        get() = servicioImp.servicioAll

    fun getOperarioById(usuarioId: Int): Observable<Mensaje> {
        return apiServices.getOperarioById(usuarioId)
    }

    fun updateLogin(codigo: Int) {
        loginImp.updateLogin(codigo)
    }

    fun getInicioFinTrabajo(usuarioId: Int, fecha: String, s: String): Registro? {
        return registroImp.getInicioFinTrabajo(usuarioId, fecha, s)
    }

    fun getRepartoActive(activo: Int?): Long? {
        return suministroImp.getRepartoActive(activo)
    }

    fun getLecturaRecuperadas(s: String, s1: String, i: Int, i1: Int): Long {
        return suministroImp.getLecturaRecuperadas(s, s1, i, i1)
    }

    fun getLecturaObservadas(activo: Int, observadas: Int): Long? {
        return suministroImp.getLecturaObservadas(activo, observadas)
    }

    fun getVerificateInspecciones(id: Int, fecha: String): Observable<Mensaje> {
        return apiServices.getVerificateInspecciones(id, fecha)
    }


    fun getVerification(fecha: String,usuarioId: Int): Verification? {
        return inspeccionImp.getVerification(fecha,usuarioId)
    }

    fun insertVerification(fecha: String,usuarioId:Int) {
        inspeccionImp.insertVerification(fecha,usuarioId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Log.i("TAG", e.toString())
                    }
                })
    }
}