package com.calida.dsige.reparto.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.InspeccionAdicionales
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_inspeccion_adicional.view.*

class InspeccionAdicionalAdapter(private val listener: OnItemClickListener.InspeccionAdicionalListener) : RecyclerView.Adapter<InspeccionAdicionalAdapter.ViewHolder>() {

    private var inspeccion = emptyList<InspeccionAdicionales>()

    fun addItems(list: List<InspeccionAdicionales>) {
        inspeccion = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_inspeccion_adicional, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(inspeccion[position], listener)
    }

    override fun getItemCount(): Int {
        return inspeccion.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(i: InspeccionAdicionales, listener: OnItemClickListener.InspeccionAdicionalListener) = with(itemView) {
            if (i.inspeccionId != 1) {
                textViewNombreTipo.visibility = View.VISIBLE
            } else {
                textViewNombreTipo.visibility = View.GONE
            }
            textViewNombreTipo.text = i.nombreTipo
            textViewDescripcion.text = i.descripcion
            itemView.setOnClickListener { view -> listener.onItemClick(i, view, adapterPosition) }
        }
    }
}