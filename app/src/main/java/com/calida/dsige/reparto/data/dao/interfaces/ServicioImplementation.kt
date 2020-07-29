package com.calida.dsige.reparto.data.dao.interfaces

import com.calida.dsige.reparto.data.local.Mega
import com.calida.dsige.reparto.data.local.Servicio
import io.realm.RealmResults

interface ServicioImplementation {

    val servicioAll: RealmResults<Servicio>

    fun insertServicio(s: Servicio)

    fun saveBytes(bytes: Int, usuario: Int)

    fun updateBytes(bytes: Int, fecha: String)

    fun getBytesIdentity(): Int

    fun getMega(): Mega

}
