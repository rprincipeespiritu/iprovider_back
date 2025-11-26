package com.incloud.hcp.facade;

import com.incloud.hcp.bean.UserSessionFront;
import com.incloud.hcp.domain.PreRegistroProveedor;
import com.incloud.hcp.dto.PreRegistroProveedorDto;

import java.util.List;


public interface PreRegistroFacade {
    PreRegistroProveedorDto save(PreRegistroProveedorDto preRegistro) throws Exception;

    PreRegistroProveedorDto updateSearchSunat(PreRegistroProveedorDto dto);

    PreRegistroProveedorDto getPreRegistroByEmail(String email);

    PreRegistroProveedorDto getPreRegistroByIdHcp(String idHcp);

    PreRegistroProveedorDto getPreRegistroById(Integer id);

    List<PreRegistroProveedorDto> getListSolicitudPendiente();

    List<PreRegistroProveedorDto> getListSolicitudPendiente(String idHcp,String idAreaCompra);

    PreRegistroProveedorDto aprobarSolicitud(Integer idPreRegistro);

    PreRegistroProveedorDto reprobarSolicitud(Integer idPreRegistro, String rechazoAC, String mensaje);

    List<PreRegistroProveedor> getValidarPreRegistro(UserSessionFront userSessionFront);
}
