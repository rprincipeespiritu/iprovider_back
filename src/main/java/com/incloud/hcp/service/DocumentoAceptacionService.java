package com.incloud.hcp.service;
import  com.incloud.hcp.domain.DocumentoAceptacion;
import com.incloud.hcp.domain.DocumentoAceptacionDetalle;
import com.incloud.hcp.domain.MtrTipoDocumento;
import com.incloud.hcp.dto.DocumentoAceptacionEntradaDto;
import com.incloud.hcp.pdf.bean.ParameterConformidadServicioPdfDTO;
import com.incloud.hcp.pdf.bean.ParameterEntradaMercaderiaPdfDTO;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DocumentoAceptacionService {

    List<DocumentoAceptacion> getAllDocumentoAceptacion();

    DocumentoAceptacion getDocumentoAceptacionbyId(Integer idTipoDocumentoAceptacion, Integer idEntregaMercaderia);

    List<DocumentoAceptacion> getDocumentoAceptacionPorFechasAndRuc(Date fechaInicio, Date fechaFin, String email);
    List<DocumentoAceptacion> getDocumentoAceptacionList(DocumentoAceptacionEntradaDto bean) throws Exception;

    void extraerDocumentoAceptacionMasivoByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin, boolean aprobarOrdenCompra, boolean enviarCorreoAprobacion);

    String extraerDocumentoAceptacionByNumOrdenCompraAndNumDocAceptacion(String numeroOrdenCompra, String numeroDocumentoAceptacion, boolean aprobarOrdenCompra, boolean enviarCorreoAprobacion);

    String getEntregaMercaderiaGenerateContent(ParameterEntradaMercaderiaPdfDTO parameterEntradaMercaderiaPdfDTO);

    String getDevolucionesGenerateContent(ParameterEntradaMercaderiaPdfDTO parameterEntradaMercaderiaPdfDTO);

    String getConformidadServicioGenerateContent(ParameterConformidadServicioPdfDTO parameterConformidadServicioPdfDTO);

    String getConformidadServicioGenerateContent(DocumentoAceptacion documentoAceptacion, List<DocumentoAceptacionDetalle> documentoAceptacionDetalle);

    Object getByActaSustento(Integer idActaSustento);
    List <MtrTipoDocumento> getByArchivoActaSustento(Integer idProveedor);

    Optional<DocumentoAceptacion> getDocumentoAceptacionId(Integer idDocumentoAceptacion);
}
