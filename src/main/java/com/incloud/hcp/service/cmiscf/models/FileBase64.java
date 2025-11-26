package com.incloud.hcp.service.cmiscf.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FileBase64 {

    private String nombreDocumento;
    private String base64;
    private String tipo;
    private String extension;
    private String descripcion;

    //---------------------------------------------------------------------------------------------------
    private String enviar; //Utilizado como flag para identificar los adjuntos que se enviaran con el Mail
    private String esEstandar;
    private String tipoEstandar;
    private String usuario;
}
