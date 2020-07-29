package com.calida.dsige.reparto.ui.listeners

import android.view.View
import com.calida.dsige.reparto.data.local.*

interface OnItemClickListener {

    interface RepartoCargoFotoListener {
        fun onItemClick(f: RepartoCargoFoto, view: View, position: Int)
    }

    interface PhotoListener {
        fun onItemClick(f: Photo, view: View, position: Int)
    }

    interface MotivoListener {
        fun onItemClick(m: Motivo, view: View, position: Int)
    }

    interface CheckListListener {
        fun onItemClick(e: CheckList, v: View, position: Int)
    }

    interface InspeccionAdicionalListener {
        fun onItemClick(i: InspeccionAdicionales, view: View, position: Int)
    }

    interface InspeccionDetalleListener {
        fun onItemClick(d: InspeccionDetalle, view: View, position: Int)
    }

    interface RepartoListener {
        fun onItemClick(r: Reparto, v: View, position: Int)
    }

    interface MenuListener {
        fun onItemClick(m: MenuPrincipal, v: View, position: Int)
    }

    interface FormatoListener {
        fun onItemClick(f: Formato, v: View, position: Int)
    }

    interface ClientesListener {
        fun onItemClick(c: GrandesClientes, v: View, position: Int)
    }

    interface MarcaListener {
        fun onItemClick(m: Marca, v: View, position: Int)
    }

    interface ObservacionListener {
        fun onItemClick(o: Observaciones, v: View, position: Int)
    }

    interface AreaListener {
        fun onItemClick(a: Area, v: View, position: Int)
    }

    interface TipoTrasladoListener {
        fun onItemClick(t: TipoTraslado, v: View, position: Int)
    }

    interface VehiculoListener {
        fun onItemClick(ve: Vehiculo, v: View, position: Int)
    }

    interface DetalleGrupoListener {
        fun onItemClick(d: DetalleGrupo, view: View, position: Int)
    }

    interface ServicioListener{
        fun onItemClick(s: Servicio, position: Int)
    }

    interface RepartoSuministroListener{
        fun onItemClick(r: RepartoCargoSuministro,view:View, position: Int)
    }
}