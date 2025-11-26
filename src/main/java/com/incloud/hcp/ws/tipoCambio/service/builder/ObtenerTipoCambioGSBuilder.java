package com.incloud.hcp.ws.tipoCambio.service.builder;

import com.google.gson.Gson;
import com.incloud.hcp.ws.tipoCambio.dto.TipoCambioGSDto;
import org.slf4j.LoggerFactory;

public class ObtenerTipoCambioGSBuilder {


    private int idEmpresa;
    private String fecha;


   private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

   public static ObtenerTipoCambioGSBuilder newBuilder(int idEmpresa, String fecha){
       return new ObtenerTipoCambioGSBuilder(idEmpresa,fecha);
   }
    private ObtenerTipoCambioGSBuilder(int idEmpresa, String fecha){
       this.idEmpresa= idEmpresa;
       this.fecha=fecha;
       }

    public String build (){
        TipoCambioGSDto dto= new TipoCambioGSDto();
        Gson gsson= new Gson();

        dto.setIdEmpresa(1);
        dto.setFecha(fecha);


         return gsson.toJson(dto);

    }

}
