package com.calida.dsige.reparto.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.InspeccionAdicionales
import com.calida.dsige.reparto.data.local.MenuPrincipal
import com.calida.dsige.reparto.data.viewModel.InspeccionViewModel
import com.calida.dsige.reparto.helper.Util
import com.calida.dsige.reparto.ui.adapters.InspeccionAdicionalAdapter
import com.calida.dsige.reparto.ui.adapters.ObservationAdapter
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_observation.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ObservationFragment : Fragment(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextObservacion -> spinnerDialogObservacion()
            R.id.fabAditional -> {
                val insp = inspeccionViewModel.getInspeccion(inspeccionId, usuarioId)
                if (insp != null) {
                    val inspeccionDetalle = inspeccionViewModel.getInspeccionDetalle(inspeccionId, usuarioId)
                    if (inspeccionDetalle.size != 0) {
                        formAditional()
                    } else {
                        inspeccionViewModel.setError("Llenar el segundo formulario")
                    }
                } else {
                    inspeccionViewModel.setError("Llenar el primer formulario")
                }
            }
            R.id.fabClose -> {
                if (inspeccionId == 1) {
                    val insp = inspeccionViewModel.getInspeccion(inspeccionId, usuarioId)
                    if (insp != null) {
                        val inspeccionDetalle = inspeccionViewModel.getInspeccionDetalle(inspeccionId, usuarioId)
                        if (inspeccionDetalle.size != 0) {
                            inspeccionViewModel.activateOrCloseInspeccion(inspeccionId, usuarioId, 0)
                        } else {
                            inspeccionViewModel.setError("Llenar el segundo formulario")
                        }
                    } else {
                        inspeccionViewModel.setError("Llenar el primer formulario")
                    }
                } else {
                    inspeccionViewModel.activateOrCloseInspeccion(inspeccionId, usuarioId, 0)
                }
            }
        }
    }

    var inspeccionId: Int = 0
    var usuarioId: Int = 0
    lateinit var a: InspeccionAdicionales
    lateinit var inspeccionViewModel: InspeccionViewModel

    private var fabClose: ExtendedFloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        a = InspeccionAdicionales()
        arguments?.let {
            inspeccionId = it.getInt(ARG_PARAM1)
            usuarioId = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_observation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inspeccionViewModel = ViewModelProvider(this).get(InspeccionViewModel::class.java)
        inspeccionViewModel.initialRealm()
        editTextObservacion.setOnClickListener(this)
        fabAditional.setOnClickListener(this)
        fabClose = view.findViewById(R.id.fabClose)
        fabClose?.setOnClickListener(this)
        bindUI()
        message()
    }


    private fun bindUI() {
        if (inspeccionId == 1) {
            editTextObservacion.visibility = View.GONE
            fabClose?.visibility = View.VISIBLE
        }

        val inspeccionAdicionalAdapter = InspeccionAdicionalAdapter(object : OnItemClickListener.InspeccionAdicionalListener {
            override fun onItemClick(i: InspeccionAdicionales, view: View, position: Int) {
                val popupMenu = PopupMenu(context!!, view)
                if (inspeccionId == 1) {
                    popupMenu.menu.add(1, 2, 1, getText(R.string.delete))
                } else {
                    if (i.tipo == 1) {
                        popupMenu.menu.add(0, 1, 0, getText(R.string.edit))
                        popupMenu.menu.add(1, 2, 1, getText(R.string.delete))
                    } else {
                        if (i.nombreValor != "NO") {
                            popupMenu.menu.add(0, 1, 0, getText(R.string.edit_medidas))
                        }
                    }
                }

                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        1 -> updateAdicionalDialog(i)
                        2 -> messageDialog(i, context!!)
                    }
                    false
                }
                popupMenu.show()
            }
        })
        val layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = inspeccionAdicionalAdapter
        val inspeccionAdicionales = inspeccionViewModel.getInspeccionAdicional(inspeccionId, usuarioId)
        inspeccionAdicionales.addChangeListener { t ->
            if (t.size != 0) {
                validateAdicional(t)
            }
            inspeccionAdicionalAdapter.addItems(t)
        }

        if (inspeccionAdicionales.size != 0) {
            validateAdicional(inspeccionAdicionales)
        }

        inspeccionAdicionalAdapter.addItems(inspeccionAdicionales)
    }

    companion object {
        @JvmStatic
        fun newInstance(inspeccionId: Int, usuarioId: Int) =
                ObservationFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, inspeccionId)
                        putInt(ARG_PARAM2, usuarioId)
                    }
                }
    }

    private fun spinnerDialogObservacion() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)
        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        textViewTitulo.text = String.format("%s", "Observación")

        val observationAdapter = ObservationAdapter(object : ObservationAdapter.OnItemClickListener {
            override fun onItemClick(m: MenuPrincipal, position: Int) {
                a.tipo = m.menuId
                editTextObservacion.setText(m.title)
                dialog.dismiss()
            }
        })
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = observationAdapter

        val menuPrincipals: ArrayList<MenuPrincipal> = ArrayList()
        menuPrincipals.add(MenuPrincipal(1, "Otros Peligros y Riesgos", 0, 0))
        //menus.add(MenuPrincipal(2, "Medidas de Control", 0, 0))
        observationAdapter.addItems(menuPrincipals)
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
                editTextDescripcion.text = null
                Util.hideKeyboardFrom(context!!, view!!)
                Util.toastMensaje(context!!, s)
            }
        })
    }

    private fun formAditional() {
        a.inspeccionAdicionalId = inspeccionViewModel.getInspeccionAditionIdentity()
        a.inspeccionId = inspeccionId
        a.usuarioId = usuarioId
        a.estado = 1
        a.nombreTipo = editTextObservacion.text.toString()
        a.descripcion = editTextDescripcion.text.toString()
        a.fechaVerificacion = Util.getFecha()
        inspeccionViewModel.validateInspeccionAditional(a)
    }

    private fun validateAdicional(i: List<InspeccionAdicionales>) {
        if (inspeccionId == 2) {
            var count = 0
            var tipo1 = 0
            var tipo2 = 0
            for (a: InspeccionAdicionales in i) {
                if (a.tipo == 2) {
                    if (a.descripcion.isEmpty()) {
                        count++
                    }
                    tipo2++
                }
                if (a.tipo == 1) {
                    tipo1++
                }
            }
            if (count == 0) {
                fabClose?.visibility = View.VISIBLE
            }

            if (tipo2 == 0) {
                if (tipo1 > 0) {
                    fabClose?.visibility = View.GONE
                }
            }
        }
    }

    private fun messageDialog(d: InspeccionAdicionales, context: Context) {
        val dialog = MaterialAlertDialogBuilder(context)
                .setTitle("Mensaje")
                .setMessage(
                        String.format("%s", "Estas seguro de borrar ?")
                )
                .setPositiveButton("Aceptar") { dialog, _ ->
                    inspeccionViewModel.deleteInspeccionAdicional(d.inspeccionAdicionalId)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.cancel()
                }
        dialog.show()
    }

    private fun updateAdicionalDialog(i: InspeccionAdicionales) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
                LayoutInflater.from(context).inflate(R.layout.dialog_inspection_detalle, null)
        val textDialogTitle: TextView = view.findViewById(R.id.textDialogTitle)
        val editTextObservacion: TextInputEditText = view.findViewById(R.id.editTextObservacion)
        val buttonGuardar: MaterialButton = view.findViewById(R.id.buttonGuardar)
        val buttonCancelar: MaterialButton = view.findViewById(R.id.buttonCancelar)
        textDialogTitle.text = String.format("%s", i.nombreTipo)

        editTextObservacion.setText(i.descripcion)

        builder.setView(view)
        val dialogSpinner = builder.create()
        dialogSpinner.show()

        buttonGuardar.setOnClickListener {
            val obs = editTextObservacion.text.toString()
            inspeccionViewModel.validateUpdateInspeccionAdicional(i.inspeccionAdicionalId, obs)
            Util.hideKeyboardFrom(context!!, view)
            dialogSpinner.dismiss()
        }

        buttonCancelar.setOnClickListener {
            dialogSpinner.dismiss()
        }
    }
}