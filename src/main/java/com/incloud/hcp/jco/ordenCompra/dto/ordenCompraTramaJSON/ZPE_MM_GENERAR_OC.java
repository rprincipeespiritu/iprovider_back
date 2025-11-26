package com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON;

import java.io.Serializable;
import java.util.List;

public class ZPE_MM_GENERAR_OC implements Serializable {

    private I_POHEADER I_POHEADER;
    private I_POHEADERX I_POHEADERX;
    private List <T_POACCOUNT> T_POACCOUNT;
    private String T_POACCOUNTX;
    private List<T_POITEM> T_POITEM;
    private String T_POITEMX;
    private String T_POSCHEDULE;
    private String T_POSCHEDULEX;
    private String T_POSERVICES;
    private List <T_POSRVACCESSVALUES> T_POSRVACCESSVALUES;
    private List <T_RETURN> T_RETURN;

    public com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON.I_POHEADER getI_POHEADER() {
        return I_POHEADER;
    }

    public void setI_POHEADER(com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON.I_POHEADER i_POHEADER) {
        I_POHEADER = i_POHEADER;
    }

    public com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON.I_POHEADERX getI_POHEADERX() {
        return I_POHEADERX;
    }

    public void setI_POHEADERX(com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON.I_POHEADERX i_POHEADERX) {
        I_POHEADERX = i_POHEADERX;
    }

    public List<com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON.T_POACCOUNT> getT_POACCOUNT() {
        return T_POACCOUNT;
    }

    public void setT_POACCOUNT(List<com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON.T_POACCOUNT> t_POACCOUNT) {
        T_POACCOUNT = t_POACCOUNT;
    }

    public String getT_POACCOUNTX() {
        return T_POACCOUNTX;
    }

    public void setT_POACCOUNTX(String t_POACCOUNTX) {
        T_POACCOUNTX = t_POACCOUNTX;
    }

    public List<com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON.T_POITEM> getT_POITEM() {
        return T_POITEM;
    }

    public void setT_POITEM(List<com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON.T_POITEM> t_POITEM) {
        T_POITEM = t_POITEM;
    }

    public String getT_POITEMX() {
        return T_POITEMX;
    }

    public void setT_POITEMX(String t_POITEMX) {
        T_POITEMX = t_POITEMX;
    }

    public String getT_POSCHEDULE() {
        return T_POSCHEDULE;
    }

    public void setT_POSCHEDULE(String t_POSCHEDULE) {
        T_POSCHEDULE = t_POSCHEDULE;
    }

    public String getT_POSCHEDULEX() {
        return T_POSCHEDULEX;
    }

    public void setT_POSCHEDULEX(String t_POSCHEDULEX) {
        T_POSCHEDULEX = t_POSCHEDULEX;
    }

    public String getT_POSERVICES() {
        return T_POSERVICES;
    }

    public void setT_POSERVICES(String t_POSERVICES) {
        T_POSERVICES = t_POSERVICES;
    }

    public List<com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON.T_POSRVACCESSVALUES> getT_POSRVACCESSVALUES() {
        return T_POSRVACCESSVALUES;
    }

    public void setT_POSRVACCESSVALUES(List<com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON.T_POSRVACCESSVALUES> t_POSRVACCESSVALUES) {
        T_POSRVACCESSVALUES = t_POSRVACCESSVALUES;
    }

    public List<com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON.T_RETURN> getT_RETURN() {
        return T_RETURN;
    }

    public void setT_RETURN(List<com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON.T_RETURN> t_RETURN) {
        T_RETURN = t_RETURN;
    }
}
