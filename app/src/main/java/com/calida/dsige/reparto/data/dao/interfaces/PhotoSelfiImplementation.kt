package com.calida.dsige.reparto.data.dao.interfaces

import com.calida.dsige.reparto.data.local.PhotoSelfi

interface PhotoSelfiImplementation {

    fun save(photo: PhotoSelfi)

    fun delete(id: Int)

    fun getPhotoIdentity(): Int

    fun photoById(id: Int): PhotoSelfi

    fun closePhoto(photo:PhotoSelfi,estado: Int)

}