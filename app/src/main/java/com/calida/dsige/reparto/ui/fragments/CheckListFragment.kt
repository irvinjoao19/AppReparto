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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.CheckList
import com.calida.dsige.reparto.data.local.InspeccionDetalle
import com.calida.dsige.reparto.data.viewModel.InspeccionViewModel
import com.calida.dsige.reparto.helper.Util
import com.calida.dsige.reparto.ui.adapters.CheckListAdapter
import com.calida.dsige.reparto.ui.adapters.InspeccionDetalleAdapter
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_check_list.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CheckListFragment : Fragment(), View.OnClickListener {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabCheckList -> {
                val insp = inspeccionViewModel.getInspeccion(inspeccionId, usuarioId)
                if (insp != null) {
                    fabCheckList.visibility = View.GONE
                    dialogFormat(inspeccionId, insp.trasladoId)
                } else {
                    inspeccionViewModel.setError("Llenar el primer formulario")
                }
            }
        }
    }

    var inspeccionId: Int = 0
    var usuarioId: Int = 0
    var tipo: Int = 0
    lateinit var inspeccionViewModel: InspeccionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            inspeccionId = it.getInt(ARG_PARAM1)
            usuarioId = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_check_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inspeccionViewModel = ViewModelProvider(this).get(InspeccionViewModel::class.java)
        inspeccionViewModel.initialRealm()
        fabCheckList.setOnClickListener(this)
        bindUI()
        message()
    }

    private fun bindUI() {
        val inspeccionDetalleAdapter = InspeccionDetalleAdapter(object : OnItemClickListener.InspeccionDetalleListener {
            override fun onItemClick(d: InspeccionDetalle, view: View, position: Int) {
//                val popupMenu = PopupMenu(context!!, view)
//                if (d.aplicaObs1 == 1) {
//                    popupMenu.menu.add(0, 1, 0, getText(R.string.edit))
//                }
//                popupMenu.menu.add(1, 2, 1, getText(R.string.delete))
//                popupMenu.setOnMenuItemClickListener { item ->
//                    when (item.itemId) {
//                        1 -> updateDetalleDialog(d)
//                        2 -> messageDialog(d, context!!)
//                    }
//                    false
//                }
//                popupMenu.show()
            }
        })
        val layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = inspeccionDetalleAdapter

        val inspeccionDetalle = inspeccionViewModel.getInspeccionDetalle(inspeccionId, usuarioId)
        inspeccionDetalle.addChangeListener { t ->
            if (t.size == 0) {
                inspeccionViewModel.activateOrCloseInspeccion(inspeccionId, usuarioId, 1)
            }
            inspeccionDetalleAdapter.addItems(t)
        }
        inspeccionDetalleAdapter.addItems(inspeccionDetalle)
    }

    companion object {
        @JvmStatic
        fun newInstance(inspeccionId: Int, usuarioId: Int) =
                CheckListFragment().apply {
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
                Util.hideKeyboardFrom(context!!, view!!)
                Util.toastMensaje(context!!, s)
            }
        })
    }

    private fun dialogFormat(formatoInspeccionId: Int, tipo: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
                LayoutInflater.from(context).inflate(R.layout.dialog_check_list, null)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val fabDetalle: FloatingActionButton = view.findViewById(R.id.fabDetalle)
        val fabClose: FloatingActionButton = view.findViewById(R.id.fabClose)
        if (formatoInspeccionId == 1) {
            fabClose.visibility = View.GONE
        }

        builder.setView(view)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()

        var size = 0

        val layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager

        val caracteristicasAdapter = CheckListAdapter(object : OnItemClickListener.CheckListListener {
            override fun onItemClick(e: CheckList, v: View, position: Int) {
                when (v.id) {
                    R.id.radioSI -> {
                        inspeccionViewModel.setIsCheck(e, 1, e.valor1)
                    }
                    R.id.radioNO -> {
                        inspeccionViewModel.setIsCheck(e, 1, e.valor2)
                    }
                    R.id.radioNA -> {
                        inspeccionViewModel.setIsCheck(e, 1, e.valor3)
                    }
                    R.id.radioTA -> {
                        inspeccionViewModel.setIsCheck(e, 1, e.valor4)
                    }
                }
            }
        })
        recyclerView.adapter = caracteristicasAdapter
        val checkList = if (formatoInspeccionId == 1) {
            inspeccionViewModel.getCheckList(formatoInspeccionId, tipo)
        } else {
            inspeccionViewModel.getCheckList(formatoInspeccionId)
        }
        caracteristicasAdapter.addItems(checkList)

        if (formatoInspeccionId == 1) {
            size = inspeccionViewModel.getCheckListCount(formatoInspeccionId, tipo)
        }

        fabDetalle.setOnClickListener {
            if (formatoInspeccionId == 2) {
                fabCheckList.visibility = View.VISIBLE
                inspeccionViewModel.insertInspeccionDetalleCheck(formatoInspeccionId, usuarioId)
                dialog.dismiss()
            } else {
                val count = inspeccionViewModel.getCheckListCountActive(formatoInspeccionId, tipo)
                if (count == size) {
                    inspeccionViewModel.insertInspeccionDetalleCheck(formatoInspeccionId, usuarioId)
                    fabCheckList.visibility = View.VISIBLE
                    dialog.dismiss()
                } else {
                    inspeccionViewModel.setError("Completar todo el formulario")
                }
            }
        }

        fabClose.setOnClickListener {
            fabCheckList.visibility = View.VISIBLE
            dialog.dismiss()
        }
    }

    private fun updateDetalleDialog(d: InspeccionDetalle) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
                LayoutInflater.from(context).inflate(R.layout.dialog_inspection_detalle, null)
        val textDialogTitle: TextView = view.findViewById(R.id.textDialogTitle)
        val textViewLayoutHint1: TextInputLayout = view.findViewById(R.id.textViewLayoutHint1)
        val editTextObservacion: TextInputEditText = view.findViewById(R.id.editTextObservacion)
        val buttonGuardar: MaterialButton = view.findViewById(R.id.buttonGuardar)
        val buttonCancelar: MaterialButton = view.findViewById(R.id.buttonCancelar)
        textDialogTitle.text = String.format("%s", d.descripcion)

        if (d.aplicaObs1 == 1) {
            textViewLayoutHint1.visibility = View.VISIBLE
            if (d.campo1.isNotEmpty()) {
                textViewLayoutHint1.hint = d.campo1
            }
        }

        editTextObservacion.setText(d.observacion)

        builder.setView(view)
        val dialogSpinner = builder.create()
        dialogSpinner.show()

        buttonGuardar.setOnClickListener {
            val f = Util.getFecha()
            val obs1 = editTextObservacion.text.toString()
            val obs2 = editTextObservacion.text.toString()
            if (inspeccionViewModel.validateUpdateInspeccionDetalle(d, f, obs1, obs2)) {
                dialogSpinner.dismiss()
            }
        }

        buttonCancelar.setOnClickListener {
            dialogSpinner.dismiss()
        }
    }

    private fun messageDialog(d: InspeccionDetalle, context: Context) {
        val dialog = MaterialAlertDialogBuilder(context)
                .setTitle("Mensaje")
                .setMessage(
                        String.format("%s %s", "Estas seguro de eliminar ?", d.descripcion)
                )
                .setPositiveButton("Aceptar") { dialog, _ ->
                    inspeccionViewModel.deleteInspeccionDetalle(d.inspeccionDetalleId)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.cancel()
                }
        dialog.show()
    }
}