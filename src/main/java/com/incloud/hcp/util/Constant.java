package com.incloud.hcp.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by USER on 17/10/2017.
 */
@Component
public class Constant {

    public static final String ZONATIME_LIMA = "America/Lima";

    public static final String KEY_GENERAL = "GENERAL";

    public static final String PATRON_ACENTOS = "[^\\p{ASCII}]";
    public static final String CODIGO_TIPO_SOLICITUD_NO_CONFORME = "03";

    public static final String DOS = "2";
    public static final String UNO = "1";
    public static final String CERO = "0";
    public static final String UNDEFINED = "undefined";

    public static final String S = "S";
    public static final String N = "N";
    public static final String SI = "SI";
    public static final String NO = "NO";

    public static final String DEVELOPMENT_LOCAL_ENVIRONMENT = "devlocal";
    public static final String DEVELOPMENT_ENVIRONMENT = "dev";
    public static final String TESTING_ENVIRONMENT = "qas";
    public static final String PRODUCTION_ENVIRONMENT = "prd";

    public static final String SAP_TIPO_PROVEEDOR_NACIONAL = "ZNAC";


    public static String URL_SAP;
    public static String USER_SAP;
    public static String PASS_SAP;

    @Value("${URL_IAS}")
    private String urlSap;

    @Value("${USER_IAS}")
    private String userSap;

    @Value("${PASS_IAS}")
    private String passSap;

    @PostConstruct
    public void init() {
        URL_SAP = urlSap;
        USER_SAP = userSap;
        PASS_SAP = passSap;
    }



}
