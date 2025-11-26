package com.incloud.hcp.ws.registrarcompra.service.builder;

import com.google.gson.Gson;
import com.incloud.hcp.domain.OrdenCompra;
import com.incloud.hcp.domain.OrdenCompraDetalle;
import com.incloud.hcp.ws.enums.CodPagoEnum;
import com.incloud.hcp.ws.enums.MonedaCompraEnum;
import com.incloud.hcp.ws.registrarcompra.dto.RegistroCompraDetalleGSDto;
import com.incloud.hcp.ws.registrarcompra.dto.RegistroCompraGSDto;
import com.incloud.hcp.ws.util.GSConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CompraRegistroGSBuilder {

    RegistroCompraGSDto dto = new RegistroCompraGSDto();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static CompraRegistroGSBuilder newBuilder(RegistroCompraGSDto dto) {
        return new CompraRegistroGSBuilder(dto);
    }

    private CompraRegistroGSBuilder(RegistroCompraGSDto dto) {

        this.dto=dto;

    }

    public String build() {

         OrdenCompra compra= new OrdenCompra();
         OrdenCompraDetalle  cd= new OrdenCompraDetalle();
        Gson gson = new Gson();


        dto.setID_Agenda(compra.getProveedorRuc());
        dto.setIdEmpresa(GSConstant.CODIGO_EMPRESA_SILVESTRE);
        dto.setNoRegistro(compra.getNoRegistro());
        dto.setFechaOrden("26/02/2021");
        dto.setFechaEntrega("26/02/2021");
        dto.setFechaVigencia("26/02/2021");
        //dto.setFechaOrden(compra.getFechaRegistro());
        //dto.setFechaEntrega(compra.getFechaEntrega());
        //dto.setFechaVigencia(compra.getfecha);
       // dto.setiD_Moneda(compra.getCodigoMondeda());
        dto.setDcto(compra.getDcto());
        dto.setSubTotal(compra.getSubtotal());
        dto.setImpuestos(compra.getImpuestos());
        dto.setTotal(compra.getTotal());
        dto.setObservaciones(compra.getObservaciones());
        dto.setiD_Pago(compra.getIdPago());
        dto.setiD_AgendaDireccion(compra.getIdAgendaDireccion());
        dto.setiD_AgendaDireccion2(compra.getIdAgendaDireccion2());
        dto.setModoPago(compra.getCondicionPagoDescripcion());
        dto.setNotasRecepcion(compra.getNotasRecepcion());

        List<RegistroCompraDetalleGSDto> list = new ArrayList<>();
        RegistroCompraDetalleGSDto detalle = new RegistroCompraDetalleGSDto();
        detalle.setOp_SolicitudCompra(cd.getOpSolicitudCompra());
        detalle.setId_Amarre(cd.getIdAmarre());
        detalle.setId_Amarre_SC(cd.getIdAmarreSC());
        detalle.setCodigoProducto(cd.getCodigoProducto());
        detalle.setKardex(cd.getKardex());
        detalle.setCantidad(cd.getCantidad());
        detalle.setPrecio(cd.getPrecioTotal());
        detalle.setImporte(cd.getPrecioTotal());
        detalle.setObservaciones(cd.getObservaciones());
        list.add(detalle);

        dto.setLista_CD(list);

        return gson.toJson(dto);
    }

}
