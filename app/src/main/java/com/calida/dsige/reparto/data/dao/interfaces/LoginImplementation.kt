package com.calida.dsige.reparto.data.dao.interfaces

import com.calida.dsige.reparto.data.local.Login
import com.calida.dsige.reparto.data.local.Operario
import io.realm.RealmQuery
import io.realm.RealmResults

interface LoginImplementation {

    val login: Login?

    fun save(login: Login)

    fun ifExistLogin(login: Login): Boolean

    fun delete()

    fun getIdUser(): RealmQuery<Login>?

    fun updateLogin(valor : Int)

    // TODO about Operarios

    fun saveOperarios(operarios: List<Operario>)

    fun getAllOperarios(): RealmResults<Operario>

    fun updateOperario(operario: Operario,value : Int)



}
