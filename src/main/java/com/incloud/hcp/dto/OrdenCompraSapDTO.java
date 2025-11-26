package com.incloud.hcp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrdenCompraSapDTO {
    @JsonProperty("PurchaseOrderType")
    private String purchaseOrderType;

    @JsonProperty("Supplier")
    private String supplier;

    @JsonProperty("PurchasingGroup")
    private String purchasingGroup;

    @JsonProperty("PurchasingOrganization")
    private String purchasingOrganization;

    @JsonProperty("CompanyCode")
    private String companyCode;

    @JsonProperty("PurchaseOrderDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String  purchaseOrderDate;

    @JsonProperty("PaymentTerms")
    private String paymentTerms;

    @JsonProperty("Language")
    private String language;

    @JsonProperty("YY1_AreaSolicitante_PDH")
    private String yy1AreaSolicitantePdh;

    @JsonProperty("_PurchaseOrderItem")
    private List<PurchaseOrderSapItem> purchaseOrderItems;
}
