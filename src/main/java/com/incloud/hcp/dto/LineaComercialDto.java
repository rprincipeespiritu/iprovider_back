package com.incloud.hcp.dto;

/**
 * Created by Administrador on 30/08/2017.
 */
public class LineaComercialDto {
    private Integer idProveedorLineaComercial;
    private Integer idLinea;
    private String linea;
    private Integer idFamilia;
    private String familia;
    private Integer idSubFamilia;
    private String subFamilia;
    private String otraLinea;
    private String indBien;
    private Integer idPadre;
    private String lineaPadre;

    public Integer getIdProveedorLineaComercial() {
        return idProveedorLineaComercial;
    }

    public void setIdProveedorLineaComercial(Integer idProveedorLineaComercial) {
        this.idProveedorLineaComercial = idProveedorLineaComercial;
    }

    public Integer getIdLinea() {
        return idLinea;
    }

    public void setIdLinea(Integer idLinea) {
        this.idLinea = idLinea;
    }

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public Integer getIdFamilia() {
        return idFamilia;
    }

    public void setIdFamilia(Integer idFamilia) {
        this.idFamilia = idFamilia;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public Integer getIdSubFamilia() {
        return idSubFamilia;
    }

    public void setIdSubFamilia(Integer idSubFamilia) {
        this.idSubFamilia = idSubFamilia;
    }

    public String getSubFamilia() {
        return subFamilia;
    }

    public void setSubFamilia(String subFamilia) {
        this.subFamilia = subFamilia;
    }

    public String getOtraLinea() {
        return otraLinea;
    }

    public void setOtraLinea(String otraLinea) {
        this.otraLinea = otraLinea;
    }

    public String getIndBien() {
        return indBien;
    }

    public void setIndBien(String indBien) {
        this.indBien = indBien;
    }

    public Integer getIdPadre() {
        return idPadre;
    }

    public String getLineaPadre() {
        return lineaPadre;
    }

    public void setLineaPadre(String lineaPadre) {
        this.lineaPadre = lineaPadre;
    }

    public void setIdPadre(Integer idPadre) {
        this.idPadre = idPadre;
    }

    @Override
    public String toString() {
        return "LineaComercialDto{" +
                "idProveedorLineaComercial" + idProveedorLineaComercial +
                "idLinea=" + idLinea + '\'' +
                ", linea='" + linea + '\'' +
                ", idFamilia=" + idFamilia +
                ", familia='" + familia + '\'' +
                ", idSubFamilia=" + idSubFamilia +
                ", subFamilia='" + subFamilia + '\'' +
                ", otraLinea='" + otraLinea + '\'' +
                ", indBien='" + indBien + '\'' +
                ", idPadre='" + idPadre + '\'' +
                ", lineaPadre='" + lineaPadre + '\'' +
                '}';
    }
}
