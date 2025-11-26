package com.incloud.hcp.rest;

import com.incloud.hcp.bean.CmisFile;
import com.incloud.hcp.bean.MensajePrefactura;
import com.incloud.hcp.config.excel.ExcelType;
import com.incloud.hcp.domain.DocumentoAceptacion;
import com.incloud.hcp.domain.Prefactura;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.domain.StorageDocument;
import com.incloud.hcp.dto.*;
import com.incloud.hcp.enums.PrefacturaOpcionEnum;
import com.incloud.hcp.enums.TipoArchivoEnum;
import com.incloud.hcp.exception.InvalidOptionException;
import com.incloud.hcp.jco.prefactura.dto.PrefacturaRFCResponseDto;
import com.incloud.hcp.repository.PrefacturaRepository;
import com.incloud.hcp.repository.ProveedorRepository;
import com.incloud.hcp.service.PrefacturaService;
import com.incloud.hcp.service.StorageDocumentService;
import com.incloud.hcp.service.cmiscf.CmisBaseService;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.util.Utils;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@RestController
@RequestMapping(value = "/api/prefactura")
public class PrefacturaRest {

    private PrefacturaService prefacturaService;
    private PrefacturaRepository prefacturaRepository;
    private StorageDocumentService storageDocumentService;
    //private CmisService cmisService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String OPCION_INVALIDA = "'%s' no es una opción valida. Las opciones aceptadas son '%s' y '%s'.";

    @Value("${cfg.folder.ecm}")
    private String folderName;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private CmisBaseService cmisBaseServicecf;

    public String FOLDER = "proveedores";

    @Autowired
    public PrefacturaRest(PrefacturaService prefacturaService, PrefacturaRepository prefacturaRepository, StorageDocumentService storageDocumentService
                          ) {
        this.prefacturaService = prefacturaService;
        this.prefacturaRepository = prefacturaRepository;
        this.storageDocumentService = storageDocumentService;
        //this.cmisService = cmisService;
    }

    @GetMapping(value = "/getPrefacturaById/{idPrefactura}")
    public ResponseEntity<Prefactura> getPrefacturaById(@PathVariable("idPrefactura") Integer idPrefactura) {
        try {
            Optional<Prefactura> opPrefactura = prefacturaRepository.getOneById(idPrefactura);
            if (opPrefactura.isPresent()) {
                return new ResponseEntity<>(opPrefactura.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @GetMapping(value = "/getPrefacturaListByRucAndReferencia/{ruc}/{referencia}")
    public ResponseEntity<List<Prefactura>> getPrefacturaListByRucAndReferencia(@PathVariable("ruc") String ruc,
                                                                                @PathVariable("referencia") String referencia) {
        try {
            List<Prefactura> prefacturaList = prefacturaRepository.getPrefacturaListByRucAndReferencia(ruc, referencia);

            if (prefacturaList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(prefacturaList, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @PostMapping(value = "/getPostPrefacturaList")
    public ResponseEntity<List<Prefactura>> getPostPrefacturaList(@RequestBody PrefacturaEntradaDto bean) {
        try {
            List<Prefactura> listaRetorno = this.prefacturaService.getPrefacturaList(bean);

            if (listaRetorno.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(listaRetorno, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @GetMapping(value = "/getPrefacturaList/{FechaInicio}/{FechaFin}")
    public ResponseEntity<List<Prefactura>> getPrefacturaList(
            @PathVariable("FechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaInicio,
            @PathVariable("FechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaFin,
            @RequestParam(value = "ruc", required = false) String ruc) {
        try {
            List<Prefactura> prefacturaList = prefacturaService.getPrefacturaListPorFechasAndRuc(fechaInicio, fechaFin, ruc);

            if (prefacturaList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(prefacturaList, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @GetMapping(value = "/getPrefacturaListByAnyDate")
    public ResponseEntity<List<Prefactura>> getPrefacturaListByAnyDate(
            @RequestParam(value = "fechaEmisionInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEmisionInicio,
            @RequestParam(value = "fechaEmisionFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEmisionFin,
            @RequestParam(value = "fechaEntradaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEntradaInicio,
            @RequestParam(value = "fechaEntradaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEntradaFin,
            @RequestParam(value = "ruc", required = false) String ruc,
            @RequestParam(value = "referencia", required = false) String referencia) {
        try {
            List<Prefactura> prefacturaList = prefacturaService.getPrefacturaListByFechasEmisionAndFechaEntradaAndRuc(fechaEmisionInicio, fechaEmisionFin, fechaEntradaInicio, fechaEntradaFin, ruc, referencia);

            if (prefacturaList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(prefacturaList, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @GetMapping(value = "/getPrefacturaListByAllFilters")
    public ResponseEntity<List<Prefactura>> getPrefacturaListByAllFilters(
            @RequestParam(value = "fechaEmisionInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEmisionInicio,
            @RequestParam(value = "fechaEmisionFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEmisionFin,
            @RequestParam(value = "fechaEntradaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEntradaInicio,
            @RequestParam(value = "fechaEntradaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEntradaFin,
            @RequestParam(value = "ruc", required = false) String ruc,
            @RequestParam(value = "referencia", required = false) String referencia,
            @RequestParam(value = "comprador", required = false) String comprador,
            @RequestParam(value = "centro", required = false) String centro,
            @RequestParam(value = "idEstado", required = false) Integer idEstado) {
        try {
            List<Prefactura> prefacturaList = prefacturaService.getPrefacturaList(fechaEmisionInicio, fechaEmisionFin, fechaEntradaInicio, fechaEntradaFin, ruc, referencia, comprador, centro, idEstado);

            if (prefacturaList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(prefacturaList, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }



    @GetMapping(value = "/getDocumentoAceptacionListByIdPrefactura/{idPrefactura}")
    public ResponseEntity<List<DocumentoAceptacion>> getDocumentoAceptacionListDePrefactura(@PathVariable("idPrefactura") Integer idPrefactura) {
        try {
            List<DocumentoAceptacion> documentoAceptacionList = prefacturaService.obtenerDocumentoAceptacionListByIdPrefactura(idPrefactura);
            if (documentoAceptacionList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(documentoAceptacionList, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    //numero de serie
    //numero de factura
    @PostMapping(value = "/nuevaPrefactura")
    public ResponseEntity<PrefacturaReferenciaDTO> ingresarNuevaPrefactura(@RequestBody PrefacturaDto prefacturaDto) {
        try {
            PrefacturaReferenciaDTO obj = prefacturaService.ingresarNuevaPrefactura(prefacturaDto);
            if (obj.getIdPrefactura().compareTo(-422) == 0){
                return new ResponseEntity<>(obj, HttpStatus.UNPROCESSABLE_ENTITY);
            }

            return new ResponseEntity<>(obj, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @PutMapping(value = "/descartarPrefactura/{idPrefactura}")
    public ResponseEntity<String> descartarPrefacturaById(@PathVariable("idPrefactura") Integer idPrefactura) {
        try {
            String respuesta = prefacturaService.descartarPrefactura(idPrefactura);
            return new ResponseEntity<>(respuesta, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @PutMapping(value = "/registrarRechazarPrefactura/{idPrefactura}/{prefacturaOpcion}")
    public ResponseEntity<PrefacturaRFCResponseDto> registrarRechazarPrefacturaById(
            @PathVariable("idPrefactura") Integer idPrefactura,
            @PathVariable(value = "prefacturaOpcion") PrefacturaOpcionEnum prefacturaOpcion,
            @RequestParam(value = "fechaContabilizacion", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaContabilizacion,
            @RequestParam(value = "fechaBase", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaBase,
            @RequestParam(value = "indicadorImpuesto", required = false) String indicadorImpuesto,
//            @RequestParam(value = "textoRechazo", required = false) String textoRechazo) {
            @RequestBody(required = false) String textoRechazo){

        String opcion = prefacturaOpcion.toString().trim().toUpperCase();


        if (!opcion.equals(PrefacturaOpcionEnum.REGISTRAR.toString()) && !opcion.equals(PrefacturaOpcionEnum.RECHAZAR.toString()))
            throw new InvalidOptionException(String.format(OPCION_INVALIDA, opcion, PrefacturaOpcionEnum.REGISTRAR.toString(), PrefacturaOpcionEnum.RECHAZAR.toString()));

        try {
            PrefacturaRFCResponseDto responseDto = new PrefacturaRFCResponseDto();

            if (opcion.equals(PrefacturaOpcionEnum.REGISTRAR.toString())) {


                if (fechaContabilizacion == null || fechaBase == null || indicadorImpuesto == null)
                    throw new InvalidOptionException("Los campos fechaContabilizacion, fechaBase y/o indicadorImpuesto no pueden ser null al " + PrefacturaOpcionEnum.REGISTRAR.toString());

                responseDto = prefacturaService.registrarPrefacturaEnSap(idPrefactura, fechaContabilizacion, fechaBase, indicadorImpuesto);
            } else { // opcion = RECHAZAR
                if (textoRechazo == null || textoRechazo.isEmpty())
                    throw new InvalidOptionException("El campo de motivo de rechazo es obligatorio al " + PrefacturaOpcionEnum.RECHAZAR.toString());

                responseDto = prefacturaService.rechazarPrefactura(idPrefactura, textoRechazo);
            }

            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            e.printStackTrace();
            throw new RuntimeException(error);
        }
    }


    @PutMapping(value = "/enviarCorreoAnulacionPrefactura/{idPrefactura}")
    public ResponseEntity<String> enviarCorreoAnulacionPrefacturaById(
            @PathVariable("idPrefactura") Integer idPrefactura,
            @RequestParam(value = "textoAnulacion") String textoAnulacion) {
        try {
            return new ResponseEntity<>(prefacturaService.enviarCorreoAnulacionPrefactura(idPrefactura, textoAnulacion), HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @PostMapping(value = "/guardarArchivosPrefactura/{idPrefactura}")
    public ResponseEntity<MensajePrefactura> guardarArchivoPrefactura(
            @PathVariable(value = "idPrefactura") Integer idPrefactura,
            @RequestParam(value = "file") MultipartFile multipartFile) {
        MensajePrefactura mensajePrefactura = new MensajePrefactura();
        //String newFolder = folderName;
        CmisFile cmisFile = new CmisFile();

        try {
            if (multipartFile != null) {
                //RREYES
                Prefactura prefactura = prefacturaRepository.getById(idPrefactura); // Obtiene la prefactura
                Proveedor proveedor = proveedorRepository.getProveedorByRuc(prefactura.getProveedorRuc()); // Obtiene al Proveedor
                String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename()); // Obtiene la extensión
                String nameFile = FilenameUtils.getName(multipartFile.getOriginalFilename()); // Obtiene el nombre
                String msgRespuesta = "";

                String folderMainId = cmisBaseServicecf.createFolder(FOLDER).getId();
                String newFolder = proveedor.getRuc()+"_"+proveedor.getIdProveedor();
                String folderId = cmisBaseServicecf.SubCreateFolder(folderMainId,newFolder).getId();
                String folderFinal = cmisBaseServicecf.SubCreateFolder(folderId,"prefactura").getId();
                String nameFolderFinal = FOLDER +"/"+newFolder+"";
                com.incloud.hcp.service.cmiscf.bean.CmisFile cmisFileCF = cmisBaseServicecf.createDocumento("/"+folderFinal, multipartFile,nameFolderFinal,nameFile);
                if(extension.toUpperCase().contains("PDF")){
                    prefactura.setPdfEcmPath(cmisFileCF.getId());
                    msgRespuesta = "Se actualizo correctamente la prefactura con numero: " + prefactura.getReferencia();
                }else if(extension.toUpperCase().contains("XML")){
                    prefactura.setXmlEcmPath(cmisFileCF.getId());
                    msgRespuesta = "Se actualizo correctamente la prefactura con numero: " + prefactura.getReferencia();
                } else {
                    msgRespuesta = "La extensión " + extension + " no es correcta.";
                }
                this.prefacturaRepository.save(prefactura);
                //END RREYES
                //String folderId = ""; //cmisService.createFolder(newFolder);
                //String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
                //byte[] base64 = multipartFile.getBytes();
                //byte[] encoded = Base64.getEncoder().encode(base64);
                //cmisFile.setUrl(new String(encoded));
                //cmisFile.setType(extension.toLowerCase());//cmisService.createDocumento(folderId, multipartFile);
                //String messageSave = prefacturaService.updatePathAdjuntoPrefactura(idPrefactura, cmisFile.getUrl(), cmisFile.getType());
                ////  logger.error("Verificar grabado: " + cmisFile.getUrl() + " " + cmisFile.getType());

                mensajePrefactura.setMensajeEcm("Se guardo correctamente el archivo " + nameFile);
                mensajePrefactura.setMensajeSaveEcm(msgRespuesta);
                mensajePrefactura.setType(extension);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }

        return new ResponseEntity<>(mensajePrefactura, HttpStatus.OK);
    }

    @PostMapping(path = "/findDocumentByIdPrefactura/{idPrefactura}/{tipoArchivo}")
    public ResponseEntity<StorageDocument> getDocumentByIdPrefacturaAndTipoArchivo(@PathVariable(value = "idPrefactura") Integer idPrefactura,
                                                                     @PathVariable(value = "tipoArchivo") TipoArchivoEnum tipoArchivoEnum) {
        String pathScpEcm = prefacturaService.getFileEcmPath(idPrefactura, tipoArchivoEnum.getFileType());
        if (!pathScpEcm.isEmpty()) {
            StorageDocument storageDocument = storageDocumentService.getDocumentByPath(pathScpEcm, true);
            return new ResponseEntity<>(storageDocument, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping(path = "/list-document")
    public ResponseEntity<List<StorageDocument>> getAll(@RequestParam(value = "path") String path) {
        Session session = null;//cmisService.getSession();
        CmisObject objectByPath = session.getObjectByPath(path);
        List<StorageDocument> docs = new ArrayList<>();

        if (objectByPath instanceof Folder) {
            ItemIterable<CmisObject> children = ((Folder) objectByPath).getChildren();
            for (CmisObject cmisObject : children) {
                StorageDocument storageDocument = new StorageDocument();
                storageDocument.setId(cmisObject.getId());
                storageDocument.setName(cmisObject.getName());
                docs.add(storageDocument);
            }
        }
        if (objectByPath instanceof Document) {
            Document document = (Document) objectByPath;
            StorageDocument storageDocument = new StorageDocument();
            storageDocument.setId(document.getId());
            storageDocument.setName(document.getName());
            storageDocument.setMimeType(document.getContentStreamMimeType());
            docs.add(storageDocument);
        }
        return new ResponseEntity<>(docs, HttpStatus.OK);
    }


    @GetMapping(path = "/listDocumentByMaxLenghAndType/{maxLength}/{fileTypeEnum}")
    public ResponseEntity<List<StorageDocument>> getAllByMaxSizeAndType(@PathVariable("maxLength") Long maxLength,
                                                                        @PathVariable(value = "fileTypeEnum") TipoArchivoEnum fileTypeEnum) {
        String fileType = fileTypeEnum.getFileType();
        String folderPath = "/".concat(folderName);
        Session session = null;//cmisService.getSession();
        CmisObject objectByPath = session.getObjectByPath(folderPath);
        List<StorageDocument> docs = new ArrayList<>();

        if (objectByPath instanceof Folder) {
            ItemIterable<CmisObject> children = ((Folder) objectByPath).getChildren();
            for (CmisObject cmisObject : children) {
                if (cmisObject instanceof Document) {
                    String filename = cmisObject.getName();
                    if (filename.substring(filename.length()-3).equalsIgnoreCase(fileType)) {
                        ContentStream contentStream = ((Document) cmisObject).getContentStream();
                        long length = contentStream.getLength();

                        if(length <= maxLength) {
                            StorageDocument storageDocument = new StorageDocument();
                            storageDocument.setId(cmisObject.getId());
                            storageDocument.setName(filename);
                            storageDocument.setLength(String.valueOf(length));
                            docs.add(storageDocument);
                        }
                    }
                }
            }
        }

        return new ResponseEntity<>(docs, HttpStatus.OK);
    }


    @GetMapping(value = "/getPrefacturaPdfById/{idPrefactura}")
    public ResponseEntity<String> getPrefacturaPdfById(@PathVariable("idPrefactura") Integer idPrefactura){
        if (idPrefactura == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        try{
            Optional<Prefactura> opPrefactura = prefacturaRepository.getOneById(idPrefactura);

            if(opPrefactura.isPresent()){
                return new ResponseEntity<>(prefacturaService.getPrefacturaPdfContent(opPrefactura.get()),HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @PostMapping(value = "actualizarPrefacturasAnuladasEnSapByRangoFechas/{fechaInicio}/{fechaFin}")
    public ResponseEntity<PrefacturaAnuladaRespuestaDto> actualizarPrefacturasAnuladas(@PathVariable(value = "fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaInicio,
                                                                                       @PathVariable(value = "fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaFin) {
        try {
            PrefacturaAnuladaRespuestaDto respuestaDto = prefacturaService.actualizarMasivoPrefacturasAnuladasPorRangoFechas(fechaInicio, fechaFin);
            return new ResponseEntity<>(respuestaDto, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @PostMapping(value = "actualizarPrefacturasRegistradasManualmenteEnSapByIdList")
    public ResponseEntity<List<PrefacturaActualizarDto>> actualizarPrefacturasRegistradas(@RequestBody GenericRequestDTO genericRequestDTO) {
        List<Integer> idList = genericRequestDTO.getIntegerList1();

        if(idList == null || idList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        try {
            List<PrefacturaActualizarDto> prefacturaActualizarDtoRespuestaList = prefacturaService.actualizarPrefacturasRegistradasEnSap(idList);
            prefacturaActualizarDtoRespuestaList.forEach(pa -> {
                pa.setPrefactura(null);
            });

            return new ResponseEntity<>(prefacturaActualizarDtoRespuestaList, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @GetMapping(value = "/descargarPrefacturaListExcel", produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public ResponseEntity<?> descargarPrefacturaListExcel(@RequestParam(value = "fechaEmisionInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEmisionInicio,
                                                          @RequestParam(value = "fechaEmisionFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEmisionFin,
                                                          @RequestParam(value = "fechaEntradaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEntradaInicio,
                                                          @RequestParam(value = "fechaEntradaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEntradaFin,
                                                          @RequestParam(value = "ruc", required = false) String ruc,
                                                          @RequestParam(value = "referencia", required = false) String referencia,
                                                          @RequestParam(value = "comprador", required = false) String comprador,
                                                          @RequestParam(value = "centro", required = false) String centro,
                                                          @RequestParam(value = "idEstado", required = false) Integer idEstado,
                                                          HttpServletResponse response) {
        String excelFileName = "ListadoPrefacturas_" + DateUtils.getFechaActualAsStringPattern("yyyy-MM-dd_hh_mm_ss") + ".xlsx";
        SXSSFWorkbook book = this.prefacturaService.descargarListaPrefacturaExcelSXLSX(fechaEmisionInicio, fechaEmisionFin, fechaEntradaInicio, fechaEntradaFin, ruc, referencia, comprador, centro, idEstado);

        try {
            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            book.write(outByteStream);
            byte[] outArray = outByteStream.toByteArray();
            response.setContentType(ExcelType.XLSX.getExtension());
            response.setContentLength(outArray.length);
            response.setHeader("Expires:", "0"); // eliminates browser caching
            response.setHeader("Content-Disposition", "attachment; filename=" + excelFileName);
            OutputStream outStream = response.getOutputStream();
            outStream.write(outArray);
            outStream.flush();

            book.dispose();
            book.close();
        } catch (FileNotFoundException e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        } catch (IOException e) {
            String error = Utils.obtieneMensajeErrorException(e);
            e.printStackTrace();
            throw new RuntimeException(error);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping(value = "/descargarPrefacturaListExcelconFiltros", produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public ResponseEntity<?> descargarPrefacturaListExcelconFiltros(@RequestParam(value = "fechaEmisionInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEmisionInicio,
                                                                    @RequestParam(value = "fechaEmisionFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEmisionFin,
                                                                    @RequestParam(value = "fechaEntradaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEntradaInicio,
                                                                    @RequestParam(value = "fechaEntradaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaEntradaFin,
                                                                    @RequestParam(value = "ruc", required = false) String ruc,
                                                                    @RequestParam(value = "referencia", required = false) String referencia,
                                                                    @RequestParam(value = "comprador", required = false) String comprador,
                                                                    @RequestParam(value = "centro", required = false) String centro,
                                                                    @RequestParam(value = "idEstado", required = false) Integer idEstado,
                                                                    @RequestParam(value = "filtroFechaEmision", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date filtroFechaEmision,
                                                                    @RequestParam(value = "filtroFechaEntrada", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date filtroFechaEntrada,
                                                                    @RequestParam(value = "filtroRuc", required = false) String filtroRuc,
                                                                    @RequestParam(value = "filtroReferencia", required = false) String filtroReferencia,
                                                                    @RequestParam(value = "filtroComprador", required = false) String filtroComprador,
                                                                    @RequestParam(value = "filtroCentro", required = false) String filtroCentro,
                                                                    @RequestParam(value = "filtroEstado", required = false) String filtroEstado,
                                                                    @RequestParam(value = "filtroSociedad", required = false) String filtroSociedad,
                                                                    @RequestParam(value = "filtroRazonSocial", required = false) String filtroRazonSocial,
                                                                    @RequestParam(value = "filtroDocumentoErp", required = false) String filtroDocumentoErp,
                                                                    @RequestParam(value = "filtroImporte", required = false) String filtroImporte,
                                                                    @RequestParam(value = "filtroIndicador", required = false) String filtroIndicador,
                                                                    @RequestParam(value = "sortCampo", required = false) String sortCampo,
                                                                    @RequestParam(value = "sortReversed", required = false) String sortReversed,
                                                                    HttpServletResponse response) {
        PrefacturaExcelRequestDto excelRequestDto = new PrefacturaExcelRequestDto();
        excelRequestDto.setFechaEmisionInicio(fechaEmisionInicio);
        excelRequestDto.setFechaEmisionFin(fechaEmisionFin);
        excelRequestDto.setFechaEntradaInicio(fechaEntradaInicio);
        excelRequestDto.setFechaEntradaFin(fechaEntradaFin);
        excelRequestDto.setRuc(ruc);
        excelRequestDto.setReferencia(referencia);
        excelRequestDto.setComprador(comprador);
        excelRequestDto.setCentro(centro);
        excelRequestDto.setIdEstado(idEstado);
        excelRequestDto.setFiltroFechaEmision(filtroFechaEmision);
        excelRequestDto.setFiltroFechaEntrada(filtroFechaEntrada);
        excelRequestDto.setFiltroRuc(filtroRuc);
        excelRequestDto.setFiltroReferencia(filtroReferencia);
        excelRequestDto.setFiltroComprador(filtroComprador);
        excelRequestDto.setFiltroCentro(filtroCentro);
        excelRequestDto.setFiltroEstado(filtroEstado);
        excelRequestDto.setFiltroSociedad(filtroSociedad);
        excelRequestDto.setFiltroRazonSocial(filtroRazonSocial);
        excelRequestDto.setFiltroDocumentoErp(filtroDocumentoErp);
        excelRequestDto.setFiltroImporte(filtroImporte);
        excelRequestDto.setFiltroIndicador(filtroIndicador);
        excelRequestDto.setSortCampo(sortCampo);
        excelRequestDto.setSortReversed(sortReversed);

        SXSSFWorkbook book = this.prefacturaService.descargarListaPrefacturaExcelSXLSXconFiltros(excelRequestDto);
        String excelFileName = "ListadoPrefacturas_" + DateUtils.getFechaActualAsStringPattern("yyyy-MM-dd__hh_mm_ss") + ".xlsx";

        try {
            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            book.write(outByteStream);
            byte[] outArray = outByteStream.toByteArray();
            response.setContentType(ExcelType.XLSX.getExtension());
            response.setContentLength(outArray.length);
            response.setHeader("Expires:", "0"); // eliminates browser caching
            response.setHeader("Content-Disposition", "attachment; filename=" + excelFileName);
            OutputStream outStream = response.getOutputStream();
            outStream.write(outArray);
            outStream.flush();

            book.dispose();
            book.close();
        } catch (FileNotFoundException e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        } catch (IOException e) {
            String error = Utils.obtieneMensajeErrorException(e);
            e.printStackTrace();
            throw new RuntimeException(error);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
