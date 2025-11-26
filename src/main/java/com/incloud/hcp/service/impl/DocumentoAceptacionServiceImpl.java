package com.incloud.hcp.service.impl;

import com.incloud.hcp.domain.*;
import com.incloud.hcp.dto.DocumentoAceptacionEntradaDto;
import com.incloud.hcp.jco.documentoAceptacion.service.JCODocumentoAceptacionService;
import com.incloud.hcp.pdf.PdfGeneratorFactory;
import com.incloud.hcp.pdf.bean.ParameterConformidadServicioPdfDTO;
import com.incloud.hcp.pdf.bean.ParameterEntradaMercaderiaPdfDTO;
import com.incloud.hcp.repository.*;
import com.incloud.hcp.service.DocumentoAceptacionService;
import com.incloud.hcp.service._framework.BaseServiceImpl;
import com.incloud.hcp.service.extractor.ExtractorDocumentoAceptacionService;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(propagation= Propagation.REQUIRED, rollbackFor=Exception.class)
public class DocumentoAceptacionServiceImpl extends BaseServiceImpl implements DocumentoAceptacionService {

    private DocumentoAceptacionRepository documentoAceptacionRepository;
    private JCODocumentoAceptacionService jcoDocumentoAceptacionService;
    private ExtractorDocumentoAceptacionService extractorDocumentoAceptacionService;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ProveedorLineaComercialRepository proveedorLineaComercialRepository;

    @Autowired
    private MtrTipoDocumentoRepository mtrTipoDocumentoRepository;

    @Autowired
    private SociedadRepository sociedadRepository;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public DocumentoAceptacionServiceImpl(DocumentoAceptacionRepository documentoAceptacionRepository,
                                          JCODocumentoAceptacionService jcoDocumentoAceptacionService,
                                          ExtractorDocumentoAceptacionService extractorDocumentoAceptacionService) {
        this.documentoAceptacionRepository = documentoAceptacionRepository;
        this.jcoDocumentoAceptacionService = jcoDocumentoAceptacionService;
        this.extractorDocumentoAceptacionService = extractorDocumentoAceptacionService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoAceptacion> getAllDocumentoAceptacion() {
        return documentoAceptacionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentoAceptacion getDocumentoAceptacionbyId(Integer idTipoDocumentoAceptacion, Integer idEntregaMercaderia) {
        return documentoAceptacionRepository.getDocumentoAceptacionById(idTipoDocumentoAceptacion, idEntregaMercaderia);
    }

    @Transactional(readOnly = true)
    public List<DocumentoAceptacion> getDocumentoAceptacionList(DocumentoAceptacionEntradaDto bean) throws Exception {
        List<DocumentoAceptacion> documentoAceptacionList = new ArrayList<>();
       /* if(!Optional.ofNullable(bean.getFechaInicio()).isPresent()) {
            throw new Exception("Debe ingresar valor de Fecha Inicio");
        }
        if(!Optional.ofNullable(bean.getFechaFin()).isPresent()) {
            throw new Exception("Debe ingresar valor de Fecha Fin");
        }*/


        List<Predicate> predicates = new ArrayList<>();
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<DocumentoAceptacion> query= cb.createQuery(DocumentoAceptacion.class);
        Root<DocumentoAceptacion> root = query.from(DocumentoAceptacion.class);
        if(Optional.ofNullable(bean.getFechaInicio()).isPresent() && Optional.ofNullable(bean.getFechaFin()).isPresent()) {

            Date fechaInicio = bean.getFechaInicio();
            Date fechaHasta = DateUtils.sumarRestarDias(bean.getFechaFin(), 1);
            predicates.add(cb.greaterThanOrEqualTo(root.<Date>get("fechaEmision"), fechaInicio));
            predicates.add(cb.lessThan(root.<Date>get("fechaEmision"), fechaHasta));
            predicates.add(cb.notEqual(root.get("idEstadoDocumentoAceptacion"), 4));
        }
        if (Optional.ofNullable(bean.getProveedorRuc()).isPresent()) {
            predicates.add(cb.equal(root.get("proveedorRuc"), bean.getProveedorRuc()));
        }
        if (Optional.ofNullable(bean.getEmail()).isPresent()) {
            Proveedor proveedor = proveedorRepository.getProveedorByEmail(bean.getEmail());
            if(Optional.ofNullable(proveedor).isPresent()){
                String ruc = proveedor.getRuc();
                predicates.add(cb.equal(root.get("proveedorRuc"), ruc));
            }

        }
        if (Optional.ofNullable(bean.getNroDocumento()).isPresent()) {
            predicates.add(cb.equal(root.get("numeroDocumentoAceptacion"), bean.getNroDocumento()));
        }
        if (Optional.ofNullable(bean.getNroOrdenCompra()).isPresent()) {
            predicates.add(cb.equal(root.get("numeroOrdenCompra"), bean.getNroOrdenCompra()));
        }
        if (Optional.ofNullable(bean.getNroHojaServicio()).isPresent()) {
            predicates.add(cb.equal(root.get("numeroGuiaProveedor"), bean.getNroHojaServicio()));
        }
        if (Optional.ofNullable(bean.getSociedad()).isPresent()) {
            predicates.add(cb.equal(root.get("sociedad"), bean.getSociedad()));
        }
        query.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
        Stream<DocumentoAceptacion> listaStream = this.entityManager.createQuery(query).getResultStream();
        documentoAceptacionList = listaStream.collect(Collectors.toList());

        documentoAceptacionList.sort(Comparator.comparing(DocumentoAceptacion::getFechaEmision).reversed());

        documentoAceptacionList.forEach(a -> {
            if (a.getSociedad() != null) {
                Sociedad soc = sociedadRepository.getByCodigoSociedad(a.getSociedad());
                a.setSociedad(soc.getRazonSocial());
            }
        });

        return documentoAceptacionList;
    }


    @Override
    @Transactional(readOnly = true)
    public List<DocumentoAceptacion> getDocumentoAceptacionPorFechasAndRuc(Date fechaInicio, Date fechaFin, String email) {
        List<DocumentoAceptacion> documentoAceptacionList = new ArrayList<>();

        if (email == null || email.isEmpty()) {
            documentoAceptacionList = documentoAceptacionRepository.getDocumentoAceptacionByFechaRegistroBetween(fechaInicio, fechaFin);
        }
        else {
            Proveedor proveedor = proveedorRepository.getProveedorByEmail(email);
            documentoAceptacionList = documentoAceptacionRepository.getDocumentoAceptacionByFechaRegistroBetweenAndProveedorRuc(fechaInicio, fechaFin, proveedor.getRuc());
        }
        // Opción iProvider - Silvestre (los id OC no están a nivel de cabecera, sino a detalle)
        return documentoAceptacionList;

        // Opción Iprovider - SAP
        /*return documentoAceptacionList.stream()
                .filter(da-> da.getIdOrdenCompra() != null) // si el Id de OC es null significa que el numero de OC asociado no se encontro entre las OC liberadas y porlo tanto el doc de aceptacion no es facturable y no debe mostrarse
                .collect(Collectors.toList());*/
    }

    @Override
    @Transactional(readOnly = true)
    public void extraerDocumentoAceptacionMasivoByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin, boolean aprobarOrdenCompra, boolean enviarCorreoAprobacion){
        logger.info("EXTRACCION DOC_ACEP MASIVA - INICIO: " + fechaInicio.toString());
        logger.info("EXTRACCION DOC_ACEP MASIVA - FIN: " + fechaFin.toString());

        while (fechaInicio.isBefore(fechaFin.plusDays(1))){
            try {
                String currentDateAsString = DateUtils.localDateToStringPattern(fechaInicio, DateUtils.STANDARD_DATE_FORMAT);
                extractorDocumentoAceptacionService.extraerDocumentoAceptacion(currentDateAsString, currentDateAsString, enviarCorreoAprobacion);
                //String currentDateAsSapString = DateUtils.localDateToSapString(fechaInicio);
                //jcoDocumentoAceptacionService.extraerDocumentoAceptacionListRFC(currentDateAsSapString, currentDateAsSapString, false, aprobarOrdenCompra, enviarCorreoAprobacion);
                fechaInicio = fechaInicio.plusDays(1);
            }
            catch(Exception e){
                String error = Utils.obtieneMensajeErrorException(e);
                logger.error("ERROR al extraer Documentos de Aceptacion de la fecha " + DateUtils.localDateToString(fechaInicio) + " : " + error);
                fechaInicio = fechaInicio.plusDays(1);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String extraerDocumentoAceptacionByNumOrdenCompraAndNumDocAceptacion(String numeroOrdenCompra, String numeroDocumentoAceptacion, boolean aprobarOrdenCompra, boolean enviarCorreoAprobacion){
        String header = "EXTRACCION DOC_ACEP POR NUMERO_OC Y NUMERO_DA: " + numeroOrdenCompra + " / " + numeroDocumentoAceptacion;
        String respuesta = "";
        logger.error(header + " // " + DateUtils.getCurrentTimestamp().toString());

        try {
            Optional<DocumentoAceptacion> opDocumentoAceptacion = documentoAceptacionRepository.findByNumeroDocumentoAceptacion(numeroDocumentoAceptacion);
            if (opDocumentoAceptacion.isPresent()){
                DocumentoAceptacion documentoAceptacion = opDocumentoAceptacion.get();
                respuesta = "El documento ya fue publicado previamente (asociado a Orden de Compra '" + documentoAceptacion.getNumeroOrdenCompra() + "') y actualmente esta en estado '" + documentoAceptacion.getEstadoDocumentoAceptacion().getDescripcion() + "'";
                logger.error(header + " // " + respuesta);
            }
            else{
                jcoDocumentoAceptacionService.extraerDocumentoAceptacionListRFC(numeroOrdenCompra, numeroDocumentoAceptacion, true, aprobarOrdenCompra, enviarCorreoAprobacion);
                respuesta = "El documento no fue publicado previamente y los datos de busqueda fueron enviados a SAP, verificar si hubo publicacion exitosa";
                logger.error(header + " // " + respuesta);
            }
        }
        catch(Exception e){
            String error = Utils.obtieneMensajeErrorException(e);
            logger.error(header + " // EXCEPCION: " + error);
            throw new RuntimeException("EXCEPCION al extraer Documento de Aceptacion '" + numeroDocumentoAceptacion + "' asociado a la Orden de Compra '" + numeroOrdenCompra + "' : " + error);
        }
        return respuesta;
    }

    @Override
    @Transactional(readOnly = true)
    public String getEntregaMercaderiaGenerateContent(ParameterEntradaMercaderiaPdfDTO parameterEntradaMercaderiaPdfDTO){
        byte[] generateEntradaMercaderia =PdfGeneratorFactory.getJasperGenerator().generateEntradaMercaderia(parameterEntradaMercaderiaPdfDTO);
        return Base64.getEncoder().encodeToString(generateEntradaMercaderia);
    }

    @Override
    public String getDevolucionesGenerateContent(ParameterEntradaMercaderiaPdfDTO parameterEntradaMercaderiaPdfDTO) {
        byte[] generateEntradaMercaderia =PdfGeneratorFactory.getJasperGenerator().generateDevoluciones(parameterEntradaMercaderiaPdfDTO);
        return Base64.getEncoder().encodeToString(generateEntradaMercaderia);
    }

    @Override
    @Transactional(readOnly = true)
    public String getConformidadServicioGenerateContent(ParameterConformidadServicioPdfDTO parameterConformidadServicioPdfDTO){
        byte[] generateConformidadServicio = PdfGeneratorFactory.getJasperGenerator().generateConformidadServicio(parameterConformidadServicioPdfDTO);
        return Base64.getEncoder().encodeToString(generateConformidadServicio);
    }


    @Override
    @Transactional(readOnly = true)
    public String getConformidadServicioGenerateContent(DocumentoAceptacion documentoAceptacion, List<DocumentoAceptacionDetalle> documentoAceptacionDetalle){
        try {
            return PdfGeneratorFactory.getJasperGenerator().generateConformidadServicio(documentoAceptacion, documentoAceptacionDetalle);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
        
    }


   @Transactional(readOnly = true)
   public Object getByActaSustento(Integer idActaSustento){

        List<DocumentoAceptacion> documentoAceptacion = this.documentoAceptacionRepository.getByActaSustento(idActaSustento);
        return documentoAceptacion;

   }

    @Transactional(readOnly = true)
    public List <MtrTipoDocumento> getByArchivoActaSustento(Integer idProveedor){
        List<ProveedorLineaComercial> proveedorLineaComercials = this.proveedorLineaComercialRepository.getListLineaComercialByIdProveedor(idProveedor);
        List <MtrTipoDocumento> mtrTipoDocumentos = new ArrayList<>();
        List <MtrTipoDocumento> mtrTipoDocumentosFinal = new ArrayList<>();
        for(ProveedorLineaComercial item:proveedorLineaComercials){
           LineaComercial linea = item.getFamilia();
           Integer respuesta = linea.getIdPadre();
            mtrTipoDocumentos = this.mtrTipoDocumentoRepository.getfindByCodigoAgrupadoAndLineaComercial("ACS",respuesta);
            for(MtrTipoDocumento mtr:mtrTipoDocumentos) {
                mtrTipoDocumentosFinal.add(mtr);
            }
       }
        for(int i=0 ;i<mtrTipoDocumentosFinal.size()-1; i++){
            for(int j=mtrTipoDocumentosFinal.size()-1; j>i; j--){
                if(mtrTipoDocumentosFinal.get(j).getDescripcion().equals(mtrTipoDocumentosFinal.get(i).getDescripcion())){
                    mtrTipoDocumentosFinal.remove(j);
                }
            }
        }

       /*LineaComercial linea = lineaComercial.getFamilia();
       Integer respuesta = linea.getIdPadre();*/

        return mtrTipoDocumentosFinal;

    }

    @Override
    public Optional<DocumentoAceptacion> getDocumentoAceptacionId(Integer idDocumentoAceptacion) {
        return documentoAceptacionRepository.findById(idDocumentoAceptacion);
    }
}
