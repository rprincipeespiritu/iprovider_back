package com.incloud.hcp.myibatis.mapper;

import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.domain.Ubigeo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UnidadMedidaMapper {
    Integer getIdSequence();

}
