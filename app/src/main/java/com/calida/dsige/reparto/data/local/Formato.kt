package com.calida.dsige.reparto.data.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Formato : RealmObject() {

    @PrimaryKey
    var formatoId: Int = 0
    var tipo: Int = 0
    var nombre: String = ""
    var abreviatura: String = ""
    var estado: Int = 0
}