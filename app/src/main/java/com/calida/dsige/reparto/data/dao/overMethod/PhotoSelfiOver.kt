package com.calida.dsige.reparto.data.dao.overMethod

import com.calida.dsige.reparto.data.dao.interfaces.PhotoSelfiImplementation
import com.calida.dsige.reparto.data.local.PhotoSelfi
import io.realm.Realm

class PhotoSelfiOver(private val realm: Realm) : PhotoSelfiImplementation {


    override fun delete(id: Int) {
        realm.executeTransaction {
            val photo: PhotoSelfi? = realm.where(PhotoSelfi::class.java).equalTo("id", id).findFirst()
            photo?.deleteFromRealm()
        }
    }

    override fun getPhotoIdentity(): Int {
        val photo = realm.where(PhotoSelfi::class.java).max("id")
        val result: Int
        result = if (photo == null) {
            1
        } else {
            photo.toInt() + 1
        }
        return result
    }

    override fun save(photo: PhotoSelfi) {
        realm.executeTransaction { realm ->
            realm.copyToRealmOrUpdate(photo)
        }
    }

    override fun photoById(id: Int): PhotoSelfi {
        return realm.where(PhotoSelfi::class.java).equalTo("iD_Foto", id).findFirst()!!
    }

    override fun closePhoto(photo: PhotoSelfi, estado: Int) {
        realm.executeTransaction {
           // photo.estado = estado
        }
    }
}