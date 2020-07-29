package com.calida.dsige.reparto.data.dao.overMethod

import com.calida.dsige.reparto.data.dao.interfaces.SincronizarImplementation
import com.calida.dsige.reparto.data.local.Sincronizar
import com.calida.dsige.reparto.data.local.SuministroCortes
import com.calida.dsige.reparto.data.local.SuministroLectura
import com.calida.dsige.reparto.data.local.SuministroReconexion
import io.realm.Realm

class SincronizarOver(private val realm: Realm) : SincronizarImplementation {

    override fun save(sincronizar: Sincronizar, activo: Int) {
        realm.executeTransaction { realm ->
            for (suministroCortes: SuministroCortes in sincronizar.suministrosCortes!!) {
                val cortes: SuministroCortes? = realm.where(SuministroCortes::class.java).equalTo("iD_Suministro", suministroCortes.iD_Suministro).equalTo("activo", activo).findFirst()
                if (cortes == null) {
                    realm.copyToRealmOrUpdate(suministroCortes)
                }
            }

            for (suministroReconexion: SuministroReconexion in sincronizar.suministroReconexion!!) {
                val reconexion: SuministroReconexion? = realm.where(SuministroReconexion::class.java).equalTo("iD_Suministro", suministroReconexion.iD_Suministro).equalTo("activo", activo).findFirst()
                if (reconexion == null) {//
                    realm.copyToRealmOrUpdate(suministroReconexion)
                }
            }
        }
    }

    override fun saveLecturasEncontradas(suministroLecturas: List<SuministroLectura>, activo: Int) {
        realm.executeTransaction { realm ->
            for (lectura: SuministroLectura in suministroLecturas) {
                val lecturas: SuministroLectura? = realm.where(SuministroLectura::class.java).equalTo("iD_Suministro", lectura.iD_Suministro).equalTo("activo", activo).findFirst()
                if (lecturas == null) {
                    realm.copyToRealmOrUpdate(lectura)
                }
            }
        }
    }
}