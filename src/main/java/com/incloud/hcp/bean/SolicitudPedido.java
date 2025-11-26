package com.incloud.hcp.bean;

import com.incloud.hcp.domain.BienServicio;
import com.incloud.hcp.domain.CentroAlmacen;
import com.incloud.hcp.domain.UnidadMedida;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by USER on 17/09/2017.
 */
public class SolicitudPedido implements Serializable {

    private static final long serialVersionUID = 1L;

    private String solicitudPedido;
    private String posicion;
    private int idClaseDocumento;
    private String descripcionClaseDocumento;
    private Integer idUnidadMedida;
    private String nroParte;
    private BienServicio bienServicio;
    private BigDecimal cantidad;
    private String estado;
    private String grupoArticulo;

    private String sapClaseDocumento;
    private String sapBienServicio;
    private String sapUnidadMedida;
    private String sapCentro;
    private String sapAlmacen;
    private Date sapfechaEntrega;

    private CentroAlmacen centro;
    private CentroAlmacen almacen;
    private UnidadMedida unidadMedida;
    private String rucProveedor;

    // adicionales silvestre

    private Integer op;
    private String serie;
    private int numero;
    private String agendaNombre;
    private String fecha;
    private String observaciones;
    private String centroCosto;
    private String unidadGestion;
    private String unidadProyecto;
    private Integer id_Amarre;
    private String codigoProducto;
    private Integer kardex;
    private String nombreProducto;
    private String centroCostoDetalle;
    private String unidadGestionDetalle;
    private String unidadProyectoDetalle;
    private BigDecimal Precio;
    private String Moneda;
    private int idMoneda;
    private String estadoSolped;
   // private String unidadMedida;
    private String sociedad;
    private String areaSolicitante;

    public String getSapRubroBien() {
        return sapRubroBien;
    }

    public void setSapRubroBien(String sapRubroBien) {
        this.sapRubroBien = sapRubroBien;
    }

    private String sapRubroBien;

    public SolicitudPedido() {
    }

/*    public SolicitudPedido(BienServicio bienServicio) {
        this.bienServicio = bienServicio;
    }*/

    public SolicitudPedido(String posicion,
                           String solicitudPedido,
                           int idClaseDocumento,
                           String descripcionClaseDocumento,
                           BienServicio bienServicio,
                           BigDecimal cantidad,
                           String estado) {
        this.posicion = posicion;
        this.solicitudPedido = solicitudPedido;
        this.idClaseDocumento = idClaseDocumento;
        this.descripcionClaseDocumento = descripcionClaseDocumento;
        this.bienServicio = bienServicio;
        this.cantidad = cantidad;
        this.estado = estado;
    }

    public String getMoneda() {
        return Moneda;
    }

    public void setMoneda(String moneda) {
        Moneda = moneda;
    }

    public int getIdMoneda() {
        return idMoneda;
    }

    public void setIdMoneda(int idMoneda) {
        this.idMoneda = idMoneda;
    }

    public String getSolicitudPedido() {
        return solicitudPedido;
    }

    public void setSolicitudPedido(String solicitudPedido) {
        this.solicitudPedido = solicitudPedido;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public int getIdClaseDocumento() {
        return idClaseDocumento;
    }

    public void setIdClaseDocumento(int idClaseDocumento) {
        this.idClaseDocumento = idClaseDocumento;
    }

    public String getDescripcionClaseDocumento() {
        return descripcionClaseDocumento;
    }

    public void setDescripcionClaseDocumento(String descripcionClaseDocumento) {
        this.descripcionClaseDocumento = descripcionClaseDocumento;
    }

    public String getNroParte() {
        return nroParte;
    }

    public void setNroParte(String nroParte) {
        this.nroParte = nroParte;
    }

    public BienServicio getBienServicio() {
        return bienServicio;
    }

    public void setBienServicio(BienServicio bienServicio) {
        this.bienServicio = bienServicio;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }



    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getGrupoArticulo() {
        return grupoArticulo;
    }

    public void setGrupoArticulo(String grupoArticulo) {
        this.grupoArticulo = grupoArticulo;
    }

    public String getSapClaseDocumento() {
        return sapClaseDocumento;
    }

    public void setSapClaseDocumento(String sapClaseDocumento) {
        this.sapClaseDocumento = sapClaseDocumento;
    }

    public String getSapBienServicio() {
        return sapBienServicio;
    }

    public void setSapBienServicio(String sapBienServicio) {
        this.sapBienServicio = sapBienServicio;
    }

    public String getSapUnidadMedida() {
        return sapUnidadMedida;
    }

    public void setSapUnidadMedida(String sapUnidadMedida) {
        this.sapUnidadMedida = sapUnidadMedida;
    }

    public String getSapCentro() {
        return sapCentro;
    }

    public void setSapCentro(String sapCentro) {
        this.sapCentro = sapCentro;
    }

    public String getSapAlmacen() {
        return sapAlmacen;
    }

    public void setSapAlmacen(String sapAlmacen) {
        this.sapAlmacen = sapAlmacen;
    }

    public Date getSapfechaEntrega() {
        return sapfechaEntrega;
    }

    public void setSapfechaEntrega(Date sapfechaEntrega) {
        this.sapfechaEntrega = sapfechaEntrega;
    }

    public CentroAlmacen getCentro() {
        return centro;
    }

    public void setCentro(CentroAlmacen centro) {
        this.centro = centro;
    }

    public CentroAlmacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(CentroAlmacen almacen) {
        this.almacen = almacen;
    }

    public Integer getIdUnidadMedida() {
        return idUnidadMedida;
    }

    public void setIdUnidadMedida(Integer idUnidadMedida) {
        this.idUnidadMedida = idUnidadMedida;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getRucProveedor() {
        return rucProveedor;
    }

    public void setRucProveedor(String rucProveedor) {
        this.rucProveedor = rucProveedor;
    }

    public Integer getOp() {
        return op;
    }

    public void setOp(Integer op) {
        this.op = op;
    }

    public BigDecimal getPrecio() {
        return Precio;
    }

    public void setPrecio(BigDecimal precio) {
        Precio = precio;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getAgendaNombre() {
        return agendaNombre;
    }

    public void setAgendaNombre(String agendaNombre) {
        this.agendaNombre = agendaNombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    public String getUnidadGestion() {
        return unidadGestion;
    }

    public void setUnidadGestion(String unidadGestion) {
        this.unidadGestion = unidadGestion;
    }

    public String getUnidadProyecto() {
        return unidadProyecto;
    }

    public void setUnidadProyecto(String unidadProyecto) {
        this.unidadProyecto = unidadProyecto;
    }

    public Integer getId_Amarre() {
        return id_Amarre;
    }

    public void setId_Amarre(Integer id_Amarre) {
        this.id_Amarre = id_Amarre;
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

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCentroCostoDetalle() {
        return centroCostoDetalle;
    }

    public void setCentroCostoDetalle(String centroCostoDetalle) {
        this.centroCostoDetalle = centroCostoDetalle;
    }

    public String getUnidadGestionDetalle() {
        return unidadGestionDetalle;
    }

    public void setUnidadGestionDetalle(String unidadGestionDetalle) {
        this.unidadGestionDetalle = unidadGestionDetalle;
    }

    public String getUnidadProyectoDetalle() {
        return unidadProyectoDetalle;
    }

    public void setUnidadProyectoDetalle(String unidadProyectoDetalle) {
        this.unidadProyectoDetalle = unidadProyectoDetalle;
    }

    public String getEstadoSolped() {
        return estadoSolped;
    }

    public void setEstadoSolped(String estadoSolped) {
        this.estadoSolped = estadoSolped;
    }

    public String getSociedad() {
        return sociedad;
    }

    public void setSociedad(String sociedad) {
        this.sociedad = sociedad;
    }

    @Override
    public String toString() {
        return "SolicitudPedido{" +
                "solicitudPedido='" + solicitudPedido + '\'' +
                ", posicion='" + posicion + '\'' +
                ", idClaseDocumento=" + idClaseDocumento +
                ", descripcionClaseDocumento='" + descripcionClaseDocumento + '\'' +
                ", idUnidadMedida=" + idUnidadMedida +
                ", nroParte='" + nroParte + '\'' +
                ", bienServicio=" + bienServicio +
                ", cantidad=" + cantidad +
                ", estado='" + estado + '\'' +
                ", grupoArticulo='" + grupoArticulo + '\'' +
                ", sapClaseDocumento='" + sapClaseDocumento + '\'' +
                ", sapBienServicio='" + sapBienServicio + '\'' +
                ", sapUnidadMedida='" + sapUnidadMedida + '\'' +
                ", sapCentro='" + sapCentro + '\'' +
                ", sapAlmacen='" + sapAlmacen + '\'' +
                ", sapfechaEntrega=" + sapfechaEntrega +
                ", centro=" + centro +
                ", almacen=" + almacen +
                ", unidadMedida=" + unidadMedida +
                ", rucProveedor='" + rucProveedor + '\'' +
                ", op='" + op + '\'' +
                ", serie='" + serie + '\'' +
                ", numero=" + numero +
                ", agendaNombre='" + agendaNombre + '\'' +
                ", fecha='" + fecha + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", centroCosto='" + centroCosto + '\'' +
                ", unidadGestion='" + unidadGestion + '\'' +
                ", unidadProyecto='" + unidadProyecto + '\'' +
                ", id_Amarre=" + id_Amarre +
                ", codigoProducto='" + codigoProducto + '\'' +
                ", kardex=" + kardex +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", centroCostoDetalle='" + centroCostoDetalle + '\'' +
                ", unidadGestionDetalle='" + unidadGestionDetalle + '\'' +
                ", unidadProyectoDetalle='" + unidadProyectoDetalle + '\'' +
                ", precio='" + Precio  + '\'' +
                ", moneda ='" + Moneda  + '\'' +
                ", idMoneda ='" + idMoneda  + '\'' +
                ", estadoSolped ='" + estadoSolped  + '\'' +
                ", sociedad ='" + sociedad  + '\'' +
                '}';
    }

    public String getAreaSolicitante() {
        return areaSolicitante;
    }

    public void setAreaSolicitante(String areaSolicitante) {
        this.areaSolicitante = areaSolicitante;
    }
}
