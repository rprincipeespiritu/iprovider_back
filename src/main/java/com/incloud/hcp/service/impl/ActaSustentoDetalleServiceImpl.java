package com.incloud.hcp.service.impl;


import com.incloud.hcp.domain.ActaSustentoDetalle;
import com.incloud.hcp.repository.ActaSustentoDetalleRepository;
import com.incloud.hcp.service.ActaSustentoDetalleService;
import com.incloud.hcp.service._framework.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(propagation= Propagation.REQUIRED, rollbackFor=Exception.class)
public class ActaSustentoDetalleServiceImpl extends BaseServiceImpl implements ActaSustentoDetalleService {

    private static Logger logger = LoggerFactory.getLogger(ActaSustentoDetalleServiceImpl.class);


    @Autowired
    private ActaSustentoDetalleRepository actaSustentoDetalleRepository;


    public Object findByIdActaSustento(Integer idActaSustento) throws Exception{

        //CONSULTAR ITEMS ACTA SUSTENTO POR ID_ACTA_SUSTENTO
        List<ActaSustentoDetalle>  actaSustentoDetalleList = null;
        try {
            actaSustentoDetalleList = this.actaSustentoDetalleRepository.findbyIdActaSustento(idActaSustento);
        }catch (Exception e){
            e.printStackTrace();
        }
        return actaSustentoDetalleList;
    }
}
