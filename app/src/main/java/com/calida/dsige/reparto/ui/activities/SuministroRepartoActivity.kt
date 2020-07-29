package com.calida.dsige.reparto.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.*
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calida.dsige.reparto.data.local.Reparto
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.data.viewModel.EnvioViewModel
import com.calida.dsige.reparto.helper.Util
import com.calida.dsige.reparto.ui.adapters.SuministroRepartoAdapter
import com.calida.dsige.reparto.ui.listeners.OnItemClickListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_suministro.*

class SuministroRepartoActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabMap -> {
                startActivity(Intent(this, PendingLocationMapsActivity::class.java).putExtra("estado", estado))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.action_menu).isVisible = false
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                val searchView = item.actionView as SearchView
                search(searchView)
                return true
            }
            R.id.action_menu -> {
                startActivity(Intent(this@SuministroRepartoActivity, MainActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var suministroRepartoAdapter: SuministroRepartoAdapter
    lateinit var envioViewModel: EnvioViewModel

    var estado: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suministro)
        envioViewModel = ViewModelProvider(this).get(EnvioViewModel::class.java)
        envioViewModel.initialRealm()
        val bundle = intent.extras
        if (bundle != null) {
            estado = bundle.getInt("estado")
            bindToolbar(bundle.getString("nombre")!!)
            bindUI()
        }
    }

    private fun bindToolbar(nombre: String) {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Lista de $nombre"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this@SuministroRepartoActivity, StartActivity::class.java))
            finish()
        }
    }

    private fun bindUI() {
        fabMap.setOnClickListener(this)
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this@SuministroRepartoActivity)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager

        suministroRepartoAdapter = SuministroRepartoAdapter(object : OnItemClickListener.RepartoListener {
            override fun onItemClick(r: Reparto, v: View, position: Int) {
                when (v.id) {
                    R.id.imageViewMap -> {
                        if (r.latitud.isNotEmpty() || r.longitud.isNotEmpty()) {
                            startActivity(Intent(this@SuministroRepartoActivity, MapsActivity::class.java)
                                    .putExtra("latitud", r.latitud)
                                    .putExtra("longitud", r.longitud)
                                    .putExtra("title", r.Suministro_Numero_reparto))
                        } else {
                            Util.toastMensaje(this@SuministroRepartoActivity, "Este suministro no cuenta con coordenadas")
                        }
                    }
                    else -> popUpSelect(r, v)
                }
            }
        })
        recyclerView.adapter = suministroRepartoAdapter

        envioViewModel.getSuministroRepartoLiveData().observe(this, Observer{ r ->
            if (r != null) {
                suministroRepartoAdapter.addItems(r)
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(Intent(this@SuministroRepartoActivity, StartActivity::class.java))
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun search(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                suministroRepartoAdapter.getFilter().filter(newText)
                return true
            }
        })
    }

    private fun popUpSelect(r: Reparto, view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menu.add(0, 1, 0, getText(R.string.title1))
        popupMenu.menu.add(1, 2, 1, getText(R.string.title2))
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> startActivity(Intent(this, RecepcionActivity::class.java).putExtra("repartoId", r.id_Reparto))
                2 -> startActivity(Intent(this, RecepcionVariosActivity::class.java).putExtra("repartoId", r.id_Reparto))
            }
            false
        }
        popupMenu.show()
    }
}