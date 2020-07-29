package com.calida.dsige.reparto.data.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Servicio : RealmObject {

    @PrimaryKey
    var id_servicio: Int = 0
    var nombre_servicio: String = ""
    var estado: Int = 0

    constructor()

    constructor(id_servicio: Int, nombre_servicio: String, estado: Int) {
        this.id_servicio = id_servicio
        this.nombre_servicio = nombre_servicio
        this.estado = estado
    }
}
