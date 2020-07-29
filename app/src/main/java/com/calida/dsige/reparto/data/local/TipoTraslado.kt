package com.calida.dsige.reparto.data.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TipoTraslado : RealmObject() {

    @PrimaryKey
    var trasladoId: Int = 0
    var nombre: String = ""
    var estado: Int = 0
}