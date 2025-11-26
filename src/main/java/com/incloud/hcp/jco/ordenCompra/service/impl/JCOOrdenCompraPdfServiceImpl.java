package com.incloud.hcp.jco.ordenCompra.service.impl;

import com.incloud.hcp.domain.*;
import com.incloud.hcp.enums.OrdenCompraEstadoEnum;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.jco.ordenCompra.dto.*;
import com.incloud.hcp.jco.ordenCompra.service.JCOOrdenCompraPdfService;
import com.incloud.hcp.repository.BienServicioRepository;
import com.incloud.hcp.repository.OrdenCompraDetalleRepository;
import com.incloud.hcp.repository.OrdenCompraRepository;
import com.incloud.hcp.repository.ProveedorRepository;
import com.incloud.hcp.util.DateUtils;
//import com.sap.conn.jco.*;
import com.incloud.hcp.util.Utils;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import io.vavr.control.Try;

import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Base64;
import java.io.*;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public class JCOOrdenCompraPdfServiceImpl implements JCOOrdenCompraPdfService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String LOGO_FOOTER = "com/incloud/hcp/templates/img/jrclogo.jpg";
    @Value("${destination.rfc.profit}")
    private String destinationProfit;

    private OrdenCompraRepository ordenCompraRepository;
    private OrdenCompraDetalleRepository ordenCompraDetalleRepository;
    private ProveedorRepository proveedorRepository;

    private BienServicioRepository bienServicioRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    public JCOOrdenCompraPdfServiceImpl(OrdenCompraRepository ordenCompraRepository, OrdenCompraDetalleRepository ordenCompraDetalleRepository, ProveedorRepository proveedorRepository, BienServicioRepository bienServicioRepository) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.ordenCompraDetalleRepository = ordenCompraDetalleRepository;
        this.proveedorRepository = proveedorRepository;
        this.bienServicioRepository = bienServicioRepository;
    }

    @Override
    public OrdenCompraPdfDto extraerOrdenCompraPdfDtoRFC(String numeroOrdenCompra) throws Exception {
        try {
            String FUNCION_RFC = "ZPE_MM_COMPRAS_DETAIL_PDF";

//            JCoDestination destination = JCoDestinationManager.getDestination(destinationProfit);
//            JCoRepository repository = destination.getRepository();
//            JCoFunction jCoFunction = repository.getFunction(FUNCION_RFC);
//            this.mapFilters(jCoFunction, numeroOrdenCompra);
//            jCoFunction.execute(destination);
//
//            JCoParameterList tableParameterList = jCoFunction.getTableParameterList();
//            JCoParameterList exportParameterList = jCoFunction.getExportParameterList();
//
//            BigDecimal montoSubtotal = Optional.ofNullable(exportParameterList.getBigDecimal("PO_SUBTO")).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
//            BigDecimal montoDescuento = Optional.ofNullable(exportParameterList.getBigDecimal("PO_TOT_COND")).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
//            BigDecimal montoIgv = Optional.ofNullable(exportParameterList.getBigDecimal("PO_IGV")).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
//            BigDecimal montoImporteTotal = Optional.ofNullable(exportParameterList.getBigDecimal("PO_TOTAL")).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

            OrdenCompraPdfExtractorMapper ordenCompraPdfExtractorMapper = null;//OrdenCompraPdfExtractorMapper.newMapper(tableParameterList);

            List<OrdenCompraPdfDto> ordenCompraPdfDtoList = ordenCompraPdfExtractorMapper.getOrdenCompraPdfDtoList();
            List<SociedadDto> sociedadDtoList = ordenCompraPdfExtractorMapper.getSociedadDtoList();
            List<OrdenCompraPosicionPdfDto> ordenCompraPosicionPdfDtoList = ordenCompraPdfExtractorMapper.getOrdenCompraPosicionPdfDtoList();
            List<OrdenCompraPosicionClaseCondicionPdfDto> claseCondicionPdfDtoList = ordenCompraPdfExtractorMapper.getClaseCondicionPdfDtoList();
            List<TextoPosicionDto> textoPosicionDtoList = ordenCompraPdfExtractorMapper.getTextoPosicionList("PO_TEXTO_POS");
            List<TextoPosicionDto> textoRegistroInfoDtoList = ordenCompraPdfExtractorMapper.getTextoPosicionList("PO_TEXTO_REG_POS");
            List<TextoPosicionDto> textoAmpliadoMaterialDtoList = ordenCompraPdfExtractorMapper.getTextoPosicionList("PO_TEXTO_AMPL_MAT");

            List<String> textoCabeceraList = ordenCompraPdfExtractorMapper.getTextoList("PO_TEXTO_CABECERA");
            List<String> textoCabeceraObservacionesList = ordenCompraPdfExtractorMapper.getTextoList("PO_TEXTO_CABECERA_OBS");
            List<String> textoFechaList = ordenCompraPdfExtractorMapper.getTextoList("PO_FECHA");
            List<String> textoNotasImportantesList = ordenCompraPdfExtractorMapper.getTextoList("PO_NOTAS_IM");
            List<String> textoNotasProveedorList = ordenCompraPdfExtractorMapper.getTextoList("PO_NOTAS_PRO");
            List<String> textoNotasClausulaList = ordenCompraPdfExtractorMapper.getTextoList("PO_CLAUSULAS");

            String header1 = "INI: " + DateUtils.getCurrentTimestamp().toString() + " -- EXTR PDF OC: " + numeroOrdenCompra;
            logger.error(header1 + " // Extraccion PDF Orden de Compra");

            OrdenCompraPdfDto ordenCompraPdfDto = new OrdenCompraPdfDto();

            if (ordenCompraPdfDtoList.size() == 1) {
                ordenCompraPdfDto = ordenCompraPdfDtoList.get(0);

                if (sociedadDtoList.size() == 1) {
                    SociedadDto sociedadDto = sociedadDtoList.get(0);

                    ordenCompraPdfDto.setClienteRazonSocial(sociedadDto.getRazonSocial());
                    ordenCompraPdfDto.setClienteRuc(sociedadDto.getRuc());
                    ordenCompraPdfDto.setClienteTelefono(sociedadDto.getTelefono());
                    ordenCompraPdfDto.setClienteDireccion(sociedadDto.getCalle() + " " + sociedadDto.getNumero() + (!sociedadDto.getPoblacion().equals("") ? (" - " + sociedadDto.getPoblacion()) : "") + (!sociedadDto.getDistrito().equals("") ? (" - " + sociedadDto.getDistrito()) : ""));
                }

                if (ordenCompraPdfDto.getProveedorNumero() != null && !ordenCompraPdfDto.getProveedorNumero().isEmpty()) {
                    ordenCompraPdfDto.setProveedorNumero(String.valueOf(Integer.parseInt(ordenCompraPdfDto.getProveedorNumero())));
                }

//                ordenCompraPdfDto.setMontoSubtotal(montoSubtotal);
//                ordenCompraPdfDto.setMontoDescuento(montoDescuento);
//                ordenCompraPdfDto.setMontoIgv(montoIgv);
//                ordenCompraPdfDto.setMontoImporteTotal(montoImporteTotal);

                logger.error(header1 + " // FOUND ONE OC: " + ordenCompraPdfDto.toString()); // MUESTRA SIEMPRE LOS DATOS DE LA OC QUE LLEGA DE SAP

                Optional<OrdenCompra> optionalOrdenCompra = ordenCompraRepository.getOrdenCompraActivaByNumero(numeroOrdenCompra);

                if (optionalOrdenCompra.isPresent()) { // OC ya existe en HANA
                    OrdenCompra ordenCompraExistente = optionalOrdenCompra.get();

                    if (ordenCompraExistente.getIdEstadoOrdenCompra().compareTo(OrdenCompraEstadoEnum.ANULADA.getId()) != 0) {
                        ordenCompraPdfDto.setOrdenCompraVersion(String.valueOf(ordenCompraExistente.getVersion()));

                        //&nbsp;
                        StringBuilder txtBuilder1 = new StringBuilder();
                        int[] txtCounter1 = new int[]{0};
                        textoCabeceraList.forEach(txt -> {
                            if (txtCounter1[0] == 0)
                                txtBuilder1.append(txt);
                            else
                                txtBuilder1.append(" <br> ".concat(txt));

                            txtCounter1[0]++;
                        });
                        ordenCompraPdfDto.setTextoCabecera(txtBuilder1.toString());

                        StringBuilder txtBuilder2 = new StringBuilder();
                        int[] txtCounter2 = new int[]{0};
                        textoCabeceraObservacionesList.forEach(txt -> {
                            if (txtCounter2[0] == 0)
                                txtBuilder2.append(txt);
                            else
                                txtBuilder2.append(" <br> ".concat(txt));

                            txtCounter2[0]++;
                        });
                        ordenCompraPdfDto.setTextoCabeceraObservaciones(txtBuilder2.toString());

                        StringBuilder txtBuilder3 = new StringBuilder();
                        int[] txtCounter3 = new int[]{0};
                        textoFechaList.forEach(txt -> {
                            if (txtCounter3[0] == 0)
                                txtBuilder3.append(txt);
                            else
                                txtBuilder3.append(" <br> ".concat(txt));

                            txtCounter3[0]++;
                        });
                        ordenCompraPdfDto.setTextoFecha(txtBuilder3.toString());

                        StringBuilder txtBuilder4 = new StringBuilder();
                        int[] txtCounter4 = new int[]{0};
                        textoNotasImportantesList.forEach(txt -> {
                            if (txtCounter4[0] == 0)
                                txtBuilder4.append(txt);
                            else
                                txtBuilder4.append(" <br> ".concat(txt));

                            txtCounter4[0]++;
                        });
                        ordenCompraPdfDto.setTextoNotasImportantes(txtBuilder4.toString());

                        StringBuilder txtBuilder5 = new StringBuilder();
                        int[] txtCounter5 = new int[]{0};
                        textoNotasProveedorList.forEach(txt -> {
                            if (txtCounter5[0] == 0)
                                txtBuilder5.append(txt);
                            else
                                txtBuilder5.append(" <br> ".concat(txt));

                            txtCounter5[0]++;
                        });
                        ordenCompraPdfDto.setTextoNotasProveedor(txtBuilder5.toString());

                        StringBuilder txtBuilder6 = new StringBuilder();
                        int[] txtCounter6 = new int[]{0};
                        textoNotasClausulaList.forEach(txt -> {
                            if (txtCounter6[0] == 0)
                                txtBuilder6.append(txt);
                            else
                                txtBuilder6.append(" <br> ".concat(txt));

                            txtCounter6[0]++;
                        });
                        ordenCompraPdfDto.setTextoNotasClausula(txtBuilder6.toString());

                        logger.error(header1 + " // MODDED ONE OC: " + ordenCompraPdfDto.toString()); // MUESTRA SIEMPRE LOS DATOS DE LA OC QUE LLEGA DE SAP

                        int[] counterArray = new int[]{0};
                        ordenCompraPosicionPdfDtoList.forEach(ocp -> {
                            counterArray[0]++;
                            logger.error(header1 + " // FOUND POS " + counterArray[0] + ": " + ocp.toString()); // MUESTRA SIEMPRE LOS DATOS DE LAS POSICIONES QUE LLEGAN DE SAP

                            if (ocp.getMaterial() != null && !ocp.getMaterial().isEmpty()) {
                                ocp.setMaterial(String.valueOf(Integer.parseInt(ocp.getMaterial())));
                            }

                            BigDecimal precioUnitarioBase = ocp.getPrecioUnitario();
                            BigDecimal precioUnitario = precioUnitarioBase.divide(ocp.getCantidadBase(), 4, RoundingMode.HALF_UP);

                            ocp.setPrecioUnitario(precioUnitario);
                            ocp.setImporte(ocp.getCantidad().multiply(precioUnitario).setScale(4, RoundingMode.HALF_UP));

                            List<TextoPosicionDto> filteredTextoAmpliadoMaterialDtoList = textoAmpliadoMaterialDtoList.stream()
                                    .filter(tp -> tp.getPosicion().equals(ocp.getPosicion()))
                                    .collect(Collectors.toList());

                            if (filteredTextoAmpliadoMaterialDtoList.size() > 0) {
                                StringBuilder textoAmpliadoMaterialBuilder = new StringBuilder();
                                int[] tamCounter = new int[]{0};
                                filteredTextoAmpliadoMaterialDtoList.forEach(tam -> {
                                    if (tamCounter[0] == 0)
                                        textoAmpliadoMaterialBuilder.append(tam.getLinea());
                                    else
                                        textoAmpliadoMaterialBuilder.append(" ".concat(tam.getLinea()));

                                    tamCounter[0]++;
                                });
                                ocp.setDescripcion(textoAmpliadoMaterialBuilder.toString());
                            }

                            OrdenCompraPosicionDataAdicionalPdfDto posicionDataAdicionalPdfDto = new OrdenCompraPosicionDataAdicionalPdfDto();

                            List<OrdenCompraPosicionClaseCondicionPdfDto> filteredClaseCondicionList = claseCondicionPdfDtoList.stream()
                                    .filter(cc -> cc.getPosicion().equals(ocp.getPosicion()))
                                    .collect(Collectors.toList());

                            posicionDataAdicionalPdfDto.setOrdenCompraPosicionClaseCondicionPdfDtoList(filteredClaseCondicionList);

                            StringBuilder textoPosicionBuilder = new StringBuilder();
                            int[] tpCounter = new int[]{0};
                            textoPosicionDtoList.stream()
                                    .filter(tp -> tp.getPosicion().equals(ocp.getPosicion()))
                                    .forEach(tp -> {
                                        if (tpCounter[0] == 0)
                                            textoPosicionBuilder.append(tp.getLinea());
                                        else
                                            textoPosicionBuilder.append(" ".concat(tp.getLinea()));

                                        tpCounter[0]++;
                                    });
                            posicionDataAdicionalPdfDto.setTextoPosicion(textoPosicionBuilder.toString());

                            StringBuilder textoRegistroInfoBuilder = new StringBuilder();
                            int[] triCounter = new int[]{0};
                            textoRegistroInfoDtoList.stream()
                                    .filter(tri -> tri.getPosicion().equals(ocp.getPosicion()))
                                    .forEach(tri -> {
                                        if (triCounter[0] == 0)
                                            textoRegistroInfoBuilder.append(tri.getLinea());
                                        else
                                            textoRegistroInfoBuilder.append(" ".concat(tri.getLinea()));

                                        triCounter[0]++;
                                    });
                            posicionDataAdicionalPdfDto.setTextoRegistroInfo(textoRegistroInfoBuilder.toString());

                            ocp.setOrdenCompraPosicionDataAdicionalPdfDto(posicionDataAdicionalPdfDto);

                            if (ocp.getPosicion() != null && !ocp.getPosicion().isEmpty()) {
                                ocp.setPosicion(String.valueOf(Integer.parseInt(ocp.getPosicion())));
                            }

                            logger.error(header1 + " // MODDED POS " + counterArray[0] + ": " + ocp.toString()); // MUESTRA SIEMPRE LOS DATOS DE LAS POSICIONES YA MODIFICADOS
                        });

                        ordenCompraPdfDto.setOrdenCompraPosicionPdfDtoList(ordenCompraPosicionPdfDtoList);
                    }
                }
            }
            logger.error(header1 + " // FULL OC: " + ordenCompraPdfDto.toString());

            return ordenCompraPdfDto;
        } catch (Exception e) {
            logger.error(e.getMessage(), e.getCause());
            throw new Exception(e);
        }
    }

    public String obtenerBase64OC(String numeroOrden) throws Exception {


/*
        try {

            //String data = new String(Files.readAllBytes(Paths.get("src/main/resources/word/PJ-BACKEND-3.html")));
            HtmlEmail htmlMail = new HtmlEmail();
            VelocityContext context = new VelocityContext();

            OrdenCompra ordenCompra = ordenCompraRepository.findByNumeroOrdenCompra(numeroOrden);

            List<OrdenCompraDetalle> ordenCompraDetalles = ordenCompraDetalleRepository.getAllByIdOrdenCompra(ordenCompra.getId());
            if (ordenCompra.getLugarEntrega() != null) {
                String data = "word/template_oc.html";
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                context.put("FECHA", formatter.format(ordenCompra.getFechaRegistro()));

                context.put("ORDEN_COMPRA", ordenCompra.getNumeroOrdenCompra());

                String lugar_entrega = "";
                if (ordenCompra.getLugarEntrega() != null) {
                    lugar_entrega = ordenCompra.getLugarEntrega();
                } else {
                    lugar_entrega = "";
                }

                context.put("LUGAR_ENTREGA", lugar_entrega);

                *//*Obtenemos path *//*
                String imagePath = getClass().getClassLoader().getResource("word/jrclogo.jpg").toExternalForm();
                context.put("JRC_LOGO", imagePath);

                String firmaPath = getClass().getClassLoader().getResource("word/firma.jpg").toExternalForm();
                context.put("JRC_FIRMA", firmaPath);


                *//*Buscamos proveedor por RUC *//*
                Proveedor proveedor = proveedorRepository.getProveedorByRuc(ordenCompra.getProveedorRuc());
                String direccion = "";

                if (proveedor != null) {
                    direccion = proveedor.getDireccionFiscal();
                }

                String cuerpo_proveedor = "<p>" + ordenCompra.getProveedorRazonSocial() + "</p> <p>" + direccion + "</p> <p><strong>RUC: </strong> " + ordenCompra.getProveedorRuc() + "</p>";

                context.put("PROVEEDOR", cuerpo_proveedor);

                String comprador = "<p><strong>Nombre: </strong> " +
                        ((ordenCompra.getCompradorNombre() == null || ordenCompra.getCompradorNombre().isEmpty())
                                ? "-"
                                : ordenCompra.getCompradorNombre()) +
                        "</p>" +
                        "<p><strong>Teléfono: </strong> " +
                        ((ordenCompra.getCompradorTelefono() == null || ordenCompra.getCompradorTelefono().isEmpty())
                                ? "-"
                                : ordenCompra.getCompradorTelefono()) +
                        "</p>" +
                        "<p><strong>Email: </strong> " +
                        ((ordenCompra.getCompradorEmail() == null || ordenCompra.getCompradorEmail().isEmpty())
                                ? "-"
                                : ordenCompra.getCompradorEmail()) +
                        "</p>";

                String solicitante = "<p><strong>Sede: </strong> " +
                        ((ordenCompra.getSedeSolicitud() == null || ordenCompra.getSedeSolicitud().isEmpty())
                                ? "-"
                                : ordenCompra.getSedeSolicitud()) +
                        "</p><p><strong>Nombre: </strong> " +
                        ((ordenCompra.getUltimoLiberadorUsuarioNombre() == null || ordenCompra.getUltimoLiberadorUsuarioNombre().isEmpty())
                                ? "-"
                                : ordenCompra.getUltimoLiberadorUsuarioNombre()) +
                        "</p>";

                context.put("COMPRADOR", comprador);
                context.put("SOLICITANTE", solicitante);
                context.put("FACTURAR_A", "<p>JRC INGENIERIA Y CONSTRUCCION SAC</p><p>20508891149</p><p>AV.JORGE BASADRE NRO.233 INT.601CENTRO EMPRESARIAL BASADRE 233 (INT. 601-602) LIMA LIMA</p>");
                context.put("CONDICION_PAGO", ordenCompra.getCondicionPagoDescripcion());
                context.put("MONEDA", ordenCompra.getCodigoMondeda());

                context.put("SUB_TOTAL", ordenCompra.getSubtotal().setScale(2, BigDecimal.ROUND_HALF_UP));
                context.put("IGV", ordenCompra.getValorImpuesto().setScale(2, BigDecimal.ROUND_HALF_UP));
                context.put("TOTAL", ordenCompra.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP));

                *//*Armamos detalle*//*
                StringBuilder detallesx = new StringBuilder();
                AtomicInteger ind = new AtomicInteger(0); // Usamos AtomicInteger para manejar el índice

                ordenCompraDetalles.forEach(OC_detalle -> {
                    BienServicio nro_pieza = this.bienServicioRepository.getByCodigoSap(OC_detalle.getCodigoSapBienServicio());
                    if (nro_pieza == null) {
                        nro_pieza = new BienServicio();
                        nro_pieza.setNro_pieza(" ");
                    }
                    int currentIndex = ind.incrementAndGet(); //Incrementa y obtiene el índice actual
                    String htmlRow =
                            "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + currentIndex + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getNumeroSolped() + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getCodigoSapBienServicio() + "<br />" + (OC_detalle.getDescripcionBienServicio() != null ? OC_detalle.getDescripcionBienServicio() : "") + "<br />" + (nro_pieza.getNro_pieza() != null ? nro_pieza.getNro_pieza().replace("&", "&amp;") : "") + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getCantidad().setScale(2, BigDecimal.ROUND_HALF_UP) + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getUnidadMedidaBienServicio() + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + formatter.format(OC_detalle.getFechaEntrega()) + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + formatter.format(OC_detalle.getFechaEntrega()) + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getPrecioUnitario().setScale(2, BigDecimal.ROUND_HALF_UP) + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getPrecioTotal().setScale(2, BigDecimal.ROUND_HALF_UP) + "</td>";

                    detallesx.append("<tr>").append(htmlRow).append("</tr>"); // Añadimos filas <tr> para estructurar la tabla
                });

                // Convierte el contenido a cadena si lo necesitas
                String detallesHtml = detallesx.toString();
                context.put("TRS", detallesHtml);


                String content = Optional.ofNullable(data)
                        .map(url -> url + "")
                        .map(template -> {
                            int i = 0;
                            return getContentMail(context, template);
                        })
                        .orElse("");
                System.out.println("content");

                //String base64Content = encodeToBase64(content);
                //return base64Content;

                return content;
            }else {
                String data = "word/template_oc_detalle_lugar.html";
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                context.put("FECHA", formatter.format(ordenCompra.getFechaRegistro()));

                context.put("ORDEN_COMPRA", ordenCompra.getNumeroOrdenCompra());

               *//* String lugar_entrega = "";
                if (ordenCompra.getLugarEntrega() != null) {
                    lugar_entrega = ordenCompra.getLugarEntrega();
                } else {
                    lugar_entrega = "";
                }

                context.put("LUGAR_ENTREGA", lugar_entrega);*//*

         *//*Obtenemos path *//*
                String imagePath = getClass().getClassLoader().getResource("word/jrclogo.jpg").toExternalForm();
                context.put("JRC_LOGO", imagePath);

                String firmaPath = getClass().getClassLoader().getResource("word/firma.jpg").toExternalForm();
                context.put("JRC_FIRMA", firmaPath);


                *//*Buscamos proveedor por RUC *//*
                Proveedor proveedor = proveedorRepository.getProveedorByRuc(ordenCompra.getProveedorRuc());
                String direccion = "";

                if (proveedor != null) {
                    direccion = proveedor.getDireccionFiscal();
                }

                String cuerpo_proveedor = "<p>" + ordenCompra.getProveedorRazonSocial() + "</p> <p>" + direccion + "</p> <p><strong>RUC: </strong> " + ordenCompra.getProveedorRuc() + "</p>";

                context.put("PROVEEDOR", cuerpo_proveedor);

                String comprador = "<p><strong>Nombre: </strong> " +
                        ((ordenCompra.getCompradorNombre() == null || ordenCompra.getCompradorNombre().isEmpty())
                                ? "-"
                                : ordenCompra.getCompradorNombre()) +
                        "</p>" +
                        "<p><strong>Teléfono: </strong> " +
                        ((ordenCompra.getCompradorTelefono() == null || ordenCompra.getCompradorTelefono().isEmpty())
                                ? "-"
                                : ordenCompra.getCompradorTelefono()) +
                        "</p>" +
                        "<p><strong>Email: </strong> " +
                        ((ordenCompra.getCompradorEmail() == null || ordenCompra.getCompradorEmail().isEmpty())
                                ? "-"
                                : ordenCompra.getCompradorEmail()) +
                        "</p>";

                String solicitante = "<p><strong>Sede: </strong> " +
                        ((ordenCompra.getSedeSolicitud() == null || ordenCompra.getSedeSolicitud().isEmpty())
                                ? "-"
                                : ordenCompra.getSedeSolicitud()) +
                        "</p><p><strong>Nombre: </strong> " +
                        ((ordenCompra.getUltimoLiberadorUsuarioNombre() == null || ordenCompra.getUltimoLiberadorUsuarioNombre().isEmpty())
                                ? "-"
                                : ordenCompra.getUltimoLiberadorUsuarioNombre()) +
                        "</p>";

                context.put("COMPRADOR", comprador);
                context.put("SOLICITANTE", solicitante);
                context.put("FACTURAR_A", "<p>JRC INGENIERIA Y CONSTRUCCION SAC</p><p>20508891149</p><p>AV.JORGE BASADRE NRO.233 INT.601CENTRO EMPRESARIAL BASADRE 233 (INT. 601-602) LIMA LIMA</p>");
                context.put("CONDICION_PAGO", ordenCompra.getCondicionPagoDescripcion());
                context.put("MONEDA", ordenCompra.getCodigoMondeda());

                context.put("SUB_TOTAL", ordenCompra.getSubtotal().setScale(2, BigDecimal.ROUND_HALF_UP));
                context.put("IGV", ordenCompra.getValorImpuesto().setScale(2, BigDecimal.ROUND_HALF_UP));
                context.put("TOTAL", ordenCompra.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP));

                *//*Armamos detalle*//*
                StringBuilder detallesx = new StringBuilder();
                AtomicInteger ind = new AtomicInteger(0); // Usamos AtomicInteger para manejar el índice

                ordenCompraDetalles.forEach(OC_detalle -> {
                    BienServicio nro_pieza = this.bienServicioRepository.getByCodigoSap(OC_detalle.getCodigoSapBienServicio());
                    if (nro_pieza == null) {
                        nro_pieza = new BienServicio();
                        nro_pieza.setNro_pieza(" ");
                    }
                    int currentIndex = ind.incrementAndGet(); //Incrementa y obtiene el índice actual
                    String htmlRow =
                            "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + currentIndex + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getNumeroSolped() + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getCodigoSapBienServicio() + "<br />" + (OC_detalle.getDescripcionBienServicio() != null ? OC_detalle.getDescripcionBienServicio() : "") + "<br />" + (nro_pieza.getNro_pieza() != null ? nro_pieza.getNro_pieza().replace("&", "&amp;") : "") + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getCantidad().setScale(2, BigDecimal.ROUND_HALF_UP) + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getUnidadMedidaBienServicio() + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + formatter.format(OC_detalle.getFechaEntrega()) + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + formatter.format(OC_detalle.getFechaEntrega()) + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getPrecioUnitario().setScale(2, BigDecimal.ROUND_HALF_UP) + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getPrecioTotal().setScale(2, BigDecimal.ROUND_HALF_UP) + "</td>" +
                                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getLugarEntregaDetalle() + "</td>";

                    detallesx.append("<tr>").append(htmlRow).append("</tr>"); // Añadimos filas <tr> para estructurar la tabla
                });

                // Convierte el contenido a cadena si lo necesitas
                String detallesHtml = detallesx.toString();
                context.put("TRS", detallesHtml);


                String content = Optional.ofNullable(data)
                        .map(url -> url + "")
                        .map(template -> {
                            int i = 0;
                            return getContentMail(context, template);
                        })
                        .orElse("");
                System.out.println("content");

                //String base64Content = encodeToBase64(content);
                //return base64Content;

                return content;
            }

            } catch(Exception e){
                return "";
            }*/
        try {
            String data = "word/template_oc.html";
            //String data = new String(Files.readAllBytes(Paths.get("src/main/resources/word/PJ-BACKEND-3.html")));
            HtmlEmail htmlMail = new HtmlEmail();
            VelocityContext context = new VelocityContext();

            OrdenCompra ordenCompra = ordenCompraRepository.findByNumeroOrdenCompra(numeroOrden);
            List<OrdenCompraDetalle> ordenCompraDetalles = ordenCompraDetalleRepository.getAllByIdOrdenCompra(ordenCompra.getId());


            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            context.put("FECHA", formatter.format(ordenCompra.getFechaRegistro()));

            context.put("ORDEN_COMPRA", ordenCompra.getNumeroOrdenCompra());

            String lugar_entrega = "";
            if (ordenCompra.getLugarEntrega() != null) {
                lugar_entrega = ordenCompra.getLugarEntrega();
            } else {
                lugar_entrega = "";
            }

            context.put("LUGAR_ENTREGA", lugar_entrega);

            /*Obtenemos path */
            String imagePath = getClass().getClassLoader().getResource("word/jrclogo.jpg").toExternalForm();
            context.put("JRC_LOGO", imagePath);

            String firmaPath = getClass().getClassLoader().getResource("word/firma.jpg").toExternalForm();
            context.put("JRC_FIRMA", firmaPath);


            /*Buscamos proveedor por RUC */
            Proveedor proveedor = proveedorRepository.getProveedorByRuc(ordenCompra.getProveedorRuc());
            String direccion = "";

            if (proveedor != null) {
                direccion = proveedor.getDireccionFiscal();
            }

            String cuerpo_proveedor = "<p>" + ordenCompra.getProveedorRazonSocial() + "</p> <p>" + direccion + "</p> <p><strong>RUC: </strong> " + ordenCompra.getProveedorRuc() + "</p>";

            context.put("PROVEEDOR", cuerpo_proveedor);

            String comprador = "<p><strong>Nombre: </strong> " +
                    ((ordenCompra.getCompradorNombre() == null || ordenCompra.getCompradorNombre().isEmpty())
                            ? "-"
                            : ordenCompra.getCompradorNombre()) +
                    "</p>" +
                    "<p><strong>Teléfono: </strong> " +
                    ((ordenCompra.getCompradorTelefono() == null || ordenCompra.getCompradorTelefono().isEmpty())
                            ? "-"
                            : ordenCompra.getCompradorTelefono()) +
                    "</p>" +
                    "<p><strong>Email: </strong> " +
                    ((ordenCompra.getCompradorEmail() == null || ordenCompra.getCompradorEmail().isEmpty())
                            ? "-"
                            : ordenCompra.getCompradorEmail()) +
                    "</p>";

            String solicitante = "<p><strong>Sede: </strong> " +
                    ((ordenCompra.getSedeSolicitud() == null || ordenCompra.getSedeSolicitud().isEmpty())
                            ? "-"
                            : ordenCompra.getSedeSolicitud().replace("&", "&amp;")) +
                    "</p><p><strong>Nombre: </strong> " +
                    ((ordenCompra.getUltimoLiberadorUsuarioNombre() == null || ordenCompra.getUltimoLiberadorUsuarioNombre().isEmpty())
                            ? "-"
                            : ordenCompra.getUltimoLiberadorUsuarioNombre()) +
                    "</p>";

            context.put("COMPRADOR", comprador);
            context.put("SOLICITANTE", solicitante);
            context.put("FACTURAR_A", "<p>JRC INGENIERIA Y CONSTRUCCION SAC</p><p>20508891149</p><p>AV.JORGE BASADRE NRO.233 INT.601CENTRO EMPRESARIAL BASADRE 233 (INT. 601-602) LIMA LIMA</p>");
            context.put("CONDICION_PAGO", ordenCompra.getCondicionPagoDescripcion());
            context.put("MONEDA", ordenCompra.getCodigoMondeda());

            context.put("SUB_TOTAL", ordenCompra.getSubtotal().setScale(2, BigDecimal.ROUND_HALF_UP));
            context.put("IGV", ordenCompra.getValorImpuesto().setScale(2, BigDecimal.ROUND_HALF_UP));
            context.put("TOTAL", ordenCompra.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP));

            /*Armamos detalle*/
            StringBuilder detallesx = new StringBuilder();
            AtomicInteger ind = new AtomicInteger(0); // Usamos AtomicInteger para manejar el índice

            ordenCompraDetalles.forEach(OC_detalle -> {
                BienServicio nro_pieza = this.bienServicioRepository.getByCodigoSap(OC_detalle.getCodigoSapBienServicio());
                if (nro_pieza == null) {
                    nro_pieza = new BienServicio();
                    nro_pieza.setNro_pieza(" ");
                }
                int currentIndex = ind.incrementAndGet(); //Incrementa y obtiene el índice actual
                String htmlRow =
                        "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + currentIndex + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getNumeroSolped() + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getCodigoSapBienServicio() + "<br />" + escapeHtml(OC_detalle.getDescripcionBienServicio()) + "<br />" + escapeHtml(nro_pieza.getNro_pieza()) + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getCantidad().setScale(2, BigDecimal.ROUND_HALF_UP) + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getUnidadMedidaBienServicio() + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + formatter.format(OC_detalle.getFechaEntrega()) + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + formatter.format(OC_detalle.getFechaEntrega()) + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getPrecioUnitario().setScale(2, BigDecimal.ROUND_HALF_UP) + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getPrecioTotal().setScale(2, BigDecimal.ROUND_HALF_UP) + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + (OC_detalle.getLugarEntregaDetalle() != null ? OC_detalle.getLugarEntregaDetalle() : "-") + "</td>";

                detallesx.append("<tr>").append(htmlRow).append("</tr>"); // Añadimos filas <tr> para estructurar la tabla
            });

            // Convierte el contenido a cadena si lo necesitas
            String detallesHtml = detallesx.toString();
            context.put("TRS", detallesHtml);


            String content = Optional.ofNullable(data)
                    .map(url -> url + "")
                    .map(template -> {
                        int i = 0;
                        return getContentMail(context, template);
                    })
                    .orElse("");
            System.out.println("content");

            //String base64Content = encodeToBase64(content);
            //return base64Content;

            return content;


        } catch (Exception e) {
            return "";
        }

    }

    @Override
    public String obtenerBase64OCAdjuntoEmail(OrdenCompra ordenCompra, List<OrdenCompraDetalle> ordenCompraDetalles) throws Exception {

        try {
            String data = "word/template_oc.html";
            //String data = new String(Files.readAllBytes(Paths.get("src/main/resources/word/PJ-BACKEND-3.html")));
            HtmlEmail htmlMail = new HtmlEmail();
            VelocityContext context = new VelocityContext();


            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            context.put("FECHA", formatter.format(ordenCompra.getFechaRegistro()));

            context.put("ORDEN_COMPRA", ordenCompra.getNumeroOrdenCompra());

            String lugar_entrega = "";
            if (ordenCompra.getLugarEntrega() != null) {
                lugar_entrega = ordenCompra.getLugarEntrega();
            } else {
                lugar_entrega = "";
            }

            context.put("LUGAR_ENTREGA", lugar_entrega);

            /*Obtenemos path */
            String imagePath = getClass().getClassLoader().getResource("word/jrclogo.jpg").toExternalForm();
            context.put("JRC_LOGO", imagePath);

            String firmaPath = getClass().getClassLoader().getResource("word/firma.jpg").toExternalForm();
            context.put("JRC_FIRMA", firmaPath);


            /*Buscamos proveedor por RUC */
            Proveedor proveedor = proveedorRepository.getProveedorByRuc(ordenCompra.getProveedorRuc());
            String direccion = "";

            if (proveedor != null) {
                direccion = proveedor.getDireccionFiscal();
            }

            String cuerpo_proveedor = "<p>" + ordenCompra.getProveedorRazonSocial() + "</p> <p>" + direccion + "</p> <p><strong>RUC: </strong> " + ordenCompra.getProveedorRuc() + "</p>";

            context.put("PROVEEDOR", cuerpo_proveedor);

            String comprador = "<p><strong>Nombre: </strong> " +
                    ((ordenCompra.getCompradorNombre() == null || ordenCompra.getCompradorNombre().isEmpty())
                            ? "-"
                            : ordenCompra.getCompradorNombre()) +
                    "</p>" +
                    "<p><strong>Teléfono: </strong> " +
                    ((ordenCompra.getCompradorTelefono() == null || ordenCompra.getCompradorTelefono().isEmpty())
                            ? "-"
                            : ordenCompra.getCompradorTelefono()) +
                    "</p>" +
                    "<p><strong>Email: </strong> " +
                    ((ordenCompra.getCompradorEmail() == null || ordenCompra.getCompradorEmail().isEmpty())
                            ? "-"
                            : ordenCompra.getCompradorEmail()) +
                    "</p>";

            String solicitante = "<p><strong>Sede: </strong> " +
                    ((ordenCompra.getSedeSolicitud() == null || ordenCompra.getSedeSolicitud().isEmpty())
                            ? "-"
                            : ordenCompra.getSedeSolicitud().replace("&", "&amp;")) +
                    "</p><p><strong>Nombre: </strong> " +
                    ((ordenCompra.getUltimoLiberadorUsuarioNombre() == null || ordenCompra.getUltimoLiberadorUsuarioNombre().isEmpty())
                            ? "-"
                            : ordenCompra.getUltimoLiberadorUsuarioNombre()) +
                    "</p>";

            context.put("COMPRADOR", comprador);
            context.put("SOLICITANTE", solicitante);
            context.put("FACTURAR_A", "<p>JRC INGENIERIA Y CONSTRUCCION SAC</p><p>20508891149</p><p>AV.JORGE BASADRE NRO.233 INT.601CENTRO EMPRESARIAL BASADRE 233 (INT. 601-602) LIMA LIMA</p>");
            context.put("CONDICION_PAGO", ordenCompra.getCondicionPagoDescripcion());
            context.put("MONEDA", ordenCompra.getCodigoMondeda());

            context.put("SUB_TOTAL", ordenCompra.getSubtotal().setScale(2, BigDecimal.ROUND_HALF_UP));
            context.put("IGV", ordenCompra.getValorImpuesto().setScale(2, BigDecimal.ROUND_HALF_UP));
            context.put("TOTAL", ordenCompra.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP));

            /*Armamos detalle*/
            StringBuilder detallesx = new StringBuilder();
            AtomicInteger ind = new AtomicInteger(0); // Usamos AtomicInteger para manejar el índice

            ordenCompraDetalles.forEach(OC_detalle -> {
                BienServicio nro_pieza = this.bienServicioRepository.getByCodigoSap(OC_detalle.getCodigoSapBienServicio());
                if (nro_pieza == null) {
                    nro_pieza = new BienServicio();
                    nro_pieza.setNro_pieza(" ");
                }
                int currentIndex = ind.incrementAndGet(); //Incrementa y obtiene el índice actual
                String htmlRow =
                        "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + currentIndex + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getNumeroSolped() + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getCodigoSapBienServicio() + "<br />" + escapeHtml(OC_detalle.getDescripcionBienServicio()) + "<br />" + escapeHtml(nro_pieza.getNro_pieza()) + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getCantidad().setScale(2, BigDecimal.ROUND_HALF_UP) + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getUnidadMedidaBienServicio() + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + formatter.format(OC_detalle.getFechaEntrega()) + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + formatter.format(OC_detalle.getFechaEntrega()) + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getPrecioUnitario().setScale(2, BigDecimal.ROUND_HALF_UP) + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + OC_detalle.getPrecioTotal().setScale(2, BigDecimal.ROUND_HALF_UP) + "</td>" +
                                "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 6px;\">" + (OC_detalle.getLugarEntregaDetalle() != null ? OC_detalle.getLugarEntregaDetalle() : "-") + "</td>";

                detallesx.append("<tr>").append(htmlRow).append("</tr>"); // Añadimos filas <tr> para estructurar la tabla
            });

            // Convierte el contenido a cadena si lo necesitas
            String detallesHtml = detallesx.toString();
            context.put("TRS", detallesHtml);


            String content = Optional.ofNullable(data)
                    .map(url -> url + "")
                    .map(template -> {
                        int i = 0;
                        return getContentMail(context, template);
                    })
                    .orElse("");
            System.out.println("content");

            //String base64Content = encodeToBase64(content);
            //return base64Content;

            return content;


        } catch (Exception e) {
            return "";
        }

    }

    String generateCidResourceUtilsNuevo(HtmlEmail htmlMail, String imagen, String tipo) {
        try {
            logger.error("Obteniendo la imagen a embeber");
            Resource resource = resourceLoader.getResource("classpath:" + imagen);
            InputStream initialStream = resource.getInputStream();
            String tickets = UUID.randomUUID().toString();
            File f = new File("newLogo_" + tipo + "_.png");
            if (!f.exists()) {
                FileUtils.copyInputStreamToFile(initialStream, f);
            }
            logger.error("Colocando la imagen en el contenido HTML: " + f);
            String cid = htmlMail.embed(f);
            return cid;
        } catch (FileNotFoundException ex) {
            logger.error("Error al buscar el recurso", ex);
        } catch (EmailException ex) {
            logger.error("Error al colocar la imagen en el contenido HTML", ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("\\", "&#92;")
                .replace("'", "&#39;");
    }

    private String encodeToBase64(String content) {
        // Convertir el contenido de texto a bytes
        byte[] encodedBytes = Base64.getEncoder().encode(content.getBytes(StandardCharsets.UTF_8));
        // Retornar el contenido en formato Base64
        return new String(encodedBytes, StandardCharsets.UTF_8
        );
    }

    String getContentMail(VelocityContext context, String template) {
        logger.error("Generando el contenido de la notificación");
        VelocityEngine velocity = getVelocityEngine();
        Template t = velocity.getTemplate(template);
        StringWriter w = new StringWriter();
        t.merge(context, w);
        return w.toString();
    }

    VelocityEngine getVelocityEngine() {
        logger.error("Inicialización de VelocityEngine");
        VelocityEngine velocity = new VelocityEngine();
        velocity.setProperty(Velocity.RESOURCE_LOADER, "classpath");
        velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocity.setProperty("input.encoding", "UTF-8");
        velocity.init();
        return velocity;
    }


    public String obtenerBase64OcRFC(String numeroOrden) throws Exception {

        try {
            //==============================================================================================================
            //TRAMA
            String trama = this.Trama(numeroOrden);

            //==============================================================================================================
            logger.info("RFC: DESTINATION - " + destinationProfit);
            Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);

            logger.info(String.valueOf(destination2.get()));
            HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));

            logger.info(String.valueOf(client));
            String hostx = "connectivityproxy.internal.cf.us10.hana.ondemand.com";
            Integer portx = 20003;


            String urlbase = String.valueOf(destination2.get().asHttp().getUri());
            String url = urlbase + "/sap/bc/srt/rfc/sap/ZWS_PDF_OC?sap-client=400";
            logger.info("RFC: URL " + url);


            logger.info("RFC: Trama" + trama);

            final StringBuffer soap = new StringBuffer();
            soap.append("\n");
            soap.append("");
            // this is a sample data..you have create your own required data  BEGIN
            soap.append(" \n");
            soap.append(" \n");
            soap.append("" + trama);
            soap.append(" \n");
            soap.append(" \n");
            soap.append("");

            HttpEntity strEntity = new StringEntity(trama, "text/xml", "UTF-8");

            logger.info(soap.toString());

            HttpPost post = new HttpPost(url);
            post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions/ZWS_PDF_OC/ZMM_PDF_OCRequest");
            post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions/ZWS_PDF_OC/ZMM_PDF_OCRequest");
            post.setHeader("Content-Type", "text/xml;charset=UTF-8");
            post.setHeader("Accept-Encoding", "gzip,deflate");

            post.setEntity(strEntity);

            logger.info("RFC: Trama Entity" + strEntity);

            logger.info("HTTP POST" + post.toString());

            HttpResponse response4 = client.execute(post);
            HttpEntity respEntity = response4.getEntity();
            String result = EntityUtils.toString(respEntity);


            DocumentBuilderFactory domFactory = DocumentBuilderFactory
                    .newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder
                    .parse(new InputSource(new StringReader(result)));

            logger.info("RFC: Resultado :" + result);
       /* SapLog sapLog = new SapLog();
        String codigo = doc.getElementsByTagName("PO_CODE").item(0).getChildNodes().item(0).getNodeValue();
        String message = doc.getElementsByTagName("PO_MSJE").item(0).getChildNodes().item(0).getNodeValue();

        sapLog.setCode(codigo);
        sapLog.setMesaj(message);*/
            String respuesta = "";
            String homologacionProveedor = "";
            if (result.equals(null)) {
                respuesta = "";
            } else {
                NodeList nodes = doc.getElementsByTagName("PO_B64DATA1").item(0).getChildNodes();
                Node node = (Node) nodes.item(0);
                if (node != null) {
                    //posiciones bas64
                    String stringBase64 = "";
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Node posicion = nodes.item(i);
                        Element elemento = (Element) posicion;

                        stringBase64 = stringBase64 + Utils.getValueNodo(elemento, "ZB64DATA");
                        System.out.println(stringBase64);

                    }
                    respuesta = stringBase64;
                } else {
                    respuesta = "";
                }
            }
            return respuesta;
        } catch (Exception ex) {
            throw new PortalException("Error RFC");
        }
    }

    public String Trama(String ordenCompra) {

        String tramaXml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_PDF_OC>\n" +
                "         <I_EBELN>" + ordenCompra + "</I_EBELN>\n" +
                "      </urn:ZMM_PDF_OC>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        return tramaXml;

    }
}