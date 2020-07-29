package com.calida.dsige.reparto.helper;

import com.calida.dsige.reparto.data.local.DetalleGrupo;
import java.util.List;

public class SpinnerSelection {

    public static int getSpinnerValueDetalleGrupo(List<DetalleGrupo> array, int text) {
        int index = 0;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getID_DetalleGrupo() == text) {
                index = i;
                break;
            }
        }
        return index;
    }

}
