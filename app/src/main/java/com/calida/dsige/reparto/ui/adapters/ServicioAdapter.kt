package com.calida.dsige.reparto.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.calida.dsige.reparto.data.local.*
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.cardview_menu.view.*

class ServicioAdapter(private val listener: OnItemClickListener.ServicioListener) : RecyclerView.Adapter<ServicioAdapter.ViewHolder>() {

    private var realm: Realm? = Realm.getDefaultInstance()
    private var activo: Int? = 1
    private var lectura: Int? = 1
    private var relectura: Int? = 2
    private var servicios = emptyList<Servicio>()

    fun addItems(list: RealmResults<Servicio>) {
        servicios = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_menu, parent, false)
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(servicios[position], listener)
    }

    override fun getItemCount(): Int {
        return servicios.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(s: Servicio, listener: OnItemClickListener.ServicioListener) = with(itemView) {
            when (s.nombre_servicio) {
                "Lectura" -> {
                    val count = realm?.where(SuministroLectura::class.java)?.distinct("iD_Suministro")?.equalTo("activo", activo)?.equalTo("estado", lectura)?.count()
                    val valor: Int? = count?.toInt()
                    if (valor == 0) {
                        textViewCount.visibility = View.GONE
                    } else {
                        textViewCount.visibility = View.VISIBLE
                        textViewCount.text = valor.toString()
                    }
                    imageViewPhoto.setImageResource(R.mipmap.ic_lectura)
                    textViewTitulo.text = s.nombre_servicio
                }
                "Relectura" -> {
                    val count = realm?.where(SuministroLectura::class.java)?.distinct("iD_Suministro")?.equalTo("activo", activo)?.equalTo("estado", relectura)?.count()
                    val valor: Int? = count?.toInt()
                    if (valor == 0) {
                        textViewCount.visibility = View.GONE
                    } else {
                        textViewCount.visibility = View.VISIBLE
                        textViewCount.text = valor.toString()
                    }
                    imageViewPhoto.setImageResource(R.mipmap.ic_relectura)
                    textViewTitulo.text = s.nombre_servicio
                }
                "Cortes" -> {
                    val count = realm?.where(SuministroCortes::class.java)?.distinct("iD_Suministro")?.equalTo("activo", activo)?.count()
                    val valor: Int? = count?.toInt()
                    if (valor == 0) {
                        textViewCount.visibility = View.GONE
                    } else {
                        textViewCount.visibility = View.VISIBLE
                        textViewCount.text = valor.toString()
                    }
                    imageViewPhoto.setImageResource(R.mipmap.ic_cortes)
                    textViewTitulo.text = s.nombre_servicio
                }
                "Reconexiones" -> {
                    val count = realm?.where(SuministroReconexion::class.java)?.distinct("iD_Suministro")?.equalTo("activo", activo)?.count()
                    val valor: Int? = count?.toInt()
                    if (valor == 0) {
                        textViewCount.visibility = View.GONE
                    } else {
                        textViewCount.visibility = View.VISIBLE
                        textViewCount.text = valor.toString()
                    }
                    imageViewPhoto.setImageResource(R.mipmap.ic_reconexion)
                    textViewTitulo.text = s.nombre_servicio
                }
                "Reparto" -> {
                    val count = realm?.where(Reparto::class.java)?.distinct("id_Reparto")?.equalTo("activo", activo)?.count()
                    val valor: Int? = count?.toInt()
                    if (valor == 0) {
                        textViewCount.visibility = View.GONE
                    } else {
                        textViewCount.visibility = View.VISIBLE
                        textViewCount.text = valor.toString()
                    }
                    imageViewPhoto.setImageResource(R.mipmap.ic_reparto)
                    textViewTitulo.text = s.nombre_servicio
                }
                "Grandes Clientes" -> {
                    val estado = 7
                    val count = realm?.where(GrandesClientes::class.java)?.notEqualTo("estado", estado)?.count()
                    val valor: Int? = count?.toInt()
                    if (valor == 0) {
                        textViewCount.visibility = View.GONE
                    } else {
                        textViewCount.visibility = View.VISIBLE
                        textViewCount.text = valor.toString()
                    }
                    imageViewPhoto.setImageResource(R.drawable.ic_users_group)
                    textViewTitulo.text = s.nombre_servicio
                }
                else -> textViewTitulo.text = s.nombre_servicio
            }
            itemView.setOnClickListener { listener.onItemClick(s, adapterPosition) }
        }
    }
}