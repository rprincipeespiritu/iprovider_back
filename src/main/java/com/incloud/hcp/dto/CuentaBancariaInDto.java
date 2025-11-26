package com.incloud.hcp.dto;

public class CuentaBancariaInDto {

    private Integer idCuenta;
    private String archivoId;
    private String archivoNombre;
    private String archivoTipo;
    private String claveControlBanco;
    private String contacto;
    private String indCuentaDetraccion;
    private String numeroCuenta;
    private String numeroCuentaCci;
    private String rutaAdjunto;
    private Integer idBanco;
    private Integer idMoneda;
    private Integer idProveedor;

    public CuentaBancariaInDto() {
    }

    public Integer getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Integer idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getArchivoId() {
        return archivoId;
    }

    public void setArchivoId(String archivoId) {
        this.archivoId = archivoId;
    }

    public String getArchivoNombre() {
        return archivoNombre;
    }

    public void setArchivoNombre(String archivoNombre) {
        this.archivoNombre = archivoNombre;
    }

    public String getArchivoTipo() {
        return archivoTipo;
    }

    public void setArchivoTipo(String archivoTipo) {
        this.archivoTipo = archivoTipo;
    }

    public String getClaveControlBanco() {
        return claveControlBanco;
    }

    public void setClaveControlBanco(String claveControlBanco) {
        this.claveControlBanco = claveControlBanco;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getIndCuentaDetraccion() {
        return indCuentaDetraccion;
    }

    public void setIndCuentaDetraccion(String indCuentaDetraccion) {
        this.indCuentaDetraccion = indCuentaDetraccion;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getNumeroCuentaCci() {
        return numeroCuentaCci;
    }

    public void setNumeroCuentaCci(String numeroCuentaCci) {
        this.numeroCuentaCci = numeroCuentaCci;
    }

    public String getRutaAdjunto() {
        return rutaAdjunto;
    }

    public void setRutaAdjunto(String rutaAdjunto) {
        this.rutaAdjunto = rutaAdjunto;
    }

    public Integer getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(Integer idBanco) {
        this.idBanco = idBanco;
    }

    public Integer getIdMoneda() {
        return idMoneda;
    }

    public void setIdMoneda(Integer idMoneda) {
        this.idMoneda = idMoneda;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    @Override
    public String toString() {
        return "CuentaBancariaInDto{" +
                "archivoId='" + archivoId + '\'' +
                ", archivoNombre='" + archivoNombre + '\'' +
                ", archivoTipo='" + archivoTipo + '\'' +
                ", claveControlBanco='" + claveControlBanco + '\'' +
                ", contacto='" + contacto + '\'' +
                ", indCuentaDetraccion='" + indCuentaDetraccion + '\'' +
                ", numeroCuenta='" + numeroCuenta + '\'' +
                ", numeroCuentaCci='" + numeroCuentaCci + '\'' +
                ", rutaAdjunto='" + rutaAdjunto + '\'' +
                ", idBanco=" + idBanco +
                ", idMoneda=" + idMoneda +
                ", idProveedor=" + idProveedor +
                '}';
    }
}
