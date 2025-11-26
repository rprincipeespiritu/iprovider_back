package com.incloud.hcp.ws;

/*import com.incloud.hcp.domain.Moneda;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.domain.Ubigeo;
import com.incloud.hcp.ws.proveedor.bean.ProveedorRegistroResponse;
import com.incloud.hcp.ws.proveedor.service.GSProveedorService;
import com.incloud.hcp.ws.proveedor.service.impl.GSProveedorServiceImpl;*/
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Ignore
public class ProveedorRegistroTest {
/*
    Proveedor proveedor;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    GSProveedorService gsProveedorService = new GSProveedorServiceImpl();

    @Before
    public void init() throws IOException {
        proveedor = new Proveedor();
        proveedor.setRazonSocial("PRODUCTOS TISSUE DEL PERU S.A.C - PROTISA-PERU");
        proveedor.setRuc("20543702618");
        proveedor.setTipoPersona("J");
        Moneda moneda = new Moneda();
        moneda.setCodigoMoneda("");
        proveedor.setMoneda(moneda);
        proveedor.setIndHabidoSunat("1");
        proveedor.setDireccionFiscal("AV. SEPARADORA INDUSTRIAL NRO 2509 - URB. SANTA RAQUEL 2DA ETAPA");
        Ubigeo uPais = new Ubigeo();
        uPais.setCodigoUbigeoSap("9589");
        proveedor.setPais(uPais);

        Ubigeo uRegion = new Ubigeo();
        uRegion.setCodigoUbigeoSap("15");
        proveedor.setRegion(uRegion);

        Ubigeo uProvincia = new Ubigeo();
        uProvincia.setCodigoUbigeoSap("1501");
        proveedor.setProvincia(uProvincia);

        Ubigeo uDistrito = new Ubigeo();
        uDistrito.setCodigoUbigeoSap("150101");
        proveedor.setDistrito(uDistrito);

        proveedor.setTelefono("4508628");
        proveedor.setEmail("proveedordev01@yopmail.com");
    }

    @Test
    public void registrarProveedor(){

        try {
            ProveedorRegistroResponse result = gsProveedorService.registro(proveedor);
            logger.info("Response: {}", result.toString());
            Assert.assertNotNull(result.getBody().getId_Agenda());

        } catch (Exception e) {
            Assert.fail("WS Exception " + e.getMessage());
        }
    }
*/
}
