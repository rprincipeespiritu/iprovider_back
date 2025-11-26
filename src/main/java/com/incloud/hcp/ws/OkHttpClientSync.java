package com.incloud.hcp.ws;

import okhttp3.*;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

public class OkHttpClientSync {

    private OkHttpClient mOkHttpClient;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public OkHttpClientSync(String type) {
        if("SOLICITUD_COMPRA".equals(type))
            mOkHttpClient = buildOkHttpClientIntegration();
        if("OBTENER_ALMACEN".equals(type))
            mOkHttpClient = buildOkHttpClientIntegration();
        if("OBTENER_PRODUCTO".equals(type))
            mOkHttpClient = buildOkHttpClientIntegration();
        if("PROVEEDOR_REGISTRO".equals(type))
            mOkHttpClient = buildOkHttpClientIntegration();
        if("COMPRA_REGISTRO".equals(type))
            mOkHttpClient = buildOkHttpClientIntegration();
        if("OBTENER_TIPOCAMBIO".equals(type))
            mOkHttpClient = buildOkHttpClientIntegration();
        mOkHttpClient = buildOkHttpClientIntegration();
        if("OCPENDIENTE_OBTENER".equals(type))
            mOkHttpClient = buildOkHttpClientIntegration();
        if("SOLICITUD_GUIA".equals(type))
            mOkHttpClient = buildOkHttpClientIntegration();
        if("DOCPENDIENTE_OBTENER".equals(type))
            mOkHttpClient = buildOkHttpClientIntegration();
        if("DOCUMENTO_REGISTRO".equals(type))
            mOkHttpClient = buildOkHttpClientIntegration();
    }


/*
    public OkHttpClientSync(String type) {

        if("PROVEEDOR_REGISTRO".equals(type)||"OBTENER_ALMACEN".equals(type)||"SOLICITUD_COMPRA".equals(type)||
                "OBTENER_PRODUCTO".equals(type)||"COMPRA_REGISTRO".equals(type))

            mOkHttpClient = buildOkHttpClientIntegration();

    }
*/
    public OkHttpClient getmOkHttpClient() {
        return mOkHttpClient;
    }

    private OkHttpClient buildOkHttpClientIntegration() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(0, 1, TimeUnit.NANOSECONDS));

        return builder.build();
    }

    public Request getRequestAuthorization(String url, String clave) {
        Request request = new Request.Builder()
                .url(url)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", clave)
                .build();

        return request;
    }

    public Request getRequestAuthBasic(String url, String usuario, String clave) {
        Request request = new Request.Builder()
                .url(url)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", getAuthorizationHeader(usuario, clave))
                .build();

        return request;
    }

    public Request getRequestAuthToken(String url, String token) {
        Request request = new Request.Builder()
                .url(url)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", String.format("Bearer %s", token))
                .build();

        return request;
    }

    public Request getRequestPostAuthorization(String url, RequestBody body, String clave) {
        Request request = new Request.Builder()
                .url(url)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Authorization", clave)
                .post(body)
                .build();

        return request;
    }

    public Request getRequestPostAuthBasic(String url, RequestBody body, String usuario, String clave) {
        Request request = new Request.Builder()
                .url(url)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .addHeader("Content-Type", "text/xml; charset=utf-8")
                .addHeader("Authorization", getAuthorizationHeader(usuario, clave))
                .post(body)
                .build();

        return request;
    }

    public Request getRequestPostAuthToken(String url, RequestBody body, String token) {
        Request request = new Request.Builder()
                .url(url)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Authorization", String.format("Bearer %s", token))
                .post(body)
                .build();

        return request;
    }

    public Document getResponseXml(Response response){
        DocumentBuilder newDocumentBuilder = null;
        try {
            newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            return newDocumentBuilder.parse(new ByteArrayInputStream(response.body().string().getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        return (node != null) ? node.getNodeValue() : "";
    }

    private String getAuthorizationHeader(String usuario, String clave) {
        String temp = new StringBuilder(usuario).append(":")
                .append(clave).toString();
        String result = "Basic "
                + new String(Base64.encodeBase64(temp.getBytes()));

        return result;
    }

}
