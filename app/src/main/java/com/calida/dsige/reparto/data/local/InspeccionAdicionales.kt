package com.calida.dsige.reparto.data.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class InspeccionAdicionales : RealmObject() {

    @PrimaryKey
    var inspeccionAdicionalId: Int = 0
    var inspeccionId: Int = 0
    var tipo: Int = 0
    var nombreTipo: String = ""
    var nombreValor : String = ""
    var descripcion: String = ""
    var estado: Int = 0
    var usuarioId: Int = 0
    var fechaVerificacion: String = ""
}