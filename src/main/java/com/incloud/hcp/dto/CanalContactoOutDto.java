package com.incloud.hcp.dto;

public class CanalContactoOutDto {

    private Integer idProveedorCanal;
    private String areaEmpresa;
    private String contacto;
    private String direccion;
    private String email;
    private String telefono;
    private Integer pais;
    private Integer proveedor;
    private Integer provincia;
    private Integer region;

    public Integer getIdProveedorCanal() {
        return idProveedorCanal;
    }

    public void setIdProveedorCanal(Integer idProveedorCanal) {
        this.idProveedorCanal = idProveedorCanal;
    }

    public String getAreaEmpresa() {
        return areaEmpresa;
    }

    public void setAreaEmpresa(String areaEmpresa) {
        this.areaEmpresa = areaEmpresa;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getPais() {
        return pais;
    }

    public void setPais(Integer pais) {
        this.pais = pais;
    }

    public Integer getProveedor() {
        return proveedor;
    }

    public void setProveedor(Integer proveedor) {
        this.proveedor = proveedor;
    }

    public Integer getProvincia() {
        return provincia;
    }

    public void setProvincia(Integer provincia) {
        this.provincia = provincia;
    }

    public Integer getRegion() {
        return region;
    }

    public void setRegion(Integer region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "LineaComercialDto{" +
                ", areaEmpresa='" + areaEmpresa + '\'' +
                ", contacto=" + contacto +
                ", direccion='" + direccion + '\'' +
                ", email=" + email +
                ", telefono='" + telefono + '\'' +
                ", pais='" + pais + '\'' +
                ", proveedor=" + proveedor +
                ", provincia='" + provincia + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}
