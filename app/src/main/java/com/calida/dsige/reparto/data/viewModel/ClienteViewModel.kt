package com.calida.dsige.reparto.data.viewModel

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calida.dsige.reparto.data.apiServices.ApiServices
import com.calida.dsige.reparto.data.apiServices.ConexionRetrofit
import com.calida.dsige.reparto.data.dao.interfaces.ComboImplementation
import com.calida.dsige.reparto.data.dao.interfaces.RegistroImplementation
import com.calida.dsige.reparto.data.dao.interfaces.SuministroImplementation
import com.calida.dsige.reparto.data.dao.overMethod.ComboOver
import com.calida.dsige.reparto.data.dao.overMethod.RegistroOver
import com.calida.dsige.reparto.data.dao.overMethod.SuministroOver
import com.calida.dsige.reparto.data.local.GrandesClientes
import com.calida.dsige.reparto.data.local.Marca
import com.calida.dsige.reparto.data.local.Mensaje
import com.calida.dsige.reparto.data.local.Observaciones
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

class ClienteViewModel : ViewModel() {

    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()

    lateinit var realm: Realm
    lateinit var registroImp: RegistroImplementation
    lateinit var suministroImp: SuministroImplementation
    lateinit var comboImplementation: ComboImplementation
    lateinit var apiServices: ApiServices

    fun initialRealm() {
        realm = Realm.getDefaultInstance()
        registroImp = RegistroOver(realm)
        suministroImp = SuministroOver(realm)
        comboImplementation = ComboOver(realm)
        apiServices = ConexionRetrofit.api.create(ApiServices::class.java)
    }

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getGrandesClientes(): RealmResults<GrandesClientes> {
        return suministroImp.getGrandesClientes()
    }

    fun getClienteById(id: Int): GrandesClientes {
        return suministroImp.getClienteById(id)
    }

    fun getMarca(): RealmResults<Marca> {
        return suministroImp.getMarca()
    }

    fun getObservaciones(): RealmResults<Observaciones> {
        return comboImplementation.getObservacion()
    }

    fun getNameMarcaById(id: Int): String {
        return suministroImp.getMarcaById(id).nombre
    }

    fun validateCliente(c: GrandesClientes, type: Int, format: Int): Boolean {
        if (format == 1) {
            if (c.fechaRegistroInicio.isEmpty() || c.fechaRegistroInicio == "01/01/1900") {
                mensajeError.value = "Iniciar fecha de Inicio"
                return false
            }

            if (c.clientePermiteAcceso.isEmpty()) {
                mensajeError.value = "Seleccione tipo de cliente"
                return false
            }
        } else {
            for (x in 0..type) {
                when (x) {
                    0 -> {
                        if (c.fechaRegistroInicio.isEmpty() || c.fechaRegistroInicio == "01/01/1900") {
                            mensajeError.value = "Iniciar fecha de Inicio"
                            return false
                        }
                    }
                    1 -> {
                        if (c.fotoInicioTrabajo.isEmpty()) {
                            mensajeError.value = "Foto de inicio de trabajo"
                            return false
                        }
                        if (c.clientePermiteAcceso.isEmpty()) {
                            mensajeError.value = "Seleccione tipo de cliente"
                            return false
                        }

                        if (c.clientePermiteAcceso == "NO") {
                            return if (c.fotoConstanciaPermiteAcceso.isEmpty()) {
                                mensajeError.value = "Tomar foto del cliente no permite acceso"
                                false
                            } else {
                                true
                            }
                        }
                    }
                    2 -> {
                        if (c.porMezclaExplosiva.isEmpty()) {
                            mensajeError.value = "Ingresar Mezcla explosiva."
                            return false
                        }

                        if (c.vManoPresionEntrada.isEmpty()) {
                            mensajeError.value = "Ingresar Valor manometro presión entrada."
                            return false
                        }
                    }
                    3 -> {
                        if (c.fotovManoPresionEntrada.isEmpty()) {
                            mensajeError.value = "Tomar foto valor manometro presión entrada."
                            return false
                        }
                        if (c.marcaCorrectorId == 0) {
                            mensajeError.value = "Ingresar Marca de corrector."
                            return false
                        }
                        if (c.vVolumenSCorreMedidor.isEmpty()) {
                            mensajeError.value = "Ingresar valor de volumen sin corregir del medidor."
                            return false
                        }
                    }
                    4 -> {
                        if (c.observacionId == 0) {
                            mensajeError.value = "Seleccione alguna observación."
                            return false
                        }
                        if (c.observacionId == 1) {
                            if (c.fotovVolumenSCorreMedidor.isEmpty()) {
                                mensajeError.value = "Ingresar foto de valor de volumen sin corregir del medidor."
                                return false
                            }
                            if (c.vVolumenSCorreUC.isEmpty()) {
                                mensajeError.value = "Ingresar valor de volumen sin corregir de la unidad correctora."
                                return false
                            }
                            val a = c.vVolumenSCorreMedidor.toInt() - c.vVolumenSCorreUC.toInt()
                            if (a <= 0 || a > 10) {
                                if (c.confirmarVolumenSCorreUC != c.vVolumenSCorreUC) {
                                    mensajeError.value = "Confirmar lectura"
                                    return false
                                }
                            }
                        }
                    }
                    5 -> {
                        if (c.observacionId == 1) {
                            if (c.fotovVolumenSCorreUC.isEmpty()) {
                                mensajeError.value = "Ingresar foto valor de volumen sin corregir de la unidad correctora."
                                return false
                            }


                            if (c.vVolumenRegUC.isEmpty()) {
                                mensajeError.value = "Ingresar valor de volumen registrador de la unidad correctora."
                                return false
                            }
                        }
                    }
                    6 -> {
                        if (c.observacionId == 1) {
                            if (c.fotovVolumenRegUC.isEmpty()) {
                                mensajeError.value = "Ingresar foto valor de volumen registrador de la unidad correctora."
                                return false
                            }
                            if (c.vPresionMedicionUC.isEmpty()) {
                                mensajeError.value = "Ingresar valor de la presión de medición de uc."
                                return false
                            }
                        }
                    }
                    7 -> {
                        if (c.observacionId == 1) {
                            if (c.fotovPresionMedicionUC.isEmpty()) {
                                mensajeError.value = "Ingresar foto valor de la presión de medición de uc."
                                return false
                            }
                            if (c.vTemperaturaMedicionUC.isEmpty()) {
                                mensajeError.value = "Ingresar valor de la temperatura de medicion de la uc."
                                return false
                            }
                        }
                    }
                    8 -> {
                        if (c.observacionId == 1) {
                            if (c.marcaCorrectorId != 1) {
                                if (c.fotovTemperaturaMedicionUC.isEmpty()) {
                                    mensajeError.value = "Tomar foto de la temperatura de medicion de la uc."
                                    return false
                                }
                            }
                            if (c.tiempoVidaBateria.isEmpty()) {
                                mensajeError.value = "Ingresar tiempo de vida de la bateria."
                                return false
                            }
                        }
                    }
                    9 -> {
                        if (c.observacionId == 1) {
                            if (c.marcaCorrectorId != 1) {
                                if (c.fotoTiempoVidaBateria.isEmpty()) {
                                    mensajeError.value = "Tomar foto tiempo de vida de la bateria."
                                    return false
                                }
                            }
                        }
                    }
                    10 -> {
                        if (c.fotoPanomarica.isEmpty()) {
                            mensajeError.value = "Ingresar foto panoramica."
                            return false
                        }
                    }
                    11 -> {
                        if (c.tieneGabinete == "SI") {
                            if (c.foroSitieneGabinete.isEmpty()) {
                                mensajeError.value = "Tomar Foto del Cabinete."
                                return false
                            }
                        }
                        if (c.presenteCliente.isEmpty()) {
                            mensajeError.value = "Ingresar si presenta cliente."
                            return false
                        }
                        if (c.presenteCliente == "SI") {
                            if (c.contactoCliente.isEmpty()) {
                                mensajeError.value = "Ingresar nombre del cliente"
                                return false
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    fun updateCliente(context: Context, c: GrandesClientes, mensaje: String) {
        registroImp.updateClientes(c)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        if (mensaje == "Cliente Actualizado") {
                            sendCliente(context, c.clienteId, mensaje)
                        } else {
                            mensajeSuccess.value = mensaje
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }
                })
    }

    private fun sendCliente(context: Context, clienteId: Int, mensaje: String) {
        val auditorias = registroImp.getClienteById(clienteId)
        auditorias.flatMap { c ->
            val realm = Realm.getDefaultInstance()
            val b = MultipartBody.Builder()
            if (c.clientePermiteAcceso == "NO") {
                val file = File(Util.getFolder(context), c.fotoConstanciaPermiteAcceso)
//                val file = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotoConstanciaPermiteAcceso)
                if (file.exists()) {
                    b.addFormDataPart("fotos", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
                }
            } else {
                if (c.fotovManoPresionEntrada.isNotEmpty()) {
                    val file = File(Util.getFolder(context), c.fotovManoPresionEntrada)
//                    val file = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotovManoPresionEntrada)
                    if (file.exists()) {
                        b.addFormDataPart("fotos", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
                    }
                }
                if (c.fotovVolumenSCorreUC.isNotEmpty()) {
                    val file2 = File(Util.getFolder(context), c.fotovVolumenSCorreUC)
//                    val file2 = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotovVolumenSCorreUC)
                    if (file2.exists()) {
                        b.addFormDataPart("fotos", file2.name, RequestBody.create(MediaType.parse("multipart/form-data"), file2))
                    }
                }
                if (c.fotovVolumenSCorreMedidor.isNotEmpty()) {
                    val file3 = File(Util.getFolder(context), c.fotovVolumenSCorreMedidor)
//                    val file3 = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotovVolumenSCorreMedidor)
                    if (file3.exists()) {
                        b.addFormDataPart("fotos", file3.name, RequestBody.create(MediaType.parse("multipart/form-data"), file3))
                    }
                }
                if (c.fotovVolumenRegUC.isNotEmpty()) {
                    val file4 = File(Util.getFolder(context), c.fotovVolumenRegUC)
//                    val file4 = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotovVolumenRegUC)
                    if (file4.exists()) {
                        b.addFormDataPart("fotos", file4.name, RequestBody.create(MediaType.parse("multipart/form-data"), file4))
                    }
                }
                if (c.fotovPresionMedicionUC.isNotEmpty()) {
                    val file5 = File(Util.getFolder(context), c.fotovPresionMedicionUC)
//                    val file5 = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotovPresionMedicionUC)
                    if (file5.exists()) {
                        b.addFormDataPart("fotos", file5.name, RequestBody.create(MediaType.parse("multipart/form-data"), file5))
                    }
                }
                if (c.fotoTiempoVidaBateria.isNotEmpty()) {
                    val file6 = File(Util.getFolder(context), c.fotoTiempoVidaBateria)
//                    val file6 = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotoTiempoVidaBateria)
                    if (file6.exists()) {
                        b.addFormDataPart("fotos", file6.name, RequestBody.create(MediaType.parse("multipart/form-data"), file6))
                    }
                }
                if (c.fotovTemperaturaMedicionUC.isNotEmpty()) {
                    val file7 = File(Util.getFolder(context), c.fotovTemperaturaMedicionUC)
//                    val file7 = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotovTemperaturaMedicionUC)
                    if (file7.exists()) {
                        b.addFormDataPart("fotos", file7.name, RequestBody.create(MediaType.parse("multipart/form-data"), file7))
                    }
                }
                if (c.fotoPanomarica.isNotEmpty()) {
                    val file8 = File(Util.getFolder(context), c.fotoPanomarica)
//                    val file8 = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.fotoPanomarica)
                    if (file8.exists()) {
                        b.addFormDataPart("fotos", file8.name, RequestBody.create(MediaType.parse("multipart/form-data"), file8))
                    }
                }
                if (c.foroSitieneGabinete.isNotEmpty()) {
                    val file9 = File(Util.getFolder(context), c.foroSitieneGabinete)
//                    val file9 = File(Environment.getExternalStorageDirectory().toString() + "/" + Util.FolderImg + "/" + c.foroSitieneGabinete)
                    if (file9.exists()) {
                        b.addFormDataPart("fotos", file9.name, RequestBody.create(MediaType.parse("multipart/form-data"), file9))
                    }
                }
            }
            val json = Gson().toJson(realm.copyFromRealm(c))
            Log.i("TAG", json)
            b.setType(MultipartBody.FORM)
            b.addFormDataPart("model", json)
            val requestBody = b.build()
            Observable.zip(Observable.just(c), apiServices.sendCliente(requestBody), BiFunction<GrandesClientes, Mensaje, Mensaje> { _, mensaje ->
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

    fun verificateFile(c: GrandesClientes) {
        apiServices.getVerificateFile(c.clienteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Mensaje> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Mensaje) {
                        closeFileClienteById(c.clienteId)
                        mensajeSuccess.postValue(t.mensaje)
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
                            }
                        } else {
                            mensajeError.postValue(t.toString())
                        }
                    }

                })
    }

    private fun closeFileClienteById(id: Int) {
        registroImp.closeFileClienteById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        Log.i("TAG", "File Actualizado")
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {

                    }
                })
    }
}