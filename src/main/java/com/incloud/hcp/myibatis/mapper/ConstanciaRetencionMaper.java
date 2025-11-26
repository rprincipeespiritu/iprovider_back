package com.incloud.hcp.myibatis.mapper;

import com.incloud.hcp.dto.ConstanciaRetencionDto;
import com.incloud.hcp.dto.ConstanciaRetencionProveedorDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Mapper
@Repository
public interface ConstanciaRetencionMaper {

    List<ConstanciaRetencionProveedorDto> obtenerListaProveedores();

    List<ConstanciaRetencionDto> obtenerDatosRetenciones(
            @Param("nombreProveedor") String nombreProveedor,
            @Param("rucProveedor") String rucProveedor,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("nroRetencion") String nroRetencion
    );
}
