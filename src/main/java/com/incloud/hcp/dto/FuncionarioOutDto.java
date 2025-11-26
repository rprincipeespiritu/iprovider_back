package com.incloud.hcp.dto;

public class FuncionarioOutDto {

    private Integer idProveedorFuncionario;
    private String apellidosNombres;
    private String cargo;
    private String ruc;
    private Integer proveedor;
    private String tipoDocumento;

    public FuncionarioOutDto(){
    }

    public Integer getIdProveedorFuncionario() {
        return idProveedorFuncionario;
    }

    public void setIdProveedorFuncionario(Integer idProveedorFuncionario) {
        this.idProveedorFuncionario = idProveedorFuncionario;
    }

    public String getApellidosNombres() {
        return apellidosNombres;
    }

    public void setApellidosNombres(String apellidosNombres) {
        this.apellidosNombres = apellidosNombres;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public Integer getProveedor() {
        return proveedor;
    }

    public void setProveedor(Integer proveedor) {
        this.proveedor = proveedor;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    @Override
    public String toString() {
        return "ProductoDto{" +
                "apellidosNombres='" + apellidosNombres + '\'' +
                ", cargo='" + cargo + '\'' +
                ", ruc='" + ruc + '\'' +
                ", proveedor='" + proveedor + '\'' +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                '}';
    }
}
