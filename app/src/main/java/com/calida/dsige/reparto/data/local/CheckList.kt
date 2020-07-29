package com.calida.dsige.reparto.data.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CheckList : RealmObject() {

    @PrimaryKey
    var checkListId: Int = 0
    var formatoId: Int = 0
    var titulo: Int = 0
    var codigo: Int = 0
    var descripcion: String = ""
    var orden: Int = 0
    var aplicaFecha: Int = 0
    var aplicaObs1: Int = 0
    var aplicaObs2: Int = 0
    var grupo: Int = 0
    var campo1: String = ""
    var campo2: String = ""
    var estado: Int = 0
    var valor1: String = ""
    var valor2: String = ""
    var valor3: String = ""
    var valor4: String = ""
    // var groupButtonIdOne: Int = 0
    // var groupButtonIdTwo: Int = 0
    // var position: Int = 0
    // var isSelectedGroupOne: Boolean = false
    // var isSelectedGroupTwo: Boolean = false
    var isSelectedSI: Boolean = false
    var isSelectedNO: Boolean = false
    var isSelectedNA: Boolean = false
    var isSelectedTA: Boolean = false

    var isSelectedB: Boolean = false
    var isSelectedM: Boolean = false
    var isSelectedNA2: Boolean = false

    var isActive: Boolean = false
}