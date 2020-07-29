package com.calida.dsige.reparto.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import com.calida.dsige.reparto.data.local.Reparto
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_suministro.view.*
import java.util.*

open class SuministroRepartoAdapter(private val listener: OnItemClickListener.RepartoListener) : RecyclerView.Adapter<SuministroRepartoAdapter.ViewHolder>() {

    private var repartos = emptyList<Reparto>()
    private var repartosList: ArrayList<Reparto> = ArrayList()

    fun addItems(list: List<Reparto>) {
        repartos = list
        repartosList = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_suministro, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(repartosList[position], listener)
    }

    override fun getItemCount(): Int {
        return repartosList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(r: Reparto, listener: OnItemClickListener.RepartoListener) = with(itemView) {
            //textViewTitle.text = r.Cliente_Reparto
            //textViewMedidor.text = r.Suministro_Medidor_reparto
            textViewDireccion.text = r.Direccion_Reparto
            textViewSuministro.text = String.format("Suministro : %s", r.Suministro_Numero_reparto)
            textViewOrden.text = String.format("Orden : %s", r.Cod_Orden_Reparto)
            imageViewMap.setOnClickListener { v -> listener.onItemClick(r, v, adapterPosition) }
            itemView.setOnClickListener { v -> listener.onItemClick(r, v, adapterPosition) }
        }
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                return FilterResults()
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                repartosList.clear()
                val keyword = charSequence.toString()
                if (keyword.isEmpty()) {
                    repartosList.addAll(repartos)
                } else {
                    val filteredList = ArrayList<Reparto>()
                    for (r: Reparto in repartos) {
                        if (r.Cliente_Reparto.toLowerCase(Locale.getDefault()).contains(keyword) ||
                                r.Direccion_Reparto.toLowerCase(Locale.getDefault()).contains(keyword) ||
                                r.Suministro_Numero_reparto.contains(keyword) ||
                                r.Suministro_Medidor_reparto.contains(keyword)) {
                            filteredList.add(r)
                        }
                    }
                    repartosList = filteredList
                }
                notifyDataSetChanged()
            }
        }
    }
}