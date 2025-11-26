package com.incloud.hcp.rest;

import com.incloud.hcp.domain.CcomparativoAdjudicado;
import com.incloud.hcp.domain.Licitacion;
import com.incloud.hcp.domain.LicitacionDetalle;
import com.incloud.hcp.domain.OrdenCompraDetalle;
import com.incloud.hcp.repository.CcomparativoAdjudicadoRepository;
import com.incloud.hcp.repository.LicitacionDetalleRepository;
import com.incloud.hcp.repository.OrdenCompraDetalleRepository;
import com.incloud.hcp.rest._framework.AppRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by USER on 07/11/2017.
 */
@RestController
@RequestMapping(value = "/api/ccomparativoAdjudicado")
public class CcomparativoAdjudicadoRest extends AppRest {

    @Autowired
    private CcomparativoAdjudicadoRepository  ccomparativoAdjudicadoRepository;

    @Autowired
    private LicitacionDetalleRepository licitacionDetalleRepository;

    @Autowired
    private OrdenCompraDetalleRepository ordenCompraDetalleRepository;


    @RequestMapping(value = "/findByCcomparativoAdjudicado/{idLicitacion}", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<List<CcomparativoAdjudicado>> findByCcomparativoAdjudicado(@RequestBody Integer idLicitacion) {
        Licitacion licitacion = new Licitacion();
        licitacion.setIdLicitacion(idLicitacion);

        List<CcomparativoAdjudicado> lista = this.ccomparativoAdjudicadoRepository.findByLicitacionOrdenado(licitacion);
        return ResponseEntity.ok().body(lista);
    }

    @RequestMapping(value = "/findByCcomparativoAdjudicadoLicitacion/{idLicitacion}", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<List<LicitacionDetalle>> findByCcomparativoAdjudicadoLicitacion(@PathVariable("idLicitacion") Integer idLicitacion) {
        Licitacion licitacion = new Licitacion();
        licitacion.setIdLicitacion(idLicitacion);
        List<LicitacionDetalle> licitacionDetalles = licitacionDetalleRepository.findByLicitacion(licitacion);
        for (int i= 0;i< licitacionDetalles.size();i++) {
            List<OrdenCompraDetalle> ordenCompraDetalle =
                    ordenCompraDetalleRepository.getAllByIdLicitacionDetalle(licitacionDetalles.get(i).getIdLicitacionDetalle());

            if(ordenCompraDetalle != null && ordenCompraDetalle.size() > 0){
                licitacionDetalles.get(i).setNumeroOrdenCompra(ordenCompraDetalle.get(0).getNumeroOrdenCompra());
            }
        }
        return ResponseEntity.ok().body(licitacionDetalles);
    }


}
