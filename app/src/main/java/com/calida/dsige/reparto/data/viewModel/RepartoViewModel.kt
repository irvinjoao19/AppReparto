package com.calida.dsige.reparto.data.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calida.dsige.reparto.data.apiServices.ApiServices
import com.calida.dsige.reparto.data.apiServices.ConexionRetrofit
import com.calida.dsige.reparto.data.dao.interfaces.ComboImplementation
import com.calida.dsige.reparto.data.dao.interfaces.RepartoImplementation
import com.calida.dsige.reparto.data.dao.overMethod.ComboOver
import com.calida.dsige.reparto.data.dao.overMethod.RepartoOver
import com.calida.dsige.reparto.data.local.*
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults

class RepartoViewModel : ViewModel() {

    val mensajeDireccion: MutableLiveData<String> = MutableLiveData()
    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()

    lateinit var realm: Realm
    lateinit var comboImplementation: ComboImplementation
    lateinit var repartoImplemtation: RepartoImplementation
    lateinit var apiServices: ApiServices

    fun initialRealm() {
        realm = Realm.getDefaultInstance()
        comboImplementation = ComboOver(realm)
        repartoImplemtation = RepartoOver(realm)
        apiServices = ConexionRetrofit.api.create(ApiServices::class.java)
    }

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getDetalleGruposById(id: Int): RealmResults<DetalleGrupo> {
        return comboImplementation.getDetalleGruposById(id)
    }

    fun getRepartoSuministro(id: Int): RealmResults<RepartoCargoSuministro> {
        return repartoImplemtation.getRepartoSuministro(id)
    }

    fun getRepartoFoto(id: Int): RealmResults<RepartoCargoFoto> {
        return repartoImplemtation.getRepartoFoto(id)
    }

    fun getReparto(): RealmResults<Reparto> {
        return repartoImplemtation.getReparto()
    }

    fun updateCheck(numero: String, b: Boolean) {
        repartoImplemtation.updateCheck(numero, b)
    }

    fun insertRepartoSuministro(id: Int) {
        repartoImplemtation.insertRepartoSuministro(id)
                .subscribeOn(Schedulers.io())
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

    fun deleteRepartoSuministro(id: Int) {
        repartoImplemtation.deleteRepartoSuministro(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mensajeError.value = "Suministro Eliminado"
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }
                })
    }

    fun getRepartoCargoIdentity(): Int {
        return repartoImplemtation.getRepartoCargoIdentity()
    }

    fun validateReparto(r: RepartoCargo) {
        if (r.tipoCargoId == 1) {
            if (r.suministroNumero.isEmpty()) {
                mensajeError.value = "Ingrese Suministro"
                return
            }
            if (r.nombreApellido.isEmpty()) {
                mensajeError.value = "Ingrese Nombres"
                return
            }
            if (r.predio.isEmpty()) {
                mensajeError.value = "Ingrese Predio"
                return
            }
        } else {
            if (r.nombreQuienRecibe.isEmpty()) {
                mensajeError.value = "Seleccione Quien Recibe"
                return
            }
            if (r.nombreEmpresa.isEmpty()) {
                mensajeError.value = "Ingrese Empresa"
                return
            }
            if (r.nombreApellido.isEmpty()) {
                mensajeError.value = "Ingrese Nombres"
                return
            }
        }
        insertReparto(r)
    }

    private fun insertReparto(r: RepartoCargo) {
        repartoImplemtation.insertReparto(r)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mensajeSuccess.value = "Guardado"
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }
                })
    }

    fun searchSuministro(s: String) {
        repartoImplemtation.searchSuministro(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: String) {
                        mensajeDireccion.value = t
                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.message
                    }
                })
    }

    fun getRepartoCargoFotoIdentity(): Int {
        return repartoImplemtation.getRepartoCargoFotoIdentity()
    }

    fun savePhoto(r: RepartoCargoFoto) {
        repartoImplemtation.savePhoto(r)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mensajeSuccess.value = "Guardado"
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }
                })
    }

    fun deleteRepartoFoto(id: Int) {
        repartoImplemtation.deleteRepartoFoto(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mensajeError.value = "Suministro Eliminado"
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }
                })
    }

    fun updateReparto(id: Int) {
        repartoImplemtation.updateReparto(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mensajeSuccess.value = "Guardado"
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }
                })
    }

    fun getRepartoByIdTipo(id: Int, i: Int): RepartoCargo? {
        return repartoImplemtation.getRepartoByIdTipo(id, i)
    }
}