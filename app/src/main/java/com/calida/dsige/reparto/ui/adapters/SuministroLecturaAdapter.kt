package com.calida.dsige.reparto.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.SuministroLectura
import io.realm.RealmResults
import java.util.*

class SuministroLecturaAdapter(private var suministros: RealmResults<SuministroLectura>?, private var layout: Int?, private var listener: OnItemClickListener?) : RecyclerView.Adapter<SuministroLecturaAdapter.ViewHolder>() {

    private var suministrosList: ArrayList<SuministroLectura> = ArrayList(suministros!!)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = layout?.let { LayoutInflater.from(parent.context).inflate(it, parent, false) }
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (Objects.requireNonNull<SuministroLectura>(suministros?.get(position)).isValid) {
            listener?.let { holder.bind(Objects.requireNonNull(suministrosList[position]), it) }
        }
    }

    override fun getItemCount(): Int {
        return suministrosList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        val textViewDireccion: TextView = itemView.findViewById(R.id.textViewContrato)
        val textViewContrato: TextView = itemView.findViewById(R.id.textViewDireccion)
        val textViewMedidor: TextView = itemView.findViewById(R.id.textViewMedidor)
        val textViewOrden: TextView = itemView.findViewById(R.id.textViewOrden)
        val imageViewMap: ImageView = itemView.findViewById(R.id.imageViewMap)

        internal fun bind(suministro: SuministroLectura, listener: OnItemClickListener) {
            textViewTitle.text = suministro.suministro_Cliente
            textViewDireccion.text = suministro.suministro_Direccion
            textViewContrato.text = suministro.suministro_Numero
            textViewMedidor.text = suministro.suministro_Medidor
            textViewOrden.text = suministro.orden.toString()
            itemView.setOnClickListener { v -> listener.onItemClick(suministro, v, adapterPosition) }
            imageViewMap.setOnClickListener { v -> listener.onItemClick(suministro, v, adapterPosition) }
        }
    }


    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                return FilterResults()
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

                suministrosList.clear()
                val keyword = charSequence.toString()
                if (keyword.isEmpty()) {
                    suministrosList.addAll(suministros!!)
                } else {
                    val filteredList = ArrayList<SuministroLectura>()
                    for (s: SuministroLectura in suministros!!) {
                        if (s.suministro_Direccion.toLowerCase(Locale.getDefault()).contains(keyword) ||
                                s.suministro_Numero.contains(keyword) ||
                                s.suministro_Medidor.contains(keyword)) {
                            filteredList.add(s)
                        }
                    }
                    suministrosList = filteredList
                }
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(s: SuministroLectura, v: View, position: Int)
    }
}