package com.incloud.hcp.ws.docspendientes.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class ObtenerDocsPendientesBodyResponse implements Serializable {

    private int op;
    private int ID_Documento;
    private String cod_Proveedor;
    private String fec_Vencimiento;
    private String serie;
    private int Tipo_Documento;
    private int numero;
    private BigDecimal saldo;
    private BigDecimal importe;
    private BigDecimal factura_Pagada;
    private String fec_Programada_pago;

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }

    public int getID_Documento() {
        return ID_Documento;
    }

    public void setID_Documento(int ID_Documento) {
        this.ID_Documento = ID_Documento;
    }

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

    public int getTipo_Documento() {
        return Tipo_Documento;
    }

    public void setTipo_Documento(int tipo_Documento) {
        Tipo_Documento = tipo_Documento;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
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
        return "ObtenerDocsPendientesBodyResponse{" +
                "op=" + op +
                ", ID_Documento=" + ID_Documento +
                ", cod_Proveedor='" + cod_Proveedor + '\'' +
                ", fec_Vencimiento='" + fec_Vencimiento + '\'' +
                ", serie='" + serie + '\'' +
                ", Tipo_Documento=" + Tipo_Documento +
                ", numero=" + numero +
                ", saldo=" + saldo +
                ", importe=" + importe +
                ", factura_Pagada=" + factura_Pagada +
                ", fec_Programada_pago='" + fec_Programada_pago + '\'' +
                '}';
    }
}
