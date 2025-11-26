package com.incloud.hcp.service.extractor;

public interface ExtractorBienServicioService {

    void extraerBienServicio(String fechaInicio, String fechaFinal) throws Exception;

    boolean toggleOrdenCompraExtractionProcessingState();

    boolean currentOrdenCompraExtractionProcessingState();
}
