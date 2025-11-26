package com.incloud.hcp.ws.enums;

public enum ItemCategoriaEnum {
    MATERIALES(0) , SERVICIOS(2);

    private final Integer valor;
     ItemCategoriaEnum(Integer valor){
         this.valor=valor;
     }

    @Override
    public String toString() {
        return "ItemCategoria{" +
                "valor=" + valor +
                '}';
    }

    public Integer getValor() {
        return valor;
    }
}
