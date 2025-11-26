package com.incloud.hcp.ws;

/*import com.incloud.hcp.domain.*;
import com.incloud.hcp.ws.enums.CodPagoEnum;
import com.incloud.hcp.ws.enums.MonedaCompraEnum;
import com.incloud.hcp.ws.producto.bean.ObtenerProductoResponse;
import com.incloud.hcp.ws.producto.service.GSObtenerProductoService;
import com.incloud.hcp.ws.producto.service.impl.GSObtenerProductoServiceImpl;
import com.incloud.hcp.ws.proveedor.bean.ProveedorRegistroResponse;
import com.incloud.hcp.ws.proveedor.service.GSProveedorService;
import com.incloud.hcp.ws.proveedor.service.impl.GSProveedorServiceImpl;
import com.incloud.hcp.ws.registrarcompra.bean.CompraRegistroResponse;
import com.incloud.hcp.ws.registrarcompra.dto.RegistroCompraDetalleGSDto;
import com.incloud.hcp.ws.registrarcompra.dto.RegistroCompraGSDto;
import com.incloud.hcp.ws.registrarcompra.service.GSCompraRegistroService;
import com.incloud.hcp.ws.registrarcompra.service.impl.GSCompraRegistroServiceImpl;
import com.incloud.hcp.ws.util.GSConstant;*/
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Ignore
public class RegistrarCompraTest {
/*
    private RegistroCompraGSDto dto;
    private RegistroCompraDetalleGSDto od;



    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    GSCompraRegistroService gsRegistroCompraService = new GSCompraRegistroServiceImpl();

    @Before
    public void init() throws IOException {
        dto = new RegistroCompraGSDto();

        dto.setIdEmpresa(GSConstant.CODIGO_EMPRESA_SILVESTRE);
        dto.setID_Agenda("20112811096");
        dto.setNoRegistro("0");
        dto.setFechaOrden("26/02/2021");
        dto.setFechaEntrega("26/02/2021");
        dto.setFechaVigencia("26/02/2021");
        dto.setiD_Moneda(0);
        dto.setNeto(new BigDecimal(52.00));
        dto.setDcto(new BigDecimal(0.00));
        dto.setSubTotal(new BigDecimal(52.00));
        dto.setImpuestos(new BigDecimal(9.00));
        dto.setTotal(new BigDecimal(61.00));
        dto.setObservaciones("Prueba OC xxx ");
        dto.setiD_Pago(1);
        dto.setiD_AgendaDireccion(3188);
        dto.setiD_AgendaDireccion2(3188);
        dto.setModoPago("CRED");
        dto.setNotasRecepcion("");

        List<RegistroCompraDetalleGSDto> list = new ArrayList<>();
        od = new RegistroCompraDetalleGSDto();
        od.setOp_SolicitudCompra(5200);
        od.setId_Amarre(0);
        od.setId_Amarre_SC(18466);
        od.setCodigoProducto("10251007233743");
        od.setKardex(4980);
        od.setCantidad(new BigDecimal(100));
        od.setPrecio(new BigDecimal(52.00));
        od.setImporte(new BigDecimal(52.00));
        od.setObservaciones("Pruebas");
        list.add(od);

        dto.setLista_CD(list);
    }

    @Test
    public void registrarCompra(){

        try {
            CompraRegistroResponse result = gsRegistroCompraService.registro(dto);
            logger.info("Response: {}", result.toString());
            Assert.assertNotNull(result.getBody());

        } catch (Exception e) {
            Assert.fail("WS Exception " + e.getMessage());
        }
    }
 */
}
