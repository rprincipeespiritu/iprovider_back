package com.incloud.hcp.pdf.service;

import java.util.List;

import com.incloud.hcp.domain.DocumentoAceptacion;
import com.incloud.hcp.domain.DocumentoAceptacionDetalle;
import com.incloud.hcp.dto.ConstanciaDetraccionDetallePdfDto;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraPdfDto;
import com.incloud.hcp.pdf.bean.ParameterComprobanteRetencionPdfDTO;
import com.incloud.hcp.pdf.bean.ParameterConformidadServicioPdfDTO;
import com.incloud.hcp.pdf.bean.ParameterEntradaMercaderiaPdfDTO;
import com.incloud.hcp.pdf.bean.PrefacturaPdfDto;


public interface PdfGeneratorService {

    byte[] generateEntradaMercaderia(ParameterEntradaMercaderiaPdfDTO parameterEntradaMercaderiaPdfDTO);

    byte[] generateDevoluciones(ParameterEntradaMercaderiaPdfDTO parameterEntradaMercaderiaPdfDTO);

    byte[] generateConformidadServicio(ParameterConformidadServicioPdfDTO parameterConformidadServicioPdfDTO);

    String generateConformidadServicio(DocumentoAceptacion documentoAceptacion, List<DocumentoAceptacionDetalle> documentoAceptacionDetalle) throws Exception;
    
    byte[] generateOrdenCompraPdfBytes(OrdenCompraPdfDto ordenCompraPdfDto);

    byte[] generateContratoMarcoPdfBytes(OrdenCompraPdfDto ordenCompraPdfDto);

    byte[] generatePrefacturaPdfBytes(PrefacturaPdfDto prefacturaPdfDto);

    byte[] generateComprobanteRetencion(ParameterComprobanteRetencionPdfDTO parameterComprobanteRetencionPdfDTO);

    byte[] generateConstanciaDetraccion(ConstanciaDetraccionDetallePdfDto constanciaDetraccionDetallePdfDto);
}
