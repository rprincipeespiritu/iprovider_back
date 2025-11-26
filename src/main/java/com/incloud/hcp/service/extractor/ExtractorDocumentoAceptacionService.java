package com.incloud.hcp.service.extractor;

public interface ExtractorDocumentoAceptacionService {

    void extraerDocumentoAceptacion(String fechaInicio, String fechaFinal, boolean enviarCorreoAprobacion) throws Exception;

    boolean toggleDocumentoAceptacionExtractionProcessingState();

    boolean currentDocumentoAceptacionExtractionProcessingState();

}
