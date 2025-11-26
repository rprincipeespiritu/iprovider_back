package com.incloud.hcp.ws;

/*import com.incloud.hcp.ws.ingresomercaderias.bean.ObtenerGuiaResponse;
import com.incloud.hcp.ws.ingresomercaderias.dto.ObtenerGuiaGSDto;
import com.incloud.hcp.ws.ingresomercaderias.service.GSObtenerGuiaService;
import com.incloud.hcp.ws.ingresomercaderias.service.impl.GSObtenerGuiaServiceImpl;*/
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Ignore
public class ObtenerGuiaTest {
/*    ObtenerGuiaGSDto datos;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    GSObtenerGuiaService gsObtenerGuiateService = new GSObtenerGuiaServiceImpl();




    @Before
    public void init() throws IOException {
        datos= new ObtenerGuiaGSDto();
        datos.setIdEmpresa(1);
        datos.setFechaInicial("01/02/2020");
        datos.setFechaFinal("28/02/2021");
        datos.setId_Agenda("20102021836");
    }

    @Test
    public void obtenerGuia(){

        try {
            ObtenerGuiaResponse result = gsObtenerGuiateService.obtenerGuia(datos.getIdEmpresa(), datos.getFechaInicial(), datos.getFechaFinal(), datos.getId_Agenda());
            logger.info("Response: {}", result.toString());
            Assert.assertNotNull(result.getBody());

        } catch (Exception e) {
            Assert.fail("WS Exception " + e.getMessage());
        }
    }*/
}
