package com.calida.dsige.reparto.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import androidx.appcompat.widget.Toolbar
import android.view.View
import com.calida.dsige.reparto.data.local.Photo
import com.calida.dsige.reparto.data.local.Registro
import com.calida.dsige.reparto.helper.Gps
import com.calida.dsige.reparto.helper.Util
import com.calida.dsige.reparto.R
import io.realm.Realm
import java.io.File
import java.util.*
import android.util.Log
import com.calida.dsige.reparto.data.dao.interfaces.*
import com.calida.dsige.reparto.data.dao.overMethod.*
import com.calida.dsige.reparto.ui.services.DistanceService
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_selfi_view_photo.*
import android.view.WindowManager
import com.calida.dsige.reparto.ui.services.SendRepartoServices

class SelfiViewPhoto : AppCompatActivity(), View.OnClickListener {

    private lateinit var realm: Realm
    private lateinit var loginImp: LoginImplementation
    private lateinit var servicioImp: ServicioImplementation
    private lateinit var photoImp: PhotoImplementation
    private lateinit var registroImp: RegistroImplementation
    private lateinit var photoRepartoImp: PhotoRepartoImplementation

    private lateinit var folder: File
    private lateinit var image: File

    var idUser: Int = 0
    var nameImg: String = ""
    var direction: String = ""
    var registro_Latitud: String = ""
    var registro_Longitud: String = ""
    var titulo: String = ""

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fileName", direction)
        outState.putString("nameImg", nameImg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selfi_view_photo)

        if (savedInstanceState != null) {
            direction = savedInstanceState.getString("fileName")!!
            nameImg = savedInstanceState.getString("nameImg")!!
        }

        realm = Realm.getDefaultInstance()
        photoRepartoImp = PhotoRepartoOver(realm)
        loginImp = LoginOver(realm)
        servicioImp = ServicioOver(realm)
        servicioImp = ServicioOver(realm)
        photoImp = PhotoOver(realm)
        registroImp = RegistroOver(realm)

        val bundle = intent.extras
        if (bundle != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            titulo = bundle.getString("repartoSelfi")!!
            binIU(titulo)
        }
    }

    private fun binIU(title: String) {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = String.format("Selfie %s", title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@SelfiViewPhoto, StartActivity::class.java)
            startActivity(intent)
            finish()
        }
        imageViewButton.setOnClickListener(this)
        val user = loginImp.login
        idUser = user!!.iD_Operario
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imageViewButton -> {
                val gps = Gps(this)
                if (gps.isLocationEnabled()) {
                    if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                        gps.showAlert(this)
                    } else {
                        registro_Latitud = gps.latitude.toString()
                        registro_Longitud = gps.longitude.toString()
                        createImage()
                    }
                } else {
                    gps.showSettingsAlert(this)
                }
            }
        }
    }

    private fun createImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", -1)
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(this@SelfiViewPhoto).packageManager) != null) {
            folder = Util.getFolder(this)
            nameImg = Util.getFechaActualRepartoPhoto(idUser, titulo) + ".jpg"
            image = File(folder, nameImg)
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
            startActivityForResult(takePictureIntent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            generateImage()
        }
    }

    private fun generateImage() {
        val image: Observable<Boolean> = Util.generateImageAsync(direction)
        image.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Boolean> {
                    override fun onComplete() {
                        Log.i("ERROR PHOTO", "EXITOSO")
                        val f = File(Util.getFolder(this@SelfiViewPhoto), nameImg)
//                        val f = File(Environment.getExternalStorageDirectory(), Util.FolderImg + "/" + nameImg)
                        Picasso.get().load(f).into(imageViewPhoto)
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Boolean) {
                        if (t) {
                            saveRegistro(titulo, nameImg)
                            imageViewButton.visibility = View.GONE
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e("ERROR PHOTO", e.toString())
                        Util.toastMensaje(this@SelfiViewPhoto, "Volver a intentarlo")
                    }
                })
    }

    @SuppressLint("InvalidWakeLockTag")
    private fun saveRegistro(observacion: String, nameImg: String) {
        val gps = Gps(this)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                gps.showAlert(this)
            } else {
                val photos: RealmList<Photo> = RealmList()
                val photo: Photo? = Photo(photoImp.getPhotoIdentity(), 0, idUser, nameImg, Util.getFechaActual(), 9, 1, gps.latitude.toString(), gps.longitude.toString())
                photos.add(photo)
                val registro = Registro(registroImp.getRegistroIdentity(), idUser, idUser, Util.getFechaActual(), registro_Latitud, registro_Longitud, 9, 1, observacion, nameImg, photos)
                if (observacion == "INICIO") {
                    startService(Intent(this, DistanceService::class.java))
                    startService(Intent(this, SendRepartoServices::class.java))
                } else {
                    Util.clearNotification(this)
                    Util.dialogMensaje(this, "Reparto Finalizado", "Ingresa a Enviar Pendientes para culminar tu trabajo")
                    stopService(Intent(this, DistanceService::class.java))
                    stopService(Intent(this, SendRepartoServices::class.java))
                }
                registroImp.saveZonaPeligrosa(registro)
            }
        } else {
            gps.showSettingsAlert(this)
        }
    }
}