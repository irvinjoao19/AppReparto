package com.calida.dsige.reparto.data.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RepartoCargoSuministro : RealmObject {

    @PrimaryKey
    var cargoSuministroId: Int = 0
    var repartoId: Int = 0
    var suministroNumero: String = ""
    var estado: Int = 0

    constructor() : super()

    constructor(repartoId: Int, suministroNumero: String, estado: Int) : super() {
        this.repartoId = repartoId
        this.suministroNumero = suministroNumero
        this.estado = estado
    }
}