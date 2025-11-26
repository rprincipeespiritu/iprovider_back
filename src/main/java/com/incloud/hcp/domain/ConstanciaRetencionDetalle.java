package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "CONSTANCIA_RETENCION_DETALLE")
@Getter
@Setter
@NoArgsConstructor
public class ConstanciaRetencionDetalle extends BaseDomain implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONSTANCIA_RETENCION_DETALLE")
    private Integer idConstaciaRetencionDetalle;
    @Column(name = "NRO_FACTURA", length = 20)
    private String nroFactura;
    @Column(name = "TOTAL_FACTURA", columnDefinition = "DECIMAL(11,2)")
    private BigDecimal totalFactura;
    @Column(name = "MONTO_PAGADO", columnDefinition = "DECIMAL(11,2)")
    private BigDecimal montoPagado;
    @Column(name = "RETENCION", columnDefinition = "DECIMAL(11,2)")
    private Double retencion;
    @Column(name = "NETO_PAGADO", columnDefinition = "DECIMAL(11,2)")
    private BigDecimal netoPagado;
    @Column(name = "MONEDA_RECIBIDA", length = 4 )
    private String monedaRecibida;
    @Column(name = "MONEDA_CONVERTIDA", length = 4)
    private String monedaConvertida;
    @Column(name = "TASA_CAMBIO", columnDefinition = "DECIMAL(6,3)")
    private Double tasaCambio;


    @ManyToOne
    @JoinColumn(name = "ID_CONSTANCIA_RETENCION", referencedColumnName ="ID_CONSTANCIA_RETENCION" )
    private ConstanciaRetencion datosCabecera;



}
