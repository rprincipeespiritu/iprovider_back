package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Access(AccessType.FIELD)
@Table(name="ORDEN_COMPRA")
public class OrdenCompra extends BaseDomain implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "ORDEN_COMPRA_ID_SEQ", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "ORDEN_COMPRA_ID_SEQ", sequenceName = "ORDEN_COMPRA_ID_SEQ", allocationSize = 1)
	@Column(name="ID_ORDEN_COMPRA", unique=true, nullable=false)
	private Integer id;

	@Column(name="NUMERO_ORDEN_COMPRA", nullable=false, length=100)
	private String numeroOrdenCompra;

	@Column(name="VERSION", nullable=false)
	private Integer version;

    @Column(name="IS_ACTIVE", nullable=false, length = 1)
    private String isActive;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = TipoOrdenCompra.class)
	@JoinColumn(name="ID_TIPO_ORDEN_COMPRA", referencedColumnName = "ID_TIPO_ORDEN_COMPRA", insertable = false, updatable = false)
	private TipoOrdenCompra tipoOrdenCompra;

	@Column(name="ID_TIPO_ORDEN_COMPRA")
	private Integer idTipoOrdenCompra;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = EstadoOrdenCompra.class)
	@JoinColumn(name="ID_ESTADO_ORDEN_COMPRA", referencedColumnName = "ID_ESTADO_ORDEN_COMPRA", insertable = false, updatable = false)
	private EstadoOrdenCompra estadoOrdenCompra;

	@Column(name="ID_ESTADO_ORDEN_COMPRA")
	private Integer idEstadoOrdenCompra;

    @Column(name="ESTADO_SAP", length = 1)
    private String estadoSap;

    @Column(name="CODIGO_CLASE_ORDEN_COMPRA", length = 4)
    private String codigoClaseOrdenCompra;

    @Column(name="CLASE_ORDEN_COMPRA", length = 30)
    private String claseOrdenCompra;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Sociedad.class)
    @JoinColumn(name="SOCIEDAD", referencedColumnName = "CODIGO_SOCIEDAD", insertable = false, updatable = false)
    private Sociedad infoSociedad;

	@Column(name="SOCIEDAD", length = 4)
	private String sociedad;

	@Column(name="COMPRADOR_USUARIO_SAP", length = 12)
	private String compradorUsuarioSap;

	@Column(name="COMPRADOR_NOMBRE", length = 80)
	private String compradorNombre;

    @Column(name="ULTIMO_LIBERADOR_USUARIO_SAP", length = 12)
    private String ultimoLiberadorUsuarioSap;

	@Column(name="PROVEEDOR_CODIGO_SAP", length = 10)
	private String proveedorCodigoSap;

    @Column(name="PROVEEDOR_RUC", length = 16)
    private String proveedorRuc;

	@Column(name="PROVEEDOR_RAZON_SOCIAL", length = 50)
	private String proveedorRazonSocial;

	@Column(name="CODIGO_MONEDA", length = 5)
	private String codigoMondeda;

	@Column(name="TOTAL", precision = 12,  scale = 2)
	private BigDecimal total;

    @Column(name="CONDICION_PAGO", length = 4)
	private String condicionPago;

    @Column(name="CONDICION_PAGO_DESC", length = 50)
    private String condicionPagoDescripcion;

	@Column(name="FECHA_ENTREGA")
	private Date fechaEntrega;

	@Column(name="FECHA_REGISTRO")
	private Date fechaRegistro;

	@Column(name="FECHA_MODIFICACION")
	private Date fechaModificacion;

    @Column(name="HORA_MODIFICACION")
    private Time horaModificacion;

    @Column(name="FECHA_PUBLICACION")
    private Timestamp fechaPublicacion;

	@Column(name="FECHA_VISUALIZACION")
	private Timestamp fechaVisualizacion;

    @Column(name="FECHA_APROBACION")
    private Timestamp fechaAprobacion;

    @Column(name="MOTIVO_RECHAZO", length = 1000)
    private String motivoRechazo;

    @Column(name="LUGAR_ENTREGA", length = 100)
    private String lugarEntrega;

    @Column(name="INDICADOR_CONTRATO_MARCO", length = 1)
    private String indicadorContratoMarco;

    @Column(name="AUTORIZADOR_FECHA_LIBERACION", length = 30)
    private String autorizadorFechaLiberacion;

    @OneToMany(mappedBy = "ordenCompra", targetEntity = OrdenCompraTextoCabecera.class, fetch = FetchType.LAZY)
    private List<OrdenCompraTextoCabecera> ordenCompraTextoCabeceraList;

    // Campos adicionales - Silvestre
    @Column(name="ID_EMPRESA")
    private Integer idEmpresa;

    @Column(name="NO_REGISTRO", length = 15)
    private String noRegistro;

    @Column(name="DCTO")
    private BigDecimal dcto;

    @Column(name="SUBTOTAL")
    private BigDecimal subtotal;

    @Column(name="IMPUESTOS")
    private BigDecimal impuestos;

    @Column(name="ID_PAGO")
    private Integer idPago;

    @Column(name="ID_AGENDA_DIRECCION")
    private Integer idAgendaDireccion;

    @Column(name="ID_AGENDA_DIRECCION2")
    private Integer idAgendaDireccion2;

    @Column(name="OBSERVACIONES")
    private String observaciones;

    @Column(name="NOTAS_RECEPCION")
    private String notasRecepcion;

    //nuevas columnas
    @Column(name="FECHA_INICIO_CONTRATO")
    private Date fechaInicioContrato;

    @Column(name="FECHA_FIN_CONTRATO")
    private Date fechaFinContrato;

    @Column(name="TIPO_EXCEPCION")
    private String tipoExcepcion;

    @Column(name="VALOR_IMPUESTO", precision = 14, scale = 3)
    private BigDecimal valorImpuesto;

    @Column(name="INDICADOR_IMPUESTO")
    private String indicadorImpuesto;

    @Column(name="ULTIMO_LIBERADOR_USUARIO_NOMBRE")
    private String ultimoLiberadorUsuarioNombre;
    
    @Column(name="COMPRADOR_EMAIL")
    private String compradorEmail;

    @Column(name="COMPRADOR_TELEFONO")
    private String compradorTelefono;
    
    @Column(name="SEDE_SOLICITUD")
    private String sedeSolicitud;
    @Column(name="CLASEDOC", length = 6)
    private String claseDoc;
    @Column(name="ESTADO_SOLICITUD")
    private String estadoSolicitud;

    public BigDecimal getValorImpuesto() {
        return valorImpuesto;
    }

    public void setValorImpuesto(BigDecimal valorImpuesto) {
        this.valorImpuesto = valorImpuesto;
    }

    public Date getFechaInicioContrato() {
        return fechaInicioContrato;
    }

    public void setFechaInicioContrato(Date fechaInicioContrato) {
        this.fechaInicioContrato = fechaInicioContrato;
    }

    public Date getFechaFinContrato() {
        return fechaFinContrato;
    }

    public void setFechaFinContrato(Date fechaFinContrato) {
        this.fechaFinContrato = fechaFinContrato;
    }

    public String getTipoExcepcion() {
        return tipoExcepcion;
    }

    public void setTipoExcepcion(String tipoExcepcion) {
        this.tipoExcepcion = tipoExcepcion;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getNotasRecepcion() {
        return notasRecepcion;
    }

    public void setNotasRecepcion(String notasRecepcion) {
        this.notasRecepcion = notasRecepcion;
    }

    public BigDecimal getDcto() {
        return dcto;
    }

    public void setDcto(BigDecimal dcto) {
        this.dcto = dcto;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNoRegistro() {
        return noRegistro;
    }

    public void setNoRegistro(String noRegistro) {
        this.noRegistro = noRegistro;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(BigDecimal impuestos) {
        this.impuestos = impuestos;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public Integer getIdAgendaDireccion() {
        return idAgendaDireccion;
    }

    public void setIdAgendaDireccion(Integer idAgendaDireccion) {
        this.idAgendaDireccion = idAgendaDireccion;
    }

    public Integer getIdAgendaDireccion2() {
        return idAgendaDireccion2;
    }

    public void setIdAgendaDireccion2(Integer idAgendaDireccion2) {
        this.idAgendaDireccion2 = idAgendaDireccion2;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumeroOrdenCompra() {
        return numeroOrdenCompra;
    }

    public void setNumeroOrdenCompra(String numeroOrdenCompra) {
        this.numeroOrdenCompra = numeroOrdenCompra;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public TipoOrdenCompra getTipoOrdenCompra() {
        return tipoOrdenCompra;
    }

    public void setTipoOrdenCompra(TipoOrdenCompra tipoOrdenCompra) {
        this.tipoOrdenCompra = tipoOrdenCompra;
    }

    public Integer getIdTipoOrdenCompra() {
        return idTipoOrdenCompra;
    }

    public void setIdTipoOrdenCompra(Integer idTipoOrdenCompra) {
        this.idTipoOrdenCompra = idTipoOrdenCompra;
    }

    public EstadoOrdenCompra getEstadoOrdenCompra() {
        return estadoOrdenCompra;
    }

    public void setEstadoOrdenCompra(EstadoOrdenCompra estadoOrdenCompra) {
        this.estadoOrdenCompra = estadoOrdenCompra;
    }

    public Integer getIdEstadoOrdenCompra() {
        return idEstadoOrdenCompra;
    }

    public void setIdEstadoOrdenCompra(Integer idEstadoOrdenCompra) {
        this.idEstadoOrdenCompra = idEstadoOrdenCompra;
    }

    public String getEstadoSap() {
        return estadoSap;
    }

    public void setEstadoSap(String estadoSap) {
        this.estadoSap = estadoSap;
    }

    public String getCodigoClaseOrdenCompra() {
        return codigoClaseOrdenCompra;
    }

    public void setCodigoClaseOrdenCompra(String codigoClaseOrdenCompra) {
        this.codigoClaseOrdenCompra = codigoClaseOrdenCompra;
    }

    public String getClaseOrdenCompra() {
        return claseOrdenCompra;
    }

    public void setClaseOrdenCompra(String claseOrdenCompra) {
        this.claseOrdenCompra = claseOrdenCompra;
    }

    public Sociedad getInfoSociedad() {
        return infoSociedad;
    }

    public void setInfoSociedad(Sociedad infoSociedad) {
        this.infoSociedad = infoSociedad;
    }

    public String getSociedad() {
        return sociedad;
    }

    public void setSociedad(String sociedad) {
        this.sociedad = sociedad;
    }

    public String getCompradorUsuarioSap() {
        return compradorUsuarioSap;
    }

    public void setCompradorUsuarioSap(String compradorUsuarioSap) {
        this.compradorUsuarioSap = compradorUsuarioSap;
    }

    public String getCompradorNombre() {
        return compradorNombre;
    }

    public void setCompradorNombre(String compradorNombre) {
        this.compradorNombre = compradorNombre;
    }

    public String getCompradorEmail() {
        return compradorEmail;
    }

    public void setCompradorEmail(String compradorEmail) {
        this.compradorEmail = compradorEmail;
    }

    public String getCompradorTelefono() {
        return compradorTelefono;
    }

    public void setCompradorTelefono(String compradorTelefono) {
        this.compradorTelefono = compradorTelefono;
    }


    public String getSedeSolicitud() {
        return sedeSolicitud;
    }

    public void setSedeSolicitud(String sedeSolicitud) {
        this.sedeSolicitud = sedeSolicitud;
    }

    public String getUltimoLiberadorUsuarioSap() {
        return ultimoLiberadorUsuarioSap;
    }

    public void setUltimoLiberadorUsuarioSap(String ultimoLiberadorUsuarioSap) {
        this.ultimoLiberadorUsuarioSap = ultimoLiberadorUsuarioSap;
    }

    public String getProveedorCodigoSap() {
        return proveedorCodigoSap;
    }

    public void setProveedorCodigoSap(String proveedorCodigoSap) {
        this.proveedorCodigoSap = proveedorCodigoSap;
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

    public String getCodigoMondeda() {
        return codigoMondeda;
    }

    public void setCodigoMondeda(String codigoMondeda) {
        this.codigoMondeda = codigoMondeda;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCondicionPago() {
        return condicionPago;
    }

    public void setCondicionPago(String condicionPago) {
        this.condicionPago = condicionPago;
    }

    public String getCondicionPagoDescripcion() {
        return condicionPagoDescripcion;
    }

    public void setCondicionPagoDescripcion(String condicionPagoDescripcion) {
        this.condicionPagoDescripcion = condicionPagoDescripcion;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Time getHoraModificacion() {
        return horaModificacion;
    }

    public void setHoraModificacion(Time horaModificacion) {
        this.horaModificacion = horaModificacion;
    }

    public Timestamp getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(Timestamp fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public Timestamp getFechaVisualizacion() {
        return fechaVisualizacion;
    }

    public void setFechaVisualizacion(Timestamp fechaVisualizacion) {
        this.fechaVisualizacion = fechaVisualizacion;
    }

    public Timestamp getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(Timestamp fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public String getLugarEntrega() {
        return lugarEntrega;
    }

    public void setLugarEntrega(String lugarEntrega) {
        this.lugarEntrega = lugarEntrega;
    }

    public String getIndicadorContratoMarco() {
        return indicadorContratoMarco;
    }

    public void setIndicadorContratoMarco(String indicadorContratoMarco) {
        this.indicadorContratoMarco = indicadorContratoMarco;
    }

    public String getAutorizadorFechaLiberacion() {
        return autorizadorFechaLiberacion;
    }

    public void setAutorizadorFechaLiberacion(String autorizadorFechaLiberacion) {
        this.autorizadorFechaLiberacion = autorizadorFechaLiberacion;
    }

    public List<OrdenCompraTextoCabecera> getOrdenCompraTextoCabeceraList() {
        return ordenCompraTextoCabeceraList;
    }

    public void setOrdenCompraTextoCabeceraList(List<OrdenCompraTextoCabecera> ordenCompraTextoCabeceraList) {
        this.ordenCompraTextoCabeceraList = ordenCompraTextoCabeceraList;
    }

    public String getIndicadorImpuesto() {
        return indicadorImpuesto;
    }

    public void setIndicadorImpuesto(String indicadorImpuesto) {
        this.indicadorImpuesto = indicadorImpuesto;
    }

    public String getUltimoLiberadorUsuarioNombre() {
        return ultimoLiberadorUsuarioNombre;
    }

    public void setUltimoLiberadorUsuarioNombre(String ultimoLiberadorUsuarioNombre) {
        this.ultimoLiberadorUsuarioNombre = ultimoLiberadorUsuarioNombre;
    }


    @Override
    public String toString() {
        return "OrdenCompra{" +
                "id=" + id +
                ", numeroOrdenCompra='" + numeroOrdenCompra + '\'' +
                ", version=" + version +
                ", isActive='" + isActive + '\'' +
                ", tipoOrdenCompra=" + tipoOrdenCompra +
                ", idTipoOrdenCompra=" + idTipoOrdenCompra +
                ", estadoOrdenCompra=" + estadoOrdenCompra +
                ", idEstadoOrdenCompra=" + idEstadoOrdenCompra +
                ", estadoSap='" + estadoSap + '\'' +
                ", codigoClaseOrdenCompra='" + codigoClaseOrdenCompra + '\'' +
                ", claseOrdenCompra='" + claseOrdenCompra + '\'' +
                ", infoSociedad=" + infoSociedad +
                ", sociedad='" + sociedad + '\'' +
                ", compradorUsuarioSap='" + compradorUsuarioSap + '\'' +
                ", compradorNombre='" + compradorNombre + '\'' +
                ", ultimoLiberadorUsuarioSap='" + ultimoLiberadorUsuarioSap + '\'' +
                ", proveedorCodigoSap='" + proveedorCodigoSap + '\'' +
                ", proveedorRuc='" + proveedorRuc + '\'' +
                ", proveedorRazonSocial='" + proveedorRazonSocial + '\'' +
                ", codigoMondeda='" + codigoMondeda + '\'' +
                ", total=" + total +
                ", condicionPago='" + condicionPago + '\'' +
                ", condicionPagoDescripcion='" + condicionPagoDescripcion + '\'' +
                ", fechaEntrega=" + fechaEntrega +
                ", fechaRegistro=" + fechaRegistro +
                ", fechaModificacion=" + fechaModificacion +
                ", horaModificacion=" + horaModificacion +
                ", fechaPublicacion=" + fechaPublicacion +
                ", fechaVisualizacion=" + fechaVisualizacion +
                ", fechaAprobacion=" + fechaAprobacion +
                ", motivoRechazo='" + motivoRechazo + '\'' +
                ", lugarEntrega='" + lugarEntrega + '\'' +
                ", indicadorContratoMarco='" + indicadorContratoMarco + '\'' +
                ", autorizadorFechaLiberacion='" + autorizadorFechaLiberacion + '\'' +
                ", ordenCompraTextoCabeceraList=" + ordenCompraTextoCabeceraList +
                ", idEmpresa=" + idEmpresa +
                ", noRegistro='" + noRegistro + '\'' +
                ", dcto=" + dcto +
                ", subtotal=" + subtotal +
                ", impuestos=" + impuestos +
                ", idPago=" + idPago +
                ", idAgendaDireccion=" + idAgendaDireccion +
                ", idAgendaDireccion2=" + idAgendaDireccion2 +
                ", observaciones='" + observaciones + '\'' +
                ", notasRecepcion='" + notasRecepcion + '\'' +
                ", fechaInicioContrato=" + fechaInicioContrato +
                ", fechaFinContrato=" + fechaFinContrato +
                ", tipoExcepcion='" + tipoExcepcion + '\'' +
                ", valorImpuesto=" + valorImpuesto +
                ", indicadorImpuesto=" + indicadorImpuesto +
                '}';
    }

    public String getEstadoSolicitud() {
        return estadoSolicitud;
    }

    public void setEstadoSolicitud(String estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }

    public String getClaseDoc() {
        return claseDoc;
    }

    public void setClaseDoc(String claseDoc) {
        this.claseDoc = claseDoc;
    }
}