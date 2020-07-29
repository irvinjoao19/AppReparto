package com.calida.dsige.reparto.ui.activities

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.DetalleGrupo
import com.calida.dsige.reparto.data.local.Reparto
import com.calida.dsige.reparto.data.local.RepartoCargo
import com.calida.dsige.reparto.data.local.RepartoCargoSuministro
import com.calida.dsige.reparto.data.viewModel.RepartoViewModel
import com.calida.dsige.reparto.helper.Gps
import com.calida.dsige.reparto.helper.Util
import com.calida.dsige.reparto.ui.adapters.DetalleGrupoAdapter
import com.calida.dsige.reparto.ui.adapters.RepartoAdapter
import com.calida.dsige.reparto.ui.adapters.RepartoSuministroAdapter
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import com.calida.dsige.reparto.ui.services.DistanceService
import com.calida.dsige.reparto.ui.services.SendRepartoServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_recepcion_varios.*
import java.util.*

class RecepcionVariosActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextRecibe -> dialogSpinner()
            R.id.buttonAdd -> dialogSuministro()
            R.id.fabMicroEmpresa -> microPhone("Empresa/Condominio/Agrupación", 1)
            R.id.fabMicroNombre -> microPhone("Nombre y Apellidos", 2)
            R.id.fabMicroDni -> microPhone("DNI", 3)
            R.id.editTextFecha -> Util.getDateDialog(this, editTextFecha)
            R.id.editTextHora -> Util.getHourDialog(this, editTextHora)
            R.id.fabGenerate -> formReparto()
        }
    }

    lateinit var repartoViewModel: RepartoViewModel

    lateinit var r: RepartoCargo
    private var repartoId = 0
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recepcion_varios)
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
        supportActionBar!!.title = getString(R.string.title2)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        r = RepartoCargo()
        repartoId = id
        repartoViewModel = ViewModelProvider(this).get(RepartoViewModel::class.java)
        repartoViewModel.initialRealm()

        val repartoSuministroAdapter = RepartoSuministroAdapter(object : OnItemClickListener.RepartoSuministroListener {
            override fun onItemClick(r: RepartoCargoSuministro, view: View, position: Int) {
                confirmDelete(r)
            }
        })
        val layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = repartoSuministroAdapter

        val inspeccionDetalle = repartoViewModel.getRepartoSuministro(id)
        inspeccionDetalle.addChangeListener { t ->
            count = t.size
            repartoSuministroAdapter.addItems(t)
        }
        repartoSuministroAdapter.addItems(inspeccionDetalle)
        count = inspeccionDetalle.size

        editTextRecibe.setOnClickListener(this)
        editTextFecha.setOnClickListener(this)
        editTextHora.setOnClickListener(this)
        buttonAdd.setOnClickListener(this)
        fabMicroEmpresa.setOnClickListener(this)
        fabMicroNombre.setOnClickListener(this)
        fabMicroDni.setOnClickListener(this)
        fabGenerate.setOnClickListener(this)

        editTextFecha.setText(Util.getFecha())
        editTextHora.setText(Util.getHora())

        repartoViewModel.mensajeSuccess.observe(this, Observer {
            if (count != 0) {
                startActivity(Intent(this, PhotoRepartoActivity::class.java).putExtra("repartoId", repartoId))
                finish()
                Util.toastMensaje(this, it)
            } else
                Util.toastMensaje(this, "Agregue Suministros")
        })

        repartoViewModel.mensajeError.observe(this, Observer {
            Util.toastMensaje(this, it)
        })

        val e: RepartoCargo? = repartoViewModel.getRepartoByIdTipo(id, 2)
        if (e != null) {
            r = e
            editTextDni.setText(e.dni)
            editTextRecibe.setText(e.nombreQuienRecibe)
            editTextEmpresa.setText(e.nombreEmpresa)
            editTextNombre.setText(e.nombreApellido)
        }
    }

    private fun dialogSpinner() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this).inflate(R.layout.dialog_combo, null)
        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        textViewTitulo.text = title
        val layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        textViewTitulo.text = String.format("%s", "Recibe")
        val detalleGrupoAdapter = DetalleGrupoAdapter(object : OnItemClickListener.DetalleGrupoListener {
            override fun onItemClick(d: DetalleGrupo, view: View, position: Int) {
                r.quienRecibeCargo = d.id
                editTextRecibe.setText(d.descripcion)
                dialog.dismiss()
            }
        })
        recyclerView.adapter = detalleGrupoAdapter
        val detalleGrupoResultadoList = repartoViewModel.getDetalleGruposById(10)
        detalleGrupoAdapter.addItems(detalleGrupoResultadoList)
    }

    private fun dialogSuministro() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this).inflate(R.layout.dialog_search, null)
        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        val editTextNombre: TextInputEditText = v.findViewById(R.id.editTextNombre)
        val fabOk: FloatingActionButton = v.findViewById(R.id.fabOk)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        textViewTitle.text = title
        val layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        textViewTitle.text = String.format("%s", "Lista de Suministro")

        val repartoAdapter = RepartoAdapter(object : OnItemClickListener.RepartoListener {
            override fun onItemClick(r: Reparto, v: View, position: Int) {
                if (v is AppCompatCheckBox) {
                    val checked: Boolean = v.isChecked
                    when (v.id) {
                        R.id.checkboxSuministro -> {
                            repartoViewModel.updateCheck(r.Suministro_Numero_reparto, checked)
                        }
                    }
                }
            }
        })
        recyclerView.adapter = repartoAdapter
        val list = repartoViewModel.getReparto()
        list.addChangeListener { t ->
            repartoAdapter.addItems(t)
        }
        repartoAdapter.addItems(list)

        editTextNombre.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                    charSequence: CharSequence, i: Int, i1: Int, i2: Int
            ) {

            }

            override fun onTextChanged(
                    charSequence: CharSequence, i: Int, i1: Int, i2: Int
            ) {

            }

            override fun afterTextChanged(editable: Editable) {
                repartoAdapter.getFilter().filter(editable)
            }
        })
        fabOk.setOnClickListener {
            repartoViewModel.insertRepartoSuministro(repartoId)
            dialog.dismiss()
        }
    }

    private fun confirmDelete(r: RepartoCargoSuministro) {
        val dialog = MaterialAlertDialogBuilder(this)
                .setTitle("Mensaje")
                .setMessage(
                        String.format("Estas seguro de eliminar el suministro %s ?", r.suministroNumero)
                )
                .setPositiveButton("Aceptar") { dialog, _ ->
                    repartoViewModel.deleteRepartoSuministro(r.cargoSuministroId)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.cancel()
                }
        dialog.show()
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
                1 -> editTextEmpresa.setText(y)
                2 -> editTextNombre.setText(y)
                3 -> {
                    val x = y.replace(" ", "")
                    if (Util.isNumeric(x)) {
                        editTextDni.setText(x)
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
                r.tipoCargoId = 2
                r.dni = editTextDni.text.toString()
                r.nombreQuienRecibe = editTextRecibe.text.toString()
                r.nombreEmpresa = editTextEmpresa.text.toString()
                r.nombreApellido = editTextNombre.text.toString()
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