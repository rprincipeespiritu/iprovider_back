package com.incloud.hcp.ws.proveedor.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.ws.OkHttpClientSync;
import com.incloud.hcp.ws.proveedor.bean.ProveedorRegistroBodyResponse;
import com.incloud.hcp.ws.proveedor.bean.ProveedorRegistroResponse;
import com.incloud.hcp.ws.proveedor.service.GSProveedorService;
import com.incloud.hcp.ws.proveedor.service.builder.ProveedorRegistroGSBuilder;
import com.incloud.hcp.ws.util.GSConstant;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class GSProveedorServiceImpl implements GSProveedorService {

    public static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ProveedorRegistroResponse registro(Proveedor p) {

        StringBuilder logJson = new StringBuilder();
        HashMap<String, String> data = new HashMap<>();
        Gson g = new Gson();
        long l = System.currentTimeMillis();
        String respuestaJson = "";
        OkHttpClientSync mOkHttpClientSync = new OkHttpClientSync("PROVEEDOR_REGISTRO");

        String url = GSConstant.URL_API_GS_TEST;
        String key = GSConstant.KEY_API_GS_TEST;

        try {
            // 1. Instanciamos el response del ws
            ProveedorRegistroResponse responseWS = new ProveedorRegistroResponse();
            // 2. Obtenemos los datos de entrada del ws en formato json y se crea un RequestBody
            String jsonInput = ProveedorRegistroGSBuilder.newBuilder(p).build();
            RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, jsonInput);
            // 3. Generamos la petición (Request) POST mandando como parámetro URL/path, body, key
            Request request = mOkHttpClientSync.getRequestPostAuthorization(String.format("%s/%s",url, GSConstant.PATH_API_GS_PROVEEDOR_REGISTRO), body, key);
            logger.info("Enviando trama registro proveedor -> {}", jsonInput);
            //System.out.println(jsonInput);
            Call call = mOkHttpClientSync.getmOkHttpClient().newCall(request);
            // 4. Respuesta del servicio
            Response response = call.execute();

            // 5. Aplicar la lógica necesaria para capturar la respuesta del ws
            if(response.isSuccessful() && !response.message().contains("ya existe")){
                // Respuesta OK: 200
                respuestaJson = response.body().string();
                logger.info("Respuesta WS -> {}", respuestaJson);
                Map<String,Object> result = new ObjectMapper().readValue(respuestaJson, HashMap.class);
                // Solo si en la trama de respuesta retorna status diferente, se instancia el objeto del bodyResponse ya que del ws retorna un(boolean)
                if(!String.valueOf(result.get("status")).equals("200")) {
                    result.put("body", new ProveedorRegistroBodyResponse());
                    Gson gson = new GsonBuilder().create();
                    respuestaJson = gson.toJson(result);
                }
                responseWS = g.fromJson(respuestaJson, ProveedorRegistroResponse.class);
            } else {
                // Respuesta ERROR: 500, 404, 403, entre otros.
                //respuestaJson = String.format("%s : %s", String.valueOf(response.code()), response.message());
                //responseWS = g.fromJson(respuestaJson, ProveedorRegistroResponse.class);
                responseWS.setStatus(String.valueOf(response.code()));
                responseWS.setMessage(response.message());
                responseWS.setBody(new ProveedorRegistroBodyResponse());
            }

            data.put("time", (System.currentTimeMillis() - l) / 1000 + " seconds");
            data.put("response", respuestaJson);
            Long time = (System.currentTimeMillis() - l) / 1000;
            return responseWS;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.getCause());
            // Instanciamos el response del ws e ingresamos los datos de exception
            ProveedorRegistroResponse responseWS = new ProveedorRegistroResponse();
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            responseWS.setMessageException("Error al registrar proveedor: " + ex.getMessage());
            responseWS.setMessageCause(ex.toString());
            responseWS.setCause(ex.getCause());
            Long time = (System.currentTimeMillis() - l) / 1000;
            return responseWS;
        }
    }
}
