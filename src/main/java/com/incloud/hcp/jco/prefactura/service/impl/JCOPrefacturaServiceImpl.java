package com.incloud.hcp.jco.prefactura.service.impl;

import com.incloud.hcp.domain.OrdenCompraDetalle;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.dto.PrefacturaActualizarDto;
import com.incloud.hcp.jco.prefactura.dto.*;
import com.incloud.hcp.jco.prefactura.service.JCOPrefacturaService;
import com.incloud.hcp.repository.OrdenCompraDetalleRepository;
import com.incloud.hcp.repository.OrdenCompraRepository;
import com.incloud.hcp.sap.SapLog;
//import com.sap.conn.jco.*;
import com.incloud.hcp.util.Utils;
import com.sap.cloud.sdk.cloudplatform.connectivity.*;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import io.vavr.control.Try;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public class JCOPrefacturaServiceImpl implements JCOPrefacturaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${destination.rfc.profit}")
    private String destinationProfit;

    @Override
    public PrefacturaRFCResponseDto registrarPrefacturaRFC(PrefacturaRFCRequestDto prefacturaRFCRequestDto) throws Exception {
        try {
            logger.error("PREFACTURA RFC /// REQUEST: " + prefacturaRFCRequestDto.toString());
            String FUNCION_RFC = "ZPE_MM_PREREG_FACTS";
            PrefacturaRFCResponseDto responseDto = new PrefacturaRFCResponseDto();
            List<SapLog> sapMessageList = new ArrayList<>();
            String tramaXML = armarTramaPrefactura(prefacturaRFCRequestDto);

//            JCoDestination destination = JCoDestinationManager.getDestination(destinationProfit);
//            JCoRepository repository = destination.getRepository();
//
//            JCoFunction jCoFunction = repository.getFunction(FUNCION_RFC);
//            this.mapFilters(jCoFunction, prefacturaRFCRequestDto);
//            jCoFunction.execute(destination);
//
//            JCoParameterList exportParameterList = jCoFunction.getExportParameterList();
//            String codigoDocumentoSap = exportParameterList.getString("E_BELNR");
//            String ejercicio = exportParameterList.getString("E_GJAHR");
//            String numeroDocumentoContable = exportParameterList.getString("E_BKPFBELNR");
//            logger.error("PREFACTURA RFC /// codigoDocumentoSap: " + codigoDocumentoSap);
//            logger.error("PREFACTURA RFC /// ejercicio: " + ejercicio);
//            logger.error("PREFACTURA RFC /// numeroDocumentoContable: " + numeroDocumentoContable);
//            responseDto.setCodigoDocumentoSap(codigoDocumentoSap);
//            responseDto.setEjercicio(ejercicio);
//            responseDto.setNumeroDocumentoContable(numeroDocumentoContable);
//
//            JCoParameterList exportTableList = jCoFunction.getTableParameterList();
//            JCoTable table = exportTableList.getTable("OT_RETURN");

//            if (table != null && !table.isEmpty()) {
//                do {
//                    SapLog sapMessage = new SapLog();
//
//                    sapMessage.setCode(table.getString("TYPE"));
//                    sapMessage.setMesaj(table.getString("MESSAGE"));
//
//                    sapMessageList.add(sapMessage);
//                } while (table.nextRow());
//            }

            logger.info("RFC: DESTINATION - " + destinationProfit);
            Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);

            logger.info(String.valueOf(destination2.get()));
            HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));


            logger.info(String.valueOf(client));
            //Uri x = new Uri("http://connectivityproxy.internal.cf.us10.hana.ondemand.com:20003");
        /*Uri x = new Uri(destination2.get().asHttp().getProxyConfiguration().get().getUri().getPath());
        logger.info(x.getHost());
        logger.info(String.valueOf(x.getPort()));
        */
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
            String url = urlbase + "/sap/bc/srt/rfc/sap/ZWS_PREREG_FACTS?sap-client=400&wsdl=1.1";

            logger.info("RFC: URL " + url);


            //String tramaXML = armarTrama2();

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
            post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions/ZWS_PREREG_FACTS/ZMM_PREREG_FACTSRequest");
            post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions/ZWS_PREREG_FACTS/ZMM_PREREG_FACTSRequest");
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

            String codigoDocumentoSap = "";
            String ejercicio = "";
            String numeroDocumentoContable = "";

            NodeList nodes1  = doc.getElementsByTagName("E_BELNR").item(0).getChildNodes();
                Node node1 = (Node) nodes1.item(0);
                if(node1 != null){
                    codigoDocumentoSap = node1.getNodeValue();
                }else{
                    codigoDocumentoSap = "";
                }

            NodeList nodes2  = doc.getElementsByTagName("E_GJAHR").item(0).getChildNodes();
            Node node2 = (Node) nodes2.item(0);
            if(node2 != null){
                ejercicio = node2.getNodeValue();
            }else{
                ejercicio = "";
            }

            NodeList nodes3  = doc.getElementsByTagName("E_BKPFBELNR").item(0).getChildNodes();
            Node node3 = (Node) nodes3.item(0);
            if(node3 != null){
                numeroDocumentoContable = node3.getNodeValue();
            }else{
                numeroDocumentoContable = "";
            }



            NodeList table = doc.getElementsByTagName("OT_RETURN").item(0).getChildNodes();
            if (table != null) {
                for (int i = 0; i < table.getLength(); i++) {

                    Node posicion = table.item(i);
                    Element elemento = (Element) posicion;
                    SapLog sapMessage = new SapLog();
                    sapMessage.setCode(Utils.getValueNodo(elemento, "TYPE"));
                    sapMessage.setMesaj(Utils.getValueNodo(elemento, "MESSAGE"));
                    sapMessageList.add(sapMessage);

                }
            }
            logger.error("PREFACTURA RFC /// codigoDocumentoSap: " + codigoDocumentoSap);
            logger.error("PREFACTURA RFC /// ejercicio: " + ejercicio);
            logger.error("PREFACTURA RFC /// numeroDocumentoContable: " + numeroDocumentoContable);
            responseDto.setCodigoDocumentoSap(codigoDocumentoSap);
            responseDto.setEjercicio(ejercicio);
            responseDto.setNumeroDocumentoContable(numeroDocumentoContable);
            responseDto.setSapMessageList(sapMessageList);
            logger.error("PREFACTURA RFC /// RESPONSE: " + responseDto.toString());
            return responseDto;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e.getCause());
            throw new Exception(e);
        }
    }

    @Autowired
    private OrdenCompraDetalleRepository ordenCompraDetalleRepository;

    private String armarTramaPrefactura(PrefacturaRFCRequestDto prefacturaRFCRequestDto)
    {

        String TAX_CODE = prefacturaRFCRequestDto.getPrefacturaRFCPosicionDtoList().get(0).getIndicadorImpuesto();
        String nroOc = prefacturaRFCRequestDto.getPrefacturaRFCPosicionDtoList().get(0).getNumeroOrdenCompra();
        List<OrdenCompraDetalle> ocd =  ordenCompraDetalleRepository.findByNumeroOrdenCompra(nroOc);
        if(ocd.size() > 0){
            TAX_CODE = ocd.get(0).getIndicadorImpuesto();
        }
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String tramaXMLactualizar = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_PREREG_FACTS>\n" +
                " <!--Optional:-->\n" +
                "         <IT_ACCOUNTINGDATA>\n" +
                " <!--Enviar vacía, se llena en sap:-->\n" +
                "         </IT_ACCOUNTINGDATA>"+
                "         <!--Optional:-->\n" +
                "         <IT_ITEMS>\n" +
                "            <!--Zero or more repetitions:-->\n";
            String OC = "";
            for (int i = 0; i < prefacturaRFCRequestDto.getPrefacturaRFCPosicionDtoList().size(); i++) {
                PrefacturaRFCPosicionDto rfcPosicionDto = prefacturaRFCRequestDto.getPrefacturaRFCPosicionDtoList().get(i);

                String  INVOICE_DOC_ITEM = String.format("%04d", (i+1));//rfcPosicionDto.getNumeroItem();

                String  PO_NUMBER=rfcPosicionDto.getNumeroOrdenCompra();
                OC = rfcPosicionDto.getNumeroOrdenCompra();
                String  PO_ITEM=rfcPosicionDto.getNumeroPosicion();
                String  REF_DOC=rfcPosicionDto.getNumeroDocumentoAceptacion();
                String  REF_DOC_YEAR=rfcPosicionDto.getYearEmision();
                String  REF_DOC_IT=rfcPosicionDto.getNumeroItem();//posicion de documento

                String  ITEM_AMOUNT= String.valueOf(rfcPosicionDto.getValorFacturado().setScale(2, BigDecimal.ROUND_HALF_UP));

                String  QUANTITY= String.valueOf(rfcPosicionDto.getCantidadFacturada().setScale(2, BigDecimal.ROUND_HALF_UP));

                String  PO_UNIT=rfcPosicionDto.getUnidadMedida();
                String  COND_TYPE="C030"; //? clase de condicion
                String  SHEET_NO=rfcPosicionDto.getNumeroDocumentoAceptacion(); //? numero de hoja de entrada
                String  SHEET_ITEM =  rfcPosicionDto.getNumeroItem();//?

                if(rfcPosicionDto.getTipoDocumentoAceptacion().equals("M"))
                {
                    SHEET_NO= "";
                    SHEET_ITEM = "";
                }else
                {
                    SHEET_ITEM = ("0000000000" + SHEET_ITEM);
                    SHEET_ITEM  = SHEET_ITEM.substring(SHEET_ITEM.length() - 10);

                    REF_DOC = "";
                    REF_DOC_IT = "";
                }


                tramaXMLactualizar = tramaXMLactualizar+"<item>\n" +
                        "               <INVOICE_DOC_ITEM>"+INVOICE_DOC_ITEM+"</INVOICE_DOC_ITEM>\n" +
                        "               <PO_NUMBER>"+PO_NUMBER+"</PO_NUMBER>\n" +
                        "               <PO_ITEM>"+PO_ITEM+"</PO_ITEM>\n" +
                        "               <REF_DOC>"+REF_DOC+"</REF_DOC>\n" +
                        "               <REF_DOC_YEAR>"+REF_DOC_YEAR+"</REF_DOC_YEAR>\n" +
                        "               <REF_DOC_IT>"+REF_DOC_IT+"</REF_DOC_IT>\n" +
                        "               <DE_CRE_IND></DE_CRE_IND>\n" +
                        "               <TAX_CODE>"+TAX_CODE+"</TAX_CODE>\n" +
                        "               <TAXJURCODE>"+""+"</TAXJURCODE>\n" +
                        "               <ITEM_AMOUNT>"+ITEM_AMOUNT+"</ITEM_AMOUNT>\n" +
                        "               <QUANTITY>"+QUANTITY+"</QUANTITY>\n" +
                        "               <PO_UNIT>"+PO_UNIT+"</PO_UNIT>\n" +
                        "               <PO_UNIT_ISO>"+""+"</PO_UNIT_ISO>\n" +
                        "               <PO_PR_QNT>"+ITEM_AMOUNT+"</PO_PR_QNT>\n" +
                        "               <PO_PR_UOM>"+""+"</PO_PR_UOM>\n" +
                        "               <PO_PR_UOM_ISO>"+""+"</PO_PR_UOM_ISO>\n" +
                        "               <COND_TYPE>"+""+"</COND_TYPE>\n" +
                        "               <COND_ST_NO>"+""+"</COND_ST_NO>\n" +
                        "               <COND_COUNT>"+""+"</COND_COUNT>\n" +
                        "               <SHEET_NO>"+SHEET_NO+"</SHEET_NO>\n" +
                        "               <ITEM_TEXT>"+""+"</ITEM_TEXT>\n" +
                        "               <FINAL_INV>"+""+"</FINAL_INV>\n" +
                        "               <SHEET_ITEM>"+SHEET_ITEM+"</SHEET_ITEM>\n" +
                        "               <GRIR_CLEAR_SRV>"+""+"</GRIR_CLEAR_SRV>\n" +
                        "               <FREIGHT_VEN>"+""+"</FREIGHT_VEN>\n" +
                        "               <CSHDIS_IND>"+""+"</CSHDIS_IND>\n" +
/*                        "               <RETENTION_DOCU_CURRENCY>"+""+"</RETENTION_DOCU_CURRENCY>\n" +
                        "               <RETENTION_PERCENTAGE>"+""+"</RETENTION_PERCENTAGE>\n" +
                        "               <RETENTION_DUE_DATE>"+""+"</RETENTION_DUE_DATE>\n" +
                        "               <NO_RETENTION>"+""+"</NO_RETENTION>\n" +
                        "               <VALUATION_TYPE>"+""+"</VALUATION_TYPE>\n" +*/
                        "            </item>\n" ;
            }
                String I_BLINE_DATE =prefacturaRFCRequestDto.getFechaEmision();
                String I_CALC_TAX_IND ="X";
                String I_COMP_CODE ="";//prefacturaRFCRequestDto.getSociedad();
                String I_DOC_DATE=prefacturaRFCRequestDto.getFechaEmision();
                String I_DOC_TYPE="01";
                String I_EBELN=prefacturaRFCRequestDto.getPrefacturaRFCPosicionDtoList().get(0).getNumeroOrdenCompra();
                String I_EXCH_RATE="1";//tipo de cambio cotizado directamente
                String I_GROSS_AMOUNT=prefacturaRFCRequestDto.getIgvTotal().toString(); //
                String I_HEADER_TXT;
                if (prefacturaRFCRequestDto.getTextoCabecera().trim().length() > 25) {
                    I_HEADER_TXT = prefacturaRFCRequestDto.getTextoCabecera().substring(0,24);
                } else {
                    I_HEADER_TXT=prefacturaRFCRequestDto.getTextoCabecera();
                }
                String I_OC_TXT = prefacturaRFCRequestDto.getPrefacturaRFCPosicionDtoList().get(0).getNumeroOrdenCompra();
                String I_INVOICE_IND="X";
                String I_PSTNG_DATE=prefacturaRFCRequestDto.getFechaContabilizacion();
                String I_REF_DOC_NO=prefacturaRFCRequestDto.getPrefacturaRFCPosicionDtoList().get(0).getNumeroDocumentoAceptacion();
                String I_REF_DOC_NO_LONG=prefacturaRFCRequestDto.getReferencia();//numero de documento referencia
                if (I_REF_DOC_NO_LONG.startsWith("01-0")) {
                    I_REF_DOC_NO_LONG = I_REF_DOC_NO_LONG.substring(4);
                }
                String I_TAX_CODE=TAX_CODE;
                String I_USNAM="QA_CSTI";

                tramaXMLactualizar = tramaXMLactualizar +"         </IT_ITEMS>\n" +
                "         <!--Optional:-->\n" +
                "         <I_ALLOC_NMBR>"+I_REF_DOC_NO_LONG+"</I_ALLOC_NMBR>"+ //OC
                "         <!--Optional:-->\n" +
                "         <I_BLINE_DATE>"+I_BLINE_DATE+"</I_BLINE_DATE>\n" +
                "         <!--Optional:-->\n" +
                "         <I_CALC_TAX_IND>"+I_CALC_TAX_IND+"</I_CALC_TAX_IND>\n" +
                "         <!--Optional:-->\n" +
                "         <I_COMP_CODE>"+I_COMP_CODE+"</I_COMP_CODE>\n" +
                "         <!--Optional:-->\n" +
                "         <I_CURRENCY>"+prefacturaRFCRequestDto.getMoneda()+"</I_CURRENCY>"+
                "         <!--Optional:-->\n" +
                "         <I_DOC_DATE>"+I_DOC_DATE+"</I_DOC_DATE>\n" +
                "         <!--Optional:-->\n" +
                "         <I_DOC_TYPE>"+I_DOC_TYPE+"</I_DOC_TYPE>\n" +
                "         <!--Optional:-->\n" +
                "         <I_EBELN>"+I_EBELN+"</I_EBELN>\n" +
                "         <!--Optional:-->\n" +
                "         <I_EXCH_RATE>"+I_EXCH_RATE+"</I_EXCH_RATE>\n" +
                "         <!--Optional:-->\n" +
                "         <I_GROSS_AMOUNT>"+I_GROSS_AMOUNT+"</I_GROSS_AMOUNT>\n" +
                "         <!--Optional:-->\n" +
                "         <I_HEADER_TXT>"+I_HEADER_TXT+"</I_HEADER_TXT>\n" +
                "         <!--Optional:-->\n" +
                "         <I_INVOICE_IND>"+I_INVOICE_IND+"</I_INVOICE_IND>\n" +
                "         <!--Optional:-->\n" +
                "         <I_PAYMT_REF>"+I_REF_DOC_NO_LONG+"</I_PAYMT_REF>\n"+
                "         <!--Optional:-->\n" +
                "         <I_PSTNG_DATE>"+I_PSTNG_DATE+"</I_PSTNG_DATE>\n" +
                "         <!--Optional:-->\n" +
                "         <I_PYMT_METH></I_PYMT_METH>\n" +
                "         <!--Optional:-->\n" +
                "         <I_REF_DOC_NO>"+I_REF_DOC_NO_LONG+"</I_REF_DOC_NO>\n" +
                "         <!--Optional:-->\n" +
                "         <I_REF_DOC_NO_LONG>"+I_REF_DOC_NO_LONG+"</I_REF_DOC_NO_LONG>\n" +
                "         <!--Optional:-->\n" +
                "         <I_SGTXT>"+I_OC_TXT+"</I_SGTXT>"+
                "         <!--Optional:-->\n" +
                "         <I_TAX_CODE>"+I_TAX_CODE+"</I_TAX_CODE>\n" +
                "         <I_USNAM>"+I_USNAM+"</I_USNAM>\n"+
                "          <!--Optional:-->\n" +
                "         <OT_GLACCOUNTDATA>\n" +
                "            <!--Zero or more repetitions:-->\n";
                tramaXMLactualizar=tramaXMLactualizar+"</OT_GLACCOUNTDATA>\n" +
                "         <!--Optional:-->\n" +
                "         <OT_RETURN>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "         </OT_RETURN>\n" +
                "      </urn:ZMM_PREREG_FACTS>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return tramaXMLactualizar;

    }

    @Override
    public List<PrefacturaAnuladaDto> obtenerPrefacturaAnuladaListRFC(String fechaInicio, String fechaFin) throws Exception {
        try {
            String FUNCION_RFC = "ZPE_MM_FACTURAS_ANULADAS";

//            JCoDestination destination = JCoDestinationManager.getDestination(destinationProfit);
//            JCoRepository repository = destination.getRepository();
//            JCoFunction jCoFunction = repository.getFunction(FUNCION_RFC);
//
//            this.mapFacturasAnuladasFilters(jCoFunction, fechaInicio, fechaFin);
//            jCoFunction.execute(destination);
//
//            JCoParameterList exportTableList = jCoFunction.getTableParameterList();
            GenericDtoExtractorMapper genericDtoExtractorMapper = null;// GenericDtoExtractorMapper.newMapper(exportTableList);

            return genericDtoExtractorMapper.getPrefacturaAnuladaDtoList();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e.getCause());
            throw new Exception(e);
        }
    }


    @Override
    public List<PrefacturaRegistradaSapDto> obtenerPrefacturaRegistradaSapListRFC(List<PrefacturaActualizarDto> prefacturaActualizarDtoList) throws Exception {
        try {
            String FUNCION_RFC = "ZPE_MM_CARGA_FACTURAS";

//            JCoDestination destination = JCoDestinationManager.getDestination(destinationProfit);
//            JCoRepository repository = destination.getRepository();
//            JCoFunction jCoFunction = repository.getFunction(FUNCION_RFC);
//
//            JCoParameterList paramList = jCoFunction.getImportParameterList();
//            JCoTable inputJCoTable = paramList.getTable("IT_MM_RUC_XBLNR");
//
//            for (int i = 0; i < prefacturaActualizarDtoList.size(); i++) {
//                PrefacturaActualizarDto prefacturaActualizar = prefacturaActualizarDtoList.get(i);
//                inputJCoTable.appendRow();
//                inputJCoTable.setRow(i);
//                inputJCoTable.setValue("STCD1", prefacturaActualizar.getRucProveedor());
//                inputJCoTable.setValue("XBLNR", prefacturaActualizar.getReferenciaSap());
//            }
//
//            jCoFunction.execute(destination);
//
//            JCoParameterList exportTableList = jCoFunction.getTableParameterList();
            GenericDtoExtractorMapper genericDtoExtractorMapper = null;//GenericDtoExtractorMapper.newMapper(exportTableList);

            return genericDtoExtractorMapper.getPrefacturaRegistradaSapDtoList();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e.getCause());
            throw new Exception(e);
        }
    }


//
//    private void mapFilters(JCoFunction function, PrefacturaRFCRequestDto prefacturaRFCRequestDto) {
//        JCoParameterList paramList = function.getImportParameterList();
//
////        paramList.setValue("I_DOC_TYPE", "01"); // tipo de documento (01=facttura)
//
//        String sociedad = prefacturaRFCRequestDto.getSociedad();
//        if (sociedad != null && !sociedad.isEmpty())
//            paramList.setValue("I_COMP_CODE", sociedad);
//
//        String referencia = prefacturaRFCRequestDto.getReferencia();
//        if (referencia != null && !referencia.isEmpty())
//            paramList.setValue("I_REF_DOC_NO", referencia);
//
//        String fechaEmision = prefacturaRFCRequestDto.getFechaEmision();
//        if (fechaEmision != null && !fechaEmision.isEmpty())
//            paramList.setValue("I_DOC_DATE", fechaEmision);
//
//        String fechaContabilizacion = prefacturaRFCRequestDto.getFechaContabilizacion();
//        if (fechaContabilizacion != null && !fechaContabilizacion.isEmpty())
//            paramList.setValue("I_PSTNG_DATE", fechaContabilizacion);
//
//        String fechaBase = prefacturaRFCRequestDto.getFechaBase();
//        if (fechaBase != null && !fechaBase.isEmpty())
//            paramList.setValue("I_BLINE_DATE", fechaBase);
//
//        String indicadorImpuesto = prefacturaRFCRequestDto.getIndicadorImpuesto();
//        if (indicadorImpuesto != null && !indicadorImpuesto.isEmpty())
//            paramList.setValue("I_TAX_CODE", indicadorImpuesto);
//
//        BigDecimal baseImponibleTotal = prefacturaRFCRequestDto.getBaseImponibleTotal();
//        if (baseImponibleTotal != null)
//            paramList.setValue("I_GROSS_AMOUNT", baseImponibleTotal);
//
////        BigDecimal igvTotal = prefacturaRFCRequestDto.getIgvTotal();
////        if (igvTotal != null)
////            paramList.setValue("I_EXCH_RATE", igvTotal);
//
////        BigDecimal retencionTotal = prefacturaRFCRequestDto.getRetencionTotal();
////        if (retencionTotal != null)
////            paramList.setValue("I_XXXXX", retencionTotal);
//
//        String textoCabecera = prefacturaRFCRequestDto.getTextoCabecera();
//        if (textoCabecera != null && !textoCabecera.isEmpty())
//            paramList.setValue("I_HEADER_TXT", textoCabecera);
//
//        String usuarioRegistroSap = prefacturaRFCRequestDto.getUsuarioRegistroSap();
//        if (usuarioRegistroSap != null && !usuarioRegistroSap.isEmpty())
//            paramList.setValue("I_USNAM", usuarioRegistroSap);
//
////        String moneda = prefacturaRFCRequestDto.getMoneda();
////        if (moneda != null && !moneda.isEmpty())
////            paramList.setValue("I_XXXXX", moneda);
//
//        JCoTable ocPosicionJCoTable = paramList.getTable("IT_ITEMS");
//        List<PrefacturaRFCPosicionDto> posicionDtoList = prefacturaRFCRequestDto.getPrefacturaRFCPosicionDtoList();
//
//        for (int i = 0; i < posicionDtoList.size(); i++) {
//            PrefacturaRFCPosicionDto posicionDto = posicionDtoList.get(i);
//
//            ocPosicionJCoTable.appendRow();
//            ocPosicionJCoTable.setRow(i);
//            ocPosicionJCoTable.setValue("INVOICE_DOC_ITEM", String.format("%06d", (i+1)*10));
//            ocPosicionJCoTable.setValue("PO_NUMBER", posicionDto.getNumeroOrdenCompra());
//            ocPosicionJCoTable.setValue("PO_ITEM", posicionDto.getNumeroPosicion());
//            ocPosicionJCoTable.setValue("QUANTITY", posicionDto.getCantidadFacturada());
//            ocPosicionJCoTable.setValue("PO_UNIT", posicionDto.getUnidadMedida());
//            ocPosicionJCoTable.setValue("ITEM_AMOUNT", posicionDto.getValorFacturado());
//
//            if(posicionDto.getTipoDocumentoAceptacion().equals("M")){ // --> EM
//                ocPosicionJCoTable.setValue("REF_DOC", posicionDto.getNumeroDocumentoAceptacion());
//                ocPosicionJCoTable.setValue("REF_DOC_IT", posicionDto.getNumeroItem());
//                ocPosicionJCoTable.setValue("REF_DOC_YEAR", posicionDto.getYearEmision());
//            }
//            else{ // tipo "S" --> HES
//                ocPosicionJCoTable.setValue("SHEET_NO", posicionDto.getNumeroDocumentoAceptacion());
//                ocPosicionJCoTable.setValue("SHEET_ITEM", posicionDto.getNumeroItem());
//            }
//        }
//    }

//
//    private void mapFacturasAnuladasFilters(JCoFunction function, String fechaInicio, String fechaFin) {
//        JCoParameterList paramList = function.getImportParameterList();
//
//        if (!fechaInicio.isEmpty() && !fechaFin.isEmpty()){
//            JCoTable jcoTableBUDAT = paramList.getTable("IP_BUDAT");
//            jcoTableBUDAT.appendRow();
//            jcoTableBUDAT.setRow(0);
//            jcoTableBUDAT.setValue("SIGN", "I");
//            jcoTableBUDAT.setValue("LOW", fechaInicio);
//
//            if (fechaInicio.equals(fechaFin)) {
//                jcoTableBUDAT.setValue("OPTION", "EQ");
//            } else {
//                jcoTableBUDAT.setValue("OPTION", "BT");
//                jcoTableBUDAT.setValue("HIGH", fechaFin);
//            }
//        }
//    }
}