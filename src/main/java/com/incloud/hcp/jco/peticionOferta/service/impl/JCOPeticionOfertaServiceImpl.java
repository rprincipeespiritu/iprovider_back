package com.incloud.hcp.jco.peticionOferta.service.impl;

import com.incloud.hcp.bean.LicitacionResponse;
import com.incloud.hcp.bean.ProveedorCustom;
import com.incloud.hcp.bean.SolicitudPedido;
import com.incloud.hcp.domain.BienServicio;
import com.incloud.hcp.domain.CentroAlmacen;
import com.incloud.hcp.domain.ClaseDocumento;
import com.incloud.hcp.domain.UnidadMedida;
import com.incloud.hcp.jco.peticionOferta.dto.PeticionOfertaRFCDto;
import com.incloud.hcp.jco.peticionOferta.dto.PeticionOfertaRFCParameterBuilder;
import com.incloud.hcp.jco.peticionOferta.dto.PeticionOfertaRFCRequestDto;
import com.incloud.hcp.jco.peticionOferta.dto.PeticionOfertaRFCResponseDto;
import com.incloud.hcp.jco.peticionOferta.service.JCOPeticionOfertaService;
import com.incloud.hcp.myibatis.mapper.ProveedorMapper;
import com.incloud.hcp.myibatis.mapper.SolicitudPedidoMapper;
import com.incloud.hcp.repository.BienServicioRepository;
import com.incloud.hcp.repository.CentroAlmacenRepository;
import com.incloud.hcp.repository.ClaseDocumentoRepository;
import com.incloud.hcp.repository.UnidadMedidaRepository;
import com.incloud.hcp.sap.SapLog;
import com.incloud.hcp.util.Utils;
import com.incloud.hcp.util.constant.SolicitudPedidoConstant;
import com.sap.cloud.sdk.cloudplatform.connectivity.*;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
//import com.sap.conn.jco.*;
//import com.sun.jndi.toolkit.url.Uri;
//import com.sun.*;
import io.vavr.control.Try;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class JCOPeticionOfertaServiceImpl implements JCOPeticionOfertaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int NRO_EJECUCIONES_RFC = 10;
    //    private final String FUNCION_RFC = "ZMMRFC_VP_CONSULTA_SOLPED";
//    private final String FUNCION_RFC = "ZPE_MM_CONSULTA_SOLPED";
    private final String FUNCION_RFC = "ZPE_MM_PETICION_OFERTA";
    //    private final String NOMBRE_TABLA_RFC = "TO_SOLPED";
    private final String NOMBRE_TABLA_RFC = "TO_PETICION_OFERTA";

    @Value("${destination.rfc.profit}")
    private String destinationProfit;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ClaseDocumentoRepository claseDocumentoRepository;

    @Autowired
    private BienServicioRepository bienServicioRepository;

    @Autowired
    private CentroAlmacenRepository centroAlmacenRepository;

    @Autowired
    private SolicitudPedidoMapper solicitudPedidoMapper;

    @Autowired
    private ProveedorMapper proveedorMapperMybatis;

    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;


    public PeticionOfertaRFCResponseDto getPeticionOfertaResponseByCodigo(String numeroSolicitud, boolean obtainFullPositionList) throws Exception {


        PeticionOfertaRFCResponseDto peticionOfertaRFCResponseDto = new PeticionOfertaRFCResponseDto();
        boolean esPeticionOferta = false;
        if (numeroSolicitud != null && !numeroSolicitud.isEmpty())
            esPeticionOferta = numeroSolicitud.startsWith("6") && numeroSolicitud.length() == 10; // se trata de un numero de Peticion de Oferta

        /* Ejecucion invocacion a RFC
        JCoDestination destination = JCoDestinationManager.getDestination(destinationProfit);
        JCoRepository repo = destination.getRepository();
        //logger.error("01A - getSolpedResponseByCodigo");
        JCoFunction jCoFunction = repo.getFunction(FUNCION_RFC);
        //logger.error("01B - getSolpedResponseByCodigo");
        PeticionOfertaRFCParameterBuilder.build(jCoFunction,
                numeroSolicitud,
                esPeticionOferta
        );
        //logger.error("01C - getSolpedResponseByCodigo");
        for(int contador=0; contador < NRO_EJECUCIONES_RFC; contador++) {
            try {
                jCoFunction.execute(destination);
                break;
            } catch (Exception e) {
                if (contador == NRO_EJECUCIONES_RFC - 1 ) {
                    logger.error("01Ca - getPeticionOfertaResponse - INI RFC ERROR: "+ e.toString());
                    throw new Exception(e);
                }
            }
        }
        */
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
            protected RequestConfig.Builder getRequestConfigBuilder(HttpDestinationProperties destination ) {
                return super.getRequestConfigBuilder(destination)
                        .setProxy(new HttpHost(hostx, portx, "http"));
            }
            @Override
            protected HttpClientBuilder getHttpClientBuilder(HttpDestinationProperties destination ) {
                return super.getHttpClientBuilder(destination)
                        .setUserAgent("SDK");
            }
        };

        HttpClientAccessor.setHttpClientFactory(customFactory);*/
        String urlbase = String.valueOf(destination2.get().asHttp().getUri());
        String url = urlbase + "/sap/bc/srt/rfc/sap/zws_consulta_solped/100/zws_consulta_solped/zws_consulta_solped";

        logger.info("RFC: URL" + url);

        String tramaXML = armarTrama(numeroSolicitud);
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

        //HttpEntity strEntity = new StringEntity(tramaXML, "text/xml", "UTF-8");
        HttpEntity strEntity = new StringEntity(soap.toString());

        HttpPost post = new HttpPost(url);
        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions:ZWS_CONSULTA_SOLPED:ZPE_MM_CONSULTA_SOLPEDRequest");
        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions:ZWS_CONSULTA_SOLPED:ZPE_MM_CONSULTA_SOLPEDRequest");
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Accept-Encoding", "gzip,deflate");

        post.setEntity(strEntity);

        logger.info("RFC: Trama Entity" + strEntity);

        HttpResponse response4 = client.execute(post);
        HttpEntity respEntity = response4.getEntity();
        String result = EntityUtils.toString(respEntity);

        DocumentBuilderFactory domFactory = DocumentBuilderFactory
                .newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder
                .parse(new InputSource(new StringReader(result)));

        /* Obteniendo los valores obtenidos del RFC
        //logger.error("02 - getSolpedResponseByCodigo - FIN RFC");
        JCoParameterList tableParameterList = jCoFunction.getTableParameterList();
        JCoParameterList result = jCoFunction.getExportParameterList();*/

        logger.info("Resultado RFC: " + result);

        logger.info("RFC: 1");
        SapLog sapLog = new SapLog();
        String codigo = doc.getElementsByTagName("PO_CODE").item(0).getChildNodes().item(0).getNodeValue();
        String message = doc.getElementsByTagName("PO_MSJE").item(0).getChildNodes().item(0).getNodeValue();

        sapLog.setCode(codigo);
        sapLog.setMesaj(message);
        peticionOfertaRFCResponseDto.setSapLog(sapLog);
        //logger.error("02b - getSolpedResponseByCodigo - sapLog: " + sapLog.toString());

        List<String> rucProveedorList = new ArrayList<>();
        List<ProveedorCustom> proveedorCustomList = new ArrayList<>();

        /* Recorriendo valores obtenidos del RFC */
        List<SolicitudPedido> peticionOfertaList = new ArrayList<SolicitudPedido> ();
        //JCoTable table = tableParameterList.getTable(NOMBRE_TABLA_RFC);
        NodeList posicionesSolped = doc.getElementsByTagName("T_SOLPED").item(0).getChildNodes();

        logger.info("RFC: 2");
        for(int i = 0; i < posicionesSolped.getLength(); i++)
        {
            Node posicion = posicionesSolped.item(i);
            Element elemento = (Element) posicion;
            //logger.error("A - getDevuelveValores TABLE JCO: " + table.toString());
            SolicitudPedido bean = new SolicitudPedido();
            String sapclasedocumento = Utils.getValueNodo(elemento, "BSART");

            if(sapclasedocumento.equals(""))
            {
                continue;
            }

            bean.setSapClaseDocumento(Utils.getValueNodo(elemento, "BSART"));
            bean.setSapBienServicio(Utils.getValueNodo(elemento, "MATNR"));
            bean.setSapUnidadMedida(Utils.getValueNodo(elemento, "MEINS"));
            bean.setSapCentro(Utils.getValueNodo(elemento, "WERKS"));
            bean.setSapAlmacen(Utils.getValueNodo(elemento, "LGORT"));
//                bean.setPosicion(table.getBigInteger("BNFPO").toString());
            bean.setPosicion(Utils.getValueNodo(elemento, "BNFPO"));//EBELP
//                bean.setSolicitudPedido(table.getString("BANFN"));
            if (esPeticionOferta || obtainFullPositionList)
                bean.setSolicitudPedido(Utils.getValueNodo(elemento, "BANFN"));//EBELN
//                bean.setCantidad(table.getBigDecimal("MENGE"));
            bean.setCantidad(BigDecimal.valueOf(Utils.getValueNodoNum(elemento, "MENGE"))); //KTMNG
            bean.setNroParte(Utils.getValueNodo(elemento, "MFRPN"));
//                bean.setSapfechaEntrega(table.getDate("LFDAT"));
            bean.setSapfechaEntrega(Utils.getValueNodoDate(elemento, "LFDAT")); //EINDT
            logger.error("B - getPeticionOferta devuelve valores bean: " + bean);

            String rucProveedor = Utils.getValueNodo(elemento, "STCD1");//
            if(!rucProveedor.isEmpty() &&!rucProveedorList.contains(rucProveedor))
                rucProveedorList.add(rucProveedor);

            if (!obtainFullPositionList && peticionOfertaList.stream().anyMatch(po -> po.getSapBienServicio().equals(bean.getSapBienServicio()) && po.getCantidad().compareTo(bean.getCantidad()) == 0))
                continue;
            else
                bean.setRucProveedor(rucProveedor);


            ClaseDocumento claseDocumento = this.getClaseDocumento(bean.getSapClaseDocumento());
            if (Optional.ofNullable(claseDocumento).isPresent()) {
                //logger.error("C - getDevuelveValores claseDocumento: " + claseDocumento.toString());
                bean.setDescripcionClaseDocumento(claseDocumento.getDescripcion());
                bean.setIdClaseDocumento(claseDocumento.getIdClaseDocumento());
            }
            else {
                throw new Exception("getPeticionOferta -- No se encontró Clase Documento: "+ bean.getSapClaseDocumento() + " Se solicita registrar previamente dicha Clase Documento en el SCP");
            }

            CentroAlmacen centro = this.getCentroAlmacen(bean.getSapCentro(),1);
            if (Optional.ofNullable(centro).isPresent()) {
                //logger.error("D - getDevuelveValores centro: " + centro.toString());
                bean.setCentro(centro);
            }
//                else {
//                    throw new Exception("getPeticionOferta -- No se encontró Centro: "+ bean.getSapCentro() + " Se solicita registrar previamente dicho Centro en el SCP");
//                }

            CentroAlmacen almacen = this.getAlmacen(centro, bean.getSapAlmacen(),2);
            if (Optional.ofNullable(almacen).isPresent()) {
                //logger.error("E - getDevuelveValores almacen: " + almacen.toString());
                bean.setAlmacen(almacen);
            }

            BienServicio bienServicio = this.getBienServicio(bean.getSapBienServicio());
            if (!Optional.ofNullable(bienServicio).isPresent()) {
                throw new Exception("getPeticionOferta -- No se encontró Bien Servicio: "+ bean.getSapBienServicio() + " Se solicita registrar previamente dicho Bien Servicio en el SCP");
            }
            //logger.error("F - getDevuelveValores bienServicio: " + bienServicio.toString());

            UnidadMedida um = this.unidadMedidaRepository.findByCodigoSap(bean.getSapUnidadMedida());
            if (!Optional.ofNullable(um).isPresent()) {
                throw new Exception("getPeticionOferta -- No se encontró Unidad Medida: "+ bean.getSapUnidadMedida() + " Se solicita registrar previamente dicha Unidad Medida en el SCP");
            }
            //logger.error("G - getDevuelveValores UnidadMedida: " + um.toString());

            bienServicio.setUnidadMedida(um);
            bean.setBienServicio(bienServicio);
            bean.setIdUnidadMedida(um.getIdUnidadMedida());
            bean.setUnidadMedida(um);
            //logger.error("03 - getSolpedResponseByCodigo beanSolicitud: " + bean.toString());

            if(Utils.getValueNodo(elemento, "LOEKZ").equalsIgnoreCase("X")){
                bean.setEstado(SolicitudPedidoConstant.ESTADO_ANULADO);
            }
            else{
                String estadoPosicion = this.getEstadoPosicion(numeroSolicitud, bean.getPosicion(), bienServicio.getIdBienServicio());
                bean.setEstado(estadoPosicion);
            }
            //logger.error("03 - getSolpedResponseByCodigo FINAL bean: " + bean.toString());

            peticionOfertaList.add(bean);
        }

        logger.info("RFC: 3");
        for(String ruc : rucProveedorList){
            List<ProveedorCustom> proveedorByRucList = proveedorMapperMybatis.getListProveedorByRuc(ruc);
            if(proveedorByRucList.size() == 1)
                proveedorCustomList.add(proveedorByRucList.get(0));
        }

        logger.info("RFC: 4");
        peticionOfertaRFCResponseDto.setListaProveedorSeleccionado(proveedorCustomList);

        peticionOfertaRFCResponseDto.setListaSolped(peticionOfertaList);
        if (peticionOfertaList != null && peticionOfertaList.size() > 0) {
            CentroAlmacen centro = null;
            if (Optional.ofNullable(peticionOfertaList.get(0).getCentro()).isPresent())
                centro = peticionOfertaList.get(0).getCentro();
            CentroAlmacen almacen = null;
            if (Optional.ofNullable(peticionOfertaList.get(0).getAlmacen()).isPresent())
                almacen = peticionOfertaList.get(0).getAlmacen();
            Date fechaEntrega = peticionOfertaList.get(0).getSapfechaEntrega();
            Integer idClaseDocumento = peticionOfertaList.get(0).getIdClaseDocumento();

            peticionOfertaRFCResponseDto.setIdClaseDocumento(idClaseDocumento);
            peticionOfertaRFCResponseDto.setFechaEntrega(fechaEntrega);
            if(Optional.ofNullable(centro).isPresent())
                peticionOfertaRFCResponseDto.setIdCentro(centro.getIdCentroAlmacen());
            if (Optional.ofNullable(almacen).isPresent()) {
                peticionOfertaRFCResponseDto.setIdAlmacen(almacen.getIdCentroAlmacen());
            }

        }
        logger.info("RFC: 5");
        //logger.error("04 - getSolpedResponseByCodigo peticionOfertaRFCResponseDto: " + peticionOfertaRFCResponseDto.toString());
        return peticionOfertaRFCResponseDto;

    }


    public List<SapLog> modificarPeticionOferta(PeticionOfertaRFCRequestDto peticionOfertaRFCRequestDto) throws Exception {

        String FUNCION_RFC_NAME = "ZPE_MM_MOD_PETICION_OFERTA";
        List<SapLog> sapMessageList = new ArrayList<>();

        /* Ejecucion invocacion a RFC
        JCoDestination destination = JCoDestinationManager.getDestination(destinationProfit);
        JCoRepository repo = destination.getRepository();
        JCoFunction jCoFunction = repo.getFunction(FUNCION_RFC_NAME);
        this.mapFilters(jCoFunction, peticionOfertaRFCRequestDto);

        for(int contador=0; contador < NRO_EJECUCIONES_RFC; contador++) {
            try {
                jCoFunction.execute(destination);
                break;
            } catch (Exception e) {
                if (contador == NRO_EJECUCIONES_RFC - 1 ) {
                    logger.error("01Ca - modPeticionOfertaResponse - INI RFC ERROR: "+ e.toString());
                    throw new Exception(e);
                }
            }
        }*/

        /* Obteniendo los valores obtenidos del RFC
        JCoParameterList tableParameterList = jCoFunction.getTableParameterList();
        JCoTable table = tableParameterList.getTable("TO_RETURN");*/

        /* Recorriendo valores obtenidos del RFC
        if (table != null && !table.isEmpty()) {
            do {
                SapLog sapMessage = new SapLog();

                sapMessage.setCode(table.getString("TYPE"));
                sapMessage.setMesaj(table.getString("MESSAGE"));

                sapMessageList.add(sapMessage);
            } while (table.nextRow());
        }*/

        return sapMessageList;
    }

/*
    private void mapFilters(JCoFunction function, PeticionOfertaRFCRequestDto peticionOfertaRequestDto) {
        JCoParameterList importParamList = function.getImportParameterList();
        JCoParameterList tableParamList = function.getTableParameterList();

        String numeroLicitacion = peticionOfertaRequestDto.getNumeroLicitacion();
        if (numeroLicitacion != null && !numeroLicitacion.isEmpty())
            importParamList.setValue("PI_IHREZ", numeroLicitacion);

        JCoTable poPosicionJCoTable = tableParamList.getTable("TO_MM_RM06E");
        List<PeticionOfertaRFCDto> peticionOfertaDtoList = peticionOfertaRequestDto.getPeticionOfertaRFCDtoList();

        for (int i = 0; i < peticionOfertaDtoList.size(); i++) {
            PeticionOfertaRFCDto peticionOfertaDto = peticionOfertaDtoList.get(i);

            poPosicionJCoTable.appendRow();
            poPosicionJCoTable.setRow(i);
            poPosicionJCoTable.setValue("EBELN", peticionOfertaDto.getNumeroPeticionOferta());
            poPosicionJCoTable.setValue("EBELP", peticionOfertaDto.getPosicionPeticionOferta());
            poPosicionJCoTable.setValue("EMATN", peticionOfertaDto.getCodigoMaterial());
            poPosicionJCoTable.setValue("NETPR", peticionOfertaDto.getPrecioUnitario());
            poPosicionJCoTable.setValue("WAERS", peticionOfertaDto.getCodigoMoneda());
        }
    }*/

    private ClaseDocumento getClaseDocumento(String codigoClaseDocumento) throws Exception {
        if (Optional.ofNullable(codigoClaseDocumento).isPresent() && !codigoClaseDocumento.isEmpty()){
            ClaseDocumento claseDoc = this.claseDocumentoRepository.
                    findByCodigoClaseDocumentoAndNivel(codigoClaseDocumento, 1);
            return claseDoc;
        }else{
            return null;
        }
    }

    private BienServicio getBienServicio(String codigoSap) throws Exception{
        if (Optional.ofNullable(codigoSap).isPresent() && !codigoSap.isEmpty()){
            BienServicio bienServ = this.bienServicioRepository.getByCodigoSap(codigoSap);
            return bienServ;
        }else{
            return null;
        }
    }

    private CentroAlmacen getCentroAlmacen(String codigoSap, int nivel) throws Exception{
        if (Optional.ofNullable(codigoSap).isPresent() && !codigoSap.isEmpty()){
            CentroAlmacen obj = this.centroAlmacenRepository.findByCodigoSapAndNivel(codigoSap, nivel);
            return obj;
        }else{
            return null;
        }

    }

    private CentroAlmacen getAlmacen(CentroAlmacen centroAlmacen, String codigoSap, int nivel) throws Exception{
        if (Optional.ofNullable(codigoSap).isPresent() && !codigoSap.isEmpty()){
            CentroAlmacen obj = this.centroAlmacenRepository.findByIdPadreAndCodigoSapAndNivel(
                    centroAlmacen.getIdCentroAlmacen(),
                    codigoSap,
                    nivel);
            return obj;
        }else{
            return null;
        }
    }

    /**
     *
     * @param codSolped = Codigo Solped
     * @param codBienServicio = Codigo de Material o Servicio
     * @return idLicitacion si la consulta devuelve algo, de lo contrario retorna null;
     */
    private String getEstadoPosicion(String codSolped, int codBienServicio) throws Exception{
        List<LicitacionResponse> listaLicitacion = this.solicitudPedidoMapper.getLicitacionBySolpedAndBien(codSolped, codBienServicio);
        String estado = "";
        if (listaLicitacion.size() > 0){
            String estadoObj = listaLicitacion.get(0).getEstadoLicitacion();

            switch (estadoObj){
                case "GE":
                    estado = SolicitudPedidoConstant.ESTADO_EN_PROCESO;
                    break;
                case "PE":
                    estado = SolicitudPedidoConstant.ESTADO_EN_PROCESO;
                    break;
                case "EV":
                    estado = SolicitudPedidoConstant.ESTADO_EN_PROCESO;
                    break;
                case "AD":
                    estado = SolicitudPedidoConstant.ESTADO_ADJUDICADO;
                    break;
                case "AN":
                    estado = SolicitudPedidoConstant.ESTADO_POR_LICITAR;
                    break;
                case "ES":
                    estado = SolicitudPedidoConstant.ESTADO_ADJUDICADO;
                    break;
                default:
                    estado = SolicitudPedidoConstant.ESTADO_POR_LICITAR;
            }
        }else{
            estado = SolicitudPedidoConstant.ESTADO_POR_LICITAR;
        }
        return estado;
    }


    private String getEstadoPosicion(String codSolped, String posicion, int codBienServicio) throws Exception{
        List<LicitacionResponse> listaLicitacion = this.solicitudPedidoMapper.getLicitacionBySolpedPosicionAndBien(codSolped, posicion, codBienServicio);
        String estado = "";
        if (listaLicitacion.size() > 0){
            String estadoObj = listaLicitacion.get(0).getEstadoLicitacion();

            switch (estadoObj){
                case "GE":
                    estado = SolicitudPedidoConstant.ESTADO_EN_PROCESO;
                    break;
                case "PE":
                    estado = SolicitudPedidoConstant.ESTADO_EN_PROCESO;
                    break;
                case "EV":
                    estado = SolicitudPedidoConstant.ESTADO_EN_PROCESO;
                    break;
                case "AD":
                    estado = SolicitudPedidoConstant.ESTADO_ADJUDICADO;
                    break;
                case "AN":
                    estado = SolicitudPedidoConstant.ESTADO_POR_LICITAR;
                    break;
                case "ES":
                    estado = SolicitudPedidoConstant.ESTADO_ADJUDICADO;
                    break;
                default:
                    estado = SolicitudPedidoConstant.ESTADO_POR_LICITAR;
            }
        }else{
            estado = SolicitudPedidoConstant.ESTADO_POR_LICITAR;
        }
        return estado;
    }

    private String armarTrama(String numeroSolicitud)
    {
        String tramaXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZPE_MM_CONSULTA_SOLPED>\n" +
                "         <PI_BANFN>"+ numeroSolicitud + "</PI_BANFN>\n" +
                "         <T_SOLPED>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <MANDT></MANDT>\n" +
                "               <BANFN></BANFN>\n" +
                "               <BNFPO></BNFPO>\n" +
                "               <BSART></BSART>\n" +
                "               <WERKS></WERKS>\n" +
                "               <LGORT></LGORT>\n" +
                "               <MATNR></MATNR>\n" +
                "               <MENGE></MENGE>\n" +
                "               <MEINS></MEINS>\n" +
                "               <LFDAT></LFDAT>\n" +
                "               <MFRPN></MFRPN>\n" +
                "               <LOEKZ></LOEKZ>\n" +
                "            </item>\n" +
                "         </T_SOLPED>\n" +
                "      </urn:ZPE_MM_CONSULTA_SOLPED>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return tramaXML;

    }

    private String armarTrama2()
    {
        String tramaXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZPE_MM_CONSULTA_SOLPED>\n" +
                "         <PI_BANFN>1000038704</PI_BANFN>\n" +
                "         <T_SOLPED>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <MANDT></MANDT>\n" +
                "               <BANFN></BANFN>\n" +
                "               <BNFPO></BNFPO>\n" +
                "               <BSART></BSART>\n" +
                "               <WERKS></WERKS>\n" +
                "               <LGORT></LGORT>\n" +
                "               <MATNR></MATNR>\n" +
                "               <MENGE></MENGE>\n" +
                "               <MEINS></MEINS>\n" +
                "               <LFDAT></LFDAT>\n" +
                "               <MFRPN></MFRPN>\n" +
                "               <LOEKZ></LOEKZ>\n" +
                "            </item>\n" +
                "         </T_SOLPED>\n" +
                "      </urn:ZPE_MM_CONSULTA_SOLPED>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return tramaXML;
    }
}
