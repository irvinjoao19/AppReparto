package com.calida.dsige.reparto.data.dao.interfaces

import androidx.lifecycle.LiveData
import com.calida.dsige.reparto.data.local.*
import io.realm.RealmResults

interface SuministroImplementation {

    fun suministroRepartoUpdate(barCode: String, activo: Int)

    fun suministroRepartoEnvio(codigo: Int, estado: Int)

    val allLectura: RealmResults<SuministroLectura>

    val allCortes: RealmResults<SuministroCortes>

    fun getSuministroLectura(estado: Int, activo: Int, observadas: Int): RealmResults<SuministroLectura>

    fun getTipoSuministroLectura(estado: String, activo: Int): RealmResults<SuministroLectura>

    fun getSuministroCortes(estado: Int, activo: Int): RealmResults<SuministroCortes>

    fun getSuministroCortes(estado: Int, activo: Int,noCortar:Int): RealmResults<SuministroCortes>

    fun CortesNext(orden: Int, activo: Int): RealmResults<SuministroCortes>

    fun suministroLecturaById(id: Int): SuministroLectura

    fun suministroCortesById(id: Int): SuministroCortes

    fun updateActivoSuministroLectura(id: Int, activo: Int)

    fun updateActivoSuministroCortes(id: Int, activo: Int)

    fun suministroLecturaByOrden(orden: Int): SuministroLectura

    fun suministroCortesByOrden(orden: Int): SuministroCortes

    fun countCorte(activo: Int)

    fun searchSuministro(estado: Int, activo: Int, tipo: String, cliente: String): RealmResults<SuministroCortes>


    //Bleker

    val allReparto: RealmResults<Reparto>

    fun suministroRepartoById(id: Int): Reparto

    fun getSuministroReparto(activo: Int): RealmResults<Reparto>

    fun getSuministroRepartoLiveData(activo: Int): LiveData<RealmResults<Reparto>>

    fun suministroRepartoByOrden(orden: Int): Reparto

    fun suministroRepartoUpdate(barCode: String)

    fun getCodigoBarra(codigo: String, activo: Int): Reparto?

    fun suministroRepartoDatos(codigo: String): Reparto

    fun repartoSaved(id: Int, iD_Suministro: Int, iD_Operario: Int, registro_Fecha_SQLITE: String, registro_Latitud: String, registro_Longitud: String, registro_Observacion: String, estado: Int)

    fun repartoSaveService(id: Int, iD_Suministro: Int, iD_Operario: Int, registro_Fecha_SQLITE: String, registro_Latitud: String, registro_Longitud: String, registro_Observacion: String, estado: Int)

    fun getRepartoAll(estado: Int): RealmResults<SendReparto>

    fun closeAllReparto(sendReparto: RealmResults<SendReparto>, estado: Int)


    /**
     * Reconexion
     */
    val allReconexion: RealmResults<SuministroReconexion>

    fun getSuministroReconexion(estado: Int, activo: Int): RealmResults<SuministroReconexion>

    fun suministroReconexionById(id: Int): SuministroReconexion

    fun updateActivoSuministroReconexion(id: Int, activo: Int)

    fun suministroReconexionByOrden(orden: Int): SuministroReconexion

    fun ReconexionesSearch(estado: String, activo: Int, cliente: String): RealmResults<SuministroReconexion>

    fun ReconexionesNext(orden: Int, activo: Int): RealmResults<SuministroReconexion>


    /**
     *Lectura
     *  */
    fun LecturaNext(orden: Int, activo: Int): RealmResults<SuministroLectura>?

    /**
     * Siguiente Orden by operario_Orden
     * estos metodos servirán para validar el servicio de sincronizacion de cortes y reconexiones
     * y futuro para lecturas y reLecturas
     */

    fun buscarLecturaByOrden(orden: Int, activo: Int): SuministroLectura

    fun buscarCortesByOrden(orden: Int, activo: Int): SuministroCortes

    fun buscarReconexionesByOrden(orden: Int, activo: Int): SuministroReconexion

    // TODO Grandes Clientes

    fun getGrandesClientes(): RealmResults<GrandesClientes>

    fun getClienteById(id: Int): GrandesClientes

    fun getMarca(): RealmResults<Marca>

    fun getMarcaById(id: Int): Marca


    fun getSuministroReclamos(tipo: String, activo: Int): RealmResults<SuministroLectura>

    fun getLecturaOnCount(activo: Int, type: Int): Long?

    fun getLecturaReclamoOnCount(activo: Int, type: String): Long?

    fun getRepartoActive(activo: Int?): Long?

    fun getLecturaRecuperadas(s: String, s1: String, i: Int, i1: Int): Long

    fun getLecturaObservadas(activo: Int,observadas: Int): Long?

}
