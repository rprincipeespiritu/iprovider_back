package com.incloud.hcp.jco.emHes.service.impl;


import com.incloud.hcp.domain.DocumentoAceptacion;
import com.incloud.hcp.domain.DocumentoAceptacionDetalle;
import com.incloud.hcp.domain.LogTransaccion;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.jco.emHes.service.JCOEmHesService;
import com.incloud.hcp.repository.DocumentoAceptacionDetalleRepository;
import com.incloud.hcp.repository.DocumentoAceptacionRepository;
import com.incloud.hcp.repository.LogTransaccionRepository;
import com.incloud.hcp.repository.TempRubroBienRepository;
import com.incloud.hcp.service.wsdlSunat.flyWeight.FunctionsXML;
import com.sap.cloud.sdk.cloudplatform.connectivity.*;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import io.vavr.control.Try;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.sound.sampled.Port;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class JCOEmHesServiceImpl implements JCOEmHesService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${destination.rfc.profit}")
    private String destinationProfit;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private TempRubroBienRepository tempRubroBienRepository;


    @Autowired
    private DocumentoAceptacionRepository documentoAceptacionRepository;

    @Autowired
    private DocumentoAceptacionDetalleRepository documentoAceptacionDetalleRepository;



    @Override
    public DocumentoAceptacion crearEm(DocumentoAceptacion documentoAceptacion) throws Exception{

        //===============================================================================================
        // LLAMMAR RFC PARA CREAR LA EM
        //llamar servicio de destination
        logger.info("RFC: DESTINATION - " + destinationProfit);
        //==============================================================================================
        //armar la data
        List<DocumentoAceptacionDetalle> aceptacionDetalle =
                this.documentoAceptacionDetalleRepository
                        .getDocumentoAceptacionDetalleListByIdDocumentoAceptacion( documentoAceptacion.getId());
        String tramaEmXML = this.tramaEM(documentoAceptacion,aceptacionDetalle);

        Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);
        logger.info(String.valueOf(destination2.get()));
        HttpClient client = HttpClientAccessor.
                getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));
        logger.info(String.valueOf(client));
        String hostx = "connectivityproxy.internal.cf.us10.hana.ondemand.com";
        Integer portx = 20003;

        /*DefaultHttpClientFactory customFactory = new DefaultHttpClientFactory() {
            @Override
            protected RequestConfig.Builder getRequestConfigBuilder(HttpDestinationProperties destination) {
                return super.getRequestConfigBuilder(destination)
                        .setProxy(new HttpHost(hostx, portx, "http"));
            }
            @Override
            protected HttpClientBuilder getHttpClientBuilder(HttpDestinationProperties destination) {
                return super.getHttpClientBuilder(destination)
                        .setUserAgent("SDK");
            }
        };
        HttpClientAccessor.setHttpClientFactory(customFactory);*/
        String urlbase = String.valueOf(destination2.get().asHttp().getUri());
        String url = urlbase +
                "/sap/bc/srt/rfc/sap/zws_generar_em/100/zws_generar_em/zws_generar_em";
        logger.info("RFC: URL " + url);

//        //==============================================================================================
//        //armar la data
//        List<DocumentoAceptacionDetalle> aceptacionDetalle =
//                this.documentoAceptacionDetalleRepository
//                        .getDocumentoAceptacionDetalleListByIdDocumentoAceptacion( documentoAceptacion.getId());
//        String tramaEmXML = this.tramaEM(documentoAceptacion,aceptacionDetalle);
        logger.info("RFC: Trama" + tramaEmXML);
        final StringBuffer soap = new StringBuffer();
        soap.append("\n");
        soap.append("");
        // this is a sample data..you have create your own required data  BEGIN
        soap.append(" \n");
        soap.append(" \n");
        soap.append("" + tramaEmXML);
        soap.append(" \n");
        soap.append(" \n");
        /* soap.append(body); */
        // END of MEssage Body
        soap.append("");
        HttpEntity strEntity = new StringEntity(tramaEmXML, "text/xml", "UTF-8");
        HttpPost post = new HttpPost(url);
        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions:ZWS_GENERAR_EM:ZPE_MM_GENERAR_EMRequest");
        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions:ZWS_GENERAR_EM:ZPE_MM_GENERAR_EMRequest");
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Accept-Encoding", "gzip,deflate");
        post.setEntity(strEntity);
        logger.info("RFC: Trama Entity" + strEntity);
        logger.info("HTTP POS" + post.toString());
        HttpResponse response4 = client.execute(post);
        HttpEntity respEntity = response4.getEntity();
        String result = EntityUtils.toString(respEntity);

        logger.info("RESULT" + result);
        boolean resultado = result.contains("faultcode");

        if(resultado){
            //=========================================
            LogTransaccion logTransaccion = new LogTransaccion();
            logTransaccion.setEnvioTrama(tramaEmXML);
            logTransaccion.setRespuestaCodigo(result);
            logTransaccion.setTipoRegistro("TRAMA RFC EM");
            logTransaccion.setTipoTransaccion("RFC_EM");
            logTransaccionRepository.save(logTransaccion);
            throw  new PortalException("SAP EM: Web service processing error; more details in the web service error log on provider side : " + result);
        }

        DocumentBuilderFactory domFactory = DocumentBuilderFactory
                .newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder
                .parse(new InputSource(new StringReader(result)));
        NodeList codigoEM = doc.getElementsByTagName("PO_MBLNR").item(0).getChildNodes();
        if(codigoEM == null){
            NodeList nodesReturn = doc.getElementsByTagName("TO_RETURN").item(0).getChildNodes();
            if(nodesReturn !=null){
                String texto = FunctionsXML.getTagValueHTML(nodesReturn, "MESSAGE");
                if(texto !=null){
                    //=========================================
                    LogTransaccion logTransaccion = new LogTransaccion();
                    logTransaccion.setEnvioTrama(tramaEmXML);
                    logTransaccion.setRespuestaCodigo(result);
                    logTransaccion.setTipoRegistro("TRAMA RFC EM");
                    logTransaccion.setTipoTransaccion("RFC_EM");
                    logTransaccionRepository.save(logTransaccion);
                    throw  new PortalException("SAP:"+texto);
                }
            }
        }else{
            Node node = (Node) codigoEM.item(0);
            if (node != null) {
                //setear numero generado en SAP
                documentoAceptacion.setNumeroDocumentoAceptacion(node.getNodeValue().trim());
            }else{
                NodeList nodesReturn = doc.getElementsByTagName("TO_RETURN").item(0).getChildNodes();
                if(nodesReturn !=null){
                    String texto = FunctionsXML.getTagValueHTML(nodesReturn, "MESSAGE");
                    if(texto !=null){
                        //=========================================
                        LogTransaccion logTransaccion = new LogTransaccion();
                        logTransaccion.setEnvioTrama(tramaEmXML);
                        logTransaccion.setRespuestaCodigo(result);
                        logTransaccion.setTipoRegistro("TRAMA RFC EM");
                        logTransaccion.setTipoTransaccion("RFC_EM");
                        logTransaccionRepository.save(logTransaccion);
                        throw  new PortalException("SAP:"+texto);
                    }
                }
            }
        }

        //---------------------------------------------------------------------------------------------
//        NodeList nodesReturn = doc.getElementsByTagName("TO_RETURN").item(0).getChildNodes();
//        if(nodesReturn !=null){
//            String texto = FunctionsXML.getTagValueHTML(nodesReturn, "MESSAGE");
//            String type = FunctionsXML.getTagValueHTML(nodesReturn, "TYPE");
//            if(type =="S"){
//                NodeList nodes = doc.getElementsByTagName("PO_LBLNI").item(0).getChildNodes();
//                Node node = (Node) nodes.item(0);
//                if (node != null) {
//                    //setear numero generado en SAP
//                    documentoAceptacion.setNumeroDocumentoAceptacion(node.getNodeValue().trim());
//                    return documentoAceptacion;
//                }
//            }else{
//                throw  new PortalException(texto);
//            }
//        }
        LogTransaccion logTransaccion = new LogTransaccion();
        logTransaccion.setEnvioTrama(tramaEmXML);
        logTransaccion.setRespuestaCodigo(result);
        logTransaccion.setTipoRegistro("TRAMA RFC EM");
        logTransaccion.setTipoTransaccion("RFC_EM");
        logTransaccionRepository.save(logTransaccion);
        return documentoAceptacion;

    }

    @Autowired
    private LogTransaccionRepository logTransaccionRepository;

    @Override
    public DocumentoAceptacion crearHes(DocumentoAceptacion documentoAceptacion) throws Exception{

        //===============================================================================================
        // LLAMMAR RFC PARA CREAR LA HES
        //llamar servicio de destination
        logger.info("RFC: DESTINATION - " + destinationProfit);
        //==============================================================================================
        //armar la data
        List<DocumentoAceptacionDetalle> aceptacionDetalle =
                this.documentoAceptacionDetalleRepository
                        .getDocumentoAceptacionDetalleListByIdDocumentoAceptacion( documentoAceptacion.getId());
        String tramaHesXML = this.tramaHes(documentoAceptacion,aceptacionDetalle);

        Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);
        logger.info(String.valueOf(destination2.get()));
        HttpClient client = HttpClientAccessor.
                getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));
        logger.info(String.valueOf(client));
        String hostx = "connectivityproxy.internal.cf.us10.hana.ondemand.com";
        Integer portx = 20003;

        /*DefaultHttpClientFactory customFactory = new DefaultHttpClientFactory() {
            @Override
            protected RequestConfig.Builder getRequestConfigBuilder(HttpDestinationProperties destination) {
                return super.getRequestConfigBuilder(destination)
                        .setProxy(new HttpHost(hostx, portx, "http"));
            }
            @Override
            protected HttpClientBuilder getHttpClientBuilder(HttpDestinationProperties destination) {
                return super.getHttpClientBuilder(destination)
                        .setUserAgent("SDK");
            }
        };
        HttpClientAccessor.setHttpClientFactory(customFactory);*/
        String urlbase = String.valueOf(destination2.get().asHttp().getUri());
        String url = urlbase +
                "/sap/bc/srt/rfc/sap/zws_generar_hes/100/zws_generar_hes/zws_generar_hes";
        logger.info("RFC: URL " + url);

        //==============================================================================================
        //armar la data
//        List<DocumentoAceptacionDetalle> aceptacionDetalle =
//                this.documentoAceptacionDetalleRepository
//                        .getDocumentoAceptacionDetalleListByIdDocumentoAceptacion( documentoAceptacion.getId());
//        String tramaHesXML = this.tramaHes(documentoAceptacion,aceptacionDetalle);
        logger.info("RFC: Trama" + tramaHesXML);
        final StringBuffer soap = new StringBuffer();
        soap.append("\n");
        soap.append("");
        // this is a sample data..you have create your own required data  BEGIN
        soap.append(" \n");
        soap.append(" \n");
        soap.append("" + tramaHesXML);
        soap.append(" \n");
        soap.append(" \n");
        /* soap.append(body); */
        // END of MEssage Body
        soap.append("");
        HttpEntity strEntity = new StringEntity(tramaHesXML, "text/xml", "UTF-8");
        HttpPost post = new HttpPost(url);
        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions:ZWS_GENERAR_HES:ZPE_MM_GENERAR_HESRequest");
        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions:ZWS_GENERAR_HES:ZPE_MM_GENERAR_HESRequest");
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Accept-Encoding", "gzip,deflate");
        post.setEntity(strEntity);
        logger.info("RFC: Trama Entity" + strEntity);
        logger.info("HTTP POS" + post.toString());
        HttpResponse response4 = client.execute(post);
        HttpEntity respEntity = response4.getEntity();
        String result = EntityUtils.toString(respEntity);
        boolean resultado = result.contains("faultcode");

        if(resultado){
            //=========================================
            LogTransaccion logTransaccion = new LogTransaccion();
            logTransaccion.setEnvioTrama(tramaHesXML);
            logTransaccion.setRespuestaCodigo(result);
            logTransaccion.setTipoRegistro("TRAMA RFC HES");
            logTransaccion.setTipoTransaccion("RFC_HES");
            logTransaccionRepository.save(logTransaccion);
            throw  new PortalException("SAP HES: Web service processing error; more details in the web service error log on provider side : "+result);
        }
        DocumentBuilderFactory domFactory = DocumentBuilderFactory
                .newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder
                .parse(new InputSource(new StringReader(result)));
        NodeList codigoEM = doc.getElementsByTagName("PO_LBLNI").item(0).getChildNodes();
        if(codigoEM == null){
            NodeList nodesReturn = doc.getElementsByTagName("TO_RETURN").item(0).getChildNodes();
            if(nodesReturn !=null){
                String texto = FunctionsXML.getTagValueHTML(nodesReturn, "MESSAGE");
                if(texto !=null){
                    //=========================================
                    LogTransaccion logTransaccion = new LogTransaccion();
                    logTransaccion.setEnvioTrama(tramaHesXML);
                    logTransaccion.setRespuestaCodigo(result);
                    logTransaccion.setTipoRegistro("TRAMA RFC HES");
                    logTransaccion.setTipoTransaccion("RFC_HES");
                    logTransaccionRepository.save(logTransaccion);
                    throw  new PortalException("SAP:"+texto);
                }
            }
        }else{
            Node node = (Node) codigoEM.item(0);
            if (node != null) {
                //setear numero generado en SAP
                documentoAceptacion.setNumeroDocumentoAceptacion(node.getNodeValue().trim());
            }else{
                NodeList nodesReturn = doc.getElementsByTagName("TO_RETURN").item(0).getChildNodes();
                if(nodesReturn !=null){
                    String texto = FunctionsXML.getTagValueHTML(nodesReturn, "MESSAGE");
                    if(texto !=null){
                        //=========================================
                        LogTransaccion logTransaccion = new LogTransaccion();
                        logTransaccion.setEnvioTrama(tramaHesXML);
                        logTransaccion.setRespuestaCodigo(result);
                        logTransaccion.setTipoRegistro("TRAMA RFC HES");
                        logTransaccion.setTipoTransaccion("RFC_HES");
                        logTransaccionRepository.save(logTransaccion);
                        throw  new PortalException("SAP:"+texto);
                    }
                }
            }
        }
        //-----------------------------------------------------------


//        NodeList nodesReturn = doc.getElementsByTagName("TO_RETURN").item(0).getChildNodes();
//        if(nodesReturn !=null){
//            String texto = FunctionsXML.getTagValueHTML(nodesReturn, "MESSAGE");
//            String type = FunctionsXML.getTagValueHTML(nodesReturn, "TYPE");
//            if(type.trim() =="S"){
//                NodeList nodes = doc.getElementsByTagName("PO_LBLNI").item(0).getChildNodes();
//                Node node = (Node) nodes.item(0);
//                if (node != null) {
//                    //setear numero generado en SAP
//                    documentoAceptacion.setNumeroDocumentoAceptacion(node.getNodeValue().trim());
//                    return documentoAceptacion;
//                }
//            }else{
//                throw  new PortalException(texto);
//            }
//        }


        LogTransaccion logTransaccion = new LogTransaccion();
        logTransaccion.setEnvioTrama(tramaHesXML);
        logTransaccion.setRespuestaCodigo(result);
        logTransaccion.setTipoRegistro("TRAMA RFC HES");
        logTransaccion.setTipoTransaccion("RFC_HES");
        logTransaccionRepository.save(logTransaccion);

        return documentoAceptacion;

    }

    public String tramaEM(DocumentoAceptacion documentoAceptacion,
                           List<DocumentoAceptacionDetalle> documentoAceptacionDetalle){
        //FECHA FORMATEADA
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);



        //ITEMS PARA LA EM
        AtomicReference<String> tramaEmXmlItems = new AtomicReference<>("");
        documentoAceptacionDetalle.forEach(item->{
            if(item.getActaSustentoDetalle().getOrdenCompraDetalle().getTipoPosicion().equals("M")){
                tramaEmXmlItems.set(tramaEmXmlItems.get()+"<item>\n" +
//                    +item.getCodigoSapBienServicio()+
                        "  <MATERIAL>"+item.getActaSustentoDetalle().getOrdenCompraDetalle().getCodigoProducto()+"</MATERIAL>\n" + //codigo de material
                        "  <PLANT>"+item.getActaSustentoDetalle().getOrdenCompraDetalle().getCodigoSapCentro()+"</PLANT>\n" + //centro
                        "  <ENTRY_QNT>"+item.getValorRecibido().intValue()+"</ENTRY_QNT>\n" + //cantidad en unidad de medida entrada
                        "  <ENTRY_UOM>"+item.getUnidadMedida()+"</ENTRY_UOM>\n" + //unidad de medida entrada
                        "  <PO_NUMBER>"+item.getNumeroOrdenCompra()+"</PO_NUMBER>\n" + //numero de orden de compras
                        "  <PO_ITEM>"+item.getActaSustentoDetalle().getOrdenCompraDetalle().getPosicionOc()+"</PO_ITEM>\n" +
                        "  <NO_MORE_GR>X</NO_MORE_GR> "+
                        " <QUANTITY>"+item.getActaSustentoDetalle().getOrdenCompraDetalle().getPrecioTotal().setScale(2, BigDecimal.ROUND_HALF_EVEN)+"</QUANTITY> "+
                        "</item>\n");
            }

        });

        String tramaEmXml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZPE_MM_GENERAR_EM>\n" +
                "         <PI_BKTXT>"+documentoAceptacion.getTextoCabecera()+"</PI_BKTXT>\n" +
                "         <PI_BLDAT>"+simpleDateFormat.format(documentoAceptacion.getFechaDocumento())+"</PI_BLDAT>\n" +
                "         <PI_BUDAT>"+simpleDateFormat.format(documentoAceptacion.getFechaContabilizacion())+"</PI_BUDAT>\n" +
                "         <!--Optional:-->\n" +
                "         <PI_BUKRS></PI_BUKRS>\n" +
                "         <PI_EBELN>"+documentoAceptacion.getNumeroOrdenCompra()+"</PI_EBELN>\n" +
                "         <PI_NOTA>"+documentoAceptacion.getNotaEntrega()+"</PI_NOTA>\n" +
                "         <TI_ITEMS>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                             " "+tramaEmXmlItems.get()+""+
                "         </TI_ITEMS>\n" +
                "         <!--Optional:-->\n" +
                "         <TO_RETURN>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <TYPE></TYPE>\n" +
                "               <ID></ID>\n" +
                "               <NUMBER></NUMBER>\n" +
                "               <MESSAGE></MESSAGE>\n" +
                "               <LOG_NO></LOG_NO>\n" +
                "               <LOG_MSG_NO></LOG_MSG_NO>\n" +
                "               <MESSAGE_V1></MESSAGE_V1>\n" +
                "               <MESSAGE_V2></MESSAGE_V2>\n" +
                "               <MESSAGE_V3></MESSAGE_V3>\n" +
                "               <MESSAGE_V4></MESSAGE_V4>\n" +
                "               <PARAMETER></PARAMETER>\n" +
                "               <ROW></ROW>\n" +
                "               <FIELD></FIELD>\n" +
                "               <SYSTEM></SYSTEM>\n" +
                "            </item>\n" +
                "         </TO_RETURN>\n" +
                "      </urn:ZPE_MM_GENERAR_EM>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return  tramaEmXml;
    }

    public String tramaHes(DocumentoAceptacion documentoAceptacion,
                          List<DocumentoAceptacionDetalle> documentoAceptacionDetalle){

        //FECHA FORMATEADA
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);


        //ITEMS PARA LA HES
        AtomicReference<String> tramaHesXmlItems = new AtomicReference<>("");
        AtomicInteger index = new AtomicInteger(1) ;
        AtomicInteger index2 = new AtomicInteger(1) ;
        documentoAceptacionDetalle.forEach(item->{
            tramaHesXmlItems.set( tramaHesXmlItems.get() +
                    "<item>\n" +
                    "<PCKG_NO>000000000"+ (index.getAndIncrement() +1) +"</PCKG_NO>\n" +
                    "<LINE_NO>000000"+item.getActaSustentoDetalle().getOrdenCompraDetalle().getPosicionOc()+"</LINE_NO>\n" +
                    "<EXT_LINE>0000000010</EXT_LINE>\n" +
                    "<SERVICE>"+item.getCodigoSapBienServicio()+"</SERVICE>\n" +
                    "<QUANTITY>"+item.getValorRecibido().setScale(2, BigDecimal.ROUND_HALF_EVEN)+"</QUANTITY>\n" +
                    "<BASE_UOM>"+item.getUnidadMedida()+"</BASE_UOM>\n" +
                    "<PRICE_UNIT>1</PRICE_UNIT>\n" +
                    "<GR_PRICE>"+item.getPrecioUnitario()+"</GR_PRICE>\n" +
                    "<SHORT_TEXT>"+item.getActaSustentoDetalle().getOrdenCompraDetalle().getObservaciones()+"</SHORT_TEXT>\n" +
                    "<CON_LINE>000000000"+ (index2.getAndIncrement() +1) +"</CON_LINE>\n" +
                    "<MATL_GROUP>00000"+item.getActaSustentoDetalle().getOrdenCompraDetalle().getCodigoSapCentro()+"</MATL_GROUP>\n" +
                    "<NET_VALUE>"+item.getActaSustentoDetalle().getOrdenCompraDetalle().getPrecioTotal()+"</NET_VALUE>\n" +
                    "</item>\n" );
        });

        String tramaHesXMl = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "    <urn:ZPE_MM_GENERAR_HES>\n" +
                "         <PI_BKTXT>"+documentoAceptacion.getTextoCabecera()+"</PI_BKTXT>\n" +//texto cabecera
                "         <PI_BLDAT>"+simpleDateFormat.format(documentoAceptacion.getFechaDocumento())+"</PI_BLDAT>\n" +//fecha del documento (BLDAT)
                "         <PI_BUDAT>"+simpleDateFormat.format(documentoAceptacion.getFechaContabilizacion())+"</PI_BUDAT>\n" +//fecha de contabilización (BUDAT)
                "         <PI_BUKRS>"+documentoAceptacion.getActaSustento().getOrdenCompra().getSociedad()+"</PI_BUKRS>\n" +// sociedad
                "         <PI_EBELN>"+documentoAceptacion.getNumeroOrdenCompra()+"</PI_EBELN>\n" + // numero de orden de compras
                "         <PI_XBLNR>"+documentoAceptacion.getReferencia()+"</PI_XBLNR>\n" +//texto referencia
                "         <TI_SERVICES>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <PCKG_NO>0000000001</PCKG_NO>\n" +
                "               <LINE_NO>000000"+documentoAceptacionDetalle.get(0).getActaSustentoDetalle().getOrdenCompraDetalle().getPosicionOc()+"</LINE_NO>\n" +
                "               <SUBPCKG_NO>0000000002</SUBPCKG_NO>              \n" +
                "            </item>\n" +
//                "              <item>\n" +
                                "" +tramaHesXmlItems + ""+
//                "            </item>\n" +
                "\n" +
                "         </TI_SERVICES>\n" +
                "         <!--Optional:-->\n" +
                "         <TO_RETURN>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <TYPE></TYPE>\n" +
                "               <ID></ID>\n" +
                "               <NUMBER></NUMBER>\n" +
                "               <MESSAGE></MESSAGE>\n" +
                "               <LOG_NO></LOG_NO>\n" +
                "               <LOG_MSG_NO></LOG_MSG_NO>\n" +
                "               <MESSAGE_V1></MESSAGE_V1>\n" +
                "               <MESSAGE_V2></MESSAGE_V2>\n" +
                "               <MESSAGE_V3></MESSAGE_V3>\n" +
                "               <MESSAGE_V4></MESSAGE_V4>\n" +
                "               <PARAMETER></PARAMETER>\n" +
                "               <ROW></ROW>\n" +
                "               <FIELD></FIELD>\n" +
                "               <SYSTEM></SYSTEM>\n" +
                "            </item>\n" +
                "         </TO_RETURN>\n" +
                "      </urn:ZPE_MM_GENERAR_HES>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return  tramaHesXMl;
    }


}
