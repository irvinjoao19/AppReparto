package com.calida.dsige.reparto.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.calida.dsige.reparto.data.local.Photo
import com.calida.dsige.reparto.helper.Util
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.ui.adapters.PhotoAdapter.ViewHolder
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.realm.RealmResults
import java.io.File
import java.util.*

class PhotoAdapter(private var photos: RealmResults<Photo>?, private var layout: Int?, private var listener: OnItemClickListener?) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = layout?.let { LayoutInflater.from(parent.context).inflate(it, parent, false) }
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (Objects.requireNonNull<Photo>(photos?.get(position)).isValid) {
            listener?.let { holder.bind(Objects.requireNonNull<Photo>(photos?.get(position)), it) }
        }
    }

    override fun getItemCount(): Int {
        return photos!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageViewPhoto: ImageView = itemView.findViewById(R.id.imageViewPhoto)
        private val textViewPhoto: TextView = itemView.findViewById(R.id.textViewPhoto)
        private val progress: ProgressBar = itemView.findViewById(R.id.progress)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        internal fun bind(photo: Photo, listener: OnItemClickListener) {
//            val f = File(Environment.getExternalStorageDirectory(), Util.FolderImg + "/" + photo.rutaFoto)
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
            itemView.setOnClickListener { listener.onItemClick(photo, adapterPosition) }
            imageView.setOnClickListener { listener.onItemDelete(photo, adapterPosition) }
        }
    }

    interface OnItemClickListener {

        fun onItemClick(photo: Photo, position: Int)

        fun onItemDelete(photo: Photo, position: Int)
    }
}