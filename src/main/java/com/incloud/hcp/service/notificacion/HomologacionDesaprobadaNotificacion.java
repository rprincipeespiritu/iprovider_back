package com.incloud.hcp.service.notificacion;

import com.incloud.hcp.domain.Proveedor;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
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
public class HomologacionDesaprobadaNotificacion extends NotificarMail {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String LOGO = "com/incloud/hcp/image/sapCompanyLogo.png";

    private final String LOGO_HEADER = "com/incloud/hcp/templates/img/header.png";
    private final String LOGO_CALL = "com/incloud/hcp/templates/img/call-to-action.png";
    private final String LOGO_FOOTER = "com/incloud/hcp/templates/img/jrclogo.jpg";

    private final String TEMPLATE = "com/incloud/hcp/templates/portal/TmpHomologacionDesaprobada.html";
    private final String ASUNTO = "Homologación desaprobada";

//    @Value("${sm.portal.url}")
    @Value("${cfg.portal.url}")
    private  String urlPortal;

    @Value("${cfg.notificacion.consulta.url}")
    private String urlConsulta;


//    public void enviar(MailSetting mailSetting, Proveedor proveedor) {
//
//        Mail mail = new Mail();
//        VelocityContext context = new VelocityContext();
//        context.put("nombreProveedor", proveedor.getRazonSocial());
//        context.put("ruc", proveedor.getRuc());
//        context.put("contacto", proveedor.getContacto());
//        context.put("url",this.urlPortal);
//
//        Optional.ofNullable(generateCid(mail.getHtmlMail(), LOGO_VENDOR))
//                .ifPresent(cid -> context.put("vendor_logo", cid));
//
//        Optional.ofNullable(generateCid(mail.getHtmlMail(), LOGO_SAN_MARTIN))
//                .ifPresent(cid -> context.put("sm_logo", cid));
//
//        String content = Optional.ofNullable(TEMPLATE)
//                .map(url -> url + "")
//                .map(template -> {
//                    int i = 0;
//                    return getContentMail(context, template);
//                })
//                .orElse("");
//        mail.setMailSetting(mailSetting);
//        try {
//            mail.enviar(proveedor.getEmail(), null, ASUNTO, content);
//        } catch (EmailException ex) {
//            logger.error("Error al enviar notificacion al proveedor ", ex);
//        }
//    }

    public String enviar(MailSetting mailSetting, Proveedor proveedor) {
        String contactoProveedor = proveedor.getContacto();
        String rucProveedor = proveedor.getRuc();
        String nombreProveedor = proveedor.getRazonSocial();
        String respuesta = "";
        HtmlEmail htmlMail = new HtmlEmail();

        // Insertar la imagen y obtener el ID de contenido
//        String cid = this.generateCidResourceUtils(htmlMail, LOGO);
//        cid = cid == null ? generateCidFromUrlExt(htmlMail) : cid;
//        cid = generateCidResourceUtils(htmlMail, LOGO);
//        logger.error("El nuevo cid: " + cid);

        ////////////////////// BEGIN METODO VELPA //////////////////////

//        String body = "<!DOCTYPE html>";
//        body = body + "<html lang='en'>";
//        body = body + "<head>";
//        body = body + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>";
//        body = body + "</head>";
//        body = body + "<body style='width: 600px;margin: auto;padding: 0;font-family: Arial;font-size: 14px;color: #333'>";
//
//        body = body + "<table cellspacing='0' cellpadding='0' width='600px'>";
//        body = body + "<tr style='border-collapse: collapse;width: 600px;padding: 0;margin: 0'>";
//        body = body + "<td class='main_first' ";
//        body = body + "style='border-collapse: collapse;width: 600px;padding: 0;margin: 0;padding-top: 12px;padding-bottom: 12px'>";
//        body = body + "<h1 style='font-family: Arial;font-weight: bold;font-size: 16px;color: #555;margin-bottom: 24px'>";
//        body = body + "Estimado/a " + contactoProveedor + "&#44;</h1>";
//        body = body + "<p style='font-family: Arial;font-size: 14px;line-height: 18px'>";
//        body = body + "Gracias por completar su información en el IProvider de Topitop&#44; es necesario que corrija y/o<br/>";
//        body = body + "complete información adicional para que pueda ser parte del proceso de homologación.<br/>";
//        body = body + "</p>";
//        body = body + "<p style='font-family: Arial;font-size: 14px;line-height: 18px'>";
//        body = body + "Haga click en el siguiente enlace para ingresar a su cuenta:";
//        body = body + "<a href=\"" + urlPortal + "\">Click para ingresar a su cuenta</a><br/><br/>";
//        body = body + "*Nota: En caso de presentar problemas con su inicio de sesión, copie y pegue el siguiente enlace en un nuevo navegador en modo incógnito:<br/>";
//        body = body + "<a href='" + urlPortal + "'>" + urlPortal + "</a><br/>";
//        body = body + "</p>";
//        body = body + "</td>";
//        body = body + "</tr>";
//        body = body + "</table>";
//
//        body = body + "<table cellpadding='0' cellspacing='0' width='600px'>";
//        body = body + "<tr style='border-collapse: collapse;width: 600px;padding: 0;margin: 0'>";
//        body = body + "<td class='colophon' ";
//        body = body + "style='border-collapse: collapse;width: 600px;padding: 0;margin: 0;font-size: 11px;color: #888;line-height: 13px'>";
//        body = body + "<p style='font-family: Arial;font-size: 12px;line-height: 13px;color: #888'>";
//        body = body + "AGRADECEREMOS NO RESPONDER ESTE CORREO. SI LO DESEA ENVIE SU CONSULTA A:<br/>";
//        body = body + urlConsulta;
//        body = body + "</p>";
//        body = body + "</td>";
//        body = body + "</tr>";
//        body = body + "</table>";
//
//        body = body + "<table cellspacing='0' cellpadding='0' width='600px'>";
//        body = body + "<tr style='border-collapse: collapse;width: 600px;padding: 0;margin: 0'>";
//        body = body + "<td class='main_last' ";
//        body = body + "style='border-collapse: collapse;width: 600px;padding: 0;margin: 0;padding-top: 12px;padding-bottom: 36px'>";
//        body = body + "<p style='font-family: Arial;font-size: 14px;line-height: 18px'>";
//        body = body + "Atentamente&#44;<br/>";
//        body = body + "Equipo IProvider<br/>";
//        body = body + "Topitop<br/>";
//        body = body + "<img align='LEFT' width='148' height='26' src='cid:' />";
//        body = body + "</p>";
//        body = body + "</td>";
//        body = body + "</tr>";
//        body = body + "</table>";
//
//        body = body + "</body>";
//        body = body + "</html>";

        //////////////////////  END  METODO VELPA //////////////////////


        try {


            VelocityContext context = new VelocityContext();

            String LOGO_H = generateCidResourceUtilsNuevo(htmlMail, LOGO_HEADER,"header");

            String LOGO_CAL = generateCidResourceUtilsNuevo(htmlMail, LOGO_CALL,"call");

            String LOGO_FOOT = generateCidResourceUtilsNuevo(htmlMail, LOGO_FOOTER,"footer");


            context.put("contacto", contactoProveedor);
            //context.put("vendor_header", LOGO_H);
            //context.put("vendor_call_action",LOGO_CAL );
            context.put("vendor_footer", LOGO_FOOT);
            context.put("urlportal", urlPortal);

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
            htmlMail.addTo(proveedor.getEmail());
            htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
            htmlMail.setSubject(ASUNTO);
            htmlMail.setHtmlMsg(content);
            htmlMail.setCharset("UTF-8");
            htmlMail.setStartTLSEnabled(true);
            htmlMail.setDebug(true);
            htmlMail.setTLS(true);

            htmlMail.send();
            respuesta = "Correo HomologacionDesaprobadaNotificacion enviado";
            //this.enviarCorreoSMTP(proveedor.getEmail(), ASUNTO, body,mailSetting);
        } catch (Exception ex) {
            respuesta = ex.getMessage();
            logger.error("Error al enviar el correo por HomologacionDesaprobadaNotificacion al proveedor " + nombreProveedor + " con RUC " + rucProveedor, ex);
        }
        return respuesta;
    }
}
