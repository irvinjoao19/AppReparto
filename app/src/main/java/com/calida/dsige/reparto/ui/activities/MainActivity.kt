package com.calida.dsige.reparto.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.*
import android.view.Menu
import android.view.MenuItem
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.calida.dsige.reparto.data.dao.interfaces.*
import com.calida.dsige.reparto.data.dao.overMethod.*
import com.calida.dsige.reparto.data.apiServices.ConexionRetrofit
import com.calida.dsige.reparto.data.local.*
import com.calida.dsige.reparto.helper.MessageError
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.helper.Util
import com.calida.dsige.reparto.ui.services.*
import com.calida.dsige.reparto.ui.adapters.MenuAdapter
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmResults
import java.io.IOException
import kotlin.system.exitProcess
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.calida.dsige.reparto.data.apiServices.ApiServices
import com.google.android.material.button.MaterialButton
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                if (registros.size == 0) {
                    confirmLogOut()
                } else {
                    Util.dialogMensaje(this@MainActivity, "Mensaje", "Tienes registros pendientes por enviar")
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    lateinit var migrationInterface: ApiServices

    lateinit var realm: Realm
    lateinit var loginImp: LoginImplementation
    lateinit var servicioImp: ServicioImplementation
    lateinit var migrationImp: MigrationImplementation
    lateinit var photoImp: PhotoImplementation
    lateinit var registroImp: RegistroImplementation

    lateinit var registros: RealmResults<Registro>
    lateinit var servicios: RealmResults<Servicio>

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    var title: Array<String>? = null
    var image: IntArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        realm = Realm.getDefaultInstance()
        loginImp = LoginOver(realm)
        servicioImp = ServicioOver(realm)
        migrationImp = MigrationOver(realm)
        photoImp = PhotoOver(realm)
        registroImp = RegistroOver(realm)
        migrationInterface = ConexionRetrofit.api.create(ApiServices::class.java)
        existsUser(loginImp.login)
    }

    private fun existsUser(login: Login?) {
        if (login == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            bindUI(login)
            servicios = servicioImp.servicioAll
            registros = registroImp.getAllRegistro(1)
        }
    }

    private fun bindUI(login: Login) {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Menu Principal"
        if (login.tipoUsuario == "S") {
            title = arrayOf("Sincronizar", "Inicio de Actividades", "Envio de Pendientes", "Administración", "Cerrar Sesion")
            image = intArrayOf(R.mipmap.ic_sync, R.mipmap.ic_add, R.mipmap.ic_send, R.mipmap.ic_configuration, R.mipmap.ic_logout)
        } else {
            title = arrayOf("Sincronizar", "Inicio de Actividades", "Envio de Pendientes", "Cerrar Sesion")
            image = intArrayOf(R.mipmap.ic_sync, R.mipmap.ic_add, R.mipmap.ic_send, R.mipmap.ic_logout)
        }

        val layoutManager = LinearLayoutManager(this@MainActivity)
        val menuAdapter = MenuAdapter(title, image, object : MenuAdapter.OnItemClickListener {
            override fun onItemClick(strings: String, position: Int) {
                when (strings) {
                    "Sincronizar" -> {
                        if (registros.size == 0) {
                            confirmMigration(login.iD_Operario)
                        } else {
                            Util.dialogMensaje(this@MainActivity, "Mensaje", "Tienes registros pendientes por enviar")
                        }
                    }
                    "Inicio de Actividades" -> {
//                        startActivity(Intent(this@MainActivity, FirmActivity::class.java))
                        if (servicios.size != 0) {
                            startActivity(Intent(this@MainActivity, StartActivity::class.java)
                                    .putExtra("usuarioId", login.iD_Operario)
                                    .putExtra("name", login.operario_Nombre))
                            finish()
                        } else {
                            Toast.makeText(this@MainActivity, "Sincronice primero !", Toast.LENGTH_LONG).show()
                        }
                    }
                    "Envio de Pendientes" -> {
                        if (servicios.size != 0) {
                            val intent = Intent(this@MainActivity, SendActivity::class.java)
                                    .putExtra("usuarioId", login.iD_Operario)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@MainActivity, "Sincronice primero !", Toast.LENGTH_LONG).show()
                        }
                    }
                    "Cerrar Sesion" -> {
//                        startActivity(Intent(this@MainActivity, ExamplePhotoActivity::class.java))
                        if (registros.size == 0) {
                            confirmLogOut()
                        } else {
                            Util.dialogMensaje(this@MainActivity, "Mensaje", "Tienes registros pendientes por enviar")
                        }
                    }
                }
            }
        })
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = menuAdapter

        val bundle = intent.extras

        if (bundle != null) {
            if (bundle.getString("update") == "si") {
                SyncObservadas().execute(login.iD_Operario)
            }
        }
    }

    private fun logOut() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun confirmMigration(operarioId: Int?) {
        builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v = LayoutInflater.from(this).inflate(R.layout.dialog_message, null)
        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        val textViewMessage: TextView = v.findViewById(R.id.textViewMessage)
        val buttonCancelar: MaterialButton = v.findViewById(R.id.buttonCancelar)
        val buttonAceptar: MaterialButton = v.findViewById(R.id.buttonAceptar)

        textViewTitle.text = String.format("%s", "Mensaje")
        textViewMessage.text = String.format("%s", "Si cuentas con datos se borraran !.\nDeseas Sincronizar ?.")

        buttonAceptar.setOnClickListener {
            SyncData().execute(operarioId)
            dialog.dismiss()
        }
        buttonCancelar.setOnClickListener {
            dialog.dismiss()
        }

        builder.setView(v)
        dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class SyncData : AsyncTask<Int, Void, String>() {
        private lateinit var builder: AlertDialog.Builder
        private lateinit var dialog: AlertDialog

        override fun onPreExecute() {
            super.onPreExecute()
            builder = AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.AppTheme))
            @SuppressLint("InflateParams") val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.dialog_alert, null)
            builder.setView(view)
            dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.show()
        }

        override fun doInBackground(vararg integers: Int?): String? {
            var result: String? = null
            val operarioId = integers[0]
            Realm.getDefaultInstance().use { realm ->
                result = migration(realm, operarioId!!)
                Thread.sleep(1000)
            }
            publishProgress()
            return result
        }

        @SuppressLint("RestrictedApi")
        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            val titulo = if (s === "Verificar si cuentas con Internet.") "Error" else "Mensaje"
            Util.dialogMensaje(this@MainActivity, titulo, s)
        }
    }

    private fun migration(realm: Realm, operarioId: Int): String? {

        val migrationImp: MigrationImplementation = MigrationOver(realm)
        var result: String? = null
        val mCall = migrationInterface.getMigration(operarioId, Util.getVersion(this@MainActivity))

        try {
            val response = mCall.execute()!!
            if (response.code() == 200) {
                val migracion: Migration? = response.body() as Migration
                if (migracion != null) {
                    migrationImp.deleteAll()
                    migrationImp.save(migracion)
                    startService()
                    result = migracion.mensaje
                }
            } else {
                val message = Gson().fromJson(response.errorBody()?.string(), MessageError::class.java)
                result = "Codigo :" + response.code() + "\nMensaje :" + message.ExceptionMessage
            }
        } catch (e: IOException) {
            result = e.message + "\nVerificar si cuentas con Internet."
        }

        return result
    }

    @SuppressLint("StaticFieldLeak")
    private inner class SyncObservadas : AsyncTask<Int, Void, String>() {

        private lateinit var builder: AlertDialog.Builder
        private lateinit var dialog: AlertDialog

        override fun onPreExecute() {
            super.onPreExecute()
            builder = AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.AppTheme))
            @SuppressLint("InflateParams") val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.dialog_alert, null)
            builder.setView(view)
            dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.show()
        }

        override fun doInBackground(vararg integers: Int?): String? {

            var result: String? = null
            val operarioId = integers[0]

            Realm.getDefaultInstance().use { realm ->
                result = migrationObservadas(realm, operarioId!!)
                Thread.sleep(1000)
            }

            publishProgress()
            return result
        }

        @SuppressLint("RestrictedApi")
        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            Util.updateNotificacionValid(this@MainActivity)
            val titulo = if (s === "Verificar si cuentas con Internet.") "Error" else "Mensaje"
            Util.dialogMensaje(this@MainActivity, titulo, s)
        }
    }

    private fun migrationObservadas(realm: Realm, operarioId: Int): String? {

        val sincronizarImp: SincronizarImplementation = SincronizarOver(realm)
        val migrationInterface: ApiServices = ConexionRetrofit.api.create(ApiServices::class.java)
        var result: String? = null
        val mCall = migrationInterface.getObservadas(operarioId)

        try {
            val response = mCall.execute()!!
            if (response.code() == 200) {
                val suministroLecturas: List<SuministroLectura>? = response.body() as List<SuministroLectura>
                if (suministroLecturas != null) {
                    sincronizarImp.saveLecturasEncontradas(suministroLecturas, 0)
                    result = "Lecturas Observadas actualizadas verificar"
                }
            } else {
                val message = Gson().fromJson(response.errorBody()?.string(), MessageError::class.java)
                result = "Codigo :" + response.code() + "\nMensaje :" + message.ExceptionMessage
            }
        } catch (e: IOException) {
            result = e.message + "\nVerificar si cuentas con Internet."
        }
        return result
    }

    private fun confirmLogOut() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.AppTheme))
        val dialog: AlertDialog

        builder.setTitle("Advertencia")
        builder.setMessage("Al cerrar Sesión estaras eliminando todo tus avances." + "\nEn caso que no lo tengas clic en aceptar.")
        builder.setPositiveButton("Aceptar") { dialogInterface, _ ->
            stopServices()
            load("Cerrando Sesión")
            logout()
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialogInterface, _ -> dialogInterface.dismiss() }
        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }


    private fun load(title: String) {
        builder = AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
                LayoutInflater.from(this@MainActivity).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        textViewTitle.text = title
        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun logout() {
        val a = Completable.fromAction {
            Realm.getDefaultInstance().use { realm ->
                val loginImp: LoginImplementation = LoginOver(realm)
                val migrationImp: MigrationImplementation = MigrationOver(realm)
                migrationImp.deleteAll()
                loginImp.delete()
            }
        }
        a.subscribeOn(Schedulers.io())
                .delay(2000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        if (dialog.isShowing) {
                            dialog.dismiss()
                        }
                        logOut()
                        exitProcess(0)
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Log.i("TAG", e.toString())
                    }

                })
    }

    private fun startService() {
        startService(Intent(this, EnableGpsService::class.java))
        startService(Intent(this, SendLocationService::class.java))
        startService(Intent(this, SendDataMovilService::class.java))
    }

    private fun stopServices() {
        stopService(Intent(this, EnableGpsService::class.java))
        stopService(Intent(this, SendLocationService::class.java))
        stopService(Intent(this, SendDataMovilService::class.java))
        stopService(Intent(this, DistanceService::class.java))
    }
}