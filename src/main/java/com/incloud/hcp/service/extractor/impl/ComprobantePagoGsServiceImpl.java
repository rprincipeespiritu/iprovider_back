package com.incloud.hcp.service.extractor.impl;

import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.repository.ProveedorRepository;
import com.incloud.hcp.service.extractor.ComprobantePagoGsService;
import com.incloud.hcp.ws.docspendientes.bean.ObtenerDocsPendientesResponse;
import com.incloud.hcp.ws.docspendientes.dto.ComprobantePagoGsDto;
import com.incloud.hcp.ws.docspendientes.service.GSObtenerDocsPendienteService;
import com.incloud.hcp.ws.enums.CodEmpresaEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class ComprobantePagoGsServiceImpl implements ComprobantePagoGsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GSObtenerDocsPendienteService gsObtenerDocsPendienteService;

    @Autowired
    ProveedorRepository proveedorRepository;


    @Override
    public List<ComprobantePagoGsDto> getComprobantePagoList(String fechaInicio, String fechaFin, String email) {
        List<ComprobantePagoGsDto> comprobantePagoDtoList = new ArrayList<>();

        email = email != null ? email : "";

        List<Integer> svEmpresas = new ArrayList<>();
        svEmpresas.add(CodEmpresaEnum.SILVESTRE.getValor());
        //empresas.add(CodEmpresaEnum.NEOAGRUM.getValor());

        List<Proveedor> proveedors = new ArrayList<>();

        if (email == null || email.equals("")) {
            proveedors = proveedorRepository.findAll();
        } else {
            Proveedor proveedor = proveedorRepository.getProveedorByEmail(email);
            proveedors.add(proveedor);
        }

        List<Proveedor> finalProveedors = new ArrayList<>();
        finalProveedors.addAll(proveedors);

        Proveedor p = new Proveedor();
        //La data de proveedor P es para pruebas- Silvestre
        p.setRuc("20546255299");
        p.setRazonSocial("PACKPLAST ENVOLTURAS S.A.C.");
        finalProveedors.add(p);

        svEmpresas.forEach(e -> { // Empresas (Silvestre - Neoagrum)

            finalProveedors.forEach(f -> {
                // hacer el llamado del servicio por cada proveedor.

                ObtenerDocsPendientesResponse response = gsObtenerDocsPendienteService.obtenerDocsPendientes(e, f.getRuc(), fechaInicio, fechaFin);
                List<ComprobantePagoGsDto> body = response.getBody();

                if (response.getBody() != null) {

                    body.forEach(d -> {
                        ComprobantePagoGsDto comprobante = new ComprobantePagoGsDto();

                        comprobante.setOp(d.getOp());
                        comprobante.setCod_Proveedor(d.getCod_Proveedor());
                        comprobante.setFec_Vencimiento(d.getFec_Vencimiento());
                        comprobante.setSaldo(d.getSaldo());
                        comprobante.setImporte(d.getImporte());
                        comprobante.setSerie(d.getSerie());
                        comprobante.setNumero(d.getNumero());
                        comprobante.setFec_Programada_pago(d.getFec_Programada_pago());
                        comprobante.setFactura_Pagada(d.getFactura_Pagada());
                        comprobante.setTipo_Documento(d.getTipo_Documento());
                        comprobante.setID_Documento(d.getID_Documento());
                        comprobante.setRazonSocial(f.getRazonSocial());


                        comprobantePagoDtoList.add(comprobante);

                    });


                }
                logger.error("BUSQUEDA COMPROBANTES DE PAGO [FILTRO]: fechaInicio = " + fechaInicio);
                logger.error("BUSQUEDA COMPROBANTES DE PAGO [FILTRO]: fechaFin = " + fechaFin);
                logger.error("BUSQUEDA COMPROBANTES DE PAGO [FILTRO]: ruc = " + f.getRuc());

            });
        });

        return comprobantePagoDtoList;
    }
}
