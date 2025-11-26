package com.incloud.hcp.ws.producto.service.builder;

import com.google.gson.Gson;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.ws.enums.*;
import com.incloud.hcp.ws.producto.dto.ProductoGSDto;
import com.incloud.hcp.ws.util.GSConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ObtenerproductoGSBuilder {
   private String fechaInicial;
   private String fechaFinal;
   private int idEmpresa;
   private int itemCategoria;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static ObtenerproductoGSBuilder newBuilder(String fechaInicial, String fechaFinal, int idEmpresa,int itemCategoria) {
        return new ObtenerproductoGSBuilder(fechaInicial,fechaFinal,idEmpresa, itemCategoria);
    }

    private ObtenerproductoGSBuilder(String fechaInicial, String fechaFinal, int idEmpresa,int itemCategoria) {
        this.fechaInicial=fechaInicial;
        this.fechaFinal=fechaFinal;
        this.idEmpresa=idEmpresa;
        this.itemCategoria=itemCategoria;
    }

    public String build() {
        ProductoGSDto dto = new ProductoGSDto();
        Gson gson = new Gson();

        dto.setFechaInicial(fechaInicial);
        dto.setFechaFinal(fechaFinal);
        dto.setIdEmpresa(idEmpresa);
        //? CodEmpresa.SILVESTRE.getValor(): CodEmpresa.NEOAGRUM.getValor()
        dto.setItemCategoria(itemCategoria);
        return gson.toJson(dto);
    }

}
