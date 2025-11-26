package com.incloud.hcp.service.cmiscf.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CmisFile {
    private String id;
    private String name;
    private String url;
    private CmisFile parent;
    private String descripcion;
    private String idFolder;

    private String extension;

    private String type;
    private String typeTika;
    private String documentNameCmis;
    private String folderNameCmis;
    private String size;

    private String esEstandar;
    private String tipoEstandar;

    private String usuario;
}