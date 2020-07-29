package com.calida.dsige.reparto.data.dao.interfaces

import android.content.Context
import androidx.lifecycle.LiveData
import com.calida.dsige.reparto.data.local.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.realm.RealmResults
import java.io.File

interface RegistroImplementation {

    fun getRegistroIdentity(): Int

    fun save(registro: Registro)

    fun saveZonaPeligrosa(registro: Registro)

    fun updateRegistroDesplaza(id: Int, tipo: Int, value: String, estado: Int)

    fun getRegistroByOrden(orden: Int, tipo: Int): Registro?

    fun getRegistro(orden: Int, tipo: Int, recupero: Int): Registro?

    fun confirmRegistro(orden: Int, tipo: Int): Boolean

    fun getAllRegistro(estado: Int): RealmResults<Registro>

    fun getResultInspeccion(operarioId: Int, estado: Int, active: Int): RealmResults<Inspeccion>

    fun getAllRegistroLiveData(estado: Int): LiveData<RealmResults<Registro>>

    fun closeAllRegistro(registros: RealmResults<Registro>, estado: Int)

    fun closeOneRegistro(registro: Registro, estado: Int)

    fun closeInspeccion(id: Int)

    fun getRepartoIdentity(): Int?

    fun getAllRegistroReparto(estado: Int): RealmResults<SendReparto>?

    fun closeAllRegistroReparto(registros: RealmResults<SendReparto>, estado: Int)

    fun getSuministro(iD_Suministro: Int): Registro?

    // TODO SOBRE ACTA DE CONFORMIDAD

    fun saveActaConformidad(iD_Suministro: Int, horaActa: String, estado: Int)

    // TODO SOBRE SUMINISTOR ENCONTRADO

    fun saveSuministroEncontrado(registro: Registro)

    fun confirmSuministroEncontrado(iD_Suministro: Int): Boolean

    // TODO RX JAVA

    fun getAllRegistroRx(estado: Int): Observable<RealmResults<Registro>>

    fun getAllInspeccion(usuarioId:Int,estado: Int): Observable<RealmResults<Inspeccion>>

    fun updateRegistroTienePhoto(cantidad: Int, estado: String, registro: Registro): Registro

    fun closePhotoEstado(estado: Int, p: Photo)

    fun getRegistroByOrdenRx(orden: Int, tipo: Int): Observable<Registro>

    // TODO REPARTO

    fun getInicioFinTrabajo(usuario: Int, fecha: String, observacion: String): Registro?

    // TODO GRANDES CLIENTE

    fun getClienteById(clienteId: Int): Observable<GrandesClientes>

    fun updateClientes(g: GrandesClientes): Completable

    fun getGoTrabajo(usuarioId: Int, fecha: String, tipo: Int, observacion: String): Registro?

    fun getSelfie(state: Int, name: String): Observable<Registro>

    fun closeFileClienteById(id: Int) : Completable

    fun getAllPhotos(context : Context): Observable<ArrayList<String>>
}