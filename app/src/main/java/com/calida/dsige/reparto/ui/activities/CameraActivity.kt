package com.calida.dsige.reparto.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.calida.dsige.reparto.R
import com.calida.dsige.reparto.ui.fragments.CameraFragment

class CameraActivity : AppCompatActivity() {

    var repartoId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        val b = intent.extras
        if (b != null) {
            savedInstanceState ?: supportFragmentManager.beginTransaction()
                    .replace(
                            R.id.container,
                            CameraFragment.newInstance(
                                    b.getInt("repartoId")
                            )
                    )
                    .commit()
        }
    }
}