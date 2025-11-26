package com.incloud.hcp.service.notificacion;

import com.incloud.hcp.bean.ProveedorCustom;
import com.incloud.hcp.bean.UserSession;
import com.incloud.hcp.domain.Licitacion;
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
 * Created by USER on 11/12/2017.
 */
@Component
public class LicitacionAnularNotificacion extends NotificarMail{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    private final String LOGO = "com/incloud/hcp/image/sapCompanyLogo.png";
//    private final String LOGO_VENDOR = "com/incloud/hcp/image/vendor_logo.png";
//    private final String LOGO_SAN_MARTIN = "com/incloud/hcp/image/sm_logo.png";
    private final String TEMPLATE = "com/incloud/hcp/templates/portal/TmpAnularLicitacion.html";
    private final String LOGO_HEADER = "com/incloud/hcp/templates/img/header.png";
//    private final String LOGO_CALL = "com/incloud/hcp/templates/img/call-to-action.png";
    private final String LOGO_FOOTER = "com/incloud/hcp/templates/img/jrclogo.jpg";

//    @Value("${sm.portal.url}")
    @Value("${cfg.portal.url}")
    private  String urlPortal;

    @Value("${cfg.notificacion.consulta.url}")
    private String urlConsulta;


//    public void enviar(MailSetting mailSetting, ProveedorCustom proveedor, Licitacion licitacion, UserSession userSession) {
//
//        Mail mail = new Mail();
//        VelocityContext context = new VelocityContext();
//
//        String nroLicitacionString = this.getNroLicitacionString(licitacion.getNroLicitacion(), licitacion.getAnioLicitacion());
//        context.put("nombreProveedor", proveedor.getRazonSocial());
//        context.put("nroLicitacion", nroLicitacionString);
//        context.put("nombreUsuarioAnulacion",licitacion.getUsuarioAnulacionId());
//        context.put("emailUsuarioAnulacion", userSession.getMail());
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
//            String asunto = "Anulacion Licitacion Nro. " + nroLicitacionString;
//
//            mail.enviar(proveedor.getEmail(), null, asunto, content);
//
//        } catch (EmailException ex) {
//            logger.error("Error al enviar notificacion", ex);
//        }
//    }


    public String getNroLicitacionString(Integer numero, Integer annio){
        String texto = ("00000000" + numero);
        String nroLicitacionString = annio + texto.substring(texto.length() - 8, texto.length());

        return nroLicitacionString;
    }

    public String enviar(MailSetting mailSetting, ProveedorCustom proveedor, Licitacion licitacion, UserSession userSession) {
        String rucProveedor = proveedor.getRuc();
        String nombreProveedor = proveedor.getRazonSocial();
        String numeroLicitacionString = this.getNroLicitacionString(licitacion.getNroLicitacion(), licitacion.getAnioLicitacion());
        String emailUsuarioAnulacion = "";
        String asunto = "Anular negociación nro. " + numeroLicitacionString;
        String motivo = "";
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
//        body = body + "Estimado/a " + nombreProveedor + "&#44;</h1>";
//        body = body + "<p style='font-family: Arial;font-size: 14px;line-height: 18px'>";
//        body = body + "Se le informa que la Licitación Nro. " + numeroLicitacionString + " ha sido anulada&#44;<br/>";
//        body = body + "para mayor detalle por favor contactarse a " + emailUsuarioAnulacion + ".";
//        body = body + "</p>";
//        body = body + "<p style='font-family: Arial;font-size: 14px;line-height: 18px'>";
//        body = body + "Agradecemos su participación.";
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
//        body = body + "<img align='LEFT' width='148' height='46' src='cid:"+cid+"' />";
//        body = body + "</p>";
//        body = body + "</td>";
//        body = body + "</tr>";
//        body = body + "</table>";
//
//        body = body + "</body>";
//        body = body + "</html>";

        //////////////////////  END  METODO VELPA //////////////////////

        if(userSession.getMail() != null){
            emailUsuarioAnulacion = "Para mayor detalle por favor contactarse a ("+userSession.getMail()+").";
        }
        if(licitacion.getComentarioAnulacion() != null){
            motivo = "Por el siguiente motivo: " + licitacion.getComentarioAnulacion() + ".";
        }
        try {
            VelocityContext context = new VelocityContext();

            String LOGO_H = generateCidResourceUtilsNuevo(htmlMail, LOGO_HEADER,"header");

//            String LOGO_CAL = generateCidResourceUtilsNuevo(htmlMail, LOGO_CALL,"call");

            String LOGO_FOOT = generateCidResourceUtilsNuevo(htmlMail, LOGO_FOOTER,"footer");


            context.put("nombreProveedor", nombreProveedor);
            context.put("nombreLicitacion", numeroLicitacionString);
            context.put("emailUsuarioAnulacion", emailUsuarioAnulacion);
            context.put("motivo", motivo);
            //context.put("vendor_header", LOGO_H);
            context.put("vendor_footer", LOGO_FOOT);

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
            htmlMail.setSubject(asunto);
            htmlMail.setHtmlMsg(content);
            htmlMail.setCharset("UTF-8");
            htmlMail.setStartTLSEnabled(true);
            htmlMail.setDebug(true);
            htmlMail.setTLS(true);

            htmlMail.send();
            respuesta = "Correo LicitacionAnularNotificacion enviado";
            //this.enviarCorreoSMTP(proveedor.getEmail(), asunto, body,mailSetting);
        } catch (Exception ex) {
            respuesta = ex.getMessage();
            logger.error("Error al enviar el correo por LicitacionAnularNotificacion al proveedor " + nombreProveedor + " con RUC " + rucProveedor + " por la licitacion " + numeroLicitacionString, ex);
        }
        return respuesta;
    }
}
