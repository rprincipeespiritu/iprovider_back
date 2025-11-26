package com.incloud.hcp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Entity
@Access(AccessType.FIELD)
@Table(name="ORDEN_COMPRA_DETALLE")
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrdenCompraDetalle extends BaseDomain implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "ORDEN_COMPRA_DETALLE_ID_SEQ", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "ORDEN_COMPRA_DETALLE_ID_SEQ", sequenceName = "ORDEN_COMPRA_DETALLE_ID_SEQ", allocationSize = 1)
	@Column(name="ID_ORDEN_COMPRA_DETALLE", unique=true)
	private Integer id;

	@JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = OrdenCompra.class)
    @JoinColumn(name="ID_ORDEN_COMPRA", referencedColumnName = "ID_ORDEN_COMPRA", insertable = false, updatable = false)
	private OrdenCompra ordenCompra;

	@Column(name="ID_ORDEN_COMPRA", nullable=false)
	private Integer idOrdenCompra;

	@Column(name="NUMERO_ORDEN_COMPRA", nullable=false, length=100)
	private String numeroOrdenCompra;

    @Column(name="POSICION", length=5)
    private String posicion;

    @Column(name="TIPO_POSICION", length=1)
    private String tipoPosicion;

	@Column(name="CODIGO_SAP_BIEN_SERVICIO", length=18)
	private String codigoSapBienServicio;

	@Column(name="DESCRIPCION_BIEN_SERVICIO", length = 50)
	private String descripcionBienServicio;

	@Column(name="UNIDAD_MEDIDA_BIEN_SERVICIO", length = 3)
	private String unidadMedidaBienServicio;

	@Column(name="CODIGO_SAP_CENTRO", length = 4)
	private String codigoSapCentro;

	@Column(name="DENOMINACION_CENTRO", length = 30)
	private String denominacionCentro;

	@Column(name="DIRECCION_CENTRO", length = 50)
	private String direccionCentro;

	@Column(name="CODIGO_SAP_ALMACEN", length = 4)
	private String codigoSapAlmacen;

    @Column(name="LUGAR_ENTREGA_DETALLE", length = 100)
    private String lugarEntregaDetalle;

    @Column(name="DENOMINACION_ALMACEN", length = 20)
    private String denominacionAlmacen;

    @Column(name="CANTIDAD", precision = 14, scale = 4)
    private BigDecimal cantidad;

    @Column(name = "id_rubro_bien")
    private Integer idRubroBien;

    @Column(name="CANTIDAD_ORIGINAL", precision = 14, scale = 4)
    private BigDecimal cantidadOriginal;

    @Column(name="PRECIO_UNITARIO", precision = 14, scale = 4)
    private BigDecimal precioUnitario;

    @Column(name="PRECIO_TOTAL", precision = 14, scale = 4)
    private BigDecimal precioTotal;

    @Column(name="PRECIO_TOTAL_ORIGINAL", precision = 14, scale = 4)
    private BigDecimal precioTotalOriginal;

    @Column(name="INDICADOR_IMPUESTO", length = 2)
    private String indicadorImpuesto;

	@Column(name="FECHA_ENTREGA")
	private Date fechaEntrega;

    @Column(name="IND_SELECCIONADO")
    private String indSeleccionado;

    @Column(name="ID_LICITACION_DETALLE")
    private Integer idLicitacionDetalle;

    @Column(name="CONDICION_PAGO", length=5)
    private String condicionPago;

    @Column(name="SOCIEDAD", length=20)
    private String sociedad;

    @Column(name="CLASE_DOC", length=100)
    private String claseDoc;

    @Column(name="VALOR_IMPUESTO", precision = 14, scale = 4)
    private BigDecimal valorImpuesto;

    public BigDecimal getValorImpuesto() {
        return valorImpuesto;
    }

    public void setValorImpuesto(BigDecimal valorImpuesto) {
        this.valorImpuesto = valorImpuesto;
    }

    public String getClaseDoc() {
        return claseDoc;
    }

    public void setClaseDoc(String claseDoc) {
        this.claseDoc = claseDoc;
    }

    public String getSociedad() {
        return sociedad;
    }

    public void setSociedad(String sociedad) {
        this.sociedad = sociedad;
    }

    public String getCondicionPago() {
        return condicionPago;
    }

    public void setCondicionPago(String condicionPago) {
        this.condicionPago = condicionPago;
    }

    //    @OneToMany(mappedBy = "ordenCompraDetalle", targetEntity = OrdenCompraDetalleTexto.class, fetch = FetchType.LAZY)
//    private List<OrdenCompraDetalleTexto> ordenCompraDetalleTextoList;
//
//    @OneToMany(mappedBy = "ordenCompraDetalle", targetEntity = OrdenCompraDetalleTextoRegistroInfo.class, fetch = FetchType.LAZY)
//    private List<OrdenCompraDetalleTextoRegistroInfo> ordenCompraDetalleTextoRegistroInfoList;
//
//    @OneToMany(mappedBy = "ordenCompraDetalle", targetEntity = OrdenCompraDetalleTextoMaterialAmpliado.class, fetch = FetchType.LAZY)
//    private List<OrdenCompraDetalleTextoMaterialAmpliado> ordenCompraDetalleTextoMaterialAmpliadoList;

    // Campos adicionales - Silvestre
    @Column(name="ID_AMARRE")
    private Integer idAmarre;

    @Column(name="ID_AMARRE_SC")
    private Integer idAmarreSC;

    @Column(name="OP_SOLICITUD_COMPRA")
    private Integer opSolicitudCompra;

    @Column(name="CODIGO_PRODUCTO", length = 20)
    private String codigoProducto;

    @Column(name="KARDEX")
    private Integer kardex;

    @Column(name="DESCRIPCION_UNIDAD_MEDIDA", length = 20)
    private String descripcionUnidadMedida;

    @Column(name="OBSERVACIONES")
    private String observaciones;


    //nuevas columnas
    @Column(name="FECHA_INICIO_CONTRATO")
    private Date fechaInicioContrato;

    @Column(name="FECHA_FIN_CONTRATO")
    private Date fechaFinContrato;

    @Column(name="TIPO_EXCEPCION")
    private String tipoExcepcion;

    @Column(name="POSICION_OC", length=100)
    private String posicionOc;

    @Column(name="NRO_SOLPED")
    private String numeroSolped;
    
    public BigDecimal getPrecioTotalOriginal() {
        return precioTotalOriginal;
    }

    public void setPrecioTotalOriginal(BigDecimal precioTotalOriginal) {
        this.precioTotalOriginal = precioTotalOriginal;
    }

    public String getPosicionOc() {
        return posicionOc;
    }

    public void setPosicionOc(String posicionOc) {
        this.posicionOc = posicionOc;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OrdenCompra getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(OrdenCompra ordenCompra) {
        this.ordenCompra = ordenCompra;
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

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public String getTipoPosicion() {
        return tipoPosicion;
    }

    public void setTipoPosicion(String tipoPosicion) {
        this.tipoPosicion = tipoPosicion;
    }

    public String getCodigoSapBienServicio() {
        return codigoSapBienServicio;
    }

    public void setCodigoSapBienServicio(String codigoSapBienServicio) {
        this.codigoSapBienServicio = codigoSapBienServicio;
    }

    public String getDescripcionBienServicio() {
        return descripcionBienServicio;
    }

    public void setDescripcionBienServicio(String descripcionBienServicio) {
        this.descripcionBienServicio = descripcionBienServicio;
    }

    public String getUnidadMedidaBienServicio() {
        return unidadMedidaBienServicio;
    }

    public void setUnidadMedidaBienServicio(String unidadMedidaBienServicio) {
        this.unidadMedidaBienServicio = unidadMedidaBienServicio;
    }

    public String getCodigoSapCentro() {
        return codigoSapCentro;
    }

    public void setCodigoSapCentro(String codigoSapCentro) {
        this.codigoSapCentro = codigoSapCentro;
    }

    public String getDenominacionCentro() {
        return denominacionCentro;
    }

    public void setDenominacionCentro(String denominacionCentro) {
        this.denominacionCentro = denominacionCentro;
    }

    public String getDireccionCentro() {
        return direccionCentro;
    }

    public void setDireccionCentro(String direccionCentro) {
        this.direccionCentro = direccionCentro;
    }

    public String getCodigoSapAlmacen() {
        return codigoSapAlmacen;
    }

    public void setCodigoSapAlmacen(String codigoSapAlmacen) {
        this.codigoSapAlmacen = codigoSapAlmacen;
    }

    public String getDenominacionAlmacen() {
        return denominacionAlmacen;
    }

    public void setDenominacionAlmacen(String denominacionAlmacen) {
        this.denominacionAlmacen = denominacionAlmacen;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getIndicadorImpuesto() {
        return indicadorImpuesto;
    }

    public void setIndicadorImpuesto(String indicadorImpuesto) {
        this.indicadorImpuesto = indicadorImpuesto;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

//    public List<OrdenCompraDetalleTexto> getOrdenCompraDetalleTextoList() {
//        return ordenCompraDetalleTextoList;
//    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getIdAmarre() {
        return idAmarre;
    }

    public void setIdAmarre(Integer idAmarre) {
        this.idAmarre = idAmarre;
    }

    public Integer getIdAmarreSC() {
        return idAmarreSC;
    }

    public void setIdAmarreSC(Integer idAmarreSC) {
        this.idAmarreSC = idAmarreSC;
    }

    public Integer getOpSolicitudCompra() {
        return opSolicitudCompra;
    }

    public void setOpSolicitudCompra(Integer opSolicitudCompra) {
        this.opSolicitudCompra = opSolicitudCompra;
    }

    public Integer getIdLicitacionDetalle() {
        return idLicitacionDetalle;
    }

    public void setIdLicitacionDetalle(Integer idLicitacionDetalle) {
        this.idLicitacionDetalle = idLicitacionDetalle;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public Integer getKardex() {
        return kardex;
    }

    public void setKardex(Integer kardex) {
        this.kardex = kardex;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getDescripcionUnidadMedida() {
        return descripcionUnidadMedida;
    }

    public void setDescripcionUnidadMedida(String descripcionUnidadMedida) {
        this.descripcionUnidadMedida = descripcionUnidadMedida;
    }

    public Integer getIdRubroBien() {
        return idRubroBien;
    }

    public void setIdRubroBien(Integer idRubroBien) {
        this.idRubroBien = idRubroBien;
    }

    public BigDecimal getCantidadOriginal() {
        return cantidadOriginal;
    }

    public void setCantidadOriginal(BigDecimal cantidadOriginal) {
        this.cantidadOriginal = cantidadOriginal;
    }



    //    public void setOrdenCompraDetalleTextoList(List<OrdenCompraDetalleTexto> ordenCompraDetalleTextoList) {
//        this.ordenCompraDetalleTextoList = ordenCompraDetalleTextoList;
//    }
//
//    public List<OrdenCompraDetalleTextoRegistroInfo> getOrdenCompraDetalleTextoRegistroInfoList() {
//        return ordenCompraDetalleTextoRegistroInfoList;
//    }
//
//    public void setOrdenCompraDetalleTextoRegistroInfoList(List<OrdenCompraDetalleTextoRegistroInfo> ordenCompraDetalleTextoRegistroInfoList) {
//        this.ordenCompraDetalleTextoRegistroInfoList = ordenCompraDetalleTextoRegistroInfoList;
//    }
//
//    public List<OrdenCompraDetalleTextoMaterialAmpliado> getOrdenCompraDetalleTextoMaterialAmpliadoList() {
//        return ordenCompraDetalleTextoMaterialAmpliadoList;
//    }
//
//    public void setOrdenCompraDetalleTextoMaterialAmpliadoList(List<OrdenCompraDetalleTextoMaterialAmpliado> ordenCompraDetalleTextoMaterialAmpliadoList) {
//        this.ordenCompraDetalleTextoMaterialAmpliadoList = ordenCompraDetalleTextoMaterialAmpliadoList;
//    }


    public String getIndSeleccionado() {
        return indSeleccionado;
    }

    public void setIndSeleccionado(String indSeleccionado) {
        this.indSeleccionado = indSeleccionado;
    }

    public String getNumeroSolped() {
        return numeroSolped;
    }

    public void setNumeroSolped(String numeroSolped) {
        this.numeroSolped = numeroSolped;
    }


    @Override
    public String toString() {
        return "OrdenCompraDetalle{" +
                "id=" + id +
                ", ordenCompra=" + ordenCompra +
                ", idOrdenCompra=" + idOrdenCompra +
                ", numeroOrdenCompra='" + numeroOrdenCompra + '\'' +
                ", posicion='" + posicion + '\'' +
                ", tipoPosicion='" + tipoPosicion + '\'' +
                ", codigoSapBienServicio='" + codigoSapBienServicio + '\'' +
                ", descripcionBienServicio='" + descripcionBienServicio + '\'' +
                ", unidadMedidaBienServicio='" + unidadMedidaBienServicio + '\'' +
                ", codigoSapCentro='" + codigoSapCentro + '\'' +
                ", denominacionCentro='" + denominacionCentro + '\'' +
                ", direccionCentro='" + direccionCentro + '\'' +
                ", codigoSapAlmacen='" + codigoSapAlmacen + '\'' +
                ", denominacionAlmacen='" + denominacionAlmacen + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", precioTotal=" + precioTotal +
                ", indicadorImpuesto='" + indicadorImpuesto + '\'' +
                ", fechaEntrega=" + fechaEntrega +
                ", opSolicitudCompra=" + opSolicitudCompra +
                ", idAmarre=" + idAmarre +
                ", idAmarreSC=" + idAmarreSC +
                ", codigoProducto='" + codigoProducto + '\'' +
                ", kardex=" + kardex +
                ", observaciones='" + observaciones + '\'' +
                ", descripcionUnidadMedida='" + descripcionUnidadMedida + '\'' +
                ", indSeleccionado='" + indSeleccionado + '\'' +
                ", posicionOc='" + posicionOc + '\'' +
                ", sociedad='" + sociedad + '\'' +
                ", claseDoc='" + claseDoc + '\'' +
//                ", ordenCompraDetalleTextoList=" + ordenCompraDetalleTextoList +
//                ", ordenCompraDetalleTextoRegistroInfoList=" + ordenCompraDetalleTextoRegistroInfoList +
//                ", ordenCompraDetalleTextoMaterialAmpliadoList=" + ordenCompraDetalleTextoMaterialAmpliadoList +
                '}';
    }

    public String getLugarEntregaDetalle() {
        return lugarEntregaDetalle;
    }

    public void setLugarEntregaDetalle(String lugarEntregaDetalle) {
        this.lugarEntregaDetalle = lugarEntregaDetalle;
    }
}