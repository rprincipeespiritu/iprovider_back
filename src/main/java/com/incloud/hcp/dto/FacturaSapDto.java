package com.incloud.hcp.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class FacturaSapDto {
    private String NroFactura;
    private LocalDateTime FechaCreacionFactura;
    private String Moneda;
    private double Monto;
    private String Vencido;
    private String Estado;
    private String Proveedor;
    private String NombreProveedor;
    private LocalDateTime FechaVencimiento;
    private String DocumentoContabilizacion;


    public String getDocumentoContabilizacion() {
        return DocumentoContabilizacion;
    }

    public void setDocumentoContabilizacion(String documentoContabilizacion) {
        DocumentoContabilizacion = documentoContabilizacion;
    }

    public String getProveedor() {
        return Proveedor;
    }

    public void setProveedor(String proveedor) {
        Proveedor = proveedor;
    }

    public String getNombreProveedor() {
        return NombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        NombreProveedor = nombreProveedor;
    }

    public FacturaSapDto() {
    }

    public String getNroFactura() {
        return NroFactura;
    }

    public void setNroFactura(String nroFactura) {
        NroFactura = nroFactura;
    }

    // Getter y Setter para FechaCreacionFactura
    public LocalDateTime getfechaCreacionFactura() {
        return FechaCreacionFactura;
    }

    public void setFechaCreacionFactura(String fechaCreacionFactura) {
        // Procesar el formato /Date(1730332800000)/ para extraer el timestamp
        if (fechaCreacionFactura != null && fechaCreacionFactura.contains("/Date(") && fechaCreacionFactura.contains(")/")) {
            // Extraer el timestamp del formato /Date(1730332800000)/
            String timestampStr = fechaCreacionFactura.substring(6, fechaCreacionFactura.length() - 2);
            try {
                long timestamp = Long.parseLong(timestampStr);  // Convertir a long
                // Convertir el timestamp a LocalDateTime
                this.FechaCreacionFactura = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
            } catch (NumberFormatException e) {
                this.FechaCreacionFactura = null;  // Si no se puede convertir, establecer como null
            }
        }
    }
    // Getter y Setter para fechadevencimiento
    public LocalDateTime getFechaVencimiento() {
        return FechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        // Procesar el formato /Date(1730332800000)/ para extraer el timestamp
        if (fechaVencimiento != null && fechaVencimiento.contains("/Date(") && fechaVencimiento.contains(")/")) {
            // Extraer el timestamp del formato /Date(1730332800000)/
            String timestampStr = fechaVencimiento.substring(6, fechaVencimiento.length() - 2);
            try {
                long timestamp = Long.parseLong(timestampStr);  // Convertir a long
                // Convertir el timestamp a LocalDateTime
                this.FechaVencimiento = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
            } catch (NumberFormatException e) {
                this.FechaVencimiento = null;  // Si no se puede convertir, establecer como null
            }
        }
    }

    public String getMoneda() {
        return Moneda;
    }

    public void setMoneda(String moneda) {
        Moneda = moneda;
    }

    public double getMonto() {
        return Monto;
    }

    public void setMonto(double monto) {
        Monto = monto;
    }

    public String getVencido() {
        return Vencido;
    }

    public void setVencido(String vencido) {
        Vencido = vencido;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }
}
