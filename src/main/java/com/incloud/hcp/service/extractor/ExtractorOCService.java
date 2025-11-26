package com.incloud.hcp.service.extractor;

public interface ExtractorOCService {

    void extraerOC(String fechaInicio, String fechaFinal, boolean enviarCorreoPublicacion) throws Exception;

    boolean toggleOrdenCompraExtractionProcessingState();

    boolean currentOrdenCompraExtractionProcessingState();

}
