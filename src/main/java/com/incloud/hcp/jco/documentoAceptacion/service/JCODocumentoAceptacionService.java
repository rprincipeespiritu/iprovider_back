package com.incloud.hcp.jco.documentoAceptacion.service;


import com.incloud.hcp.jco.documentoAceptacion.dto.SapTableItemDto;

import java.util.List;

public interface JCODocumentoAceptacionService {

    void extraerDocumentoAceptacionListRFC(String parametro1, String parametro2, boolean extraccionUnicoDocumento, boolean aprobarOrdenCompra, boolean enviarCorreoAprobacion) throws Exception;
    
    void extraerDocumentoAceptacionListRFC_old(String parametro1, String parametro2, boolean extraccionUnicoDocumento, boolean aprobarOrdenCompra, boolean enviarCorreoAprobacion) throws Exception;
    
    void extraerDocumentoAceptacionAnuladasList(boolean extraccionUnicoDocumento) throws Exception;

    void extraerDocumentoAceptacionHES(String parametro1, String parametro2, boolean aprobarOrdenCompra, boolean enviarCorreoAprobacion) throws Exception;

    List<SapTableItemDto> extraerDataDocumentoAceptacionRFC(String parametro1, String parametro2, boolean unicoDocumentoAceptacion) throws Exception;

    boolean toggleDocumentoAceptacionExtractionProcessingState();

    boolean currentDocumentoAceptacionExtractionProcessingState();

    void extraerDocumentoAceptacionListMaterialDocument(String materialDocument, boolean extraccionUnicoDocumento, boolean aprobarOrdenCompra, boolean enviarCorreoAprobacion) throws Exception;
}
