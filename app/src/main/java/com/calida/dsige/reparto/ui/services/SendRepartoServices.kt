package com.calida.dsige.reparto.ui.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import com.calida.dsige.reparto.data.apiServices.ApiServices
import com.calida.dsige.reparto.data.apiServices.ConexionRetrofit
import com.calida.dsige.reparto.data.dao.interfaces.RegistroImplementation
import com.calida.dsige.reparto.data.dao.overMethod.RegistroOver
import com.calida.dsige.reparto.data.local.Mensaje
import com.calida.dsige.reparto.data.local.Photo
import com.calida.dsige.reparto.data.local.Registro
import com.calida.dsige.reparto.helper.Util
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class SendRepartoServices : Service() {

    private val timer = Timer()

    override fun onBind(intent: Intent): IBinder? {
        Log.i("service", "Close DistanceService2")
        return null
    }

    override fun onCreate() {
        Log.i("service", "Iniciando DistanceService")
        super.onCreate()
    }

    private fun startService() {
//        stopService(Intent(this, AlertRepartoSleepService::class.java))
        timer.scheduleAtFixedRate(mainTask(), 0, 600000)
    }

    private inner class mainTask : TimerTask() {
        override fun run() {
            toastHandler.sendEmptyMessage(0)
        }
    }

    private val toastHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            val realm = Realm.getDefaultInstance()
            val registroImp = RegistroOver(realm)
            val sendInterfaces = ConexionRetrofit.api.create(ApiServices::class.java)
            sendDataRx(this@SendRepartoServices,registroImp, sendInterfaces)
        }
    }

    override fun onDestroy() {
        timer.cancel()
        Log.i("service", "Close DistanceService")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startService()
        return START_STICKY
    }

    private fun sendDataRx(context: Context, registroImp: RegistroImplementation, sendInterfaces: ApiServices) {
        val auditorias = registroImp.getAllRegistroRx(1)
        auditorias.flatMap { observable ->
            Observable.fromIterable(observable).flatMap { a ->

                val realm = Realm.getDefaultInstance()
                val registroImpRx: RegistroImplementation = RegistroOver(realm)
                val b = MultipartBody.Builder()
                val filePaths: ArrayList<String> = ArrayList()

                for (p: Photo in a.photos!!) {
                    if (p.rutaFoto.isNotEmpty()) {
//                        val file = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + p.rutaFoto)
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
                Observable.zip(Observable.just(a), sendInterfaces.sendRegistroRx(requestBody), BiFunction<Registro, Mensaje, Mensaje> { registro, mensaje ->
                    registroImpRx.closeOneRegistro(registro, 0)
                    mensaje
                })
            }
        }.subscribeOn(Schedulers.io())
                .delay(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Mensaje> {

                    override fun onSubscribe(d: Disposable) {
                        Log.i("TAG", d.toString())
                    }

                    override fun onNext(t: Mensaje) {
                        Log.i("TAG", "ENVIO REPARTO")
                    }

                    override fun onError(e: Throwable) {
                        Log.i("TAG", e.message.toString())
                    }

                    override fun onComplete() {

                    }
                })
    }
}