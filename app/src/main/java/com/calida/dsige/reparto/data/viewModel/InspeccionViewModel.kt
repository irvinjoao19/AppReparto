package com.calida.dsige.reparto.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calida.dsige.reparto.data.dao.interfaces.ComboImplementation
import com.calida.dsige.reparto.data.dao.interfaces.InspeccionImp
import com.calida.dsige.reparto.data.dao.interfaces.LoginImplementation
import com.calida.dsige.reparto.data.dao.overMethod.ComboOver
import com.calida.dsige.reparto.data.dao.overMethod.InspeccionOver
import com.calida.dsige.reparto.data.dao.overMethod.LoginOver
import com.calida.dsige.reparto.data.local.*
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults

class InspeccionViewModel : ViewModel() {

    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()

    val inspeccion: MutableLiveData<Inspeccion> = MutableLiveData()

    lateinit var realm: Realm
    lateinit var inspeccionImp: InspeccionImp
    lateinit var comboImp: ComboImplementation
    lateinit var loginImp: LoginImplementation

    fun initialRealm() {
        realm = Realm.getDefaultInstance()
        inspeccionImp = InspeccionOver(realm)
        comboImp = ComboOver(realm)
        loginImp = LoginOver(realm)
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

    fun getAreas(): RealmResults<Area> {
        return comboImp.getAreas()
    }

    fun getTipoTraslado(): RealmResults<TipoTraslado> {
        return comboImp.getTipoTraslado()
    }

    fun getCheckList(id: Int): RealmResults<CheckList> {
        return inspeccionImp.getCheckList(id)
    }

    fun getCheckList(id: Int, tipo: Int): RealmResults<CheckList> {
        return inspeccionImp.getCheckList(id, tipo)
    }

    fun setIsCheck(e: CheckList, type: Int, name: String) {
        return inspeccionImp.setIsCheck(e, type, name)
    }

    fun getInspeccion(id: Int, operarioId: Int): Inspeccion? {
        return inspeccionImp.getInspeccion(id, operarioId)
    }

    fun setInspeccion(id: Int) {
        inspeccionImp.getInspeccionById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Inspeccion> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Inspeccion) {
                        inspeccion.value = t
                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }

                })
    }

    fun getInspeccionIdentity(): Int {
        return inspeccionImp.getInspeccionIdentity()
    }

    fun validateInspeccion(i: Inspeccion): Boolean {

        if (i.areaId == 0) {
            mensajeError.value = "Seleccione Actividad."
            return false
        }

        if (i.vFecha.isEmpty()) {
            mensajeError.value = "Ingrese fecha."
            return false
        }

        if (i.vHora.isEmpty()) {
            mensajeError.value = "Ingrese hora."
            return false
        }

        if (i.lugar.isEmpty()) {
            mensajeError.value = "Ingrese lugar."
            return false
        }

        if (i.inspeccionId == 1) {
            if (i.trasladoId == 0) {
                mensajeError.value = "Seleccion Tipo de Traslado"
                return false
            }

            if (i.trasladoId == 2) {
                if (i.kilometraje.isEmpty()) {
                    mensajeError.value = "Ingrese Kilometraje"
                    return false
                }
            }
        }

        insertOrUpdateInspeccion(i)
        return true
    }

    private fun insertOrUpdateInspeccion(i: Inspeccion) {
        inspeccionImp.saveInspeccion(i)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mensajeSuccess.value = "Inspección Guardada"
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }
                })
    }

    // TODO INSPECCION DETALLE

    fun getInspeccionDetalleIdentity(): Int {
        return inspeccionImp.getInspeccionDetalleIdentity()
    }

    fun getInspeccionDetalle(id: Int, operarioId: Int): RealmResults<InspeccionDetalle> {
        return inspeccionImp.getInspeccionDetalle(id, operarioId)
    }

    fun insertInspeccionDetalleCheck(formatoInspeccionId: Int, usuarioId: Int) {
        inspeccionImp.saveInspeccionDetalleCheck(formatoInspeccionId, usuarioId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mensajeSuccess.value = "Check List Guardados"
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }

                })
    }

    fun insertInspeccionDetalle(detalle: List<InspeccionDetalle>, id: Int) {
        inspeccionImp.saveInspeccionDetalle(detalle, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mensajeSuccess.value = "Check List Guardados"
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }

                })
    }

    fun validateUpdateInspeccionDetalle(d: InspeccionDetalle, f: String, obs1: String, obs2: String): Boolean {
        if (d.aplicaObs1 == 1) {
            if (obs1.isEmpty()) {
                mensajeError.value = "Ingrese Observación"
                return false
            }
        }
        updateInspeccionDetalle(d, f, obs1, obs2)
        return true
    }

    fun deleteInspeccionDetalle(id: Int) {
        inspeccionImp.deleteInspeccionDetalle(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mensajeSuccess.value = "Detalle Eliminado"
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }
                })
    }

    fun deleteInspeccionAdicional(id: Int) {
        inspeccionImp.deleteInspeccionAdicional(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mensajeSuccess.value = "Detalle Eliminado"
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }
                })

    }

    private fun updateInspeccionDetalle(d: InspeccionDetalle, f: String, obs1: String, obs2: String) {
        inspeccionImp.updateInspeccionDetalle(d, f, obs1, obs2)
        mensajeSuccess.value = "Detalle Actualizado"
    }

    // TODO INSPECCION ADICIONAL

    fun getInspeccionAditionIdentity(): Int {
        return inspeccionImp.getInspeccionAditionalIdentity()
    }

    fun getInspeccionAdicional(id: Int, operarioId: Int): RealmResults<InspeccionAdicionales> {
        if (id == 1){
            return inspeccionImp.getInspeccionAdicional(id, operarioId)
        }
        return inspeccionImp.getInspeccionAdicional2(id, operarioId,"SI")
    }

    fun validateInspeccionAditional(a: InspeccionAdicionales): Boolean {
        if (a.inspeccionId != 1) {
            if (a.nombreTipo.isEmpty()) {
                mensajeError.value = "Ingrese Observación"
                return false
            }
        } else {
            if (a.descripcion.isEmpty()) {
                mensajeError.value = "Ingrese Descripción"
                return false
            }
        }

        insertOrUpdateInspeccionAditional(a)
        return true
    }

    fun validateUpdateInspeccionAdicional(id: Int, o: String) {
        updateInspeccionAdicional(id, o)
    }

    private fun updateInspeccionAdicional(id: Int, o: String) {
        inspeccionImp.updateInspeccionAdicional(id, o)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mensajeSuccess.value = "Actualizado"
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }
                })
    }

    private fun insertOrUpdateInspeccionAditional(i: InspeccionAdicionales) {
        inspeccionImp.saveInspeccionAditional(i)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mensajeSuccess.value = "Inspección Guardada"
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }
                })
    }

    fun activateOrCloseInspeccion(id: Int, usuarioId: Int, tipo: Int) {
        inspeccionImp.activateOrCloseInspeccion(id, usuarioId, tipo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        if (tipo == 0) {
                            mensajeSuccess.value = "Inspección Cerrada"
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        mensajeError.value = e.toString()
                    }
                })
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    fun getCheckListCount(formatoInspeccionId: Int, tipo: Int): Int {
        return inspeccionImp.getCheckListCount(formatoInspeccionId, tipo)
    }

    fun getCheckListCountActive(formatoInspeccionId: Int, tipo: Int): Int {
        return inspeccionImp.getCheckListCountActive(formatoInspeccionId, tipo)
    }
}