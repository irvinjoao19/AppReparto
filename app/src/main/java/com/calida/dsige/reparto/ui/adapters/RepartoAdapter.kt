package com.calida.dsige.reparto.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.Reparto
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_search_suministro.view.*
import java.util.*

class RepartoAdapter(private var listener: OnItemClickListener.RepartoListener) : RecyclerView.Adapter<RepartoAdapter.ViewHolder>() {

    private var clientes = emptyList<Reparto>()
    private var clientesList: ArrayList<Reparto> = ArrayList()

    fun addItems(list: List<Reparto>) {
        clientes = list
        clientesList = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_search_suministro, parent, false)
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(Objects.requireNonNull(clientesList[position]), listener)
    }

    override fun getItemCount(): Int {
        return clientesList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(s: Reparto, listener: OnItemClickListener.RepartoListener) = with(itemView) {
            checkboxSuministro.text = s.Suministro_Numero_reparto
            checkboxSuministro.isChecked = s.isActive
            checkboxSuministro.setOnClickListener { v -> listener.onItemClick(s, v, adapterPosition) }
        }
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                return FilterResults()
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                clientesList.clear()
                val keyword = charSequence.toString()
                if (keyword.isEmpty()) {
                    clientesList.addAll(clientes)
                } else {
                    val filteredList = ArrayList<Reparto>()
                    for (cliente: Reparto in clientes) {
                        if (cliente.Suministro_Numero_reparto.toLowerCase(Locale.getDefault()).contains(keyword)) {
                            filteredList.add(cliente)
                        }
                    }
                    clientesList = filteredList
                }
                notifyDataSetChanged()
            }
        }
    }
}