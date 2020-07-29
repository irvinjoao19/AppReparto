package com.calida.dsige.reparto.data.dao.overMethod

import com.calida.dsige.reparto.data.dao.interfaces.ParametroImplementation
import com.calida.dsige.reparto.data.local.Parametro
import io.realm.Realm
import io.realm.RealmResults

class ParametroOver(private val realm: Realm) : ParametroImplementation {

    override val getAllParametro: RealmResults<Parametro>?
        get() = realm.where(Parametro::class.java).findAll()

    override fun getParametroById(id: Int): Parametro? {
        return realm.where(Parametro::class.java).equalTo("id_Configuracion", id).findFirst()
    }
}