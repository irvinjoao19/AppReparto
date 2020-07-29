package com.calida.dsige.reparto.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.CheckList
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.cardview_check_list.view.*

class CheckListAdapter(private val listener: OnItemClickListener.CheckListListener) :
        RecyclerView.Adapter<CheckListAdapter.ViewHolder>() {

    private var checkList = emptyList<CheckList>()

    fun addItems(list: RealmResults<CheckList>) {
        checkList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_check_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(checkList[position], listener)
    }

    override fun getItemCount(): Int {
        return checkList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(c: CheckList, listener: OnItemClickListener.CheckListListener) = with(itemView) {
            if (c.titulo == 1) {
                textViewPrincipal.visibility = View.VISIBLE

                textViewPrincipal.text = c.descripcion
                textViewDescripcion.visibility = View.GONE
                radioGroup.visibility = View.GONE
                divider.visibility = View.GONE
            } else {
                textViewPrincipal.visibility = View.GONE

                textViewDescripcion.text = c.descripcion
                textViewDescripcion.visibility = View.VISIBLE
                radioGroup.visibility = View.VISIBLE
                divider.visibility = View.VISIBLE

                radioSI.isChecked = c.isSelectedSI
                radioNO.isChecked = c.isSelectedNO
                radioNA.isChecked = c.isSelectedNA
                radioTA.isChecked = c.isSelectedTA

                radioSI.text = c.valor1
                radioNO.text = c.valor2

                if (c.valor3.isEmpty()) {
                    radioNA.visibility = View.INVISIBLE
                } else {
                    radioNA.visibility = View.VISIBLE
                }

                if (c.valor4.isEmpty()) {
                    radioTA.visibility = View.INVISIBLE
                } else {
                    radioTA.visibility = View.VISIBLE
                }

                radioNA.text = c.valor3
                radioTA.text = c.valor4

                radioSI.setOnClickListener { view ->
                    notifyDataSetChanged()
                    listener.onItemClick(c, view, adapterPosition)
                }
                radioNO.setOnClickListener { view ->
                    notifyDataSetChanged()
                    listener.onItemClick(c, view, adapterPosition)
                }
                radioNA.setOnClickListener { view ->
                    notifyDataSetChanged()
                    listener.onItemClick(c, view, adapterPosition)
                }
                radioTA.setOnClickListener { view ->
                    notifyDataSetChanged()
                    listener.onItemClick(c, view, adapterPosition)
                }
            }
        }
    }
}