package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Access(AccessType.FIELD)
@Table(name="DOCUMENTO_ACEPTACION")
public class DocumentoAceptacion extends BaseDomain implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "DOCUMENTO_ACEPTACION_ID_SEQ", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "DOCUMENTO_ACEPTACION_ID_SEQ", sequenceName = "DOCUMENTO_ACEPTACION_ID_SEQ", allocationSize = 1)
	@Column(name="ID_DOCUMENTO_ACEPTACION", unique=true, nullable=false)
	private Integer id;

    @Column(name="NUMERO_DOCUMENTO_ACEPTACION", nullable=false, length=10)
    private String numeroDocumentoAceptacion;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = TipoDocumentoAceptacion.class)
    @JoinColumn(name="ID_TIPO_DOCUMENTO_ACEPTACION", referencedColumnName = "ID_TIPO_DOCUMENTO_ACEPTACION", insertable = false, updatable = false)
    private TipoDocumentoAceptacion tipoDocumentoAceptacion;

    @Column(name="ID_TIPO_DOCUMENTO_ACEPTACION", nullable=false)
    private Integer idTipoDocumentoAceptacion;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = EstadoDocumentoAceptacion.class)
    @JoinColumn(name="ID_ESTADO_DOCUMENTO_ACEPTACION", referencedColumnName = "ID_ESTADO_DOCUMENTO_ACEPTACION", insertable = false, updatable = false)
    private EstadoDocumentoAceptacion estadoDocumentoAceptacion;

    @Column(name="ID_ESTADO_DOCUMENTO_ACEPTACION", nullable=false)
    private Integer idEstadoDocumentoAceptacion;

    @Column(name="ID_ORDEN_COMPRA")
    private Integer idOrdenCompra;

	@Column(name="NUMERO_ORDEN_COMPRA", length=10)
	private String numeroOrdenCompra;

    @Column(name="POSICION_ORDEN_COMPRA", length=5)
    private String posicionOrdenCompra;

    @Column(name="NUMERO_GUIA_PROVEEDOR", length=30)
    private String numeroGuiaProveedor;

    @Column(name="PROVEEDOR_RUC", length = 16)
    private String proveedorRuc;

    @Column(name="PROVEEDOR_RAZON_SOCIAL", length = 70)
    private String proveedorRazonSocial;

    @Column(name="USUARIO_SAP_RECEPCION", length = 12)
    private String usuarioSapRecepcion;

    @Column(name="USUARIO_SAP_AUTORIZA", length = 12)
    private String usuarioSapAutoriza;

    @Column(name="CODIGO_MONEDA", length=5)
    private String codigoMoneda;

    @Column(name="FECHA_EMISION")
    private Date fechaEmision;

    @Column(name="FECHA_ACEPTACION")
    private Date fechaAceptacion;

    @Column(name="FECHA_PUBLICACION")
    private Timestamp fechaPublicacion;

    @Column(name="STATUS_SAP", length=20)
    private String statusSap;

    @Column(name="NRO_GUIA_REMISION", length=20)  //mizalo
    private String nroGuiaRemision;

    @OneToMany(mappedBy = "documentoAceptacion", targetEntity = DocumentoAceptacionDetalle.class, fetch = FetchType.LAZY)
    private List<DocumentoAceptacionDetalle> documentoAceptacionDetalleList;

    // Campos adicionales - Silvestre
    @Column(name="OP_GUIA_COMPRA")
    private Integer opGuiaCompra;

    @Column(name="CODIGO_ALMACEN")
    private Integer codigoAlmacen;

    @Column(name="CODIGO_DIRECCION")
    private Integer codigoDireccion;

    @Column(name="OBSERVACION")
    private String observacion;

    @Column(name="ENTREGA_COMPLETA")
    private Boolean entregaCompleta;

    //===============================
    // CAMPOOS NUEVOS
    //===============================
    @Column(name="FECHA_DOCUMENTO")
    private Date fechaDocumento;

    @Column(name="FECHA_CONTABILIZACION")
    private Date fechaContabilizacion;

    @Column(name="NOTA_ENTREGA")
    private String notaEntrega;

    @Column(name = "TEXTO_CABECERA")
    private String textoCabecera;

    @Column(name = "REFERENCIA")
    private String referencia;


    @ManyToOne
    @JoinColumn(name="ID_ACTA_SUSTENTO")
    private ActaSustento actaSustento;
    //===============================

    @Column(name="VALOR_IMPUESTO", precision = 14, scale = 3)
    private BigDecimal valorImpuesto;

    @Column(name ="SOCIEDAD")
    private String sociedad;

    @Column(name ="NUMERO_RECEPCION")
    private String numeroRecepcion;

    @Column(name ="NUMERO_LOTE")
    private String numeroLote;

    @Column(name ="USUARIO_NOMBRE_RECEPCION")
    private String usuarioNombreRecepcion;

    public String getSociedad() {
        return sociedad;
    }

    public void setSociedad(String sociedad) {
        this.sociedad = sociedad;
    }

    public String getNumeroRecepcion() {
        return numeroRecepcion;
    }

    public void setNumeroRecepcion(String numeroRecepcion) {
        this.numeroRecepcion = numeroRecepcion;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumeroDocumentoAceptacion() {
        return numeroDocumentoAceptacion;
    }

    public void setNumeroDocumentoAceptacion(String numeroDocumentoAceptacion) {
        this.numeroDocumentoAceptacion = numeroDocumentoAceptacion;
    }

    public TipoDocumentoAceptacion getTipoDocumentoAceptacion() {
        return tipoDocumentoAceptacion;
    }

    public void setTipoDocumentoAceptacion(TipoDocumentoAceptacion tipoDocumentoAceptacion) {
        this.tipoDocumentoAceptacion = tipoDocumentoAceptacion;
    }

    public Integer getIdTipoDocumentoAceptacion() {
        return idTipoDocumentoAceptacion;
    }

    public void setIdTipoDocumentoAceptacion(Integer idTipoDocumentoAceptacion) {
        this.idTipoDocumentoAceptacion = idTipoDocumentoAceptacion;
    }

    public EstadoDocumentoAceptacion getEstadoDocumentoAceptacion() {
        return estadoDocumentoAceptacion;
    }

    public void setEstadoDocumentoAceptacion(EstadoDocumentoAceptacion estadoDocumentoAceptacion) {
        this.estadoDocumentoAceptacion = estadoDocumentoAceptacion;
    }

    public Integer getIdEstadoDocumentoAceptacion() {
        return idEstadoDocumentoAceptacion;
    }

    public void setIdEstadoDocumentoAceptacion(Integer idEstadoDocumentoAceptacion) {
        this.idEstadoDocumentoAceptacion = idEstadoDocumentoAceptacion;
    }

    public Integer getIdOrdenCompra() {
        return idOrdenCompra;
    }

    public void setIdOrdenCompra(Integer idOrdenCompra) {
        this.idOrdenCompra = idOrdenCompra;
    }

    public String getNumeroOrdenCompra() {
        return numeroOrdenCompra;
    }

    public void setNumeroOrdenCompra(String numeroOrdenCompra) {
        this.numeroOrdenCompra = numeroOrdenCompra;
    }

    public String getPosicionOrdenCompra() {
        return posicionOrdenCompra;
    }

    public void setPosicionOrdenCompra(String posicionOrdenCompra) {
        this.posicionOrdenCompra = posicionOrdenCompra;
    }

    public String getNumeroGuiaProveedor() {
        return numeroGuiaProveedor;
    }

    public void setNumeroGuiaProveedor(String numeroGuiaProveedor) {
        this.numeroGuiaProveedor = numeroGuiaProveedor;
    }

    public String getProveedorRuc() {
        return proveedorRuc;
    }

    public void setProveedorRuc(String proveedorRuc) {
        this.proveedorRuc = proveedorRuc;
    }

    public String getProveedorRazonSocial() {
        return proveedorRazonSocial;
    }

    public void setProveedorRazonSocial(String proveedorRazonSocial) {
        this.proveedorRazonSocial = proveedorRazonSocial;
    }

    public String getUsuarioSapRecepcion() {
        return usuarioSapRecepcion;
    }

    public void setUsuarioSapRecepcion(String usuarioSapRecepcion) {
        this.usuarioSapRecepcion = usuarioSapRecepcion;
    }

    public String getUsuarioSapAutoriza() {
        return usuarioSapAutoriza;
    }

    public void setUsuarioSapAutoriza(String usuarioSapAutoriza) {
        this.usuarioSapAutoriza = usuarioSapAutoriza;
    }

    public String getCodigoMoneda() {
        return codigoMoneda;
    }

    public void setCodigoMoneda(String codigoMoneda) {
        this.codigoMoneda = codigoMoneda;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Date getFechaAceptacion() {
        return fechaAceptacion;
    }

    public void setFechaAceptacion(Date fechaAceptacion) {
        this.fechaAceptacion = fechaAceptacion;
    }

    public Timestamp getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(Timestamp fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getStatusSap() {
        return statusSap;
    }

    public void setStatusSap(String statusSap) {
        this.statusSap = statusSap;
    }

    public List<DocumentoAceptacionDetalle> getDocumentoAceptacionDetalleList() {
        return documentoAceptacionDetalleList;
    }

    public void setDocumentoAceptacionDetalleList(List<DocumentoAceptacionDetalle> documentoAceptacionDetalleList) {
        this.documentoAceptacionDetalleList = documentoAceptacionDetalleList;
    }

    public Integer getOpGuiaCompra() {
        return opGuiaCompra;
    }

    public void setOpGuiaCompra(Integer opGuiaCompra) {
        this.opGuiaCompra = opGuiaCompra;
    }

    public Integer getCodigoAlmacen() {
        return codigoAlmacen;
    }

    public void setCodigoAlmacen(Integer codigoAlmacen) {
        this.codigoAlmacen = codigoAlmacen;
    }

    public Integer getCodigoDireccion() {
        return codigoDireccion;
    }

    public void setCodigoDireccion(Integer codigoDireccion) {
        this.codigoDireccion = codigoDireccion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }


    public Date getFechaDocumento() {
        return fechaDocumento;
    }

    public void setFechaDocumento(Date fechaDocumento) {
        this.fechaDocumento = fechaDocumento;
    }

    public Date getFechaContabilizacion() {
        return fechaContabilizacion;
    }

    public void setFechaContabilizacion(Date fechaContabilizacion) {
        this.fechaContabilizacion = fechaContabilizacion;
    }

    public String getNotaEntrega() {
        return notaEntrega;
    }

    public void setNotaEntrega(String notaEntrega) {
        this.notaEntrega = notaEntrega;
    }

    public String getTextoCabecera() {
        return textoCabecera;
    }

    public void setTextoCabecera(String textoCabecera) {
        this.textoCabecera = textoCabecera;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public ActaSustento getActaSustento() {
        return actaSustento;
    }

    public void setActaSustento(ActaSustento actaSustento) {
        this.actaSustento = actaSustento;
    }

    public BigDecimal getValorImpuesto() {
        return valorImpuesto;
    }

    public void setValorImpuesto(BigDecimal valorImpuesto) {
        this.valorImpuesto = valorImpuesto;
    }

    public String getNroGuiaRemision() {
        return nroGuiaRemision;
    }

    public void setNroGuiaRemision(String nroGuiaRemision) {
        this.nroGuiaRemision = nroGuiaRemision;
    }

    public Boolean getEntregaCompleta() {
        return entregaCompleta;
    }

    public void setEntregaCompleta(Boolean entregaCompleta) {
        this.entregaCompleta = entregaCompleta;
    }

    public String getUsuarioNombreRecepcion() {
        return usuarioNombreRecepcion;
    }

    public void setUsuarioNombreRecepcion(String usuarioNombreRecepcion) {
        this.usuarioNombreRecepcion = usuarioNombreRecepcion;
    }
    

    @Override
    public String toString() {
        return "DocumentoAceptacion{" +
                "id=" + id +
                ", numeroDocumentoAceptacion='" + numeroDocumentoAceptacion + '\'' +
                ", tipoDocumentoAceptacion=" + tipoDocumentoAceptacion +
                ", idTipoDocumentoAceptacion=" + idTipoDocumentoAceptacion +
                ", estadoDocumentoAceptacion=" + estadoDocumentoAceptacion +
                ", idEstadoDocumentoAceptacion=" + idEstadoDocumentoAceptacion +
                ", idOrdenCompra=" + idOrdenCompra +
                ", numeroOrdenCompra='" + numeroOrdenCompra + '\'' +
                ", posicionOrdenCompra='" + posicionOrdenCompra + '\'' +
                ", numeroGuiaProveedor='" + numeroGuiaProveedor + '\'' +
                ", proveedorRuc='" + proveedorRuc + '\'' +
                ", proveedorRazonSocial='" + proveedorRazonSocial + '\'' +
                ", usuarioSapRecepcion='" + usuarioSapRecepcion + '\'' +
                ", usuarioSapAutoriza='" + usuarioSapAutoriza + '\'' +
                ", codigoMoneda='" + codigoMoneda + '\'' +
                ", fechaEmision=" + fechaEmision +
                ", fechaAceptacion=" + fechaAceptacion +
                ", fechaPublicacion=" + fechaPublicacion +
                ", statusSap='" + statusSap + '\'' +
                ", opGuiaCompra='" + opGuiaCompra + '\'' +
                ", codigoAlmacen='" + codigoAlmacen + '\'' +
                ", codigoDireccion='" + codigoDireccion + '\'' +
                ", observacion='" + observacion + '\'' +
                ", documentoAceptacionDetalleList=" + documentoAceptacionDetalleList +
                ", fechaDocumento=" + fechaDocumento +
                ", fechaContabilizacion=" + fechaContabilizacion +
                ", notaEntrega=" + notaEntrega +
                ", textoCabecera=" + textoCabecera +
                ", referencia=" + referencia +
                ", actaSustento=" + actaSustento +
                ", nroGuiaRemision=" + nroGuiaRemision +
                '}';
    }
}