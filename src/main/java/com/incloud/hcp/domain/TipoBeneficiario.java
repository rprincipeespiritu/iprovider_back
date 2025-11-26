package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="tipo_beneficiario")
public class TipoBeneficiario extends BaseDomain implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id_tipo_beneficiario", unique = true, nullable = true)
    @GeneratedValue(generator = "tipo_beneficiario_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name="tipo_beneficiario_id_seq", sequenceName = "tipo_beneficiario_id_seq", allocationSize = 1)
    private Integer idTipoBeneficiario;

    @Column(name="codigo_tipo_cuenta_beneficiario", nullable = false, length = 3)
    private String codigoTipoCuentaBeneficiario;

    @Column(name="descripcion_beneficiario", nullable = false, length = 30)
    private String descripcion;

    public TipoBeneficiario(){
    }

    public Integer getIdTipoBeneficiario(){
        return this.idTipoBeneficiario;
    }

    public void setIdTipoBeneficiario(Integer idTipoBeneficiario){
        this.idTipoBeneficiario = idTipoBeneficiario;
    }

    public String getCodigoTipoCuentaBeneficiario(){
        return this.codigoTipoCuentaBeneficiario;
    }

    public void setCodigoTipoCuentaBeneficiario(String codigoTipoCuentaBeneficiario){
        this.codigoTipoCuentaBeneficiario = codigoTipoCuentaBeneficiario;
    }

    public String getDescripcion(){
        return this.descripcion;
    }

    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }

    @Override
    public String toString(){
        return "TipoBeneficiario{" +
                "idTipoCuentaBeneficiario=" + idTipoBeneficiario +
                ", codigoTipoCuentaBeneficiario='" + codigoTipoCuentaBeneficiario + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
