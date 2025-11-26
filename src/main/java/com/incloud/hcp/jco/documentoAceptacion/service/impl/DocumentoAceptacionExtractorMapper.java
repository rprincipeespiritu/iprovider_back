package com.incloud.hcp.jco.documentoAceptacion.service.impl;

import com.incloud.hcp.domain.DocumentoAceptacion;
import com.incloud.hcp.jco.documentoAceptacion.dto.SapTableItemDto;
import com.incloud.hcp.util.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//import com.sap.conn.jco.JCoParameterList;
//import com.sap.conn.jco.JCoTable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class DocumentoAceptacionExtractorMapper {

    //private JCoParameterList jCoParameterList;

//    public static DocumentoAceptacionExtractorMapper newMapper(JCoParameterList exportParameterList) {
//        return new DocumentoAceptacionExtractorMapper(exportParameterList);
//    }
//
//    private DocumentoAceptacionExtractorMapper(JCoParameterList jCoParameterList) {
//        this.jCoParameterList = jCoParameterList;
//    }

    public SapTableItemDto ObtenerDataServicio(Document doc, String nroServicio, String nroOC, String posicionOC)
    {
        NodeList posiciones = doc.getElementsByTagName("T_SPEDIDO").item(0).getChildNodes();
        SapTableItemDto sapTableItemDto = new SapTableItemDto();

        for(int i = 0; i < posiciones.getLength(); i++) {
            Node posicion = posiciones.item(i);
            Element elemento = (Element) posicion;

            String NumeroOCx = Utils.getValueNodo(elemento,"EBELN");
            String PosicionOCx = Utils.getValueNodo(elemento,"EBELP");
            String NroServiciox = Utils.getValueNodo(elemento,"BELNR");

            if(NumeroOCx.equals(nroOC) && PosicionOCx.equals(posicionOC) && NroServiciox.equals(nroServicio))
            {
                sapTableItemDto.setCodigoMaterial(Utils.getValueNodo(elemento,"SRVPOS"));
                sapTableItemDto.setNumeroItem(Integer.parseInt(Utils.getValueNodo(elemento,"EXTROW")));
            }
        }

        return sapTableItemDto;
    }

    public SapTableItemDto HojasDeEntradaMercaderia(Document doc, String nroServicio, String nroOC, String posicionOC)
    {
        NodeList posiciones = doc.getElementsByTagName("T_SPEDIDO").item(0).getChildNodes();
        SapTableItemDto sapTableItemDto = new SapTableItemDto();

        for(int i = 0; i < posiciones.getLength(); i++) {
            Node posicion = posiciones.item(i);
            Element elemento = (Element) posicion;

            String NumeroOCx = Utils.getValueNodo(elemento,"EBELN");
            String PosicionOCx = Utils.getValueNodo(elemento,"EBELP");
            String NroServiciox = Utils.getValueNodo(elemento,"BELNR");

            if(NumeroOCx.equals(nroOC) && PosicionOCx.equals(posicionOC) && NroServiciox.equals(nroServicio))
            {
                sapTableItemDto.setCodigoMaterial(Utils.getValueNodo(elemento,"SRVPOS"));
                sapTableItemDto.setNumeroItem(Integer.parseInt(Utils.getValueNodo(elemento,"EXTROW")));
            }
        }

        return sapTableItemDto;
    }

    public List<SapTableItemDto> getSapTableItemDtoList(Document doc) {

        NodeList posiciones = doc.getElementsByTagName("T_HPEDIDO").item(0).getChildNodes();

        List<SapTableItemDto> sapTableItemDtoList = new ArrayList<>();

        for(int i = 0; i < posiciones.getLength(); i++) {
            Node posicion = posiciones.item(i);
            Element elemento = (Element) posicion;

            SapTableItemDto sapTableItemDto = new SapTableItemDto();

            sapTableItemDto.setSociedad(Utils.getValueNodo(elemento, "BUKRS"));
            sapTableItemDto.setValorImpuesto(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"IGVPH")));
            sapTableItemDto.setNumeroDocumentoAceptacion(Utils.getValueNodo(elemento,"BELNR"));
            sapTableItemDto.setNumeroItem(Utils.getValueNodoNum(elemento,"BUZEI").intValue());
            sapTableItemDto.setMovimiento(Utils.getValueNodo(elemento,"BWART"));
            sapTableItemDto.setNumeroOrdenCompra(Utils.getValueNodo(elemento,"EBELN"));
            sapTableItemDto.setPosicionOrdenCompra(Utils.getValueNodo(elemento,"EBELP"));
            sapTableItemDto.setNumeroGuiaProveedor(Utils.getValueNodo(elemento,"XBLNR"));
            sapTableItemDto.setCodigoMaterial(Utils.getValueNodo(elemento,"MATNR"));
            sapTableItemDto.setDescripcionMaterial(Utils.getValueNodo(elemento,"MAKTX"));
            sapTableItemDto.setDescripcionServicio(Utils.getValueNodo(elemento,"TXZ01"));
            sapTableItemDto.setUnidadMedidaMaterial(Utils.getValueNodo(elemento,"ERFME"));
            sapTableItemDto.setUnidadMedidaServicio(Utils.getValueNodo(elemento,"MEINS"));
            sapTableItemDto.setUsuarioSapRecepcion(Utils.getValueNodo(elemento,"ERNAM"));
            sapTableItemDto.setCodigoMoneda(Utils.getValueNodo(elemento,"WAERS"));
            sapTableItemDto.setCantidadAceptadaClienteMaterial(Optional.ofNullable(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"MENGE"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
            sapTableItemDto.setCantidadAceptadaClienteServicio(Optional.ofNullable(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"ACT_MENGE"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
            sapTableItemDto.setCantidadPendiente(Optional.ofNullable(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"MENGEP"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));

            sapTableItemDto.setPrecioUnitario(Optional.ofNullable(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"NETPR"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));

            sapTableItemDto.setValorRecibido(Optional.ofNullable(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"WRBTR"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
            sapTableItemDto.setValorRecibidoMonedalocal(Optional.ofNullable(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"DMBTR"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
            sapTableItemDto.setValorRecibidoServicio(Optional.ofNullable(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"ACT_WERT"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
            sapTableItemDto.setFechaEmision(Utils.getValueNodoDate(elemento,"ERDAT"));
            sapTableItemDto.setFechaAceptacion(Utils.getValueNodoDate(elemento,"BLDAT"));
            sapTableItemDto.setIndicadorImpuesto(Utils.getValueNodo(elemento,"TAX_CODE"));
            sapTableItemDto.setNumDocApectacionRelacionado(Utils.getValueNodo(elemento,"LFBNR"));
            sapTableItemDto.setNumItemRelacionado(Utils.getValueNodoNum(elemento,"LFPOS").intValue());
            sapTableItemDto.setStatus(Utils.getValueNodo(elemento,"STATUS").trim());
            sapTableItemDto.setGuiaRemision(Utils.getValueNodo(elemento,"FRBNR").trim()); // mizalo guia remision

            /*Actualizamos datos de servicios*/
            SapTableItemDto sapTableItemDtoServicio = this.ObtenerDataServicio(doc, Utils.getValueNodo(elemento,"BELNR"), Utils.getValueNodo(elemento,"EBELN"), Utils.getValueNodo(elemento,"EBELP"));
            if(sapTableItemDtoServicio != null)
            {
                if(sapTableItemDtoServicio.getCodigoMaterial() != null)
                {
                    sapTableItemDto.setCodigoMaterial(sapTableItemDtoServicio.getCodigoMaterial());
                    sapTableItemDto.setNumeroItem(sapTableItemDtoServicio.getNumeroItem());
                    sapTableItemDto.setPrecioUnitario(Optional.ofNullable(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"ACT_WERT"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
                }
            }

            sapTableItemDtoList.add(sapTableItemDto);

        }

        return sapTableItemDtoList;
    }

    public List<SapTableItemDto> getSapTableItemTdPedidoDtoList(Document doc) {

        NodeList posiciones = doc.getElementsByTagName("T_DPEDIDO").item(0).getChildNodes();

        List<SapTableItemDto> sapTableItemDtoList = new ArrayList<>();

        for(int i = 0; i < posiciones.getLength(); i++) {
            Node posicion = posiciones.item(i);
            Element elemento = (Element) posicion;

            SapTableItemDto sapTableItemDto = new SapTableItemDto();

            sapTableItemDto.setSociedad(Utils.getValueNodo(elemento, "BUKRS"));
            sapTableItemDto.setValorImpuesto(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"IGVPH")));
            sapTableItemDto.setNumeroDocumentoAceptacion(Utils.getValueNodo(elemento,"BELNR"));
            sapTableItemDto.setNumeroItem(Utils.getValueNodoNum(elemento,"BUZEI").intValue());
            sapTableItemDto.setMovimiento(Utils.getValueNodo(elemento,"BWART"));
            sapTableItemDto.setNumeroOrdenCompra(Utils.getValueNodo(elemento,"EBELN"));
            sapTableItemDto.setPosicionOrdenCompra(Utils.getValueNodo(elemento,"EBELP"));
            sapTableItemDto.setNumeroGuiaProveedor(Utils.getValueNodo(elemento,"XBLNR"));
            sapTableItemDto.setCodigoMaterial(Utils.getValueNodo(elemento,"MATNR"));
            sapTableItemDto.setDescripcionMaterial(Utils.getValueNodo(elemento,"MAKTX"));
            sapTableItemDto.setDescripcionServicio(Utils.getValueNodo(elemento,"TXZ01"));
            sapTableItemDto.setUnidadMedidaMaterial(Utils.getValueNodo(elemento,"ERFME"));
            sapTableItemDto.setUnidadMedidaServicio(Utils.getValueNodo(elemento,"MEINS"));
            sapTableItemDto.setUsuarioSapRecepcion(Utils.getValueNodo(elemento,"ERNAM"));
            sapTableItemDto.setCodigoMoneda(Utils.getValueNodo(elemento,"WAERS"));
            sapTableItemDto.setCantidadAceptadaClienteMaterial(Optional.ofNullable(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"MENGE"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
            sapTableItemDto.setCantidadAceptadaClienteServicio(Optional.ofNullable(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"ACT_MENGE"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
            sapTableItemDto.setCantidadPendiente(Optional.ofNullable(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"MENGEP"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));

            sapTableItemDto.setPrecioUnitario(Optional.ofNullable(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"NETPR"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));

            sapTableItemDto.setValorRecibido(Optional.ofNullable(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"WRBTR"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
            sapTableItemDto.setValorRecibidoMonedalocal(Optional.ofNullable(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"DMBTR"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
            sapTableItemDto.setValorRecibidoServicio(Optional.ofNullable(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"ACT_WERT"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
            sapTableItemDto.setFechaEmision(Utils.getValueNodoDate(elemento,"ERDAT"));
            sapTableItemDto.setFechaAceptacion(Utils.getValueNodoDate(elemento,"BLDAT"));
            sapTableItemDto.setIndicadorImpuesto(Utils.getValueNodo(elemento,"TAX_CODE"));
            sapTableItemDto.setNumDocApectacionRelacionado(Utils.getValueNodo(elemento,"LFBNR"));
            sapTableItemDto.setNumItemRelacionado(Utils.getValueNodoNum(elemento,"LFPOS").intValue());
            sapTableItemDto.setStatus(Utils.getValueNodo(elemento,"STATUS").trim());
            //122

            /*Actualizamos datos de servicios*/
            SapTableItemDto sapTableItemDtoServicio = this.HojasDeEntradaMercaderia(doc, Utils.getValueNodo(elemento,"BELNR"), Utils.getValueNodo(elemento,"EBELN"), Utils.getValueNodo(elemento,"EBELP"));
            if(sapTableItemDtoServicio != null)
            {
                if(sapTableItemDtoServicio.getCodigoMaterial() != null)
                {
                    sapTableItemDto.setCodigoMaterial(sapTableItemDtoServicio.getCodigoMaterial());
                    sapTableItemDto.setNumeroItem(sapTableItemDtoServicio.getNumeroItem());
                    sapTableItemDto.setPrecioUnitario(Optional.ofNullable(BigDecimal.valueOf(Utils.getValueNodoNum(elemento,"ACT_WERT"))).orElse(BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP));
                }
            }

            sapTableItemDtoList.add(sapTableItemDto);

        }

        return sapTableItemDtoList;
    }



}