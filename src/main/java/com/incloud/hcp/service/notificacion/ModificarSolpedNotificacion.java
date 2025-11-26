package com.incloud.hcp.service.notificacion;

import com.incloud.hcp.domain.ActaSustento;
import com.incloud.hcp.domain.ModificacionSolped;
import com.incloud.hcp.domain.Proveedor;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by Administrador on 13/11/2017.
 */
@Component
public class ModificarSolpedNotificacion extends NotificarMail {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String ASUNTO = "IProvider -  Modificación Solped";
    private final String LOGO = "com/incloud/hcp/image/sapCompanyLogo.png";
    private final String LOGO_HEADER = "com/incloud/hcp/templates/img/header.png";
    private final String LOGO_CALL = "com/incloud/hcp/templates/img/call-to-action.png";
    private final String LOGO_FOOTER = "com/incloud/hcp/templates/img/jrclogo.jpg";
    private final String TEMPLATE = "com/incloud/hcp/templates/portal/ModificarSolped.html";
    private final String TEMPLATEAPROBACION = "com/incloud/hcp/templates/portal/ModificarSolped.html";

//    @Value("${sm.portal.url}")
    @Value("${cfg.portal.url.centenario}")
    private  String urlPortal;

    @Value("${cfg.notificacion.consulta.url}")
    private String urlConsulta;


    public String enviar(MailSetting mailSetting, ModificacionSolped modificacionSolped, String email){
        String contactoProveedor = email;

        String nroSolped =String.valueOf(modificacionSolped.getNroSolped());
        String respuesta = "";
        HtmlEmail htmlMail = new HtmlEmail();

        try {

            VelocityContext context = new VelocityContext();

            String LOGO_H = generateCidResourceUtilsNuevo(htmlMail, LOGO_HEADER,"header");

            String LOGO_CAL = generateCidResourceUtilsNuevo(htmlMail, LOGO_CALL,"call");

            String LOGO_FOOT = generateCidResourceUtilsNuevo(htmlMail, LOGO_FOOTER,"footer");


            //context.put("vendor_header", LOGO_H);
            //context.put("vendor_call_action",LOGO_CAL );
            context.put("vendor_footer", LOGO_FOOT);
            context.put("urlportal", urlPortal);
            context.put("nroSolped", nroSolped);

            String content = Optional.ofNullable(TEMPLATE)
                    .map(url -> url + "")
                    .map(template -> {
                        int i = 0;
                        return getContentMail(context, template);
                    })
                    .orElse("");

            htmlMail.setHostName(mailSetting.getHost());
            htmlMail.setSmtpPort(Integer.parseInt(mailSetting.getPort()));
            htmlMail.setAuthenticator(new DefaultAuthenticator(mailSetting.getUser(), mailSetting.getPassword()));
            htmlMail.addTo(email);
            htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
            htmlMail.setSubject(ASUNTO);
            htmlMail.setHtmlMsg(content);
            htmlMail.setCharset("UTF-8");
            htmlMail.setStartTLSEnabled(true);
            htmlMail.setDebug(true);
            htmlMail.setTLS(true);

            htmlMail.send();
            respuesta = "Correo ModificarSolped enviado";
            //this.enviarCorreoSMTP(proveedor.getEmail(), ASUNTO, body, mailSetting);
        } catch (Exception ex) {
            respuesta = ex.getMessage();
            logger.error("Error al enviar el correo por ", ex);
        }
        return respuesta;
    }


}
