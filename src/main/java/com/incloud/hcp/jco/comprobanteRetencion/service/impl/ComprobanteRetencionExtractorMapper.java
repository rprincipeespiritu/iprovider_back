package com.incloud.hcp.jco.comprobanteRetencion.service.impl;

import com.incloud.hcp.jco.comprobanteRetencion.dto.*;
import com.incloud.hcp.util.Utils;
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

public class ComprobanteRetencionExtractorMapper {

    public static List<SapTableCRHeaderDto> getHeaderDtoList(NodeList headerList) {
        List<SapTableCRHeaderDto> headerDtoList = new ArrayList<>();
        if (headerList != null) {
            for (int i = 0; i < headerList.getLength(); i++) {
                if (headerList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) headerList.item(i);

                    SapTableCRHeaderDto headerDto = new SapTableCRHeaderDto();

                    headerDto.setSociedad(Utils.getValueNodo(elemento, "BUKRS"));
                    headerDto.setNumeroDocumentoErp(Utils.getValueNodo(elemento, "BELNR"));
                    headerDto.setEjercicio(Integer.parseInt(Utils.getValueNodo(elemento, "GJAHR")));
                    headerDto.setClaseDocumento(Utils.getValueNodo(elemento, "BLART"));
                    headerDto.setFechaEmision(Utils.getValueNodoDate(elemento, "BLDAT"));
                    headerDto.setFechaContabilizacion(Utils.getValueNodoDate(elemento, "BUDAT"));
                    headerDto.setSerieCorrelativoDocumento(Utils.getValueNodo(elemento, "CTNUMBER"));
                    headerDto.setImporteBaseCalculoRetencion(Optional.ofNullable(new BigDecimal(Utils.getValueNodo(elemento, "WT_QSSHH"))).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));
                    headerDto.setImporteBaseMonedaLocal(Optional.ofNullable(new BigDecimal(Utils.getValueNodo(elemento, "WT_QSSHB"))).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));
                    headerDto.setImporteRetencion(Optional.ofNullable(new BigDecimal(Utils.getValueNodo(elemento, "WT_QBSHH"))).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));
                    headerDto.setImporteRetencionMonedaLocal(Optional.ofNullable(new BigDecimal(Utils.getValueNodo(elemento, "WT_QBSHB"))).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));
                    headerDto.setMonedaDocumento(Utils.getValueNodo(elemento, "WAERS"));
                    headerDto.setMonedaLocal(Utils.getValueNodo(elemento, "HWAER"));
                    headerDto.setProveedorRuc(Utils.getValueNodo(elemento, "STCDT1"));
                    headerDto.setProveedorRazonSocial(Utils.getValueNodo(elemento, "RAZSOCRECEP"));
                    headerDto.setProveedorEmail(Utils.getValueNodo(elemento, "EMAIL_RECEP"));

                    headerDtoList.add(headerDto);
                }
            }
        }
        return headerDtoList;
    }

    public static List<SapTableCRItemDto> getItemDtoList(NodeList itemList) {
        List<SapTableCRItemDto> itemDtoList = new ArrayList<>();

        if (itemList != null) {
            for (int i = 0; i < itemList.getLength(); i++) {
                if (itemList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) itemList.item(i);

                    SapTableCRItemDto itemDto = new SapTableCRItemDto();

                    itemDto.setNumeroDocumentoErp(Utils.getValueNodo(elemento, "BELNR"));
                    itemDto.setSociedad(Utils.getValueNodo(elemento, "BUKRS"));
                    itemDto.setEjercicio(Integer.parseInt(Utils.getValueNodo(elemento, "GJAHR")));
                    itemDto.setTipoComprobante(Utils.getValueNodo(elemento, "BLART"));
                    itemDto.setSerieFactura(Utils.getValueNodo(elemento, "ZSERIEFACTURA"));
                    itemDto.setCorrelativoFactura(Utils.getValueNodo(elemento, "ZNUMEROFACTURA"));
                    itemDto.setFechaEmision(Utils.getValueNodoDate(elemento, "BLDAT"));
                    itemDto.setMoneda(Utils.getValueNodo(elemento, "HWAER"));
                    itemDto.setImporteTotalComprobante(Optional.ofNullable(new BigDecimal(Utils.getValueNodo(elemento, "WRBTR"))).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));
                    itemDto.setImportePago(Optional.ofNullable(new BigDecimal(Utils.getValueNodo(elemento, "WRBTR"))).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));
                    itemDto.setImporteRetencionSoles(Optional.ofNullable(new BigDecimal(Utils.getValueNodo(elemento, "WT_QBSHH"))).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));
                    itemDto.setImporteNetoSoles(Optional.ofNullable(new BigDecimal(Utils.getValueNodo(elemento, "DMBTR"))).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));

                    itemDtoList.add(itemDto);
                }
            }
        }
        return itemDtoList;
    }

    public static List<SapTableCRTotalDto> getTotalDtoList(NodeList totalList) {
        List<SapTableCRTotalDto> totalDtoList = new ArrayList<>();

        if (totalList != null) {
            for (int i = 0; i < totalList.getLength(); i++) {
                if (totalList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) totalList.item(i);

                    SapTableCRTotalDto totalDto = new SapTableCRTotalDto();

                    totalDto.setNumeroDocumentoErp(Utils.getValueNodo(elemento, "BELNR"));
                    totalDto.setSociedad(Utils.getValueNodo(elemento, "BUKRS"));
                    totalDto.setEjercicio(Integer.parseInt(Utils.getValueNodo(elemento, "GJAHR")));
                    totalDto.setImporteTotalRetencionMonedaLocal(Optional.ofNullable(new BigDecimal(Utils.getValueNodo(elemento, "WT_QBSHH"))).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));

                    totalDtoList.add(totalDto);
                }
            }
        }
        return totalDtoList;
    }

    public static List<SapTableCRSociedadDto> getSociedadDtoList(NodeList sociedadList) {
        List<SapTableCRSociedadDto> sociedadDtoList = new ArrayList<>();

        if (sociedadList != null) {
            for (int i = 0; i < sociedadList.getLength(); i++) {
                if (sociedadList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) sociedadList.item(i);

                    SapTableCRSociedadDto sociedadDto = new SapTableCRSociedadDto();

                    sociedadDto.setSociedad(Utils.getValueNodo(elemento, "BUKRS"));
                    sociedadDto.setRazonSocial(Utils.getValueNodo(elemento, "NAME1"));
                    sociedadDto.setCalle(Utils.getValueNodo(elemento, "STREET"));
                    sociedadDto.setNumeroEdificio(Utils.getValueNodo(elemento, "HOUSE_NUM1"));
                    sociedadDto.setPoblacion(Utils.getValueNodo(elemento, "CITY1"));
                    sociedadDto.setDistrito(Utils.getValueNodo(elemento, "CITY2"));
                    sociedadDto.setTelefono(Utils.getValueNodo(elemento, "TEL_NUMBER"));
                    sociedadDto.setRuc(Utils.getValueNodo(elemento, "PAVAL"));

                    sociedadDtoList.add(sociedadDto);
                }
            }
        }
        return sociedadDtoList;
    }
}