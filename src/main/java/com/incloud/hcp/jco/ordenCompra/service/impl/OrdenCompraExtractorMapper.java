package com.incloud.hcp.jco.ordenCompra.service.impl;

import com.incloud.hcp.config.ToDate;
import com.incloud.hcp.domain.*;
import com.incloud.hcp.enums.OrdenCompraEstadoSapEnum;
import com.incloud.hcp.enums.OrdenCompraTipoEnum;
import com.incloud.hcp.jco.comprobanteRetencion.dto.SapTableCRHeaderDto;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.util.Utils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//import com.sap.conn.jco.JCoParameterList;
//import com.sap.conn.jco.JCoTable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class OrdenCompraExtractorMapper {

    public static List<OrdenCompra> getOrdenCompraList(NodeList headerList) throws ParseException {
        List<OrdenCompra> ordenCompras = new ArrayList<>();
        if (headerList != null) {
            for (int i = 0; i < headerList.getLength(); i++) {
                if (headerList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) headerList.item(i);
                    OrdenCompra ordenCompra = new OrdenCompra();
                    String fechaEntrega = Utils.getValueNodo(elemento, "EINDT");
                    String fechaRegistro = Utils.getValueNodo(elemento, "CREAT_DATE");
                    String fechaModificacion = Utils.getValueNodo(elemento, "UDATE");
                    /*Timestamp fechaEntrega1=new Timestamp(fechaEntrega.getTime());
                    Timestamp fechaRegistro1=new Timestamp(fechaRegistro.getTime());
                    Timestamp fechaModificacion1=new Timestamp(fechaModificacion.getTime());*/
                    ordenCompra.setNumeroOrdenCompra(Utils.getValueNodo(elemento, "PO_NUMBER"));
                    ordenCompra.setIdTipoOrdenCompra(!(Utils.getValueNodo(elemento, "DOC_TYPE").equalsIgnoreCase("ZC03")) ? OrdenCompraTipoEnum.MATERIAL.getId() : OrdenCompraTipoEnum.SERVICIO.getId());
                    ordenCompra.setIndicadorContratoMarco("0");
                    ordenCompra.setEstadoSap((Optional.ofNullable(Utils.getValueNodo(elemento, "DELETE_IND")).orElse("").equalsIgnoreCase(OrdenCompraEstadoSapEnum.LIBERADA.getCodigo())) ? OrdenCompraEstadoSapEnum.ANULADA.getCodigo() : Utils.getValueNodo(elemento, "PO_REL_IND").equalsIgnoreCase("") ? OrdenCompraEstadoSapEnum.ANULADA.getCodigo() : Utils.getValueNodo(elemento, "PO_REL_IND"));
                    ordenCompra.setCodigoClaseOrdenCompra(Utils.getValueNodo(elemento, "DOC_TYPE"));
                    ordenCompra.setClaseOrdenCompra(Utils.getValueNodo(elemento, "BATXT"));
                    ordenCompra.setSociedad(Utils.getValueNodo(elemento, "COMP_CODE"));
                    ordenCompra.setCompradorUsuarioSap(Utils.getValueNodo(elemento, "CREATED_BY"));
                    ordenCompra.setCompradorNombre(Utils.getValueNodo(elemento, "NAME_TEXT"));
                    ordenCompra.setUltimoLiberadorUsuarioSap(Utils.getValueNodo(elemento, "USERNAME"));
                    ordenCompra.setProveedorCodigoSap(Utils.getValueNodo(elemento, "VENDOR"));
                    ordenCompra.setProveedorRuc(Utils.getValueNodo(elemento, "STCD1"));
                    ordenCompra.setProveedorRazonSocial(Utils.getValueNodo(elemento, "NAME1"));
                    ordenCompra.setCodigoMondeda(Utils.getValueNodo(elemento, "CURRENCY"));
                    ordenCompra.setTotal(Optional.ofNullable(new BigDecimal(Utils.getValueNodo(elemento, "RLWRT"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
                    ordenCompra.setCondicionPago(Utils.getValueNodo(elemento, "PMNTTRMS"));
                    ordenCompra.setCondicionPagoDescripcion(Utils.getValueNodo(elemento, "TEXT1"));

                    Optional<Date> dateEntrega = ToDate.getDateFromString(fechaEntrega, "yyyy-MM-dd");
                    Optional<Date> fechaRegistro1 = ToDate.getDateFromString(fechaRegistro, "yyyy-MM-dd");
                    Optional<Date> fechaModificacion1 = ToDate.getDateFromString(fechaModificacion, "yyyy-MM-dd");
                    if(!fechaEntrega.equals("0000-00-00")) {
                        ordenCompra.setFechaEntrega(dateEntrega.get());
                    }
                    if(!fechaRegistro.equals("0000-00-00")) {
                        ordenCompra.setFechaRegistro(fechaRegistro1.get());
                    }
                    if(!fechaModificacion.equals("0000-00-00")) {

                        ordenCompra.setFechaModificacion(fechaModificacion1.get());
                    }
                    ordenCompra.setHoraModificacion(DateUtils.toTime(Utils.getValueNodo(elemento, "UTIME")));
                    ordenCompra.setLugarEntrega(Utils.getValueNodo(elemento, "DELIVDIR1"));
                    ordenCompra.setAutorizadorFechaLiberacion(Utils.getValueNodo(elemento, "AUTORIZADO_POR"));

                    ordenCompras.add(ordenCompra);

                }
            }
        }
        return ordenCompras;
    }

    public static List<OrdenCompraTextoCabecera> getOrdenCompraTextoCabeceraList(NodeList headerList) {

        List<OrdenCompraTextoCabecera> ordenCompraTextoCabeceraList = new ArrayList<>();
        if (headerList != null) {
            for (int i = 0; i < headerList.getLength(); i++) {
                if (headerList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) headerList.item(i);
                    OrdenCompraTextoCabecera ordenCompraTextoCabecera = new OrdenCompraTextoCabecera();
                    ordenCompraTextoCabecera.setNumeroOrdenCompra(Utils.getValueNodo(elemento, "PO_NUMBER"));
                    ordenCompraTextoCabecera.setLinea(Utils.getValueNodo(elemento, "TDLINE"));
                    ordenCompraTextoCabeceraList.add(ordenCompraTextoCabecera);

                }
            }
        }
        return ordenCompraTextoCabeceraList;
    }

    public static List<OrdenCompraDetalle> getOrdenCompraDetalleList(NodeList headerList) throws ParseException {
        List<OrdenCompraDetalle> ordenCompraDetalleList = new ArrayList<>();
        if (headerList != null) {
            for (int i = 0; i < headerList.getLength(); i++) {
                if (headerList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) headerList.item(i);
                    OrdenCompraDetalle ordenCompraDetalle = new OrdenCompraDetalle();
                    ordenCompraDetalle.setNumeroOrdenCompra(Utils.getValueNodo(elemento, "PO_NUMBER"));
                    ordenCompraDetalle.setPosicion(Utils.getValueNodo(elemento, "PO_ITEM"));
                    ordenCompraDetalle.setCodigoSapBienServicio(Utils.getValueNodo(elemento, "MATERIAL"));
                    ordenCompraDetalle.setDescripcionBienServicio(Utils.getValueNodo(elemento, "SHORT_TEXT"));
                    ordenCompraDetalle.setUnidadMedidaBienServicio(Utils.getValueNodo(elemento, "PO_UNIT"));
                    ordenCompraDetalle.setCodigoSapCentro(Utils.getValueNodo(elemento, "PLANT"));
                    ordenCompraDetalle.setDenominacionCentro(Utils.getValueNodo(elemento, "NAME1"));
                    ordenCompraDetalle.setDireccionCentro(Utils.getValueNodo(elemento, "STRAS"));
                    ordenCompraDetalle.setCodigoSapAlmacen(Utils.getValueNodo(elemento, "STGE_LOC"));
                    ordenCompraDetalle.setDenominacionAlmacen(Utils.getValueNodo(elemento, "LGOBE"));
                    ordenCompraDetalle.setCantidad(Optional.ofNullable(new BigDecimal(Utils.getValueNodo(elemento, "QUANTITY"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
                    ordenCompraDetalle.setPrecioUnitario(Optional.ofNullable(new BigDecimal(Utils.getValueNodo(elemento, "NET_PRICE"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
//                ordenCompraDetalle.setPrecioTotal(Optional.ofNullable(table.getBigDecimal("PRICE_UNIT")).orElse(new BigDecimal("1.00")).setScale(4, RoundingMode.HALF_UP)); // GUARDA LA CANTIDAD BASE TEMPORALMENTE EN EL CAMPO "PRECIO TOTAL"
                    BigDecimal cantidadBase = new BigDecimal(Utils.getValueNodo(elemento, "PRICE_UNIT"));
                    ordenCompraDetalle.setPrecioTotal(((cantidadBase != null && cantidadBase.compareTo(BigDecimal.ZERO) != 0) ? cantidadBase : BigDecimal.ONE).setScale(4, RoundingMode.HALF_UP)); // GUARDA LA CANTIDAD BASE TEMPORALMENTE EN EL CAMPO "PRECIO TOTAL"
                    ordenCompraDetalle.setIndicadorImpuesto(Utils.getValueNodo(elemento, "TAX_CODE"));
                    String fechaEntrega = Utils.getValueNodo(elemento, "EINDT");
                  //  Timestamp fechaEntrega1=new Timestamp(fechaEntrega.getTime());
                    ordenCompraDetalle.setValorImpuesto(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"KBETR")));
                    Optional<Date> dateEntrega = ToDate.getDateFromString(fechaEntrega, "yyyy-MM-dd");
                    if(!fechaEntrega.equals("0000-00-00")) {
                        ordenCompraDetalle.setFechaEntrega(dateEntrega.get());
                    }

                    ordenCompraDetalleList.add(ordenCompraDetalle);
                }
            }
        }
        return ordenCompraDetalleList;

    }

    public static List<OrdenCompraDetalleTexto> getOrdenCompraDetalleTextoList(NodeList headerList) {
        List<OrdenCompraDetalleTexto> ordenCompraDetalleTextList = new ArrayList<>();

        if (headerList != null) {
            for (int i = 0; i < headerList.getLength(); i++) {
                if (headerList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) headerList.item(i);
                    OrdenCompraDetalleTexto ordenCompraDetalleTexto = new OrdenCompraDetalleTexto();

                    ordenCompraDetalleTexto.setNumeroOrdenCompra(Utils.getValueNodo(elemento, "PO_NUMBER"));
                    ordenCompraDetalleTexto.setPosicion(Utils.getValueNodo(elemento, "PO_ITEM"));
                    ordenCompraDetalleTexto.setLinea(Utils.getValueNodo(elemento, "TDLINE"));

                    ordenCompraDetalleTextList.add(ordenCompraDetalleTexto);
                }
            }
        }
        return ordenCompraDetalleTextList;

    }

    public static List<OrdenCompraDetalleTextoRegistroInfo> getOrdenCompraDetalleTextoRegistroInfoList(NodeList headerList) {
        List<OrdenCompraDetalleTextoRegistroInfo> ordenCompraDetalleTextoRegistroInfoList = new ArrayList<>();
        if (headerList != null) {
            for (int i = 0; i < headerList.getLength(); i++) {
                if (headerList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) headerList.item(i);
                    OrdenCompraDetalleTextoRegistroInfo ordenCompraDetalleTextoRegistroInfo = new OrdenCompraDetalleTextoRegistroInfo();

                    ordenCompraDetalleTextoRegistroInfo.setNumeroOrdenCompra(Utils.getValueNodo(elemento, "PO_NUMBER"));
                    ordenCompraDetalleTextoRegistroInfo.setPosicion(Utils.getValueNodo(elemento, "PO_ITEM"));
                    ordenCompraDetalleTextoRegistroInfo.setLinea(Utils.getValueNodo(elemento, "TDLINE"));

                    ordenCompraDetalleTextoRegistroInfoList.add(ordenCompraDetalleTextoRegistroInfo);
                }
            }
        }
        return ordenCompraDetalleTextoRegistroInfoList;
    }


    public static List<OrdenCompraDetalleTextoMaterialAmpliado> getOrdenCompraDetalleTextoMaterialAmpliadoList(NodeList headerList) {
        List<OrdenCompraDetalleTextoMaterialAmpliado> ordenCompraDetalleTextoMaterialAmpliadoList = new ArrayList<>();
        if (headerList != null) {
            for (int i = 0; i < headerList.getLength(); i++) {
                if (headerList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) headerList.item(i);
                    OrdenCompraDetalleTextoMaterialAmpliado ordenCompraDetalleTextoMaterialAmpliado = new OrdenCompraDetalleTextoMaterialAmpliado();

                    ordenCompraDetalleTextoMaterialAmpliado.setNumeroOrdenCompra(Utils.getValueNodo(elemento, "PO_NUMBER"));
                    ordenCompraDetalleTextoMaterialAmpliado.setPosicion(Utils.getValueNodo(elemento, "PO_ITEM"));
                    ordenCompraDetalleTextoMaterialAmpliado.setLinea(Utils.getValueNodo(elemento, "TDLINE"));

                    ordenCompraDetalleTextoMaterialAmpliadoList.add(ordenCompraDetalleTextoMaterialAmpliado);
                }
            }
        }
        return ordenCompraDetalleTextoMaterialAmpliadoList;
    }

        //   private JCoParameterList jCoParameterList;

//    public static OrdenCompraExtractorMapper newMapper(JCoParameterList exportParameterList) {
//        return new OrdenCompraExtractorMapper(exportParameterList);
//    }
//
//    private OrdenCompraExtractorMapper(JCoParameterList jCoParameterList) {
//        this.jCoParameterList = jCoParameterList;
//    }
//
//    public List<OrdenCompra> getOrdenCompraList() {
//        List<OrdenCompra> ordenCompraList = new ArrayList<>();
//        JCoTable table = jCoParameterList.getTable("PO_HEADER");
//
//        if (table != null && !table.isEmpty()) {
//            do {
//                OrdenCompra ordenCompra = new OrdenCompra();
//
//                ordenCompra.setNumeroOrdenCompra(table.getString("PO_NUMBER"));
//                ordenCompra.setIdTipoOrdenCompra(!(table.getString("DOC_TYPE").equalsIgnoreCase("ZC03")) ? OrdenCompraTipoEnum.MATERIAL.getId() : OrdenCompraTipoEnum.SERVICIO.getId());
//                ordenCompra.setIndicadorContratoMarco("0");
//                ordenCompra.setEstadoSap((Optional.ofNullable(table.getString("DELETE_IND")).orElse("").equalsIgnoreCase(OrdenCompraEstadoSapEnum.LIBERADA.getCodigo())) ? OrdenCompraEstadoSapEnum.ANULADA.getCodigo() : (table.getString("PO_REL_IND").equalsIgnoreCase("") ? OrdenCompraEstadoSapEnum.ANULADA.getCodigo() : table.getString("PO_REL_IND") ));
//                ordenCompra.setCodigoClaseOrdenCompra(table.getString("DOC_TYPE"));
//                ordenCompra.setClaseOrdenCompra(table.getString("BATXT"));
//                ordenCompra.setSociedad(table.getString("COMP_CODE"));
//                ordenCompra.setCompradorUsuarioSap(table.getString("CREATED_BY"));
//                ordenCompra.setCompradorNombre(table.getString("NAME_TEXT"));
//                ordenCompra.setUltimoLiberadorUsuarioSap(table.getString("USERNAME"));
//                ordenCompra.setProveedorCodigoSap(table.getString("VENDOR"));
//                ordenCompra.setProveedorRuc(table.getString("STCD1"));
//                ordenCompra.setProveedorRazonSocial(table.getString("NAME1"));
//                ordenCompra.setCodigoMondeda(table.getString("CURRENCY"));
//                ordenCompra.setTotal(Optional.ofNullable(table.getBigDecimal("RLWRT")).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
//                ordenCompra.setCondicionPago(table.getString("PMNTTRMS"));
//                ordenCompra.setCondicionPagoDescripcion(table.getString("TEXT1"));
//                ordenCompra.setFechaEntrega(table.getDate("EINDT"));
//                ordenCompra.setFechaRegistro(table.getDate("CREAT_DATE"));
//                ordenCompra.setFechaModificacion(table.getDate("UDATE"));
//                ordenCompra.setHoraModificacion(DateUtils.dateToTime(table.getDate("UTIME")));
//                ordenCompra.setLugarEntrega(table.getString("DELIVDIR1"));
//                ordenCompra.setAutorizadorFechaLiberacion(table.getString("AUTORIZADO_POR"));
//
//                ordenCompraList.add(ordenCompra);
//            } while (table.nextRow());
//        }
//        return ordenCompraList;
//    }
//
//
//    public List<OrdenCompraTextoCabecera> getOrdenCompraTextoCabeceraList() {
//        List<OrdenCompraTextoCabecera> ordenCompraTextoCabeceraList = new ArrayList<>();
//        JCoTable table = jCoParameterList.getTable("PO_TEXTO_CABECERA");
//
//        if (table != null && !table.isEmpty()) {
//            do {
//                OrdenCompraTextoCabecera ordenCompraTextoCabecera = new OrdenCompraTextoCabecera();
//
//                ordenCompraTextoCabecera.setNumeroOrdenCompra(table.getString("PO_NUMBER"));
//                ordenCompraTextoCabecera.setLinea(table.getString("TDLINE"));
//
//                ordenCompraTextoCabeceraList.add(ordenCompraTextoCabecera);
//            } while (table.nextRow());
//        }
//        return ordenCompraTextoCabeceraList;
//    }
//
//
//    public List<OrdenCompraDetalle> getOrdenCompraDetalleList() {
//        List<OrdenCompraDetalle> ordenCompraDetalleList = new ArrayList<>();
//        JCoTable table = jCoParameterList.getTable("PO_ITEMS_AUX");
//
//        if (table != null && !table.isEmpty()) {
//            do {
//                OrdenCompraDetalle ordenCompraDetalle = new OrdenCompraDetalle();
//
//                ordenCompraDetalle.setNumeroOrdenCompra(table.getString("PO_NUMBER"));
//                ordenCompraDetalle.setPosicion(table.getString("PO_ITEM"));
//                ordenCompraDetalle.setCodigoSapBienServicio(table.getString("MATERIAL"));
//                ordenCompraDetalle.setDescripcionBienServicio(table.getString("SHORT_TEXT"));
//                ordenCompraDetalle.setUnidadMedidaBienServicio(table.getString("PO_UNIT"));
//                ordenCompraDetalle.setCodigoSapCentro(table.getString("PLANT"));
//                ordenCompraDetalle.setDenominacionCentro(table.getString("NAME1"));
//                ordenCompraDetalle.setDireccionCentro(table.getString("STRAS"));
//                ordenCompraDetalle.setCodigoSapAlmacen(table.getString("STGE_LOC"));
//                ordenCompraDetalle.setDenominacionAlmacen(table.getString("LGOBE"));
//                ordenCompraDetalle.setCantidad(Optional.ofNullable(table.getBigDecimal("QUANTITY")).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
//                ordenCompraDetalle.setPrecioUnitario(Optional.ofNullable(table.getBigDecimal("NET_PRICE")).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
////                ordenCompraDetalle.setPrecioTotal(Optional.ofNullable(table.getBigDecimal("PRICE_UNIT")).orElse(new BigDecimal("1.00")).setScale(4, RoundingMode.HALF_UP)); // GUARDA LA CANTIDAD BASE TEMPORALMENTE EN EL CAMPO "PRECIO TOTAL"
//                BigDecimal cantidadBase = table.getBigDecimal("PRICE_UNIT");
//                ordenCompraDetalle.setPrecioTotal(((cantidadBase != null && cantidadBase.compareTo(BigDecimal.ZERO) != 0) ? cantidadBase : BigDecimal.ONE).setScale(4, RoundingMode.HALF_UP)); // GUARDA LA CANTIDAD BASE TEMPORALMENTE EN EL CAMPO "PRECIO TOTAL"
//                ordenCompraDetalle.setIndicadorImpuesto(table.getString("TAX_CODE"));
//                ordenCompraDetalle.setFechaEntrega(table.getDate("EINDT"));
//
//                ordenCompraDetalleList.add(ordenCompraDetalle);
//            } while (table.nextRow());
//        }
//        return ordenCompraDetalleList;
//    }
//
//
//    public List<OrdenCompraDetalleTexto> getOrdenCompraDetalleTextoList() {
//        List<OrdenCompraDetalleTexto> ordenCompraDetalleTextList = new ArrayList<>();
//        JCoTable table = jCoParameterList.getTable("PO_TEXT");
//
//        if (table != null && !table.isEmpty()) {
//            do {
//                OrdenCompraDetalleTexto ordenCompraDetalleTexto = new OrdenCompraDetalleTexto();
//
//                ordenCompraDetalleTexto.setNumeroOrdenCompra(table.getString("PO_NUMBER"));
//                ordenCompraDetalleTexto.setPosicion(table.getString("PO_ITEM"));
//                ordenCompraDetalleTexto.setLinea(table.getString("TDLINE"));
//
//                ordenCompraDetalleTextList.add(ordenCompraDetalleTexto);
//            } while (table.nextRow());
//        }
//        return ordenCompraDetalleTextList;
//    }
//
//
//    public List<OrdenCompraDetalleTextoRegistroInfo> getOrdenCompraDetalleTextoRegistroInfoList() {
//        List<OrdenCompraDetalleTextoRegistroInfo> ordenCompraDetalleTextoRegistroInfoList = new ArrayList<>();
//        JCoTable table = jCoParameterList.getTable("PO_TEXTO_REG_POS");
//
//        if (table != null && !table.isEmpty()) {
//            do {
//                OrdenCompraDetalleTextoRegistroInfo ordenCompraDetalleTextoRegistroInfo = new OrdenCompraDetalleTextoRegistroInfo();
//
//                ordenCompraDetalleTextoRegistroInfo.setNumeroOrdenCompra(table.getString("PO_NUMBER"));
//                ordenCompraDetalleTextoRegistroInfo.setPosicion(table.getString("PO_ITEM"));
//                ordenCompraDetalleTextoRegistroInfo.setLinea(table.getString("TDLINE"));
//
//                ordenCompraDetalleTextoRegistroInfoList.add(ordenCompraDetalleTextoRegistroInfo);
//            } while (table.nextRow());
//        }
//        return ordenCompraDetalleTextoRegistroInfoList;
//    }
//
//    public List<OrdenCompraDetalleTextoMaterialAmpliado> getOrdenCompraDetalleTextoMaterialAmpliadoList() {
//        List<OrdenCompraDetalleTextoMaterialAmpliado> ordenCompraDetalleTextoMaterialAmpliadoList = new ArrayList<>();
//        JCoTable table = jCoParameterList.getTable("PO_TEXTO_AMPL_MAT");
//
//        if (table != null && !table.isEmpty()) {
//            do {
//                OrdenCompraDetalleTextoMaterialAmpliado ordenCompraDetalleTextoMaterialAmpliado = new OrdenCompraDetalleTextoMaterialAmpliado();
//
//                ordenCompraDetalleTextoMaterialAmpliado.setNumeroOrdenCompra(table.getString("PO_NUMBER"));
//                ordenCompraDetalleTextoMaterialAmpliado.setPosicion(table.getString("PO_ITEM"));
//                ordenCompraDetalleTextoMaterialAmpliado.setLinea(table.getString("TDLINE"));
//
//                ordenCompraDetalleTextoMaterialAmpliadoList.add(ordenCompraDetalleTextoMaterialAmpliado);
//            } while (table.nextRow());
//        }
//        return ordenCompraDetalleTextoMaterialAmpliadoList;
//    }
    }


