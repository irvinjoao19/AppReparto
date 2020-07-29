package com.calida.dsige.reparto.data.dao.interfaces

import com.calida.dsige.reparto.data.local.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.realm.RealmResults

interface RepartoImplementation {

    fun getRepartoSuministro(id: Int): RealmResults<RepartoCargoSuministro>

    fun getRepartoFoto(id: Int): RealmResults<RepartoCargoFoto>

    fun getReparto(): RealmResults<Reparto>

    fun updateCheck(numero: String, b: Boolean)

    fun insertRepartoSuministro(id: Int): Completable

    fun deleteRepartoSuministro(id: Int): Completable

    fun getRepartoCargoIdentity(): Int

    fun getRepartoCargoFotoIdentity(): Int

    fun insertReparto(c: RepartoCargo): Completable

    fun searchSuministro(s: String): Observable<String>

    fun savePhoto(r: RepartoCargoFoto): Completable

    fun deleteRepartoFoto(id: Int): Completable

    fun updateReparto(id: Int): Completable

    fun getRepartoByIdTipo(id: Int, i: Int): RepartoCargo?

    fun getResultReparto(i: Int): RealmResults<RepartoCargo>

    fun getPhotoAll(i: Int): RealmResults<RepartoCargoFoto>

    fun getAllRegistroRx(i: Int): Observable<RealmResults<RepartoCargo>>

    fun closeOneRegistro(registro: RepartoCargo, i: Int)

    fun updateRepartoEnvio(codigo: Int): Completable
}