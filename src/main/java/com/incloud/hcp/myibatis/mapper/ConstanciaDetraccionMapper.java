package com.incloud.hcp.myibatis.mapper;

import com.incloud.hcp.dto.ConstanciaDetraccionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
@Repository
public interface ConstanciaDetraccionMapper {

    List<ConstanciaDetraccionDto> getListaDetracciones(
            @Param("razonSocial") String razonSocial,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("nroComprobante") String nroComprobante,
            @Param("ruc") String ruc
    );
}
