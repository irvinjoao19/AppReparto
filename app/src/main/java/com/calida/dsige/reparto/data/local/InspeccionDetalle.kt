package com.calida.dsige.reparto.data.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class InspeccionDetalle : RealmObject() {

    @PrimaryKey
    var inspeccionDetalleId: Int = 0
    var inspeccionId: Int = 0
    var checkListId: Int = 0
    var estadoCheckList: Int = 0
    var nombreEstadoCheckList: String = ""
    var estado2CheckList: Int = 0
    var nombreEstado2CheckList: String = ""
    var descripcion: String = ""
    var fecha: String = ""
    var observacion: String = ""
    var observacion2: String = ""
    var estado: Int = 0
    var usuarioId: Int = 0
    var fechaVerificacion: String = ""
    var aplicaFecha: Int = 0
    var aplicaObs1: Int = 0
    var aplicaObs2: Int = 0
    var campo1: String = ""
    var campo2: String = ""
}