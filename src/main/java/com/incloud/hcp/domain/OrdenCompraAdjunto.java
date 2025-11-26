package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "ORDEN_COMPRA_ADJUNTO")
@Getter
@Setter
@NoArgsConstructor
public class OrdenCompraAdjunto  extends BaseDomain implements Serializable {

    @Column(name = "id_orden_compra_adjunto", precision = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_orden_compra_adjunto")
    @Id
    @SequenceGenerator(name = "seq_orden_compra_adjunto", sequenceName = "seq_orden_compra_adjunto", allocationSize = 1)
    private Integer id;
    @NotEmpty
    @Size(max = 60)
    @Column(name = "archivo_id", nullable = false, length = 60)
    private String archivoId;
    @NotEmpty

    @Column(name = "archivo_nombre", nullable = false)
    private String archivoNombre;
    @Size(max = 60)
    @Column(name = "estado", nullable = false)
    private String estado;

    @NotEmpty
    @Size(max = 100)
    @Column(name = "archivo_tipo", nullable = false, length = 100)
    private String archivoTipo;

    @NotEmpty
    @Size(max = 1000)
    @Column(name = "ruta_adjunto", nullable = false, length = 1000)
    private String rutaAdjunto;

    @NotEmpty
    @Size(max = 100)
    @Column(name = "tipo_doc", nullable = false, length = 100)
    private String tipoDoc;

    @NotEmpty
    @Size(max = 20)
    @Column(name = "numero_oc", nullable = false, length = 20)
    private String numeroOc;
}
