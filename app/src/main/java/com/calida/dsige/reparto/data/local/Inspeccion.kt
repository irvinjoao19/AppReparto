package com.calida.dsige.reparto.data.local

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Inspeccion : RealmObject() {

    @PrimaryKey
    var id: Int = 0
    var inspeccionId: Int = 0
    var nroInspeccion: String = ""
    var fecha: String = ""
    var operarioLecturaId: Int = 0
    var clienteId: Int = 0

    var titulo: String = ""
    var lugar: String = ""
    var chkCharlaPre: Int = 0
    var chkRevisarEpp: Int = 0
    var estado: Int = 0
    var usuarioId: Int = 0
    var identity: Int = 0

    var areaId: Int = 0
    var nombreArea: String = ""
    var trasladoId: Int = 0
    var nombreTraslado: String = ""
    var vehiculoId: Int = 0
    var placa: String = ""
    var marca: String = ""
    var kilometraje: String = ""

    var detalles: RealmList<InspeccionDetalle>? = null
    var adicionales: RealmList<InspeccionAdicionales>? = null

    var vFecha: String = ""
    var vHora: String = ""
    var active: Int = 0
    var fechaVerificacion: String = ""

    var nombreOperario : String = ""
}