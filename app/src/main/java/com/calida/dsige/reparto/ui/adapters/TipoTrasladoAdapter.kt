package com.calida.dsige.reparto.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.TipoTraslado
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.cardview_combo.view.*

class TipoTrasladoAdapter(private val listener: OnItemClickListener.TipoTrasladoListener) :
        RecyclerView.Adapter<TipoTrasladoAdapter.ViewHolder>() {

    private var traslados = emptyList<TipoTraslado>()

    fun addItems(list: RealmResults<TipoTraslado>) {
        traslados = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_combo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(traslados[position], listener)
    }

    override fun getItemCount(): Int {
        return traslados.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(t: TipoTraslado, listener: OnItemClickListener.TipoTrasladoListener) = with(itemView) {
            textViewNombre.text = t.nombre
            itemView.setOnClickListener { v -> listener.onItemClick(t, v, adapterPosition) }
        }
    }
}