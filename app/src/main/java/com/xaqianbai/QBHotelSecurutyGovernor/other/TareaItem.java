package com.xaqianbai.QBHotelSecurutyGovernor.other;

public class TareaItem {
    public int FiLeft = 0;
    public int FiRight = 0;
    public int FiY = 0;

    public TareaItem(int iLeft, int iRight, int iY) {
        FiLeft = iLeft;
        FiRight = iRight;
        FiY = iY;
    }

    public boolean isLink(int iLeft, int iRight, int iY) {
        if (Math.abs(iY - FiY) < 2)
            if (iLeft <= FiRight && iRight >= FiLeft) return true;
        return false;
    }


}
