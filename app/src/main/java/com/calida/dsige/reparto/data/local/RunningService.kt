package com.calida.dsige.reparto.data.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RunningService : RealmObject() {

    @PrimaryKey
    var id: Int = 0
    var estado: Int = 0


}