package com.incloud.hcp.service;

import com.incloud.hcp.dto.ConstanciaDetraccionDto;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ConstanciaDetraccionService {

    public String saveConstanciaDetraccion(MultipartFile file) ;

    public List<ConstanciaDetraccionDto> getListaDetracciones(String razonSocial,
                                                              String fechaInicio,
                                                              String fechaFin,
                                                              String nroComprobante,
                                                              String ruc);


}
