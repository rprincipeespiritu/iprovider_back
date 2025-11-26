package com.incloud.hcp.jco.emHes.service;

import com.incloud.hcp.domain.DocumentoAceptacion;
import com.incloud.hcp.domain.DocumentoAceptacionDetalle;
import com.incloud.hcp.domain.RubroBien;

import java.util.List;

public interface JCOEmHesService {

    DocumentoAceptacion crearEm(DocumentoAceptacion documentoAceptacion) throws Exception;

    DocumentoAceptacion crearHes(DocumentoAceptacion documentoAceptacionHes) throws Exception;
}
