package com.calida.dsige.reparto.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.Photo
import com.calida.dsige.reparto.helper.Util
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import com.squareup.picasso.Picasso
import io.realm.RealmResults
import kotlinx.android.synthetic.main.cardview_firm.view.*
import java.io.File

class FirmAdapter(private val listener: OnItemClickListener.PhotoListener) :
        RecyclerView.Adapter<FirmAdapter.ViewHolder>() {

    private var firms = emptyList<Photo>()

    fun addItems(list: RealmResults<Photo>) {
        firms = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_firm, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(firms[position], listener)
    }

    override fun getItemCount(): Int {
        return firms.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(p: Photo, listener: OnItemClickListener.PhotoListener) = with(itemView) {
//            val f = File(Environment.getExternalStorageDirectory(), Util.FolderImg + "/" + p.rutaFoto)
            val f = File(Util.getFolder(itemView.context), p.rutaFoto)
            Picasso.get().load(f).into(imageViewFirm)
            textViewName.text = String.format("%s", p.rutaFoto)
            itemView.setOnClickListener { view -> listener.onItemClick(p, view, adapterPosition) }
        }
    }
}