package com.incloud.hcp.rest;

import com.incloud.hcp.bean.MensajePrefactura;
import com.incloud.hcp.domain.DocumentoAceptacion;
import com.incloud.hcp.domain.DocumentoAceptacionDetalle;
import com.incloud.hcp.domain.OrdenCompra;
import com.incloud.hcp.domain.Sociedad;
import com.incloud.hcp.dto.PrefacturaDto;
import com.incloud.hcp.repository.DocumentoAceptacionDetalleRepository;
import com.incloud.hcp.repository.DocumentoAceptacionRepository;
import com.incloud.hcp.repository.OrdenCompraRepository;
import com.incloud.hcp.service.SociedadService;
import com.incloud.hcp.service.wsdlSunat.BillConsultPortBidingServiceLocator;
import com.incloud.hcp.service.wsdlSunat.BillConsultService;
import com.incloud.hcp.service.wsdlSunat.StatusResponse;
import com.incloud.hcp.service.wsdlSunat.flyWeight.FunctionsXML;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.util.NumberUtils;
import com.incloud.hcp.util.Utils;
import org.apache.axis.client.Stub;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


@RestController
@RequestMapping(value = "/api/Sunat")
public class ServicioSunatRest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private SociedadService sociedadService;

    @Value("${cfg.sunat.user}")
    private String repositoryUser;

    @Value("${cfg.sunat.pass}")
    private String repositoryPass;

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    @Autowired
    private DocumentoAceptacionRepository documentoAceptacionRepository;

    @Autowired
    private DocumentoAceptacionDetalleRepository documentoAceptacionDetalleRepository;

    @Autowired
    public ServicioSunatRest(SociedadService sociedadService) {
        this.sociedadService = sociedadService;
    }

    @PostMapping(value = "/ValidarComprobante", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<MensajePrefactura> VerificarComprobanteRest(@RequestParam(value = "file") MultipartFile archivoSunat,
                                                                      @RequestParam(value = "ordenCompra") String ordenCompra,
                                                                      @RequestParam(value = "nroDocumento") String nroDocumento,
                                                                      @RequestParam(value = "nroGuiaRemision") String nroGuiaRemision) {


        MensajePrefactura bean = new MensajePrefactura();
        String codess;
        String messages;
        String serieComprobante = "";
        String nroComprobante = "";
        PrefacturaDto prefactura = new PrefacturaDto();

        //@add ppo 18.01.2021

        String rucProveedor = "";
        String rucCliente = "";
        String tipoComprobante = "";
        String montoTotal = "";
        String igv = "";
        String subTotal = "";
        String referenciaFactura = "";
        String fechaEmisionString = "";
        String codigoMoneda = "";
        Date fechaEmision = null;
        NodeList facturaNodeList = null;
        String importe = "";
        String texto = "";
        String cadenaNumerosOc = "";

        Optional<DocumentoAceptacion> documentoAceptacion = null;
        BigDecimal totalOc = new BigDecimal(0);
        if(nroDocumento.contains("-")){
            String[] parts = nroDocumento.split("-");
            System.out.println(parts);
            for (int i = 0; i < parts.length; i++) {
                documentoAceptacion = documentoAceptacionRepository.findByNumeroDocumentoAceptacion(parts[i].trim());
                if(documentoAceptacion.isPresent()){
                    List<DocumentoAceptacionDetalle> documentoAceptacionDetalleList =
                            documentoAceptacionDetalleRepository.getByIdDocumentoAceptacion(documentoAceptacion.get().getId());
                    for (DocumentoAceptacionDetalle item : documentoAceptacionDetalleList){
                        //totalOc = totalOc.add( item.getPrecioUnitario().multiply(item.getCantidadAceptadaCliente()));
                        if(item.getDocumentoAceptacion().getTipoDocumentoAceptacion().getId().equals(1)){
                            totalOc = totalOc.add( item.getPrecioUnitario().multiply(item.getCantidadAceptadaCliente()));
                        }else{
                            totalOc = totalOc.add( item.getPrecioUnitario());
                        }
                    }
                }
            }
        }else{
            documentoAceptacion = documentoAceptacionRepository.findByNumeroDocumentoAceptacion(nroDocumento);
            if(documentoAceptacion.isPresent()){
                List<DocumentoAceptacionDetalle> documentoAceptacionDetalleList =
                        documentoAceptacionDetalleRepository.getByIdDocumentoAceptacion(documentoAceptacion.get().getId());
                for (DocumentoAceptacionDetalle item : documentoAceptacionDetalleList){
                    if(item.getDocumentoAceptacion().getTipoDocumentoAceptacion().getId().equals(1)){
                        totalOc = totalOc.add( item.getPrecioUnitario().multiply(item.getCantidadAceptadaCliente()));
                    }else{
                        totalOc = totalOc.add( item.getPrecioUnitario());
                    }
                }
            }
        }
        System.out.println(totalOc);
        //Fin
        try {
            File sunatXml = FunctionsXML.convert(archivoSunat);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(sunatXml);
            //@add ppo 18.01.2020
            boolean isStandar = true;
            NodeList facturaNodeListAux = doc.getElementsByTagName("Invoice");
            if (facturaNodeListAux.getLength() == 0) {
                facturaNodeListAux = doc.getElementsByTagName("tns:Invoice");
            }

            if (facturaNodeListAux.getLength() == 0) {
                isStandar = false;
            }


            //region validcion guia remision
            XPath xPathValidation = XPathFactory.newInstance().newXPath();
            String signatureId = xPathValidation.compile("/Invoice/Signature/ID").evaluate(doc);
            /*
            String nroGuiaXml=xPathValidation.compile("/Invoice/DespatchDocumentReference/ID").evaluate(doc).trim(); //mizalo
            if(!nroGuiaRemision.equals("null")){

                //if (!signatureId.equals(nroGuiaRemision))
                if(!nroGuiaXml.equals("null") || !nroGuiaXml.equals("") || nroGuiaXml.length()>0 ) {// mizalo
                    String[] nroGuia = nroGuiaXml.split("-");  // mizalo
                    String newNroGuia = nroGuia[0].trim() + "-" + nroGuia[1].trim(); //mizalo
                    if (!newNroGuia.equals(nroGuiaRemision)) {  //mizalo

                        throw new Exception("El número de guía de remisión ingresado es distinto al documento importado.");
                    }

                }
            //endregion
            }
            */

            //Fin
            logger.error("estandar "+isStandar);
            if (isStandar) {//Como siempre estubo funcionando
                logger.error("VerificarComprobanteRest 0 :: " + isStandar);
                XPath xPath = XPathFactory.newInstance().newXPath();
                referenciaFactura = xPath.compile("/Invoice/ID").evaluate(doc);
                String montoImpuestos = xPath.compile("/Invoice/TaxTotal/TaxAmount").evaluate(doc);
                String rucProveedorAlt = xPath.compile("/Invoice/AccountingSupplierParty/Party/PartyIdentification/ID").evaluate(doc);
                String rucClienteAlt = xPath.compile("/Invoice/AccountingCustomerParty/Party/PartyIdentification/ID").evaluate(doc);

                doc.getDocumentElement().normalize();

                NodeList proveedorNodeList = doc.getElementsByTagName("cac:AccountingSupplierParty");
                if (proveedorNodeList.getLength() == 0)
                    proveedorNodeList = doc.getElementsByTagName("n5:AccountingSupplierParty");

                NodeList clienteNodeList = doc.getElementsByTagName("cac:AccountingCustomerParty");
                if (clienteNodeList.getLength() == 0)
                    clienteNodeList = doc.getElementsByTagName("n5:AccountingCustomerParty");

                facturaNodeList = doc.getElementsByTagName("Invoice");
                if (facturaNodeList.getLength() == 0)
                    facturaNodeList = doc.getElementsByTagName("tns:Invoice");

                NodeList taxSubTotalNodeList = doc.getElementsByTagName("cac:TaxTotal");
                if (taxSubTotalNodeList.getLength() == 0)
                    taxSubTotalNodeList = doc.getElementsByTagName("n5:TaxTotal");

                NodeList taxTotalNodeList = doc.getElementsByTagName("cac:LegalMonetaryTotal");
                if (taxTotalNodeList.getLength() == 0)
                    taxTotalNodeList = doc.getElementsByTagName("n5:LegalMonetaryTotal");

                rucProveedor = FunctionsXML.getTagValueHTML(proveedorNodeList, "cbc:ID");
                if (rucProveedor == null)
                    rucProveedor = FunctionsXML.getTagValueHTML(proveedorNodeList, "n4:ID");

                rucCliente = FunctionsXML.getTagValueHTML(clienteNodeList, "cbc:ID");
                if (rucCliente == null)
                    rucCliente = FunctionsXML.getTagValueHTML(clienteNodeList, "n4:ID");

                if (rucProveedor == null || (!NumberUtils.stringIsLong(rucProveedor) && rucProveedor.length() != 11))
                    rucProveedor = rucProveedorAlt;

                if (rucCliente == null || (!NumberUtils.stringIsLong(rucCliente) && rucCliente.length() != 11))
                    rucCliente = rucClienteAlt;

                tipoComprobante = FunctionsXML.getTagValueHTML(facturaNodeList, "cbc:InvoiceTypeCode");
                if (tipoComprobante == null)
                    tipoComprobante = FunctionsXML.getTagValueHTML(facturaNodeList, "n4:InvoiceTypeCode");

                montoTotal = FunctionsXML.getTagValueHTML(taxTotalNodeList, "cbc:PayableAmount");
                if (montoTotal == null)
                    montoTotal = FunctionsXML.getTagValueHTML(taxTotalNodeList, "n4:PayableAmount");
                //======================================================================================================
                // CAMPOS NUEVOS
                //======================================================================================================
                NodeList taxPriceNodeList = doc.getElementsByTagName("cac:TaxSubtotal");
                importe = FunctionsXML.getTagValueHTML(taxPriceNodeList, "cbc:TaxableAmount");
                if (importe == null)
                    importe = FunctionsXML.getTagValueHTML(taxPriceNodeList, "cbc:TaxableAmount");
                if(importe.equals("0.00"))
                    importe = FunctionsXML.getTagValueHTMLImporte(taxPriceNodeList, "cbc:TaxableAmount");
                // texto item posicion 0
                NodeList taxItemNodeList = doc.getElementsByTagName("cac:Item");
                if (taxItemNodeList != null) {
                    texto = FunctionsXML.getTagValueHTML(taxItemNodeList, "cbc:Description");
                }
                cadenaNumerosOc = FunctionsXML.getTagValueHTML(facturaNodeList, "cbc:InvoiceTypeCode");

                codigoMoneda = FunctionsXML.getTagValueHTML(facturaNodeList, "cbc:DocumentCurrencyCode");
                //======================================================================================================
                igv = "";
                List<String> igvList = FunctionsXML.getTagValueIntoTagHTML(taxSubTotalNodeList, "cbc:Name", "IGV", "cbc:TaxAmount");
                if (igvList.isEmpty())
                    igvList = FunctionsXML.getTagValueIntoTagHTML(taxSubTotalNodeList, "n4:Name", "IGV", "n4:TaxAmount");
                if (!igvList.isEmpty())
                    igv = igvList.get(0);
                if (igv == null || !NumberUtils.stringIsBigDecimal(igv))
                    igv = montoImpuestos;

                subTotal = "";
                List<String> subTotalList = FunctionsXML.getTagValueIntoTagHTML(taxSubTotalNodeList, "cbc:Name", "IGV", "cbc:TaxableAmount");
                if (subTotalList.isEmpty())
                    subTotalList = FunctionsXML.getTagValueIntoTagHTML(taxSubTotalNodeList, "n4:Name", "IGV", "n4:TaxableAmount");
                if (!subTotalList.isEmpty())
                    subTotal = subTotalList.get(0);
                if (subTotal == null || !NumberUtils.stringIsBigDecimal(subTotal) || ((new BigDecimal(subTotal).compareTo(BigDecimal.ZERO)) == 0)) {
                    String subTotalAlt = FunctionsXML.getTagValueHTML(taxTotalNodeList, "cbc:LineExtensionAmount");
                    if (subTotalAlt == null)
                        subTotalAlt = FunctionsXML.getTagValueHTML(taxTotalNodeList, "n4:LineExtensionAmount");
                    if (subTotalAlt != null && NumberUtils.stringIsBigDecimal(subTotalAlt) && (new BigDecimal(subTotalAlt).compareTo(BigDecimal.ZERO) > 0))
                        subTotal = subTotalAlt;
                    else
                        subTotal = (new BigDecimal(montoTotal).subtract(new BigDecimal(igv))).toString();
                }
            } else { //Nuevo Formato
                logger.error("VerificarComprobanteRest 1 :: " + isStandar);
                tipoComprobante = "01";
                NodeList generalNodeListTextSpan = doc.getElementsByTagName("text:span");
                for (int i = 0; i < generalNodeListTextSpan.getLength(); i++) {
                    Node item = generalNodeListTextSpan.item(i);
                    //String texto = item.getTextContent();
                    if (i == 0) { //Ruc Proveedor
                        rucProveedor = item.getTextContent();
                    } else if (i == 6) {//Referencia Factura F000-234
                        referenciaFactura = item.getTextContent();
                    } else if (i == 15) { // Ruc Cliente
                        rucCliente = item.getTextContent();
                    } else if (i == 23) {//FechaEmision
                        fechaEmisionString = item.getTextContent();
                    } else if (i == 39) {//Sub Total
                        subTotal = item.getTextContent();
                    } else if (i == 40) { //Igv
                        igv = item.getTextContent();
                    } else if (i == 41) {//Monto Total
                        montoTotal = item.getTextContent();
                    } else if (i == 42) {//Moneda
                        if (item.getTextContent() != null && item.getTextContent().equalsIgnoreCase("S/")) {
                            codigoMoneda = "PEN";
                            logger.error("VerificarComprobanteRest 2 :: " + isStandar);
                        }
                    }


                }
            }
            if (documentoAceptacion.get().getProveedorRuc().equals(rucProveedor)) {

            } else {
                messages = "El número de comprobante no le pertenece "+documentoAceptacion.get().getProveedorRuc();//+"-"+rucProveedor+"-"+rucCliente;
                bean.setType("EL");
                bean.setMensaje(messages);
                return new ResponseEntity<>(bean, HttpStatus.OK);
            }

            String[] refFacturaStrings = referenciaFactura.split("-");

            if (refFacturaStrings.length > 1) {


                serieComprobante = refFacturaStrings[0];
                nroComprobante = refFacturaStrings[1];

                BillConsultPortBidingServiceLocator locator = new BillConsultPortBidingServiceLocator();
                locator.setBillConsultServicePortEndpointAddress("https://ww1.sunat.gob.pe/ol-it-wsconscpegem/billConsultService");//
                BillConsultService port = locator.getBillConsultServicePort();
                Stub stub = ((Stub) port);
                int nroComprobanteInteger = 0;
                try {
                    nroComprobanteInteger = Integer.parseInt(nroComprobante + "");
                } catch (Exception exp) {
                    messages = "El valor en XML no cumple con un formato valido (SERIE-NUMERO)";
                    bean.setType("EL");
                    bean.setMensaje(messages);
                    return new ResponseEntity<>(bean, HttpStatus.OK);
                }
                SOAPHeaderElement wsseSecurity = new SOAPHeaderElement(new PrefixedQName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security", "wsse"));
                MessageElement usernameToken = new MessageElement("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:UsernameToken");
                MessageElement username = new MessageElement("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:Username");
                MessageElement password = new MessageElement("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:Password");

                //rucProveedor buscarlo en la tabla Sociedad y se va a tomar el userName y Passwrod si no existe un throw exception
                //validar si va el rucProveedor o rucCliente
                //Inicio J
                Sociedad sociedad1 = sociedadService.getOneByRucCliente(rucCliente); //rucProveedor

                if (sociedad1 == null) {
                    throw new Exception("la sociedad no existe en iprovider : " + rucCliente); //rucProveedor
                }
                //Fin J
                logger.error("valor "+rucCliente+"-"+sociedad1.getUsuario()+"-"+sociedad1.getClave());
                username.setObjectValue(rucCliente+sociedad1.getUsuario());
                //en caso de reversion descomentar linea de abajo y comentar linea arriba
                //username.setObjectValue(repositoryUser);
                //username.setObjectValue(rucCliente + "JAVIERT1");
                usernameToken.addChild(username);
                password.setObjectValue(sociedad1.getClave());
                //en caso de reversion descomentar linea de abajo y comentar linea arriba
                //password.setObjectValue(rModificacionepositoryPass);
                usernameToken.addChild(password);
                wsseSecurity.addChild(usernameToken);
                stub.setHeader(wsseSecurity);
                logger.error("antes que llame al  servicio "+rucProveedor+"-"+serieComprobante+"-"+nroComprobanteInteger);
                StatusResponse ff = port.getStatus(rucProveedor, tipoComprobante, serieComprobante, nroComprobanteInteger);
                logger.error("despues que llame servicio");
                codess = ff.getStatusCode();
                messages = ff.getStatusMessage();
                logger.error("mensaje respopnse "+messages+"-"+codess);
                if (codess.equalsIgnoreCase("0001") || codess.equalsIgnoreCase("0009") || codess.equalsIgnoreCase("0010")) {
                    bean.setType("S");
                    bean.setMensaje(messages);
                } else {
                    bean.setType("E");
                    bean.setMensaje(messages);
                    return new ResponseEntity<>(bean, HttpStatus.OK);
                }
                logger.error("valor 2 paso response"+isStandar);
                if (isStandar) {//Como siempre estubo funcionando
                    logger.error("VerificarComprobanteRest 3 :: " + isStandar);
                    fechaEmisionString = FunctionsXML.getTagValueHTML(facturaNodeList, "cbc:IssueDate");
                    if (fechaEmisionString == null)
                        fechaEmisionString = FunctionsXML.getTagValueHTML(facturaNodeList, "n4:IssueDate");

                    codigoMoneda = FunctionsXML.getTagValueHTML(facturaNodeList, "cbc:DocumentCurrencyCode");
                    if (codigoMoneda == null)
                        codigoMoneda = FunctionsXML.getTagValueHTML(facturaNodeList, "n4:DocumentCurrencyCode");

                    fechaEmision = DateUtils.stringToUtilDate(fechaEmisionString);
                } else {
                    logger.error("VerificarComprobanteRest 4 :: " + isStandar);
                    if (StringUtils.isNotBlank(fechaEmisionString)) {
                        logger.error("VerificarComprobanteRest 5 :: " + isStandar);
                        // String[] auxDate = fechaEmisionString.split(" ");
                        //DateFormat fmt = new SimpleDateFormat("MMMMM-dd-yyyy");
                        //fechaEmision = fmt.parse(auxDate[2] + "-" + auxDate[0] + "-" + auxDate[4]);
                        String[] auxDate = fechaEmisionString.split(" ");
                        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                        //fechaEmision  = fmt.parse(auxDate[2] + "-" + auxDate[0] + "-" + auxDate[4]);
                        fechaEmision = fmt.parse(auxDate[0] + "/" + getNumberMonth(auxDate[2]) + "/" + auxDate[4]);

                    }
                }
                logger.error("valor antes de moneda "+documentoAceptacion.get().getCodigoMoneda());
                if (!codigoMoneda.equals(documentoAceptacion.get().getCodigoMoneda())) {
                    throw new Exception("La moneda de la factura electrónica (" + codigoMoneda + ") \n" +
                        "no coincide con la moneda del pedido (" + documentoAceptacion.get().getCodigoMoneda() + ")");
                    //throw  new Exception("El código de moneda no coincide XML: "+ codigoMoneda +" | Orden Compra: "+documentoAceptacion.get().getCodigoMoneda());
                }
                logger.error("validar importe");
                //VALIDAR IMPORTE TOTAL
                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
                symbols.setGroupingSeparator(' ');
//                BigDecimal.valueOf(Long.parseLong(importe)) !=
                /*
                if (!importe.equals(String.valueOf(totalOc.setScale(2)))) {
                    totalOc = totalOc.setScale(2);
                    BigDecimal importeXml = new BigDecimal(importe);
                    throw new Exception("El monto del " +
                        "comprobante electrónico (" + formatter.format(importeXml) + ") no coincide con el monto aceptado (" + formatter.format(totalOc) + ")");
                    //throw  new Exception("El monto total del documento ("+formatter.format(totalOc)+") no coindice con el monto total de la ("+formatter.format(importeXml)+") factura XML");
                }

                */
                logger.error("validamos tolerancia");
                // Inicio Mizalo - pagos parciales igual CENTENARIO
                BigDecimal importeMas = totalOc;
                BigDecimal importeMenos = totalOc;
                BigDecimal toleranciaMas = new BigDecimal(0.05);
                BigDecimal toleranciaMenos = new BigDecimal(0.05);
                importeMas = importeMas.multiply(toleranciaMas);
                importeMenos = importeMenos.multiply(toleranciaMenos);

                importeMas = totalOc.add(importeMas);
                importeMenos = totalOc.subtract(importeMenos);

                BigDecimal importeOriginal = new BigDecimal(importe);
                if(importeOriginal.compareTo(importeMas)> 0){
                    BigDecimal importeXml = new BigDecimal(importe);
                    throw  new Exception("El monto del " +
                            "comprobante electrónico ("+formatter.format(importeXml)+") es mayor que el monto aceptado ("+formatter.format(importeMas)+") Tolerancia de +- S/0.50");

                }

                if(importeOriginal.compareTo(importeMenos) < 0){
                    BigDecimal importeXml = new BigDecimal(importe);
                    throw  new Exception("El monto del " +
                            "comprobante electrónico ("+formatter.format(importeXml)+") es menor que el monto aceptado ("+formatter.format(importeMas)+") Tolerancia de +- S/0.50");

                }
                // Fin Mizalo - Habilitado


                logger.error("validamos sociedad");
                Sociedad sociedad = sociedadService.getOneByRucCliente(rucCliente);
                if (sociedad == null) {
                    throw new Exception("la sociedad no existe en iprovider : " + rucCliente);
                }
                logger.error("VerificarComprobanteRest  sociedad  :: " + sociedad.getCodigoSociedad());
                logger.error("VerificarComprobanteRest  rucProveedor  :: " + rucProveedor);
                logger.error("VerificarComprobanteRest  fechaEmision  :: " + fechaEmision);
                logger.error("VerificarComprobanteRest   referenciaFactura :: " + referenciaFactura);
                logger.error("VerificarComprobanteRest  codigoMoneda  :: " + codigoMoneda);
                logger.error("VerificarComprobanteRest  igv  :: " + igv);
                logger.error("VerificarComprobanteRest  subTotal  :: " + subTotal);
                logger.error("VerificarComprobanteRest  montoTotal  :: " + montoTotal);
                prefactura.setSociedad(sociedad != null ? sociedad.getCodigoSociedad() : "");
                prefactura.setProveedorRuc(rucProveedor);
                prefactura.setFechaEmision(fechaEmision);
                prefactura.setReferencia(referenciaFactura);
                prefactura.setCodigoMoneda(codigoMoneda);
                prefactura.setIgv(igv);
                prefactura.setSubTotal(subTotal);
                prefactura.setTotal(montoTotal);
                prefactura.setImporte(importe);
                prefactura.setTexto(texto);
                prefactura.setCadenaNumerosOrdenCompra(cadenaNumerosOc);
                prefactura.setClaseDoc(cadenaNumerosOc);
                // prefactura.setObservaciones(    );

                bean.setPrefactura(prefactura);
            } else {
                bean.setType("EX");
                bean.setMensaje("La referencia de factura: '" + referenciaFactura + "' encontrada en el archivo XML no es valida");
            }

            if (sunatXml.exists()) {
                sunatXml.delete();
            }

        } catch (IOException e) {
            messages = e.toString();
            bean.setType("EX");
            if (messages.equalsIgnoreCase("El Usuario ingresado no existe")) { // si el RUC del receptor no es uno valido, el usuario de autenticacion generado usando dicho RUC no es correcto
                messages = "El receptor no es el correcto ("+rucCliente+")";
            }
            bean.setMensaje(messages);
            logger.error("mensaje devuelto "+messages+"-"+e.getCause());
            System.out.println(e.getMessage()+"-"+messages);
//            System.out.println(e.toString());
        } catch (Exception e) {
            // TODO: handle exception
            messages = e.getMessage();
            bean.setType("EL");
            bean.setMensaje(messages);
            System.out.println(e.getMessage()+"-"+messages);
//            System.out.println(e.toString());
        }

        return new ResponseEntity<>(bean, HttpStatus.OK);
    }

    @PostMapping(value = "/LeerComprobante", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<PrefacturaDto> LeerComprobante(@RequestParam(value = "file") MultipartFile archivoSunat) {
        PrefacturaDto prefactura = new PrefacturaDto();
        //@add ppo 18.01.2021
        String rucProveedor = "";
        String rucCliente = "";
        String tipoComprobante = "";
        String montoTotal = "";
        String igv = "";
        String subTotal = "";
        String referenciaFactura = "";
        String fechaEmisionString = "";
        String codigoMoneda = "";
        Date fechaEmision = null;
        NodeList facturaNodeList = null;


        //Fin
        try {
            File sunatXml = FunctionsXML.convert(archivoSunat);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(sunatXml);
            //@add ppo 18.01.2020
            boolean isStandar = true;
            NodeList facturaNodeListAux = doc.getElementsByTagName("Invoice");
            if (facturaNodeListAux.getLength() == 0) {
                facturaNodeListAux = doc.getElementsByTagName("tns:Invoice");
            }

            if (facturaNodeListAux.getLength() == 0) {
                isStandar = false;
            }

            //Fin
            if (isStandar) {//Forma normal
                logger.error("LeerComprobante 0 :: " + isStandar);
                XPath xPath = XPathFactory.newInstance().newXPath();
                referenciaFactura = xPath.compile("/Invoice/ID").evaluate(doc);
                String montoImpuestos = xPath.compile("/Invoice/TaxTotal/TaxAmount").evaluate(doc);
                String rucProveedorAlt = xPath.compile("/Invoice/AccountingSupplierParty/Party/PartyIdentification/ID").evaluate(doc);
                String rucClienteAlt = xPath.compile("/Invoice/AccountingCustomerParty/Party/PartyIdentification/ID").evaluate(doc);

                doc.getDocumentElement().normalize();

                NodeList proveedorNodeList = doc.getElementsByTagName("cac:AccountingSupplierParty");
                if (proveedorNodeList.getLength() == 0)
                    proveedorNodeList = doc.getElementsByTagName("n5:AccountingSupplierParty");

                NodeList clienteNodeList = doc.getElementsByTagName("cac:AccountingCustomerParty");
                if (clienteNodeList.getLength() == 0)
                    clienteNodeList = doc.getElementsByTagName("n5:AccountingCustomerParty");

                facturaNodeList = doc.getElementsByTagName("Invoice");
                if (facturaNodeList.getLength() == 0)
                    facturaNodeList = doc.getElementsByTagName("tns:Invoice");

                NodeList taxSubTotalNodeList = doc.getElementsByTagName("cac:TaxTotal");
                if (taxSubTotalNodeList.getLength() == 0)
                    taxSubTotalNodeList = doc.getElementsByTagName("n5:TaxTotal");

                NodeList taxTotalNodeList = doc.getElementsByTagName("cac:LegalMonetaryTotal");
                if (taxTotalNodeList.getLength() == 0)
                    taxTotalNodeList = doc.getElementsByTagName("n5:LegalMonetaryTotal");

                rucProveedor = FunctionsXML.getTagValueHTML(proveedorNodeList, "cbc:ID");
                if (rucProveedor == null)
                    rucProveedor = FunctionsXML.getTagValueHTML(proveedorNodeList, "n4:ID");

                rucCliente = FunctionsXML.getTagValueHTML(clienteNodeList, "cbc:ID");
                if (rucCliente == null)
                    rucCliente = FunctionsXML.getTagValueHTML(clienteNodeList, "n4:ID");

                if (rucProveedor == null || (!NumberUtils.stringIsLong(rucProveedor) && rucProveedor.length() != 11))
                    rucProveedor = rucProveedorAlt;

                if (rucCliente == null || (!NumberUtils.stringIsLong(rucCliente) && rucCliente.length() != 11))
                    rucCliente = rucClienteAlt;

                tipoComprobante = FunctionsXML.getTagValueHTML(facturaNodeList, "cbc:InvoiceTypeCode");
                if (tipoComprobante == null)
                    tipoComprobante = FunctionsXML.getTagValueHTML(facturaNodeList, "n4:InvoiceTypeCode");

                montoTotal = FunctionsXML.getTagValueHTML(taxTotalNodeList, "cbc:PayableAmount");
                if (montoTotal == null)
                    montoTotal = FunctionsXML.getTagValueHTML(taxTotalNodeList, "n4:PayableAmount");

                igv = "";
                List<String> igvList = FunctionsXML.getTagValueIntoTagHTML(taxSubTotalNodeList, "cbc:Name", "IGV", "cbc:TaxAmount");
                if (igvList.isEmpty())
                    igvList = FunctionsXML.getTagValueIntoTagHTML(taxSubTotalNodeList, "n4:Name", "IGV", "n4:TaxAmount");
                if (!igvList.isEmpty())
                    igv = igvList.get(0);
                if (igv == null || !NumberUtils.stringIsBigDecimal(igv))
                    igv = montoImpuestos;

                subTotal = "";
                List<String> subTotalList = FunctionsXML.getTagValueIntoTagHTML(taxSubTotalNodeList, "cbc:Name", "IGV", "cbc:TaxableAmount");
                if (subTotalList.isEmpty())
                    subTotalList = FunctionsXML.getTagValueIntoTagHTML(taxSubTotalNodeList, "n4:Name", "IGV", "n4:TaxableAmount");
                if (!subTotalList.isEmpty())
                    subTotal = subTotalList.get(0);
                if (subTotal == null || !NumberUtils.stringIsBigDecimal(subTotal) || ((new BigDecimal(subTotal).compareTo(BigDecimal.ZERO)) == 0)) {
                    String subTotalAlt = FunctionsXML.getTagValueHTML(taxTotalNodeList, "cbc:LineExtensionAmount");
                    if (subTotalAlt == null)
                        subTotalAlt = FunctionsXML.getTagValueHTML(taxTotalNodeList, "n4:LineExtensionAmount");
                    if (subTotalAlt != null && NumberUtils.stringIsBigDecimal(subTotalAlt) && (new BigDecimal(subTotalAlt).compareTo(BigDecimal.ZERO) > 0))
                        subTotal = subTotalAlt;
                    else
                        subTotal = (new BigDecimal(montoTotal).subtract(new BigDecimal(igv))).toString();
                }

                fechaEmisionString = FunctionsXML.getTagValueHTML(facturaNodeList, "cbc:IssueDate");
                if (fechaEmisionString == null)
                    fechaEmisionString = FunctionsXML.getTagValueHTML(facturaNodeList, "n4:IssueDate");

                codigoMoneda = FunctionsXML.getTagValueHTML(facturaNodeList, "cbc:DocumentCurrencyCode");
                if (codigoMoneda == null)
                    codigoMoneda = FunctionsXML.getTagValueHTML(facturaNodeList, "n4:DocumentCurrencyCode");

                fechaEmision = DateUtils.stringToUtilDate(fechaEmisionString);
            } else {
                logger.error("LeerComprobante 1 :: " + isStandar);
                tipoComprobante = "01";
                NodeList generalNodeListTextSpan = doc.getElementsByTagName("text:span");
                for (int i = 0; i < generalNodeListTextSpan.getLength(); i++) {
                    Node item = generalNodeListTextSpan.item(i);
                    //String texto = item.getTextContent();
                    if (i == 0) { //Ruc Proveedor
                        rucProveedor = item.getTextContent();
                    } else if (i == 6) {//Referencia Factura F000-234
                        referenciaFactura = item.getTextContent();
                    } else if (i == 15) { // Ruc Cliente
                        rucCliente = item.getTextContent();
                    } else if (i == 23) {//FechaEmision
                        fechaEmisionString = item.getTextContent();
                    } else if (i == 39) {//Sub Total
                        subTotal = item.getTextContent();
                    } else if (i == 40) { //Igv
                        igv = item.getTextContent();
                    } else if (i == 41) {//Monto Total
                        montoTotal = item.getTextContent();
                    } else if (i == 42) {//Moneda
                        if (item.getTextContent() != null && item.getTextContent().equalsIgnoreCase("S/")) {
                            codigoMoneda = "PEN";
                        }
                    }


                }
                if (StringUtils.isNotBlank(fechaEmisionString)) {
                    logger.error("LeerComprobante 2 :: " + isStandar);
                    //String[] auxDate = fechaEmisionString.split(" ");
                    //DateFormat fmt = new SimpleDateFormat("MMMMM-dd-yyyy");
                    //fechaEmision = fmt.parse(auxDate[2] + "-" + auxDate[0] + "-" + auxDate[4]);
                    String[] auxDate = fechaEmisionString.split(" ");
                    DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                    //fechaEmision  = fmt.parse(auxDate[2] + "-" + auxDate[0] + "-" + auxDate[4]);
                    fechaEmision = fmt.parse(auxDate[0] + "/" + getNumberMonth(auxDate[2]) + "/" + auxDate[4]);

                }
            }

            Sociedad sociedad = sociedadService.getOneByRucCliente(rucCliente);
            logger.error("LeerComprobante  sociedad  :: " + sociedad.getCodigoSociedad());
            logger.error("LeerComprobante  rucProveedor  :: " + rucProveedor);
            logger.error("LeerComprobante  fechaEmision  :: " + fechaEmision);
            logger.error("LeerComprobante   referenciaFactura :: " + referenciaFactura);
            logger.error("LeerComprobante  codigoMoneda  :: " + codigoMoneda);
            logger.error("LeerComprobante  igv  :: " + igv);
            logger.error("LeerComprobante  subTotal  :: " + subTotal);
            logger.error("LeerComprobante  montoTotal  :: " + montoTotal);
            prefactura.setSociedad(sociedad != null ? sociedad.getCodigoSociedad() : "");
            prefactura.setProveedorRuc(rucProveedor);
            prefactura.setFechaEmision(fechaEmision);
            prefactura.setReferencia(referenciaFactura);
            prefactura.setCodigoMoneda(codigoMoneda);
            prefactura.setIgv(igv);
            prefactura.setSubTotal(subTotal);
            prefactura.setTotal(montoTotal);
            prefactura.setObservaciones("RUC Cliente: " + rucCliente + " // Tipo Comprobante: " + tipoComprobante);

            if (sunatXml.exists()) {
                sunatXml.delete();
            }

            return new ResponseEntity<>(prefactura, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    public String getNumberMonth(String nameMonth) {
        String[] monthStringEn = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String[] monthStringEs = new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        int numMes = 0;
        for (int i = 0; i < monthStringEs.length; i++) {
            if (monthStringEs[i].equalsIgnoreCase(nameMonth)) {
                numMes = i + 1;
                break;
            }
            if (monthStringEn[i].equalsIgnoreCase(nameMonth)) {
                numMes = i + 1;
                break;
            }
        }
        return String.format("%02d", numMes);
    }
    //Backup
    /*
    @PostMapping(value = "/ValidarComprobante",produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<MensajePrefactura> VerificarComprobanteRest(@RequestParam(value = "file") MultipartFile archivoSunat){
        MensajePrefactura bean = new MensajePrefactura();
        String codess;
        String messages;
        String serieComprobante = "";
        String nroComprobante = "";
        PrefacturaDto prefactura = new PrefacturaDto();

        try {
            File sunatXml = FunctionsXML.convert(archivoSunat);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(sunatXml);

            XPath xPath = XPathFactory.newInstance().newXPath();
            String referenciaFactura = xPath.compile("/Invoice/ID").evaluate(doc);
            String montoImpuestos = xPath.compile("/Invoice/TaxTotal/TaxAmount").evaluate(doc);
            String rucProveedorAlt = xPath.compile("/Invoice/AccountingSupplierParty/Party/PartyIdentification/ID").evaluate(doc);
            String rucClienteAlt = xPath.compile("/Invoice/AccountingCustomerParty/Party/PartyIdentification/ID").evaluate(doc);

            doc.getDocumentElement().normalize();

            NodeList proveedorNodeList = doc.getElementsByTagName("cac:AccountingSupplierParty");
            if(proveedorNodeList.getLength() == 0)
                proveedorNodeList = doc.getElementsByTagName("n5:AccountingSupplierParty");

            NodeList clienteNodeList = doc.getElementsByTagName("cac:AccountingCustomerParty");
            if(clienteNodeList.getLength() == 0)
                clienteNodeList = doc.getElementsByTagName("n5:AccountingCustomerParty");

            NodeList facturaNodeList = doc.getElementsByTagName("Invoice");
            if(facturaNodeList.getLength() == 0)
                facturaNodeList = doc.getElementsByTagName("tns:Invoice");

            NodeList taxSubTotalNodeList = doc.getElementsByTagName("cac:TaxTotal");
            if(taxSubTotalNodeList.getLength() == 0)
                taxSubTotalNodeList = doc.getElementsByTagName("n5:TaxTotal");

            NodeList taxTotalNodeList = doc.getElementsByTagName("cac:LegalMonetaryTotal");
            if(taxTotalNodeList.getLength() == 0)
                taxTotalNodeList = doc.getElementsByTagName("n5:LegalMonetaryTotal");

            String rucProveedor = FunctionsXML.getTagValueHTML(proveedorNodeList, "cbc:ID");
            if(rucProveedor == null)
                rucProveedor = FunctionsXML.getTagValueHTML(proveedorNodeList, "n4:ID");

            String rucCliente = FunctionsXML.getTagValueHTML(clienteNodeList, "cbc:ID");
            if(rucCliente == null)
                rucCliente = FunctionsXML.getTagValueHTML(clienteNodeList, "n4:ID");

            if(rucProveedor == null || (!NumberUtils.stringIsLong(rucProveedor) && rucProveedor.length() != 11))
                rucProveedor = rucProveedorAlt;

            if(rucCliente == null || (!NumberUtils.stringIsLong(rucCliente) && rucCliente.length() != 11))
                rucCliente = rucClienteAlt;

            String tipoComprobante = FunctionsXML.getTagValueHTML(facturaNodeList, "cbc:InvoiceTypeCode");
            if(tipoComprobante == null)
                tipoComprobante = FunctionsXML.getTagValueHTML(facturaNodeList, "n4:InvoiceTypeCode");

            String montoTotal = FunctionsXML.getTagValueHTML(taxTotalNodeList, "cbc:PayableAmount");
            if(montoTotal == null)
                montoTotal = FunctionsXML.getTagValueHTML(taxTotalNodeList, "n4:PayableAmount");

            String igv = "";
            List<String> igvList = FunctionsXML.getTagValueIntoTagHTML(taxSubTotalNodeList, "cbc:Name", "IGV", "cbc:TaxAmount");
            if(igvList.isEmpty())
                igvList = FunctionsXML.getTagValueIntoTagHTML(taxSubTotalNodeList, "n4:Name", "IGV", "n4:TaxAmount");
            if(!igvList.isEmpty())
                igv = igvList.get(0);
            if(igv == null || !NumberUtils.stringIsBigDecimal(igv))
                igv = montoImpuestos;

            String subTotal = "";
            List<String> subTotalList = FunctionsXML.getTagValueIntoTagHTML(taxSubTotalNodeList, "cbc:Name", "IGV", "cbc:TaxableAmount" );
            if(subTotalList.isEmpty())
                subTotalList = FunctionsXML.getTagValueIntoTagHTML(taxSubTotalNodeList, "n4:Name", "IGV", "n4:TaxableAmount" );
            if(!subTotalList.isEmpty())
                subTotal = subTotalList.get(0);
            if(subTotal == null || !NumberUtils.stringIsBigDecimal(subTotal) || ((new BigDecimal(subTotal).compareTo(BigDecimal.ZERO)) == 0)){
                String subTotalAlt = FunctionsXML.getTagValueHTML(taxTotalNodeList, "cbc:LineExtensionAmount");
                if(subTotalAlt == null)
                    subTotalAlt = FunctionsXML.getTagValueHTML(taxTotalNodeList, "n4:LineExtensionAmount");
                if (subTotalAlt != null && NumberUtils.stringIsBigDecimal(subTotalAlt) && (new BigDecimal(subTotalAlt).compareTo(BigDecimal.ZERO) > 0))
                    subTotal = subTotalAlt;
                else
                    subTotal =(new BigDecimal(montoTotal).subtract(new BigDecimal(igv))).toString();
            }

            String[] refFacturaStrings = referenciaFactura.split("-");

            if (refFacturaStrings.length > 1) {
                serieComprobante = refFacturaStrings[0];
                nroComprobante = refFacturaStrings[1];

                BillConsultPortBidingServiceLocator locator = new BillConsultPortBidingServiceLocator();
                locator.setBillConsultServicePortEndpointAddress("https://ww1.sunat.gob.pe/ol-it-wsconscpegem/billConsultService");//
                BillConsultService port = locator.getBillConsultServicePort();
                Stub stub = ((Stub) port);
                int nroComprobanteInteger = Integer.parseInt(nroComprobante + "");
                SOAPHeaderElement wsseSecurity = new SOAPHeaderElement(new PrefixedQName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security", "wsse"));
                MessageElement usernameToken = new MessageElement("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:UsernameToken");
                MessageElement username = new MessageElement("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:Username");
                MessageElement password = new MessageElement("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:Password");

                username.setObjectValue(rucCliente.concat(repositoryUser));
                usernameToken.addChild(username);
                password.setObjectValue(repositoryPass);
                usernameToken.addChild(password);
                wsseSecurity.addChild(usernameToken);
                stub.setHeader(wsseSecurity);
                StatusResponse ff = port.getStatus(rucProveedor, tipoComprobante, serieComprobante, nroComprobanteInteger);

                codess = ff.getStatusCode();
                messages = ff.getStatusMessage();
                if (codess.equalsIgnoreCase("0001") || codess.equalsIgnoreCase("0009") || codess.equalsIgnoreCase("0010")) {
                    bean.setType("S");
                    bean.setMensaje(messages);
                } else {
                    bean.setType("E");
                    bean.setMensaje(messages);
                }

                String fechaEmisionString = FunctionsXML.getTagValueHTML(facturaNodeList, "cbc:IssueDate");
                if(fechaEmisionString == null)
                    fechaEmisionString = FunctionsXML.getTagValueHTML(facturaNodeList, "n4:IssueDate");

                String codigoMoneda = FunctionsXML.getTagValueHTML(facturaNodeList, "cbc:DocumentCurrencyCode");
                if(codigoMoneda == null)
                    codigoMoneda = FunctionsXML.getTagValueHTML(facturaNodeList, "n4:DocumentCurrencyCode");

                Date fechaEmision = DateUtils.stringToUtilDate(fechaEmisionString);
                Sociedad sociedad = sociedadService.getOneByRucCliente(rucCliente);

                prefactura.setSociedad(sociedad != null ? sociedad.getCodigoSociedad() : "");
                prefactura.setProveedorRuc(rucProveedor);
                prefactura.setFechaEmision(fechaEmision);
                prefactura.setReferencia(referenciaFactura);
                prefactura.setCodigoMoneda(codigoMoneda);
                prefactura.setIgv(igv);
                prefactura.setSubTotal(subTotal);
                prefactura.setTotal(montoTotal);
                // prefactura.setObservaciones(    );

                bean.setPrefactura(prefactura);
            }
            else{
                bean.setType("EX");
                bean.setMensaje("La referencia de factura: '" + referenciaFactura + "' encontrada en el archivo XML no es valida");
            }

            if (sunatXml.exists()) {
                sunatXml.delete();
            }

        } catch (IOException e) {
            messages = e.toString();
            bean.setType("EX");
            if(messages.equalsIgnoreCase("El Usuario ingresado no existe")) { // si el RUC del receptor no es uno valido, el usuario de autenticacion generado usando dicho RUC no es correcto
                messages = "El receptor no es el correcto";
            }
            bean.setMensaje(messages);
//            System.out.println(e.toString());
        }catch (Exception e) {
            // TODO: handle exception
            messages = e.toString();
            bean.setType("EL");
            bean.setMensaje(messages);
//            System.out.println(e.toString());
        }

        return new ResponseEntity<>(bean, HttpStatus.OK);
    }

    @PostMapping(value = "/LeerComprobante",produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<PrefacturaDto> LeerComprobante(@RequestParam(value = "file") MultipartFile archivoSunat){
        PrefacturaDto prefactura = new PrefacturaDto();

        try {
            File sunatXml = FunctionsXML.convert(archivoSunat);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(sunatXml);

            XPath xPath = XPathFactory.newInstance().newXPath();
            String referenciaFactura = xPath.compile("/Invoice/ID").evaluate(doc);
            String montoImpuestos = xPath.compile("/Invoice/TaxTotal/TaxAmount").evaluate(doc);
            String rucProveedorAlt = xPath.compile("/Invoice/AccountingSupplierParty/Party/PartyIdentification/ID").evaluate(doc);
            String rucClienteAlt = xPath.compile("/Invoice/AccountingCustomerParty/Party/PartyIdentification/ID").evaluate(doc);

            doc.getDocumentElement().normalize();

            NodeList proveedorNodeList = doc.getElementsByTagName("cac:AccountingSupplierParty");
            if(proveedorNodeList.getLength() == 0)
                proveedorNodeList = doc.getElementsByTagName("n5:AccountingSupplierParty");

            NodeList clienteNodeList = doc.getElementsByTagName("cac:AccountingCustomerParty");
            if(clienteNodeList.getLength() == 0)
                clienteNodeList = doc.getElementsByTagName("n5:AccountingCustomerParty");

            NodeList facturaNodeList = doc.getElementsByTagName("Invoice");
            if(facturaNodeList.getLength() == 0)
                facturaNodeList = doc.getElementsByTagName("tns:Invoice");

            NodeList taxSubTotalNodeList = doc.getElementsByTagName("cac:TaxTotal");
            if(taxSubTotalNodeList.getLength() == 0)
                taxSubTotalNodeList = doc.getElementsByTagName("n5:TaxTotal");

            NodeList taxTotalNodeList = doc.getElementsByTagName("cac:LegalMonetaryTotal");
            if(taxTotalNodeList.getLength() == 0)
                taxTotalNodeList = doc.getElementsByTagName("n5:LegalMonetaryTotal");

            String rucProveedor = FunctionsXML.getTagValueHTML(proveedorNodeList, "cbc:ID");
            if(rucProveedor == null)
                rucProveedor = FunctionsXML.getTagValueHTML(proveedorNodeList, "n4:ID");

            String rucCliente = FunctionsXML.getTagValueHTML(clienteNodeList, "cbc:ID");
            if(rucCliente == null)
                rucCliente = FunctionsXML.getTagValueHTML(clienteNodeList, "n4:ID");

            if(rucProveedor == null || (!NumberUtils.stringIsLong(rucProveedor) && rucProveedor.length() != 11))
                rucProveedor = rucProveedorAlt;

            if(rucCliente == null || (!NumberUtils.stringIsLong(rucCliente) && rucCliente.length() != 11))
                rucCliente = rucClienteAlt;

            String tipoComprobante = FunctionsXML.getTagValueHTML(facturaNodeList, "cbc:InvoiceTypeCode");
            if(tipoComprobante == null)
                tipoComprobante = FunctionsXML.getTagValueHTML(facturaNodeList, "n4:InvoiceTypeCode");

            String montoTotal = FunctionsXML.getTagValueHTML(taxTotalNodeList, "cbc:PayableAmount");
            if(montoTotal == null)
                montoTotal = FunctionsXML.getTagValueHTML(taxTotalNodeList, "n4:PayableAmount");

            String igv = "";
            List<String> igvList = FunctionsXML.getTagValueIntoTagHTML(taxSubTotalNodeList, "cbc:Name", "IGV", "cbc:TaxAmount");
            if(igvList.isEmpty())
                igvList = FunctionsXML.getTagValueIntoTagHTML(taxSubTotalNodeList, "n4:Name", "IGV", "n4:TaxAmount");
            if(!igvList.isEmpty())
                igv = igvList.get(0);
            if(igv == null || !NumberUtils.stringIsBigDecimal(igv))
                igv = montoImpuestos;

            String subTotal = "";
            List<String> subTotalList = FunctionsXML.getTagValueIntoTagHTML(taxSubTotalNodeList, "cbc:Name", "IGV", "cbc:TaxableAmount" );
            if(subTotalList.isEmpty())
                subTotalList = FunctionsXML.getTagValueIntoTagHTML(taxSubTotalNodeList, "n4:Name", "IGV", "n4:TaxableAmount" );
            if(!subTotalList.isEmpty())
                subTotal = subTotalList.get(0);
            if(subTotal == null || !NumberUtils.stringIsBigDecimal(subTotal) || ((new BigDecimal(subTotal).compareTo(BigDecimal.ZERO)) == 0)){
                String subTotalAlt = FunctionsXML.getTagValueHTML(taxTotalNodeList, "cbc:LineExtensionAmount");
                if(subTotalAlt == null)
                    subTotalAlt = FunctionsXML.getTagValueHTML(taxTotalNodeList, "n4:LineExtensionAmount");
                if (subTotalAlt != null && NumberUtils.stringIsBigDecimal(subTotalAlt) && (new BigDecimal(subTotalAlt).compareTo(BigDecimal.ZERO) > 0))
                    subTotal = subTotalAlt;
                else
                    subTotal =(new BigDecimal(montoTotal).subtract(new BigDecimal(igv))).toString();
            }

            String fechaEmisionString = FunctionsXML.getTagValueHTML(facturaNodeList, "cbc:IssueDate");
            if(fechaEmisionString == null)
                fechaEmisionString = FunctionsXML.getTagValueHTML(facturaNodeList, "n4:IssueDate");

            String codigoMoneda = FunctionsXML.getTagValueHTML(facturaNodeList, "cbc:DocumentCurrencyCode");
            if(codigoMoneda == null)
                codigoMoneda = FunctionsXML.getTagValueHTML(facturaNodeList, "n4:DocumentCurrencyCode");

            Date fechaEmision = DateUtils.stringToUtilDate (fechaEmisionString);
            Sociedad sociedad = sociedadService.getOneByRucCliente(rucCliente);

            prefactura.setSociedad(sociedad != null ? sociedad.getCodigoSociedad() : "");
            prefactura.setProveedorRuc(rucProveedor);
            prefactura.setFechaEmision(fechaEmision);
            prefactura.setReferencia(referenciaFactura);
            prefactura.setCodigoMoneda(codigoMoneda);
            prefactura.setIgv(igv);
            prefactura.setSubTotal(subTotal);
            prefactura.setTotal(montoTotal);
            prefactura.setObservaciones("RUC Cliente: " + rucCliente + " // Tipo Comprobante: " + tipoComprobante);

            if (sunatXml.exists()) {
                sunatXml.delete();
            }

            return new ResponseEntity<>(prefactura, HttpStatus.OK);
        }
        catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }
     */
}
