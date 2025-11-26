package com.incloud.hcp.domain;

import com.google.common.base.MoreObjects;
import com.incloud.hcp.domain.MtrTipoDocumento;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.domain._framework.BaseDomain;
import com.incloud.hcp.domain._framework.Identifiable;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.logging.Logger;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "proveedor_adjunto_cuenta_bancaria")
public class ProveedorAdjuntoCuentaBancaria extends BaseDomain implements Identifiable<Integer>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(com.incloud.hcp.domain.ProveedorAdjuntoCuentaBancaria.class.getName());

    // Raw attributes
    private Integer id;
    private String archivoId;
    private String archivoNombre;
    private String archivoExtension;
    private String archivoTipo;
    private String rutaAdjunto;

    // Many to one
    private Proveedor idProveedor;

    @Override
    public String entityClassName() {
        return ProveedorAdjuntoCuentaBancaria.class.getSimpleName();
    }

    // -- [id] ------------------------

    @Override
    @Column(name = "id_proveedor_adjunto_cuenta_bancaria", precision = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_proveedor_adjunto_cuenta_bancaria")
    @Id
    @SequenceGenerator(name = "seq_proveedor_adjunto_cuenta_bancaria", sequenceName = "seq_proveedor_adjunto_cuenta_bancaria", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public ProveedorAdjuntoCuentaBancaria id(Integer id) {
        setId(id);
        return this;
    }

    @Override
    @Transient
    public boolean isIdSet() {
        return id != null;
    }
    // -- [archivoId] ------------------------

    @NotEmpty
    @Size(max = 60)
    @Column(name = "archivo_id", nullable = false, length = 60)
    public String getArchivoId() {
        return archivoId;
    }

    public void setArchivoId(String archivoId) {
        this.archivoId = archivoId;
    }

    public ProveedorAdjuntoCuentaBancaria archivoId(String archivoId) {
        setArchivoId(archivoId);
        return this;
    }
    // -- [archivoNombre] ------------------------

    @NotEmpty
    @Column(name = "archivo_nombre", nullable = false)
    public String getArchivoNombre() {
        return archivoNombre;
    }

    public void setArchivoNombre(String archivoNombre) {
        this.archivoNombre = archivoNombre;
    }

    public ProveedorAdjuntoCuentaBancaria archivoNombre(String archivoNombre) {
        setArchivoNombre(archivoNombre);
        return this;
    }
    // -- [archivoExtension] ------------------------

    @NotEmpty
    @Size(max = 100)
    @Column(name = "archivo_extension", nullable = false, length = 100)
    public String getArchivoExtension() {
        return archivoExtension;
    }

    public void setArchivoExtension(String archivoExtension) {
        this.archivoExtension = archivoExtension;
    }

    public ProveedorAdjuntoCuentaBancaria archivoExtension(String archivoExtension) {
        setArchivoTipo(archivoExtension);
        return this;
    }
    // -- [archivoTipo] ------------------------

    @NotEmpty
    @Size(max = 100)
    @Column(name = "archivo_tipo", nullable = false, length = 100)
    public String getArchivoTipo() {
        return archivoTipo;
    }

    public void setArchivoTipo(String archivoTipo) {
        this.archivoTipo = archivoTipo;
    }

    public ProveedorAdjuntoCuentaBancaria archivoTipo(String archivoTipo) {
        setArchivoTipo(archivoTipo);
        return this;
    }
    // -- [rutaAdjunto] ------------------------

    @NotEmpty
    @Size(max = 1000)
    @Column(name = "ruta_adjunto", nullable = false, length = 1000)
    public String getRutaAdjunto() {
        return rutaAdjunto;
    }

    public void setRutaAdjunto(String rutaAdjunto) {
        this.rutaAdjunto = rutaAdjunto;
    }

    public ProveedorAdjuntoCuentaBancaria rutaAdjunto(String rutaAdjunto) {
        setRutaAdjunto(rutaAdjunto);
        return this;
    }




    // -----------------------------------------------------------------
    // Many to One support
    // -----------------------------------------------------------------

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // many-to-one: ProveedorAdjuntoSunat.idProveedor ==> Proveedor.id
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -


    public ProveedorAdjuntoCuentaBancaria() {
    }

    public ProveedorAdjuntoCuentaBancaria(Proveedor proveedor, String rutaAdjunto, String archivoId, String archivoNombre, String archivoTipo) {
        this.idProveedor = proveedor;
        this.rutaAdjunto = rutaAdjunto;
        this.archivoId = archivoId;
        this.archivoNombre = archivoNombre;
        this.archivoTipo = archivoTipo;
    }


    @JoinColumn(name = "id_proveedor", nullable = false)
    @ManyToOne
    public Proveedor getIdProveedor() {
        return idProveedor;
    }

    /**
     * Set the {@link #idProveedor} without adding this ProveedorAdjuntoSunat instance on the passed {@link #idProveedor}
     */
    public void setIdProveedor(Proveedor idProveedor) {
        this.idProveedor = idProveedor;
    }

    public ProveedorAdjuntoCuentaBancaria idProveedor(Proveedor idProveedor) {
        setIdProveedor(idProveedor);
        return this;
    }

    /**
     * Apply the default values.
     */
    public ProveedorAdjuntoCuentaBancaria withDefaults() {
        return this;
    }

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof ProveedorAdjuntoCuentaBancaria && hashCode() == other.hashCode());
    }

    /**
     * Construct a readable string representation for this ProveedorAdjuntoSunat instance.
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .add("id", getId()) //
                .add("archivoId", getArchivoId()) //
                .add("archivoNombre", getArchivoNombre()) //
                .add("archivoTipo", getArchivoTipo()) //
                .add("rutaAdjunto", getRutaAdjunto()) //
                .toString();
    }
}