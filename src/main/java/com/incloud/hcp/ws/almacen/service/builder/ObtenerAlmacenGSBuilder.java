package com.incloud.hcp.ws.almacen.service.builder;

import com.google.gson.Gson;
import com.incloud.hcp.ws.almacen.dto.AlmacenGSDto;
import org.slf4j.LoggerFactory;

public class ObtenerAlmacenGSBuilder {


    private int idEmpresa;
    private String fechaInicio;
    private String fechaFinal;

   private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

   public static ObtenerAlmacenGSBuilder newBuilder(int idEmpresa, String fechaInicio, String fechaFinal){
       return new ObtenerAlmacenGSBuilder(idEmpresa,fechaInicio,fechaFinal);
   }
    private ObtenerAlmacenGSBuilder (int idEmpresa, String fechaInicio, String fechaFinal){
       this.idEmpresa= idEmpresa;
       this.fechaInicio=fechaInicio;
       this.fechaFinal=fechaFinal;}

    public String build (){
        AlmacenGSDto dto= new AlmacenGSDto();
        Gson gsson= new Gson();

        dto.setIdEmpresa(1);
        dto.setFechaInicial(fechaInicio);
        dto.setFechaFinal(fechaFinal);

         return gsson.toJson(dto);

    }

}
