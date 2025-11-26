package com.incloud.hcp.ws.docspendientes.dto;

import java.math.BigDecimal;

public class ComprobantePagoGsDto {

    private Integer op;
    private Integer iD_Documento;
    private String cod_Proveedor;
    private String fec_Vencimiento;
    private String serie;
    private String tipo_Documento;
    private Integer numero;
    private BigDecimal saldo;
    private BigDecimal importe;
    private BigDecimal factura_Pagada;
    private String fec_Programada_pago;
    private String razonSocial;

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public Integer getOp() {
        return op;
    }

    public void setOp(Integer op) {
        this.op = op;
    }

    public Integer getID_Documento() {return iD_Documento; }

    public void setID_Documento(Integer id_Documento) {this.iD_Documento = id_Documento; }

    public String getTipo_Documento() { return tipo_Documento; }

    public void setTipo_Documento(String tipo_Documento) { this.tipo_Documento = tipo_Documento; }

    public String getCod_Proveedor() {
        return cod_Proveedor;
    }

    public void setCod_Proveedor(String cod_Proveedor) {
        this.cod_Proveedor = cod_Proveedor;
    }

    public String getFec_Vencimiento() {
        return fec_Vencimiento;
    }

    public void setFec_Vencimiento(String fec_Vencimiento) {
        this.fec_Vencimiento = fec_Vencimiento;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public BigDecimal getFactura_Pagada() {
        return factura_Pagada;
    }

    public void setFactura_Pagada(BigDecimal factura_Pagada) {
        this.factura_Pagada = factura_Pagada;
    }

    public String getFec_Programada_pago() {
        return fec_Programada_pago;
    }

    public void setFec_Programada_pago(String fec_Programada_pago) {
        this.fec_Programada_pago = fec_Programada_pago;
    }

    @Override
    public String toString() {
        return "ComprobantePagoGsDto{" +
                "op=" + op +
                ", ID_Documento=" + iD_Documento +
                ", cod_Proveedor='" + cod_Proveedor + '\'' +
                ", fec_Vencimiento='" + fec_Vencimiento + '\'' +
                ", serie='" + serie + '\'' +
                ", Tipo_Documento=" + tipo_Documento +
                ", numero=" + numero +
                ", saldo=" + saldo +
                ", importe=" + importe +
                ", factura_Pagada=" + factura_Pagada +
                ", fec_Programada_pago='" + fec_Programada_pago + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                '}';
    }
}

