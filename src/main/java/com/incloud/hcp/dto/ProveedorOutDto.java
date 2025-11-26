package com.incloud.hcp.dto;

import java.util.Date;

public class ProveedorOutDto {

    private Integer idProveedor;
    private String cargoRepresentanteLegal;
    private String cargoPersonaTesoreria;
    private String cargoPersonaCreditoCobranza;
    private String celular;
    private String celularPersonaCompra;
    private String contacto;
    private String codigoPostal;
    private String direccionFiscal;
    private String email;
    private String emailRepresentanteLegal;
    private String emailRepresentanteLegal2;
    private String emailRepresentanteLegal3;
    private String emailRepresentanteLegal4;
    private String emailPersonaTesoreria;
    private String emailPersonaTesoreria2;
    private String emailPersonaTesoreria3;
    private String emailPersonaTesoreria4;
    private String emailPersonaCreditoCobranza;
    private String emailPersonaCreditoCobranza2;
    private String emailPersonaCreditoCobranza3;
    private String emailPersonaCreditoCobranza4;
    private String emailPersonaCompra;
    private String emailPersonaCompra2;
    private String emailPersonaCompra3;
    private String emailPersonaCompra4;
    private Integer idPais;
    private Integer idRegion;
    private Integer idProvincia;
    private Integer idDistrito;
    private String bancoExtranjero;
    private Integer idTipoProveedor;
    private Integer idCondicionPago;
    private Integer idMoneda;
    private Integer idTipoComprobante;
    private String indTipoVentaBien;
    private String indTipoVentaServicio;
    private String indTipoFacturacionManual;
    private String indTipoFacturacionElect;
    private String nombreRepresentanteLegal;
    private String nroDocumRepresentanteLegal;
    private String nombrePersonaTesoreria;
    private String nroDocumPersonaTesoreria;
    private String nombrePersonaCreditoCobranza;
    private String nroDocumPersonaCreditoCobranza;
    private String nombrePersonaCompra;
    private String operacionesAfectas;
    private String razonSocial;
    private String ruc;
    private String telefono;
    private String tipoPersona;
    private Integer idBanco;
    private Boolean flagCuentabancaria;

    private String acredorCodigoSap;
    private String activo;
    private String emailRetencion;
    private Double evalHomologacion;
    private Double evalDesempeno;
    private Date fechaCreacion;
    private Date fechaModificacion;
    private String idHcp;
    private String territAmazonia;
    private Integer usuarioCreacion;
    private Integer idEstadoProveedor;
    private String indAceptacion;
    private Integer idAreaCompra;

    public Integer getIdAreaCompra() {
        return idAreaCompra;
    }

    public void setIdAreaCompra(Integer idAreaCompra) {
        this.idAreaCompra = idAreaCompra;
    }

    private String celularRepresentanteLegal;

    public String getCelularRepresentanteLegal() {
        return celularRepresentanteLegal;
    }

    public void setCelularRepresentanteLegal(String celularRepresentanteLegal) {
        this.celularRepresentanteLegal = celularRepresentanteLegal;
    }

    public String getCelularPersonaCreditoCobranza() {
        return celularPersonaCreditoCobranza;
    }

    public void setCelularPersonaCreditoCobranza(String celularPersonaCreditoCobranza) {
        this.celularPersonaCreditoCobranza = celularPersonaCreditoCobranza;
    }

    public String getCelularTesoreria() {
        return celularTesoreria;
    }

    public void setCelularTesoreria(String celularTesoreria) {
        this.celularTesoreria = celularTesoreria;
    }

    private String celularPersonaCreditoCobranza;

    private String celularTesoreria;

    private Integer idTipoBeneficiario;
    private String cuentaBeneficiario;
    private String nombreBeneficiario;
    private String direccionBeneficiario;
    private String ciudadBeneficiario;
    private String referenciaBeneficiario;
    private String nombreBancoCtaExtranjero;
    private String ciudadBancoCtaExtranjero;
    private String direccionBancoCtaExtranjero;
    private String tipoCodigoBancoCtaExtranjero;
    private String codigoBancoCtaExtranjero;
    private Integer idPaisBeneficiario;
    private Integer idEstadoBeneficiario;
    private Integer idPaisBancoExtranjero;
    private Integer idEstadoBancoExtranjero;

    public String getNombreBeneficiario() {
        return nombreBeneficiario;
    }

    public void setNombreBeneficiario(String nombreBeneficiario) {
        this.nombreBeneficiario = nombreBeneficiario;
    }

    public String getDireccionBeneficiario() {
        return direccionBeneficiario;
    }

    public void setDireccionBeneficiario(String direccionBeneficiario) {
        this.direccionBeneficiario = direccionBeneficiario;
    }

    public String getBancoExtranjero() {
        return bancoExtranjero;
    }

    public void setBancoExtranjero(String bancoExtranjero) {
        this.bancoExtranjero = bancoExtranjero;
    }

    public String getCiudadBeneficiario() {
        return ciudadBeneficiario;
    }

    public void setCiudadBeneficiario(String ciudadBeneficiario) {
        this.ciudadBeneficiario = ciudadBeneficiario;
    }

    public String getReferenciaBeneficiario() {
        return referenciaBeneficiario;
    }

    public void setReferenciaBeneficiario(String referenciaBeneficiario) {
        this.referenciaBeneficiario = referenciaBeneficiario;
    }

    public String getNombreBancoCtaExtranjero() {
        return nombreBancoCtaExtranjero;
    }

    public void setNombreBancoCtaExtranjero(String nombreBancoCtaExtranjero) {
        this.nombreBancoCtaExtranjero = nombreBancoCtaExtranjero;
    }

    public Integer getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(Integer idBanco) {
        this.idBanco = idBanco;
    }

    public String getCiudadBancoCtaExtranjero() {
        return ciudadBancoCtaExtranjero;
    }

    public void setCiudadBancoCtaExtranjero(String ciudadBancoCtaExtranjero) {
        this.ciudadBancoCtaExtranjero = ciudadBancoCtaExtranjero;
    }

    public String getDireccionBancoCtaExtranjero() {
        return direccionBancoCtaExtranjero;
    }

    public void setDireccionBancoCtaExtranjero(String direccionBancoCtaExtranjero) {
        this.direccionBancoCtaExtranjero = direccionBancoCtaExtranjero;
    }

    public String getTipoCodigoBancoCtaExtranjero() {
        return tipoCodigoBancoCtaExtranjero;
    }

    public void setTipoCodigoBancoCtaExtranjero(String tipoCodigoBancoCtaExtranjero) {
        this.tipoCodigoBancoCtaExtranjero = tipoCodigoBancoCtaExtranjero;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getCodigoBancoCtaExtranjero() {
        return codigoBancoCtaExtranjero;
    }

    public void setCodigoBancoCtaExtranjero(String codigoBancoCtaExtranjero) {
        this.codigoBancoCtaExtranjero = codigoBancoCtaExtranjero;
    }

    public Integer getIdPaisBeneficiario() {
        return idPaisBeneficiario;
    }

    public void setIdPaisBeneficiario(Integer idPaisBeneficiario) {
        this.idPaisBeneficiario = idPaisBeneficiario;
    }

    public Integer getIdEstadoBeneficiario() {
        return idEstadoBeneficiario;
    }

    public void setIdEstadoBeneficiario(Integer idEstadoBeneficiario) {
        this.idEstadoBeneficiario = idEstadoBeneficiario;
    }

    public Integer getIdPaisBancoExtranjero() {
        return idPaisBancoExtranjero;
    }

    public void setIdPaisBancoExtranjero(Integer idPaisBancoExtranjero) {
        this.idPaisBancoExtranjero = idPaisBancoExtranjero;
    }

    public Integer getIdEstadoBancoExtranjero() {
        return idEstadoBancoExtranjero;
    }

    public void setIdEstadoBancoExtranjero(Integer idEstadoBancoExtranjero) {
        this.idEstadoBancoExtranjero = idEstadoBancoExtranjero;
    }

    public ProveedorOutDto() {
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getCargoRepresentanteLegal() {
        return cargoRepresentanteLegal;
    }

    public void setCargoRepresentanteLegal(String cargoRepresentanteLegal) {
        this.cargoRepresentanteLegal = cargoRepresentanteLegal;
    }

    public Boolean getFlagCuentabancaria() {
        return flagCuentabancaria;
    }

    public void setFlagCuentabancaria(Boolean flagCuentabancaria) {
        this.flagCuentabancaria = flagCuentabancaria;
    }

    public String getCargoPersonaTesoreria() {
        return cargoPersonaTesoreria;
    }

    public void setCargoPersonaTesoreria(String cargoPersonaTesoreria) {
        this.cargoPersonaTesoreria = cargoPersonaTesoreria;
    }

    public String getCargoPersonaCreditoCobranza() {
        return cargoPersonaCreditoCobranza;
    }

    public void setCargoPersonaCreditoCobranza(String cargoPersonaCreditoCobranza) {
        this.cargoPersonaCreditoCobranza = cargoPersonaCreditoCobranza;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCelularPersonaCompra() {
        return celularPersonaCompra;
    }

    public void setCelularPersonaCompra(String celularPersonaCompra) {
        this.celularPersonaCompra = celularPersonaCompra;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getDireccionFiscal() {
        return direccionFiscal;
    }

    public void setDireccionFiscal(String direccionFiscal) {
        this.direccionFiscal = direccionFiscal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailRepresentanteLegal() {
        return emailRepresentanteLegal;
    }

    private String indDetraccion;

    private Boolean indEmiteRecibo;

    public Boolean getIndEmiteRecibo() {
        return indEmiteRecibo;
    }

    public void setIndEmiteRecibo(Boolean indEmiteRecibo) {
        this.indEmiteRecibo = indEmiteRecibo;
    }

    public String getIndDetraccion() {
        return indDetraccion;
    }

    public void setIndDetraccion(String indDetraccion) {
        this.indDetraccion = indDetraccion;
    }

    public void setEmailRepresentanteLegal(String emailRepresentanteLegal) {
        this.emailRepresentanteLegal = emailRepresentanteLegal;
    }

    public String getEmailRepresentanteLegal2() {
        return emailRepresentanteLegal2;
    }

    public void setEmailRepresentanteLegal2(String emailRepresentanteLegal2) {
        this.emailRepresentanteLegal2 = emailRepresentanteLegal2;
    }

    public String getEmailRepresentanteLegal3() {
        return emailRepresentanteLegal3;
    }

    public void setEmailRepresentanteLegal3(String emailRepresentanteLegal3) {
        this.emailRepresentanteLegal3 = emailRepresentanteLegal3;
    }

    public String getEmailRepresentanteLegal4() {
        return emailRepresentanteLegal4;
    }

    public void setEmailRepresentanteLegal4(String emailRepresentanteLegal4) {
        this.emailRepresentanteLegal4 = emailRepresentanteLegal4;
    }

    public String getEmailPersonaTesoreria() {
        return emailPersonaTesoreria;
    }

    public void setEmailPersonaTesoreria(String emailPersonaTesoreria) {
        this.emailPersonaTesoreria = emailPersonaTesoreria;
    }

    public String getEmailPersonaTesoreria2() {
        return emailPersonaTesoreria2;
    }

    public void setEmailPersonaTesoreria2(String emailPersonaTesoreria2) {
        this.emailPersonaTesoreria2 = emailPersonaTesoreria2;
    }

    public String getEmailPersonaTesoreria3() {
        return emailPersonaTesoreria3;
    }

    public void setEmailPersonaTesoreria3(String emailPersonaTesoreria3) {
        this.emailPersonaTesoreria3 = emailPersonaTesoreria3;
    }

    public String getEmailPersonaTesoreria4() {
        return emailPersonaTesoreria4;
    }

    public void setEmailPersonaTesoreria4(String emailPersonaTesoreria4) {
        this.emailPersonaTesoreria4 = emailPersonaTesoreria4;
    }

    public String getEmailPersonaCreditoCobranza() {
        return emailPersonaCreditoCobranza;
    }

    public void setEmailPersonaCreditoCobranza(String emailPersonaCreditoCobranza) {
        this.emailPersonaCreditoCobranza = emailPersonaCreditoCobranza;
    }

    public String getEmailPersonaCreditoCobranza2() {
        return emailPersonaCreditoCobranza2;
    }

    public void setEmailPersonaCreditoCobranza2(String emailPersonaCreditoCobranza2) {
        this.emailPersonaCreditoCobranza2 = emailPersonaCreditoCobranza2;
    }

    public String getEmailPersonaCreditoCobranza3() {
        return emailPersonaCreditoCobranza3;
    }

    public void setEmailPersonaCreditoCobranza3(String emailPersonaCreditoCobranza3) {
        this.emailPersonaCreditoCobranza3 = emailPersonaCreditoCobranza3;
    }

    public String getEmailPersonaCreditoCobranza4() {
        return emailPersonaCreditoCobranza4;
    }

    public void setEmailPersonaCreditoCobranza4(String emailPersonaCreditoCobranza4) {
        this.emailPersonaCreditoCobranza4 = emailPersonaCreditoCobranza4;
    }

    public String getEmailPersonaCompra() {
        return emailPersonaCompra;
    }

    public void setEmailPersonaCompra(String emailPersonaCompra) {
        this.emailPersonaCompra = emailPersonaCompra;
    }

    public String getEmailPersonaCompra2() {
        return emailPersonaCompra2;
    }

    public void setEmailPersonaCompra2(String emailPersonaCompra2) {
        this.emailPersonaCompra2 = emailPersonaCompra2;
    }

    public String getEmailPersonaCompra3() {
        return emailPersonaCompra3;
    }

    public void setEmailPersonaCompra3(String emailPersonaCompra3) {
        this.emailPersonaCompra3 = emailPersonaCompra3;
    }

    public String getEmailPersonaCompra4() {
        return emailPersonaCompra4;
    }

    public void setEmailPersonaCompra4(String emailPersonaCompra4) {
        this.emailPersonaCompra4 = emailPersonaCompra4;
    }

    public Integer getIdPais() {
        return idPais;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }

    public Integer getIdRegion() {
        return idRegion;
    }

    public void setIdRegion(Integer idRegion) {
        this.idRegion = idRegion;
    }

    public Integer getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(Integer idProvincia) {
        this.idProvincia = idProvincia;
    }

    public Integer getIdDistrito() {
        return idDistrito;
    }

    public void setIdDistrito(Integer idDistrito) {
        this.idDistrito = idDistrito;
    }

    public Integer getIdTipoProveedor() {
        return idTipoProveedor;
    }

    public void setIdTipoProveedor(Integer idTipoProveedor) {
        this.idTipoProveedor = idTipoProveedor;
    }

    public Integer getIdCondicionPago() {
        return idCondicionPago;
    }

    public void setIdCondicionPago(Integer idCondicionPago) {
        this.idCondicionPago = idCondicionPago;
    }

    public Integer getIdMoneda() {
        return idMoneda;
    }

    public void setIdMoneda(Integer idMoneda) {
        this.idMoneda = idMoneda;
    }

    public Integer getIdTipoComprobante() {
        return idTipoComprobante;
    }

    public void setIdTipoComprobante(Integer idTipoComprobante) {
        this.idTipoComprobante = idTipoComprobante;
    }

    public String getIndTipoVentaBien() {
        return indTipoVentaBien;
    }

    public void setIndTipoVentaBien(String indTipoVentaBien) {
        this.indTipoVentaBien = indTipoVentaBien;
    }

    public String getIndTipoVentaServicio() {
        return indTipoVentaServicio;
    }

    public void setIndTipoVentaServicio(String indTipoVentaServicio) {
        this.indTipoVentaServicio = indTipoVentaServicio;
    }

    public String getIndTipoFacturacionManual() {
        return indTipoFacturacionManual;
    }

    public void setIndTipoFacturacionManual(String indTipoFacturacionManual) {
        this.indTipoFacturacionManual = indTipoFacturacionManual;
    }

    public String getIndTipoFacturacionElect() {
        return indTipoFacturacionElect;
    }

    public void setIndTipoFacturacionElect(String indTipoFacturacionElect) {
        this.indTipoFacturacionElect = indTipoFacturacionElect;
    }

    public String getNombreRepresentanteLegal() {
        return nombreRepresentanteLegal;
    }

    public void setNombreRepresentanteLegal(String nombreRepresentanteLegal) {
        this.nombreRepresentanteLegal = nombreRepresentanteLegal;
    }

    public String getNroDocumRepresentanteLegal() {
        return nroDocumRepresentanteLegal;
    }

    public void setNroDocumRepresentanteLegal(String nroDocumRepresentanteLegal) {
        this.nroDocumRepresentanteLegal = nroDocumRepresentanteLegal;
    }

    public String getNombrePersonaTesoreria() {
        return nombrePersonaTesoreria;
    }

    public void setNombrePersonaTesoreria(String nombrePersonaTesoreria) {
        this.nombrePersonaTesoreria = nombrePersonaTesoreria;
    }

    public String getNroDocumPersonaTesoreria() {
        return nroDocumPersonaTesoreria;
    }

    public void setNroDocumPersonaTesoreria(String nroDocumPersonaTesoreria) {
        this.nroDocumPersonaTesoreria = nroDocumPersonaTesoreria;
    }

    public String getNombrePersonaCreditoCobranza() {
        return nombrePersonaCreditoCobranza;
    }

    public void setNombrePersonaCreditoCobranza(String nombrePersonaCreditoCobranza) {
        this.nombrePersonaCreditoCobranza = nombrePersonaCreditoCobranza;
    }

    public String getNroDocumPersonaCreditoCobranza() {
        return nroDocumPersonaCreditoCobranza;
    }

    public void setNroDocumPersonaCreditoCobranza(String nroDocumPersonaCreditoCobranza) {
        this.nroDocumPersonaCreditoCobranza = nroDocumPersonaCreditoCobranza;
    }

    public String getNombrePersonaCompra() {
        return nombrePersonaCompra;
    }

    public void setNombrePersonaCompra(String nombrePersonaCompra) {
        this.nombrePersonaCompra = nombrePersonaCompra;
    }

    public String getOperacionesAfectas() {
        return operacionesAfectas;
    }

    public void setOperacionesAfectas(String operacionesAfectas) {
        this.operacionesAfectas = operacionesAfectas;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getAcredorCodigoSap() {
        return acredorCodigoSap;
    }

    public void setAcredorCodigoSap(String acredorCodigoSap) {
        this.acredorCodigoSap = acredorCodigoSap;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getEmailRetencion() {
        return emailRetencion;
    }

    public void setEmailRetencion(String emailRetencion) {
        this.emailRetencion = emailRetencion;
    }

    public Double getEvalHomologacion() {
        return evalHomologacion;
    }

    public void setEvalHomologacion(Double evalHomologacion) {
        this.evalHomologacion = evalHomologacion;
    }

    public Double getEvalDesempeno() {
        return evalDesempeno;
    }

    public void setEvalDesempeno(Double evalDesempeno) {
        this.evalDesempeno = evalDesempeno;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getIdHcp() {
        return idHcp;
    }

    public void setIdHcp(String idHcp) {
        this.idHcp = idHcp;
    }

    public String getTerritAmazonia() {
        return territAmazonia;
    }

    public void setTerritAmazonia(String territAmazonia) {
        this.territAmazonia = territAmazonia;
    }

    public Integer getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(Integer usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public Integer getIdEstadoProveedor() {
        return idEstadoProveedor;
    }

    public void setIdEstadoProveedor(Integer idEstadoProveedor) {
        this.idEstadoProveedor = idEstadoProveedor;
    }

    public String getIndAceptacion() {
        return indAceptacion;
    }

    public void setIndAceptacion(String indAceptacion) {
        this.indAceptacion = indAceptacion;
    }

    public Integer getIdTipoBeneficiario() {
        return idTipoBeneficiario;
    }

    public void setIdTipoBeneficiario(Integer idTipoBeneficiario) {
        this.idTipoBeneficiario = idTipoBeneficiario;
    }

    public String getCuentaBeneficiario() {
        return cuentaBeneficiario;
    }

    public void setCuentaBeneficiario(String cuentaBeneficiario) {
        this.cuentaBeneficiario = cuentaBeneficiario;
    }

    @Override
    public String toString() {
        return "ProveedorOutDto{" +
            "idProveedor=" + idProveedor +
            ", cargoRepresentanteLegal='" + cargoRepresentanteLegal + '\'' +
            ", cargoPersonaTesoreria='" + cargoPersonaTesoreria + '\'' +
            ", cargoPersonaCreditoCobranza='" + cargoPersonaCreditoCobranza + '\'' +
            ", celular='" + celular + '\'' +
            ", celularPersonaCompra='" + celularPersonaCompra + '\'' +
            ", contacto='" + contacto + '\'' +
            ", codigoPostal='" + codigoPostal + '\'' +
            ", direccionFiscal='" + direccionFiscal + '\'' +
            ", email='" + email + '\'' +

            ", emailRepresentanteLegal='" + emailRepresentanteLegal + '\'' +
            ", emailRepresentanteLegal2='" + emailRepresentanteLegal2 + '\'' +
            ", emailRepresentanteLegal3='" + emailRepresentanteLegal3 + '\'' +
            ", emailRepresentanteLegal4='" + emailRepresentanteLegal4 + '\'' +

            ", emailPersonaTesoreria='" + emailPersonaTesoreria + '\'' +
            ", emailPersonaTesoreria2='" + emailPersonaTesoreria2 + '\'' +
            ", emailPersonaTesoreria3='" + emailPersonaTesoreria3 + '\'' +
            ", emailPersonaTesoreria4='" + emailPersonaTesoreria4 + '\'' +

            ", emailPersonaCreditoCobranza='" + emailPersonaCreditoCobranza + '\'' +
            ", emailPersonaCreditoCobranza2='" + emailPersonaCreditoCobranza2 + '\'' +
            ", emailPersonaCreditoCobranza3='" + emailPersonaCreditoCobranza3 + '\'' +
            ", emailPersonaCreditoCobranza4='" + emailPersonaCreditoCobranza4 + '\'' +

            ", emailPersonaCompra='" + emailPersonaCompra + '\'' +
            ", emailPersonaCompra2='" + emailPersonaCompra2 + '\'' +
            ", emailPersonaCompra3='" + emailPersonaCompra3 + '\'' +
            ", emailPersonaCompra4='" + emailPersonaCompra4 + '\'' +

            ", idPais=" + idPais +
            ", idRegion=" + idRegion +
            ", idProvincia=" + idProvincia +
            ", idDistrito=" + idDistrito +
            ", idTipoProveedor=" + idTipoProveedor +
            ", idCondicionPago=" + idCondicionPago +
            ", idMoneda=" + idMoneda +
            ", idTipoComprobante=" + idTipoComprobante +
            ", indTipoVentaBien='" + indTipoVentaBien + '\'' +
            ", indTipoVentaServicio='" + indTipoVentaServicio + '\'' +
            ", indTipoFacturacionManual='" + indTipoFacturacionManual + '\'' +
            ", indTipoFacturacionElect='" + indTipoFacturacionElect + '\'' +
            ", nombreRepresentanteLegal='" + nombreRepresentanteLegal + '\'' +
            ", nroDocumRepresentanteLegal='" + nroDocumRepresentanteLegal + '\'' +
            ", nombrePersonaTesoreria='" + nombrePersonaTesoreria + '\'' +
            ", nroDocumPersonaTesoreria='" + nroDocumPersonaTesoreria + '\'' +
            ", nombrePersonaCreditoCobranza='" + nombrePersonaCreditoCobranza + '\'' +
            ", nroDocumPersonaCreditoCobranza='" + nroDocumPersonaCreditoCobranza + '\'' +
            ", nombrePersonaCompra='" + nombrePersonaCompra + '\'' +
            ", operacionesAfectas='" + operacionesAfectas + '\'' +
            ", razonSocial='" + razonSocial + '\'' +
            ", ruc='" + ruc + '\'' +
            ", telefono='" + telefono + '\'' +
            ", tipoPersona='" + tipoPersona + '\'' +
            ", acredorCodigoSap='" + acredorCodigoSap + '\'' +
            ", activo='" + activo + '\'' +
            ", emailRetencion='" + emailRetencion + '\'' +
            ", evalHomologacion=" + evalHomologacion +
            ", evalDesempeno=" + evalDesempeno +
            ", fechaCreacion=" + fechaCreacion +
            ", fechaModificacion=" + fechaModificacion +
            ", idHcp='" + idHcp + '\'' +
            ", territAmazonia='" + territAmazonia + '\'' +
            ", usuarioCreacion=" + usuarioCreacion +
            ", idEstadoProveedor=" + idEstadoProveedor +
            ", indAceptacion='" + indAceptacion + '\'' +
            ", idTipoBeneficiario=" + idTipoBeneficiario +
            ", cuentaBeneficiario='" + cuentaBeneficiario + '\'' +
            ", nombreBeneficiario='" + nombreBeneficiario + '\'' +
            ", direccionBeneficiario='" + direccionBeneficiario + '\'' +
            ", ciudadBeneficiario='" + ciudadBeneficiario + '\'' +
            ", referenciaBeneficiario='" + referenciaBeneficiario + '\'' +
            ", nombreBancoCtaExtranjero='" + nombreBancoCtaExtranjero + '\'' +
            ", ciudadBancoCtaExtranjero='" + ciudadBancoCtaExtranjero + '\'' +
            ", direccionBancoCtaExtranjero='" + direccionBancoCtaExtranjero + '\'' +
            ", tipoCodigoBancoCtaExtranjero='" + tipoCodigoBancoCtaExtranjero + '\'' +
            ", codigoBancoCtaExtranjero='" + codigoBancoCtaExtranjero + '\'' +
            ", idPaisBeneficiario=" + idPaisBeneficiario +
            ", idEstadoBeneficiario=" + idEstadoBeneficiario +
            ", idPaisBancoExtranjero=" + idPaisBancoExtranjero +
            ", idEstadoBancoExtranjero=" + idEstadoBancoExtranjero +
            ", idBanco=" + idBanco +
            ", bancoExtranjero='" + bancoExtranjero + '\'' +
            ", idAreaCompra='" + idAreaCompra + '\'' +


            '}';
    }
}
