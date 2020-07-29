package com.calida.dsige.reparto.data.dao.interfaces

import com.calida.dsige.reparto.data.local.*
import io.realm.RealmResults

interface ComboImplementation {

    fun tipoLecturaAll(): RealmResults<TipoLectura>

    fun detalleGrupoAll(): RealmResults<DetalleGrupo>

    fun getDetalleGrupoById(id: Int): DetalleGrupo

    fun getDetalleGruposById(id: Int): RealmResults<DetalleGrupo>

    fun getDetalleGrupoByLectura(servicioId: Int): RealmResults<DetalleGrupo>

    fun getDetalleGrupoByMotivo(servicioId: Int, grupo: String): RealmResults<DetalleGrupo>

    fun getDetalleGrupoByResultado(servicioId: Int, grupo: String): RealmResults<DetalleGrupo>

    fun getMotivos(): RealmResults<Motivo>

    fun getMotivosById(id: Int?): Motivo?

    fun getAreas(): RealmResults<Area>

    fun getTipoTraslado(): RealmResults<TipoTraslado>

    fun getVehiculos(): RealmResults<Vehiculo>

    fun getFormato(tipo: Int): RealmResults<Formato>

    fun getDetalleGrupoByParentId(id: Int): RealmResults<DetalleGrupo>

    fun getObservacion(): RealmResults<Observaciones>

}