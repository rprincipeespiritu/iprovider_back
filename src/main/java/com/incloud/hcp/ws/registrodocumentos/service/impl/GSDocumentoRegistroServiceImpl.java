package com.incloud.hcp.ws.registrodocumentos.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.incloud.hcp.domain.OrdenCompra;
import com.incloud.hcp.domain.Prefactura;
import com.incloud.hcp.ws.OkHttpClientSync;
import com.incloud.hcp.ws.registrodocumentos.bean.DocumentoRegistroBodyResponse;
import com.incloud.hcp.ws.registrodocumentos.bean.DocumentoRegistroResponse;
import com.incloud.hcp.ws.registrodocumentos.dto.RegistroDocumentoGSDto;
import com.incloud.hcp.ws.registrodocumentos.service.GSDocumentoRegistroService;
import com.incloud.hcp.ws.registrodocumentos.service.builder.DocumentoRegistroGSBuilder;
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
public class GSDocumentoRegistroServiceImpl implements GSDocumentoRegistroService {

    public static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public DocumentoRegistroResponse registro(RegistroDocumentoGSDto dto) {

        StringBuilder logJson = new StringBuilder();
        HashMap<String, String> data = new HashMap<>();
        Gson g = new Gson();
        long l = System.currentTimeMillis();
        String respuestaJson = "";
        OkHttpClientSync mOkHttpClientSync = new OkHttpClientSync("DOCUMENTO_REGISTRO");

        String url = GSConstant.URL_API_GS_TEST;
        String key = GSConstant.KEY_API_GS_TEST;

        try {
            // 1. Instanciamos el response del ws
            DocumentoRegistroResponse responseWS = new DocumentoRegistroResponse();
            // 2. Obtenemos los datos de entrada del ws en formato json y se crea un RequestBody
            String jsonInput = DocumentoRegistroGSBuilder.newBuilder(dto).build();
            RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, jsonInput);
            // 3. Generamos la petición (Request) POST mandando como parámetro URL/path, body, key
            Request request = mOkHttpClientSync.getRequestPostAuthorization(String.format("%s/%s",url, GSConstant.PATH_API_GS_DOCUMENTOS_REGISTRO), body, key);
            logger.info("Enviando trama registro de documentos -> {}", jsonInput);
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
                    result.put("body", new DocumentoRegistroBodyResponse());
                    Gson gson = new GsonBuilder().create();
                    respuestaJson = gson.toJson(result);
                }
                responseWS = g.fromJson(respuestaJson, DocumentoRegistroResponse.class);
            } else {
                // Respuesta ERROR: 500, 404, 403, entre otros.
                //respuestaJson = String.format("%s : %s", String.valueOf(response.code()), response.message());
                //responseWS = g.fromJson(respuestaJson, ProveedorRegistroResponse.class);
                responseWS.setStatus(String.valueOf(response.code()));
                responseWS.setMessage(response.message());
                responseWS.setBody(new DocumentoRegistroBodyResponse());
            }

            data.put("time", (System.currentTimeMillis() - l) / 1000 + " seconds");
            data.put("response", respuestaJson);
            Long time = (System.currentTimeMillis() - l) / 1000;
            return responseWS;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.getCause());
            // Instanciamos el response del ws e ingresamos los datos de exception
            DocumentoRegistroResponse responseWS = new DocumentoRegistroResponse();
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            responseWS.setMessageException("Error al registrar los documentos: " + ex.getMessage());
            responseWS.setMessageCause(ex.toString());
            responseWS.setCause(ex.getCause());
            Long time = (System.currentTimeMillis() - l) / 1000;
            return responseWS;
        }
    }
}
