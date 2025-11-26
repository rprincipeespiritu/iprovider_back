package com.incloud.hcp.service.cmiscf.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileCmisDto implements Serializable {
    //@JsonProperty(value = "")
    private String objectId;
    private Integer idAdjunto;
    private String arcTituloCrea;
    private String arcTituloModi;
    private String arcMime;
    private String arcExtencion;
    private Integer arcTamano;
    private String cmisFolderId;
    private String cmisFolderNombre;
    private String cmisArchivoNombre;
    private String cmisArchivoRoot;
    private String token;
}
