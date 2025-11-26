package com.incloud.hcp.service.impl;

import com.incloud.hcp.dto.CanalContactoInDto;
import com.incloud.hcp.dto.CanalContactoOutDto;
import com.incloud.hcp.dto.ProductoDto;
import com.incloud.hcp.dto.ProductoOutDto;
import com.incloud.hcp.myibatis.mapper.ProductoMapper;
import com.incloud.hcp.service.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoMapper productoMapper;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ProductoOutDto crearProducto(ProductoDto bean) {
        ProductoOutDto result = new ProductoOutDto();
        ProductoDto beanCentro = new ProductoDto();

        beanCentro.setIdProveedorProducto(bean.getIdProveedorProducto());
        beanCentro.setMarca(bean.getMarca());
        beanCentro.setProducto(bean.getProducto());
        beanCentro.setProveedor(bean.getProveedor());
        beanCentro.setDescripcionAdicional(bean.getDescripcionAdicional());

        if (beanCentro.getIdProveedorProducto() != null){
            logger.info("UPDATE PRODUCTO - PROVEEDOR");
            productoMapper.updateProducto(beanCentro);

            result.setIdProveedorProducto(bean.getIdProveedorProducto());
            result.setMarca(bean.getMarca());
            result.setProducto(bean.getProducto());
            result.setProveedor(bean.getProveedor());
            result.setDescripcionAdicional(bean.getDescripcionAdicional());
        }else{
            logger.info("CREATE PRODUCTO - PROVEEDOR");
            Integer validarId = productoMapper.getIdSequence();
            beanCentro.setIdProveedorProducto(validarId);
            productoMapper.getCrearProducto(beanCentro);

            result.setIdProveedorProducto(validarId);
            result.setMarca(bean.getMarca());
            result.setProducto(bean.getProducto());
            result.setProveedor(bean.getProveedor());
            result.setDescripcionAdicional(bean.getDescripcionAdicional());
        }



        return result;
    }
}
