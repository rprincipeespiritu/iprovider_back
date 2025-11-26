package com.incloud.hcp.service._framework.reportes.enums;

public enum TipoReporteJasperEnum {

    PDF("word"),
    PDF_DESCARGAR("PDF_DESCARGAR"),
    DOCX("DOCX");

    private final String estado;

    TipoReporteJasperEnum(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }
}
