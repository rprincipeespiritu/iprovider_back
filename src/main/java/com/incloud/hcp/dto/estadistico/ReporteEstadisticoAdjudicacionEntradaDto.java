package com.incloud.hcp.dto.estadistico;

public class ReporteEstadisticoAdjudicacionEntradaDto {

    private Integer anno;
    private String ruc;
    private String grupoCompra;  // mizalo

    public Integer getAnno() {
        return anno;
    }

    public void setAnno(Integer anno) {
        this.anno = anno;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getGrupoCompra() {
        return grupoCompra;
    }

    public void setGrupoCompra(String grupoCompra) {
        this.grupoCompra = grupoCompra;
    }

    @Override
    public String toString() {
        return "ReporteEstadisticoAdjudicacionEntradaDto{" +
                "anno=" + anno +
                ", ruc='" + ruc + '\'' +
                ", grupoCompra='" + grupoCompra + '\'' +  // mizalo
                '}';
    }
}
