package com.incloud.hcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PurchaseOrderSapItem {
    @JsonProperty("PurchaseRequisition")
    private String purchaseRequisition;

    @JsonProperty("PurchaseRequisitionItem")
    private String purchaseRequisitionItem;

    @JsonProperty("OrderQuantity")
    private double orderQuantity;

    @JsonProperty("PurchaseOrderQuantityUnit")
    private String purchaseOrderQuantityUnit;

    @JsonProperty("NetPriceAmount")
    private double netPriceAmount;

    @JsonProperty("DocumentCurrency")
    private String documentCurrency;

    @JsonProperty("TaxCode")
    private String taxCode;
}
