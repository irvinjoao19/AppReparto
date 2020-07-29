package com.calida.dsige.reparto.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.RepartoCargoFoto
import com.calida.dsige.reparto.data.viewModel.RepartoViewModel
import com.calida.dsige.reparto.helper.Util
import com.calida.dsige.reparto.ui.adapters.RepartoCargoFotoAdapter
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import com.calida.dsige.reparto.ui.services.DistanceService
import com.calida.dsige.reparto.ui.services.SendRepartoServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_photo_reparto.*

class PhotoRepartoActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabAdd -> {
                if (foto == 2) {
                    startActivity(Intent(this, FirmActivity::class.java).putExtra("repartoId", repartoId))
                } else {
                    startActivity(Intent(this, CameraActivity::class.java).putExtra("repartoId", repartoId))
                }
            }
            R.id.fab -> repartoViewModel.updateReparto(repartoId)
        }
    }

    lateinit var repartoViewModel: RepartoViewModel
    private var repartoId = 0
    private var cantidad = 0
    private var foto = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_reparto)
        val b = intent.extras
        if (b != null) {
            bindUI(b.getInt("repartoId"))
        }
    }

    private fun bindUI(id: Int) {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Foto"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        repartoId = id
        repartoViewModel = ViewModelProvider(this).get(RepartoViewModel::class.java)
        repartoViewModel.initialRealm()

        val cargoFotoAdapter = RepartoCargoFotoAdapter(object : OnItemClickListener.RepartoCargoFotoListener {
            override fun onItemClick(f: RepartoCargoFoto, view: View, position: Int) {
                confirmDelete(f)
            }
        })

        val layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = cargoFotoAdapter

        val cargoFotos = repartoViewModel.getRepartoFoto(id)
        cargoFotos.addChangeListener { t ->
            cantidad = t.size
            if (cantidad == 3) {
                fabAdd.visibility = View.GONE
                fab.visibility = View.VISIBLE
            } else {
                fabAdd.visibility = View.VISIBLE
                fab.visibility = View.GONE
            }
            getCount(t)
            cargoFotoAdapter.addItems(t)
        }
        cantidad = cargoFotos.size
        getCount(cargoFotos)

        if (cantidad == 3) {
            fabAdd.visibility = View.GONE
            fab.visibility = View.VISIBLE
        } else {
            fabAdd.visibility = View.VISIBLE
            fab.visibility = View.GONE
        }

        cargoFotoAdapter.addItems(cargoFotos)
        fabAdd.setOnClickListener(this)
        fab.setOnClickListener(this)

        repartoViewModel.mensajeSuccess.observe(this, Observer {
            Util.toastMensaje(this, it)
            startService(Intent(this, DistanceService::class.java))
            startService(Intent(this, SendRepartoServices::class.java))
            finish()
        })
    }

    private fun getCount(f: List<RepartoCargoFoto>) {
        foto = 0
        for (p: RepartoCargoFoto in f) {
            if (p.tipo == 1) {
                foto++
            }
        }
    }

    private fun confirmDelete(r: RepartoCargoFoto) {
        val dialog = MaterialAlertDialogBuilder(this)
                .setTitle("Mensaje")
                .setMessage(
                        String.format("Estas seguro de eliminar ?")
                )
                .setPositiveButton("Aceptar") { dialog, _ ->
                    repartoViewModel.deleteRepartoFoto(r.fotoCargoId)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.cancel()
                }
        dialog.show()
    }
}
