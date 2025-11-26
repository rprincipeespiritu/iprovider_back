package com.incloud.hcp.rest;

import com.incloud.hcp.job.*;
import com.incloud.hcp.service.extractor.ExtractorBienServicioService;
import com.incloud.hcp.service.extractor.ExtractorDocumentoAceptacionService;
import com.incloud.hcp.service.extractor.ExtractorOCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/util")
public class UtilRest {

    @Value("${war.build.datetime}")
    private String warBuildDatetime;

    private ContratoMarcoExtractionJob contratoMarcoExtractionJob;
    private DocumentoAceptacionExtractionJob documentoAceptacionExtractionJob;
    private DocumentoAceptacionHistoricExtractionJob documentoAceptacionHistoricExtractionJob;
    private OrdenCompraExtractionJob ordenCompraExtractionJob;
    private PrefacturaAnuladaUpdateJob prefacturaAnuladaUpdateJob;

    private ExtractorOCService extractorOCService;
    private ExtractorDocumentoAceptacionService extractorDocumentoAceptacionService;
    private ExtractorBienServicioService extractorBienServicioService;

    @Autowired
    public UtilRest(ContratoMarcoExtractionJob contratoMarcoExtractionJob,
                    DocumentoAceptacionExtractionJob documentoAceptacionExtractionJob,
                    DocumentoAceptacionHistoricExtractionJob documentoAceptacionHistoricExtractionJob,
                    OrdenCompraExtractionJob ordenCompraExtractionJob,
                    PrefacturaAnuladaUpdateJob prefacturaAnuladaUpdateJob,
                    ExtractorOCService extractorOCService,
                    ExtractorDocumentoAceptacionService extractorDocumentoAceptacionService,
                    ExtractorBienServicioService extractorBienServicioService) {
        this.contratoMarcoExtractionJob = contratoMarcoExtractionJob;
        this.documentoAceptacionHistoricExtractionJob = documentoAceptacionHistoricExtractionJob;
        this.documentoAceptacionExtractionJob = documentoAceptacionExtractionJob;
        this.ordenCompraExtractionJob = ordenCompraExtractionJob;
        this.prefacturaAnuladaUpdateJob = prefacturaAnuladaUpdateJob;
        this.extractorOCService = extractorOCService;
        this.extractorDocumentoAceptacionService = extractorDocumentoAceptacionService;
        this.extractorBienServicioService = extractorBienServicioService;
    }

    @GetMapping(value = "/jobs/contrato-marco-extractor-check-status")
    public ResponseEntity<String> contratoMarcoExtractorCurrentStatus() {
        return new ResponseEntity<>("ContratoMarcoExtractionJob is currently enabled: " + contratoMarcoExtractionJob.current(), HttpStatus.OK);
    }

    @GetMapping(value = "/jobs/contrato-marco-extractor-toggle-status")
    public ResponseEntity<String> contratoMarcoExtractorToggleStatus() {
        return new ResponseEntity<>("ContratoMarcoExtractionJob is now enabled: " + contratoMarcoExtractionJob.toggle(), HttpStatus.OK);
    }

    @GetMapping(value = "/jobs/documento-aceptacion-extractor-check-status")
    public ResponseEntity<String> documentoAceptacionExtractorCurrentStatus() {
        return new ResponseEntity<>("DocumentoAceptacionExtractionJob is currently enabled: " + documentoAceptacionExtractionJob.current(), HttpStatus.OK);
    }

    @GetMapping(value = "/jobs/documento-aceptacion-extractor-toggle-status")
    public ResponseEntity<String> documentoAceptacionExtractorToggleStatus() {
        return new ResponseEntity<>("DocumentoAceptacionExtractionJob is now enabled: " + documentoAceptacionExtractionJob.toggle(), HttpStatus.OK);
    }

    @GetMapping(value = "/jobs/documento-aceptacion-historic-extractor-check-status")
    public ResponseEntity<String> documentoAceptacionHistoricExtractorCurrentStatus() {
        return new ResponseEntity<>("DocumentoAceptacionHistoricExtractionJob is currently enabled: " + documentoAceptacionHistoricExtractionJob.current(), HttpStatus.OK);
    }

    @GetMapping(value = "/jobs/documento-aceptacion-historic-extractor-toggle-status")
    public ResponseEntity<String> documentoAceptacionHistoricExtractorToggleStatus() {
        return new ResponseEntity<>("DocumentoAceptacionHistoricExtractionJob is now enabled: " + documentoAceptacionHistoricExtractionJob.toggle(), HttpStatus.OK);
    }

    @GetMapping(value = "/jobs/orden-compra-extractor-check-status")
    public ResponseEntity<String> ordenCompraExtractorCurrentStatus() {
        return new ResponseEntity<>("OrdenCompraExtractionJob is currently enabled: " + ordenCompraExtractionJob.current(), HttpStatus.OK);
    }

    @GetMapping(value = "/jobs/orden-compra-extractor-toggle-status")
    public ResponseEntity<String> ordenCompraExtractorToggleStatus() {
        return new ResponseEntity<>("OrdenCompraExtractionExtractionJob is now enabled: " + ordenCompraExtractionJob.toggle(), HttpStatus.OK);
    }

    @GetMapping(value = "/jobs/prefactura-anulada-updater-check-status")
    public ResponseEntity<String> prefacturaAnuladaUpdaterCurrentStatus() {
        return new ResponseEntity<>("PrefacturaAnuladaUpdateJob is currently enabled: " + prefacturaAnuladaUpdateJob.current(), HttpStatus.OK);
    }

    @GetMapping(value = "/jobs/prefactura-anulada-updater-toggle-status")
    public ResponseEntity<String> prefacturaAnuladaUpdaterToggleStatus() {
        return new ResponseEntity<>("PrefacturaAnuladaUpdateJob is now enabled: " + prefacturaAnuladaUpdateJob.toggle(), HttpStatus.OK);
    }

    @GetMapping(value = "/process/documento-aceptacion-extraction-check-status")
    public ResponseEntity<String> currentDocumentoAceptacionExtractionProcessingStatus() {
        return new ResponseEntity<>("DocumentoAceptacion Extraction is currently processing: " + extractorDocumentoAceptacionService.currentDocumentoAceptacionExtractionProcessingState(), HttpStatus.OK);
    }

    @GetMapping(value = "/process/documento-aceptacion-extraction-force-toggle-status")
    public ResponseEntity<String> forceToggleDocumentoAceptacionExtractionProcessingStatus() {
        return new ResponseEntity<>("DocumentoAceptacion Extraction is now processing: " + extractorDocumentoAceptacionService.toggleDocumentoAceptacionExtractionProcessingState(), HttpStatus.OK);
    }

    @GetMapping(value = "process/orden-compra-extraction-check-status")
    public ResponseEntity<String> currentOrdenCompraExtractionProcessingStatus() {
        return new ResponseEntity<>("OrdenCompra Extraction is currently processing: " + extractorOCService.currentOrdenCompraExtractionProcessingState(), HttpStatus.OK);
    }

    @GetMapping(value = "process/orden-compra-extraction-force-toggle-status")
    public ResponseEntity<String> forceToggleOrdenCompraExtractionProcessingStatus() {
        return new ResponseEntity<>("OrdenCompra Extraction is now processing: " + extractorOCService.toggleOrdenCompraExtractionProcessingState(), HttpStatus.OK);
    }

    @GetMapping(value = "process/bien-servicio-extraction-check-status")
    public ResponseEntity<String> currentBienServicioExtractionProcessingStatus() {
        return new ResponseEntity<>("OrdenCompra Extraction is currently processing: " + extractorBienServicioService.currentOrdenCompraExtractionProcessingState(), HttpStatus.OK);
    }

    @GetMapping(value = "process/bien-servicio-extraction-force-toggle-status")
    public ResponseEntity<String> forceToggleBienServicioExtractionProcessingStatus() {
        return new ResponseEntity<>("OrdenCompra Extraction is now processing: " + extractorBienServicioService.toggleOrdenCompraExtractionProcessingState(), HttpStatus.OK);
    }

    @GetMapping(value = "getWarBuildDatetime")
    public ResponseEntity<String> getWarBuildDatetime() {
        return new ResponseEntity<>(warBuildDatetime, HttpStatus.OK);
    }
}
