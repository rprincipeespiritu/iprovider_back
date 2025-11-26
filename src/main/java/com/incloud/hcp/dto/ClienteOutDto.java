package com.incloud.hcp.dto;

public class ClienteOutDto {

    private Integer idProveedorCliente;
    private String codigoTipoProveedorCliente;
    private String email;
    private String personaContacto;
    private Double porcParticipacion;
    private String razonSocial;
    private String rubro;
    private String ruc;
    private String telefono;
    private Integer proveedor;

    public ClienteOutDto() {
    }

    public Integer getIdProveedorCliente() {
        return idProveedorCliente;
    }

    public void setIdProveedorCliente(Integer idProveedorCliente) {
        this.idProveedorCliente = idProveedorCliente;
    }

    public String getCodigoTipoProveedorCliente() {
        return codigoTipoProveedorCliente;
    }

    public void setCodigoTipoProveedorCliente(String codigoTipoProveedorCliente) {
        this.codigoTipoProveedorCliente = codigoTipoProveedorCliente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonaContacto() {
        return personaContacto;
    }

    public void setPersonaContacto(String personaContacto) {
        this.personaContacto = personaContacto;
    }

    public Double getPorcParticipacion() {
        return porcParticipacion;
    }

    public void setPorcParticipacion(Double porcParticipacion) {
        this.porcParticipacion = porcParticipacion;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getRubro() {
        return rubro;
    }

    public void setRubro(String rubro) {
        this.rubro = rubro;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
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
        return "ClienteOutDto{" +
                "codigoTipoProveedorCliente='" + codigoTipoProveedorCliente + '\'' +
                ", email='" + email + '\'' +
                ", personaContacto='" + personaContacto + '\'' +
                ", porcParticipacion=" + porcParticipacion +
                ", razonSocial='" + razonSocial + '\'' +
                ", rubro='" + rubro + '\'' +
                ", ruc='" + ruc + '\'' +
                ", telefono='" + telefono + '\'' +
                ", proveedor=" + proveedor +
                '}';
    }
}
