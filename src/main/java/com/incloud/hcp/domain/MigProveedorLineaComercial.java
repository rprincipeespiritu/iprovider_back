package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="mig_proveedor_linea_comercial")
public class MigProveedorLineaComercial extends BaseDomain implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id")
    private Integer id;

    @Column(name="id_familia")
    private Integer idFamilia;

    @Column(name="linea_comercial")
    private String lineaComercial;

    @Column(name="ruc_proveedor")
    private String rucProveedor;

    @Column(name="codigo_acreedor_sap")
    private String codigoAcreedorSap;

    @Column(name="IND_MIGRACION_OK",length = 1)
    private String indMigracionOK;

    @Column(name="IND_ERROR_RUC",length = 1)
    private String indErrorRuc;

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

    public Integer getIdFamilia() {
        return idFamilia;
    }

    public void setIdFamilia(Integer idFamilia) {
        this.idFamilia = idFamilia;
    }

    public String getLineaComercial() {
        return lineaComercial;
    }

    public void setLineaComercial(String lineaComercial) {
        this.lineaComercial = lineaComercial;
    }

    public String getRucProveedor() {
        return rucProveedor;
    }

    public void setRucProveedor(String rucProveedor) {
        this.rucProveedor = rucProveedor;
    }

    public String getCodigoAcreedorSap() {
        return codigoAcreedorSap;
    }

    public void setCodigoAcreedorSap(String codigoAcreedorSap) {
        this.codigoAcreedorSap = codigoAcreedorSap;
    }

    public String getIndMigracionOK() {
        return indMigracionOK;
    }

    public void setIndMigracionOK(String indMigracionOK) {
        this.indMigracionOK = indMigracionOK;
    }

    public String getIndErrorRuc() {
        return indErrorRuc;
    }

    public void setIndErrorRuc(String indErrorRuc) {
        this.indErrorRuc = indErrorRuc;
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
        return "MigProveedorLineaComercial{" +
                "id=" + id +
                ", idFamilia=" + idFamilia +
                ", lineaComercial='" + lineaComercial + '\'' +
                ", rucProveedor='" + rucProveedor + '\'' +
                ", codigoAcreedorSap='" + codigoAcreedorSap + '\'' +
                ", indMigracionOK='" + indMigracionOK + '\'' +
                ", indErrorRuc='" + indErrorRuc + '\'' +
                ", error='" + error + '\'' +
                ", idProveedor=" + idProveedor +
                '}';
    }
}
