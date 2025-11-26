package com.incloud.hcp.ws.registrodocumentos.bean;

public class DocumentoRegistroListaBody {

    private int iD_Amarre;

    public int getiD_Amarre() {
        return iD_Amarre;
    }

    public void setiD_Amarre(int iD_Amarre) {
        this.iD_Amarre = iD_Amarre;
    }

    @Override
    public String toString() {
        return "DocumentoRegistroListaBody{" +
                "iD_Amarre=" + iD_Amarre +
                '}';
    }
}
