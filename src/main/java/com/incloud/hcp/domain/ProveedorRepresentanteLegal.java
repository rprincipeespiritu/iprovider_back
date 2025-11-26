package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="proveedor_representante_legal")
public class ProveedorRepresentanteLegal extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ID_PROVEEDOR_REPRESENTANTE_LEGAL")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProveedorRepresentanteLegal;



    @Column(name="NOMBRES")
    private String nombres;

    @Column(name="APELLIDOS")
    private String apellidos;

    @Column(name="NACIONALIDAD")
    private Integer nacionalidad;

    @Column(name="NACIONALIDAD_OTRO")
    private String nacionalidadOtro;

    @ManyToOne
    @JoinColumn(name="ID_ESTADO_CIVIL")
    private EstadoCivil estadoCivil;

    @Column(name="NOMBRE_CONYUGE_CONVIVIENTE")
    private String nombreConyugeConviviente;

    @Column(name="DOCUMENTO_IDENTIDAD")
    private Integer documentoIdentidad;

    @Column(name="NUMERO_DOCUMENTO")
    private String numeroDocumento;

    @ManyToOne
    @JoinColumn(name="ID_PROVEEDOR_DECLARACION_JURADA")
    private ProveedorDeclaracionJurada proveedorDeclaracionJurada;



    //getter and setter


    public Integer getIdProveedorRepresentanteLegal() {
        return idProveedorRepresentanteLegal;
    }

    public void setIdProveedorRepresentanteLegal(Integer idProveedorRepresentanteLegal) {
        this.idProveedorRepresentanteLegal = idProveedorRepresentanteLegal;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
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

    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getNombreConyugeConviviente() {
        return nombreConyugeConviviente;
    }

    public void setNombreConyugeConviviente(String nombreConyugeConviviente) {
        this.nombreConyugeConviviente = nombreConyugeConviviente;
    }

    public Integer getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(Integer documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public ProveedorDeclaracionJurada getProveedorDeclaracionJurada() {
        return proveedorDeclaracionJurada;
    }

    public void setProveedorDeclaracionJurada(ProveedorDeclaracionJurada proveedorDeclaracionJurada) {
        this.proveedorDeclaracionJurada = proveedorDeclaracionJurada;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    @Override
    public String toString() {
        return "ProveedorRepresentanteLegal{" +
                "idProveedorRepresentanteLegal=" + idProveedorRepresentanteLegal +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", nacionalidad=" + nacionalidad +
                ", nacionalidadOtro='" + nacionalidadOtro + '\'' +
                ", estadoCivil=" + estadoCivil +
                ", nombreConyugeConviviente='" + nombreConyugeConviviente + '\'' +
                ", documentoIdentidad=" + documentoIdentidad +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", proveedorDeclaracionJurada=" + proveedorDeclaracionJurada +
                '}';
    }
}
