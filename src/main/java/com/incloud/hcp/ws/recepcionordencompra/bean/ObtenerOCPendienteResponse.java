package com.incloud.hcp.ws.recepcionordencompra.bean;

import java.io.Serializable;
import java.util.List;

public class ObtenerOCPendienteResponse implements Serializable {

    // GS
    private String status;
    private String message;
    private String origen;
    private List<ObtenerOCPendienteBodyResponse> body;

    //
    private String messageException;           // Exception > getMessage()
    private String messageCause;
    private Throwable cause;                  // Exception > getCause()
    //private ProveedorDto proveedorDto;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public List<ObtenerOCPendienteBodyResponse> getBody() {
        return body;
    }

    public void setBody(List<ObtenerOCPendienteBodyResponse> body) {
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
        return "ObtenerOCPendienteResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", origen='" + origen + '\'' +
                ", body=" + body +
                ", messageException='" + messageException + '\'' +
                ", messageCause='" + messageCause + '\'' +
                '}';
    }
}
