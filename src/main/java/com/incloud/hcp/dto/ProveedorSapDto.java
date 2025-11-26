package com.incloud.hcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProveedorSapDto {

    private D d;
    @Data
    @NoArgsConstructor
    public static class D {
        private List<Result> results;
    }
    @Data
    @NoArgsConstructor
    public static class Result{
        private Metadata __metadata;

        @JsonProperty("Supplier")
        private String acredorCodigoSap;

        @JsonProperty("StreetName")
        private String nombreCalle;
        @JsonProperty("HouseNumber")
        private String numeroCasa;
        @JsonProperty("BPTaxNumber")
        private String ruc;

        @JsonProperty("BusinessPartnerFullName")
        private String razonSocial;

        @JsonProperty("PhoneNumber")
        private String telefono;
        @JsonProperty("PaymentTerms")
        private String idCondicionPago;
        @JsonProperty("PurchaseOrderCurrency")
        private String idMoneda;
        @JsonProperty("Country")
        private String idTipoProveedor;
        @JsonProperty("AddressID")
        private String idDireccion;
    }
    @Data
    @NoArgsConstructor
    public static class Metadata {
        private String id;
        private String uri;
        private String type;
    }

}
