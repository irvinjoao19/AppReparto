package com.calida.dsige.reparto.data.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calida.dsige.reparto.data.apiServices.ApiServices
import com.calida.dsige.reparto.data.apiServices.ConexionRetrofit
import com.calida.dsige.reparto.data.dao.interfaces.PhotoImplementation
import com.calida.dsige.reparto.data.dao.interfaces.RegistroImplementation
import com.calida.dsige.reparto.data.dao.interfaces.RepartoImplementation
import com.calida.dsige.reparto.data.dao.interfaces.SuministroImplementation
import com.calida.dsige.reparto.data.dao.overMethod.PhotoOver
import com.calida.dsige.reparto.data.dao.overMethod.RegistroOver
import com.calida.dsige.reparto.data.dao.overMethod.RepartoOver
import com.calida.dsige.reparto.data.dao.overMethod.SuministroOver
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

class EnvioViewModel : ViewModel() {

    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()

    lateinit var realm: Realm
    lateinit var photoImp: PhotoImplementation
    lateinit var registroImp: RegistroImplementation
    lateinit var suministroImp: SuministroImplementation
    lateinit var repartoImp: RepartoImplementation
    lateinit var apiServices: ApiServices

    fun initialRealm() {
        realm = Realm.getDefaultInstance()
        photoImp = PhotoOver(realm)
        registroImp = RegistroOver(realm)
        suministroImp = SuministroOver(realm)
        repartoImp = RepartoOver(realm)
        apiServices = ConexionRetrofit.api.create(ApiServices::class.java)
    }

    fun setError(s: String) {
        mensajeError.value = s
    }

    val error: LiveData<String>
        get() = mensajeError

    val success: LiveData<String>
        get() = mensajeSuccess

    fun getResultPhoto(): RealmResults<RepartoCargoFoto> {
        return repartoImp.getPhotoAll(1)
    }

    fun getSuministroRepartoLiveData(): LiveData<RealmResults<Reparto>> {
        return suministroImp.getSuministroRepartoLiveData(1)
    }

    fun sendData(context: Context) {
        val auditorias = registroImp.getAllRegistroRx(1)
        auditorias.flatMap { observable ->
            Observable.fromIterable(observable).flatMap { a ->
                val realm = Realm.getDefaultInstance()
                val registroImp: RegistroImplementation = RegistroOver(realm)
                val b = MultipartBody.Builder()
                val filePaths: ArrayList<String> = ArrayList()
                for (p: Photo in a.photos!!) {
                    if (p.rutaFoto.isNotEmpty()) {
                        val file = File(Util.getFolder(context), p.rutaFoto)
                        if (file.exists()) {
                            filePaths.add(file.toString())
                        }
                    }
                }

                for (i in 0 until filePaths.size) {
                    val file = File(filePaths[i])
                    b.addFormDataPart("fotos", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
                }

                val json = Gson().toJson(realm.copyFromRealm(a))
                Log.i("TAG", json)
                b.setType(MultipartBody.FORM)
                b.addFormDataPart("model", json)
                val requestBody = b.build()
                Observable.zip(Observable.just(a), apiServices.sendRegistroRx(requestBody), BiFunction<Registro, Mensaje, Mensaje> { registro, mensaje ->
                    registroImp.closeOneRegistro(registro, 0)
                    mensaje
                })
            }
        }.subscribeOn(Schedulers.computation())
                .delay(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Mensaje> {

                    override fun onSubscribe(d: Disposable) {
                        Log.i("TAG", d.toString())
                    }

                    override fun onNext(t: Mensaje) {
                        Log.i("TAG", t.mensaje)
                    }

                    override fun onError(t: Throwable) {
                        if (t is HttpException) {
                            val body = t.response().errorBody()
                            val errorConverter: Converter<ResponseBody, MessageError> = ConexionRetrofit.api.responseBodyConverter(MessageError::class.java, arrayOfNulls<Annotation>(0))
                            try {
                                val error = errorConverter.convert(body!!)
                                mensajeError.postValue(error.Message)
                            } catch (e: IOException) {
                                mensajeError.postValue(e.toString())
                            }
                        } else {
                            mensajeError.postValue(t.toString())
                        }
                    }

                    override fun onComplete() {
                        mensajeSuccess.postValue("Registros Enviados")
                    }
                })
    }

    fun sendDataReparto(context: Context) {
        val auditorias = repartoImp.getAllRegistroRx(2)
        auditorias.flatMap { observable ->
            Observable.fromIterable(observable).flatMap { a ->
                val realm = Realm.getDefaultInstance()
//                val repartoImp: RepartoImplementation = RepartoOver(realm)
                val b = MultipartBody.Builder()
                val filePaths: ArrayList<String> = ArrayList()
                for (p: RepartoCargoFoto in a.photos!!) {
                    if (p.rutaFoto.isNotEmpty()) {
                        val file = File(Util.getFolder(context), p.rutaFoto)
                        if (file.exists()) {
                            filePaths.add(file.toString())
                        }
                    }
                }

                for (i in 0 until filePaths.size) {
                    val file = File(filePaths[i])
                    b.addFormDataPart("fotos", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
                }

                val json = Gson().toJson(realm.copyFromRealm(a))
                Log.i("TAG", json)
                b.setType(MultipartBody.FORM)
                b.addFormDataPart("model", json)
                val requestBody = b.build()
                Observable.zip(Observable.just(a), apiServices.sendReparto(requestBody), BiFunction<RepartoCargo, Mensaje, Mensaje> { registro, mensaje ->
//                    repartoImp.closeOneRegistro(registro, 0)
                    mensaje
                })
            }
        }.subscribeOn(Schedulers.computation())
                .delay(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Mensaje> {

                    override fun onSubscribe(d: Disposable) {
                        Log.i("TAG", d.toString())
                    }

                    override fun onNext(t: Mensaje) {
                        updateRegistro(t.codigo)
                        Log.i("TAG", t.mensaje)
                    }

                    override fun onError(t: Throwable) {
                        if (t is HttpException) {
                            val body = t.response().errorBody()
                            val errorConverter: Converter<ResponseBody, MessageError> = ConexionRetrofit.api.responseBodyConverter(MessageError::class.java, arrayOfNulls<Annotation>(0))
                            try {
                                val error = errorConverter.convert(body!!)
                                mensajeError.postValue(error.Message)
                            } catch (e: IOException) {
                                mensajeError.postValue(e.toString())
                            }
                        } else {
                            mensajeError.postValue(t.toString())
                        }
                    }

                    override fun onComplete() {
                        mensajeSuccess.postValue("Registros Enviados")
                    }
                })
    }

    private fun updateRegistro(codigo: Int) {
        repartoImp.updateRepartoEnvio(codigo)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                    }
                })
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    fun sendPhoto(context: Context) {
        val auditorias = registroImp.getAllPhotos(context)
        auditorias.flatMap { observable ->
            Observable.fromIterable(observable).flatMap { i ->
                val b = MultipartBody.Builder()
                val file = File(i)
                if (file.exists()) {
                    b.addFormDataPart("fotos", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
                }
                b.setType(MultipartBody.FORM)
                val requestBody = b.build()
                Observable.zip(Observable.just(i), apiServices.sendPhoto(requestBody), BiFunction<String, Mensaje, Mensaje> { registro, mensaje ->
                    mensaje
                })
            }
        }.subscribeOn(Schedulers.computation())
                .delay(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Mensaje> {

                    override fun onSubscribe(d: Disposable) {
                        Log.i("TAG", d.toString())
                    }

                    override fun onNext(t: Mensaje) {
                        Log.i("TAG", t.mensaje)
                    }

                    override fun onError(t: Throwable) {
                        if (t is HttpException) {
                            val body = t.response().errorBody()
                            val errorConverter: Converter<ResponseBody, MessageError> = ConexionRetrofit.api.responseBodyConverter(MessageError::class.java, arrayOfNulls<Annotation>(0))
                            try {
                                val error = errorConverter.convert(body!!)
                                mensajeError.postValue(error.Message)
                            } catch (e: IOException) {
                                mensajeError.postValue(e.toString())
                            }
                        } else {
                            mensajeError.postValue(t.toString())
                        }
                    }

                    override fun onComplete() {
                        mensajeSuccess.postValue("Fotos Enviados")
                    }
                })
    }

    fun getResultReparto(): RealmResults<RepartoCargo> {
        return repartoImp.getResultReparto(2)
    }

    fun getResultRegistro(): RealmResults<Registro> {
        return registroImp.getAllRegistro(1)
    }
}