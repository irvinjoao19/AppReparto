package com.calida.dsige.reparto.data.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Vehiculo : RealmObject() {
    @PrimaryKey
    var vehiculoId: Int = 0
    var placa: String = ""
    var marca: String = ""
    var modelo: String = ""
    var anioFabricacion: String = ""
    var cilindrada: String = ""
    var inspeccionTecnica: String = ""
    var soat: String = ""
    var estado: Int = 0
}