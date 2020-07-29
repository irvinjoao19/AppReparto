package com.calida.dsige.reparto.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.Observaciones
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.cardview_combo.view.*

class ObservacionAdapter(private val listener: OnItemClickListener.ObservacionListener) :
        RecyclerView.Adapter<ObservacionAdapter.ViewHolder>() {

    private var observaciones = emptyList<Observaciones>()

    fun addItems(list: RealmResults<Observaciones>) {
        observaciones = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_combo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(observaciones[position], listener)
    }

    override fun getItemCount(): Int {
        return observaciones.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(o: Observaciones, listener: OnItemClickListener.ObservacionListener) = with(itemView) {
            textViewNombre.text = String.format("%s", o.descripcion)
            itemView.setOnClickListener { view -> listener.onItemClick(o, view, adapterPosition) }
        }
    }
}