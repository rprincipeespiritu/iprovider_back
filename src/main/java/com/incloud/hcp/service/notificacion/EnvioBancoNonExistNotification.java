package com.incloud.hcp.service.notificacion;

import com.incloud.hcp.dto.NonExistedBankRequest;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EnvioBancoNonExistNotification extends NotificarMail{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String LOGO = "com/incloud/hcp/image/sapCompanyLogo.png";
    private final String LOGO_HEADER = "com/incloud/hcp/templates/img/header.png";
    private final String LOGO_CALL = "com/incloud/hcp/templates/img/call-to-action.png";
    private final String LOGO_FOOTER = "com/incloud/hcp/templates/img/jrclogo.jpg";
    private final String TEMPLATE_NO_BANCO = "com/incloud/hcp/templates/portal/TmpNoExistBank.html";
    private final String ASUNTO_BANCO = "Banco no existente";

    @Value("${cfg.portal.url}")
    private  String urlPortal;

    @Value("${cfg.portal.url.activation}")
    private  String urlPortalSap;

    @Value("${cfg.notificacion.consulta.url}")
    private String urlConsulta;


    public String enviarBankNonExist(MailSetting mailSetting, NonExistedBankRequest nonExistedBankRequest, String mail){


        HtmlEmail htmlMail = new HtmlEmail();
        String respuesta = "";
        String mailCc=nonExistedBankRequest.getMailValorCc();
        try {
            VelocityContext context = new VelocityContext();

            String LOGO_H = generateCidResourceUtilsNuevo(htmlMail, LOGO_HEADER,"header");

            String LOGO_CAL = generateCidResourceUtilsNuevo(htmlMail, LOGO_CALL,"call");

            String LOGO_FOOT = generateCidResourceUtilsNuevo(htmlMail, LOGO_FOOTER,"footer");


            context.put("contacto", nonExistedBankRequest.getNombreProveedor());
            //context.put("vendor_header", LOGO_H);
            //context.put("vendor_call_action",LOGO_CAL );
            context.put("vendor_footer", LOGO_FOOT);
            context.put("bank_name", nonExistedBankRequest.getNombreBanco());
            context.put("bank_dir", nonExistedBankRequest.getDireccionBanco());
            context.put("bank_city", nonExistedBankRequest.getCiudadBanco());
            context.put("bank_country", nonExistedBankRequest.getPaisBanco());
            context.put("bank_swift", nonExistedBankRequest.getSwift());

            String content = Optional.ofNullable(TEMPLATE_NO_BANCO)
                    .map(url -> url + "")
                    .map(template -> {
                        int i = 0;
                        return getContentMail(context, template);
                    })
                    .orElse("");


            htmlMail.setHostName(mailSetting.getHost());
            htmlMail.setSmtpPort(Integer.parseInt(mailSetting.getPort()));
            htmlMail.setAuthenticator(new DefaultAuthenticator(mailSetting.getUser(), mailSetting.getPassword()));
            htmlMail.addTo(mail);
            htmlMail.addCc(mailCc);
            htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
            htmlMail.setSubject(ASUNTO_BANCO);
            htmlMail.setHtmlMsg(content);
            htmlMail.setCharset("UTF-8");
            htmlMail.setStartTLSEnabled(true);
            htmlMail.setDebug(true);
            htmlMail.setTLS(true);

            htmlMail.send();
            respuesta = "Correo Banco no existente notificación enviada.";
        } catch (Exception ex) {
            respuesta = ex.getMessage();
            logger.error("Error al enviar Banco no existente. ", ex);
        }
        return respuesta;
    }

}
