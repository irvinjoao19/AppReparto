package com.calida.dsige.reparto.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.Toolbar
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.calida.dsige.reparto.data.local.*
import com.calida.dsige.reparto.helper.Gps
import com.calida.dsige.reparto.helper.Util
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.viewModel.StartViewModel
import com.calida.dsige.reparto.ui.adapters.ServicioAdapter
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import com.calida.dsige.reparto.ui.services.DistanceService
import com.calida.dsige.reparto.ui.services.SendRepartoServices
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_start.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class StartActivity : AppCompatActivity() {

    lateinit var startViewModel: StartViewModel
    lateinit var builder: AlertDialog.Builder
    var dialog: AlertDialog? = null

    var direction: String = ""
    var nameImg: String = ""
    var titulo: String = ""
    var lecturaManual: Int = 0
    var nameServices: String = ""
    var stateServices: Int = 0
    var online: Int = 0
    var lectura: Int? = 0
    var activo: Int? = 1

    var usuarioId: Int = 0

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fileName", direction)
        outState.putString("nameImg", nameImg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        if (savedInstanceState != null) {
            direction = savedInstanceState.getString("fileName")!!
            nameImg = savedInstanceState.getString("nameImg")!!
        }

        val b = intent.extras
        if (b != null) {
            usuarioId = b.getInt("usuarioId")
        }
        bindUI()
        message()
    }

    private fun bindUI() {
        startViewModel = ViewModelProvider(this).get(StartViewModel::class.java)
        startViewModel.initialRealm()
        val l = startViewModel.getLogin()
        usuarioId = l.iD_Operario
        online = l.operario_EnvioEn_Linea

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Inicio de Actividades"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
            finish()
        }

        val layoutManager = LinearLayoutManager(this)
        val serviciodapter = ServicioAdapter(object : OnItemClickListener.ServicioListener {
            override fun onItemClick(s: Servicio, position: Int) {
                when (s.id_servicio) {
                    5 -> tipoReparto(s.id_servicio, s.nombre_servicio)
                }
            }
        })

        val services = startViewModel.servicioAll
        serviciodapter.addItems(services)

        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = serviciodapter

    }

    private fun tipoReparto(state: Int, name: String) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this).inflate(R.layout.dialog_reparto, null)
        val linearLayoutStart: LinearLayout = v.findViewById(R.id.linearLayoutStart)
        val linearLayoutEnd: LinearLayout = v.findViewById(R.id.linearLayoutEnd)
        val linearLayoutLista: LinearLayout = v.findViewById(R.id.linearLayoutLista)
        val textViewCountCode: TextView = v.findViewById(R.id.textViewCountCode)
        val buttonAceptar: MaterialButton = v.findViewById(R.id.buttonAceptar)
        val buttonActive: MaterialButton = v.findViewById(R.id.buttonActive)

        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        val inicio: Registro? = startViewModel.getInicioFinTrabajo(usuarioId, Util.getFecha(), "INICIO")
        if (inicio != null) {
            linearLayoutStart.visibility = View.GONE

            val final: Registro? = startViewModel.getInicioFinTrabajo(usuarioId, Util.getFecha(), "FIN")
            if (final != null) {
                linearLayoutEnd.visibility = View.GONE
                textViewCountCode.visibility = View.GONE
                linearLayoutLista.visibility = View.GONE
                buttonActive.visibility = View.GONE
            } else {
                linearLayoutEnd.visibility = View.VISIBLE
                textViewCountCode.visibility = View.VISIBLE
                linearLayoutLista.visibility = View.VISIBLE
                buttonActive.visibility = View.VISIBLE
            }
        } else {
            linearLayoutStart.visibility = View.VISIBLE
            linearLayoutEnd.visibility = View.GONE
            textViewCountCode.visibility = View.GONE
            linearLayoutLista.visibility = View.GONE
            buttonActive.visibility = View.GONE
        }

        linearLayoutStart.setOnClickListener {
            val intent = Intent(this@StartActivity, SelfiViewPhoto::class.java)
            intent.putExtra("repartoSelfi", "INICIO")
            startActivity(intent)
            dialog.dismiss()
        }
        linearLayoutEnd.setOnClickListener {
            mensajeConfirmacion()
            dialog.dismiss()
        }

//        val count = realm.where(Reparto::class.java)?.distinct("id_Reparto")?.equalTo("activo", activo)!!.count()
        val count = startViewModel.getRepartoActive(activo)
        if (count != null) {
            if (count > 0) {
                textViewCountCode.visibility = View.VISIBLE
                textViewCountCode.text = count.toString()
            } else {
                textViewCountCode.visibility = View.GONE
            }
        }
        linearLayoutLista.setOnClickListener {
            val intent = Intent(this@StartActivity, SuministroRepartoActivity::class.java)
            intent.putExtra("estado", state)
            intent.putExtra("nombre", name)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }
        buttonAceptar.setOnClickListener {
            dialog.dismiss()
        }

        buttonActive.setOnClickListener {
            startService(Intent(this, DistanceService::class.java))
            startService(Intent(this, SendRepartoServices::class.java))
            dialog.dismiss()
        }
    }

    private fun mensajeConfirmacion() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        builder.setTitle("Mensaje")
                .setMessage("Estas seguro de ingresar a FIN DE TRABAJO?\nRecuerda que al tomar la selfie se bloqueará lista de reparto")
                .setPositiveButton("Aceptar") { d, _ ->
                    val intent = Intent(this@StartActivity, SelfiViewPhoto::class.java)
                    intent.putExtra("repartoSelfi", "FIN")
                    startActivity(intent)
                    d.dismiss()
                }
                .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun createImage(code: Int) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(this).packageManager) != null) {
            val folder = Util.getFolder(this)
            nameImg = Util.getFechaActualForPhoto(usuarioId, usuarioId) + ".jpg"
            val image = File(folder, nameImg)
            direction = "$folder/$nameImg"
            val uriSavedImage = Uri.fromFile(image)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage)
            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                    m.invoke(null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            startActivityForResult(takePictureIntent, code)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode != 1) {
                Util.toastMensaje(this, "Espere....")
            }
            generateImage(requestCode)
        }
    }

    private fun generateImage(code: Int) {
        val image: Observable<Boolean> = Util.generateImageAsync(direction)
        image.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Boolean> {
                    override fun onComplete() {
                        Log.i("ERROR PHOTO", "EXITOSO")
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Boolean) {
                        if (t) {
                            saveRegistroReparto(nameImg)
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e("ERROR PHOTO", e.toString())
                        Util.toastMensaje(this@StartActivity, "Volver a intentarlo")
                    }
                })
    }

    private fun saveRegistroReparto(nameImg: String) {
        val gps = Gps(this)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                gps.showAlert(this)
            } else {
                val photos: RealmList<Photo> = RealmList()
                val photo: Photo? = Photo(startViewModel.getPhotoIdentity(), 0, usuarioId, nameImg, Util.getFechaActual(), 11, 1, gps.latitude.toString(), gps.longitude.toString())
                photos.add(photo)
                val registro = Registro(startViewModel.getRegistroIdentity(), usuarioId, usuarioId, Util.getFechaActual(), gps.latitude.toString(), gps.longitude.toString(), 11, 1, "INICIO", nameImg, photos)
                startViewModel.saveZonaPeligrosa(registro)
                startService(Intent(this, DistanceService::class.java))
                startService(Intent(this, SendRepartoServices::class.java))
                val intent = Intent(this, SuministroRepartoActivity::class.java)
                        .putExtra("estado", stateServices)
                        .putExtra("nombre", nameServices)
                        .putExtra("usuarioId", usuarioId)
                startActivity(intent)
            }
        } else {
            gps.showSettingsAlert(this)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun message() {
        startViewModel.error.observe(this, androidx.lifecycle.Observer { s ->
            if (s != null) {
                Util.dialogMensaje(this, "Mensaje", s)
            }
        })
    }
}