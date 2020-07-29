package com.calida.dsige.reparto.data.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RepartoCargoFoto : RealmObject {

    @PrimaryKey
    var fotoCargoId: Int = 0
    var cargoId: Int = 0
    var rutaFoto: String = ""
    var tipo: Int = 0 // 1 foto 2 firma
    var estado: Int = 0

    constructor() : super()

    constructor(fotoCargoId: Int, cargoId: Int, rutaFoto: String, tipo: Int, estado: Int) : super() {
        this.fotoCargoId = fotoCargoId
        this.cargoId = cargoId
        this.rutaFoto = rutaFoto
        this.tipo = tipo
        this.estado = estado
    }
}