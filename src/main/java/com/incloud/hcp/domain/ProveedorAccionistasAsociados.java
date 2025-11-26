package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="PROVEEDOR_ACCIONISTAS_ASOCIADOS")
public class ProveedorAccionistasAsociados extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ID_PROVEEDOR_ACCIONISTAS_ASOCIADOS")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProveedorAccionistasAsociados;

    @ManyToOne
    @JoinColumn(name="ID_PROVEEDOR_DECLARACION_JURADA")
    private ProveedorDeclaracionJurada proveedorDeclaracionJurada;


    @Column(name="NOMBRE_RAZON_SOCIAL")
    private String nombreRazonSocial;


    @Column(name="NACIONALIDAD")
    private Integer nacionalidad;


    @Column(name="NACIONALIDAD_OTRO")
    private String nacionalidadOtro;

    @Column(name="DOCUMENTO_IDENTIDAD")
    private String documentoIdentidad;

    @Column(name="NUMERO_DOCUMENTO")
    private String numeroDocumento;

    @Column(name="RUC_EQUIVALENTE")
    private String rucEquivalente;

    @Column(name="PORCENTAJE_PARTICIPACION")
    private String procentajeParticipacion;

    @Column(name="ES_PEP")
    private Integer esPep;

    @Column(name="CARGO_EJERCIDO")
    private String cargoEjercido;

    @Column(name="ENTIDAD")
    private String entidad;

    @Column(name="FECHA_INICIO_FIN")
    private String fechaInicioFin;

    @Column(name="ES_PARIENTE_PEP")
    private Integer esParientePep;

    @Column(name="VINCULO_PARENTAL")
    private String vinculoParental;

    @Column(name="NOMBRES_PARIENTE_PEP")
    private String nombresParientePep;

    @Column(name="CARGO_EJERCIDO_PARIENTE")
    private String cargoEjercidoPariente;

    @Column(name="ENTIDAD_PARIENTE")
    private String entidadPariente;

    @Column(name="FECHA_INICIO_FIN_PARIENTE")
    private String fechaInicioFinPariente;

    @Column(name="ID_NACIONALIDAD_PARIENTE")
    private Integer nacionalidadPariente;

    @Column(name="DNI_PARIENTE")
    private String dniPariente;


    //setter an getter

    public Integer getIdProveedorAccionistasAsociados() {
        return idProveedorAccionistasAsociados;
    }

    public void setIdProveedorAccionistasAsociados(Integer idProveedorAccionistasAsociados) {
        this.idProveedorAccionistasAsociados = idProveedorAccionistasAsociados;
    }

    public ProveedorDeclaracionJurada getProveedorDeclaracionJurada() {
        return proveedorDeclaracionJurada;
    }

    public void setProveedorDeclaracionJurada(ProveedorDeclaracionJurada proveedorDeclaracionJurada) {
        this.proveedorDeclaracionJurada = proveedorDeclaracionJurada;
    }

    public String getNombreRazonSocial() {
        return nombreRazonSocial;
    }

    public void setNombreRazonSocial(String nombreRazonSocial) {
        this.nombreRazonSocial = nombreRazonSocial;
    }

    public Integer getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(Integer nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getNacionalidadOtro() {
        return nacionalidadOtro;
    }

    public void setNacionalidadOtro(String nacionalidadOtro) {
        this.nacionalidadOtro = nacionalidadOtro;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getRucEquivalente() {
        return rucEquivalente;
    }

    public void setRucEquivalente(String rucEquivalente) {
        this.rucEquivalente = rucEquivalente;
    }

    public String getProcentajeParticipacion() {
        return procentajeParticipacion;
    }

    public void setProcentajeParticipacion(String procentajeParticipacion) {
        this.procentajeParticipacion = procentajeParticipacion;
    }

    public Integer getEsPep() {
        return esPep;
    }

    public void setEsPep(Integer esPep) {
        this.esPep = esPep;
    }

    public String getCargoEjercido() {
        return cargoEjercido;
    }

    public void setCargoEjercido(String cargoEjercido) {
        this.cargoEjercido = cargoEjercido;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getFechaInicioFin() {
        return fechaInicioFin;
    }

    public void setFechaInicioFin(String fechaInicioFin) {
        this.fechaInicioFin = fechaInicioFin;
    }

    public Integer getEsParientePep() {
        return esParientePep;
    }

    public void setEsParientePep(Integer esParientePep) {
        this.esParientePep = esParientePep;
    }

    public String getVinculoParental() {
        return vinculoParental;
    }

    public void setVinculoParental(String vinculoParental) {
        this.vinculoParental = vinculoParental;
    }

    public String getNombresParientePep() {
        return nombresParientePep;
    }

    public void setNombresParientePep(String nombresParientePep) {
        this.nombresParientePep = nombresParientePep;
    }

    public String getCargoEjercidoPariente() {
        return cargoEjercidoPariente;
    }

    public void setCargoEjercidoPariente(String cargoEjercidoPariente) {
        this.cargoEjercidoPariente = cargoEjercidoPariente;
    }

    public String getEntidadPariente() {
        return entidadPariente;
    }

    public void setEntidadPariente(String entidadPariente) {
        this.entidadPariente = entidadPariente;
    }

    public String getFechaInicioFinPariente() {
        return fechaInicioFinPariente;
    }

    public void setFechaInicioFinPariente(String fechaInicioFinPariente) {
        this.fechaInicioFinPariente = fechaInicioFinPariente;
    }

    public Integer getNacionalidadPariente() {
        return nacionalidadPariente;
    }

    public void setNacionalidadPariente(Integer nacionalidadPariente) {
        this.nacionalidadPariente = nacionalidadPariente;
    }

    public String getDniPariente() {
        return dniPariente;
    }

    public void setDniPariente(String dniPariente) {
        this.dniPariente = dniPariente;
    }

    @Override
    public String toString() {
        return "ProveedorAccionistasAsociados{" +
                "idProveedorAccionistasAsociados=" + idProveedorAccionistasAsociados +
                ", proveedorDeclaracionJurada=" + proveedorDeclaracionJurada +
                ", nombreRazonSocial='" + nombreRazonSocial + '\'' +
                ", nacionalidad=" + nacionalidad +
                ", nacionalidadOtro='" + nacionalidadOtro + '\'' +
                ", documentoIdentidad='" + documentoIdentidad + '\'' +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", rucEquivalente='" + rucEquivalente + '\'' +
                ", procentajeParticipacion='" + procentajeParticipacion + '\'' +
                ", esPep=" + esPep +
                ", cargoEjercido='" + cargoEjercido + '\'' +
                ", entidad='" + entidad + '\'' +
                ", fechaInicioFin='" + fechaInicioFin + '\'' +
                ", esParientePep='" + esParientePep + '\'' +
                ", vinculoParental='" + vinculoParental + '\'' +
                ", nombresParientePep='" + nombresParientePep + '\'' +
                ", cargoEjercidoPariente='" + cargoEjercidoPariente + '\'' +
                ", entidadPariente='" + entidadPariente + '\'' +
                ", fechaInicioFinPariente='" + fechaInicioFinPariente + '\'' +
                ", nacionalidadPariente=" + nacionalidadPariente +
                ", dniPariente=" + dniPariente +
                '}';
    }
}
