package com.incloud.hcp.ws.registrodocumentos.bean;

import java.io.Serializable;
import java.util.List;

public class DocumentoRegistroBodyResponse implements Serializable {

    private int oP;
    private List<DocumentoRegistroListaBody> lista;

    public int getoP() {
        return oP;
    }

    public void setoP(int oP) {
        this.oP = oP;
    }

    public List<DocumentoRegistroListaBody> getLista() {
        return lista;
    }

    public void setLista(List<DocumentoRegistroListaBody> lista) {
        this.lista = lista;
    }

    @Override
    public String toString() {
        return "DocumentoRegistroBodyResponse{" +
                "oP=" + oP +
                ", lista=" + lista +
                '}';
    }
}
