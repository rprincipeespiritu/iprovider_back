package com.incloud.hcp.service.extractor.impl;

import com.incloud.hcp.domain.*;
import com.incloud.hcp.repository.BienServicioRepository;
import com.incloud.hcp.service.extractor.ExtractorBienServicioService;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.ws.enums.CodEmpresaEnum;
import com.incloud.hcp.ws.enums.ItemCategoriaEnum;
import com.incloud.hcp.ws.enums.TipoCategoriaEnum;
import com.incloud.hcp.ws.producto.bean.ObtenerProductoBodyResponse;
import com.incloud.hcp.ws.producto.bean.ObtenerProductoResponse;
import com.incloud.hcp.ws.producto.service.GSObtenerProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ExtractorBienServicioServiceImpl implements ExtractorBienServicioService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AtomicBoolean ocProcessing = new AtomicBoolean(false);

    @Autowired
    BienServicioRepository bienServicioRepository;

    @Autowired
    GSObtenerProductoService gsObtenerProductoService;


    @Override
    public void extraerBienServicio(String fechaInicio, String fechaFinal) throws Exception {

        List<Integer> svEmpresas = new ArrayList<>();
        svEmpresas.add(CodEmpresaEnum.SILVESTRE.getValor());
        //empresas.add(CodEmpresaEnum.NEOAGRUM.getValor());

        List<Integer> svItemCategorias = new ArrayList<>();
        svItemCategorias.add(ItemCategoriaEnum.MATERIALES.getValor());
        svItemCategorias.add(ItemCategoriaEnum.SERVICIOS.getValor());

        if (!ocProcessing.get()) {
            ocProcessing.set(!ocProcessing.get());
            try {
                svEmpresas.forEach(e -> {
                    svItemCategorias.forEach(ic -> {
                        String header1 = "INI: " + DateUtils.getCurrentTimestamp().toString() + " -- EXTR BIEN_SERVICIO -- RANGO: " + fechaInicio + " - " + fechaFinal + " // ";
                        ObtenerProductoResponse response = gsObtenerProductoService.obtenerproducto(fechaInicio, fechaFinal, e, ic);
                        List<ObtenerProductoBodyResponse> body = response.getBody();
                        String itemCategoria = ic.equals(ItemCategoriaEnum.MATERIALES.getValor()) ? "Materiales" : "Servicios";
                        logger.info(header1 + "CANTIDAD DE BIEN_SERVICIO - " + itemCategoria + " ENCONTRADOS: " + body.size());

                        if (body.size() > 0) {
                            body.forEach(bs -> {
                                Integer kardex = bs.getKardex();
                                String codigo = bs.getCodigo();
                                BienServicio bienServicioExistente = bienServicioRepository.findByKardexAndCodigoSap(kardex, codigo);
                                String header2 = header1.concat("BIEN_SERVICIO: codigo => " + bs.getCodigo() + ", kardex => " + bs.getKardex());
                                if (bienServicioExistente == null) { // BS no existe en HANA
                                    BienServicio bienServicio = new BienServicio();
                                    bienServicio.setIdEmpresa(e);
                                    this.getBienServicioValue(bienServicio, bs, ic);

                                    logger.info(header2 + " // WRITING NEW BS: " + bienServicio.toString());
                                    bienServicioRepository.save(bienServicio);
                                } else { // BS ya existe en HANA
                                    BienServicio b = new BienServicio();
                                    b.setIdEmpresa(e);
                                    b.setIdBienServicio(bienServicioExistente.getIdBienServicio());
                                    this.getBienServicioValue(b, bs, ic);

                                    logger.info(header2 + " // WRITING UPDATE BS: " + b.toString());
                                    bienServicioRepository.save(b);
                                }
                            });
                        }
                        logger.info(header1 + "FINISHED");

                    });
                });

                if (ocProcessing.get())
                    ocProcessing.set(!ocProcessing.get());
            } catch (Exception e) {
                if (ocProcessing.get())
                    ocProcessing.set(!ocProcessing.get());
                logger.error(e.getMessage(), e.getCause());
                throw new Exception(e);
            }
        } else {
            logger.error("INI: " + DateUtils.getCurrentTimestamp().toString() + " // BIEN_SERVICIO Extraction esta procesando");
        }

    }

    private void getBienServicioValue(BienServicio b, ObtenerProductoBodyResponse bs, Integer itemCategoria) {
        b.setCodigoSap(bs.getCodigo());
        if(bs.getNombre().length() > 80) {
            b.setDescripcion(bs.getNombre().substring(0,80));
        } else {
            b.setDescripcion(bs.getNombre());
        }
        b.setDescripcionLarga(bs.getNombre());
        b.setNumeroParte(""); //no aplica
        b.setTipoItem(itemCategoria.equals(ItemCategoriaEnum.MATERIALES.getValor()) ? "M" : "S");

        RubroBien r = new RubroBien();
        r.setIdRubro(bs.getTipoCategoria().equalsIgnoreCase("Materiales") ? TipoCategoriaEnum.MATERIALES.getValor() : TipoCategoriaEnum.SERVICIOS.getValor());
        b.setRubroBien(r);

        UnidadMedida u = new UnidadMedida();
        u.setIdUnidadMedida(1); // TEMPORAL
        b.setUnidadMedida(u);

        b.setKardex(bs.getKardex());
        b.setUnidadInventario(bs.getUnidadInventario());
        b.setUnidadPresentacion(bs.getUnidadPresentacion());
        b.setTipoCategoria(bs.getTipoCategoria());
        b.setFactorConversion(bs.getFactorConversion());
    }

    @Override
    public boolean toggleOrdenCompraExtractionProcessingState() {
        ocProcessing.set(!ocProcessing.get());
        return ocProcessing.get();
    }

    @Override
    public boolean currentOrdenCompraExtractionProcessingState() {
        return ocProcessing.get();
    }

    /*
    @Override
    public void obtenerProducto(String fechaInicial, String fechaFinal, int idEmpresa, int itemCategoria) {

        ObtenerProductoResponse response= gsObtenerProductoService.obtenerproducto(fechaInicial,fechaFinal,idEmpresa,itemCategoria);

        logger.info("CONSULTO SERVICIO OBTENER PRODUCTOS: {}", response.getBody().size());

        if(response.getBody() != null) {
            List<ObtenerProductoBodyResponse> body = response.getBody();
            List<BienServicio> producto = new ArrayList<>();

            // Cabecera


            body.forEach(p -> {

                BienServicio b = new BienServicio();
                BienServicio b1 = new BienServicio();
                b.setKardex(p.getKardex());
                b.setDescripcion(p.getNombre());
                b.setCodigoSap("S");
                b.setTipoItem("I");
                RubroBien r = new RubroBien();
                r.setIdRubro(p.getTipoCategoria().equalsIgnoreCase("Materiales") ? TipoCategoriaEnum.MATERIALES.getValor() : TipoCategoriaEnum.SERVICIOS.getValor() );
                b.setRubroBien(r);
                UnidadMedida u = new UnidadMedida();
                u.setIdUnidadMedida(571);
                b.setUnidadMedida(u);
                b1= bienServicioRepository.findByKardex(p.getKardex());

                if(b1!=null){
                    b.setIdBienServicio(b1.getIdBienServicio());
                }


                producto.add(b);
                    }
            );
            logger.info("Preview save all Producto: {}", producto.size());
            bienServicioRepository.saveAll(producto);
        }

    }
    * */

}

