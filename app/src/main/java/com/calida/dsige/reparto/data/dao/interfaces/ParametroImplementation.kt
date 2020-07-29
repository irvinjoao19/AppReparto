package com.calida.dsige.reparto.data.dao.interfaces

import com.calida.dsige.reparto.data.local.Parametro
import io.realm.RealmResults

interface ParametroImplementation {

    val getAllParametro: RealmResults<Parametro>?

    fun getParametroById(id: Int): Parametro?
}