package com.incloud.hcp.wsdl.inside;

import java.util.Optional;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
/**
 * Created by Administrador on 09/11/2017.
 */
@Service
public class InSiteServiceImpl implements InSiteService, InSiteConstant {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

// Version 2
//    @Override
//    public InSiteResponse getConsultaRuc(String ruc) throws InSiteException {
////        String header = "getConsultaRuc - RUC " + ruc + " // ";
//        try {
//            ServiceRUCLocator l = new ServiceRUCLocator();
//            ServiceRUCPortType type = l.getServiceRUCPort();
//            //String data = type.consultaRUC(ruc, USER_DEFAULT, LICENSE_DEFAULT, FORMAT_DEFAULT);
////            logger.error(header + "respuesta JSON String: " + data);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            // set `accept` header
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//
//            ResponseEntity<String> res ;
//
//            try {
//                res = restTemplate.getForEntity("http://sunat.sinergytech.pe/api/v1/ruc/"+ruc+"?token=sinergy", String.class);
//
//            }catch (Exception e){
//                e.printStackTrace();
//                throw new  Exception("error"+ e.getMessage());
//            }
//
//            JSONParser parser = new JSONParser();
//            //JSONArray array = (JSONArray) parser.parse(data);
//            JSONObject json = (JSONObject) parser.parse(res.getBody());
//            System.out.println(json);
////            Map<String, String> map = new HashMap<>();
////            logger.error(header + "respuesta JSON Array toString: " + array.toString());
////            logger.error(header + "respuesta JSON Array toJSONString: " + array.toJSONString());
//
////            for (int i = 0; i < array.size(); i++) {
//////                logger.error(header + "respuesta JSON Array Item " + i + ": " + array.get(i));
////                try {
////                    Map<String, String> values = (Map) array.get(i);
////                    Set<String> keys = values.keySet();
////                    keys.stream().forEach(key -> {
////                        map.put(key, values.get(key));
////                    });
////                }catch (Exception ex){
////
////                }
////            }
//
//            InSiteResponse response = new InSiteResponse();
//
//            response.setRuc(String.valueOf(Optional.ofNullable(json.get("ruc")).orElse("")));
//            response.setCondicion(Optional.ofNullable(json.get("condicion"))
//                    .map(s -> s.equals("HABIDO"))
//                    .orElse(Boolean.FALSE));
//            response.setEstado(Optional.ofNullable(json.get("estado"))
//                    .map(s -> s.equals("ACTIVO"))
//                    .orElse(Boolean.FALSE));
//            response.setUbigeo(String.valueOf(Optional.ofNullable(json.get("departamento")).orElse("")));
//            response.setDireccion(String.valueOf(Optional.ofNullable(json.get("direccion")).orElse("NINGUNO")));
//            if (StringUtils.isBlank(response.getDireccion())) {
//                response.setDireccion(String.valueOf(Optional.ofNullable(json.get("direccion")).orElse("")));
//            }
//
//
//            response.setRazonSocial(String.valueOf(Optional.ofNullable(json.get("razonSocial")).orElse("")).replace("\"", ""));
//            response.setRegion(String.valueOf(Optional.ofNullable(json.get("region")).orElse("NINGUNO")));
//            response.setProvincia(String.valueOf(Optional.ofNullable(json.get("provincia")).orElse("NINGUNO")));
//            response.setDistrito(String.valueOf(Optional.ofNullable(json.get("distrito")).orElse("NINGUNO")));
//
//            JSONArray arr = (JSONArray) json.get("actEconomicas");
//            String cadena = "";
//            if(!arr.isEmpty()){
//                for (int x=0;x<arr.size();x++){
//                    cadena =cadena + (arr.get(x)) +" - ";
//                }
//            }
//            response.setActividadEconomica(String.valueOf(Optional.ofNullable(String.valueOf(cadena)).orElse("")));
//
//            JSONArray sistElectronica = (JSONArray) json.get("sistElectronica");
//            String sisElec = "";
//            if(!sistElectronica.isEmpty()){
//                for (int x=0;x<sistElectronica.size();x++){
//                    sisElec =sisElec + (sistElectronica.get(x)) +" - ";
//                }
//            }
//            response.setCodigoSistemaEmisionElect(String.valueOf(Optional.ofNullable(String.valueOf(sisElec)).orElse("")));
//
//
//            JSONArray cpPago = (JSONArray) json.get("cpPago");
//            String cpP = "";
//            if(!cpPago.isEmpty()){
//                for (int x=0;x<cpPago.size();x++){
//                    cpP =cpP + (cpPago.get(x)) +" - ";
//                }
//            }
//
//            response.setCodigoComprobantePago(String.valueOf(Optional.ofNullable(String.valueOf(cpP)).orElse("")));
//
//            JSONArray padrones = (JSONArray) json.get("padrones");
//            String pad = "";
//            if(!padrones.isEmpty()){
//                for (int x=0;x<padrones.size();x++){
//                    pad =pad + (padrones.get(x)) +" - ";
//                }
//            }
//
//            response.setCodigoPadron(String.valueOf(Optional.ofNullable(String.valueOf(pad)).orElse("")));
//
//            String fechaIns = "";
//            if(json.get("fechaInscripcion") !=null){
//                fechaIns = String.valueOf(json.get("fechaInscripcion"));
//                fechaIns = fechaIns.replace("T00:00:00.000Z","");
//            }
//            response.setFechaInicioActiSunat(fechaIns);
//
//            if (response.getRuc().isEmpty()) {
//                throw new InSiteException("El RUC consultado no existe");
//            }
//            return response;
//        } catch (Exception ex) {
//            logger.error("Error al consultar el numero de ruc en el 'Servicio de Consulta'", ex);
//            throw new InSiteException(ex.getMessage());
//        }
//    }


    // EAAR: version 3 WS SUNAT
    @Override
    public InSiteResponse getConsultaRuc(String ruc) throws InSiteException {
        try {

            /*Implementamos nuevo consumo de SUNAT */
            String url_sunat = "https://apis.growbiz.la/sunat?ruc=" + ruc;
            
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request postRequest_pk = new Request.Builder()
                    .url(url_sunat)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("x-api-key", "RjygYJMPxx2VrtugFxWku3nLKOmORXNT7hJA9LZp")                       
                    .get()
                    .build();

            Response postResponse_pk = client.newCall(postRequest_pk).execute();

            if (postResponse_pk.isSuccessful()) {

                String responseBody = postResponse_pk.body().string();

                try {
                    // Convertir la respuesta en un objeto JSON
                    JSONObject jsonObject = new JSONObject(responseBody);

                    // Extraer los valores y asignarlos a variables
                    String razonSocial = jsonObject.getString("razon_social");
                    String tipoDocumento = jsonObject.getString("tipo_documento");
                    String numeroDocumento = jsonObject.getString("numero_documento");
                    String estado = jsonObject.getString("estado");
                    String condicion = jsonObject.getString("condicion");
                    String direccion = jsonObject.getString("direccion");
                    String ubigeo = jsonObject.getString("ubigeo");
                    String viaTipo = jsonObject.getString("via_tipo");
                    String viaNombre = jsonObject.getString("via_nombre");
                    String zonaCodigo = jsonObject.getString("zona_codigo");
                    String zonaTipo = jsonObject.getString("zona_tipo");
                    String numero = jsonObject.getString("numero");
                    String interior = jsonObject.getString("interior");
                    String lote = jsonObject.getString("lote");
                    String dpto = jsonObject.getString("dpto");
                    String manzana = jsonObject.getString("manzana");
                    String kilometro = jsonObject.getString("kilometro");
                    String distrito = jsonObject.getString("distrito");
                    String provincia = jsonObject.getString("provincia");
                    String departamento = jsonObject.getString("departamento");
                    boolean esAgenteRetencion = jsonObject.getBoolean("_es_agente_retencion");
                    String comercio_exterior = jsonObject.getString("comercio_exterior");
                    String tipo_facturacion = jsonObject.getString("tipo_facturacion");
                    String actividad_economica = jsonObject.getString("actividad_economica");
                    String tipo_contabilidad = jsonObject.getString("tipo_contabilidad");
                    

                    InSiteResponse response = new InSiteResponse();

                    response.setRuc(ruc);
                    response.setCondicion(Optional.ofNullable(condicion)
                        .map(s -> s.equals("HABIDO"))
                        .orElse(Boolean.FALSE));
                    response.setEstado(Optional.ofNullable(estado)
                        .map(s -> s.equals("ACTIVO"))
                        .orElse(Boolean.FALSE));

                    response.setUbigeo(Optional.ofNullable(ubigeo).orElse(""));
                    response.setDireccion(Optional.ofNullable(direccion).orElse(""));
                    //if (StringUtils.isBlank(response.getDireccion())) {
                    //    response.setDireccion(Optional.ofNullable(map.get(DIRECCION)).orElse(""));
                    //}

                    response.setRazonSocial(Optional.ofNullable(razonSocial).orElse("").replace("&\"", ""));
                    response.setRegion(Optional.ofNullable(departamento).orElse(""));
                    response.setProvincia(Optional.ofNullable(provincia).orElse(""));
                    response.setDistrito(Optional.ofNullable(distrito).orElse(""));

                    response.setActividadEconomica(Optional.ofNullable(actividad_economica).orElse(""));
                    response.setCodigoSistemaEmisionElect(Optional.ofNullable(tipo_facturacion).orElse(""));
                    response.setCodigoComprobantePago(Optional.ofNullable(tipo_contabilidad).orElse(""));
                    response.setCodigoPadron("");
                    response.setFechaInicioActiSunat("");

                    return response;

                }
                catch(Exception ex){
                    logger.error("Error al consultar el numero de ruc en el 'Servicio de Consulta'", ex);
                    throw new InSiteException(ex.getMessage());
                }
            }else
            {
                throw new InSiteException("El RUC consultado no existe");   
            }

            /*
            ServiceRUCLocator l = new ServiceRUCLocator();
            ServiceRUCPortType type = l.getServiceRUCPort();
            String data = type.consultaRUC(ruc, USER_DEFAULT, LICENSE_DEFAULT, FORMAT_DEFAULT);


            data = data.replace("ntttttt", "");//fix hardcode
            data = data.replace("nttttttt", "");//fix hardcode
            data = data.replace("tt", "");//fix hardcode
            data = data.replace("ttt", "");//fix hardcode
            data = data.replace("tttt", "");//fix hardcode
            data = data.replace("ttttt", "");//fix hardcode
            data = data.replace("tttttt", "");//fix hardcode
            data = data.replace("ttttttt", "");//fix hardcode

            data = data.replace("'", "");
            data = data.replace("{\"", "{'");
            data = data.replace("\":", "':");
            data = data.replace(":\"", ":'");
            data = data.replace("\"}", "'}");
            data = data.replace("\"", "");
            data = data.replace("'", "\"");

            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(data);
            Map<String, String> map = new HashMap<>();

            for (int i = 0; i < array.size(); i++) {
                Map<String, String> values = (Map) array.get(i);
                Set<String> keys = values.keySet();
                keys.stream().forEach(key -> {
                    try {
                        map.put(key, values.get(key));
                    } catch (Exception e) {
                    }
                });
            }

            InSiteResponse response = new InSiteResponse();

            response.setRuc(Optional.ofNullable(map.get(RUC)).orElse(""));
            response.setCondicion(Optional.ofNullable(map.get(CONDICION))
                .map(s -> s.equals("HABIDO"))
                .orElse(Boolean.FALSE));
            response.setEstado(Optional.ofNullable(map.get(ACTIVO))
                .map(s -> s.equals("ACTIVO"))
                .orElse(Boolean.FALSE));

            response.setUbigeo(Optional.ofNullable(map.get(UBIGEO)).orElse(""));
            response.setDireccion(Optional.ofNullable(map.get(DIRECCION_02)).orElse(""));
            if (StringUtils.isBlank(response.getDireccion())) {
                response.setDireccion(Optional.ofNullable(map.get(DIRECCION)).orElse(""));
            }

            response.setRazonSocial(Optional.ofNullable(map.get(RAZON_SOCIAL)).orElse("").replace("&\"", ""));
            response.setRegion(Optional.ofNullable(map.get(REGION)).orElse(""));
            response.setProvincia(Optional.ofNullable(map.get(PROVINCIA)).orElse(""));
            response.setDistrito(Optional.ofNullable(map.get(DISTRITO)).orElse(""));

            response.setActividadEconomica(Optional.ofNullable(map.get(ACTIVIDAD_ECONOMICA)).orElse(""));
            response.setCodigoSistemaEmisionElect(Optional.ofNullable(map.get(SISTEMA_EMISION_ELECTRONICA)).orElse(""));
            response.setCodigoComprobantePago(Optional.ofNullable(map.get(COMPROBANTE_PAGO)).orElse(""));
            response.setCodigoPadron(Optional.ofNullable(map.get(PADRON)).orElse(""));
            response.setFechaInicioActiSunat(Optional.ofNullable(map.get(FECHA_INICIO_ACTIVIDAD)).orElse(""));

            if (response.getRuc().isEmpty()) {
                throw new InSiteException("El RUC consultado no existe");
            }
            return response; */
        } catch (Exception ex) {
            logger.error("Error al consultar el numero de ruc en el 'Servicio de Consulta'", ex);
            throw new InSiteException(ex.getMessage());
        }
    }
}
