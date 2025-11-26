package com.incloud.hcp.ws.registrarcompra.bean;

import java.io.Serializable;

public class CompraRegistroBodyResponse implements Serializable {

    private int oP;
    private int iD_Amarre;

    public int getoP() {
        return oP;
    }

    public void setoP(int oP) {
        this.oP = oP;
    }

    public int getiD_Amarre() {
        return iD_Amarre;
    }

    public void setiD_Amarre(int iD_Amarre) {
        this.iD_Amarre = iD_Amarre;
    }

    @Override
    public String toString() {
        return "CompraRegistroBodyResponse{" +
                "oP=" + oP +
                ", iD_Amarre=" + iD_Amarre +
                '}';
    }
}
