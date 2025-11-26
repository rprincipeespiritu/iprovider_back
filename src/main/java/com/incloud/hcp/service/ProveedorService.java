package com.incloud.hcp.service;

import com.incloud.hcp.bean.ProveedorCustom;
import com.incloud.hcp.bean.ProveedorFiltro;
import com.incloud.hcp.bean.UserSession;
import com.incloud.hcp.domain.*;
import com.incloud.hcp.dto.*;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.rest.bean.ProveedorDatosGeneralesDTO;
import com.incloud.hcp.service.cmiscf.bean.CmisFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Administrador on 29/08/2017.
 */
public interface ProveedorService {
    
    void saveProveedorSHana();

    void eliminarDatosProveedor(Integer idProveedor);

    Proveedor getProveedorById(Integer idProveedor);

    Proveedor getProveedorByIdHcp(String idHcp) throws PortalException;

    Proveedor getProveedorByRuc(String ruc);
    ProveedorDto getProveedorDtoByRuc(String ruc);
    ProveedorDto getProveedorDtoByRucResponder(String ruc);
    ProveedorDto getProveedorDtoByEmailResponder(String email);
    Proveedor getProveedorDtoByEmail(String email);

    Proveedor save(Proveedor documento);

    ProveedorDto getProveedorDtoByIdHcp(String idHcp) throws PortalException;

    ProveedorDto getProveedorByAcreedorCodigoSap(String codigoSAP) throws PortalException;

    ProveedorDto getProveedorDtoById(Integer idProveedor) throws PortalException;

    ProveedorDto saveDto(ProveedorDto dto) throws Exception;

    ProveedorDto toDto(Proveedor proveedor) throws PortalException;

    List<LineaComercialDto> getListLineaComercialByIdProveedor(Integer idProveedor);

    List<ProveedorCatalogoDto> getListCatalogosByIdProveedor(Integer idProveedor);

    List<Proveedor> getListProveedor();

    List<ProveedorCustom> getListProveedorByFiltro(ProveedorFiltro filtro);
    List<ProveedorCustom> getListProveedorByFiltroPaginado(ProveedorFiltro filtro);
    List<ProveedorCustom> getListProveedorByFiltroValidacion(UserSession userSession, ProveedorFiltro filtro);
    List<ProveedorCustom> getListProveedorByFiltroLicitacion(ProveedorFiltro filtro);
    List<ProveedorCustom> getListProveedorByFiltroLicitacionPaginado(ProveedorFiltro filtro);

    List<ProveedorCustom> getListProveedorByRuc(String ruc);

    List<ProveedorCustom> getListProveedorSinHcpID();

    public List<ProveedorDatosGeneralesDTO> getProveedorDatosGenerales(
            String fechaCreacionIni, String fechaCreacionFin) throws PortalException;

    ProveedorDto sendToSap(Integer idProveedor) throws PortalException;

    void evaluarDataMaestra(Integer idProveedor) throws PortalException ;
    void rechazarDataMaestra(Integer idProveedor,String motivo) throws PortalException ;
    Integer updateProveedorIDHCP(ListProveedorHCP listProveedorHCP);
    //carga Excel para la creacion en la BD
    public List<ProveedorXLSXDTO> uploadExcel(InputStream in);

    String saveEmail(String ruc, String email);

    public List<ProveedorCustom> devuelveProveedor(String emailProveedor) throws  PortalException;

    public ProveedorOutDto inspeccionProveedor(ProveedorInDto bean) throws Exception;

    public ProveedorOutDto inspeccionProveedorParcial(ProveedorInDto bean) throws Exception;

    String getTipoDocumento(Integer idProveedor) throws Exception;

    byte[] docxToPdf(InputStream docxStream) throws Exception;

    ProveedorAdjuntoCuentaBancaria uploadAdjuntoCuentaBancaria (Integer idProveedor, MultipartFile file) throws Exception;
    
    List<FacturaSapDto> getEstadoCuentaProveedor(String email) throws Exception;
    
    List<ProveedorAdjuntoCuentaBancaria> getListAdjuntoCuentaBancariaByProveedor (Integer idProveedor) throws Exception;

    XWPFDocument getTipoDocumentoPJ(Integer idProveedor) throws Exception;

    String getTipoDocumentoPJHTML(Integer idProveedor) throws Exception;

    List<ProveedorAdjuntoSunat> guardarAdjuntoSunat(Integer proveedor, List<ProveedorAdjuntoSunatDto> listAdjunto, String operacion) throws Exception;
    List<ProveedorAdjuntoSunat> eliminarAdjunto (Integer idProveedor,String idAdjunto);
    List<ProveedorCatalogo> eliminarAdjuntoCatalogo (Integer idProveedor, String idAdjunto);

    ProveedorDto inspeccionProveedorCompleto(ProveedorDto bean) throws Exception;

   List<Proveedor> insertMigrProveedortoProveedor(InputStream inputStream);
   void extraerProveedorSap() throws Exception;

}
