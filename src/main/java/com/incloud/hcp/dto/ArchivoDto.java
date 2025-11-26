package com.incloud.hcp.dto;

import com.incloud.hcp.domain.EstadoProveedor;
import com.incloud.hcp.domain.ProveedorCliente;
import com.incloud.hcp.domain.ProveedorFuncionario;
import com.incloud.hcp.domain.ProveedorInstalacion;
import com.incloud.hcp.domain.ProveedorPermiso;
import com.incloud.hcp.domain.ProveedorPreguntaInformacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrador on 30/08/2017.
 */
public class ArchivoDto {

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getContenidoBase64() {
        return contenidoBase64;
    }

    public void setContenidoBase64(String contenidoBase64) {
        this.contenidoBase64 = contenidoBase64;
    }

    private String nombre;
    private String extension;
    private String contenidoBase64;


    @Override
    public String toString() {
        return "ProveedorDto{" +
            "nombre=" + nombre +
            ", extension='" + extension + '\'' +
            ", contenidoBase64='" + contenidoBase64 + '\'' +
            '}';
    }
}
