package com.incloud.hcp.dto.estadistico;

public class ReporteEstadisticoAdjudicacionDto {

    private Integer anno;
    private Integer mes;
    private String descripcionMes;
    // inicio mizalo
    private String ruc;
    private String razonSocial;
    // fin mizalo
    private Integer totalAdjudicados;
    private Integer totalParticipacion;

    public Integer getAnno() {
        return anno;
    }

    public void setAnno(Integer anno) {
        this.anno = anno;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public String getDescripcionMes() {
        return descripcionMes;
    }

    public void setDescripcionMes(String descripcionMes) {
        this.descripcionMes = descripcionMes;
    }

    public Integer getTotalAdjudicados() {
        return totalAdjudicados;
    }

    public void setTotalAdjudicados(Integer totalAdjudicados) {
        this.totalAdjudicados = totalAdjudicados;
    }

    public Integer getTotalParticipacion() {
        return totalParticipacion;
    }

    public void setTotalParticipacion(Integer totalParticipacion) {
        this.totalParticipacion = totalParticipacion;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    @Override
    public String toString() {

        return "ReporteEstadisticoAdjudicacionDto{" +
                "anno=" + anno +
                ", mes=" + mes +
                ", descripcionMes='" + descripcionMes + '\'' +
                ", ruc='" + ruc + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                ", totalAdjudicados=" + totalAdjudicados +
                ", totalParticipacion=" + totalParticipacion +
                '}';
    }
}
