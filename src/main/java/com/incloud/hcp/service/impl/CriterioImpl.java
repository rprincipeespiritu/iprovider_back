package com.incloud.hcp.service.impl;

import com.incloud.hcp.domain.Cargo;
import com.incloud.hcp.domain.Criterio;
import com.incloud.hcp.repository.CargoRepository;
import com.incloud.hcp.repository.CriterioRepository;
import com.incloud.hcp.service.CargoService;
import com.incloud.hcp.service.CriterioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(propagation= Propagation.REQUIRED, rollbackFor=Exception.class)
public class CriterioImpl implements CriterioService {

    @Autowired
    private CriterioRepository criterioRepository;

    public Object getCriterioByTipoCriterio(String tipoCriterio) throws Exception {

        List<Criterio> criterioList = this.criterioRepository.getCriterioByTipoCriterio(tipoCriterio);
        return criterioList;
    }
}
