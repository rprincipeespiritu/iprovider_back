package com.incloud.hcp.dto;

public class InstalacionInDto {

    private Integer idProveedorInstalacion;
    private String codigoTipoInstalacion;
    private String direccion;
    private String telefono;
    private Integer proveedor;

    public InstalacionInDto(){
    }

    public Integer getIdProveedorInstalacion() {
        return idProveedorInstalacion;
    }

    public void setIdProveedorInstalacion(Integer idProveedorInstalacion) {
        this.idProveedorInstalacion = idProveedorInstalacion;
    }

    public String getCodigoTipoInstalacion() {
        return codigoTipoInstalacion;
    }

    public void setCodigoTipoInstalacion(String codigoTipoInstalacion) {
        this.codigoTipoInstalacion = codigoTipoInstalacion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getProveedor() {
        return proveedor;
    }

    public void setProveedor(Integer proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public String toString() {
        return "ProductoDto{" +
                "codigoTipoInstalacion='" + codigoTipoInstalacion + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", proveedor='" + proveedor + '\'' +
                '}';
    }
}
