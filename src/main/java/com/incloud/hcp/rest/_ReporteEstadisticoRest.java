package com.incloud.hcp.rest;

import com.incloud.hcp.config.excel.ExcelType;
import com.incloud.hcp.dto.estadistico.*;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.service.ReporteEstadisticoService;
import com.incloud.hcp.util.Utils;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/_reporteEstadisticoRest")
public class _ReporteEstadisticoRest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ReporteEstadisticoService reporteEstadisticoService;


    @ApiOperation(value = "Genera Reporte Estadistico de Adjudicacion por RUC del Proveedor", produces = "application/json")
    @GetMapping(value = "/reporteAdjudicacion/{ruc}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ReporteEstadisticoAdjudicacionSalidaDto> reporteAdjudicacion(@PathVariable String ruc) throws URISyntaxException {
        log.debug("Find by id reporteAdjudicacion : {}", ruc);
        try {
            return Optional.ofNullable(this.reporteEstadisticoService.reporteAdjudicacion(ruc))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            //throw new RuntimeException(error);
            throw new PortalException(error);  // mizalo
        }
    }
    // inicio mizalo
    @ApiOperation(value = "Genera Reporte Estadistico de Adjudicacion por RUC del Proveedor", produces = "application/json")
    @GetMapping(value = "/reporteAdjudicacionAcreedor/{acreedor}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ReporteEstadisticoAdjudicacionSalidaDto> reporteAdjudicacionAcreedor(
            @PathVariable String acreedor) throws URISyntaxException {
        log.debug("Find by id reporteAdjudicacionAcreedor : {}", acreedor);
        try {
            return Optional.ofNullable(this.reporteEstadisticoService.reporteAdjudicacionAcreedor(acreedor))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            //throw new RuntimeException(error);
            throw new PortalException(error);  // mizalo
        }
    }

    @ApiOperation(value = "Genera Reporte Estadistico de Adjudicacion por Grupo de Compra", produces = "application/json")
    @GetMapping(value = "/reporteAdjudicacionGrupoCompra/{grupoCompra}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ReporteEstadisticoAdjudicacionGrupoCompraSalidaDto> reporteAdjudicacionGrupoCompra(
            @PathVariable String grupoCompra) throws URISyntaxException {
        log.debug("Find by id reporteAdjudicacionGrupoCompra : {}", grupoCompra);
        try {
            return Optional.ofNullable(this.reporteEstadisticoService.reporteAdjudicacionGrupoCompra(grupoCompra))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            //throw new RuntimeException(error);
            throw new PortalException(error);  // mizalo
        }
    }

    @ApiOperation(value = "Genera Excel XLSX de registros Reporte Adjudicacion", produces = "application/vnd.ms-excel")
    @GetMapping(value = "/reporteExcelAdjudicacionGrupoCompra", produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public ResponseEntity<?> reporteExcelAdjudicacionGrupoCompra(
            @QueryParam("ruc") String ruc,
            @QueryParam("acreedor") String acreedor,
            HttpServletResponse response) {
        log.debug("Ingresando reporteExcelAdjudicacionGrupoCompra");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_hh_mm_ss");
        String excelFileName = "Adjudicacion" + formatter.format(LocalDateTime.now()) + ".xlsx";

        SXSSFWorkbook book = null;
        try {
            book = this.reporteEstadisticoService.reporteAdjudicacionExcelGrupoCompra(ruc, acreedor);
            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            book.write(outByteStream);
            byte[] outArray = outByteStream.toByteArray();
            //response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setContentType(ExcelType.XLSX.getExtension());
            response.setContentLength(outArray.length);
            //response.setHeader("Expires:", "0"); // eliminates browser caching
            response.setHeader("Content-Disposition", "attachment; filename=" + excelFileName);
            OutputStream outStream = response.getOutputStream();
            outStream.write(outArray);
            outStream.flush();

            book.dispose();
            book.close();
        } catch (FileNotFoundException e) {
            String error = Utils.obtieneMensajeErrorException(e);
            e.printStackTrace();
            //throw new RuntimeException(error);
            throw new PortalException(error);  // mizalo
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            e.printStackTrace();
            //throw new RuntimeException(error);
            throw new PortalException(error);  // mizalo
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // fin mizalo


    @ApiOperation(value = "Genera Reporte Estadistico de Participacion por RUC del Proveedor", produces = "application/json")
    @GetMapping(value = "/reporteParticipacion/{ruc}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ReporteEstadisticoParticipacionSalidaDto> reporteParticipacion(@PathVariable String ruc) throws URISyntaxException {
        log.debug("Find by id reporteParticipacion : {}", ruc);
        try {
            return Optional.ofNullable(this.reporteEstadisticoService.reporteParticipacion(ruc))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Genera Reporte Estadistico de Status de Peticion de Oferta por RUC del Proveedor", produces = "application/json")
    @PostMapping(value = "/reporteStatusPeticionOferta", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ReporteStatusPeticionOfertaSalidaDto> reporteStatusPeticionOferta(
            @RequestBody ReporteStatusPeticionOfertaEntradaDto bean) throws URISyntaxException {
        log.debug("Find by id reporteStatusPeticionOferta : {}", bean);
        try {
            return Optional.ofNullable(this.reporteEstadisticoService.reporteStatusPeticionOferta(bean))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Genera Reporte Estadistico de Status de Peticion de Oferta por RUC del Proveedor (Todos - Paginado)", produces = "application/json")
    @PostMapping(value = "/reporteStatusPeticionOfertaTodosPaginado", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ReporteStatusPeticionOfertaSalidaDto> reporteStatusPeticionOfertaTodosPaginado(
            @RequestBody ReporteStatusPeticionOfertaEntradaPaginadoDto bean) throws URISyntaxException {
        log.debug("Find by id reporteStatusPeticionOfertaTodosPaginado : {}", bean);
        try {
            return Optional.ofNullable(this.reporteEstadisticoService.reporteStatusPeticionOfertaTodosPaginado(bean))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Genera Reporte Estadistico de Status de Peticion de Oferta por RUC del Proveedor (Adjudicados - Paginado)", produces = "application/json")
    @PostMapping(value = "/reporteStatusPeticionOfertaAdjuPaginado", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ReporteStatusPeticionOfertaSalidaDto> reporteStatusPeticionOfertaAdjuPaginado(
            @RequestBody ReporteStatusPeticionOfertaEntradaPaginadoDto bean) throws URISyntaxException {
        log.debug("Find by id reporteStatusPeticionOfertaAdjuPaginado : {}", bean);
        try {
            return Optional.ofNullable(this.reporteEstadisticoService.reporteStatusPeticionOfertaAdjuPaginado(bean))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Genera Reporte Estadistico de Status de Peticion de Oferta por RUC del Proveedor (En Proceso - Paginado)", produces = "application/json")
    @PostMapping(value = "/reporteStatusPeticionOfertaEnProcPaginado", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ReporteStatusPeticionOfertaSalidaDto> reporteStatusPeticionOfertaEnProcPaginado(
            @RequestBody ReporteStatusPeticionOfertaEntradaPaginadoDto bean) throws URISyntaxException {
        log.debug("Find by id reporteStatusPeticionOfertaEnProcPaginado : {}", bean);
        try {
            return Optional.ofNullable(this.reporteEstadisticoService.reporteStatusPeticionOfertaEnProcPaginado(bean))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }



}
