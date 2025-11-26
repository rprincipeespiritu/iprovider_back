package com.incloud.hcp.service.notificacion;

import com.incloud.hcp.domain.Prefactura;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.domain.Usuario;
import com.incloud.hcp.enums.PrefacturaEstadoEnum;
import com.incloud.hcp.util.StrUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ComprobanteFactoringNotificacion extends NotificarMail {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String LOGO_HEADER = "com/incloud/hcp/templates/img/header.png";
    private final String LOGO_FOOTER = "com/incloud/hcp/templates/img/jrclogo.jpg";
    private final String TEMPLATE = "com/incloud/hcp/templates/portal/TmpComprobanteFactoring.html";

    @Value("${cfg.portal.url}")
    private  String urlPortal;

    public String enviar(MailSetting mailSetting, Proveedor proveedor, String referencia){
        String asunto = "Registrada con el N° " + referencia;
        HtmlEmail htmlMail = new HtmlEmail();
        String respuesta = "";
        try {

            VelocityContext context = new VelocityContext();

            String LOGO_H = generateCidResourceUtilsNuevo(htmlMail, LOGO_HEADER,"header");

            String LOGO_FOOT = generateCidResourceUtilsNuevo(htmlMail, LOGO_FOOTER,"footer");

            context.put("nombreProveedor", proveedor.getRazonSocial());
            //context.put("vendor_header", LOGO_H);
            context.put("vendor_footer", LOGO_FOOT);
//            context.put("urlportal", urlPortal);
            context.put("referencia", referencia);

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
            respuesta = "Correo ProveedorPotencialAprobadoNotificacion enviado";
            //this.enviarCorreoSMTP(proveedor.getEmail(), ASUNTO, body, mailSetting);
        } catch (Exception ex) {
            ex.printStackTrace();
            respuesta = ex.getMessage();
            logger.error("Error al enviar el correo por ProveedorPotencialAprobadoNotificacion al proveedor", ex);
        }
        return respuesta;
    }
}
