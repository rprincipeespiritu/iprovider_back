package com.incloud.hcp.ws.almacen.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.incloud.hcp.domain.CentroAlmacen;
import com.incloud.hcp.ws.OkHttpClientSync;
import com.incloud.hcp.ws.almacen.bean.ObtenerAlmacenBodyResponse;
import com.incloud.hcp.ws.almacen.bean.ObtenerAlmacenResponse;
import com.incloud.hcp.ws.almacen.service.GSAlmacenService;
import com.incloud.hcp.ws.almacen.service.builder.ObtenerAlmacenGSBuilder;
import com.incloud.hcp.ws.util.GSConstant;
import okhttp3.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class GSAlmacenServiceImpl implements GSAlmacenService {

    public static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");
    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ObtenerAlmacenResponse obtenerAlmacen(int idEmpresa, String fechaInicio, String fechaFinal) {

        HashMap<String, String> data = new HashMap<>();
        Gson g = new Gson();
        long l = System.currentTimeMillis();
        String respuestaJson = "";
        OkHttpClientSync mOkHttpClientSync = new OkHttpClientSync("OBTENER_ALMACEN");

        String url = GSConstant.URL_API_GS_TEST;
        String key = GSConstant.KEY_API_GS_TEST;

       try {
            ObtenerAlmacenResponse responseWS = new ObtenerAlmacenResponse();
            String jsonInput = ObtenerAlmacenGSBuilder.newBuilder(idEmpresa,fechaInicio,fechaFinal).build();

            RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, jsonInput);
            Request request = mOkHttpClientSync.getRequestPostAuthorization(String.format("%s/%s",url, GSConstant.PATH_API_GS_OBTENER_ALMACEN), body, key);

            logger.info("Enviando trama para Obtener Almacen -> {}", jsonInput);
            System.out.println(jsonInput);
            Call call = mOkHttpClientSync.getmOkHttpClient().newCall(request);

            Response response = call.execute();


            if(response.isSuccessful()){
                respuestaJson = response.body().string();
                logger.info("Respuesta WS -> {}", respuestaJson);
                Map<String,Object> result = new ObjectMapper().readValue(respuestaJson, HashMap.class);
                if(!String.valueOf(result.get("status")).equals("200")) {
                    result.put("body", new ObtenerAlmacenBodyResponse());
                    Gson gson = new GsonBuilder().create();
                    respuestaJson = gson.toJson(result);
                }
                responseWS = g.fromJson(respuestaJson, ObtenerAlmacenResponse.class);
            }

            else {
                //respuestaJson = String.format("%s : %s", String.valueOf(response.code()), response.message());
                //responseWS = g.fromJson(respuestaJson, ObtenerAlmacenBodyResponse.class);
                responseWS.setStatus(String.valueOf(response.code()));
                responseWS.setMessage(response.message());
                responseWS.setBody(new ArrayList<>());
            }

            data.put("time", (System.currentTimeMillis() - l) / 1000 + " seconds");
            data.put("response", respuestaJson);
            Long time = (System.currentTimeMillis() - l) / 1000;
            return responseWS;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.getCause());
            ObtenerAlmacenResponse responseWS = new ObtenerAlmacenResponse();
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            responseWS.setMessageException("Error al obtener el registro de almacen: " + ex.getMessage());
            responseWS.setMessageCause(ex.toString());
            responseWS.setCause(ex.getCause());
            Long time = (System.currentTimeMillis() - l) / 1000;
            return responseWS;
        }






    }
}
