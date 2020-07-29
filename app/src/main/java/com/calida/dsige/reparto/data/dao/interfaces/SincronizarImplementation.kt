package com.calida.dsige.reparto.data.dao.interfaces

import com.calida.dsige.reparto.data.local.Sincronizar
import com.calida.dsige.reparto.data.local.SuministroLectura

interface SincronizarImplementation {

    fun save(sincronizar: Sincronizar, activo: Int)

    fun saveLecturasEncontradas(suministroLecturas: List<SuministroLectura>, activo: Int)
}
