package com.calida.dsige.reparto.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
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
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_preview_camera.*
import java.io.File
import java.lang.Exception

class PreviewCameraActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabOk -> formRegistroPhoto()
            R.id.fabClose -> {
                startActivity(
                        Intent(this, CameraActivity::class.java)
                                .putExtra("repartoId", repartoId)
                )
                finish()
            }
        }
    }

    lateinit var repartoViewModel: RepartoViewModel
    private var repartoId = 0
    private var nameImg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_camera)

        val b = intent.extras
        if (b != null) {
            repartoId = b.getInt("repartoId")
            nameImg = b.getString("nameImg")!!
            bindUI()
        }
    }

    private fun bindUI() {
        repartoViewModel = ViewModelProvider(this).get(RepartoViewModel::class.java)
        repartoViewModel.initialRealm()

        fabClose.setOnClickListener(this)
        fabOk.setOnClickListener(this)
        textViewImg.text = nameImg

        Handler().postDelayed({
//            val f = File(Environment.getExternalStorageDirectory().toString() + Folder + "/$nameImg")
            val f = File(Util.getFolder(this), nameImg)
            Picasso.get().load(f)
                    .into(imageView, object : Callback {
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {

                        }
                    })
        }, 200)

        repartoViewModel.mensajeError.observe(this, Observer { s ->
            if (s != null) {
                Util.toastMensaje(this, s)
            }
        })

        repartoViewModel.mensajeSuccess.observe(this, Observer { s ->
            if (s != null) {
//                startActivity(Intent(this, PhotoRepartoActivity::class.java)
//                        .putExtra("repartoId", repartoId)
//                )
                finish()
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(
                    Intent(this, CameraActivity::class.java)
                            .putExtra("repartoId", repartoId)
            )
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun formRegistroPhoto() {
        repartoViewModel.savePhoto(RepartoCargoFoto(repartoViewModel.getRepartoCargoFotoIdentity(), repartoId, nameImg,1,1))
    }
}