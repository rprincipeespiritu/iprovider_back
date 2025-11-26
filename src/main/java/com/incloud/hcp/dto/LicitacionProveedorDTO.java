package com.incloud.hcp.dto;

import com.incloud.hcp.domain.Licitacion;
import com.incloud.hcp.domain.LicitacionProveedorPK;
import com.incloud.hcp.domain.Proveedor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LicitacionProveedorDTO implements Serializable {

    private LicitacionProveedorPK id;
    private Timestamp fechaRegistro;
    private BigDecimal evaluacionDesempeno;
    private BigDecimal evaluacionHomologacion;
    private Integer noConformes;
    private Date fechaCierreRecepcion;
    private String indSiParticipa;
    private boolean enviaCorreoProveedor;
    private Date fechaConfirmacionParticipacion;
    private String fechaCierreRecepcionProveedorString;

    private LicitacionDto licitacion;
    private ProveedorLicitacionDto proveedor;

    // Getters y Setters

}
