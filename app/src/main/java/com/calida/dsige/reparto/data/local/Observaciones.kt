package com.calida.dsige.reparto.data.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Observaciones : RealmObject() {

    @PrimaryKey
    var observacionId: Int = 0
    var abreviatura: String = ""
    var descripcion: String = ""
    var estado: Int = 0
}