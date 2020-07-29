package com.calida.dsige.reparto.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.Area
import com.calida.dsige.reparto.data.local.Inspeccion
import com.calida.dsige.reparto.data.local.TipoTraslado
import com.calida.dsige.reparto.data.viewModel.InspeccionViewModel
import com.calida.dsige.reparto.helper.Gps
import com.calida.dsige.reparto.helper.Util
import com.calida.dsige.reparto.ui.adapters.AreAdapter
import com.calida.dsige.reparto.ui.adapters.TipoTrasladoAdapter
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_general.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GeneralFragment : Fragment(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextFecha -> Util.getDateDialog(context!!, editTextFecha)
            R.id.editTextHora -> Util.getHourDialog(context!!, editTextHora)
            R.id.editTextArea -> dialogSpinner(1, "Area")
            R.id.editTextTraslado -> dialogSpinner(2, "Tipo Traslado")
            R.id.fabGenerate -> formInspeccion()
        }
    }

    lateinit var inspeccionViewModel: InspeccionViewModel

    private var viewPager: ViewPager? = null

    var inspeccionId: Int = 0
    var usuarioId: Int = 0

    lateinit var i: Inspeccion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        i = Inspeccion()
        arguments?.let {
            inspeccionId = it.getInt(ARG_PARAM1)
            usuarioId = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_general, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inspeccionViewModel = ViewModelProvider(this).get(InspeccionViewModel::class.java)
        inspeccionViewModel.initialRealm()
        editTextFecha.setText(Util.getFecha())
        editTextHora.setText(Util.getHora())
        fabGenerate.setOnClickListener(this)
        editTextFecha.setOnClickListener(this)
        editTextHora.setOnClickListener(this)
        editTextArea.setOnClickListener(this)
        editTextTraslado.setOnClickListener(this)
        microTouchListener(editTextLugar)
        bindUI()
        message()
    }

    private fun bindUI() {
        viewPager = activity!!.findViewById(R.id.viewPager)

        val l = inspeccionViewModel.getLogin()
        editTextOperario.setText(l.operario_Nombre)
        editTextPlaca.setText(l.placa)
        editTextMarca.setText(l.marca)
        i.vehiculoId = l.vehiculoId
        editTextCliente.setText(String.format("%s", "Calidda"))

        if (inspeccionId == 2) {
            textViewTraslado.visibility = View.GONE
        }

        val insp = inspeccionViewModel.getInspeccion(inspeccionId, usuarioId)
        if (insp != null) {
            editTextFecha.setText(insp.vFecha)
            editTextHora.setText(insp.vHora)
            editTextLugar.setText(insp.lugar)
            editTextArea.setText(insp.nombreArea)
            i.areaId = insp.areaId
            i.trasladoId = insp.trasladoId

            if (insp.trasladoId == 2) {
                textViewPlaca.visibility = View.VISIBLE
                textViewMarca.visibility = View.VISIBLE
                textViewKilometraje.visibility = View.VISIBLE
            }
            editTextTraslado.setText(insp.nombreTraslado)
            editTextKilometraje.setText(insp.kilometraje)
            Log.i("TAG", "INGRESO")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(inspeccionId: Int, usuarioId: Int) =
                GeneralFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, inspeccionId)
                        putInt(ARG_PARAM2, usuarioId)
                    }
                }
    }

    private fun message() {
        inspeccionViewModel.error.observe(this, Observer<String> { s ->
            if (s != null) {
                Util.hideKeyboardFrom(context!!, view!!)
                Util.toastMensaje(context!!, s)
            }
        })

        inspeccionViewModel.success.observe(this, Observer<String> { s ->
            if (s != null) {
                viewPager!!.currentItem = 1
                Util.hideKeyboardFrom(context!!, view!!)
                Util.toastMensaje(context!!, s)
            }
        })
    }

    private fun formInspeccion() {
        i.id = inspeccionViewModel.getInspeccionIdentity()
        i.inspeccionId = inspeccionId
        i.operarioLecturaId = usuarioId
        i.nombreOperario = editTextOperario.text.toString()
        i.usuarioId = usuarioId
        i.clienteId = 1
        i.vFecha = editTextFecha.text.toString()
        i.vHora = editTextHora.text.toString()
        i.fecha = String.format("%s %s", editTextFecha.text.toString(), editTextHora.text.toString())
        i.lugar = editTextLugar.text.toString()
        i.nombreArea = editTextArea.text.toString()
        i.fechaVerificacion = Util.getFecha()
        i.nombreTraslado = editTextTraslado.text.toString()
        i.kilometraje = editTextKilometraje.text.toString()
        i.active = 2
        inspeccionViewModel.validateInspeccion(i)
    }

    private fun dialogSpinner(tipo: Int, name: String) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)
        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        textViewTitulo.text = String.format(name)
        when (tipo) {
            1 -> {
                val areaAdapter = AreAdapter(object : OnItemClickListener.AreaListener {
                    override fun onItemClick(a: Area, v: View, position: Int) {
                        i.areaId = a.areaId
                        editTextArea.setText(a.nombre)
                        dialog.dismiss()
                    }
                })
                recyclerView.adapter = areaAdapter
                val area = inspeccionViewModel.getAreas()
                areaAdapter.addItems(area)
            }
            2 -> {
                val tipoTraslado = TipoTrasladoAdapter(object : OnItemClickListener.TipoTrasladoListener {
                    override fun onItemClick(t: TipoTraslado, v: View, position: Int) {
                        textViewPlaca.visibility = View.GONE
                        textViewMarca.visibility = View.GONE
                        textViewKilometraje.visibility = View.GONE

                        if (t.trasladoId == 2) {
                            textViewPlaca.visibility = View.VISIBLE
                            textViewMarca.visibility = View.VISIBLE
                            textViewKilometraje.visibility = View.VISIBLE
                        }
                        i.trasladoId = t.trasladoId
                        editTextTraslado.setText(t.nombre)
                        dialog.dismiss()
                    }
                })
                recyclerView.adapter = tipoTraslado
                val traslado = inspeccionViewModel.getTipoTraslado()
                tipoTraslado.addItems(traslado)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun microTouchListener(input: TextInputEditText) {
        input.setOnTouchListener { _, event ->
            val drawableRight = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= input.right - input
                                .compoundDrawables[drawableRight].bounds.width()) {
                    val gps = Gps(context!!)
                    if (gps.isLocationEnabled()) {
                        progressBarLugar.visibility = View.VISIBLE
                        Util.getLocationName(context!!, editTextLugar, gps.location!!, progressBarLugar)
                    } else {
                        gps.showSettingsAlert(context!!)
                    }
                } else {
                    editTextLugar.text = null
                    Util.showKeyboard(editTextLugar, context!!)
                }
            }
            true
        }
    }
}