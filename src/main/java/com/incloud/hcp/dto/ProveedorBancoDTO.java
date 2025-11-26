package com.incloud.hcp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProveedorBancoDTO {
    private D d;
    @Data
    @NoArgsConstructor
    public static class D {
        private List<ResultBanco> results;
    }
    @Data
    @NoArgsConstructor
    public static class ResultBanco{
        private Metadata __metadata;

        @JsonProperty("BankNumber")
        private String claveControlBanco;

        @JsonProperty ("BankAccount")
        private String numeroCuenta;
        @JsonProperty("BankAccountReferenceText")
        private String numeroCuentaCci;

        @JsonProperty("BankAccountName")
        private String idMoneda;
        @JsonProperty("BankControlKey")
        private String paisBanco;


    }
    @Data
    @NoArgsConstructor
    public static class Metadata {
        private String id;
        private String uri;
        private String type;
    }
}
