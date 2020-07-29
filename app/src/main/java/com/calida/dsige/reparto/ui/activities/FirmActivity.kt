package com.calida.dsige.reparto.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.local.Photo
import com.calida.dsige.reparto.data.local.RepartoCargoFoto
import com.calida.dsige.reparto.data.viewModel.PhotoViewModel
import com.calida.dsige.reparto.data.viewModel.RepartoViewModel
import com.calida.dsige.reparto.helper.Gps
import com.calida.dsige.reparto.helper.Util
import kotlinx.android.synthetic.main.activity_firm.*

class FirmActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabFirma -> {
                if (paintView.validDraw()) {
                    val nameImg = paintView.save(this, repartoId, 5)
                    repartoViewModel.savePhoto(RepartoCargoFoto(repartoViewModel.getRepartoCargoFotoIdentity(), repartoId, nameImg, 2,1))
                } else {
                    repartoViewModel.setError("Debes de Firmar.")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.firma, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                paintView.clear()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    lateinit var repartoViewModel: RepartoViewModel
    lateinit var p: Photo
    var repartoId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firm)
        p = Photo()
        val b = intent.extras
        if (b != null) {
            repartoId = b.getInt("repartoId")
            bindUI()
        }
    }

    private fun bindUI() {
        repartoViewModel = ViewModelProvider(this).get(RepartoViewModel::class.java)
        repartoViewModel.initialRealm()
        fabFirma.setOnClickListener(this)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = String.format("Tomar Firma")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        paintView.init(metrics)

        repartoViewModel.mensajeError.observe(this, Observer {
            Util.toastMensaje(this, it)
        })

        repartoViewModel.mensajeSuccess.observe(this, Observer {
            Util.toastMensaje(this, it)
            finish()
        })
    }
}