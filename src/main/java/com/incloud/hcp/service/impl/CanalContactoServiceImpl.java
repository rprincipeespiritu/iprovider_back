package com.incloud.hcp.service.impl;

import com.incloud.hcp.dto.CanalContactoInDto;
import com.incloud.hcp.dto.CanalContactoOutDto;
import com.incloud.hcp.dto.LineaComercialInDto;
import com.incloud.hcp.dto.LineaComercialOutDto;
import com.incloud.hcp.myibatis.mapper.ContactoMapper;
import com.incloud.hcp.service.CanalContactoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation= Propagation.REQUIRED, rollbackFor=Exception.class)
public class CanalContactoServiceImpl implements CanalContactoService {

    @Autowired
    private ContactoMapper contactoMapper;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public CanalContactoOutDto crearCanalContacto(CanalContactoInDto bean) {
        CanalContactoOutDto result = new CanalContactoOutDto();
        CanalContactoInDto beanCentro = new CanalContactoInDto();

//        String areaDefault = "NA";
//        bean.setAreaEmpresa(areaDefault);

        beanCentro.setIdProveedorCanal(bean.getIdProveedorCanal());
        beanCentro.setAreaEmpresa(bean.getAreaEmpresa());
        beanCentro.setContacto(bean.getContacto());
        beanCentro.setDireccion(bean.getDireccion());
        beanCentro.setEmail(bean.getEmail());
        beanCentro.setTelefono(bean.getTelefono());
        beanCentro.setPais(bean.getPais());
        beanCentro.setProveedor(bean.getProveedor());
        beanCentro.setProvincia(bean.getProvincia());
        beanCentro.setRegion(bean.getRegion());

        if(beanCentro.getIdProveedorCanal() != null){
            logger.info("UPDATE CANAL CONTACTO - PROVEEDOR");
            contactoMapper.updateCanalContacto(beanCentro);
            result.setIdProveedorCanal(bean.getIdProveedorCanal());
            result.setAreaEmpresa(bean.getAreaEmpresa());
            result.setContacto(bean.getContacto());
            result.setDireccion(bean.getDireccion());
            result.setEmail(bean.getEmail());
            result.setTelefono(bean.getTelefono());
            result.setPais(bean.getPais());
            result.setProveedor(bean.getProveedor());
            result.setProvincia(bean.getProvincia());
            result.setRegion(bean.getRegion());

        }else{
            logger.info("CREATE CANAL CONTACTO - PROVEEDOR");
            Integer validarId = contactoMapper.getIdSequence();
            beanCentro.setIdProveedorCanal(validarId);
            contactoMapper.getCrearCanalContacto(beanCentro);

            result.setIdProveedorCanal(validarId);
            result.setAreaEmpresa(bean.getAreaEmpresa());
            result.setContacto(bean.getContacto());
            result.setDireccion(bean.getDireccion());
            result.setEmail(bean.getEmail());
            result.setTelefono(bean.getTelefono());
            result.setPais(bean.getPais());
            result.setProveedor(bean.getProveedor());
            result.setProvincia(bean.getProvincia());
            result.setRegion(bean.getRegion());
        }

        return result;
    }
}
