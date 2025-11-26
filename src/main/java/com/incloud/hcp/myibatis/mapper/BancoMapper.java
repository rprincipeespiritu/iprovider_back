package com.incloud.hcp.myibatis.mapper;

import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.domain.Ubigeo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrador on 21/08/2017.
 */
@Mapper
@Repository
public interface BancoMapper {



    Banco getBancoByCodigo(String codigo);

    Integer getIdSequence();

    Integer existeBanco(String codigoBanco);

    void insertBancos(Banco bean);

}
