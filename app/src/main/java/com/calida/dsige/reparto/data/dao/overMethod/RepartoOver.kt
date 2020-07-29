package com.calida.dsige.reparto.data.dao.overMethod

import com.calida.dsige.reparto.data.dao.interfaces.RepartoImplementation
import com.calida.dsige.reparto.data.local.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmResults

class RepartoOver(private val realm: Realm) : RepartoImplementation {

    override fun getRepartoSuministro(id: Int): RealmResults<RepartoCargoSuministro> {
        return realm.where(RepartoCargoSuministro::class.java).equalTo("repartoId", id).findAll()
    }

    override fun getRepartoFoto(id: Int): RealmResults<RepartoCargoFoto> {
        return realm.where(RepartoCargoFoto::class.java).equalTo("cargoId", id).findAll()
    }

    override fun getReparto(): RealmResults<Reparto> {
        return realm.where(Reparto::class.java).findAll()
    }

    override fun updateCheck(numero: String, b: Boolean) {
        realm.executeTransaction { r ->
            val re: Reparto? = r.where(Reparto::class.java).equalTo("Suministro_Numero_reparto", numero).findFirst()
            if (re != null) {
                re.isActive = b
            }
        }
    }

    override fun insertRepartoSuministro(id: Int): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val isActive = true
                    val list: RealmResults<Reparto> = r.where(Reparto::class.java).equalTo("isActive", isActive).findAll()
                    for (e: Reparto in list) {
                        val registro: RepartoCargo? = realm.where(RepartoCargo::class.java).equalTo("repartoId", id).findFirst()
                        if (registro != null) {
                            val d = RepartoCargoSuministro()
                            d.cargoSuministroId = e.id_Reparto
                            d.repartoId = id
                            d.suministroNumero = e.Suministro_Numero_reparto
                            d.estado = 1
                            r.copyToRealmOrUpdate(d)
                            registro.suministros?.add(d)
                        }
                    }
                }
            }
        }
    }

    override fun deleteRepartoSuministro(id: Int): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val s: RepartoCargoSuministro? = r.where(RepartoCargoSuministro::class.java).equalTo("cargoSuministroId", id).findFirst()
                    if (s != null) {
                        val re = r.where(Reparto::class.java).equalTo("id_Reparto", id).findFirst()
                        if (re != null) {
                            re.isActive = false
                        }
                        s.deleteFromRealm()
                    }

                }
            }
        }
    }

    override fun getRepartoCargoIdentity(): Int {
        val registro = realm.where(RepartoCargo::class.java).max("cargoId")
        val result: Int
        result = if (registro == null) {
            1
        } else {
            registro.toInt() + 1
        }
        return result
    }

    override fun getRepartoCargoFotoIdentity(): Int {
        val registro = realm.where(RepartoCargoFoto::class.java).max("fotoCargoId")
        val result: Int
        result = if (registro == null) {
            1
        } else {
            registro.toInt() + 1
        }
        return result
    }

    override fun insertReparto(c: RepartoCargo): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val re: RepartoCargo? = r.where(RepartoCargo::class.java).equalTo("cargoId", c.cargoId).findFirst()
                    if (re != null) {
                        re.tipoCargoId = c.tipoCargoId
                        re.repartoId = c.repartoId
                        re.suministroNumero = c.suministroNumero
                        re.nombreApellido = c.nombreApellido
                        re.dni = c.dni
                        re.predio = c.predio
                        re.quienRecibeCargo = c.quienRecibeCargo
                        re.nombreQuienRecibe = c.nombreQuienRecibe
                        re.nombreEmpresa = c.nombreEmpresa
                        re.lecturaCargo = c.lecturaCargo
                        re.latitud = c.latitud
                        re.longitud = c.longitud
                        re.fecha = c.fecha
                        re.hora = c.hora
                        re.fechaMovil = c.fechaMovil
                        re.estado = c.estado
                    } else {
                        r.copyToRealmOrUpdate(c)
                    }
                }
            }
        }
    }

    override fun searchSuministro(s: String): Observable<String> {
        return Observable.create { e ->
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val re: Reparto? = r.where(Reparto::class.java).equalTo("Suministro_Numero_reparto", s).findFirst()
                    if (re == null) {
                        e.onError(Throwable("Suministro no encontrado"))
                        e.onComplete()
                        return@executeTransaction
                    }
                    e.onNext(re.Direccion_Reparto)
                    e.onComplete()
                }
            }
        }
    }

    override fun savePhoto(r: RepartoCargoFoto): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { re ->
                    val registro: RepartoCargo? = realm.where(RepartoCargo::class.java).equalTo("repartoId", r.cargoId).findFirst()
                    if (registro != null) {
                        re.copyToRealmOrUpdate(r)
                        registro.photos?.add(r)
                    }
                }
            }
        }
    }

    override fun deleteRepartoFoto(id: Int): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val s: RepartoCargoFoto = r.where(RepartoCargoFoto::class.java).equalTo("fotoCargoId", id).findFirst()!!
                    s.deleteFromRealm()
                }
            }
        }
    }

    override fun updateReparto(id: Int): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val re: RepartoCargo? = r.where(RepartoCargo::class.java).equalTo("repartoId", id).findFirst()
                    if (re != null) {
                        re.estado = 2
                    }
                    val isActive = true
                    val a: RealmResults<Reparto> = r.where(Reparto::class.java).equalTo("isActive", isActive).findAll()
                    for (d in a) {
                        d.isActive = false
                    }
                }
            }
        }
    }

    override fun getRepartoByIdTipo(id: Int, i: Int): RepartoCargo? {
        return realm.where(RepartoCargo::class.java)
                .equalTo("repartoId", id)
                .equalTo("tipoCargoId", i).findFirst()
    }

    override fun getResultReparto(i: Int): RealmResults<RepartoCargo> {
        return realm.where(RepartoCargo::class.java).equalTo("estado", i).findAll()
    }

    override fun getPhotoAll(i: Int): RealmResults<RepartoCargoFoto> {
        return realm.where(RepartoCargoFoto::class.java).equalTo("estado", i).findAll()
    }

    override fun getAllRegistroRx(i: Int): Observable<RealmResults<RepartoCargo>> {
        return Observable.create { e ->
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val re: RealmResults<RepartoCargo>? = r.where(RepartoCargo::class.java).equalTo("estado", i).findAll()
                    if (re == null) {
                        e.onError(Throwable("No hay registros"))
                        e.onComplete()
                        return@executeTransaction
                    }
                    e.onNext(re)
                    e.onComplete()
                }
            }
        }
    }

    override fun closeOneRegistro(registro: RepartoCargo, i: Int) {
        realm.executeTransaction {
            registro.estado = i
//            if (registro.photos != null) {
//                val photos = registro.photos?.createSnapshot()
//                if (photos != null){
//                    for (p in photos) {
//                        p.estado = 0
//                    }
//                }
//                val suministros = registro.suministros?.createSnapshot()
//                if (suministros != null){
//                    for (p in suministros) {
//                        p.estado = 0
//                    }
//                }
//            }
        }
    }

    override fun updateRepartoEnvio(codigo: Int): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val re: RepartoCargo? = r.where(RepartoCargo::class.java).equalTo("cargoId", codigo).findFirst()
                    if (re != null) {
                        re.estado = 0

                        val a: RealmResults<RepartoCargoFoto> = r.where(RepartoCargoFoto::class.java).equalTo("cargoId", re.repartoId).findAll()
                        for (d in a) {
                            d.estado = 0
                        }

                        val s: RealmResults<RepartoCargoSuministro> = r.where(RepartoCargoSuministro::class.java).equalTo("repartoId", re.repartoId).findAll()
                        for (d in s) {
                            d.estado = 0
                        }
                    }
                }
            }
        }
    }
}