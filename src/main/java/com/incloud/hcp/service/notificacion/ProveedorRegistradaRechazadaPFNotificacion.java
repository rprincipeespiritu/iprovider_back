package com.incloud.hcp.service.notificacion;

import com.incloud.hcp.domain.Prefactura;
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
public class ProveedorRegistradaRechazadaPFNotificacion extends NotificarMail {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    private static final String MENSAJE_RECHAZO = "Motivo: %s";
    private static final String ASUNTO = "Factura %s %s";
    private final String LOGO = "com/incloud/hcp/image/sapCompanyLogo.png";

    private final String LOGO_HEADER = "com/incloud/hcp/templates/img/header.png";
    private final String LOGO_CALL = "com/incloud/hcp/templates/img/call-to-action.png";
    private final String LOGO_FOOTER = "com/incloud/hcp/templates/img/jrclogo.jpg";
    private final String TEMPLATERECHAZO = "com/incloud/hcp/templates/portal/PreFacturaRechazada.html";
    private final String TEMPLATEAPROBADO = "com/incloud/hcp/templates/portal/PreFacturaAprobada.html";

    @Value("${cfg.portal.url}")
    private  String urlPortal;

    @Value("${cfg.notificacion.consulta.url}")
    private String urlConsulta;

//    @Value("${cfg.portal.url}")
//    private String urlPortal;



    public String enviar(MailSetting mailSetting,Prefactura prefactura, Usuario proveedor, Usuario aprobador){
//        Sociedad sociedad = prefactura.getSociedad();
        String accion = "Registrada";
        String mensajeEstado = "el registro";
        String textoAccion = "se encuentra Aprobada.";
        String textoFinal = "Puede visualizar el estado de la factura en la ventana 'Publicación de Comprobantes de Pago' del Portal iProvider";
        String referencia = prefactura.getReferencia();
//        String ruc = prefactura.getProveedorRuc();
        String motivo = "";
        String tipoDestinatario = "";
        String emailContacto = "";
        String nombreDestinatario = "";
//        String lineaNombreAprobador = "";
//        String lineaCorreoAprobador = "";

        HtmlEmail htmlMail = new HtmlEmail();
        String TEMPLATE_FINAL = "";
        String respuesta= "";



        ////////////////////// VARIABLES METODO VELPA //////////////////////

        if(proveedor != null){
            tipoDestinatario = "proveedor";
            emailContacto = proveedor.getEmail();
            nombreDestinatario = StrUtils.escapeComma(prefactura.getProveedorRazonSocial());
        } else {
            String mensaje = "Error en los datos de " + tipoDestinatario + " para el envio de correo por " + mensajeEstado + " de la prefactura: " + referencia;
            logger.error(mensaje);
            return mensaje;
        }

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
//        body = body + "Estimado/a Proveedor: " + nombreDestinatario + "<br/>";
//        body = body + "RUC " + ruc + "</h1>";
//        body = body + "<p style='font-family: Arial;font-size: 14px;line-height: 18px'>";
//        body = body + "La factura N° " + referencia + " " + textoAccion + "<br/> <br/>";
//        body = body + textoFinal;
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
//        body = body + lineaNombreAprobador;
//        body = body + "Area Contable<br/>";
//        body = body + "Topitop<br/>";
//        body = body + "<img align='LEFT' width='148' height='46' src='cid:' />";
//        body = body + lineaCorreoAprobador;
//        body = body + "</p>";
//        body = body + "</td>";
//        body = body + "</tr>";
//        body = body + "</table>";
//
//        body = body + "</body>";
//        body = body + "</html>";

        //////////////////////  END  METODO VELPA //////////////////////

        //////////////////////  END  METODO VELPA //////////////////////
        try {

            VelocityContext context = new VelocityContext();

            String LOGO_H = generateCidResourceUtilsNuevo(htmlMail, LOGO_HEADER,"header");

            String LOGO_CAL = generateCidResourceUtilsNuevo(htmlMail, LOGO_CALL,"call");

            String LOGO_FOOT = generateCidResourceUtilsNuevo(htmlMail, LOGO_FOOTER,"footer");

            context.put("contacto", nombreDestinatario);
            //context.put("vendor_header", LOGO_H);
            //context.put("vendor_call_action",LOGO_CAL );
            context.put("vendor_footer", LOGO_FOOT);
            context.put("urlportal", urlPortal);
            context.put("referencia", referencia);

            if (PrefacturaEstadoEnum.REGISTRADA.getId() != prefactura.getIdEstadoPrefactura().intValue()) {
                if (aprobador.getEmail() != null && !aprobador.getEmail().isEmpty()) {
                    //lineaNombreAprobador = aprobador.getNombre() + " " + aprobador.getApellido() + "<br/>";
                    //lineaCorreoAprobador = "<br/>" + aprobador.getEmail();
                    TEMPLATE_FINAL = TEMPLATEAPROBADO;
                }

                if (PrefacturaEstadoEnum.RECHAZADA.getId() == prefactura.getIdEstadoPrefactura().intValue()) {
                    accion = "Rechazada";
                    mensajeEstado = "el rechazo";
//                    textoAccion = "ha sido Rechazada por el siguiente motivo:";
//                    textoFinal = StrUtils.escapeComma(prefactura.getMotivoRechazo());
                    motivo = StrUtils.escapeComma(prefactura.getMotivoRechazo());
                    context.put("motivo", motivo);
                    TEMPLATE_FINAL = TEMPLATERECHAZO;
                }
                else if (PrefacturaEstadoEnum.ANULADA.getId() == prefactura.getIdEstadoPrefactura().intValue()) {
                    accion = "Anulada";
                    mensajeEstado = "la anulacion";
                    motivo = StrUtils.escapeComma(prefactura.getMotivoRechazo());
                    context.put("motivo", motivo);
//                    textoAccion = "ha sido Anulada por el siguiente motivo:";
//                    textoFinal = StrUtils.escapeComma(prefactura.getMotivoRechazo());
                    TEMPLATE_FINAL = TEMPLATERECHAZO;
                }
                context.put("textoFinal", textoFinal);
            } else {
                TEMPLATE_FINAL = TEMPLATEAPROBADO;
                context.put("textoAccion", textoAccion);
            }
//            context.put("ruc", ruc);

            String content = Optional.ofNullable(TEMPLATE_FINAL)
                    .map(url -> url + "")
                    .map(template -> {
                        int i = 0;
                        return getContentMail(context, template);
                    })
                    .orElse("");

            htmlMail.setHostName(mailSetting.getHost());
            htmlMail.setSmtpPort(Integer.parseInt(mailSetting.getPort()));
            htmlMail.setAuthenticator(new DefaultAuthenticator(mailSetting.getUser(), mailSetting.getPassword()));
            htmlMail.addTo(emailContacto);
            htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
            htmlMail.setSubject(accion);
            htmlMail.setHtmlMsg(content);
            htmlMail.setCharset("UTF-8");
            htmlMail.setStartTLSEnabled(true);
            htmlMail.setDebug(true);
            htmlMail.setTLS(true);

            htmlMail.send();
            respuesta = "Se envio correctamente el correo por " + mensajeEstado + " de la prefactura " + referencia + " al " + tipoDestinatario;
            //this.enviarCorreoSMTP(proveedor.getEmail(), ASUNTO, body, mailSetting);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error al enviar el correo por ProveedorPotencialAprobadoNotificacion al proveedor", ex);
            respuesta = ex.getMessage();
        }
        return respuesta;
    }
}