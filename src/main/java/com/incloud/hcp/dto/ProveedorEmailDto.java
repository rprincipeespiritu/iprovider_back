package com.incloud.hcp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProveedorEmailDto {
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

        @JsonProperty("EmailAddress")
        private String email;

    }
    @Data
    @NoArgsConstructor
    public static class Metadata {
        private String id;
        private String uri;
        private String type;
    }
}
