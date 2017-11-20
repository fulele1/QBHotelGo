package com.xaqb.unlock.other;

import java.util.ArrayList;


public class TareaSet {
    protected ArrayList<Tarea> FoItem = new ArrayList<Tarea>();

    public TareaSet() {

    }

    public void add(int iLeft, int iRight, int iY, boolean bOpen) {
        Tarea oItem, oFrom;
        int i;
        ArrayList<Tarea> oFind = new ArrayList<Tarea>();
        for (i = 0; i < FoItem.size(); i++) {
            oItem = FoItem.get(i);
            if (oItem.test(iLeft, iRight, iY)) oFind.add(oItem);
        }
        if (oFind.size() > 0) {
            oItem = oFind.get(0);
            for (i = 1; i < oFind.size(); i++) {
                oFrom = oFind.get(i);
                oItem.merge(oFrom);
                FoItem.remove(oFrom);
            }
            oItem.add(iLeft, iRight, iY, bOpen);
        } else FoItem.add(new Tarea(iLeft, iRight, iY, bOpen));
    }

    public int count() {
        return FoItem.size();
    }

    public Tarea get(int i) {
        if (i >= 0 && i < FoItem.size())
            return FoItem.get(i);
        return null;
    }

    public void Over() {
        int i;
        Tarea oItem;
        i = 0;
        while (i < FoItem.size()) {
            oItem = FoItem.get(i);
            if (oItem.isOpen())
                FoItem.remove(i);
            else i++;
        }
    }
}
