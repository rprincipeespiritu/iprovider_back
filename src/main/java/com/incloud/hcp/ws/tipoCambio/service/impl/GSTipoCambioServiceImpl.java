package com.incloud.hcp.ws.tipoCambio.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.incloud.hcp.ws.OkHttpClientSync;
import com.incloud.hcp.ws.tipoCambio.bean.ObtenerTipoCambioBodyResponse;
import com.incloud.hcp.ws.tipoCambio.bean.TipoCambioResponse;
import com.incloud.hcp.ws.tipoCambio.service.GSTipoCambioService;
import com.incloud.hcp.ws.tipoCambio.service.builder.ObtenerTipoCambioGSBuilder;
import com.incloud.hcp.ws.util.GSConstant;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GSTipoCambioServiceImpl implements GSTipoCambioService {

    public static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");
    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public TipoCambioResponse obtenerTipoCambio(int idEmpresa, String fecha) {

        HashMap<String, String> data = new HashMap<>();
        Gson g = new Gson();
        long l = System.currentTimeMillis();
        String respuestaJson = "";
        OkHttpClientSync mOkHttpClientSync = new OkHttpClientSync("OBTENER_TIPOCAMBIO");

        String url = GSConstant.URL_API_GS_TEST;
        String key = GSConstant.KEY_API_GS_TEST;

       try {
            TipoCambioResponse responseWS = new TipoCambioResponse();
            String jsonInput = ObtenerTipoCambioGSBuilder.newBuilder(idEmpresa,fecha).build();

            RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, jsonInput);
            Request request = mOkHttpClientSync.getRequestPostAuthorization(String.format("%s/%s",url, GSConstant.PATH_API_GS_TIPOCAMBIO_OBTENER), body, key);

            logger.info("Enviando trama para Obtener tipo de cambio -> {}", jsonInput);
            System.out.println(jsonInput);
            Call call = mOkHttpClientSync.getmOkHttpClient().newCall(request);

            Response response = call.execute();


            if(response.isSuccessful()){
                respuestaJson = response.body().string();
                logger.info("Respuesta WS -> {}", respuestaJson);
                Map<String,Object> result = new ObjectMapper().readValue(respuestaJson, HashMap.class);
                if(!String.valueOf(result.get("status")).equals("200")) {
                    result.put("body", new ObtenerTipoCambioBodyResponse());
                    Gson gson = new GsonBuilder().create();
                    respuestaJson = gson.toJson(result);
                }
                responseWS = g.fromJson(respuestaJson, TipoCambioResponse.class);
            }

            else {
                //respuestaJson = String.format("%s : %s", String.valueOf(response.code()), response.message());
                //responseWS = g.fromJson(respuestaJson, ObtenerAlmacenBodyResponse.class);
                responseWS.setStatus(String.valueOf(response.code()));
                responseWS.setMessage(response.message());
                responseWS.setBody(new ObtenerTipoCambioBodyResponse());
            }

            data.put("time", (System.currentTimeMillis() - l) / 1000 + " seconds");
            data.put("response", respuestaJson);
            Long time = (System.currentTimeMillis() - l) / 1000;
            return responseWS;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.getCause());
            TipoCambioResponse responseWS = new TipoCambioResponse();
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            responseWS.setMessageException("Error al obtener el tipo de cambio: " + ex.getMessage());
            responseWS.setMessageCause(ex.toString());
            responseWS.setCause(ex.getCause());
            Long time = (System.currentTimeMillis() - l) / 1000;
            return responseWS;
        }






    }
}
