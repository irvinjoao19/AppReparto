package com.calida.dsige.reparto.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.calida.dsige.reparto.helper.Util
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.RepartoCargoFoto
import com.calida.dsige.reparto.ui.adapters.RepartoCargoFotoAdapter.ViewHolder
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_photo.view.*
import java.io.File

class RepartoCargoFotoAdapter(private var listener: OnItemClickListener.RepartoCargoFotoListener) : RecyclerView.Adapter<ViewHolder>() {

    private var photos = emptyList<RepartoCargoFoto>()

    fun addItems(list: List<RepartoCargoFoto>) {
        photos = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_photo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photos[position], listener)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(photo: RepartoCargoFoto, listener: OnItemClickListener.RepartoCargoFotoListener) = with(itemView) {
            val f = File(Util.getFolder(itemView.context), photo.rutaFoto)
            Picasso.get()
                    .load(f)
                    .into(imageViewPhoto, object : Callback {
                        override fun onSuccess() {
                            progress.visibility = View.GONE
                        }

                        override fun onError(e: Exception) {

                        }
                    })
            textViewPhoto.text = photo.rutaFoto
            imageView.setOnClickListener { v -> listener.onItemClick(photo, v, adapterPosition) }
        }
    }
}