package com.incloud.hcp.jco.ubigeo.service.impl;


import com.incloud.hcp.domain.Ubigeo;
import com.incloud.hcp.jco.ubigeo.dto.UbigeoRFCDTO;
import com.incloud.hcp.jco.ubigeo.dto.UbigeoRFCResponseDto;
import com.incloud.hcp.jco.ubigeo.service.JCOUbigeoService;
import com.incloud.hcp.repository.UbigeoRepository;
import com.incloud.hcp.sap.SapLog;
import com.incloud.hcp.util.Utils;
//import com.sun.jndi.toolkit.url.Uri;
//import io.vavr.control.Try;
import io.vavr.control.Try;
import org.apache.commons.lang.StringUtils;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpHost;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.util.EntityUtils;
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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
//CF
import com.sap.cloud.sdk.cloudplatform.connectivity.*;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class JCOUbigeoServiceImpl implements JCOUbigeoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int NRO_EJECUCIONES_RFC = 10;

//    private final String FUNCION_RFC = "ZMMRFC_CONSULTA_UBIGEO";
    private final String FUNCION_RFC = "ZPE_MM_CONSULTA_UBIGEO";
    private final String NOMBRE_TABLA_RFC_PAISES = "T_T005";
    private final String NOMBRE_TABLA_RFC_REGION = "T_T005S";
    private final String NOMBRE_TABLA_RFC_POBLACION = "T_ADRCITY";
    private final String NOMBRE_TABLA_RFC_DISTRITO = "T_ADRCITYPRT";
    private final Integer NIVEL_PAIS = 0;
    private final Integer NIVEL_REGION = 1;
    private final Integer NIVEL_PROVINCIA = 2;
    private final Integer NIVEL_DISTRITO = 3;



    @Value("${destination.rfc.profit}")
    private String destinationProfit;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UbigeoRepository ubigeoRepository;


    @Override
    public UbigeoRFCResponseDto listarandActualizarUbigeoRFC() throws Exception {
        UbigeoRFCResponseDto ubigeoRFCResponseDto = new UbigeoRFCResponseDto();

        /* Ejecucion invocacion a RFC
        JCoDestination destination = JCoDestinationManager.getDestination(destinationProfit);
        JCoRepository repo = destination.getRepository();
        logger.error("01A - getUBIGEORFC");
        JCoFunction jCoFunction = repo.getFunction(FUNCION_RFC);
        logger.error("01tB - getUBIGEORFC");


        logger.error("01C - getUBIGEORFC");
        for(int contador=0; contador < NRO_EJECUCIONES_RFC; contador++) {
            try {
                jCoFunction.execute(destination);
                break;
            } catch (Exception e) {
                if (contador == NRO_EJECUCIONES_RFC - 1 ) {
                    logger.error("01Ca - getUBIGEORFC - INI RFC ERROR: "+ e.toString());
                    throw new Exception(e);
                }
            }
        }
        */
        /* Obteniendo los valores obtenidos del RFC
        logger.error("02 - getUBIGEORFC - FIN RFC");
        JCoParameterList tableParameterList = jCoFunction.getTableParameterList();
        JCoParameterList result = jCoFunction.getExportParameterList();
        */
        Integer max = 0;
        Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);
        HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));
        //Uri x = new Uri("http://connectivityproxy.internal.cf.us10.hana.ondemand.com:20003");
        /*Uri x = new Uri(destination2.get().asHttp().getProxyConfiguration().get().getUri().getPath());
        logger.info(x.getHost());
        logger.info(String.valueOf(x.getPort()));
         */

        logger.error("Obtenemos ");
        String urlbase = String.valueOf(destination2.get().asHttp().getUri());
        String url = urlbase + "/sap/bc/srt/rfc/sap/zws_consulta_ubigeo/100/zws_consulta_ubigeo/zws_consulta_ubigeo";
        String tramaXML = armarTrama();

        StringEntity strEntity = new StringEntity(tramaXML, "text/xml", "UTF-8");
        HttpPost post = new HttpPost(url);
        post.setHeader("SOAPAction", "GetData");
        post.setEntity(strEntity);

        logger.error("Obtenemos 00");
        HttpResponse response4 = client.execute(post);
        HttpEntity respEntity = response4.getEntity();
        String result = EntityUtils.toString(respEntity);

        logger.error("Obtenemos 001");
        DocumentBuilderFactory domFactory = DocumentBuilderFactory
                .newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder
                .parse(new InputSource(new StringReader(result)));

        logger.error("Obtenemos 1");

        SapLog sapLog = new SapLog();
        String codigoSap = doc.getElementsByTagName("PO_CODE").item(0).getChildNodes().item(0).getNodeValue();
        String message = doc.getElementsByTagName("PO_MSJE").item(0).getChildNodes().item(0).getNodeValue();
        logger.error("Obtenemos 2");
        sapLog.setCode(codigoSap);
        sapLog.setMesaj(message);
        ubigeoRFCResponseDto.setSapLog(sapLog);
        logger.error("02b - getUBIGEORFC - sapLog: " + sapLog.toString());


        /* Recorriendo valores obtenidos del RFC */
        ///Paises RFC
        List<UbigeoRFCDTO> listaPaises=new ArrayList<>();
        //JCoTable table = tableParameterList.getTable(NOMBRE_TABLA_RFC_PAISES);
        NodeList paises = doc.getElementsByTagName(NOMBRE_TABLA_RFC_PAISES).item(0).getChildNodes();

        int contador = 0;
        int contadorTotal = 0;

        for(int i = 0; i < paises.getLength(); i++) {
            Node pais = paises.item(i);
            Element elemento = (Element) pais;
            UbigeoRFCDTO ubigeoRFCDTO=new UbigeoRFCDTO();
            ubigeoRFCDTO.setClavePais(Utils.getValueNodo(elemento, "LAND1"));
            ubigeoRFCDTO.setDenominacion(Utils.getValueNodo(elemento,"LANDX"));

            try {
                Ubigeo ubigeoEncontrado = this.ubigeoRepository.findByCodigoUbigeoSapAndNivel(
                        ubigeoRFCDTO.getClavePais(),
                        NIVEL_PAIS);
                if (Optional.ofNullable(ubigeoEncontrado).isPresent()) {
                    contador++;
                    ubigeoEncontrado.setCodigoUbigeoSapErp(ubigeoRFCDTO.getClavePais());
                    ubigeoEncontrado.setDescripcion(ubigeoRFCDTO.getDenominacion());
                    this.ubigeoRepository.save(ubigeoEncontrado);
                } else {

                    max ++;
                    Ubigeo ubigeonuevo = new Ubigeo();
                    ubigeonuevo.setIdUbigeo(max);
                    ubigeonuevo.setCodigoUbigeoSap(ubigeoRFCDTO.getClavePais());
                    ubigeonuevo.setCodigoUbigeoSapErp(ubigeoRFCDTO.getClavePais());
                    ubigeonuevo.setDescripcion(ubigeoRFCDTO.getDenominacion());
                    ubigeonuevo.setIdPadre(null);
                    ubigeonuevo.setNivel(NIVEL_PAIS);
                    this.ubigeoRepository.save(ubigeonuevo);

                    logger.error("UBIGEO ERR PAIS : " + ubigeonuevo.toString());
                }
            } catch (Exception ex) {
                logger.error("UBIGEO ERR PAIS EXCEPTION: " + ex.getMessage());
                logger.error("UBIGEO ERR PAIS EXCEPTION: " + ubigeoRFCDTO.toString());
            }
            contadorTotal++;
            listaPaises.add(ubigeoRFCDTO);
        }

        logger.error("Obtenemos 04");
        ubigeoRFCResponseDto.setContadorActualizadoPais(contador);
        ubigeoRFCResponseDto.setContadorTotalPais(contadorTotal);


        contador = 0;
        contadorTotal = 0;
        List<UbigeoRFCDTO> listaRegion=new ArrayList<>();
        logger.error("Obtenemos 045");
        //JCoTable tableRegion = tableParameterList.getTable(NOMBRE_TABLA_RFC_REGION);
        NodeList regiones = doc.getElementsByTagName(NOMBRE_TABLA_RFC_REGION).item(0).getChildNodes();
        logger.error("Obtenemos 05");
        for(int i = 0; i < regiones.getLength(); i++) {

            logger.error("Obtenemos regiones" + String.valueOf(i));
            Node region = regiones.item(i);
            Element elemento = (Element) region;
            UbigeoRFCDTO ubigeoRFCDTO = new UbigeoRFCDTO();

            logger.error("Obtenemos 055");
            ubigeoRFCDTO.setClavePais(Utils.getValueNodo(elemento, "LAND1"));
            ubigeoRFCDTO.setDenominacion(Utils.getValueNodo(elemento, "BEZEI"));
            ubigeoRFCDTO.setClaveRegion(Utils.getValueNodo(elemento, "BLAND"));
            ubigeoRFCDTO.setClavePoblacion(Utils.getValueNodo(elemento, "FPRCD"));
            ubigeoRFCDTO.setClaveEquivalenciaSunat(Utils.getValueNodo(elemento, "BLAND_S"));

            logger.error("Obtenemos 056");
            Ubigeo ubigeoEncontrado = new Ubigeo();
            if (!Utils.getValueNodo(elemento, "BLAND_S").equals("")) {

                logger.error("Obtenemos 057");
                logger.error("PARAM : " + Utils.getValueNodo(elemento, "BLAND_S") + " - " + NIVEL_REGION);

                ubigeoEncontrado = this.ubigeoRepository.findByCodigoUbigeoSapAndNivel(
                        ubigeoRFCDTO.getClaveEquivalenciaSunat(),
                        NIVEL_REGION
                );

                logger.error("Obtenemos 058");
            }

            logger.error("UBIGEO ENCONTRADO : " + ubigeoEncontrado.toString());

            if (ubigeoEncontrado.getIdUbigeo() != null) {

                contador++;
                ubigeoEncontrado.setCodigoUbigeoSapErp(ubigeoRFCDTO.getClaveRegion());
                ubigeoEncontrado.setDescripcion(ubigeoRFCDTO.getDenominacion());
                this.ubigeoRepository.save(ubigeoEncontrado);

            } else {

                try
                {



                logger.error("PADRE REGION : " + ubigeoRFCDTO.getClavePais());
                /*Obtener Padre*/

                logger.error("Obtenemos 068");
                Ubigeo ubigeopadre = this.ubigeoRepository.findByCodigoUbigeoSapAndNivel(
                        ubigeoRFCDTO.getClavePais(),
                        NIVEL_PAIS);

                logger.error("Obtenemos 069");

                logger.error("PADRE REGION : " + ubigeopadre.toString());

                max ++;
                Ubigeo ubigeonuevo = new Ubigeo();
                ubigeonuevo.setIdUbigeo(max);
                ubigeonuevo.setCodigoUbigeoSap(ubigeoRFCDTO.getClaveRegion());
                ubigeonuevo.setCodigoUbigeoSapErp(ubigeoRFCDTO.getClaveRegion());
                ubigeonuevo.setDescripcion(ubigeoRFCDTO.getDenominacion());
                ubigeonuevo.setIdPadre(ubigeopadre.getIdUbigeo());
                ubigeonuevo.setNivel(NIVEL_REGION);

                logger.error("Obtenemos 0699");

                logger.error("UBIGEO - REGION : " + ubigeonuevo.toString());

                this.ubigeoRepository.save(ubigeonuevo);

                logger.error("UBIGEO ERR REGION : " + ubigeoRFCDTO.toString());
                }
                catch (Exception ex)
                {
                    logger.error("UBIGEO ERR : " + ex.getMessage());
                }
            }
            contadorTotal++;
            listaRegion.add(ubigeoRFCDTO);
        }
        logger.error("Obtenemos 06");
        ubigeoRFCResponseDto.setContadorActualizadoRegion(contador);
        ubigeoRFCResponseDto.setContadorTotalRegion(contadorTotal);


        contador = 0;
        contadorTotal = 0;
        List<UbigeoRFCDTO> listaPoblacion=new ArrayList<>();
        //JCoTable tablePoblacion = tableParameterList.getTable(NOMBRE_TABLA_RFC_POBLACION);
        NodeList poblaciones = doc.getElementsByTagName(NOMBRE_TABLA_RFC_POBLACION).item(0).getChildNodes();
        logger.error("Poblaciones " + poblaciones.toString());

        for(int i = 0; i < poblaciones.getLength(); i++)
        {
            Node poblacion = poblaciones.item(i);
            Element elemento = (Element) poblacion;
            UbigeoRFCDTO ubigeoRFCDTO = new UbigeoRFCDTO();
            ubigeoRFCDTO.setClavePais(Utils.getValueNodo(elemento, "COUNTRY"));
            ubigeoRFCDTO.setDenominacion(Utils.getValueNodo(elemento, "CITY_NAME"));
            ubigeoRFCDTO.setClaveRegion(Utils.getValueNodo(elemento, "REGION"));
            ubigeoRFCDTO.setClavePoblacion(Utils.getValueNodo(elemento, "CITY_CODE"));
            ubigeoRFCDTO.setClaveEquivalenciaSunat(Utils.getValueNodo(elemento, "CITY_CODE_S"));
            logger.error("UBIGEO PROVINCIA 01: " + ubigeoRFCDTO.toString());

            try {

                Ubigeo ubigeoEncontrado = this.ubigeoRepository.findByCodigoUbigeoSapAndNivel(
                        ubigeoRFCDTO.getClavePoblacion(),
                        NIVEL_PROVINCIA
                );


                if (ubigeoEncontrado.getIdUbigeo() != null) {

                    contador++;
                    ubigeoEncontrado.setCodigoUbigeoSapErp(ubigeoRFCDTO.getClavePoblacion());
                    ubigeoEncontrado.setDescripcion(ubigeoRFCDTO.getDenominacion());
                    this.ubigeoRepository.save(ubigeoEncontrado);

                } else {

                    logger.error("PADRE prov : " + ubigeoRFCDTO.getClaveRegion());
                    /*Obtener Padre*/
                    Ubigeo ubigeopadre = this.ubigeoRepository.findByCodigoUbigeoSapAndNivel(
                            ubigeoRFCDTO.getClaveRegion(),
                            NIVEL_REGION);

                    logger.error("PADRE prov : " + ubigeopadre.toString());

                    max ++;
                    Ubigeo ubigeonuevo = new Ubigeo();
                    ubigeonuevo.setIdUbigeo(max);
                    ubigeonuevo.setCodigoUbigeoSap(ubigeoRFCDTO.getClavePoblacion());
                    ubigeonuevo.setCodigoUbigeoSapErp(ubigeoRFCDTO.getClavePoblacion());
                    ubigeonuevo.setDescripcion(ubigeoRFCDTO.getDenominacion());
                    ubigeonuevo.setIdPadre(ubigeopadre.getIdUbigeo());
                    ubigeonuevo.setNivel(NIVEL_PROVINCIA);

                    this.ubigeoRepository.save(ubigeonuevo);
                    logger.error("UBIGEO ERR PROVINCIA 02: " + ubigeoRFCDTO.toString());
                }
            }
            catch(Exception ex) {
                logger.error("UBIGEO ERR PROVINCIA EXCEPTION: " + ubigeoRFCDTO.toString());
            }
            contadorTotal++;
            listaPoblacion.add(ubigeoRFCDTO);
        }

        ubigeoRFCResponseDto.setContadorActualizadoProvincia(contador);
        ubigeoRFCResponseDto.setContadorTotalProvincia(contadorTotal);


        contador = 0;
        contadorTotal=0;
        List<UbigeoRFCDTO> listaDistrito=new ArrayList<>();
        //JCoTable tableDistrito = tableParameterList.getTable(NOMBRE_TABLA_RFC_DISTRITO);
        NodeList distritos = doc.getElementsByTagName(NOMBRE_TABLA_RFC_DISTRITO).item(0).getChildNodes();

        for(int i = 0; i < distritos.getLength(); i++) {
            Node distritox = distritos.item(i);
            Element elemento = (Element) distritox;

            UbigeoRFCDTO ubigeoRFCDTO = new UbigeoRFCDTO();
            ubigeoRFCDTO.setClavePais(Utils.getValueNodo(elemento, "COUNTRY"));
            ubigeoRFCDTO.setDenominacion(Utils.getValueNodo(elemento, "CITY_PART"));
            String distrito = Utils.getValueNodo(elemento, "CITYP_CODE");
            //distrito = distrito.substring(2,8);
            ubigeoRFCDTO.setClaveDistrito(distrito);
            ubigeoRFCDTO.setClavePoblacion(Utils.getValueNodo(elemento, "CITY_CODE"));

            try {
                Ubigeo ubigeoEncontrado = this.ubigeoRepository.findByCodigoUbigeoSapAndNivel(
                        ubigeoRFCDTO.getClaveDistrito(),
                        NIVEL_DISTRITO
                );

                if (ubigeoEncontrado.getIdUbigeo() != null) {
                    contador++;
                    ubigeoEncontrado.setCodigoUbigeoSapErp(ubigeoRFCDTO.getClaveDistrito());
                    ubigeoEncontrado.setDescripcion(ubigeoRFCDTO.getDenominacion());
                    this.ubigeoRepository.save(ubigeoEncontrado);
                } else {

                    logger.error("PADRE dist : " + ubigeoRFCDTO.getClavePoblacion());
                    /*Obtener Padre*/
                    Ubigeo ubigeopadre = this.ubigeoRepository.findByCodigoUbigeoSapAndNivel(
                            ubigeoRFCDTO.getClavePoblacion(),
                            NIVEL_PROVINCIA);
                    logger.error("PADRE dist : " + ubigeopadre.toString());

                    max ++;
                    Ubigeo ubigeonuevo = new Ubigeo();
                    ubigeonuevo.setIdUbigeo(max);
                    ubigeonuevo.setCodigoUbigeoSap(ubigeoRFCDTO.getClaveDistrito());
                    ubigeonuevo.setCodigoUbigeoSapErp(ubigeoRFCDTO.getClaveDistrito());
                    ubigeonuevo.setDescripcion(ubigeoRFCDTO.getDenominacion());
                    ubigeonuevo.setIdPadre(ubigeopadre.getIdUbigeo());
                    ubigeonuevo.setNivel(NIVEL_DISTRITO);

                    this.ubigeoRepository.save(ubigeonuevo);

                    logger.error("UBIGEO ERR DISTRITO : " + ubigeoRFCDTO.toString());
                }
            }
            catch(Exception ex) {
                logger.error("UBIGEO ERR DISTRITO exception: " + ubigeoRFCDTO.toString());
            }

            contadorTotal++;
            listaDistrito.add(ubigeoRFCDTO);
         }

        ubigeoRFCResponseDto.setContadorActualizadoDistrito(contador);
        ubigeoRFCResponseDto.setContadorTotalDistrito(contadorTotal);



        ubigeoRFCResponseDto.setListaPaises(listaPaises);
        ubigeoRFCResponseDto.setListaRegion(listaRegion);
        ubigeoRFCResponseDto.setListaDistrito(listaDistrito);
        ubigeoRFCResponseDto.setListaPoblacion(listaPoblacion);
        logger.error("Cantidad Paises" +listaPaises.size() );
        logger.error("Cantidad Regiones" +listaRegion.size() );
        logger.error("Cantidad Distrito" +listaDistrito.size() );
        logger.error("Cantidad Provincia" +listaPoblacion.size() );
        return ubigeoRFCResponseDto;
    }

    private String armarTrama()
    {
        String tramaXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZPE_MM_CONSULTA_UBIGEO>\n" +
                "         <T_ADRCITY>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <COUNTRY>?</COUNTRY>\n" +
                "               <CITY_CODE>?</CITY_CODE>\n" +
                "               <REGION>?</REGION>\n" +
                "               <CITY_NAME>?</CITY_NAME>\n" +
                "               <CITY_CODE_S>?</CITY_CODE_S>\n" +
                "            </item>\n" +
                "         </T_ADRCITY>\n" +
                "         <T_ADRCITYPRT>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <COUNTRY>?</COUNTRY>\n" +
                "               <CITY_CODE>?</CITY_CODE>\n" +
                "               <CITYP_CODE>?</CITYP_CODE>\n" +
                "               <CITY_PART>?</CITY_PART>\n" +
                "            </item>\n" +
                "         </T_ADRCITYPRT>\n" +
                "         <T_T005>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <LAND1>?</LAND1>\n" +
                "               <LANDX>?</LANDX>\n" +
                "            </item>\n" +
                "         </T_T005>\n" +
                "         <T_T005S>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <LAND1>?</LAND1>\n" +
                "               <BLAND>?</BLAND>\n" +
                "               <FPRCD>?</FPRCD>\n" +
                "               <BEZEI>?</BEZEI>\n" +
                "               <BLAND_S>?</BLAND_S>\n" +
                "            </item>\n" +
                "         </T_T005S>\n" +
                "      </urn:ZPE_MM_CONSULTA_UBIGEO>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return tramaXML;

    }


}
