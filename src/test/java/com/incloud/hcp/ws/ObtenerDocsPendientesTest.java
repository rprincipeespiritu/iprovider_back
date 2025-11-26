package com.incloud.hcp.ws;

/*import com.incloud.hcp.ws.docspendientes.bean.ObtenerDocsPendientesResponse;
import com.incloud.hcp.ws.docspendientes.dto.ObtenerDocsPendientesGSDto;
import com.incloud.hcp.ws.docspendientes.service.GSObtenerDocsPendienteService;
import com.incloud.hcp.ws.docspendientes.service.impl.GSObtenerDocsPendienteServiceImpl;*/
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Ignore
public class ObtenerDocsPendientesTest {
  /*  ObtenerDocsPendientesGSDto datos;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    GSObtenerDocsPendienteService gsObtenerDocsPendienteService = new GSObtenerDocsPendienteServiceImpl();




    @Before
    public void init() throws IOException {
        datos= new ObtenerDocsPendientesGSDto();
        datos.setIdEmpresa(1);
        datos.setiD_Agenda("20546255299");
        datos.setFechaInicial("01/07/2020");
        datos.setFechaFinal("30/07/2020");

    }

    @Test
    public void obtenerDocsPendientes(){

        try {
            ObtenerDocsPendientesResponse result = gsObtenerDocsPendienteService.obtenerDocsPendientes(datos.getIdEmpresa(), datos.getiD_Agenda(), datos.getFechaInicial(), datos.getFechaFinal());
            logger.info("Response: {}", result.toString());
            Assert.assertNotNull(result.getBody());

        } catch (Exception e) {
            Assert.fail("WS Exception " + e.getMessage());
        }
    }*/
}
