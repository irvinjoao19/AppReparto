package com.calida.dsige.reparto.data.dao.overMethod

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.calida.dsige.reparto.data.RealmLiveData
import com.calida.dsige.reparto.data.dao.interfaces.RegistroImplementation
import com.calida.dsige.reparto.data.local.*
import com.calida.dsige.reparto.helper.Util
import io.reactivex.Completable
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmResults
import java.io.File

class RegistroOver(private val realm: Realm) : RegistroImplementation {

    override fun getRegistroIdentity(): Int {
        val registro = realm.where(Registro::class.java).max("id")
        val result: Int
        result = if (registro == null) {
            1
        } else {
            registro.toInt() + 1
        }
        return result
    }

    override fun save(registro: Registro) {
        realm.executeTransaction { realm ->
            if (!confirmRegistro(registro.orden, registro.tipo)) {
                realm.copyToRealmOrUpdate(registro)
            } else {
                val updateRegistro: Registro? = getRegistroByOrden(registro.orden, registro.tipo)
                updateRegistro?.registro_Desplaza = registro.registro_Desplaza
                updateRegistro?.registro_Lectura = registro.registro_Lectura
                updateRegistro?.registro_Confirmar_Lectura = registro.registro_Confirmar_Lectura
                updateRegistro?.registro_Observacion = registro.registro_Observacion
                updateRegistro?.grupo_Incidencia_Codigo = registro.grupo_Incidencia_Codigo
                updateRegistro?.codigo_Resultado = registro.codigo_Resultado
                updateRegistro?.registro_Constancia = registro.registro_Constancia
                updateRegistro?.registro_TieneFoto = registro.registro_TieneFoto
                updateRegistro?.estado = registro.estado
                updateRegistro?.motivoId = registro.motivoId
                updateRegistro?.registro_Fecha_SQLITE = registro.registro_Fecha_SQLITE
            }
        }
    }

    override fun saveZonaPeligrosa(registro: Registro) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(registro)
        }
    }

    override fun updateRegistroDesplaza(id: Int, tipo: Int, value: String, estado: Int) {
        realm.executeTransaction { realm ->
            val registro: Registro = realm.where(Registro::class.java).equalTo("iD_Suministro", id).equalTo("tipo", tipo).findFirst()!!
            registro.registro_Desplaza = value
            registro.estado = estado
        }
    }

    override fun getRegistroByOrden(orden: Int, tipo: Int): Registro? {
        return realm.where(Registro::class.java).equalTo("orden", orden).equalTo("tipo", tipo).findFirst()
    }

    override fun getRegistro(orden: Int, tipo: Int, recupero: Int): Registro? {
        return if (recupero == 10) {
            realm.where(Registro::class.java).equalTo("orden", orden).equalTo("tipo", recupero).findFirst()
        } else {
            realm.where(Registro::class.java).equalTo("orden", orden).equalTo("tipo", tipo).findFirst()
        }
    }

    override fun confirmRegistro(orden: Int, tipo: Int): Boolean {
        var result = false
        val registro: Registro? = realm.where(Registro::class.java).equalTo("orden", orden).equalTo("tipo", tipo).findFirst()
        if (registro != null) {
            result = true
        }
        return result
    }

    override fun getAllRegistro(estado: Int): RealmResults<Registro> {
        return realm.where(Registro::class.java).equalTo("estado", estado).findAll()
    }

    override fun getResultInspeccion(operarioId: Int, estado: Int, active: Int): RealmResults<Inspeccion> {
        return realm.where(Inspeccion::class.java)
                .equalTo("operarioLecturaId", operarioId)
                .equalTo("estado", estado)
                .equalTo("active", active)
                .equalTo("fechaVerificacion", Util.getFecha()).findAll()
    }

    override fun getAllRegistroLiveData(estado: Int): LiveData<RealmResults<Registro>> {
        return RealmLiveData(realm.where(Registro::class.java).equalTo("estado", estado).findAllAsync())
    }

    override fun closeAllRegistro(registros: RealmResults<Registro>, estado: Int) {
        realm.executeTransaction {
            val r = registros.createSnapshot()
            if (r != null) {
                for (i in r.indices) {
                    r[i]?.estado = estado
                }
            }
        }
    }

    override fun closeOneRegistro(registro: Registro, estado: Int) {
        realm.executeTransaction {
            if (registro.tipo == 1 || registro.tipo == 10) {
                if (registro.grupo_Incidencia_Codigo == "2" || registro.grupo_Incidencia_Codigo == "21") {
                    val suministro = realm.where(SuministroLectura::class.java).equalTo("suministro_Numero", registro.suministro_Numero.toString()).findFirst()
                    if (suministro != null) {
                        suministro.activo = 1
                        suministro.estado = 10
                        registro.tipo = 10
                    }
                }
            }

            registro.estado = estado
            registro.registro_Desplaza = "1"

            if (registro.photos != null) {
                val photos = registro.photos?.createSnapshot()

                for (p in photos!!) {
                    if (p.estado != 2) {
                        p.estado = 0
                    }
                    if (registro.grupo_Incidencia_Codigo == "2" || registro.grupo_Incidencia_Codigo == "21") {
                        p.tipo = 10
                    }
                }
            }
        }
    }

    override fun closeInspeccion(id: Int) {
        realm.executeTransaction { r ->
            val i = r.where(Inspeccion::class.java).equalTo("id", id).findFirst()
            if (i != null) {
                i.active = 1
//                val detalles = i.detalles
//                if (detalles != null) {
//                    val de = detalles.createSnapshot()
//                    for (d: InspeccionDetalle in de) {
//                        d.estado = 0
//                    }
//                }
//                val adicionales = i.adicionales
//                if (adicionales != null) {
//                    val add = adicionales.createSnapshot()
//                    for (a: InspeccionAdicionales in add) {
//                        a.estado = 0
//                    }
//                }
            }
        }
    }


    override fun getRepartoIdentity(): Int? {
        val reparto = realm.where(SendReparto::class.java).max("id_Reparto")
        val result: Int
        result = if (reparto == null) {
            1
        } else {
            reparto.toInt() + 1
        }
        return result
    }

    override fun getAllRegistroReparto(estado: Int): RealmResults<SendReparto>? {
        return realm.where(SendReparto::class.java).equalTo("estado", estado).findAll()
    }

    override fun closeAllRegistroReparto(registros: RealmResults<SendReparto>, estado: Int) {
        realm.executeTransaction {
            val r = registros.createSnapshot()
            if (r != null) {
                for (i in r.indices) {
                    r[i]?.estado = estado
                }
            }
        }
    }

    override fun saveActaConformidad(iD_Suministro: Int, horaActa: String, estado: Int) {
        realm.executeTransaction {
            val updateSuministro: Registro? = getSuministro(iD_Suministro)
            updateSuministro?.horaActa = horaActa
            updateSuministro?.estado = estado
        }
    }

    override fun saveSuministroEncontrado(registro: Registro) {
        realm.executeTransaction { realm ->
            if (!confirmSuministroEncontrado(registro.iD_Suministro)) {
                realm.copyToRealmOrUpdate(registro)
            } else {
                val updateSuministro: Registro? = getSuministro(registro.iD_Suministro)
                updateSuministro?.registro_Constancia = registro.registro_Constancia
                updateSuministro?.suministroCliente = registro.suministroCliente
                updateSuministro?.suministroDireccion = registro.suministroDireccion
                updateSuministro?.registro_Observacion = registro.registro_Observacion
                updateSuministro?.estado = registro.estado
            }
        }
    }

    override fun confirmSuministroEncontrado(iD_Suministro: Int): Boolean {
        var result = false
        val suministroEncontrado: Registro? = realm.where(Registro::class.java).equalTo("iD_Suministro", iD_Suministro).findFirst()
        if (suministroEncontrado != null) {
            result = true
        }
        return result
    }

    override fun getSuministro(iD_Suministro: Int): Registro? {
        return realm.where(Registro::class.java).equalTo("iD_Suministro", iD_Suministro).findFirst()
    }

    override fun getAllRegistroRx(estado: Int): Observable<RealmResults<Registro>> {
        return Observable.create { emitter ->
            val r = Realm.getDefaultInstance()
            try {
                val a = r.where(Registro::class.java).equalTo("estado", estado).findAll()
                emitter.onNext(a)
                emitter.onComplete()
            } catch (e: Throwable) {
                emitter.onError(e)
            } finally {
                r.close()
            }
        }
    }

    override fun getAllInspeccion(usuarioId: Int, estado: Int): Observable<RealmResults<Inspeccion>> {
        return Observable.create { emitter ->
            val r = Realm.getDefaultInstance()
            try {
                val a: RealmResults<Inspeccion> = r.where(Inspeccion::class.java).equalTo("operarioLecturaId", usuarioId).equalTo("estado", estado).findAll()
                emitter.onNext(a)
                emitter.onComplete()
            } catch (e: Throwable) {
                emitter.onError(e)
            } finally {
                r.close()
            }
        }
    }

    /**
     * @estado == 0
     * La foto fue eliminado del celular y se cambiara de estado a 0
     * para que solo se envie el registro y que el sistema verifique que solo se guardo el registro
     */
    override fun updateRegistroTienePhoto(cantidad: Int, estado: String, registro: Registro): Registro {
        if (cantidad == 0) {
            if (estado == "0") {
                realm.executeTransaction {
                    registro.registro_TieneFoto = estado
                }
            }
        }
        return registro
    }

    /**
     * @estado == 0
     * Cuando la foto es eliminada se cambia de estado a 0
     * para que en el servidor no se pueda guardar el registro de la foto
     */
    override fun closePhotoEstado(estado: Int, p: Photo) {
        realm.executeTransaction {
            p.estado = estado
        }
    }

    override fun getRegistroByOrdenRx(orden: Int, tipo: Int): Observable<Registro> {
        return Observable.create { emitter ->
            try {
                Realm.getDefaultInstance().use { realm ->
                    val a = realm.where(Registro::class.java).equalTo("orden", orden).equalTo("tipo", tipo).findFirst()!!
                    emitter.onNext(a)
                    emitter.onComplete()
                }
            } catch (e: Throwable) {
                emitter.onError(e)
            }
        }
    }

    override fun getInicioFinTrabajo(usuario: Int, fecha: String, observacion: String): Registro? {
        return realm.where(Registro::class.java)
                .equalTo("iD_Suministro", usuario)
                .equalTo("iD_Operario", usuario)
                .equalTo("registro_Observacion", observacion)
                .beginGroup()
                .contains("registro_Fecha_SQLITE", fecha)
                .endGroup()
                .findFirst()
    }

    override fun getClienteById(clienteId: Int): Observable<GrandesClientes> {
        return Observable.create { emitter ->
            try {
                Realm.getDefaultInstance().use { realm ->
                    val a = realm.where(GrandesClientes::class.java).equalTo("clienteId", clienteId).findFirst()!!
                    emitter.onNext(a)
                    emitter.onComplete()
                }
            } catch (e: Throwable) {
                emitter.onError(e)
            }
        }
    }

    override fun updateClientes(g: GrandesClientes): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { rr ->
                    val c: GrandesClientes? = rr.where(GrandesClientes::class.java).equalTo("clienteId", g.clienteId).findFirst()
                    if (c != null) {
                        c.codigoEMR = g.codigoEMR
                        c.fechaRegistroInicio = g.fechaRegistroInicio
                        c.clientePermiteAcceso = g.clientePermiteAcceso
                        c.fotoConstanciaPermiteAcceso = g.fotoConstanciaPermiteAcceso
                        c.porMezclaExplosiva = g.porMezclaExplosiva
                        c.vManoPresionEntrada = g.vManoPresionEntrada
                        c.fotovManoPresionEntrada = g.fotovManoPresionEntrada
                        c.marcaCorrectorId = g.marcaCorrectorId
                        c.fotoMarcaCorrector = g.fotoMarcaCorrector
                        c.vVolumenSCorreUC = g.vVolumenSCorreUC
                        c.fotovVolumenSCorreUC = g.fotovVolumenSCorreUC
                        c.vVolumenSCorreMedidor = g.vVolumenSCorreMedidor
                        c.fotovVolumenSCorreMedidor = g.fotovVolumenSCorreMedidor
                        c.vVolumenRegUC = g.vVolumenRegUC
                        c.fotovVolumenRegUC = g.fotovVolumenRegUC
                        c.vPresionMedicionUC = g.vPresionMedicionUC
                        c.fotovPresionMedicionUC = g.fotovPresionMedicionUC
                        c.vTemperaturaMedicionUC = g.vTemperaturaMedicionUC
                        c.fotovTemperaturaMedicionUC = g.fotovTemperaturaMedicionUC
                        c.tiempoVidaBateria = g.tiempoVidaBateria
                        c.fotoTiempoVidaBateria = g.fotoTiempoVidaBateria
                        c.fotoPanomarica = g.fotoPanomarica
                        c.tieneGabinete = g.tieneGabinete
                        c.foroSitieneGabinete = g.foroSitieneGabinete
                        c.presenteCliente = g.presenteCliente
                        c.contactoCliente = g.contactoCliente
                        c.latitud = g.latitud
                        c.longitud = g.longitud
                        c.estado = g.estado
                        c.comentario = g.comentario
                        c.fotoInicioTrabajo = g.fotoInicioTrabajo
                        c.confirmarVolumenSCorreUC = g.confirmarVolumenSCorreUC
                        c.observacionId = g.observacionId
                        c.nombreObservaciones = g.nombreObservaciones
                    }
                }
            }
        }
    }

    override fun getGoTrabajo(usuarioId: Int, fecha: String, tipo: Int, observacion: String): Registro? {
        return realm.where(Registro::class.java)
                .equalTo("iD_Suministro", usuarioId)
                .equalTo("iD_Operario", usuarioId)
                .equalTo("registro_Observacion", observacion)
                .equalTo("tipo", tipo)
                .beginGroup()
                .contains("registro_Fecha_SQLITE", fecha)
                .endGroup()
                .findFirst()
    }

    override fun getSelfie(state: Int, name: String): Observable<Registro> {
        return Observable.create { emitter ->
            val r = Realm.getDefaultInstance()
            try {
                val a = r.where(Registro::class.java).equalTo("tipo", state).equalTo("registro_Observacion", name).findFirst()!!
                emitter.onNext(a)
                emitter.onComplete()
            } catch (e: Throwable) {
                emitter.onError(e)
            } finally {
                r.close()
            }
        }
    }

    override fun closeFileClienteById(id: Int): Completable {
        return Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                realm.executeTransaction { rr ->
                    val c: GrandesClientes? = rr.where(GrandesClientes::class.java).equalTo("clienteId", id).findFirst()
                    if (c != null) {
                        c.estado = 7
                    }
                }
            }
        }
    }

    override fun getAllPhotos(context: Context): Observable<ArrayList<String>> {
        return Observable.create { e ->
            val filePaths: ArrayList<String> = ArrayList()
            val directory = Util.getFolder(context)
            val files = directory.listFiles()
            if (files == null) {
                e.onError(Throwable("No hay fotos en la carpeta"))
                e.onComplete()
                return@create
            }

            for (i in files.indices) {
                filePaths.add(files[i].toString())
            }
            e.onNext(filePaths)
            e.onComplete()
        }
    }
}