package com.calida.dsige.reparto.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.Area
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.cardview_combo.view.*

class AreAdapter(private val listener: OnItemClickListener.AreaListener) :
        RecyclerView.Adapter<AreAdapter.ViewHolder>() {

    private var areas = emptyList<Area>()

    fun addItems(list: RealmResults<Area>) {
        areas = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_combo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(areas[position], listener)
    }

    override fun getItemCount(): Int {
        return areas.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(a: Area, listener: OnItemClickListener.AreaListener) = with(itemView) {
            textViewNombre.text = a.nombre
            itemView.setOnClickListener { v -> listener.onItemClick(a, v, adapterPosition) }
        }
    }
}