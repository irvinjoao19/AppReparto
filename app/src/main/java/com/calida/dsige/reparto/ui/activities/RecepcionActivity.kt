package com.calida.dsige.reparto.ui.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.RepartoCargo
import com.calida.dsige.reparto.data.viewModel.RepartoViewModel
import com.calida.dsige.reparto.helper.Gps
import com.calida.dsige.reparto.helper.Util
import com.calida.dsige.reparto.ui.services.DistanceService
import com.calida.dsige.reparto.ui.services.SendRepartoServices
import kotlinx.android.synthetic.main.activity_recepcion.*
import java.util.*
import kotlin.collections.ArrayList

class RecepcionActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonBusqueda -> repartoViewModel.searchSuministro(editTextSuministro.text.toString())
            R.id.fabMicroNombre -> microPhone("Ingrese Nombres", 1)
            R.id.fabMicroDni -> microPhone("DNI", 2)
            R.id.fabMicroPrecio -> microPhone("Predio", 3)
            R.id.fabMicroLectura -> microPhone("Lectura de Medidor", 4)
            R.id.editTextFecha -> Util.getDateDialog(this, editTextFecha)
            R.id.editTextHora -> Util.getHourDialog(this, editTextHora)
            R.id.fabGenerate -> formReparto()
        }
    }

    lateinit var repartoViewModel: RepartoViewModel

    lateinit var r: RepartoCargo
    private var repartoId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recepcion)
        val b = intent.extras
        if (b != null) {
            bindUI(b.getInt("repartoId"))
            stopService(Intent(this, DistanceService::class.java))
            stopService(Intent(this, SendRepartoServices::class.java))
        }
    }

    private fun bindUI(id: Int) {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.title1)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        r = RepartoCargo()
        repartoId = id
        repartoViewModel = ViewModelProvider(this).get(RepartoViewModel::class.java)
        repartoViewModel.initialRealm()

        buttonBusqueda.setOnClickListener(this)

        fabMicroNombre.setOnClickListener(this)
        fabMicroDni.setOnClickListener(this)
        fabMicroPrecio.setOnClickListener(this)
        fabMicroLectura.setOnClickListener(this)
        editTextFecha.setOnClickListener(this)
        editTextHora.setOnClickListener(this)
        fabGenerate.setOnClickListener(this)

        editTextFecha.setText(Util.getFecha())
        editTextHora.setText(Util.getHora())

        repartoViewModel.mensajeDireccion.observe(this, Observer {
            textViewSuministro.visibility = View.VISIBLE
            textViewDireccion.visibility = View.VISIBLE
            textViewSuministro.text = editTextSuministro.text.toString()
            textViewDireccion.text = it
        })

        repartoViewModel.mensajeError.observe(this, Observer {
            Util.toastMensaje(this, it)
        })

        repartoViewModel.mensajeSuccess.observe(this, Observer {
            startActivity(Intent(this, PhotoRepartoActivity::class.java).putExtra("repartoId", repartoId))
            finish()
            Util.toastMensaje(this, it)
        })

        val e: RepartoCargo? = repartoViewModel.getRepartoByIdTipo(id, 1)
        if (e != null) {
            r = e
            textViewSuministro.visibility = View.VISIBLE
            textViewDireccion.visibility = View.VISIBLE
            editTextSuministro.setText(e.suministroNumero)
            textViewSuministro.text = e.suministroNumero
            textViewDireccion.text = e.nombreEmpresa
            editTextNombre.setText(e.nombreApellido)
            editTextDni.setText(e.dni)
            editTextPredio.setText(e.predio)
            editTextLectura.setText(e.lecturaCargo)
        }
    }

    private fun microPhone(titulo: String, permission: Int) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, titulo)

        try {
            startActivityForResult(intent, permission)
        } catch (a: ActivityNotFoundException) {
            Util.toastMensaje(this, "Dispositivo no compatible para esta opción")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val result: ArrayList<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val y = result?.get(0)!!
            //val y = result?.get(0)!!.replace(" ", "")
            when (requestCode) {
                1 -> editTextNombre.setText(y)
                2 -> {
                    val x = y.replace(" ", "")
                    if (Util.isNumeric(x)) {
                        editTextDni.setText(x)
                    } else {
                        Util.toastMensaje(this, "Solo valores numericos")
                    }
                }
                3 -> editTextPredio.setText(y)
                4 -> {
                    val x = y.replace(" ", "")
                    if (Util.isNumeric(x)) {
                        editTextLectura.setText(x)
                    } else {
                        Util.toastMensaje(this, "Solo valores numericos")
                    }
                }
            }
        }
    }

    private fun formReparto() {
        val gps = Gps(this)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                gps.showAlert(this)
            } else {
                r.repartoId = repartoId
                r.cargoId = repartoViewModel.getRepartoCargoIdentity()
                r.tipoCargoId = 1
                r.suministroNumero = textViewSuministro.text.toString()
                r.nombreEmpresa = textViewDireccion.text.toString()
                r.nombreApellido = editTextNombre.text.toString()
                r.dni = editTextDni.text.toString()
                r.predio = editTextPredio.text.toString()
                r.lecturaCargo = editTextLectura.text.toString()
                r.estado = 1
                r.latitud = gps.latitude.toString()
                r.longitud = gps.longitude.toString()
                r.fecha = editTextFecha.text.toString()
                r.hora = editTextHora.text.toString()
                r.fechaMovil = Util.getFecha()
                repartoViewModel.validateReparto(r)
            }
        } else {
            gps.showSettingsAlert(this)
        }
    }
}