package com.calida.dsige.reparto.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.MenuPrincipal
import kotlinx.android.synthetic.main.cardview_menu.view.*

class ObservationAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<ObservationAdapter.ViewHolder>() {

    private var menus = emptyList<MenuPrincipal>()

    fun addItems(list: List<MenuPrincipal>) {
        menus = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(menus[position], listener)
    }

    override fun getItemCount(): Int {
        return menus.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(m: MenuPrincipal, listener: OnItemClickListener) = with(itemView) {
            textViewTitulo.text = m.title
            imageViewPhoto.visibility = View.GONE
            textViewCount.visibility = View.GONE
            itemView.setOnClickListener { listener.onItemClick(m, adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(m: MenuPrincipal, position: Int)
    }
}