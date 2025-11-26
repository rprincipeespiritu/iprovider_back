package com.incloud.hcp.jco.servicios.service.impl;

import com.incloud.hcp.jco.servicios.dto.ServiciosRFCResponseDto;
import com.incloud.hcp.jco.servicios.service.JCOServiciosService;
import com.incloud.hcp.jco.servicios.service.JCOServiciosServiceNew;
import com.incloud.hcp.repository.TempBienServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public class JCOServiciosServiceImpl implements JCOServiciosService {

    @Autowired
    private JCOServiciosServiceNew jcoServiciosServiceNew;

    @Autowired
    private TempBienServicioRepository tempBienServicioRepository;

    public ServiciosRFCResponseDto actualizarMaterialesRFC(String fechaInicio, String fechaFin) throws Exception {
        ServiciosRFCResponseDto serviciosRFCResponseDto = this.jcoServiciosServiceNew.getListServicios(fechaInicio, fechaFin);
        this.tempBienServicioRepository.saveNeworUpdateActualizados();
        return serviciosRFCResponseDto;
    }
}
