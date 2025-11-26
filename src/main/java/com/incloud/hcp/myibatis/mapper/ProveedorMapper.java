package com.incloud.hcp.myibatis.mapper;

import com.incloud.hcp.bean.ProveedorCustom;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.dto.CuentaBancariaInDto;
import com.incloud.hcp.dto.ProveedorInDto;
import com.incloud.hcp.dto.ProveedorOutDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 06/09/2017.
 */
@Mapper
@Repository
public interface ProveedorMapper {

    List<ProveedorCustom> devuelveProveedor(String emailProveedor);

    List<ProveedorCustom> getListProveedorByRuc(String ruc);

    List<ProveedorCustom> getListProveedorByFiltro(
            @Param("idsPais") ArrayList<String> idsPais,
            @Param("razonSocial")String razonSocial,
            @Param("idsRegion") ArrayList<String> idsRegion,
            @Param("idsProvincia") ArrayList<String> idsProvincia,
            @Param("nroRuc") String nroRuc,
            @Param("tipoProveedor") String tipoProveedor,
            @Param("tipoPersona") String tipoPersona,
            @Param("indHomologado") String indHomologado,
            @Param("marca") String marca,
            @Param("producto") String producto,
            @Param("descripcionAdicional") String descripcionAdicional,
            @Param("idsLinea") ArrayList<String> idsLinea,
            @Param("idsFamilia") ArrayList<String> idsFamilia,
            @Param("idsSubFamilia") ArrayList<String> idsSubFamilia,
            @Param("estadoProveedor") String estadoProveedor,
            @Param("idAreaCompra") Integer idAreaCompra
    );

    List<ProveedorCustom> getListProveedorByFiltroPaginado(
            @Param("idsPais") ArrayList<String> idsPais,
            @Param("razonSocial")String razonSocial,
            @Param("idsRegion") ArrayList<String> idsRegion,
            @Param("idsProvincia") ArrayList<String> idsProvincia,
            @Param("nroRuc") String nroRuc,
            @Param("tipoProveedor") String tipoProveedor,
            @Param("tipoPersona") String tipoPersona,
            @Param("indHomologado") String indHomologado,
            @Param("marca") String marca,
            @Param("producto") String producto,
            @Param("descripcionAdicional") String descripcionAdicional,
            @Param("idsLinea") ArrayList<String> idsLinea,
            @Param("idsFamilia") ArrayList<String> idsFamilia,
            @Param("idsSubFamilia") ArrayList<String> idsSubFamilia,
            @Param("estadoProveedor") String estadoProveedor,
            @Param("nroRegistros") Integer nroRegistros,
            @Param("paginaMostrar") Integer paginaMostrar
    );

    List<ProveedorCustom> getListProveedorMigrados();

    List<ProveedorCustom> getListProveedorByFiltroValidacion(
            @Param("idsPais") ArrayList<String> idsPais,
            @Param("idsRegion") ArrayList<String> idsRegion,
            @Param("idsProvincia") ArrayList<String> idsProvincia,
            @Param("nroRuc") String nroRuc,
            @Param("razonSocial") String razonSocial,
            @Param("tipoProveedor") String tipoProveedor,
            @Param("tipoPersona") String tipoPersona,
            @Param("indHomologado") String indHomologado,
            @Param("marca") String marca,
            @Param("producto") String producto,
            @Param("descripcionAdicional") String descripcionAdicional,
            @Param("idsLinea") ArrayList<String> idsLinea,
            @Param("idsFamilia") ArrayList<String> idsFamilia,
            @Param("idsSubFamilia") ArrayList<String> idsSubFamilia,
            @Param("estadoProveedor") String estadoProveedor,
            @Param("idAreaCompra") Integer idAreaCompra
    );

    List<ProveedorCustom> getListProveedorByFiltroLicitacion(
            @Param("idsPais") ArrayList<String> idsPais,
            @Param("razonSocial")String razonSocial,
            @Param("idsRegion") ArrayList<String> idsRegion,
            @Param("idsProvincia") ArrayList<String> idsProvincia,
            @Param("nroRuc") String nroRuc,
            @Param("tipoProveedor") String tipoProveedor,
            @Param("tipoPersona") String tipoPersona,
            @Param("indHomologado") String indHomologado,
            @Param("marca") String marca,
            @Param("producto") String producto,
            @Param("descripcionAdicional") String descripcionAdicional,
            @Param("idsLinea") ArrayList<String> idsLinea,
            @Param("idsFamilia") ArrayList<String> idsFamilia,
            @Param("idsSubFamilia") ArrayList<String> idsSubFamilia
    );

    List<ProveedorCustom> getListProveedorByFiltroLicitacionPaginado(
            @Param("idsPais") ArrayList<String> idsPais,
            @Param("razonSocial")String razonSocial,
            @Param("idsRegion") ArrayList<String> idsRegion,
            @Param("idsProvincia") ArrayList<String> idsProvincia,
            @Param("nroRuc") String nroRuc,
            @Param("tipoProveedor") String tipoProveedor,
            @Param("tipoPersona") String tipoPersona,
            @Param("indHomologado") String indHomologado,
            @Param("marca") String marca,
            @Param("producto") String producto,
            @Param("descripcionAdicional") String descripcionAdicional,
            @Param("idsLinea") ArrayList<String> idsLinea,
            @Param("idsFamilia") ArrayList<String> idsFamilia,
            @Param("idsSubFamilia") ArrayList<String> idsSubFamilia,
            @Param("nroRegistros") Integer nroRegistros,
            @Param("paginaMostrar") Integer paginaMostrar
    );

    //Crea Proveedor de ProveedorServiceImpl
    void getCreaProveedor(ProveedorInDto bean);

    //Actualiza Proveedor de ProveedorServiceImpl
    void getActualizaProveedor(ProveedorInDto bean);

    //Busca Proveedor de ProveedorServiceImpl
    ProveedorOutDto validarProveedorCorreo(String correo);

    void updateEstadoProveedor(@Param("idProveedor") Integer idProveedor,
                               @Param("idEstadoProveedor") Integer idEstadoProveedor);

}
