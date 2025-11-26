package com.incloud.hcp.service;

import com.incloud.hcp.bean.LicitacionRequest;
import com.incloud.hcp.domain.Licitacion;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Map;

/**
 * Created by USER on 29/08/2017.
 */
public interface LicitacionService {

    List<Licitacion> getListaLicitacionByFiltro(Map<String, Object> json);

    List<Licitacion> getListaLicitacionByFiltroPaginado(Map<String, Object> json);

    LicitacionRequest saveLicitacion(LicitacionRequest objLicitacion) throws Exception;

    LicitacionRequest getLicitacionById(Integer idLicitacion);

    String sendMail(Integer idLicitacion,String emailPublicacion);

    void updateLicitacionEstadoPorEvaluar();

    String anularLicitacion(Map<String, Object> json);

    public String republicarLicitacion(Map<String, Object> json);

    Workbook reporteLicitacion(Integer idLicitacion);


    /** JRAMOS - UPDATE*/
    String mailForwarding(List<Integer> proveedors, Integer idLicitacion, String cuerpo);

    Integer verificarEsLicitacionMaterialLibre(Integer idLicitacion);

    Licitacion getCerrarLicitacion(Map<String, Object> json) throws Exception;
}
