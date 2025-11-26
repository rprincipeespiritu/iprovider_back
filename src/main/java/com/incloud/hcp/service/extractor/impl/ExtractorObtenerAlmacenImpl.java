package com.incloud.hcp.service.extractor.impl;

import com.incloud.hcp.domain.CentroAlmacen;
import com.incloud.hcp.repository.CentroAlmacenRepository;
import com.incloud.hcp.service.extractor.ExtractorAlmacenService;
import com.incloud.hcp.ws.almacen.bean.ObtenerAlmacenBodyResponse;
import com.incloud.hcp.ws.almacen.bean.ObtenerAlmacenResponse;
import com.incloud.hcp.ws.almacen.service.GSAlmacenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExtractorObtenerAlmacenImpl implements ExtractorAlmacenService {

    @Autowired
    private GSAlmacenService gsAlmacenService;

    @Autowired
    private CentroAlmacenRepository centroAlmacenRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void obtenerAlmacen(int idEmpresa, String fechaInicial, String fechaFinal) {

        ObtenerAlmacenResponse response = gsAlmacenService.obtenerAlmacen(idEmpresa, fechaInicial, fechaFinal);


        logger.info("CONSULTO SERVICIO OBTENER ALMACEN: {}", response.getBody().size());

        if (response.getBody() != null) {
            List<ObtenerAlmacenBodyResponse> body = response.getBody();
            List<CentroAlmacen> centroAlmacen = new ArrayList<>();
            // Cabecera
            body.forEach(a -> {


                        CentroAlmacen c = new CentroAlmacen();
                        CentroAlmacen c1 = new CentroAlmacen();
                        c.setIdErp(a.getId());
                        c.setIdPadre(c.getIdCentroAlmacen());
                        c.setNivel(1);
                        c.setCodigoSap(""+a.getId());
                        c.setDenominacion(a.getDescripcion());
                        c.setPais(a.getPais());
                        c.setDepartamento(a.getDepartamento());
                        c.setDistrito(a.getDistrito());
                        c.setPoblacion(a.getProvincia());
                        c.setDescripcion(a.getDescripcion());
                        c.setDireccion(a.getDireccion());
                        c1 = centroAlmacenRepository.findByIdErp(a.getId());

                        if(c1!=null){
                            c.setIdCentroAlmacen(c1.getIdCentroAlmacen());
                        }
                        centroAlmacen.add(c);
                    }
            );
            logger.info("Preview save all Almacen: {}", centroAlmacen.size());
            centroAlmacenRepository.saveAll(centroAlmacen);
        }
    }
}




