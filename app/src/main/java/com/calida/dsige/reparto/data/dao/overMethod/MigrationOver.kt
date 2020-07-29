package com.calida.dsige.reparto.data.dao.overMethod

import com.calida.dsige.reparto.data.dao.interfaces.MigrationImplementation
import com.calida.dsige.reparto.data.local.*
import io.realm.Realm
import io.realm.RealmResults

open class MigrationOver(private val realm: Realm) : MigrationImplementation {

    override fun save(migration: Migration) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(migration)
        }
    }

    override fun deleteAll() {
        realm.executeTransaction { realm ->
            val detalleGrupo: RealmResults<DetalleGrupo>? = realm.where(DetalleGrupo::class.java).findAll()
            detalleGrupo?.deleteAllFromRealm()
            val registros: RealmResults<Registro>? = realm.where(Registro::class.java).notEqualTo("registro_Observacion","INICIO").findAll()
            registros?.deleteAllFromRealm()
            val photos: RealmResults<Photo>? = realm.where(Photo::class.java).findAll()
            photos?.deleteAllFromRealm()
            val suministroCortes: RealmResults<SuministroCortes>? = realm.where(SuministroCortes::class.java).findAll()
            suministroCortes?.deleteAllFromRealm()
            val suministroLectura: RealmResults<SuministroLectura>? = realm.where(SuministroLectura::class.java).findAll()
            suministroLectura?.deleteAllFromRealm()
            val reparto: RealmResults<Reparto>? = realm.where(Reparto::class.java).findAll()
            reparto?.deleteAllFromRealm()
            val sendReparto: RealmResults<SendReparto>? = realm.where(SendReparto::class.java).findAll()
            sendReparto?.deleteAllFromRealm()
            val servicio: RealmResults<Servicio>? = realm.where(Servicio::class.java).findAll()
            servicio?.deleteAllFromRealm()
            val suministroReconexion: RealmResults<SuministroReconexion>? = realm.where(SuministroReconexion::class.java).findAll()
            suministroReconexion?.deleteAllFromRealm()
//            val i: RealmResults<Inspeccion>? = realm.where(Inspeccion::class.java).findAll()
//            i?.deleteAllFromRealm()
//            val d: RealmResults<InspeccionDetalle>? = realm.where(InspeccionDetalle::class.java).findAll()
//            d?.deleteAllFromRealm()
//            val a: RealmResults<InspeccionAdicionales>? = realm.where(InspeccionAdicionales::class.java).findAll()
//            a?.deleteAllFromRealm()
            val a: RealmResults<Area>? = realm.where(Area::class.java).findAll()
            a?.deleteAllFromRealm()
            val t: RealmResults<TipoTraslado>? = realm.where(TipoTraslado::class.java).findAll()
            t?.deleteAllFromRealm()
            val v: RealmResults<Vehiculo>? = realm.where(Vehiculo::class.java).findAll()
            v?.deleteAllFromRealm()
            val s: RealmResults<CheckList>? = realm.where(CheckList::class.java).findAll()
            s?.deleteAllFromRealm()
        }
    }

    override fun deleteAllReparto() {
        realm.executeTransaction {
            val sendReparto: RealmResults<SendReparto>? = realm.where(SendReparto::class.java).findAll()
            val photoReparto: RealmResults<PhotoReparto>? = realm.where(PhotoReparto::class.java).findAll()
            sendReparto?.deleteAllFromRealm()
            photoReparto?.deleteAllFromRealm()
        }
    }
}