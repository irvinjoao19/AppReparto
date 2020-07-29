package com.calida.dsige.reparto.data.dao.overMethod

import com.calida.dsige.reparto.data.dao.interfaces.ComboImplementation
import com.calida.dsige.reparto.data.local.*
import io.realm.Realm
import io.realm.RealmResults

class ComboOver(private val realm: Realm) : ComboImplementation {

    override fun detalleGrupoAll(): RealmResults<DetalleGrupo> {
        return realm.where(DetalleGrupo::class.java).findAll()
    }

    override fun tipoLecturaAll(): RealmResults<TipoLectura> {
        return realm.where(TipoLectura::class.java).findAll()
    }

    override fun getDetalleGrupoById(id: Int): DetalleGrupo {
        return realm.where(DetalleGrupo::class.java).equalTo("iD_DetalleGrupo", id).findFirst()!!
    }

    override fun getDetalleGruposById(id: Int): RealmResults<DetalleGrupo> {
        return realm.where(DetalleGrupo::class.java).equalTo("id_Servicio", id).findAll()
    }

    override fun getDetalleGrupoByLectura(servicioId: Int): RealmResults<DetalleGrupo> {
        return realm.where(DetalleGrupo::class.java).equalTo("id_Servicio", servicioId).findAll()
    }

    override fun getDetalleGrupoByMotivo(servicioId: Int, grupo: String): RealmResults<DetalleGrupo> {
        return realm.where(DetalleGrupo::class.java).equalTo("id_Servicio", servicioId).equalTo("grupo", grupo).findAll()
    }

    override fun getDetalleGrupoByResultado(servicioId: Int, grupo: String): RealmResults<DetalleGrupo> {
        return realm.where(DetalleGrupo::class.java).equalTo("id_Servicio", servicioId).equalTo("grupo", grupo).findAll()
    }

    override fun getMotivos(): RealmResults<Motivo> {
        return realm.where(Motivo::class.java).findAll()
    }

    override fun getMotivosById(id: Int?): Motivo? {
        return realm.where(Motivo::class.java).equalTo("codigo", id).findFirst()
    }

    override fun getAreas(): RealmResults<Area> {
        return realm.where(Area::class.java).findAll()
    }

    override fun getTipoTraslado(): RealmResults<TipoTraslado> {
        return realm.where(TipoTraslado::class.java).findAll()
    }

    override fun getVehiculos(): RealmResults<Vehiculo> {
        return realm.where(Vehiculo::class.java).findAll()
    }

    override fun getFormato(tipo: Int): RealmResults<Formato> {
        return realm.where(Formato::class.java).equalTo("tipo", tipo).findAll()
    }

    override fun getDetalleGrupoByParentId(id: Int): RealmResults<DetalleGrupo> {
        return realm.where(DetalleGrupo::class.java).equalTo("parentId", id).findAll()
    }

    override fun getObservacion(): RealmResults<Observaciones> {
        return realm.where(Observaciones::class.java).findAll()
    }
}