package com.calida.dsige.reparto.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.InspeccionDetalle
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_inspeccion_detalle.view.*

class InspeccionDetalleAdapter(private val listener: OnItemClickListener.InspeccionDetalleListener) :
        RecyclerView.Adapter<InspeccionDetalleAdapter.ViewHolder>() {

    private var detalles = emptyList<InspeccionDetalle>()

    fun addItems(list: List<InspeccionDetalle>) {
        detalles = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_inspeccion_detalle, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(detalles[position], listener)
    }

    override fun getItemCount(): Int {
        return detalles.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(i: InspeccionDetalle, listener: OnItemClickListener.InspeccionDetalleListener) = with(itemView) {
            textViewNombreEstado.text = i.nombreEstadoCheckList
            textViewDescripcion.text = i.descripcion

            when {
                i.aplicaObs1 == 1 -> textViewObservacion.visibility = View.VISIBLE
                else -> textViewObservacion.visibility = View.GONE
            }

            if (i.observacion.isEmpty()) {
                textViewObservacion.text = String.format("%s", "Ingresar Observación")
                textViewObservacion.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
            } else {
                textViewObservacion.setTextColor(ContextCompat.getColor(context, R.color.text))
                textViewObservacion.text =  i.observacion
            }

            itemView.setOnClickListener { view -> listener.onItemClick(i, view, adapterPosition) }
        }
    }
}