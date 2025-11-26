package com.incloud.hcp.service.impl;

import com.incloud.hcp.domain.ConstanciaDetraccionDetalle;
import com.incloud.hcp.dto.ConstanciaDetraccionDetallePdfDto;
import com.incloud.hcp.dto.ConstanciaDetraccionProveedorDto;
import com.incloud.hcp.myibatis.mapper.ConstanciaDetraccionDetalleMapper;
import com.incloud.hcp.myibatis.mapper.ParametroMapper;
import com.incloud.hcp.pdf.PdfGeneratorFactory;
import com.incloud.hcp.repository.ConstanciaDetraccionDetalleRepository;
import com.incloud.hcp.service.ConstanciaDetraccionDetalleService;

import com.incloud.hcp.service.notificacion.EnvioCorreoConstanciaDetraccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class ConstanciaDetraccionDetalleServiceImpl implements ConstanciaDetraccionDetalleService {

    @Autowired
    private ConstanciaDetraccionDetalleMapper constanciaDetraccionDetalleMapper;
    @Autowired
    private EnvioCorreoConstanciaDetraccion envioCorreoConstanciaDetraccion;
    @Autowired
    private  ConstanciaDetraccionDetalleRepository constanciaDetraccionDetalleRepository;

    @Autowired
    private ParametroMapper parametroMapper;

    @Override
    public List<ConstanciaDetraccionProveedorDto> obtenerProveedores() {

        return constanciaDetraccionDetalleMapper.obtenerListaProveedores();
    }
    @Transactional
    @Override
    public String enviarCorreoConstanciaDetraccion() {
        String respuesta = "";
       List<ConstanciaDetraccionProveedorDto> dataConstanciaProveedores = this.obtenerProveedores();
       for (ConstanciaDetraccionProveedorDto dataProveedor: dataConstanciaProveedores){
           respuesta = this.envioCorreoConstanciaDetraccion.enviar(parametroMapper.getMailSetting(), dataProveedor);

           if("correo EnvioCorreoConstanciaDetraccion enviado".equals(respuesta)){
               Optional<ConstanciaDetraccionDetalle> constanciaDetraccionDetalle = this.constanciaDetraccionDetalleRepository.findById(dataProveedor.getIdCostanciaDetalle());
               if (constanciaDetraccionDetalle.isPresent()){
                   ConstanciaDetraccionDetalle constanciaDetraccionDetalleActualizada =  constanciaDetraccionDetalle.get();
                   constanciaDetraccionDetalleActualizada.setEnviaCorreo(true);
                   constanciaDetraccionDetalleRepository.save(constanciaDetraccionDetalleActualizada);
               }
           }

       }

       return respuesta;
    }

    @Override
    public String generarPdfConstanciaDetraccion(Integer idDetalleDetraccion) {
        ConstanciaDetraccionDetallePdfDto constanciaDetraccionDetallePdfDto = constanciaDetraccionDetalleMapper.obtenerPdfConstancia(idDetalleDetraccion);
        byte[] reporte = PdfGeneratorFactory.getJasperGenerator().generateConstanciaDetraccion(constanciaDetraccionDetallePdfDto);
        return Base64.getEncoder().encodeToString(reporte);

    }


}
