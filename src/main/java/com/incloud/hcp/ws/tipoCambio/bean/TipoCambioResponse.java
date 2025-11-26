package com.incloud.hcp.ws.tipoCambio.bean;

import java.io.Serializable;

public class TipoCambioResponse implements Serializable {

    private String message;
    private String status;
    private String origen;
    private ObtenerTipoCambioBodyResponse body;

    private String messageException;
    private String messageCause;
    private Throwable cause;

    public String getMessage() {
        return message;
    }

    public void setMessage(String mesagge) {
        this.message = mesagge;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public ObtenerTipoCambioBodyResponse getBody() {
        return body;
    }

    public void setBody(ObtenerTipoCambioBodyResponse body) {
        this.body = body;
    }

    public String getMessageException() {
        return messageException;
    }

    public void setMessageException(String messageException) {
        this.messageException = messageException;
    }

    public String getMessageCause() {
        return messageCause;
    }

    public void setMessageCause(String messageCause) {
        this.messageCause = messageCause;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "TipoCambioResponse{" +
                "mesagge='" + message + '\'' +
                ", status='" + status + '\'' +
                ", origen='" + origen + '\'' +
                ", body=" + body +
                ", messageException='" + messageException + '\'' +
                ", messageCause='" + messageCause + '\'' +
                ", cause=" + cause +
                '}';
    }
}
