package com.incloud.hcp.service.extractor.impl;

import com.incloud.hcp.bean.ProveedorCustom;
import com.incloud.hcp.bean.SolicitudPedido;
import com.incloud.hcp.myibatis.mapper.ProveedorMapper;
import com.incloud.hcp.service.extractor.SolicitudPedidoGs;
import com.incloud.hcp.ws.obtenersolicitudcompra.bean.ObtenerCompraResponse;
import com.incloud.hcp.ws.obtenersolicitudcompra.bean.ObtenerCompraResponseDto;
import com.incloud.hcp.ws.obtenersolicitudcompra.dto.SolicitudCompraResponseDto;
import com.incloud.hcp.ws.obtenersolicitudcompra.service.GSObtenerSolicitudCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SolicitudPedidoGsImpl implements SolicitudPedidoGs {

    @Autowired
    GSObtenerSolicitudCompraService gsObtenerSolicitudCompraService;

    @Autowired
    private ProveedorMapper proveedorMapperMybatis;

    int idEmpresa = 1;
    String fechaInicio = "01/01/2021";
    String fechaFinal = "30/03/2021";


    @Override
    public SolicitudCompraResponseDto solicitudCompraByCodigo(Integer codigo, boolean obtainFullPositionList) {

        SolicitudCompraResponseDto solicitudCompraResponseDto = new SolicitudCompraResponseDto();

        ObtenerCompraResponse response = gsObtenerSolicitudCompraService.obtenerSolicitudCompra(idEmpresa, fechaInicio, fechaFinal);

        boolean esPeticionOferta = false;
        if (codigo != null)
            esPeticionOferta = codigo.longValue() == 4; // se trata de un codigo de producto


        List<String> rucProveedorList = new ArrayList<>();
        List<ProveedorCustom> proveedorCustomList = new ArrayList<>();
        List<SolicitudPedido> solicitudPedido = new ArrayList<>();


        if (response.getBody() != null) {
            List<ObtenerCompraResponseDto> body = response.getBody();


            body.forEach(oc -> {

                SolicitudPedido solicitud = new SolicitudPedido();


                solicitud.setOp(oc.getOp());
                solicitud.setSerie(oc.getSerie());
                solicitud.setNumero(oc.getNumero());
                solicitud.setRucProveedor(oc.getId_agenda());
                solicitud.setAgendaNombre(oc.getAgendaNombre());
                solicitud.setObservaciones(oc.getObservaciones());
                solicitud.setCentroCosto(oc.getCentroCosto());
                solicitud.setUnidadGestion(oc.getUnidadGestion());
                solicitud.setUnidadGestion(oc.getUnidadGestion());
                solicitud.setUnidadProyecto(oc.getUnidadProyecto());
                solicitud.setId_Amarre(oc.getId_Amarre());
                solicitud.setCodigoProducto(oc.getCodigoProducto());
                solicitud.setKardex(oc.getKardex());
                solicitud.setNombreProducto(oc.getNombreProducto());
                solicitud.setCantidad(oc.getCantidad());
                solicitud.setCentroCostoDetalle(oc.getCentroCostoDetalle());
                solicitud.setUnidadGestionDetalle(oc.getUnidadGestionDetalle());
                solicitud.setUnidadProyectoDetalle(oc.getUnidadProyectoDetalle());


                // solicitudPedido.stream().filter(a -> a.getOp().equals(codigo)).map(a -> solicitudPedido.add(solicitud));
                solicitudPedido.add(solicitud);
                rucProveedorList.add(oc.getId_agenda());
            });


        }
        List<SolicitudPedido> resultSolicitudPedidos = solicitudPedido.stream()
                .filter(t -> t.getOp() == codigo)
                .collect(Collectors.toList());

        for (String ruc : rucProveedorList) {
            List<ProveedorCustom> proveedorByRucList = proveedorMapperMybatis.getListProveedorByRuc(ruc);
            if (proveedorByRucList.size() == 1)
                proveedorCustomList.add(proveedorByRucList.get(0));
        }
        solicitudCompraResponseDto.setListaProveedorSeleccionado(proveedorCustomList);
        solicitudCompraResponseDto.setListaSolped(resultSolicitudPedidos);


        return solicitudCompraResponseDto;
    }
}
