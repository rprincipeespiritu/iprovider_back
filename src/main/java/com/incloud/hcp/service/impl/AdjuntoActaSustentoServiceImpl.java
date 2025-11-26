package com.incloud.hcp.service.impl;


import com.incloud.hcp.domain.ActaSustentoDetalle;
import com.incloud.hcp.domain.AdjuntoActaSustento;
import com.incloud.hcp.repository.AdjuntoActaSustentoRepository;
import com.incloud.hcp.service.AdjuntoActaSustentoService;
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
public class AdjuntoActaSustentoServiceImpl extends BaseServiceImpl implements AdjuntoActaSustentoService {

    private static Logger logger = LoggerFactory.getLogger(AdjuntoActaSustentoServiceImpl.class);

    @Autowired
    private AdjuntoActaSustentoRepository adjuntoActaSustentoRepository;


    public Object findByIdActaSustento(Integer idActaSustento) throws Exception {
        //CONSULTAR ITEMS ACTA SUSTENTO POR ID_ACTA_SUSTENTO
        List<AdjuntoActaSustento> adjuntoActaSustentoList = null;
        try {
            adjuntoActaSustentoList = this.adjuntoActaSustentoRepository.findbyIdActaSustento(idActaSustento);
        }catch (Exception e){
            e.printStackTrace();
        }
        return adjuntoActaSustentoList;

    }
}
