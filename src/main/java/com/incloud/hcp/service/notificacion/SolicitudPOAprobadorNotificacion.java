package com.incloud.hcp.service.notificacion;

import com.incloud.hcp.domain.AprobadorSolicitud;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.domain.SolicitudBlacklist;
import com.incloud.hcp.domain.Usuario;
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
 * Created by Administrador on 14/11/2017.
 */
@Component
public class SolicitudPOAprobadorNotificacion extends NotificarMail {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String LOGO = "com/incloud/hcp/image/sapCompanyLogo.png";
    private final String LOGO_VENDOR = "com/incloud/hcp/image/vendor_logo.png";
    private final String LOGO_SAN_MARTIN = "com/incloud/hcp/image/sm_logo.png";
    private final String TEMPLATE = "com/incloud/hcp/templates/portal/TmpSolicitudPOAprobador.html";

//    @Value("${sm.portal.url}")
    @Value("${cfg.portal.url}")
    private  String urlPortal;

    @Value("${cfg.notificacion.consulta.url}")
    private String urlConsulta;


    public String enviar(MailSetting mailSetting, AprobadorSolicitud aprobador, SolicitudBlacklist solicitud) {
        String respuesta = "";
        Mail mail = new Mail();
        VelocityContext context = new VelocityContext();

        Optional<Usuario> oUsuario = Optional.of(aprobador).map(AprobadorSolicitud::getUsuario);
        String name = oUsuario.map(usuario -> String.join(" ", usuario.getNombre(), usuario.getApellido()))
                .orElse("Desconocido");

        Optional<Proveedor> oProveedor = Optional.ofNullable(solicitud)
                .map(SolicitudBlacklist::getProveedor);

        String ruc = oProveedor.map(Proveedor::getRuc).orElse("Desconocido");

        context.put("nombreAprobador", name);
        context.put("url",this.urlPortal);
        context.put("nombreProveedor", oProveedor.map(Proveedor::getRazonSocial).orElse("Desconocido"));
        context.put("rucProveedor", ruc);

        Optional.ofNullable(generateCidResourceUtils(mail.getHtmlMail(), LOGO))
                .ifPresent(cid -> context.put("logo", cid));

        Optional.ofNullable(generateCidResourceUtils(mail.getHtmlMail(), LOGO_VENDOR))
                .ifPresent(cid -> context.put("vendor_logo", cid));

        Optional.ofNullable(generateCidResourceUtils(mail.getHtmlMail(), LOGO_SAN_MARTIN))
                .ifPresent(cid -> context.put("sm_logo", cid));

        String content = Optional.ofNullable(TEMPLATE)
                .map(url -> url + "")
                .map(template -> {
                    int i = 0;
                    return getContentMail(context, template);
                })
                .orElse("");
        mail.setMailSetting(mailSetting);
        try {
            if (oUsuario.isPresent()) {
                String asunto = Optional.ofNullable(solicitud).map(SolicitudBlacklist::getCriteriosBlacklist)
                        .map(criterio -> criterio.getTipoSolicitudBlacklist())
                        .map(tipo -> tipo.getDescripcion())
                        .map(desc -> String.join(" ", "IProvider", "-", desc, ":", ruc))
                        .orElse("Sin asunto");

                HtmlEmail htmlMail = new HtmlEmail();
                htmlMail.setHostName(mailSetting.getHost());
                htmlMail.setSmtpPort(Integer.parseInt(mailSetting.getPort()));
                htmlMail.setAuthenticator(new DefaultAuthenticator(mailSetting.getUser(), mailSetting.getPassword()));
                htmlMail.addTo(oUsuario.get().getEmail());
                htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
                htmlMail.setSubject(asunto);
                htmlMail.setHtmlMsg(content);
                htmlMail.setCharset("UTF-8");
                htmlMail.setStartTLSEnabled(true);
                htmlMail.setDebug(true);
                htmlMail.setTLS(true);

                htmlMail.send();
                respuesta = "Correo TmpSolicitudPOAprobador enviado";
                //mail.enviar(oUsuario.get().getEmail(), null, asunto, content);
            }

        } catch (EmailException ex) {
            respuesta = ex.getMessage();
            logger.error("Error al enviar notificacion al usuario aprobador ", ex);
        }
        return respuesta;
    }
}
