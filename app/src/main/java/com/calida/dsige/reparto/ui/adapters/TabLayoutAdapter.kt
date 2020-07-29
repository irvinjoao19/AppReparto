package com.calida.dsige.reparto.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.calida.dsige.reparto.ui.fragments.*

abstract class TabLayoutAdapter {

    class TabLayoutCheckListAdapter(fm: FragmentManager, private val numberOfTabs: Int, var inspeccionId: Int, var usuarioId: Int)
        : FragmentStatePagerAdapter(fm, numberOfTabs) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> GeneralFragment.newInstance(inspeccionId, usuarioId)
                1 -> CheckListFragment.newInstance(inspeccionId, usuarioId)
                2 -> ObservationFragment.newInstance(inspeccionId,usuarioId)
                else -> Fragment()
            }
        }

        override fun getCount(): Int {
            return numberOfTabs
        }
    }

    class TabLayoutClient(fm: FragmentManager, private val numberOfTabs: Int, val id: Int)
        : FragmentStatePagerAdapter(fm, numberOfTabs) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> GeneralClientFragment.newInstance(id)
                1 -> FileFragment.newInstance(id)
                else -> Fragment()
            }
        }

        override fun getCount(): Int {
            return numberOfTabs
        }
    }
}