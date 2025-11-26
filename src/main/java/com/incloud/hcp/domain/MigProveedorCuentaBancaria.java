package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="mig_proveedor_cuenta_bancaria")
public class MigProveedorCuentaBancaria extends BaseDomain implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id")
    private Integer id;

    @Column(name="clave_control_banco")
    private String claveControlBanco;

    @Column(name="contacto")
    private String contacto;

    @Column(name="ind_cuenta_detraccion")
    private String indCuentaDetraccion;

    @Column(name="numero_cuenta")
    private String numeroCuenta;

    @Column(name="numero_cuenta_cci")
    private String numeroCuentaCci;

    @Column(name="descripcion_banco")
    private String descripcionBanco;

    @Column(name="id_moneda")
    private String idMoneda;

    @Column(name="ruc_proveedor")
    private String rucProveedor;

    @Column(name="pais_banco")
    private String paisBanco;

    @Column(name="codigo_acreedor_sap")
    private String codigoAcreedorSap;

    @Column(name="error",length = 1000)
    private String error;

    @Column(name="idProveedor")
    private Integer idProveedor;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDescripcionBanco() {
        return descripcionBanco;
    }

    public void setDescripcionBanco(String descripcionBanco) {
        this.descripcionBanco = descripcionBanco;
    }

    public String getIdMoneda() {
        return idMoneda;
    }

    public void setIdMoneda(String idMoneda) {
        this.idMoneda = idMoneda;
    }

    public String getRucProveedor() {
        return rucProveedor;
    }

    public void setRucProveedor(String rucProveedor) {
        this.rucProveedor = rucProveedor;
    }

    public String getPaisBanco() {
        return paisBanco;
    }

    public void setPaisBanco(String paisBanco) {
        this.paisBanco = paisBanco;
    }

    public String getCodigoAcreedorSap() {
        return codigoAcreedorSap;
    }

    public void setCodigoAcreedorSap(String codigoAcreedorSap) {
        this.codigoAcreedorSap = codigoAcreedorSap;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    @Override
    public String toString() {
        return "MigProveedorCuentaBancaria{" +
                "id=" + id +
                ", claveControlBanco='" + claveControlBanco + '\'' +
                ", contacto='" + contacto + '\'' +
                ", indCuentaDetraccion='" + indCuentaDetraccion + '\'' +
                ", numeroCuenta='" + numeroCuenta + '\'' +
                ", numeroCuentaCci='" + numeroCuentaCci + '\'' +
                ", descripcionBanco='" + descripcionBanco + '\'' +
                ", idMoneda='" + idMoneda + '\'' +
                ", rucProveedor='" + rucProveedor + '\'' +
                ", paisBanco='" + paisBanco + '\'' +
                ", codigoAcreedorSap='" + codigoAcreedorSap + '\'' +
                ", error='" + error + '\'' +
                ", idProveedor=" + idProveedor +
                '}';
    }
}
