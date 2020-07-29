package com.calida.dsige.reparto.data.dao.interfaces

import com.calida.dsige.reparto.data.local.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.realm.RealmResults

interface InspeccionImp {

    fun getInspecciones(operarioId:Int,estado: Int): RealmResults<Inspeccion>

    fun getInspecciones(operarioId:Int,estado: Int, active: Int): RealmResults<Inspeccion>

    fun getCheckList(id: Int): RealmResults<CheckList>

    fun getCheckList(id: Int, tipo: Int): RealmResults<CheckList>

    fun setIsCheck(e: CheckList, type: Int, name: String)

    fun getInspeccionIdentity(): Int

    fun saveInspeccion(i: Inspeccion): Completable

    fun getInspeccionById(id: Int): Observable<Inspeccion>

    fun getInspeccion(id: Int,operarioId:Int): Inspeccion?

    // TODO : INSPECION DETALLE

    fun getInspeccionDetalleIdentity(): Int

    fun getInspeccionDetalle(id: Int,operarioId:Int): RealmResults<InspeccionDetalle>

    fun saveInspeccionDetalle(i: List<InspeccionDetalle>, id: Int): Completable

    fun saveInspeccionDetalleCheck(formatoInspeccionId: Int, usuarioId: Int): Completable

    fun updateInspeccionDetalle(d: InspeccionDetalle, f: String, obs1: String, obs2: String)

    fun deleteInspeccionDetalle(id: Int) : Completable

    fun deleteInspeccionAdicional(id: Int) : Completable

    //TODO  : INSPECCION ADICIONAL

    fun getInspeccionAditionalIdentity(): Int

    fun getInspeccionAdicional(id: Int,operarioId: Int): RealmResults<InspeccionAdicionales>

    fun getInspeccionAdicional2(id: Int,operarioId: Int,s:String): RealmResults<InspeccionAdicionales>

    fun saveInspeccionAditional(i: InspeccionAdicionales): Completable

    fun updateInspeccionAdicional(id : Int, o: String) : Completable

    fun activateOrCloseInspeccion(id: Int,usuarioId:Int,tipo: Int): Completable

    fun getCheckListCount(id: Int, tipo: Int): Int

    fun getCheckListCountActive(id: Int, tipo: Int): Int

    fun getVerification(fecha: String,usuarioId:Int): Verification?

    fun insertVerification(fecha: String,usuarioId:Int) : Completable
}