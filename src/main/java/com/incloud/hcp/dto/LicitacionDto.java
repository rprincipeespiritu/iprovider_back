package com.incloud.hcp.dto;

import com.incloud.hcp.domain.ClaseDocumento;
import com.incloud.hcp.domain.Moneda;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LicitacionDto implements Serializable {

    private Integer idLicitacion;
    private String comentarioAnulacion;
    private String comentarioLicitacion;
    private String estadoLicitacion;
    private Timestamp fechaInicioRecepcionOferta;
    private Timestamp fechaCierreRecepcionOferta;
    private Timestamp fechaUltimaRenegociacion;
    private Timestamp fechaCierreConfirmacionParticipacion;
    private Timestamp fechaCierreConsultaPregunta;
    private Timestamp fechaCreacion;
    private Timestamp fechaEntregaInicio;
    private Timestamp fechaModificacion;
    private Timestamp fechaPublicacion;
    private String necesidadUrgencia;
    private Integer nroLicitacion;
    private Integer anioLicitacion;
    private String usuarioCreacion;
    private String usuarioModificacion;
    private String puntoEntrega;
    private String usuarioPublicacionId;
    private String usuarioPublicacionName;
    private String usuarioPublicacionEmail;
    private String usuarioAnulacionId;
    private String indRepublicado;
    private String indActivarCotizacion;
    private String indEjecucionSapOk;
    private String indCreacionProveedorSapOk;
    private String indCreacionOcSapOk;
    private String indCreacionCmSapOk;
    private String numeroPeticionOfertaLicitacionSap;
    private String nombreLicitacion;

    private String fechaCierreRecepcionOfertaString;
    private String fechaEntregaInicioString;

    private ClaseDocumento claseDocumento;
    private Moneda moneda;
}
