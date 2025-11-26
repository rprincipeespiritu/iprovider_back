package com.incloud.hcp.dto;

public class NonExistedBankRequest {

    private String nombreBanco;
    private String direccionBanco;
    private String ciudadBanco;
    private String paisBanco;
    private String swift;
    private String nombreProveedor;

    private String mailValorCc;

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public String getDireccionBanco() {
        return direccionBanco;
    }

    public void setDireccionBanco(String direccionBanco) {
        this.direccionBanco = direccionBanco;
    }

    public String getCiudadBanco() {
        return ciudadBanco;
    }

    public void setCiudadBanco(String ciudadBanco) {
        this.ciudadBanco = ciudadBanco;
    }

    public String getPaisBanco() {
        return paisBanco;
    }

    public void setPaisBanco(String paisBanco) {
        this.paisBanco = paisBanco;
    }

    public String getSwift() {
        return swift;
    }

    public void setSwift(String swift) {
        this.swift = swift;
    }

    public String getMailValorCc() {
        return mailValorCc;
    }

    public void setMailValorCc(String mailValorCc) {
        this.mailValorCc = mailValorCc;
    }
}
