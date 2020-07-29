package com.calida.dsige.reparto.data.dao.interfaces

import com.calida.dsige.reparto.data.local.Migration

interface MigrationImplementation {

    fun save(migration: Migration)

    fun deleteAll()

    fun deleteAllReparto()

}