package com.calida.dsige.reparto.data.local

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Migration : RealmObject() {

    @PrimaryKey
    var migrationId: Int = 0
    var servicios: RealmList<Servicio>? = null
    var parametros: RealmList<Parametro>? = null
    var suministroLecturas: RealmList<SuministroLectura>? = null
    var suministroCortes: RealmList<SuministroCortes>? = null
    var tipoLecturas: RealmList<TipoLectura>? = null
    var detalleGrupos: RealmList<DetalleGrupo>? = null
    var suministroReconexiones: RealmList<SuministroReconexion>? = null
    var repartoLectura: RealmList<Reparto>? = null
    var motivos: RealmList<Motivo>? = null
    var estados: RealmList<Estado>? = null
    var checkList: RealmList<CheckList>? = null
    var formatos: RealmList<Formato>? = null
    var clientes: RealmList<GrandesClientes>? = null
    var marcas: RealmList<Marca>? = null
    var areas: RealmList<Area>? = null
    var tipoTraslados: RealmList<TipoTraslado>? = null
    var vehiculos: RealmList<Vehiculo>? = null
    var observaciones: RealmList<Observaciones>? = null
    var mensaje: String? = ""

}
