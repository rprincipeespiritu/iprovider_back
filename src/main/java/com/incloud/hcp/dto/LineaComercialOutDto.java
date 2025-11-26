package com.incloud.hcp.dto;

public class LineaComercialOutDto {

    private Integer idProveedorLineaComercial;
    private Integer familia;
    private Integer lineaComercial;
    private Integer proveedor;
    private Integer subFamilia;
    private String otrosLineaComercial;

    public Integer getIdProveedorLineaComercial() {
        return idProveedorLineaComercial;
    }

    public void setIdProveedorLineaComercial(Integer idProveedorLineaComercial) {
        this.idProveedorLineaComercial = idProveedorLineaComercial;
    }

    public Integer getFamilia() {
        return familia;
    }

    public void setFamilia(Integer familia) {
        this.familia = familia;
    }

    public Integer getLineaComercial() {
        return lineaComercial;
    }

    public void setLineaComercial(Integer lineaComercial) {
        this.lineaComercial = lineaComercial;
    }

    public Integer getProveedor() {
        return proveedor;
    }

    public void setProveedor(Integer proveedor) {
        this.proveedor = proveedor;
    }

    public Integer getSubFamilia() {
        return subFamilia;
    }

    public void setSubFamilia(Integer subFamilia) {
        this.subFamilia = subFamilia;
    }

    public String getOtrosLineaComercial() {
        return otrosLineaComercial;
    }

    public void setOtrosLineaComercial(String otrosLineaComercial) {
        this.otrosLineaComercial = otrosLineaComercial;
    }

    @Override
    public String toString() {
        return "LineaComercialDto{" +
                "idProveedorLineaComercial" + idProveedorLineaComercial +
                ", idFamilia='" + familia + '\'' +
                ", idLineaComercial=" + lineaComercial +
                ", idProveedor='" + proveedor + '\'' +
                ", idSubFamilia=" + subFamilia +
                ", otraLinea='" + otrosLineaComercial + '\'' +
                '}';
    }
}
