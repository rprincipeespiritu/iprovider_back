package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;
import com.incloud.hcp.validation.FixedLength;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the proveedor database table.
 *
 */
@Entity
@Table(name="mig_proveedor")
public class MigProveedor extends BaseDomain implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id")
    private Integer id;

    @Column(name="acreedor_codigo_sap", length=10)
    private String acreedorCodigoSap;


    @Column(length=100)
    private String contacto;

    @Column(name="direccion_fiscal", length=500)
    private String direccionFiscal;

    @Column(name="email", length=80)
    private String email;

    @Column(name="evaluacion_desempeno",  precision=5, scale=2)
    private BigDecimal evaluacionDesempeno;

    @Column(name="evaluacion_homologacion", precision=5, scale=2)
    private BigDecimal evaluacionHomologacion;

    @Column(name="fecha_creacion")
    private Date fechaCreacion;

    @Column(name="fecha_modificacion")
    private Date fechaModificacion;

    @Column(name="ind_black_list", length=1)
    private String indBlackList;

    @Column(name="ind_bloqueado_sap", length=1)
    private String indBloqueadoSap;

    @Column(name="ind_homologado", length=1)
    private String indHomologado;

    @Column(name="ind_proveedor_comunidad", length=1)
    private String indProveedorComunidad;

    @Column(name="ind_sujeto_retencion", length=1)
    private String indSujetoRetencion;

    @Column(length=30)
    private String password;

    @Column(name="razon_social",  length=300)
    private String razonSocial;

    @Column(length=16)
    private String ruc;

    @Column(name="telefono", length=30)
    private String telefono;

    @Column(name="tipo_persona", length=1)
    private String tipoPersona;

    @Column(name="usuario_creacion")
    private Integer usuarioCreacion;

    @Column(name="usuario_modificacion")
    private Integer usuarioModificacion;


    //uni-directional many-to-one association to CondicionPago
    @Column(name="id_condicion_pago")
    private String idCondicionPago;

    //uni-directional many-to-one association to Moneda
    @Column(name="id_moneda")
    private String idMoneda;

    //uni-directional many-to-one association to TipoComprobante
    @Column(name="id_tipo_comprobante")
    private Integer idTipoComprobante;

    //uni-directional many-to-one association to TipoProveedor

    @Column(name="id_tipo_proveedor")
    private String idTipoProveedor;

    //uni-directional many-to-one association to Ubigeo
    @Column(name="id_distrito")
    private Integer idDistrito;

    //uni-directional many-to-one association to Ubigeo
    @Column(name="id_provincia")
    private Integer idProvincia;

    //uni-directional many-to-one association to Ubigeo
    @Column(name="id_region")
    private Integer idRegion;

    //uni-directional many-to-one association to Ubigeo
    @Column(name="id_pais")
    private Integer idPais;

    @Column(name="id_tipo_beneficiario")
    private String idTipoBeneficiario;

    @Column(name="cuenta_beneficiario", length=100)
    private String cuentaBeneficiario;

    @Column(name="referencia_proveedor", length=30)
    private String referenciaProveedor;

    @Column(name="carpeta_id", length=60)
    private String carpetaId;

    @Column(name="hcp_id", length=60)
    private String idHcp;

    @Column(name="activo", length=1)
    private String activo;

    @Column(name="email_retencion", length=80)
    private String emailRetencion;


    @Size(max = 100)
    @Column(name = "territorio_region_amazonia", length = 100)
    private String territorioRegionAmazonia;

    @Size(max = 30)
    @Column(name = "codigo_postal", length = 30)
    private String codigoPostal;

    @Size(max = 30)
    @Column(name = "celular", length = 30)
    private String celular;

    @FixedLength(length = 1)
    @Column(name = "ind_habido_sunat", length = 1)
    private String indHabidoSunat;

    @FixedLength(length = 1)
    @Column(name = "ind_activo_sunat", length = 1)
    private String indActivoSunat;

    @Size(max = 100)
    @Column(name = "fecha_inicio_acti_sunat", length = 100)
    private String fechaInicioActiSunat;

    @FixedLength(length = 1)
    @Column(name = "ind_creacion_unica_vez", length = 1)
    private String indCreacionUnicaVez;

    @FixedLength(length = 1)
    @Column(name = "ind_proveedor_excepcion", length = 1)
    private String indProveedorExcepcion;

    @Size(max = 1000)
    @Column(name = "codigo_sistema_emision_elect", length = 1000)
    private String codigoSistemaEmisionElect;

    @Size(max = 1000)
    @Column(name = "codigo_comprobante_pago", length = 1000)
    private String codigoComprobantePago;

    @Size(max = 1000)
    @Column(name = "codigo_padron", length = 1000)
    private String codigoPadron;

//    @NotEmpty
    @Size(max = 10)
    @Column(name = "codigo_grupo_compra", length = 10)
    private String codigoGrupoCompra;

//    @NotEmpty
    @Size(max = 10)
    @Column(name = "codigo_grupo_tesoreria", length = 10)
    private String codigoGrupoTesoreria;

    @Size(max = 100)
    @Column(name = "operaciones_afectas", length = 100)
    private String operacionesAfectas;

    @FixedLength(length = 1)
    @Column(name = "ind_tipo_venta_bien", length = 1)
    private String indTipoVentaBien;

    @FixedLength(length = 1)
    @Column(name = "ind_tipo_venta_servicio", length = 1)
    private String indTipoVentaServicio;

    @FixedLength(length = 1)
    @Column(name = "ind_tipo_facturacion_elect", length = 1)
    private String indTipoFacturacionElect;

    @FixedLength(length = 1)
    @Column(name = "ind_tipo_facturacion_manual", length = 1)
    private String indTipoFacturacionManual;

    @Size(max = 100)
    @Column(name = "nombre_representante_legal", length = 100)
    private String nombreRepresentanteLegal;

    @Size(max = 100)
    @Column(name = "cargo_representante_legal", length = 100)
    private String cargoRepresentanteLegal;

    @Size(max = 100)
    @Column(name = "email_representante_legal", length = 100)
    private String emailRepresentanteLegal;

    @Size(max = 40)
    @Column(name = "nro_docum_representante_legal", length = 40)
    private String nroDocumRepresentanteLegal;

    @Size(max = 100)
    @Column(name = "nombre_persona_credito_cobranza", length = 100)
    private String nombrePersonaCreditoCobranza;

    @Size(max = 100)
    @Column(name = "cargo_persona_credito_cobranza", length = 100)
    private String cargoPersonaCreditoCobranza;

    @Size(max = 100)
    @Column(name = "email_persona_credito_cobranza", length = 100)
    private String emailPersonaCreditoCobranza;

    @Size(max = 40)
    @Column(name = "nro_docum_persona_credito_cobranza", length = 40)
    private String nroDocumPersonaCreditoCobranza;

    @Size(max = 100)
    @Column(name = "nombre_persona_tesoreria", length = 100)
    private String nombrePersonaTesoreria;

    @Size(max = 100)
    @Column(name = "cargo_persona_tesoreria", length = 100)
    private String cargoPersonaTesoreria;

    @Size(max = 100)
    @Column(name = "email_persona_tesoreria", length = 100)
    private String emailPersonaTesoreria;

    @Size(max = 40)
    @Column(name = "nro_docum_persona_tesoreria", length = 40)
    private String nroDocumPersonaTesoreria;

    @Size(max = 100)
    @Column(name = "nombre_persona_compra", length = 100)
    private String nombrePersonaCompra;

    @Size(max = 100)
    @Column(name = "cargo_persona_compra", length = 100)
    private String cargoPersonaCompra;

    @Size(max = 100)
    @Column(name = "email_persona_compra", length = 100)
    private String emailPersonaCompra;

    @Size(max = 40)
    @Column(name = "nro_docum_persona_compra", length = 40)
    private String nroDocumPersonaCompra;

    @Size(max = 30)
    @Column(name = "celular_persona_compra", length = 30)
    private String celularPersonaCompra;

    @FixedLength(length = 1)
    @Column(name = "ind_aceptacion", length = 1)
    private String indAceptacion;

    @Size(max = 1000)
    @Column(name = "codigo_actividad_economica", length = 1000)
    private String codigoActividadEconomica;

    @Size(max = 10)
    @Column(name = "codigo_profesion_oficio", length = 10)
    private String codigoProfesionOficio;

    @Size(max = 10)
    @Column(name = "codigo_tipo_proveedor_actividad", length = 10)
    private String codigoTipoProveedorActividad;

    // Many to one
    @Column(name="id_estado_proveedor")
    private Integer idEstadoProveedor;

    @Column(name="ind_migrado_sap", length=1)
    private String indMigradoSap;

    @Column(name="tel_mov", length=30)
    private String telefonoMovil;

    @Column(name="NOMBRE_PROVEEDOR_OSE")
    private String nombreProveedorOse;

    /**JRAMOS UPDATE*/
    @Column(name="IND_AGENTE_RETENCION",length = 1)
    private String indAgenteRetencion;

    @Size(max = 10)
    @Column(name="CODIGO_DIRECCION", length = 10)
    private String codigoDireccion;

    @Column(name="NOMBRE_BENEFICIARIO", length=150)
    private String nombreBeneficiario;

    @Column(name="DIRECCION_BENEFICIARIO", length=100)
    private String direccionBeneficiario;

    @Column(name="CIUDAD_BENEFICIARIO", length=100)
    private String ciudadBeneficiario;

    @Column(name="REFERENCIA_BENEFICIARIO", length=100)
    private String referenciaBeneficiario;

    @Column(name="NOMBRE_BANCO_CTA_EXTRANJERO", length=100)
    private String nombreBancoCtaExtranjero;

    @Column(name="CIUDAD_BANCO_CTA_EXTRANJERO", length=100)
    private String ciudadBancoCtaExtranjero;

    @Column(name="DIRECCION_BANCO_CTA_EXTRANJERO", length=100)
    private String direccionBancoCtaExtranjero;

    @Column(name="TIPO_CODIGO_BANCO_CTA_EXTRANJERO", length=100)
    private String tipoCodigoBancoCtaExtranjero;

    @Column(name="CODIGO_BANCO_CTA_EXTRANJERO", length=100)
    private String codigoBancoCtaExtranjero;


    @Column(name="ID_PAIS_BENEFICIARIO")
    private Integer idPaisBeneficiario;

    @Column(name="ID_ESTADO_BENEFICIARIO")
    private Integer idEstadoBeneficiario;

    @Column(name="ID_PAIS_BANCO_CTA_EXTRANJERO")
    private Integer idPaisBancoExtranjero;

    @Column(name="ID_BANCO")
    private Integer idBanco;

    @Column(name="ID_ESTADO_BANCO_CTA_EXTRANJERO")
    private Integer idEstadoBancoExtranjero;

    @Column(name="FLAG_ACTIVO", length=2)
    private Integer flagActivo;


    @Column(name="IND_MIGRACION_OK",length = 1)
    private String indMigracionOK;

    @Column(name="IND_ERROR_RUC",length = 1)
    private String indErrorRuc;

    @Column(name="IND_ERROR_EMAIL",length = 1)
    private String indErrorMail;

    @Column(name="error",length = 1000)
    private String error;

    @Column(name="idProveedor")
    private Integer idProveedor;

    @Column(name="IND_DETRACCION",length = 5)
    private String indDetraccion;

    @Column(name="COD_AREA_COMPRA",length = 10)
    private String codAreaCompra;

    public String getCodAreaCompra() {
        return codAreaCompra;
    }

    public void setCodAreaCompra(String codAreaCompra) {
        this.codAreaCompra = codAreaCompra;
    }

    public String getIndDetraccion() {
        return indDetraccion;
    }

    public void setIndDetraccion(String indDetraccion) {
        this.indDetraccion = indDetraccion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAcreedorCodigoSap() {
        return acreedorCodigoSap;
    }

    public void setAcreedorCodigoSap(String acreedorCodigoSap) {
        this.acreedorCodigoSap = acreedorCodigoSap;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
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

    public BigDecimal getEvaluacionDesempeno() {
        return evaluacionDesempeno;
    }

    public void setEvaluacionDesempeno(BigDecimal evaluacionDesempeno) {
        this.evaluacionDesempeno = evaluacionDesempeno;
    }

    public BigDecimal getEvaluacionHomologacion() {
        return evaluacionHomologacion;
    }

    public void setEvaluacionHomologacion(BigDecimal evaluacionHomologacion) {
        this.evaluacionHomologacion = evaluacionHomologacion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getIndBlackList() {
        return indBlackList;
    }

    public void setIndBlackList(String indBlackList) {
        this.indBlackList = indBlackList;
    }

    public String getIndBloqueadoSap() {
        return indBloqueadoSap;
    }

    public void setIndBloqueadoSap(String indBloqueadoSap) {
        this.indBloqueadoSap = indBloqueadoSap;
    }

    public String getIndHomologado() {
        return indHomologado;
    }

    public void setIndHomologado(String indHomologado) {
        this.indHomologado = indHomologado;
    }

    public String getIndProveedorComunidad() {
        return indProveedorComunidad;
    }

    public void setIndProveedorComunidad(String indProveedorComunidad) {
        this.indProveedorComunidad = indProveedorComunidad;
    }

    public String getIndSujetoRetencion() {
        return indSujetoRetencion;
    }

    public void setIndSujetoRetencion(String indSujetoRetencion) {
        this.indSujetoRetencion = indSujetoRetencion;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Integer getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(Integer usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public Integer getUsuarioModificacion() {
        return usuarioModificacion;
    }

    public void setUsuarioModificacion(Integer usuarioModificacion) {
        this.usuarioModificacion = usuarioModificacion;
    }

    public String getIdCondicionPago() {
        return idCondicionPago;
    }

    public void setIdCondicionPago(String idCondicionPago) {
        this.idCondicionPago = idCondicionPago;
    }

    public String getIdMoneda() {
        return idMoneda;
    }

    public void setIdMoneda(String idMoneda) {
        this.idMoneda = idMoneda;
    }

    public Integer getIdTipoComprobante() {
        return idTipoComprobante;
    }

    public void setIdTipoComprobante(Integer idTipoComprobante) {
        this.idTipoComprobante = idTipoComprobante;
    }

    public String getIdTipoProveedor() {
        return idTipoProveedor;
    }

    public void setIdTipoProveedor(String idTipoProveedor) {
        this.idTipoProveedor = idTipoProveedor;
    }

    public Integer getIdDistrito() {
        return idDistrito;
    }

    public void setIdDistrito(Integer idDistrito) {
        this.idDistrito = idDistrito;
    }

    public Integer getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(Integer idProvincia) {
        this.idProvincia = idProvincia;
    }

    public Integer getIdRegion() {
        return idRegion;
    }

    public void setIdRegion(Integer idRegion) {
        this.idRegion = idRegion;
    }

    public Integer getIdPais() {
        return idPais;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }

    public String getIdTipoBeneficiario() {
        return idTipoBeneficiario;
    }

    public void setIdTipoBeneficiario(String idTipoBeneficiario) {
        this.idTipoBeneficiario = idTipoBeneficiario;
    }

    public String getCuentaBeneficiario() {
        return cuentaBeneficiario;
    }

    public void setCuentaBeneficiario(String cuentaBeneficiario) {
        this.cuentaBeneficiario = cuentaBeneficiario;
    }

    public String getReferenciaProveedor() {
        return referenciaProveedor;
    }

    public void setReferenciaProveedor(String referenciaProveedor) {
        this.referenciaProveedor = referenciaProveedor;
    }

    public String getCarpetaId() {
        return carpetaId;
    }

    public void setCarpetaId(String carpetaId) {
        this.carpetaId = carpetaId;
    }

    public String getIdHcp() {
        return idHcp;
    }

    public void setIdHcp(String idHcp) {
        this.idHcp = idHcp;
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

    public String getTerritorioRegionAmazonia() {
        return territorioRegionAmazonia;
    }

    public void setTerritorioRegionAmazonia(String territorioRegionAmazonia) {
        this.territorioRegionAmazonia = territorioRegionAmazonia;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getIndHabidoSunat() {
        return indHabidoSunat;
    }

    public void setIndHabidoSunat(String indHabidoSunat) {
        this.indHabidoSunat = indHabidoSunat;
    }

    public String getIndActivoSunat() {
        return indActivoSunat;
    }

    public void setIndActivoSunat(String indActivoSunat) {
        this.indActivoSunat = indActivoSunat;
    }

    public String getFechaInicioActiSunat() {
        return fechaInicioActiSunat;
    }

    public void setFechaInicioActiSunat(String fechaInicioActiSunat) {
        this.fechaInicioActiSunat = fechaInicioActiSunat;
    }

    public String getIndCreacionUnicaVez() {
        return indCreacionUnicaVez;
    }

    public void setIndCreacionUnicaVez(String indCreacionUnicaVez) {
        this.indCreacionUnicaVez = indCreacionUnicaVez;
    }

    public String getIndProveedorExcepcion() {
        return indProveedorExcepcion;
    }

    public void setIndProveedorExcepcion(String indProveedorExcepcion) {
        this.indProveedorExcepcion = indProveedorExcepcion;
    }

    public String getCodigoSistemaEmisionElect() {
        return codigoSistemaEmisionElect;
    }

    public void setCodigoSistemaEmisionElect(String codigoSistemaEmisionElect) {
        this.codigoSistemaEmisionElect = codigoSistemaEmisionElect;
    }

    public String getCodigoComprobantePago() {
        return codigoComprobantePago;
    }

    public void setCodigoComprobantePago(String codigoComprobantePago) {
        this.codigoComprobantePago = codigoComprobantePago;
    }

    public String getCodigoPadron() {
        return codigoPadron;
    }

    public void setCodigoPadron(String codigoPadron) {
        this.codigoPadron = codigoPadron;
    }

    public String getCodigoGrupoCompra() {
        return codigoGrupoCompra;
    }

    public void setCodigoGrupoCompra(String codigoGrupoCompra) {
        this.codigoGrupoCompra = codigoGrupoCompra;
    }

    public String getCodigoGrupoTesoreria() {
        return codigoGrupoTesoreria;
    }

    public void setCodigoGrupoTesoreria(String codigoGrupoTesoreria) {
        this.codigoGrupoTesoreria = codigoGrupoTesoreria;
    }

    public String getOperacionesAfectas() {
        return operacionesAfectas;
    }

    public void setOperacionesAfectas(String operacionesAfectas) {
        this.operacionesAfectas = operacionesAfectas;
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

    public String getIndTipoFacturacionElect() {
        return indTipoFacturacionElect;
    }

    public void setIndTipoFacturacionElect(String indTipoFacturacionElect) {
        this.indTipoFacturacionElect = indTipoFacturacionElect;
    }

    public String getIndTipoFacturacionManual() {
        return indTipoFacturacionManual;
    }

    public void setIndTipoFacturacionManual(String indTipoFacturacionManual) {
        this.indTipoFacturacionManual = indTipoFacturacionManual;
    }

    public String getNombreRepresentanteLegal() {
        return nombreRepresentanteLegal;
    }

    public void setNombreRepresentanteLegal(String nombreRepresentanteLegal) {
        this.nombreRepresentanteLegal = nombreRepresentanteLegal;
    }

    public String getCargoRepresentanteLegal() {
        return cargoRepresentanteLegal;
    }

    public void setCargoRepresentanteLegal(String cargoRepresentanteLegal) {
        this.cargoRepresentanteLegal = cargoRepresentanteLegal;
    }

    public String getEmailRepresentanteLegal() {
        return emailRepresentanteLegal;
    }

    public void setEmailRepresentanteLegal(String emailRepresentanteLegal) {
        this.emailRepresentanteLegal = emailRepresentanteLegal;
    }

    public String getNroDocumRepresentanteLegal() {
        return nroDocumRepresentanteLegal;
    }

    public void setNroDocumRepresentanteLegal(String nroDocumRepresentanteLegal) {
        this.nroDocumRepresentanteLegal = nroDocumRepresentanteLegal;
    }

    public String getNombrePersonaCreditoCobranza() {
        return nombrePersonaCreditoCobranza;
    }

    public void setNombrePersonaCreditoCobranza(String nombrePersonaCreditoCobranza) {
        this.nombrePersonaCreditoCobranza = nombrePersonaCreditoCobranza;
    }

    public String getCargoPersonaCreditoCobranza() {
        return cargoPersonaCreditoCobranza;
    }

    public void setCargoPersonaCreditoCobranza(String cargoPersonaCreditoCobranza) {
        this.cargoPersonaCreditoCobranza = cargoPersonaCreditoCobranza;
    }

    public String getEmailPersonaCreditoCobranza() {
        return emailPersonaCreditoCobranza;
    }

    public void setEmailPersonaCreditoCobranza(String emailPersonaCreditoCobranza) {
        this.emailPersonaCreditoCobranza = emailPersonaCreditoCobranza;
    }

    public String getNroDocumPersonaCreditoCobranza() {
        return nroDocumPersonaCreditoCobranza;
    }

    public void setNroDocumPersonaCreditoCobranza(String nroDocumPersonaCreditoCobranza) {
        this.nroDocumPersonaCreditoCobranza = nroDocumPersonaCreditoCobranza;
    }

    public String getNombrePersonaTesoreria() {
        return nombrePersonaTesoreria;
    }

    public void setNombrePersonaTesoreria(String nombrePersonaTesoreria) {
        this.nombrePersonaTesoreria = nombrePersonaTesoreria;
    }

    public String getCargoPersonaTesoreria() {
        return cargoPersonaTesoreria;
    }

    public void setCargoPersonaTesoreria(String cargoPersonaTesoreria) {
        this.cargoPersonaTesoreria = cargoPersonaTesoreria;
    }

    public String getEmailPersonaTesoreria() {
        return emailPersonaTesoreria;
    }

    public void setEmailPersonaTesoreria(String emailPersonaTesoreria) {
        this.emailPersonaTesoreria = emailPersonaTesoreria;
    }

    public String getNroDocumPersonaTesoreria() {
        return nroDocumPersonaTesoreria;
    }

    public void setNroDocumPersonaTesoreria(String nroDocumPersonaTesoreria) {
        this.nroDocumPersonaTesoreria = nroDocumPersonaTesoreria;
    }

    public String getNombrePersonaCompra() {
        return nombrePersonaCompra;
    }

    public void setNombrePersonaCompra(String nombrePersonaCompra) {
        this.nombrePersonaCompra = nombrePersonaCompra;
    }

    public String getCargoPersonaCompra() {
        return cargoPersonaCompra;
    }

    public void setCargoPersonaCompra(String cargoPersonaCompra) {
        this.cargoPersonaCompra = cargoPersonaCompra;
    }

    public String getEmailPersonaCompra() {
        return emailPersonaCompra;
    }

    public void setEmailPersonaCompra(String emailPersonaCompra) {
        this.emailPersonaCompra = emailPersonaCompra;
    }

    public String getNroDocumPersonaCompra() {
        return nroDocumPersonaCompra;
    }

    public void setNroDocumPersonaCompra(String nroDocumPersonaCompra) {
        this.nroDocumPersonaCompra = nroDocumPersonaCompra;
    }

    public String getCelularPersonaCompra() {
        return celularPersonaCompra;
    }

    public void setCelularPersonaCompra(String celularPersonaCompra) {
        this.celularPersonaCompra = celularPersonaCompra;
    }

    public String getIndAceptacion() {
        return indAceptacion;
    }

    public void setIndAceptacion(String indAceptacion) {
        this.indAceptacion = indAceptacion;
    }

    public String getCodigoActividadEconomica() {
        return codigoActividadEconomica;
    }

    public void setCodigoActividadEconomica(String codigoActividadEconomica) {
        this.codigoActividadEconomica = codigoActividadEconomica;
    }

    public String getCodigoProfesionOficio() {
        return codigoProfesionOficio;
    }

    public void setCodigoProfesionOficio(String codigoProfesionOficio) {
        this.codigoProfesionOficio = codigoProfesionOficio;
    }

    public String getCodigoTipoProveedorActividad() {
        return codigoTipoProveedorActividad;
    }

    public void setCodigoTipoProveedorActividad(String codigoTipoProveedorActividad) {
        this.codigoTipoProveedorActividad = codigoTipoProveedorActividad;
    }

    public Integer getIdEstadoProveedor() {
        return idEstadoProveedor;
    }

    public void setIdEstadoProveedor(Integer idEstadoProveedor) {
        this.idEstadoProveedor = idEstadoProveedor;
    }

    public String getIndMigradoSap() {
        return indMigradoSap;
    }

    public void setIndMigradoSap(String indMigradoSap) {
        this.indMigradoSap = indMigradoSap;
    }

    public String getTelefonoMovil() {
        return telefonoMovil;
    }

    public void setTelefonoMovil(String telefonoMovil) {
        this.telefonoMovil = telefonoMovil;
    }

    public String getNombreProveedorOse() {
        return nombreProveedorOse;
    }

    public void setNombreProveedorOse(String nombreProveedorOse) {
        this.nombreProveedorOse = nombreProveedorOse;
    }

    public String getIndAgenteRetencion() {
        return indAgenteRetencion;
    }

    public void setIndAgenteRetencion(String indAgenteRetencion) {
        this.indAgenteRetencion = indAgenteRetencion;
    }

    public String getCodigoDireccion() {
        return codigoDireccion;
    }

    public void setCodigoDireccion(String codigoDireccion) {
        this.codigoDireccion = codigoDireccion;
    }

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

    public Integer getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(Integer idBanco) {
        this.idBanco = idBanco;
    }

    public Integer getIdEstadoBancoExtranjero() {
        return idEstadoBancoExtranjero;
    }

    public void setIdEstadoBancoExtranjero(Integer idEstadoBancoExtranjero) {
        this.idEstadoBancoExtranjero = idEstadoBancoExtranjero;
    }

    public Integer getFlagActivo() {
        return flagActivo;
    }

    public void setFlagActivo(Integer flagActivo) {
        this.flagActivo = flagActivo;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getIndMigracionOK() {
        return indMigracionOK;
    }

    public void setIndMigracionOK(String indMigracionOK) {
        this.indMigracionOK = indMigracionOK;
    }

    public String getIndErrorRuc() {
        return indErrorRuc;
    }

    public void setIndErrorRuc(String indErrorRuc) {
        this.indErrorRuc = indErrorRuc;
    }

    public String getIndErrorMail() {
        return indErrorMail;
    }

    public void setIndErrorMail(String indErrorMail) {
        this.indErrorMail = indErrorMail;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }
}