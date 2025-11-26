package com.incloud.hcp.service.extractor.impl;

import com.incloud.hcp.config.ToDate;
import com.incloud.hcp.domain.BienServicio;
import com.incloud.hcp.domain.DocumentoAceptacion;
import com.incloud.hcp.domain.DocumentoAceptacionDetalle;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.enums.DocumentoAceptacionEstadoEnum;
import com.incloud.hcp.enums.DocumentoAceptacionTipoEnum;
import com.incloud.hcp.repository.BienServicioRepository;
import com.incloud.hcp.repository.DocumentoAceptacionDetalleRepository;
import com.incloud.hcp.repository.DocumentoAceptacionRepository;
import com.incloud.hcp.repository.ProveedorRepository;
import com.incloud.hcp.service.extractor.ExtractorDocumentoAceptacionService;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.ws.enums.CodEmpresaEnum;
import com.incloud.hcp.ws.ingresomercaderias.bean.GuiaBodyDetalle;
import com.incloud.hcp.ws.ingresomercaderias.bean.GuiaBodyResponse;
import com.incloud.hcp.ws.ingresomercaderias.bean.ObtenerGuiaResponse;
import com.incloud.hcp.ws.ingresomercaderias.service.GSObtenerGuiaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class ExtractorDocumentoAceptacionServiceImpl implements ExtractorDocumentoAceptacionService {

    @Autowired
    private GSObtenerGuiaService gsObtenerGuiaService;

    @Autowired
    private DocumentoAceptacionRepository documentoAceptacionRepository;

    @Autowired
    private DocumentoAceptacionDetalleRepository documentoAceptacionDetalleRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private BienServicioRepository bienServicioRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AtomicBoolean daProcessing = new AtomicBoolean(false);

    @Override
    public void extraerDocumentoAceptacion(String fechaInicio, String fechaFinal, boolean enviarCorreoAprobacion) throws Exception {

        List<Integer> svEmpresas = new ArrayList<>();
        svEmpresas.add(CodEmpresaEnum.SILVESTRE.getValor());
        //empresas.add(CodEmpresaEnum.NEOAGRUM.getValor());

        if (!daProcessing.get()) {
            try {
                svEmpresas.forEach(e -> { // Empresas (Silvestre - Neoagrum)
                    String header1 = "INI: " + DateUtils.getCurrentTimestamp().toString() + " -- EXTR DA -- RANGO: " + fechaInicio + " - " + fechaFinal + " // ";
                    ObtenerGuiaResponse responseWS = gsObtenerGuiaService.obtenerGuia(e, fechaInicio, fechaFinal, "");
                    List<GuiaBodyResponse> body = responseWS.getBody();
                    logger.info(header1 + "CANTIDAD DE EM ENCONTRADAS: " + body.size());
                    if (body.size() > 0) {
                        List<String> codigoProveedores = body.stream().map(GuiaBodyResponse::getCodigoProveedor).distinct().collect(Collectors.toList());
                        List<Proveedor> proveedors = proveedorRepository.findByRucs(codigoProveedores);
                        body.forEach(gc -> {
                            //
                            Optional<Proveedor> proveedorOpt = proveedors.stream().filter(p -> p.getRuc().equals(gc.getCodigoProveedor())).findFirst();
                            Proveedor p = proveedorOpt.orElse(new Proveedor(gc.getCodigoProveedor()));
                            //
                            Integer idEntradaMercaderiaExistente = documentoAceptacionRepository.getIdDocumentoAceptacionByNumero(String.valueOf(gc.getOpGuiaCompra()));
                            String header2 = header1.concat("RUC: " + gc.getCodigoProveedor() + " // " + "EM: " + gc.getOpGuiaCompra());
                            if (idEntradaMercaderiaExistente == null) { // si no existe previamente la entrada de mercaderia
                                DocumentoAceptacion entradaMercaderia = new DocumentoAceptacion();
                                this.getGCValue(e, entradaMercaderia, gc, p);

                                logger.info(header2 + " // WRITING NEW EM: " + entradaMercaderia.toString());
                                DocumentoAceptacion newEntradaMercaderia = documentoAceptacionRepository.save(entradaMercaderia);
                                Integer idEntradaMercaderia = entradaMercaderia.getId();

                                List<String> codigoProductos = gc.getDetalle().stream().map(GuiaBodyDetalle::getCodigoProducto).distinct().collect(Collectors.toList());
                                List<BienServicio> bienServicios = bienServicioRepository.findByCodigos(codigoProductos);

                                gc.getDetalle().forEach(det -> {
                                    //
                                    Optional<BienServicio> bienServicioOpt = bienServicios.stream().filter(bss -> bss.getCodigoSap().equals(det.getCodigoProducto())).findFirst();
                                    BienServicio bs = bienServicioOpt.orElse(new BienServicio(det.getCodigoProducto(), "", "", det.getKardex()));
                                    //
                                    DocumentoAceptacionDetalle entradaMercaderiaDetalle = new DocumentoAceptacionDetalle();
                                    entradaMercaderiaDetalle.setIdDocumentoAceptacion(idEntradaMercaderia);
                                    this.getGCDetalleValue(newEntradaMercaderia, entradaMercaderiaDetalle, det, p, bs);

                                    logger.info(header2 + " // WRITING NEW EM POS: " + entradaMercaderiaDetalle.toString());
                                    documentoAceptacionDetalleRepository.save(entradaMercaderiaDetalle);
                                });
                            } else {
                                // EM Ya existe en Hana
                            }
                        });
                    }
                    logger.info(header1 + "FINISHED");

                });

                if (daProcessing.get())
                    daProcessing.set(!daProcessing.get());
            } catch (Exception e) {
                if (daProcessing.get())
                    daProcessing.set(!daProcessing.get());
                logger.error(e.getMessage(), e.getCause());
                throw new Exception(e);
            }
        } else {
            logger.info("INI: " + DateUtils.getCurrentTimestamp().toString() + " // DocumentoAceptacion Extraction esta procesando");
        }
    }

    private void getGCValue(Integer idEmpresa, DocumentoAceptacion entradaMercaderia, GuiaBodyResponse gc, Proveedor p) {
        //entradaMercaderia.setCodigoMoneda(primerItem.getCodigoMoneda());
        //entradaMercaderia.setFechaAceptacion();
        Optional<Date> dateEmision = ToDate.getDateFromString(gc.getFecha(), "MM/dd/yyyy");
        entradaMercaderia.setFechaEmision(dateEmision.get());
        entradaMercaderia.setFechaPublicacion(DateUtils.getCurrentTimestamp());
        entradaMercaderia.setIdEstadoDocumentoAceptacion(DocumentoAceptacionEstadoEnum.ACTIVO.getId()); //Default 1 - Activa
        //entradaMercaderia.setIdOrdenCompra(ordenCompra.getId()); // no aplica - es a nivel de  detalle
        entradaMercaderia.setIdTipoDocumentoAceptacion(DocumentoAceptacionTipoEnum.ENTRADA_MERCADERIA.getId());
        entradaMercaderia.setNumeroDocumentoAceptacion(String.valueOf(gc.getOpGuiaCompra()));
        entradaMercaderia.setNumeroGuiaProveedor(gc.getSerie() + "-" + gc.getNumero());
        //entradaMercaderia.setNumeroOrdenCompra(numeroOrdenCompra); // no aplica - es a nivel de detalle
        //entradaMercaderia.setPosicionOrdenCompra();
        entradaMercaderia.setProveedorRuc(gc.getCodigoProveedor());
        entradaMercaderia.setProveedorRazonSocial(p.getRazonSocial());
        //entradaMercaderia.setStatusSap();
        //entradaMercaderia.setUsuarioSapAutoriza();
        //entradaMercaderia.setUsuarioSapRecepcion();
        entradaMercaderia.setOpGuiaCompra(gc.getOpGuiaCompra());
        entradaMercaderia.setCodigoAlmacen(gc.getCodigoAlmacen());
        entradaMercaderia.setCodigoDireccion(gc.getCodigoDireccion());
        entradaMercaderia.setObservacion(gc.getObservacion());
    }

    private void getGCDetalleValue(DocumentoAceptacion documentoAceptacion, DocumentoAceptacionDetalle detalle, GuiaBodyDetalle det, Proveedor p,  BienServicio bs) {

        detalle.setCantidadAceptadaCliente(new BigDecimal(det.getCantidad()));
        //detalle.setCantidadPendiente(item.getCantidadPendiente());
        detalle.setCodigoSapBienServicio(det.getCodigoProducto());
        detalle.setDescripcionBienServicio(""); // Buscar del maestro
        detalle.setIdEstadoDocumentoAceptacionDetalle(DocumentoAceptacionEstadoEnum.ACTIVO.getId()); //Default 1 - Activa
        //detalle.setIndicadorImpuesto();
        detalle.setMovimiento("");
        //detalle.setNumDocApectacionRelacionado();
        //detalle.setNumItemRelacionado();
        detalle.setNumeroDocumentoAceptacion(documentoAceptacion.getNumeroDocumentoAceptacion());
        //detalle.setNumeroItem(); //por hacerlo
        //detalle.setNumeroOrdenCompra();
        //detalle.setPosicionOrdenCompra();
        /* Obtener el precio en base al id_amarreOC, buscar por el código en la tabla oc_detalle y capturar el precio. */
        //detalle.setPrecioUnitario(0);
        //detalle.setUnidadMedida();
        //detalle.setValorRecibido(item.getValorRecibido());
        //detalle.setValorRecibidoMonedalocal(item.getValorRecibidoMonedalocal());

        detalle.setKardex(det.getKardex());
        detalle.setDescripcionUnidadMedida(det.getUnidad());
        detalle.setObservacion(det.getObservacion());
        detalle.setOpGuiaCompra(documentoAceptacion.getOpGuiaCompra());
        detalle.setIdAmarre(det.getId_Amarre()); //  ID SILVESTRE
        detalle.setIdAmarreOC(det.getId_AmarreOC()); // OC DETALLE RELACIONADA
    }

    @Override
    public boolean toggleDocumentoAceptacionExtractionProcessingState() {
        daProcessing.set(!daProcessing.get());
        return daProcessing.get();
    }

    @Override
    public boolean currentDocumentoAceptacionExtractionProcessingState() {
        return daProcessing.get();
    }
}
