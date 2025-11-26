package com.incloud.hcp.service.impl;

import com.incloud.hcp.dto.CanalContactoInDto;
import com.incloud.hcp.dto.CanalContactoOutDto;
import com.incloud.hcp.dto.CuentaBancariaInDto;
import com.incloud.hcp.dto.CuentaBancariaOutDto;
import com.incloud.hcp.myibatis.mapper.ContactoMapper;
import com.incloud.hcp.myibatis.mapper.CuentaBancariaMapper;
import com.incloud.hcp.service.CuentaBancariaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional(propagation= Propagation.REQUIRED, rollbackFor=Exception.class)
public class CuentaBancariaServiceImpl implements CuentaBancariaService {

    @Autowired
    private CuentaBancariaMapper cuentaBancariaMapper;

    @Override
    public CuentaBancariaOutDto crearCuentaBancaria(CuentaBancariaInDto bean) {
        CuentaBancariaOutDto result = new CuentaBancariaOutDto();
        CuentaBancariaInDto beanCentro = new CuentaBancariaInDto();

        beanCentro.setIdCuenta(bean.getIdCuenta());
        beanCentro.setArchivoId(bean.getArchivoId());
        beanCentro.setArchivoNombre(bean.getArchivoNombre());
        beanCentro.setArchivoTipo(bean.getArchivoTipo());
        beanCentro.setClaveControlBanco(bean.getClaveControlBanco());
        beanCentro.setContacto(bean.getContacto());
        beanCentro.setIndCuentaDetraccion(bean.getIndCuentaDetraccion());
        beanCentro.setNumeroCuenta(bean.getNumeroCuenta());
        beanCentro.setNumeroCuentaCci(bean.getNumeroCuentaCci());
        beanCentro.setRutaAdjunto(bean.getRutaAdjunto());
        beanCentro.setIdBanco(bean.getIdBanco());
        beanCentro.setIdMoneda(bean.getIdMoneda());
        beanCentro.setIdProveedor(bean.getIdProveedor());

        if(beanCentro.getIdCuenta() != null){

            cuentaBancariaMapper.updateCuentaBancaria(beanCentro);

            result.setIdCuenta(bean.getIdCuenta());
            result.setArchivoId(bean.getArchivoId());
            result.setArchivoNombre(bean.getArchivoNombre());
            result.setArchivoTipo(bean.getArchivoTipo());
            result.setClaveControlBanco(bean.getClaveControlBanco());
            result.setContacto(bean.getContacto());
            result.setIndCuentaDetraccion(bean.getIndCuentaDetraccion());
            result.setNumeroCuenta(bean.getNumeroCuenta());
            result.setNumeroCuentaCci(bean.getNumeroCuentaCci());
            result.setRutaAdjunto(bean.getRutaAdjunto());
            result.setIdBanco(bean.getIdBanco());
            result.setIdMoneda(bean.getIdMoneda());
            result.setIdProveedor(bean.getIdProveedor());
        }else{
            Integer validarId = cuentaBancariaMapper.getIdSequence();
            beanCentro.setIdCuenta(validarId);
            cuentaBancariaMapper.getCrearCuentaBancaria(beanCentro);

            result.setIdCuenta(validarId);
            result.setArchivoId(bean.getArchivoId());
            result.setArchivoNombre(bean.getArchivoNombre());
            result.setArchivoTipo(bean.getArchivoTipo());
            result.setClaveControlBanco(bean.getClaveControlBanco());
            result.setContacto(bean.getContacto());
            result.setIndCuentaDetraccion(bean.getIndCuentaDetraccion());
            result.setNumeroCuenta(bean.getNumeroCuenta());
            result.setNumeroCuentaCci(bean.getNumeroCuentaCci());
            result.setRutaAdjunto(bean.getRutaAdjunto());
            result.setIdBanco(bean.getIdBanco());
            result.setIdMoneda(bean.getIdMoneda());
            result.setIdProveedor(bean.getIdProveedor());
        }

        return result;
    }
}
