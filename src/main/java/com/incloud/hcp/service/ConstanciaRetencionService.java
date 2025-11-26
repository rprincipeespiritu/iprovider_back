package com.incloud.hcp.service;

import com.incloud.hcp.dto.ConstanciaRetencionDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



import java.util.List;

@Service
public interface ConstanciaRetencionService {

    String saveConstanciaRetenciones(MultipartFile file);

    String enviarCorreoConstanciaRetencion();

    List<ConstanciaRetencionDto> obtenerListaRetenciones(
            String nombreProveedor,
            String rucProveedor,
            String fechaInicio,
            String fechaFin,
            String nroRetencion
    );
}
