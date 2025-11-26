package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "CONSTANCIA_DETRACCION_DETALLE")
@Getter
@Setter
@NoArgsConstructor
public class ConstanciaDetraccionDetalle extends BaseDomain implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_CONSTANCIA_DETALLE")
    private Integer idCostanciaDetalle;

    @Column(name = "NRO_CONSTANCIA")
    private Integer nroConstancia;

    @Column(name = "TIPO_DOC_PROVEEDOR", length = 50)
    private String tipoDocProveedor;
    @Column(name="NRO_DOC_PROVEEDOR", length = 20)
    private String nroDocProveedor;

    @Column(name="RAZON_SOCIAL_PROV", length = 100)
    private String razonSocialProv;

    @Column(name = "CODIGO_OPERACION", length = 3)
    private String codigoOperacion;

    @Column(name = "NOMBRE_OPERACION", length = 70)
    private String nombreOperacion;

    @Column(name = "CODIGO_BIEN_SERV", length = 4)
    private String codigoBienServ;

    @Column(name = "NOMBRE_BIEN_SERV", length = 70)
    private String nombreBienServ;

    @Column(name= "MONTO_DEPOSITO", columnDefinition = "DECIMAL(11,2)")
    private Double montoDeposito;

    @Column(name = "PERIODO_TRIBUTARIO", length = 10)
    private String periodoTributario;

    @Column(name = "TIPO_COMPROBANTE", length = 20)
    private String tipoComprobante;

    @Column(name= "NRO_COMPROBANTE", length = 20)
    private String nroComprobante;
    @Column(name = "ENVIA_CORREO", columnDefinition = "boolean default false")
    private boolean enviaCorreo;

    @ManyToOne
    @JoinColumn(name = "ID_CONSTANCIA_DETR", referencedColumnName = "ID_CONSTANCIA_DETR")
    private ConstanciaDetraccion constanciaCab;

    @Override
    public String toString() {
        return "ConstanciaDetraccionDetalle{" +
                "idCostanciaDetalle=" + idCostanciaDetalle +
                ", nroConstancia=" + nroConstancia +
                ", tipoDocProveedor='" + tipoDocProveedor + '\'' +
                ", nroDocProveedor='" + nroDocProveedor + '\'' +
                ", razonSocialProv='" + razonSocialProv + '\'' +
                ", codigoOperacion='" + codigoOperacion + '\'' +
                ", nombreOperacion='" + nombreOperacion + '\'' +
                ", codigoBienServ='" + codigoBienServ + '\'' +
                ", nombreBienServ='" + nombreBienServ + '\'' +
                ", montoDeposito=" + montoDeposito +
                ", periodoTributario='" + periodoTributario + '\'' +
                ", tipoComprobante='" + tipoComprobante + '\'' +
                ", nroComprobante='" + nroComprobante + '\'' +
                ", enviaCorreo=" + enviaCorreo +
                ", constanciaCab=" + constanciaCab +
                '}';
    }
}
