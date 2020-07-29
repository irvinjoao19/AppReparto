package com.calida.dsige.reparto.ui.adapters

import android.graphics.Color
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.SuministroCortes
import io.realm.RealmResults
import java.util.*

class SuministroCortesAdapter(private var suministros: RealmResults<SuministroCortes>?, private var layout: Int?, private var listener: OnItemClickListener?) : RecyclerView.Adapter<SuministroCortesAdapter.ViewHolder>() {

    private var suministrosList: ArrayList<SuministroCortes> = ArrayList(suministros!!)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = layout?.let { LayoutInflater.from(parent.context).inflate(it, parent, false) }
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (Objects.requireNonNull(suministrosList[position]).isValid) {
            listener?.let { holder.bind(Objects.requireNonNull(suministrosList[position]), it) }
        }
    }

    override fun getItemCount(): Int {
        return suministrosList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardViewPrincipal: CardView = itemView.findViewById(R.id.cardViewPrincipal)
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val textViewDireccion: TextView = itemView.findViewById(R.id.textViewContrato)
        private val textViewContrato: TextView = itemView.findViewById(R.id.textViewDireccion)
        private val textViewMedidor: TextView = itemView.findViewById(R.id.textViewMedidor)
        private val textViewOrden: TextView = itemView.findViewById(R.id.textViewOrden)
        private val imageViewMap: ImageView = itemView.findViewById(R.id.imageViewMap)

        internal fun bind(suministro: SuministroCortes, listener: OnItemClickListener) {

            if (suministro.suministro_NoCortar == 1) {
                cardViewPrincipal.setCardBackgroundColor(Color.GREEN)
            } else {
                cardViewPrincipal.setCardBackgroundColor(Color.WHITE)
            }

            textViewTitle.text = suministro.suministro_Cliente
            textViewDireccion.text = suministro.suministro_Direccion
            textViewContrato.text = suministro.suministro_Numero
            textViewMedidor.text = suministro.suministro_Medidor
            textViewOrden.text = suministro.orden.toString()
            imageViewMap.visibility = View.GONE
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
                    val filteredList = ArrayList<SuministroCortes>()
                    for (suministro: SuministroCortes in suministros!!) {
                        if (suministro.suministro_Cliente!!.toLowerCase(Locale.getDefault()).contains(keyword) ||
                                suministro.suministro_Direccion!!.toLowerCase(Locale.getDefault()).contains(keyword) ||
                                suministro.suministro_Medidor!!.contains(keyword)) {
                            filteredList.add(suministro)
                        }
                    }
                    suministrosList = filteredList
                }
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(s: SuministroCortes, v: View, position: Int)
    }
}