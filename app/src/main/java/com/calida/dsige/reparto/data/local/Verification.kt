package com.calida.dsige.reparto.data.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Verification : RealmObject {

    @PrimaryKey
    var id: Int = 0
    var fecha: String = ""
    var usuarioId: Int = 0

    constructor() : super()

    constructor(id: Int, fecha: String, usuarioId: Int) : super() {
        this.id = id
        this.fecha = fecha
        this.usuarioId = usuarioId
    }
}