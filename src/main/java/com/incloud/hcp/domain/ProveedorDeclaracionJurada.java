package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="proveedor_declaracion_jurada")
public class ProveedorDeclaracionJurada extends BaseDomain implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ID_DECLARACION_JURADA")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDeclaracionJurada;


    @Column(name="NOMBRES")
    private String nombres;

    @Column(name="APELLIDOS")
    private String apellidos;


    @Column(name="ID_NACIONALIDAD")
    private Integer nacionalidad;

    @Column(name="NACIONALIDAD_OTRO")
    private String nacionalidadOtro;

    @ManyToOne
    @JoinColumn(name="ID_ESTADO_CIVIL")
    private EstadoCivil estadoCivil;


    @Column(name="NOMBRES_CONYUGE_CONVIVIENTE")
    private String nombresConyugeConviviente;

    @Column(name="ID_TIPO_DOCUMENTO")
    private Integer tipoDocumento;

    @Column(name="NUMERO_DOCUMENTO")
    private String numeroDocumento;

    @Column(name="RUC")
    private String ruc;

    @Column(name="DOMICILIO")
    private String domicilio;

    @ManyToOne
    @JoinColumn(name="ID_PAIS")
    private Ubigeo pais;

    @ManyToOne
    @JoinColumn(name="ID_DEPARTAMENTO")
    private Ubigeo departamento;

    @ManyToOne
    @JoinColumn(name="ID_PROVINCIA")
    private Ubigeo provincia;

    @ManyToOne
    @JoinColumn(name="ID_DISTRITO")
    private Ubigeo distrito;

    @Column(name="DOMICILIO_OFICINA")
    private String domicilioOficina;


    @ManyToOne
    @JoinColumn(name="ID_PAIS_OFICINA")
    private Ubigeo paisOficina;

    @ManyToOne
    @JoinColumn(name="ID_DEPARTAMENTO_OFICINA")
    private Ubigeo departamentoOficina;

    @ManyToOne
    @JoinColumn(name="ID_PROVINCIA_OFICINA")
    private Ubigeo provinciaOficina;

    @ManyToOne
    @JoinColumn(name="ID_DISTRITO_OFICINA")
    private Ubigeo distritoOficina;

    @Column(name="ACTIVIDAD_ECONOMICA")
    private String actividadEconomica;

    @Column(name="ANIOS_EXPERIENCIA")
    private String aniosExperiencia;

    @Column(name="RUBROS_OPERA")
    private String rubrosOpera;

    //-- IDENTIFICACION COMO PEP O PARIENTE DE PEP

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
    private String nacionalidadPariente;

    //IDENTIFICACION COMO PEP O PARIENTE DE PEP

    @Column(name="IND_ANTECEDENTES_PENALES")
    private Integer indAntecedentesPenales;

    @Column(name="NUMERO_FECHA_CONDENA")
    private String numeroFechaCondena;

    @Column(name="DELITOS_INVOLUCRADOS")
    private String delitosInvolucrados;

    @Column(name="IND_ANTECEDENTES_POLICIALES")
    private Integer indAntecedentesPoliciales;

    @Column(name="NUMERO_ANTECEDENTES")
    private String numeroAntecedentes;

    @Column(name="DELITOS_INVOLUCRADOS_POLICIALES")
    private String delitosInvolucradosPoliciales;

    @Column(name="DESCRIPCION_ESTADO_CASO")
    private String descripcionEstadoCaso;

    @ManyToOne
    @JoinColumn(name="ID_PROVEEDOR")
    private Proveedor proveedor;

    //=============================================================================
    // CAMPOS PARA PJ
    //=============================================================================
    @Column(name="POLITICA_CUMPLIMIENTO_PREGUNTA_1")
    private String politicaCumplimientoPregunta1;

    @Column(name="POLITICA_CUMPLIMIENTO_PREGUNTA_2")
    private String politicaCumplimientoPregunta2;

    @Column(name="POLITICA_CUMPLIMIENTO_PREGUNTA_3")
    private String politicaCumplimientoPregunta3;

    @Column(name="POLITICA_CUMPLIMIENTO_PREGUNTA_4")
    private String politicaCumplimientoPregunta4;

    @Column(name="POLITICA_CUMPLIMIENTO_PREGUNTA_5")
    private String politicaCumplimientoPregunta5;

    @Column(name="POLITICA_CUMPLIMIENTO_PREGUNTA_6")
    private String politicaCumplimientoPregunta6;

    @Column(name="POLITICA_CUMPLIMIENTO_PREGUNTA_7")
    private String politicaCumplimientoPregunta7;

    @Column(name="POLITICA_CUMPLIMIENTO_PREGUNTA_8")
    private String politicaCumplimientoPregunta8;

    @Column(name="DNI_PARIENTE")
    private String dniPariente;

    @Column(name="RAZON_SOCIAL_DOMINACION")
    private String razonSocialDominacion;

    @Column(name="FECHA_SOCIEDAD")
    private Date fechaSociedad;

    @Column(name="NOMBRE_COMERCIAL")
    private String nombreComercial;

    @Column(name="GRUPO_ECONOMICO")
    private String grupoEconomico;

    @Column(name="PARTIDA_REGISTRAL")
    private String partidaRegistral;

    @Column(name="OFICINA_REGISTRAL")
    private String oficinaRegistral;


    @Column(name="TIPO_PERSONA")
    private String tipoPersona;

    //ANTECEDENTES SOCIEDAD
    @Column(name="IND_ANTECEDENTES_PENALES_REPRESENTANTE")
    private Integer indAntecedentesPenalesRepresentante;

    @Column(name="IND_ANTECEDENTES_POLICIALES_REPRESENTANTE")
    private Integer indAntecedentesPolicialesRepresentante;

    @ManyToOne
    @JoinColumn(name="ID_PAIS_SOCIEDAD")
    private Ubigeo paisSociedad;


    //================================================================================
    // GETTERS AND SETTERS
    //================================================================================


    public Integer getIndAntecedentesPenalesRepresentante() {
        return indAntecedentesPenalesRepresentante;
    }

    public void setIndAntecedentesPenalesRepresentante(Integer indAntecedentesPenalesRepresentante) {
        this.indAntecedentesPenalesRepresentante = indAntecedentesPenalesRepresentante;
    }

    public Integer getIndAntecedentesPolicialesRepresentante() {
        return indAntecedentesPolicialesRepresentante;
    }

    public void setIndAntecedentesPolicialesRepresentante(Integer indAntecedentesPolicialesRepresentante) {
        this.indAntecedentesPolicialesRepresentante = indAntecedentesPolicialesRepresentante;
    }

    public Ubigeo getPaisSociedad() {
        return paisSociedad;
    }

    public void setPaisSociedad(Ubigeo paisSociedad) {
        this.paisSociedad = paisSociedad;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getPoliticaCumplimientoPregunta1() {
        return politicaCumplimientoPregunta1;
    }

    public void setPoliticaCumplimientoPregunta1(String politicaCumplimientoPregunta1) {
        this.politicaCumplimientoPregunta1 = politicaCumplimientoPregunta1;
    }

    public String getPoliticaCumplimientoPregunta2() {
        return politicaCumplimientoPregunta2;
    }

    public void setPoliticaCumplimientoPregunta2(String politicaCumplimientoPregunta2) {
        this.politicaCumplimientoPregunta2 = politicaCumplimientoPregunta2;
    }

    public String getPoliticaCumplimientoPregunta3() {
        return politicaCumplimientoPregunta3;
    }

    public void setPoliticaCumplimientoPregunta3(String politicaCumplimientoPregunta3) {
        this.politicaCumplimientoPregunta3 = politicaCumplimientoPregunta3;
    }

    public String getPoliticaCumplimientoPregunta4() {
        return politicaCumplimientoPregunta4;
    }

    public void setPoliticaCumplimientoPregunta4(String politicaCumplimientoPregunta4) {
        this.politicaCumplimientoPregunta4 = politicaCumplimientoPregunta4;
    }

    public String getPoliticaCumplimientoPregunta6() {
        return politicaCumplimientoPregunta6;
    }

    public void setPoliticaCumplimientoPregunta6(String politicaCumplimientoPregunta6) {
        this.politicaCumplimientoPregunta6 = politicaCumplimientoPregunta6;
    }

    public String getPoliticaCumplimientoPregunta7() {
        return politicaCumplimientoPregunta7;
    }

    public void setPoliticaCumplimientoPregunta7(String politicaCumplimientoPregunta7) {
        this.politicaCumplimientoPregunta7 = politicaCumplimientoPregunta7;
    }

    public String getPoliticaCumplimientoPregunta8() {
        return politicaCumplimientoPregunta8;
    }

    public void setPoliticaCumplimientoPregunta8(String politicaCumplimientoPregunta8) {
        this.politicaCumplimientoPregunta8 = politicaCumplimientoPregunta8;
    }

    public String getDniPariente() {
        return dniPariente;
    }

    public void setDniPariente(String dniPariente) {
        this.dniPariente = dniPariente;
    }

    public String getRazonSocialDominacion() {
        return razonSocialDominacion;
    }

    public void setRazonSocialDominacion(String razonSocialDominacion) {
        this.razonSocialDominacion = razonSocialDominacion;
    }

    public Date getFechaSociedad() {
        return fechaSociedad;
    }

    public void setFechaSociedad(Date fechaSociedad) {
        this.fechaSociedad = fechaSociedad;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getGrupoEconomico() {
        return grupoEconomico;
    }

    public void setGrupoEconomico(String grupoEconomico) {
        this.grupoEconomico = grupoEconomico;
    }

    public String getPartidaRegistral() {
        return partidaRegistral;
    }

    public void setPartidaRegistral(String partidaRegistral) {
        this.partidaRegistral = partidaRegistral;
    }

    public String getOficinaRegistral() {
        return oficinaRegistral;
    }

    public void setOficinaRegistral(String oficinaRegistral) {
        this.oficinaRegistral = oficinaRegistral;
    }

    public Integer getIdDeclaracionJurada() {
        return idDeclaracionJurada;
    }

    public void setIdDeclaracionJurada(Integer idDeclaracionJurada) {
        this.idDeclaracionJurada = idDeclaracionJurada;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
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





    public String getNombresConyugeConviviente() {
        return nombresConyugeConviviente;
    }

    public void setNombresConyugeConviviente(String nombresConyugeConviviente) {
        this.nombresConyugeConviviente = nombresConyugeConviviente;
    }

    public Integer getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(Integer tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public Ubigeo getPais() {
        return pais;
    }

    public void setPais(Ubigeo pais) {
        this.pais = pais;
    }

    public Ubigeo getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Ubigeo departamento) {
        this.departamento = departamento;
    }

    public Ubigeo getProvincia() {
        return provincia;
    }

    public void setProvincia(Ubigeo provincia) {
        this.provincia = provincia;
    }

    public Ubigeo getDistrito() {
        return distrito;
    }

    public void setDistrito(Ubigeo distrito) {
        this.distrito = distrito;
    }

    public String getDomicilioOficina() {
        return domicilioOficina;
    }

    public void setDomicilioOficina(String domicilioOficina) {
        this.domicilioOficina = domicilioOficina;
    }

    public Ubigeo getPaisOficina() {
        return paisOficina;
    }

    public void setPaisOficina(Ubigeo paisOficina) {
        this.paisOficina = paisOficina;
    }

    public Ubigeo getDepartamentoOficina() {
        return departamentoOficina;
    }

    public void setDepartamentoOficina(Ubigeo departamentoOficina) {
        this.departamentoOficina = departamentoOficina;
    }

    public Ubigeo getProvinciaOficina() {
        return provinciaOficina;
    }

    public void setProvinciaOficina(Ubigeo provinciaOficina) {
        this.provinciaOficina = provinciaOficina;
    }

    public Ubigeo getDistritoOficina() {
        return distritoOficina;
    }

    public void setDistritoOficina(Ubigeo distritoOficina) {
        this.distritoOficina = distritoOficina;
    }

    public String getActividadEconomica() {
        return actividadEconomica;
    }

    public void setActividadEconomica(String actividadEconomica) {
        this.actividadEconomica = actividadEconomica;
    }

    public String getAniosExperiencia() {
        return aniosExperiencia;
    }

    public void setAniosExperiencia(String aniosExperiencia) {
        this.aniosExperiencia = aniosExperiencia;
    }

    public String getRubrosOpera() {
        return rubrosOpera;
    }

    public void setRubrosOpera(String rubrosOpera) {
        this.rubrosOpera = rubrosOpera;
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

    public String getNacionalidadPariente() {
        return nacionalidadPariente;
    }

    public void setNacionalidadPariente(String nacionalidadPariente) {
        this.nacionalidadPariente = nacionalidadPariente;
    }

    public Integer getIndAntecedentesPenales() {
        return indAntecedentesPenales;
    }

    public void setIndAntecedentesPenales(Integer indAntecedentesPenales) {
        this.indAntecedentesPenales = indAntecedentesPenales;
    }

    public String getNumeroFechaCondena() {
        return numeroFechaCondena;
    }

    public void setNumeroFechaCondena(String numeroFechaCondena) {
        this.numeroFechaCondena = numeroFechaCondena;
    }

    public String getDelitosInvolucrados() {
        return delitosInvolucrados;
    }

    public void setDelitosInvolucrados(String delitosInvolucrados) {
        this.delitosInvolucrados = delitosInvolucrados;
    }

    public Integer getIndAntecedentesPoliciales() {
        return indAntecedentesPoliciales;
    }

    public void setIndAntecedentesPoliciales(Integer indAntecedentesPoliciales) {
        this.indAntecedentesPoliciales = indAntecedentesPoliciales;
    }

    public String getNumeroAntecedentes() {
        return numeroAntecedentes;
    }

    public void setNumeroAntecedentes(String numeroAntecedentes) {
        this.numeroAntecedentes = numeroAntecedentes;
    }

    public String getDelitosInvolucradosPoliciales() {
        return delitosInvolucradosPoliciales;
    }

    public void setDelitosInvolucradosPoliciales(String delitosInvolucradosPoliciales) {
        this.delitosInvolucradosPoliciales = delitosInvolucradosPoliciales;
    }

    public String getDescripcionEstadoCaso() {
        return descripcionEstadoCaso;
    }

    public void setDescripcionEstadoCaso(String descripcionEstadoCaso) {
        this.descripcionEstadoCaso = descripcionEstadoCaso;
    }

    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getPoliticaCumplimientoPregunta5() {
        return politicaCumplimientoPregunta5;
    }

    public void setPoliticaCumplimientoPregunta5(String politicaCumplimientoPregunta5) {
        this.politicaCumplimientoPregunta5 = politicaCumplimientoPregunta5;
    }

    @Override
    public String toString() {
        return "ProveedorDeclaracionJurada{" +
                "idDeclaracionJurada=" + idDeclaracionJurada +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", nacionalidad=" + nacionalidad +
                ", nacionalidadOtro='" + nacionalidadOtro + '\'' +
                ", estadoCivil=" + estadoCivil +
                ", nombresConyugeConviviente='" + nombresConyugeConviviente + '\'' +
                ", tipoDocumento=" + tipoDocumento +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", ruc='" + ruc + '\'' +
                ", domicilio='" + domicilio + '\'' +
                ", pais=" + pais +
                ", departamento=" + departamento +
                ", provincia=" + provincia +
                ", distrito=" + distrito +
                ", domicilioOficina='" + domicilioOficina + '\'' +
                ", paisOficina=" + paisOficina +
                ", departamentoOficina=" + departamentoOficina +
                ", provinciaOficina=" + provinciaOficina +
                ", distritoOficina=" + distritoOficina +
                ", actividadEconomica='" + actividadEconomica + '\'' +
                ", aniosExperiencia='" + aniosExperiencia + '\'' +
                ", rubrosOpera='" + rubrosOpera + '\'' +
                ", esPep=" + esPep +
                ", cargoEjercido='" + cargoEjercido + '\'' +
                ", entidad='" + entidad + '\'' +
                ", fechaInicioFin='" + fechaInicioFin + '\'' +
                ", esParientePep=" + esParientePep +
                ", vinculoParental='" + vinculoParental + '\'' +
                ", nombresParientePep='" + nombresParientePep + '\'' +
                ", cargoEjercidoPariente='" + cargoEjercidoPariente + '\'' +
                ", entidadPariente='" + entidadPariente + '\'' +
                ", fechaInicioFinPariente='" + fechaInicioFinPariente + '\'' +
                ", nacionalidadPariente='" + nacionalidadPariente + '\'' +
                ", indAntecedentesPenales=" + indAntecedentesPenales +
                ", numeroFechaCondena='" + numeroFechaCondena + '\'' +
                ", delitosInvolucrados='" + delitosInvolucrados + '\'' +
                ", indAntecedentesPoliciales=" + indAntecedentesPoliciales +
                ", numeroAntecedentes='" + numeroAntecedentes + '\'' +
                ", delitosInvolucradosPoliciales='" + delitosInvolucradosPoliciales + '\'' +
                ", descripcionEstadoCaso='" + descripcionEstadoCaso + '\'' +
                ", proveedor=" + proveedor +
                ", politicaCumplimientoPregunta1='" + politicaCumplimientoPregunta1 + '\'' +
                ", politicaCumplimientoPregunta2='" + politicaCumplimientoPregunta2 + '\'' +
                ", politicaCumplimientoPregunta3='" + politicaCumplimientoPregunta3 + '\'' +
                ", politicaCumplimientoPregunta4='" + politicaCumplimientoPregunta4 + '\'' +
                ", politicaCumplimientoPregunta5='" + politicaCumplimientoPregunta5 + '\'' +
                ", politicaCumplimientoPregunta6='" + politicaCumplimientoPregunta6 + '\'' +
                ", politicaCumplimientoPregunta7='" + politicaCumplimientoPregunta7 + '\'' +
                ", politicaCumplimientoPregunta8='" + politicaCumplimientoPregunta8 + '\'' +
                ", dniPariente='" + dniPariente + '\'' +
                ", razonSocialDominacion='" + razonSocialDominacion + '\'' +
                ", fechaSociedad=" + fechaSociedad +
                ", nombreComercial='" + nombreComercial + '\'' +
                ", grupoEconomico='" + grupoEconomico + '\'' +
                ", partidaRegistral='" + partidaRegistral + '\'' +
                ", oficinaRegistral='" + oficinaRegistral + '\'' +
                ", tipoPersona='" + tipoPersona + '\'' +
                ", paisSociedad='" + paisSociedad + '\'' +
                '}';
    }
}
