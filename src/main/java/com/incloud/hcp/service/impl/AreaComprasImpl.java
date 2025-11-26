package com.incloud.hcp.service.impl;

import com.incloud.hcp.domain.AreaCompras;
import com.incloud.hcp.repository.AreaComprasRepository;
import com.incloud.hcp.service.AreaComprasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(propagation= Propagation.REQUIRED, rollbackFor=Exception.class)
public class AreaComprasImpl implements AreaComprasService {
    @Autowired
    AreaComprasRepository areaComprasRepository;

    @Transactional(readOnly = true)
    public List<AreaCompras> getListAll() {
    return this.areaComprasRepository.findAll();
}

    public List<AreaCompras> getAllAreas() {
        return areaComprasRepository.findAllByOrderByCodigoAsc();
    }
}
