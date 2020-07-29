package com.calida.dsige.reparto.data.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Reparto : RealmObject {

    @PrimaryKey
    var id_Reparto: Int = 0
    var id_Operario_Reparto: Int = 0
    var foto_Reparto: Int = 0
    var id_observacion: Int = 0
    var Suministro_Medidor_reparto: String = ""
    var Suministro_Numero_reparto: String = ""
    var Direccion_Reparto: String = ""
    var Cod_Orden_Reparto: String = ""
    var Cod_Actividad_Reparto: String = ""
    var Cliente_Reparto: String = ""
    var CodigoBarra: String = ""
    var estado: Int = 0
    var activo: Int = 0
    var latitud: String = ""
    var longitud: String = ""
    var isActive: Boolean = false

    constructor() : super()

    constructor(id_Reparto: Int, id_Operario_Reparto: Int, foto_Reparto: Int, id_observacion: Int, Suministro_Medidor_reparto: String, Suministro_Numero_reparto: String, Direccion_Reparto: String, Cod_Orden_Reparto: String, Cod_Actividad_Reparto: String, Cliente_Reparto: String, CodigoBarra: String , estado: Int, activo: Int) : super() {
        this.id_Reparto = id_Reparto
        this.id_Operario_Reparto = id_Operario_Reparto
        this.foto_Reparto = foto_Reparto
        this.id_observacion = id_observacion
        this.Suministro_Medidor_reparto = Suministro_Medidor_reparto
        this.Suministro_Numero_reparto = Suministro_Numero_reparto
        this.Direccion_Reparto = Direccion_Reparto
        this.Cod_Orden_Reparto = Cod_Orden_Reparto
        this.Cod_Actividad_Reparto = Cod_Actividad_Reparto
        this.Cliente_Reparto = Cliente_Reparto
        this.CodigoBarra = CodigoBarra
        this.estado = estado
        this.activo = activo
    }


}