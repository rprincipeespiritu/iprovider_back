package com.incloud.hcp.domain;


import com.incloud.hcp.domain._framework.BaseDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "CONSTANCIA_DETRACCION")
@Getter
@Setter
@NoArgsConstructor
public class ConstanciaDetraccion extends BaseDomain implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONSTANCIA_DETR")
    private Integer idConstanciaDtr;
    @Column(name = "NRO_OPERACION", unique = true)
    private Long nroOperacion;
    @Column(name = "FECHA_PAGO", columnDefinition = "DATETIME")
    private LocalDateTime fechaPago;

    @Column(name = "ARCHIVO", length = 70)
    private String archivo;

    @Column(name = "LOTE", length = 20)
    private String Lote;

    @Column(name = "RUC_ADQ", length = 20)
    private String rucAdq;

    @Column(name = "RAZON_SOCIAL_ADQ", length = 100)
    private String razonSocialAdq;

    @Column(name = "NRO_DEPOSITO")
    private Integer nroDeposito;

    @Column(name = "MONTO_TOTAL", columnDefinition = "DECIMAL(11,2)")
    private Double montoTotal;

    @OneToMany(mappedBy = "constanciaCab", cascade = CascadeType.ALL)
    List<ConstanciaDetraccionDetalle> detalles;


}
