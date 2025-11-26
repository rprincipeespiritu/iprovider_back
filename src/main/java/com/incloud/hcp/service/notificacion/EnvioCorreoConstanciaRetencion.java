package com.incloud.hcp.service.notificacion;

import com.incloud.hcp.dto.ConstanciaDetraccionProveedorDto;
import com.incloud.hcp.dto.ConstanciaRetencionProveedorDto;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EnvioCorreoConstanciaRetencion extends NotificarMail {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String LOGO = "com/incloud/hcp/templates/img/call-to-action.png"; //por arreglar

    private final String TEMPLATE = "com/incloud/hcp/templates/portal/TmpEnvioCorreoConstanciaRetencion.html";

    //    @Value("${sm.portal.url}")
    @Value("${cfg.portal.url}")
    private  String urlPortal;

    @Value("${cfg.notificacion.consulta.url}")
    private String urlConsulta;

    public String enviar(MailSetting mailSetting, ConstanciaRetencionProveedorDto dataProveedor) {
        String nroRetencion = dataProveedor.getNroRetencion();
        String asunto = "PUBLICACIÓN COMPROBANTE DE RETENCIÓN";
        String razonSocialProveedor = dataProveedor.getRazonSocialProveedor();
        HtmlEmail htmlMail = new HtmlEmail();
        String respuesta = "";

        try {


            VelocityContext context = new VelocityContext();


            String LOGO_JRC = generateCidResourceUtilsNuevo(htmlMail, LOGO,"footer");

            context.put("razonSocial", razonSocialProveedor);
            context.put("jrc_logo",LOGO_JRC );
            context.put("urlportal", urlPortal);
            context.put("nroRetencion", nroRetencion);

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
            htmlMail.addTo(dataProveedor.getEmailProveedor());
            htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
            htmlMail.setSubject(asunto);
            htmlMail.setHtmlMsg(content);
            htmlMail.setCharset("UTF-8");
            htmlMail.setStartTLSEnabled(true);
            htmlMail.setDebug(true);
            htmlMail.setTLS(true);
            respuesta = "correo EnvioCorreoConstanciaRetencion enviado";

            //htmlMail.send();
            enviarCorreoSMTP(dataProveedor.getEmailProveedor(), asunto, content, mailSetting );
            //this.enviarCorreoSMTP(proveedor.getEmail(), asunto, body,mailSetting);
        } catch (Exception ex) {
            logger.error("Error al enviar el correo por por EnvioCorreoConstanciaRetencion al proveedor " + dataProveedor.getRazonSocialProveedor() + " con la constancia de detraccion " + dataProveedor.getNroRetencion(), ex);
            respuesta = ex.getMessage();
        }
        return respuesta;
    }

}
