package com.calida.dsige.reparto.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.RepartoCargoSuministro
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_combo.view.*
import kotlinx.android.synthetic.main.cardview_inspeccion_detalle.view.*

class RepartoSuministroAdapter(private val listener: OnItemClickListener.RepartoSuministroListener) :
        RecyclerView.Adapter<RepartoSuministroAdapter.ViewHolder>() {

    private var detalles = emptyList<RepartoCargoSuministro>()

    fun addItems(list: List<RepartoCargoSuministro>) {
        detalles = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_combo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(detalles[position], listener)
    }

    override fun getItemCount(): Int {
        return detalles.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(r: RepartoCargoSuministro, listener: OnItemClickListener.RepartoSuministroListener) = with(itemView) {
            textViewNombre.text = r.suministroNumero
            imageViewClose.visibility = View.VISIBLE
            imageViewClose.setOnClickListener { view -> listener.onItemClick(r, view, adapterPosition) }
        }
    }
}