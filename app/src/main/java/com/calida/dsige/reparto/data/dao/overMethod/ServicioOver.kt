package com.calida.dsige.reparto.data.dao.overMethod

import com.calida.dsige.reparto.data.dao.interfaces.ServicioImplementation
import com.calida.dsige.reparto.data.local.Mega
import com.calida.dsige.reparto.data.local.Servicio
import com.calida.dsige.reparto.helper.Util
import io.realm.Realm
import io.realm.RealmResults

class ServicioOver(private val realm: Realm) : ServicioImplementation {

    override val servicioAll: RealmResults<Servicio>
        get() = realm.where(Servicio::class.java).findAll()

    override fun insertServicio(s: Servicio) {
        realm.executeTransaction { r ->
            r.copyToRealmOrUpdate(s)
        }
    }

    override fun saveBytes(bytes: Int, usuario: Int) {
        realm.executeTransaction { realm ->
            val b = realm.where(Mega::class.java).findFirst()
            if (b == null) {
                val b2 = Mega(getBytesIdentity(), bytes, usuario, Util.getFechaActual())
                realm.copyToRealmOrUpdate(b2)
            }
        }
    }

    override fun updateBytes(bytes: Int, fecha: String) {
        realm.executeTransaction { realm ->
            val b = realm.where(Mega::class.java).findFirst()
            if (b != null) {
                b.bytes = b.bytes!! + bytes
                b.fecha = fecha
            }
        }
    }

    override fun getBytesIdentity(): Int {
        val max = realm.where(Mega::class.java).max("megaId")
        return if (max == null) 1 else max.toInt() + 1
    }

    override fun getMega(): Mega {
        return realm.where(Mega::class.java).findFirst()!!
    }
}
