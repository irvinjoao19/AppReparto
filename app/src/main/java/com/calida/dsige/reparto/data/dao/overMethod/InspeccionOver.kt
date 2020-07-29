package com.calida.dsige.reparto.data.dao.overMethod

import com.calida.dsige.reparto.data.dao.interfaces.InspeccionImp
import com.calida.dsige.reparto.data.local.*
import com.calida.dsige.reparto.helper.Util
import io.reactivex.Completable
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

class InspeccionOver(var realm: Realm) : InspeccionImp {

    override fun getInspecciones(operarioId: Int, estado: Int): RealmResults<Inspeccion> {
        return realm.where(Inspeccion::class.java).equalTo("operarioLecturaId", operarioId).equalTo("estado", estado).equalTo("fechaVerificacion", Util.getFecha()).findAll()
    }

    override fun getInspecciones(operarioId: Int, estado: Int, active: Int): RealmResults<Inspeccion> {
        return realm.where(Inspeccion::class.java).equalTo("operarioLecturaId", operarioId).equalTo("estado", estado).equalTo("active", active).equalTo("fechaVerificacion", Util.getFecha()).findAll()
    }

    override fun getCheckList(id: Int): RealmResults<CheckList> {
        return realm.where(CheckList::class.java).equalTo("formatoId", id).findAll()
    }

    override fun getCheckList(id: Int, tipo: Int): RealmResults<CheckList> {
        return realm.where(CheckList::class.java).equalTo("formatoId", id).equalTo("grupo", tipo).findAll()
    }

    override fun setIsCheck(e: CheckList, type: Int, name: String) {
        realm.executeTransaction { r ->
            val c: CheckList? = r.where(CheckList::class.java).equalTo("checkListId", e.checkListId).findFirst()
            if (c != null) {
                c.isActive = true
                if (type == 1) {
                    when (name) {
                        e.valor1 -> {
                            c.isSelectedSI = true
                            c.isSelectedNO = false
                            c.isSelectedNA = false
                            c.isSelectedTA = false
                        }
                        e.valor2 -> {
                            c.isSelectedSI = false
                            c.isSelectedNO = true
                            c.isSelectedNA = false
                            c.isSelectedTA = false
                        }
                        e.valor3 -> {
                            c.isSelectedSI = false
                            c.isSelectedNO = false
                            c.isSelectedNA = true
                            c.isSelectedTA = false
                        }
                        e.valor4 -> {
                            c.isSelectedSI = false
                            c.isSelectedNO = false
                            c.isSelectedNA = false
                            c.isSelectedTA = true
                        }
                    }
                } else {
                    when (name) {
                        e.valor1 -> {
                            c.isSelectedB = true
                            c.isSelectedM = false
                            c.isSelectedNA2 = false
                        }
                        e.valor2 -> {
                            c.isSelectedB = false
                            c.isSelectedM = true
                            c.isSelectedNA2 = false
                        }
                        e.valor3 -> {
                            c.isSelectedB = false
                            c.isSelectedM = false
                            c.isSelectedNA2 = true
                        }
                    }
                }
            }
        }
    }

    override fun getInspeccionIdentity(): Int {
        val i = realm.where(Inspeccion::class.java).max("id")
        return if (i == null) 1 else i.toInt() + 1
    }

    override fun saveInspeccion(i: Inspeccion): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val inspection = realm.where(Inspeccion::class.java).equalTo("operarioLecturaId", i.operarioLecturaId).equalTo("inspeccionId", i.inspeccionId).equalTo("fechaVerificacion", Util.getFecha()).findFirst()
                    if (inspection != null) {
                        if (inspection.areaId != i.areaId) {
                            val d: RealmResults<InspeccionDetalle>? = realm.where(InspeccionDetalle::class.java)
                                    .equalTo("inspeccionId", i.inspeccionId)
                                    .equalTo("usuarioId", i.usuarioId)
                                    .equalTo("fechaVerificacion", Util.getFecha()).findAll()
                            if (i.inspeccionId == 2) {
                                if (d != null) {
                                    for (detalle: InspeccionDetalle in d) {
                                        val a = r.where(InspeccionAdicionales::class.java).equalTo("inspeccionAdicionalId", detalle.inspeccionDetalleId).findFirst()
                                        a?.deleteFromRealm()
                                    }
                                }
                            }
                            d?.deleteAllFromRealm()

                            val c: RealmResults<CheckList>? = realm.where(CheckList::class.java)
                                    .equalTo("formatoId", i.inspeccionId)
                                    .findAll()
                            if (c != null) {
                                for (ch: CheckList in c) {
                                    ch.isSelectedSI = false
                                    ch.isSelectedNO = false
                                    ch.isSelectedNA = false
                                    ch.isSelectedTA = false
                                    ch.isActive = false
                                }
                            }
                        }

                        inspection.vFecha = i.vFecha
                        inspection.vHora = i.vHora
                        inspection.fecha = i.fecha
                        inspection.lugar = i.lugar
                        inspection.nombreArea = i.nombreArea
                        inspection.nombreTraslado = i.nombreTraslado
                        inspection.kilometraje = i.kilometraje
                        inspection.usuarioId = i.usuarioId
                        inspection.active = 2
                    } else {
                        r.copyToRealmOrUpdate(i)
                    }
                }
            }
        }
    }

    override fun getInspeccionById(id: Int): Observable<Inspeccion> {
        return Observable.create { emitter ->
            try {
                Realm.getDefaultInstance().use { realm ->
                    val a = realm.where(Inspeccion::class.java).equalTo("inspeccionId", id).findFirst()!!
                    emitter.onNext(a)
                    emitter.onComplete()
                }
            } catch (e: Throwable) {
                emitter.onError(e)
            }
        }
    }

    override fun getInspeccion(id: Int, operarioId: Int): Inspeccion? {
        return realm.where(Inspeccion::class.java).equalTo("operarioLecturaId", operarioId).equalTo("inspeccionId", id).equalTo("fechaVerificacion", Util.getFecha()).findFirst()
    }

    override fun getInspeccionDetalleIdentity(): Int {
        val i = realm.where(InspeccionDetalle::class.java).max("inspeccionDetalleId")
        return if (i == null) 1 else i.toInt() + 1
    }

    override fun getInspeccionDetalle(id: Int, operarioId: Int): RealmResults<InspeccionDetalle> {
        return realm.where(InspeccionDetalle::class.java).equalTo("usuarioId", operarioId).equalTo("inspeccionId", id).equalTo("fechaVerificacion", Util.getFecha()).findAll().sort("inspeccionDetalleId", Sort.ASCENDING)
    }

    override fun saveInspeccionDetalle(i: List<InspeccionDetalle>, id: Int): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val inspecccion: Inspeccion? = r.where(Inspeccion::class.java).equalTo("inspeccionId", id).findFirst()
                    if (inspecccion != null) {
                        for (detail: InspeccionDetalle in i) {
                            val d = realm.where(InspeccionDetalle::class.java).equalTo("inspeccionDetalleId", detail.inspeccionDetalleId).findFirst()
                            if (d == null) {
                                r.copyToRealmOrUpdate(detail)
                                inspecccion.detalles?.add(detail)
                            } else {
                                d.estadoCheckList = detail.estadoCheckList
                                d.nombreEstadoCheckList = detail.nombreEstadoCheckList
                                d.estado2CheckList = detail.estado2CheckList
                                d.nombreEstado2CheckList = detail.nombreEstado2CheckList
                            }
                        }
                    }
                }
            }
        }
    }

    override fun saveInspeccionDetalleCheck(formatoInspeccionId: Int, usuarioId: Int): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val isActive = true
                    val list: RealmResults<CheckList> = r.where(CheckList::class.java).equalTo("formatoId", formatoInspeccionId).equalTo("isActive", isActive).findAll()
                    for (e: CheckList in list) {
                        val d = InspeccionDetalle()
                        d.inspeccionId = formatoInspeccionId
                        d.checkListId = e.checkListId
                        d.inspeccionDetalleId = e.checkListId
                        d.aplicaObs1 = e.aplicaObs1
                        d.aplicaObs2 = e.aplicaObs2
                        d.aplicaFecha = e.aplicaFecha
                        d.descripcion = e.descripcion
                        d.usuarioId = usuarioId
                        d.fecha = Util.getFecha()
                        d.fechaVerificacion = Util.getFecha()
                        d.campo1 = e.campo1
                        d.campo2 = e.campo2
                        if (e.isSelectedSI) {
                            d.estadoCheckList = 1
                            d.nombreEstadoCheckList = e.valor1
                        }
                        if (e.isSelectedNO) {
                            d.estadoCheckList = 2
                            d.nombreEstadoCheckList = e.valor2
                        }
                        if (e.isSelectedNA) {
                            d.estadoCheckList = 3
                            d.nombreEstadoCheckList = e.valor3
                        }
                        if (e.isSelectedTA) {
                            d.estadoCheckList = 4
                            d.nombreEstadoCheckList = e.valor4
                        }

                        val inspecccion: Inspeccion? = r.where(Inspeccion::class.java)
                                .equalTo("operarioLecturaId", usuarioId)
                                .equalTo("fechaVerificacion", Util.getFecha())
                                .equalTo("inspeccionId", formatoInspeccionId).findFirst()
                        if (inspecccion != null) {
                            val detalle = r.where(InspeccionDetalle::class.java)
                                    .equalTo("usuarioId", usuarioId)
                                    .equalTo("inspeccionDetalleId", d.inspeccionDetalleId)
                                    .equalTo("fechaVerificacion", Util.getFecha()).findFirst()
                            if (detalle == null) {
                                r.copyToRealmOrUpdate(d)
                                inspecccion.detalles?.add(d)
                            } else {
                                detalle.estadoCheckList = d.estadoCheckList
                                detalle.nombreEstadoCheckList = d.nombreEstadoCheckList
                                detalle.estado2CheckList = d.estado2CheckList
                                detalle.nombreEstado2CheckList = d.nombreEstado2CheckList
                            }
                        }

                        if (formatoInspeccionId == 2) {
                            val a = InspeccionAdicionales()
                            a.inspeccionAdicionalId = d.inspeccionDetalleId
                            a.inspeccionId = formatoInspeccionId
                            a.estado = 1
                            a.tipo = 2
                            a.usuarioId = usuarioId
                            a.nombreTipo = d.descripcion
                            a.nombreValor = d.nombreEstadoCheckList
                            a.fechaVerificacion = Util.getFecha()

                            val i: Inspeccion? = r.where(Inspeccion::class.java)
                                    .equalTo("operarioLecturaId", usuarioId)
                                    .equalTo("inspeccionId", formatoInspeccionId)
                                    .equalTo("fechaVerificacion", Util.getFecha()).findFirst()
                            if (i != null) {
                                r.copyToRealmOrUpdate(a)
                                i.adicionales?.add(a)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun updateInspeccionDetalle(d: InspeccionDetalle, f: String, obs1: String, obs2: String) {
        realm.executeTransaction { r ->
            val detail = r.where(InspeccionDetalle::class.java).equalTo("inspeccionDetalleId", d.inspeccionDetalleId).findFirst()
            if (detail != null) {
                detail.fecha = f
                detail.observacion = obs1
                detail.observacion2 = obs2
                detail.estado = 1
            }
        }
    }

    override fun deleteInspeccionDetalle(id: Int): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val detalle = r.where(InspeccionDetalle::class.java).equalTo("inspeccionDetalleId", id).findFirst()
                    if (detalle != null) {
                        val a = r.where(InspeccionAdicionales::class.java).equalTo("inspeccionAdicionalId", detalle.inspeccionDetalleId).findFirst()
                        a?.deleteFromRealm()

                        val c = r.where(CheckList::class.java).equalTo("checkListId", detalle.checkListId).findFirst()
                        if (c != null) {
                            c.isSelectedSI = false
                            c.isSelectedNO = false
                            c.isSelectedNA = false
                            c.isSelectedB = false
                            c.isSelectedM = false
                            c.isSelectedNA2 = false
                            c.isActive = false
                        }
                        detalle.deleteFromRealm()
                    }
                }
            }
        }
    }

    override fun deleteInspeccionAdicional(id: Int): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val a = r.where(InspeccionAdicionales::class.java).equalTo("inspeccionAdicionalId", id).findFirst()
                    a?.deleteFromRealm()
                }
            }
        }
    }

    override fun getInspeccionAditionalIdentity(): Int {
        val i = realm.where(InspeccionAdicionales::class.java).max("inspeccionAdicionalId")
        return if (i == null) 1 else i.toInt() + 1
    }

    override fun getInspeccionAdicional(id: Int, operarioId: Int): RealmResults<InspeccionAdicionales> {
        return realm.where(InspeccionAdicionales::class.java).equalTo("usuarioId", operarioId).equalTo("inspeccionId", id).equalTo("fechaVerificacion", Util.getFecha()).findAll().sort("inspeccionAdicionalId", Sort.ASCENDING)
    }

    override fun getInspeccionAdicional2(id: Int, operarioId: Int, s: String): RealmResults<InspeccionAdicionales> {
        return realm.where(InspeccionAdicionales::class.java)
                .equalTo("usuarioId", operarioId)
                .equalTo("inspeccionId", id)
                .equalTo("nombreValor", s)
                .equalTo("fechaVerificacion", Util.getFecha())
                .findAll().sort("inspeccionAdicionalId", Sort.ASCENDING)
    }

    override fun saveInspeccionAditional(i: InspeccionAdicionales): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val inspecccion: Inspeccion? = r.where(Inspeccion::class.java).equalTo("inspeccionId", i.inspeccionId).equalTo("fechaVerificacion", Util.getFecha()).findFirst()
                    if (inspecccion != null) {
                        r.copyToRealmOrUpdate(i)
                        inspecccion.adicionales?.add(i)
                    }
                }
            }
        }
    }

    override fun updateInspeccionAdicional(id: Int, o: String): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val detail = r.where(InspeccionAdicionales::class.java).equalTo("inspeccionAdicionalId", id).findFirst()
                    if (detail != null) {
                        detail.descripcion = o
                    }
                }
            }
        }
    }

    override fun activateOrCloseInspeccion(id: Int, usuarioId: Int, tipo: Int): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val i: Inspeccion? = r.where(Inspeccion::class.java).equalTo("operarioLecturaId", usuarioId).equalTo("inspeccionId", id).equalTo("fechaVerificacion", Util.getFecha()).findFirst()
                    if (i != null) {
                        if (tipo == 0) {
                            i.active = 0
                            i.estado = 1
                        } else {
                            i.active = 2
                            i.estado = 0
                        }
                    }
                }
            }
        }
    }

    override fun getCheckListCount(id: Int, tipo: Int): Int {
        val titulo = 0
        return realm.where(CheckList::class.java).equalTo("formatoId", id)
                .equalTo("titulo", titulo).equalTo("grupo", tipo).findAll().count()
    }

    override fun getCheckListCountActive(id: Int, tipo: Int): Int {
        val titulo = 0
        val isActive = true
        return realm.where(CheckList::class.java)
                .equalTo("formatoId", id)
                .equalTo("grupo", tipo)
                .equalTo("titulo", titulo)
                .equalTo("isActive", isActive)
                .findAll().count()
    }

    override fun getVerification(fecha: String, usuarioId: Int): Verification? {
        return realm.where(Verification::class.java)
                .equalTo("usuarioId", usuarioId)
                .equalTo("fecha", fecha).findFirst()
    }

    override fun insertVerification(fecha: String, usuarioId: Int): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { r ->
                    val i = r.where(Verification::class.java).max("id")
                    val v = Verification()
                    v.id = if (i == null) 1 else i.toInt() + 1
                    v.fecha = fecha
                    v.usuarioId = usuarioId
                    r.copyToRealmOrUpdate(v)
                }
            }
        }
    }
}