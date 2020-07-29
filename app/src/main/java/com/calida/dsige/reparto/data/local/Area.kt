package com.calida.dsige.reparto.data.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Area : RealmObject() {

    @PrimaryKey
    var areaId: Int = 0
    var nombre: String = ""
    var estado: Int = 0
}