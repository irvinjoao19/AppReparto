package com.calida.dsige.reparto.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.calida.dsige.reparto.data.local.*
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.viewModel.EnvioViewModel
import com.calida.dsige.reparto.helper.Util
import com.calida.dsige.reparto.ui.adapters.SendAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_send.*

class SendActivity : AppCompatActivity() {

    lateinit var sendAdapter: SendAdapter

    lateinit var builder: AlertDialog.Builder
    var dialog: AlertDialog? = null

    lateinit var envioViewModel: EnvioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        val b = intent.extras
        if (b != null) {
            envioViewModel = ViewModelProvider(this).get(EnvioViewModel::class.java)
            envioViewModel.initialRealm()
            bindUI(b.getInt("usuarioId"))
            message()
        }
    }

    private fun bindUI(usuarioId: Int) {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Envio de Pendientes"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val layoutManager = LinearLayoutManager(this)
        sendAdapter = SendAdapter(object : SendAdapter.OnItemClickListener {
            override fun onItemClick(m: MenuPrincipal, position: Int) {
                confirmSend(usuarioId, m.cantidad, m.menuId)
            }
        })
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = sendAdapter

        val registro = envioViewModel.getResultRegistro()
        val reparto = envioViewModel.getResultReparto()
        val menuPrincipals: ArrayList<MenuPrincipal> = ArrayList()
        menuPrincipals.add(MenuPrincipal(1, "Enviar Registros", registro.size, R.mipmap.ic_registro))
        menuPrincipals.add(MenuPrincipal(2, "Enviar Repartos", reparto.size, R.mipmap.ic_registro))
        menuPrincipals.add(MenuPrincipal(3, "Reenviar Fotos", 0, R.mipmap.ic_fotos))
        textViewRegistro.text = String.format("%s %s", "Registros", reparto.size)
        sendAdapter.addItems(menuPrincipals)

        val photos = envioViewModel.getResultPhoto()
        textViewPhoto.text = String.format("%s %s", "Fotos ", photos.size)
    }

    private fun confirmSend(usuarioId: Int, count: Int, id: Int) {
        val materialDialog = MaterialAlertDialogBuilder(this)
                .setTitle("Mensaje")
                .setMessage(String.format("%s", "Antes de enviar asegurate de contar con internet !.\nDeseas enviar los Registros ?."))
                .setPositiveButton("Aceptar") { dialog, _ ->
                    when (id) {
                        1 -> if (count > 0) {
                            envioViewModel.sendData(this)
                            load("Enviando Registros...")
                        } else {
                            Util.dialogMensaje(this@SendActivity, "Mensaje", "No cuentas con ningún Registro")
                        }
                        2 -> if (count > 0) {
                            envioViewModel.sendDataReparto(this)
                            load("Enviando Repartos...")
                        } else {
                            Util.dialogMensaje(this@SendActivity, "Mensaje", "No cuentas con ningún Registro")
                        }
                        3 -> {
                            envioViewModel.sendPhoto(this)
                            load("Enviando Fotos...")
                        }
                    }
                    dialog.dismiss()
                }.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
        materialDialog.show()
    }

    private fun load(title: String) {
        builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
                LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        textViewTitle.text = title
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    private fun message() {
        envioViewModel.error.observe(this, Observer { s ->
            if (s != null) {
                if (dialog != null) {
                    if (dialog!!.isShowing) {
                        dialog!!.dismiss()
                    }
                }
                Util.dialogMensaje(this@SendActivity, "Mensaje", s)
            }
        })

        envioViewModel.success.observe(this, Observer { s ->
            if (s != null) {
                if (dialog != null) {
                    if (dialog!!.isShowing) {
                        dialog!!.dismiss()
                    }
                }
                val resultados = envioViewModel.getResultRegistro()
                val reparto = envioViewModel.getResultReparto()
                val menuPrincipals: ArrayList<MenuPrincipal> = ArrayList()
                menuPrincipals.add(MenuPrincipal(1, "Enviar Registros", resultados.size, R.mipmap.ic_registro))
                menuPrincipals.add(MenuPrincipal(2, "Enviar Repartos", reparto.size, R.mipmap.ic_registro))
                textViewRegistro.text = String.format("%s %s", "Registros", resultados.size)
                sendAdapter.addItems(menuPrincipals)
                val photos = envioViewModel.getResultPhoto()
                textViewPhoto.text = String.format("%s %s", "Fotos ", photos.size)
                Util.dialogMensaje(this@SendActivity, "Mensaje", s)
            }
        })
    }
    // TODO ANTIGUO RX JAVA 15/08/2019

    // TODO ANTIGUO GITHUB 16/01/2019
}