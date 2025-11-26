package com.incloud.hcp.jco.ordenCompra.service.impl;

import com.incloud.hcp.domain.*;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraRFCParameterBuilder;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraResponseDto;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraServicioRFCParameterBuilder;
import com.incloud.hcp.jco.ordenCompra.service.JCOOrdenCompraService;
import com.incloud.hcp.repository.*;
import com.incloud.hcp.sap.SapLog;
import com.incloud.hcp.service.wsdlSunat.flyWeight.FunctionsXML;
import com.incloud.hcp.util.DateUtils;
//import com.sap.conn.jco.*;
import com.incloud.hcp.util.Utils;
import com.sap.cloud.sdk.cloudplatform.connectivity.*;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import io.vavr.control.Try;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class JCOOrdenCompraServiceImpl implements JCOOrdenCompraService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int NRO_EJECUCIONES_RFC = 10;
    private final int NIVEL = 1;
    //    private final String FUNCION_RFC = "ZMM_GENERAR_OC";
    private final String FUNCION_RFC = "ZPE_MM_GENERAR_OC";
    private final String NOMBRE_TABLA_RPTA_RFC = "TO_RETURN";
    private final String TIPO_ITEM_MATERIAL = "MATERIAL";

    @Value("${destination.rfc.profit}")
    private String destinationProfit;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CotizacionDetalleRepository cotizacionDetalleRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private LicitacionRepository licitacionRepository;

    @Autowired
    private LogTransaccionRepository logTransaccionRepository;

    @Autowired
    private CondicionPagoReposity condicionPagoReposity;

    @Autowired
    private BienServicioRepository bienServicioRepository;

    @Autowired
    private RubroBienRepository rubroBienRepository;

    public OrdenCompraResponseDto grabarOrdenCompraSAP(
        Proveedor proveedor,
        OrdenCompra ordenCompra, List<OrdenCompraDetalle> ordenCompraDetalles,
        Integer idLicitacion) throws Exception {

        OrdenCompraResponseDto ordenCompraResponseDto = new OrdenCompraResponseDto();
        Licitacion licitacion = licitacionRepository.findById(idLicitacion).get();
        List<CotizacionDetalle> cotizacionDetalles = cotizacionDetalleRepository.findByCotizacion(licitacion, proveedor);
        //==============================================================================================================
        //TRAMA
        String tramaXML = this.TramaXml("", proveedor, "", ordenCompra, ordenCompraDetalles, cotizacionDetalles, licitacion.getNombreLicitacion());

        logger.info(tramaXML);
        //LOCAL RFC
//        HttpParams httpParameters = new BasicHttpParams();
//        // Set the timeout in milliseconds until a connection is established.
//        int timeoutConnection = 15000;
//        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//        // Set the default socket timeout (SO_TIMEOUT)
//        // in milliseconds which is the timeout for waiting for data.
//        int timeoutSocket = 35000;
//        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
//
//        DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
//        String encoding = Base64.getEncoder().encodeToString(("CMENDEZ:Csti2022").getBytes(StandardCharsets.UTF_8));
//
//        /*
//         * httpclient.getCredentialsProvider().setCredentials( new
//         * AuthScope("os.icloud.com", 80, null, "Digest"), new
//         * UsernamePasswordCredentials(username, password));
//         */
//
//        //now create a soap request message as follows:
//        final StringBuffer soap = new StringBuffer();
//        soap.append("\n");
//        soap.append("");
//        // this is a sample data..you have create your own required data  BEGIN
//        soap.append(" \n");
//        soap.append(" \n");
//        soap.append("" + tramaXML);
//        soap.append(" \n");
//        soap.append(" \n");
//
//        /* soap.append(body); */
//        // END of MEssage Body
//        soap.append("");
//
//        HttpEntity strEntity = new StringEntity(tramaXML, "text/xml", "UTF-8");
//        HttpPost httppost = new HttpPost("http://191.1.64.127:8002/sap/bc/srt/rfc/sap/ZWS_GENERAR_OC?sap-client=400&wsdl=1.1&mode=sap_wsdl");
//        httppost.setHeader("soapaction", "urn:sap-com:document:sap:rfc:functions/ZWS_GENERAR_OC/ZMM_GENERAR_OCRequest");
//        httppost.setHeader("Content-Type", "text/xml; charset=utf-8");
//        httppost.setHeader("Authorization", "Basic " + encoding);
//        System.out.println("executing request" + httppost.getRequestLine());
//        httppost.setEntity(strEntity);
//        logger.info(soap.toString());
//        HttpResponse response4 = httpclient.execute(httppost);
//        HttpEntity respEntity = response4.getEntity();
//        String result = EntityUtils.toString(respEntity);
        //==============================================================================================================
        logger.info("RFC: DESTINATION - " + destinationProfit);
        Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);

        logger.info(String.valueOf(destination2.get()));
        HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));

        logger.info(String.valueOf(client));
        String hostx = "connectivityproxy.internal.cf.us10.hana.ondemand.com";
        Integer portx = 20003;

       /* DefaultHttpClientFactory customFactory = new DefaultHttpClientFactory() {
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
        // http://191.1.64.127:8002/sap/bc/srt/rfc/sap/ZWS_GENERAR_OC?sap-client=400&wsdl=1.1&mode=sap_wsdl
        String url = urlbase + "/sap/bc/srt/rfc/sap/ZWS_GENERAR_OC?sap-client=400&wsdl=1.1&mode=sap_wsdl";

        logger.info("RFC: URL " + url);


        logger.info("RFC: Trama" + tramaXML);

        final StringBuffer soap = new StringBuffer();
        soap.append("\n");
        soap.append("");
        // this is a sample data..you have create your own required data  BEGIN
        soap.append(" \n");
        soap.append(" \n");
        soap.append("" + tramaXML);
        soap.append(" \n");
        soap.append(" \n");

        /* soap.append(body); */
        // END of MEssage Body
        soap.append("");

        HttpEntity strEntity = new StringEntity(tramaXML, "text/xml", "UTF-8");

        logger.info(soap.toString());
        //HttpEntity strEntity = new StringEntity(soap.toString());
        //HttpEntity strEntity = new StringEntity(soap.toString(), ContentType.TEXT_XML);

        HttpPost post = new HttpPost(url);
        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions/ZWS_GENERAR_OC/ZMM_GENERAR_OCRequest");
        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions/ZWS_GENERAR_OC/ZMM_GENERAR_OCRequest");
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Accept-Encoding", "gzip,deflate");

        post.setEntity(strEntity);

        logger.info("RFC: Trama Entity" + strEntity);

        logger.info("HTTP POS" + post.toString());

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
            NodeList nodes = doc.getElementsByTagName("I_EBELN").item(0).getChildNodes();
            Node node = (Node) nodes.item(0);
            if (node != null) {
                respuesta = String.valueOf(node.getNodeValue().trim());

                ordenCompraResponseDto.setNumeroOrdenCompra(respuesta);
            } else {
                respuesta = "";
            }
            if (respuesta.equals("")) {
                NodeList nodesReturn = doc.getElementsByTagName("T_RETURN").item(0).getChildNodes();
                if (nodesReturn != null) {
                    String texto = FunctionsXML.getTagValueHTML(nodesReturn, "MESSAGE");
                    if (texto != null) {

                        ordenCompraResponseDto.setMessageSap(texto);
                        //throw  new PortalException(texto);
                        //=========================================
                        LogTransaccion logTransaccion = new LogTransaccion();
                        logTransaccion.setEnvioTrama(tramaXML);
                        logTransaccion.setRespuestaCodigo(result);
                        logTransaccion.setTipoRegistro("TRAMA RFC OC");
                        logTransaccion.setTipoTransaccion("RFC_OC");
                        logTransaccionRepository.save(logTransaccion);
                        //=========================================


                    }
                }
            }


        }
        LogTransaccion logTransaccion = new LogTransaccion();
        logTransaccion.setEnvioTrama(tramaXML);
        logTransaccion.setRespuestaCodigo(result);
        logTransaccion.setTipoRegistro("TRAMA RFC OC");
        logTransaccion.setTipoTransaccion("RFC_OC");
        logTransaccionRepository.save(logTransaccion);
        logger.info("RPTA" + respuesta);
        logger.info("RPTA 2" + ordenCompraResponseDto);
        return ordenCompraResponseDto;
    }


    public OrdenCompraResponseDto grabarOrdenCompra(
        String claseDocumento,
        Proveedor proveedor,
        Usuario usuario,
        List<CcomparativoAdjudicado> ccomparativoAdjudicadoList) throws Exception {
        OrdenCompraResponseDto ordenCompraResponseDto = new OrdenCompraResponseDto();

//        /* Ejecucion invocacion a RFC */
//        JCoDestination destination = JCoDestinationManager.getDestination(destinationProfit);
//        JCoRepository repo = destination.getRepository();
//        logger.error("01A - grabarOrdenCompra");
//        JCoFunction jCoFunction = repo.getFunction(FUNCION_RFC);
//        logger.error("01B - grabarOrdenCompra");
//
//        /* Pasando los datos de parametria */
//        Date fechaActual = DateUtils.obtenerFechaActual();
//        String sfechaActual = DateUtils.convertDateToString("yyyyMMdd", fechaActual);
//        CotizacionDetalle cotizacionDetalle = ccomparativoAdjudicadoList.get(0).getCotizacionDetalle();
//        cotizacionDetalle = this.cotizacionDetalleRepository.getOne(cotizacionDetalle.getIdCotizacionDetalle());
//        String tipoItem = cotizacionDetalle.getBienServicio().getTipoItem();
//        logger.error("01B - grabarOrdenCompra tipoItem: " + tipoItem);
//        Licitacion licitacion = cotizacionDetalle.getCotizacion().getLicitacion();
//
//        if (tipoItem.equals(TIPO_ITEM_MATERIAL)) {
//            OrdenCompraRFCParameterBuilder.build(
//                    jCoFunction,
//                    claseDocumento,
//                    sfechaActual,
//                    proveedor,
//                    usuario,
//                    licitacion,
//                    ccomparativoAdjudicadoList
//            );
//        }
//        else  {
//            OrdenCompraServicioRFCParameterBuilder.build(
//                    jCoFunction,
//                    claseDocumento,
//                    sfechaActual,
//                    proveedor,
//                    usuario,
//                    licitacion,
//                    ccomparativoAdjudicadoList
//            );
//        }
//
//        logger.error("01C - GET grabarOrdenCompra");
//        for(int contador=0; contador < NRO_EJECUCIONES_RFC; contador++) {
//            try {
//                jCoFunction.execute(destination);
//                break;
//            } catch (Exception e) {
//                if (contador == NRO_EJECUCIONES_RFC - 1 ) {
//                    logger.error("01Ca - grabarOrdenCompra - INI RFC ERROR: "+ e.toString());
//                    throw new Exception(e);
//                }
//            }
//        }
//
//        /* Obteniendo los valores obtenidos del RFC */
//        logger.error("02 - GET grabarOrdenCompra - FIN RFC");
//        JCoParameterList result = jCoFunction.getExportParameterList();
//        List<SapLog> listSapLog = new ArrayList<>();
//        String nroOrdenCompra = result.getString("PO_EBELN");
//        logger.error("03 - GET grabarOrdenCompra - FIN RFC nroOrdenCompra: " + nroOrdenCompra);
//
//        ordenCompraResponseDto.setCodigoAcreedorSap(proveedor.getAcreedorCodigoSap());
//        ordenCompraResponseDto.setProveedorSAP(proveedor);
//        ordenCompraResponseDto.setNumeroOrdenCompra(nroOrdenCompra);
//        logger.error("03 - GET grabarOrdenCompra - FIN ordenCompraResponseDto: " + ordenCompraResponseDto.toString());
//
//        JCoTable table = jCoFunction.getTableParameterList().getTable(NOMBRE_TABLA_RPTA_RFC);
//        if (table != null && !table.isEmpty()) {
//            do {
//                SapLog sapLog = new SapLog();
//                sapLog.setTipo(table.getString("TYPE"));
//                sapLog.setCode(table.getString("NUMBER"));
//                sapLog.setMesaj(table.getString("MESSAGE"));
//                sapLog.setParameter(table.getString("PARAMETER"));
//                sapLog.setRow(table.getString("ROW"));
//                sapLog.setField(table.getString("FIELD"));
//                sapLog.setSystem(table.getString("SYSTEM"));
//                logger.error("grabarOrdenCompra sapLog" + sapLog.toString());
//                listSapLog.add(sapLog);
//            } while (table.nextRow());
//        }
//        ordenCompraResponseDto.setSapLogList(listSapLog);
//        ordenCompraResponseDto.setExito(true);
////        if (listSapLog != null && listSapLog.size() > 0) {
////            for (SapLog beanLog : listSapLog) {
////                if (!beanLog.getTipo().equals("W")) {
////                    ordenCompraResponseDto.setExito(false);
////                    ordenCompraResponseDto.setNumeroOrdenCompra("");
////                }
////            }
////        }
//        if (StringUtils.isBlank(nroOrdenCompra)) {
//            ordenCompraResponseDto.setExito(false);
//            ordenCompraResponseDto.setNumeroOrdenCompra("");
//            SapLog sapLog = new SapLog();
//            sapLog.setTipo("E");
//            sapLog.setMesaj("SAP no devolvio Nro de Orden de Compra respectivo");
//            listSapLog.add(sapLog);
//            ordenCompraResponseDto.setSapLogList(listSapLog);
//        }
//        logger.error("04 - GET grabarOrdenCompra - FIN ordenCompraResponseDto: " + ordenCompraResponseDto.toString());
        return ordenCompraResponseDto;
    }

    public String TramaXml(String claseDocumento,
                           Proveedor proveedor,
                           String usuario,
                           OrdenCompra ordenCompra,
                           List<OrdenCompraDetalle> ordenCompraDetalleList,
                           List<CotizacionDetalle> cotizacionDetalles,
                           String nroLicitacion) {
        //FECHA FORMATEADA
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String COMP_CODE = ordenCompraDetalleList.get(0).getSociedad();
        String DOC_TYPE = ordenCompraDetalleList.get(0).getClaseDoc();
        String STATUS = "";
        String CREATED_BY = "CMENDEZ";
        String ITEM_INTVL = "00010";
        String VENDOR = proveedor.getAcreedorCodigoSap();
        String LANGU = "S";
        CondicionPago condicionPago = condicionPagoReposity.getById(Integer.parseInt(ordenCompraDetalleList.get(0).getCondicionPago()));
        String conpago = "";
        if (condicionPago != null) {
            conpago = condicionPago.getCodigoSap();
        }
        String PMNTTRMS = conpago;
        String PURCH_ORG = "1100";
        String PUR_GROUP = "";//proveedor.getCodigoGrupoCompra();
        String CURRENCY = ordenCompra.getCodigoMondeda();
        String COLLECT_NO = "DESCENTRAL";
        String fechaInicio = simpleDateFormat.format(ordenCompraDetalleList.get(0).getFechaInicioContrato());
        String fechaFin = simpleDateFormat.format(ordenCompraDetalleList.get(0).getFechaFinContrato());
        String TIPO_EXCEP = "";//ordenCompraDetalleList.get(0).getTipoExcepcion();

        //Obtenemos el tipo de OC del primer Item
        if (ordenCompraDetalleList.get(0).getTipoPosicion().equals("M")) {
            STATUS = "";
        }

        String trama = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
            "   <soapenv:Header/>\n" +
            "   <soapenv:Body>\n" +
            " <urn:ZMM_GENERAR_OC>\n" +
            "         <I_HEADER_TEXT>" + nroLicitacion + "</I_HEADER_TEXT>\n" +
            "         <!--Optional:-->\n" +
            "<I_POHEADER>\n" +
            "<PO_NUMBER></PO_NUMBER>\n" +
            "            <COMP_CODE></COMP_CODE>\n" +
            "            <DOC_TYPE>" + DOC_TYPE + "</DOC_TYPE>\n" +
            "            <DELETE_IND></DELETE_IND>\n" +
            "            <STATUS></STATUS>\n" +
            "            <CREAT_DATE></CREAT_DATE>\n" +
            "            <CREATED_BY>CMENDEZ</CREATED_BY>\n" +
            "            <ITEM_INTVL>" + ITEM_INTVL + "</ITEM_INTVL>\n" +
            "            <VENDOR>" + VENDOR + "</VENDOR>\n" +
            "            <LANGU>S</LANGU>\n" +
            "            <LANGU_ISO></LANGU_ISO>\n" +
            "            <PMNTTRMS>" + PMNTTRMS + "</PMNTTRMS>\n" +
            "            <DSCNT1_TO></DSCNT1_TO>\n" +
            "            <DSCNT2_TO></DSCNT2_TO>\n" +
            "            <DSCNT3_TO></DSCNT3_TO>\n" +
            "            <DSCT_PCT1></DSCT_PCT1>\n" +
            "            <DSCT_PCT2></DSCT_PCT2>\n" +
            "            <PURCH_ORG>" + PURCH_ORG + "</PURCH_ORG>\n" +
            "            <PUR_GROUP></PUR_GROUP>\n" +
            "            <CURRENCY>" + CURRENCY + "</CURRENCY>\n" +
            "            <CURRENCY_ISO></CURRENCY_ISO>\n" +
            "            <EXCH_RATE></EXCH_RATE>\n" +
            "            <EX_RATE_FX></EX_RATE_FX>\n" +
            "            <DOC_DATE></DOC_DATE>\n" +
            "            <VPER_START>" + fechaInicio + "</VPER_START>\n" +
            "            <VPER_END>" + fechaFin + "</VPER_END>\n" +
            "            <WARRANTY></WARRANTY>\n" +
            "            <QUOTATION></QUOTATION>\n" +
            "            <QUOT_DATE></QUOT_DATE>\n" +
            "            <REF_1></REF_1>\n" +
            "            <SALES_PERS></SALES_PERS>\n" +
            "            <TELEPHONE></TELEPHONE>\n" +
            "            <SUPPL_VEND></SUPPL_VEND>\n" +
            "            <CUSTOMER></CUSTOMER>\n" +
            "            <AGREEMENT></AGREEMENT>\n" +
            "            <GR_MESSAGE></GR_MESSAGE>\n" +
            "            <SUPPL_PLNT></SUPPL_PLNT>\n";
        if (DOC_TYPE.equals("ZIMM") || DOC_TYPE.equals("ZIMP")) {
            trama = trama + "<INCOTERMS1>FOB</INCOTERMS1>\n" +
                "<INCOTERMS2>" + proveedor.getPais().getDescripcion() + "</INCOTERMS2>\n";
        } else {
            trama = trama + "<INCOTERMS2></INCOTERMS2>\n" +
                "<PRE_VENDOR></PRE_VENDOR>\n";
        }
        trama = trama +
            "            <COLLECT_NO></COLLECT_NO>\n" +
            "            <DIFF_INV></DIFF_INV>\n" +
            "            <OUR_REF></OUR_REF>\n" +
            "            <LOGSYSTEM></LOGSYSTEM>\n" +
            "            <SUBITEMINT></SUBITEMINT>\n" +
            "            <PO_REL_IND></PO_REL_IND>\n" +
            "            <REL_STATUS></REL_STATUS>\n" +
            "            <VAT_CNTRY></VAT_CNTRY>\n" +
            "            <VAT_CNTRY_ISO></VAT_CNTRY_ISO>\n" +
            "            <REASON_CANCEL></REASON_CANCEL>\n" +
            "            <REASON_CODE></REASON_CODE>\n" +
            "         </I_POHEADER>\n" +
            "         <!--Optional:-->\n" +
            "<I_POHEADERX></I_POHEADERX>\n" +
            "<I_TIPO_EXCEP>" + TIPO_EXCEP + "</I_TIPO_EXCEP>\n" +
            "<T_POACCOUNT>";

        int i = 0;
        for (OrdenCompraDetalle ocS : ordenCompraDetalleList) {
            //posicion de servicios
//            if(ocS.getTipoPosicion().equals("S")){
            String GL_ACCOUNT = "0659610073";
            String CO_AREA = "GC00";
            String PROFIT_CTR = "10U125500D";
            String WBS_ELEMENT = "10U1.255H.C.52.G-6";
            String SERIAL_NO = "";
            String CMMT_ITEM = ""; //ZINVG

            if (ocS.getTipoPosicion().equals("M")) {
                SERIAL_NO = "01";
            }
            boolean NO_VA = false;
            if (NO_VA) {

                Integer contador = new Integer(10 * (i + 1));
                String sScontador = StringUtils.leftPad(contador.toString().trim(), 5, '0');

                String PO_ITEM = String.valueOf(sScontador);

                trama = trama + "         <!--Zero or more repetitions:-->\n" +
                    "            <item>\n" +
                    "               <PO_ITEM>" + PO_ITEM + "</PO_ITEM>\n" +
                    "               <SERIAL_NO>" + SERIAL_NO + "</SERIAL_NO>\n" + //01
                    "               <DELETE_IND></DELETE_IND>\n" +
                    "               <CREAT_DATE></CREAT_DATE>\n" +
                    "               <QUANTITY>" + ocS.getCantidad() + "</QUANTITY>\n" + //+100.0+
                    "               <DISTR_PERC></DISTR_PERC>\n" +
                    "               <NET_VALUE>" + ocS.getPrecioUnitario() + "</NET_VALUE>\n" + //ocS.getPrecioUnitario() "+1000.0+"
                    "               <GL_ACCOUNT></GL_ACCOUNT>\n" + //"+GL_ACCOUNT+"
                    "               <BUS_AREA></BUS_AREA>\n" +
                    "               <COSTCENTER></COSTCENTER>\n" +
                    "               <SD_DOC></SD_DOC>\n" +
                    "               <ITM_NUMBER></ITM_NUMBER>\n" +
                    "               <SCHED_LINE></SCHED_LINE>\n" +
                    "               <ASSET_NO></ASSET_NO>\n" +
                    "               <SUB_NUMBER></SUB_NUMBER>\n" +
                    "               <ORDERID></ORDERID>\n" +
                    "               <GR_RCPT></GR_RCPT>\n" +
                    "               <UNLOAD_PT></UNLOAD_PT>\n" +
                    "               <CO_AREA></CO_AREA>\n" + //"+CO_AREA+"
                    "               <COSTOBJECT></COSTOBJECT>\n" +
                    "               <PROFIT_CTR></PROFIT_CTR>\n" +//"+PROFIT_CTR+"
                    "               <WBS_ELEMENT></WBS_ELEMENT>\n" +//"+WBS_ELEMENT+"
                    "               <NETWORK></NETWORK>\n" +
                    "               <RL_EST_KEY></RL_EST_KEY>\n" +
                    "               <PART_ACCT></PART_ACCT>\n" +
                    "               <CMMT_ITEM>" + CMMT_ITEM + "</CMMT_ITEM>\n" +
                    "               <REC_IND></REC_IND>\n" +
                    "               <FUNDS_CTR></FUNDS_CTR>\n" +
                    "               <FUND></FUND>\n" +
                    "               <FUNC_AREA></FUNC_AREA>\n" +
                    "               <REF_DATE></REF_DATE>\n" +
                    "               <TAX_CODE></TAX_CODE>\n" +
                    "               <TAXJURCODE></TAXJURCODE>\n" +
                    "               <NOND_ITAX></NOND_ITAX>\n" +
                    "               <ACTTYPE></ACTTYPE>\n" +
                    "               <CO_BUSPROC></CO_BUSPROC>\n" +
                    "               <RES_DOC></RES_DOC>\n" +
                    "               <RES_ITEM></RES_ITEM>\n" +
                    "               <ACTIVITY></ACTIVITY>\n" +
                    "               <GRANT_NBR></GRANT_NBR>\n" +
                    "               <CMMT_ITEM_LONG></CMMT_ITEM_LONG>\n" +
                    "               <FUNC_AREA_LONG></FUNC_AREA_LONG>\n" +
                    "               <BUDGET_PERIOD></BUDGET_PERIOD>\n" +
                    "               <FINAL_IND></FINAL_IND>\n" +
                    "               <FINAL_REASON></FINAL_REASON>\n" +
                    "            </item>\n";
                i++;
            }

        }


        trama = trama + "" +
            "</T_POACCOUNT>\n" +
            "<T_POACCOUNTX></T_POACCOUNTX>\n" +
            "<T_POITEM>\n";
        int ii = 0;
        for (OrdenCompraDetalle oc : ordenCompraDetalleList) {
            //posicion de materiales bienes
            //if(oc.getTipoPosicion().equals("M")){
            Integer contador = new Integer(10 * (ii + 1));
            String scontador = StringUtils.leftPad(contador.toString().trim(), 5, '0');

            String PO_ITEM = scontador;
            String SHORT_TEXT = oc.getDescripcionBienServicio();
            String EMATERIAL = oc.getCodigoProducto();
            String MATERIAL_EXTERNAL = oc.getCodigoProducto();
            String PLANT = oc.getCodigoSapCentro();


            BienServicio bienServicio = bienServicioRepository.getByCodigoSap(oc.getCodigoSapBienServicio());
            String rubroBienCodigoSap = "";
            String MATL_GROUP = "";

            if (oc.getCodigoSapBienServicio().isEmpty()) {
                Optional<RubroBien> rubroBien = rubroBienRepository.findById(oc.getIdRubroBien());
                if (rubroBien.isPresent()) {
                    rubroBienCodigoSap = rubroBien.get().getCodigoSap();
                }
                SHORT_TEXT = "SOLPED";
                 MATL_GROUP = rubroBienCodigoSap;
            }else{
                 MATL_GROUP = bienServicio !=null ? bienServicio.getRubroBien().getCodigoSap() :cotizacionDetalles.get(ii).getBienServicio().getRubroBien().getCodigoSap();
            }

            //String MATL_GROUP =bienServicio !=null ? bienServicio.getRubroBien().getCodigoSap() :cotizacionDetalles.get(ii).getBienServicio().getRubroBien().getCodigoSap();


            String QUANTITY = String.valueOf(oc.getCantidad());
            String PO_UNIT = oc.getUnidadMedidaBienServicio(); /*Aqui puede dar error*/
            String CONV_NUM1 = "1";
            String CONV_DEN1 = "1";
            String NET_PRICE = String.valueOf(oc.getPrecioUnitario());
            String TAX_CODE = oc.getIndicadorImpuesto();
            String PRNT_PRICE = "X";
            String UNLIMITED_DLV = "X";

            String ITEM_CAT = "0";
            String ACCTASSCAT = "";
            String GR_IND = "X";
            String IR_IND = "X";
            String GR_BASEDIV = "X";

            String PREQ_NO = String.valueOf(oc.getOpSolicitudCompra()); //!!
            if (PREQ_NO.length() < 10) {
                PREQ_NO = "00" + PREQ_NO;
            }
            String PREQ_ITEM = oc.getPosicion(); //!!

            String PREQ_NAME = "CMENDEZ";
            String PERIOD_IND_EXPIRATION_DATE = "";
            String REF_ITEM = "";
            if (oc.getTipoPosicion().equals("M")) {
                EMATERIAL = "";
                ITEM_CAT = "0";
                ACCTASSCAT = "";
                REF_ITEM = "00010";
            } else {
                MATERIAL_EXTERNAL = "";
            }

            trama = trama + "<!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <PO_ITEM>" + PO_ITEM + "</PO_ITEM>\n" +
                "               <DELETE_IND></DELETE_IND>\n" +
                "               <SHORT_TEXT>" + SHORT_TEXT.trim().replaceAll("\n", "") + "</SHORT_TEXT>\n" +
                "               <MATERIAL>" + MATERIAL_EXTERNAL + "</MATERIAL>\n" +
                "               <MATERIAL_EXTERNAL>" + MATERIAL_EXTERNAL + "</MATERIAL_EXTERNAL>\n" +
                "               <MATERIAL_GUID></MATERIAL_GUID>\n" +
                "               <MATERIAL_VERSION></MATERIAL_VERSION>\n" +
                "               <EMATERIAL></EMATERIAL>\n" +
                "               <EMATERIAL_EXTERNAL></EMATERIAL_EXTERNAL>\n" +
                "               <EMATERIAL_GUID></EMATERIAL_GUID>\n" +
                "               <EMATERIAL_VERSION></EMATERIAL_VERSION>\n" +
                "               <PLANT>" + PLANT + "</PLANT>\n" +
                "               <STGE_LOC></STGE_LOC>\n" +
                "               <TRACKINGNO></TRACKINGNO>\n" +
                "               <MATL_GROUP>" + MATL_GROUP + "</MATL_GROUP>\n" +
                "               <INFO_REC></INFO_REC>\n" +
                "               <VEND_MAT></VEND_MAT>\n" +
                "               <QUANTITY>" + QUANTITY + "</QUANTITY>\n" +
                "               <PO_UNIT>" + PO_UNIT + "</PO_UNIT>\n" +
                "               <PO_UNIT_ISO></PO_UNIT_ISO>\n" +
                "               <ORDERPR_UN></ORDERPR_UN>\n" +
                "               <ORDERPR_UN_ISO></ORDERPR_UN_ISO>\n" +
                "               <CONV_NUM1>" + CONV_NUM1 + "</CONV_NUM1>\n" +
                "               <CONV_DEN1>" + CONV_DEN1 + "</CONV_DEN1>\n" +
                "               <NET_PRICE>" + NET_PRICE + "</NET_PRICE>\n" +
                "               <PRICE_UNIT>" + "1" + "</PRICE_UNIT>\n" +
                "               <GR_PR_TIME></GR_PR_TIME>\n" +
                "               <TAX_CODE>" + TAX_CODE + "</TAX_CODE>\n" +
                "               <BON_GRP1></BON_GRP1>\n" +
                "               <QUAL_INSP></QUAL_INSP>\n" +
                "               <INFO_UPD></INFO_UPD>\n" +
                "               <PRNT_PRICE>" + PRNT_PRICE + "</PRNT_PRICE>\n" +
                "               <EST_PRICE></EST_PRICE>\n" +
                "               <REMINDER1></REMINDER1>\n" +
                "               <REMINDER2></REMINDER2>\n" +
                "               <REMINDER3></REMINDER3>\n" +
                "               <OVER_DLV_TOL></OVER_DLV_TOL>\n" +
                "               <UNLIMITED_DLV>" + UNLIMITED_DLV + "</UNLIMITED_DLV>\n" +
                "               <UNDER_DLV_TOL></UNDER_DLV_TOL>\n" +
                "               <VAL_TYPE></VAL_TYPE>\n" +
                "               <NO_MORE_GR></NO_MORE_GR>\n" +
                "               <FINAL_INV></FINAL_INV>\n" +
                "               <ITEM_CAT></ITEM_CAT>\n" +
                "               <ACCTASSCAT></ACCTASSCAT>\n" +
                "               <DISTRIB></DISTRIB>\n" +
                "               <PART_INV></PART_INV>\n" +
                "               <GR_IND>" + GR_IND + "</GR_IND>\n" +
                "               <GR_NON_VAL></GR_NON_VAL>\n" +
                "               <IR_IND>" + IR_IND + "</IR_IND>\n" +
                "               <FREE_ITEM></FREE_ITEM>\n" +
                "               <GR_BASEDIV>" + GR_BASEDIV + "</GR_BASEDIV>\n" +
                "               <ACKN_REQD></ACKN_REQD>\n" +
                "               <ACKNOWL_NO></ACKNOWL_NO>\n" +
                "               <AGREEMENT></AGREEMENT>\n" +
                "               <AGMT_ITEM></AGMT_ITEM>\n" +
                "               <SHIPPING></SHIPPING>\n" +
                "               <CUSTOMER></CUSTOMER>\n" +
                "               <COND_GROUP></COND_GROUP>\n" +
                "               <NO_DISCT></NO_DISCT>\n" +
                "               <PLAN_DEL></PLAN_DEL>\n" +
                "               <NET_WEIGHT></NET_WEIGHT>\n" +
                "               <WEIGHTUNIT></WEIGHTUNIT>\n" +
                "               <WEIGHTUNIT_ISO></WEIGHTUNIT_ISO>\n" +
                "               <TAXJURCODE></TAXJURCODE>\n" +
                "               <CTRL_KEY></CTRL_KEY>\n";
            if (DOC_TYPE.equals("ZIMM") || DOC_TYPE.equals("ZIMP")) {
                trama = trama + "<CONF_CTRL>Z001</CONF_CTRL>\n";
            } else {
                trama = trama + "<CONF_CTRL></CONF_CTRL>\n";
            }
            trama = trama + "" +
                "               <REV_LEV></REV_LEV>\n" +
                "               <FUND></FUND>\n" +
                "               <FUNDS_CTR></FUNDS_CTR>\n" +
                "               <CMMT_ITEM></CMMT_ITEM>\n" +
                "               <PRICEDATE></PRICEDATE>\n" +
                "               <PRICE_DATE></PRICE_DATE>\n" +
                "               <GROSS_WT></GROSS_WT>\n" +
                "               <VOLUME></VOLUME>\n" +
                "               <VOLUMEUNIT></VOLUMEUNIT>\n" +
                "               <VOLUMEUNIT_ISO></VOLUMEUNIT_ISO>\n" +
                "               <INCOTERMS1></INCOTERMS1>\n" +
                "               <INCOTERMS2></INCOTERMS2>\n" +
                "               <PRE_VENDOR></PRE_VENDOR>\n" +
                "               <VEND_PART></VEND_PART>\n" +
                "               <HL_ITEM></HL_ITEM>\n" +
                "               <GR_TO_DATE></GR_TO_DATE>\n" +
                "               <SUPP_VENDOR></SUPP_VENDOR>\n" +
                "               <SC_VENDOR></SC_VENDOR>\n" +
                "               <KANBAN_IND></KANBAN_IND>\n" +
                "               <ERS></ERS>\n" +
                "               <R_PROMO></R_PROMO>\n" +
                "               <POINTS></POINTS>\n" +
                "               <POINT_UNIT></POINT_UNIT>\n" +
                "               <POINT_UNIT_ISO></POINT_UNIT_ISO>\n" +
                "               <SEASON></SEASON>\n" +
                "               <SEASON_YR></SEASON_YR>\n" +
                "               <BON_GRP2></BON_GRP2>\n" +
                "               <BON_GRP3></BON_GRP3>\n" +
                "               <SETT_ITEM></SETT_ITEM>\n" +
                "               <MINREMLIFE></MINREMLIFE>\n" +
                "               <RFQ_NO></RFQ_NO>\n" +
                "               <RFQ_ITEM></RFQ_ITEM>\n" +
                "               <PREQ_NO>" + PREQ_NO + "</PREQ_NO>\n" +
                "               <PREQ_ITEM>" + PREQ_ITEM + "</PREQ_ITEM>\n" +
                "               <REF_DOC></REF_DOC>\n" +
                "               <REF_ITEM>" + REF_ITEM + "</REF_ITEM>\n" +
                "               <SI_CAT></SI_CAT>\n" +
                "               <RET_ITEM></RET_ITEM>\n" +
                "               <AT_RELEV></AT_RELEV>\n" +
                "               <ORDER_REASON></ORDER_REASON>\n" +
                "               <BRAS_NBM></BRAS_NBM>\n" +
                "               <MATL_USAGE></MATL_USAGE>\n" +
                "               <MAT_ORIGIN></MAT_ORIGIN>\n" +
                "               <IN_HOUSE></IN_HOUSE>\n" +
                "               <INDUS3></INDUS3>\n" +
                "               <INF_INDEX></INF_INDEX>\n" +
                "               <UNTIL_DATE></UNTIL_DATE>\n" +
                "               <DELIV_COMPL></DELIV_COMPL>\n" +
                "               <PART_DELIV></PART_DELIV>\n" +
                "               <SHIP_BLOCKED></SHIP_BLOCKED>\n" +
                "               <PREQ_NAME>CMENDEZ</PREQ_NAME>\n" +
                "               <PERIOD_IND_EXPIRATION_DATE>" + PERIOD_IND_EXPIRATION_DATE + "</PERIOD_IND_EXPIRATION_DATE>\n" +
                "               <INT_OBJ_NO></INT_OBJ_NO>\n" +
                "               <PCKG_NO></PCKG_NO>\n" +
                "               <BATCH></BATCH>\n" +
                "               <VENDRBATCH></VENDRBATCH>\n" +
                "               <CALCTYPE></CALCTYPE>\n" +
                "               <GRANT_NBR></GRANT_NBR>\n" +
                "               <CMMT_ITEM_LONG></CMMT_ITEM_LONG>\n" +
                "               <FUNC_AREA_LONG></FUNC_AREA_LONG>\n" +
                "               <NO_ROUNDING></NO_ROUNDING>\n" +
                "               <PO_PRICE></PO_PRICE>\n" +
                "               <SUPPL_STLOC></SUPPL_STLOC>\n" +
                "               <SRV_BASED_IV></SRV_BASED_IV>\n" +
                "               <FUNDS_RES></FUNDS_RES>\n" +
                "               <RES_ITEM></RES_ITEM>\n" +
                "               <ORIG_ACCEPT></ORIG_ACCEPT>\n" +
                "               <ALLOC_TBL></ALLOC_TBL>\n" +
                "               <ALLOC_TBL_ITEM></ALLOC_TBL_ITEM>\n" +
                "               <SRC_STOCK_TYPE></SRC_STOCK_TYPE>\n" +
                "               <REASON_REJ></REASON_REJ>\n" +
                "               <CRM_SALES_ORDER_NO></CRM_SALES_ORDER_NO>\n" +
                "               <CRM_SALES_ORDER_ITEM_NO></CRM_SALES_ORDER_ITEM_NO>\n" +
                "               <CRM_REF_SALES_ORDER_NO></CRM_REF_SALES_ORDER_NO>\n" +
                "               <CRM_REF_SO_ITEM_NO></CRM_REF_SO_ITEM_NO>\n" +
                "               <PRIO_URGENCY></PRIO_URGENCY>\n" +
                "               <PRIO_REQUIREMENT></PRIO_REQUIREMENT>\n" +
                "               <REASON_CODE></REASON_CODE>\n" +
                "</item>\n";

            //}
            ii++;
        }

        trama = trama + "</T_POITEM>\n" +
            "<T_POITEMX></T_POITEMX>\n" +
            "<T_POSCHEDULE></T_POSCHEDULE>\n" +
            "<T_POSCHEDULEX></T_POSCHEDULEX>\n" +
            "<T_POSERVICES></T_POSERVICES>\n" +
            "<T_POSRVACCESSVALUES></T_POSRVACCESSVALUES>\n" +
            "<T_RETURN>\n" +
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
            "         </T_RETURN>\n" +
            "      </urn:ZMM_GENERAR_OC>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>\n";

        return trama;

    }
}
