package com.incloud.hcp.jco.materiales.service.impl;

import com.incloud.hcp.jco.materiales.dto.MaterialesRFCResponseDto;
import com.incloud.hcp.jco.materiales.service.JCOMaterialesService;
import com.incloud.hcp.jco.materiales.service.JCOMaterialesServiceNew;
import com.incloud.hcp.repository.TempBienServicioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class JCOMaterialesServiceImpl implements JCOMaterialesService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private JCOMaterialesServiceNew jcoMaterialesServiceNew;


    @Autowired
    private TempBienServicioRepository tempBienServicioRepository;

    public MaterialesRFCResponseDto actualizarMaterialesRFC(String fechaInicio, String fechaFin) throws Exception {
        logger.error("01 - actualizarMaterialesRFC - INI");
        MaterialesRFCResponseDto materialesRFCResponseDto = this.jcoMaterialesServiceNew.getListMaterialesRFC(fechaInicio, fechaFin);
        logger.error("02 - actualizarMaterialesRFC - FIN");
        this.tempBienServicioRepository.saveNeworUpdateActualizados();
        materialesRFCResponseDto.setListaBienServicio(null);
        logger.error("03 - actualizarMaterialesRFC - FIN");
        return materialesRFCResponseDto;
    }
}
