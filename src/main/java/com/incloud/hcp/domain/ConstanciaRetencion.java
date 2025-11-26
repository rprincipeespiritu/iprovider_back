package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "CONSTANCIA_RETENCION")
@Getter
@Setter
@NoArgsConstructor
public class ConstanciaRetencion extends BaseDomain implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONSTANCIA_RETENCION")
    private Integer  idConstanciaRetencion;

    @Column(name = "NRO_RETENCION", unique = true, length = 20)
    private String nroRetencion;
    @Column(name = "FECHA_DOC", columnDefinition = "DATE")
    private LocalDate fechaDoc;
    @Column(name = "RUC_EMISOR", length = 20)
    private String rucEmisor;
    @Column(name = "NOMBRE_EMISOR", length = 100)
    private String nombreEmisor;

    @Column(name = "RUC_PROVEEDOR", length = 20)
    private String rucProveedor;
    @Column(name = "NOMBRE_PROVEEDOR", length = 100)
    private String nombreProveedor;
    @Column(name = "CODIGO_RETENCION_SISTEMA", length = 4)
    private String codigoRetencionSistema;
    @Column(name="PORCENTAJE_RETENCION", columnDefinition = "DECIMAL(11,2)")
    private Double porcentajeRetencion;
    @Column(name="TOTAL_PAGO_RETENCION", columnDefinition = "DECIMAL(11,2)")
    private BigDecimal totalPagoRetencion;
    @Column(name= "MONTO_TOTAL", columnDefinition = "DECIMAL(11,2)")
    private BigDecimal montoTotal;
    @Column(name = "ENVIA_CORREO", columnDefinition = "boolean default false")
    private boolean enviaCorreo;

    @OneToMany(mappedBy = "datosCabecera", cascade = CascadeType.ALL)
    private List<ConstanciaRetencionDetalle> detallesCostancia;

}
