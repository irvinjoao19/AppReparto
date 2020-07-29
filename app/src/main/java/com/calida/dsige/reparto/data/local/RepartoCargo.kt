package com.calida.dsige.reparto.data.local

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

open class RepartoCargo : RealmObject() {

    @PrimaryKey
    var cargoId: Int = 0
    var tipoCargoId: Int = 0
    var repartoId: Int = 0
    var suministroNumero: String = ""
    var nombreApellido: String = ""
    var dni: String = ""
    var predio: String = ""
    var quienRecibeCargo: Int = 0
    var nombreQuienRecibe: String = ""
    var nombreEmpresa: String = ""
    var lecturaCargo: String = ""
    var latitud: String = ""
    var longitud: String = ""
    var fecha: String = ""
    var hora: String = ""
    var fechaMovil: String = ""
    var estado: Int = 0
    var photos: RealmList<RepartoCargoFoto>? = null
    var suministros: RealmList<RepartoCargoSuministro>? = null
}