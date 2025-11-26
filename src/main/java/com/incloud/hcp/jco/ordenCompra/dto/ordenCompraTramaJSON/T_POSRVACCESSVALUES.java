package com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON;

import java.io.Serializable;

public class T_POSRVACCESSVALUES implements Serializable {

    private String T_POSRVACCESSVALUES;
    private String PCKG_NO;
    private String LINE_NO;
    private String SERNO_LINE;
    private String PERCENTAGE;
    private String SERIAL_NO;
    private String QUANTITY;
    private String NET_VALUE;

    public T_POSRVACCESSVALUES() {
    }

    public String getT_POSRVACCESSVALUES() {
        return T_POSRVACCESSVALUES;
    }

    public void setT_POSRVACCESSVALUES(String t_POSRVACCESSVALUES) {
        T_POSRVACCESSVALUES = t_POSRVACCESSVALUES;
    }

    public String getPCKG_NO() {
        return PCKG_NO;
    }

    public void setPCKG_NO(String PCKG_NO) {
        this.PCKG_NO = PCKG_NO;
    }

    public String getLINE_NO() {
        return LINE_NO;
    }

    public void setLINE_NO(String LINE_NO) {
        this.LINE_NO = LINE_NO;
    }

    public String getSERNO_LINE() {
        return SERNO_LINE;
    }

    public void setSERNO_LINE(String SERNO_LINE) {
        this.SERNO_LINE = SERNO_LINE;
    }

    public String getPERCENTAGE() {
        return PERCENTAGE;
    }

    public void setPERCENTAGE(String PERCENTAGE) {
        this.PERCENTAGE = PERCENTAGE;
    }

    public String getSERIAL_NO() {
        return SERIAL_NO;
    }

    public void setSERIAL_NO(String SERIAL_NO) {
        this.SERIAL_NO = SERIAL_NO;
    }

    public String getQUANTITY() {
        return QUANTITY;
    }

    public void setQUANTITY(String QUANTITY) {
        this.QUANTITY = QUANTITY;
    }

    public String getNET_VALUE() {
        return NET_VALUE;
    }

    public void setNET_VALUE(String NET_VALUE) {
        this.NET_VALUE = NET_VALUE;
    }
}
