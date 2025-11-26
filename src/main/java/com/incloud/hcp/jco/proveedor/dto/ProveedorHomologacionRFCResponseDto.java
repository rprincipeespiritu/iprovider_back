package com.incloud.hcp.jco.proveedor.dto;

import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.dto.ProveedorDto;
import com.incloud.hcp.sap.SapLog;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ProveedorHomologacionRFCResponseDto implements Serializable {

    private String nroAcreedor;
    private Date fechaIniHomInterna;
    private Date fechaFinHomInterna;

    private Date fechaIniHomExterna;
    private Date fechaFinHomExterna;
    private SapLog sapLog;

    public SapLog getSapLog() {
        return sapLog;
    }

    public void setSapLog(SapLog sapLog) {
        this.sapLog = sapLog;
    }

    public String getNroAcreedor() {
        return nroAcreedor;
    }
    public void setNroAcreedor(String nroAcreedor) {
        this.nroAcreedor = nroAcreedor;
    }

    public Date getFechaIniHomInterna() {
        return fechaIniHomInterna;
    }
    public void setFechaIniHomInterna(Date fechaIniHomInterna) {
        this.fechaIniHomInterna = fechaIniHomInterna;
    }

    public Date getFechaFinHomInterna() {
        return fechaFinHomInterna;
    }
    public void setFechaFinHomInterna(Date fechaFinHomInterna) {
        this.fechaFinHomInterna = fechaFinHomInterna;
    }

    public Date getFechaIniHomExterna() {
        return fechaIniHomExterna;
    }
    public void setFechaIniHomExterna(Date fechaIniHomExterna) {
        this.fechaIniHomExterna = fechaIniHomExterna;
    }

    public Date getFechaFinHomExterna() {
        return fechaFinHomExterna;
    }
    public void setFechaFinHomExterna(Date fechaFinHomExterna) {
        this.fechaFinHomExterna = fechaFinHomExterna;
    }


    @Override
    public String toString() {
        return "ProveedorRFCResponseDto{" +
                ", nroAcreedor='" + nroAcreedor +
                '}';
    }
}
