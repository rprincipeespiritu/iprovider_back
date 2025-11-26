package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name="proveedor_antecedentes_representante_legal")
public class ProveedorAntecedenteRepresentanteLegal extends BaseDomain implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_PROVEEDOR_ANTECEDENTES_REPRESENTANTE_LEGAL")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idProveedorAntecedenteRepresentanteLegal;

	@ManyToOne
	@JoinColumn(name="ID_PROVEEDOR_REPRESENTANTE_LEGAL")
	private ProveedorRepresentanteLegal proveedorRepresentanteLegal;

	@Column(name="NUMERO_ANTECEDENTE", nullable=true)
	private String numeroAntecedente;

	@Column(name="DELITOS_INVOLUCRADOS", nullable=true)
	private String delitosInvolucrados;

	@Column(name="DESCRIPCION_ESTADO_CASO", nullable=true)
	private String descripcionEstadoCaso;

	@Column(name="FECHA_CONDENA", nullable=true)
	private Date fechaCondena;

	@Column(name="TIPO", nullable=true)
	private String tipo;


	//getter and setter


	public Integer getIdProveedorAntecedenteRepresentanteLegal() {
		return idProveedorAntecedenteRepresentanteLegal;
	}

	public void setIdProveedorAntecedenteRepresentanteLegal(Integer idProveedorAntecedenteRepresentanteLegal) {
		this.idProveedorAntecedenteRepresentanteLegal = idProveedorAntecedenteRepresentanteLegal;
	}

	public ProveedorRepresentanteLegal getProveedorRepresentanteLegal() {
		return proveedorRepresentanteLegal;
	}

	public void setProveedorRepresentanteLegal(ProveedorRepresentanteLegal proveedorRepresentanteLegal) {
		this.proveedorRepresentanteLegal = proveedorRepresentanteLegal;
	}

	public String getNumeroAntecedente() {
		return numeroAntecedente;
	}

	public void setNumeroAntecedente(String numeroAntecedente) {
		this.numeroAntecedente = numeroAntecedente;
	}

	public String getDelitosInvolucrados() {
		return delitosInvolucrados;
	}

	public void setDelitosInvolucrados(String delitosInvolucrados) {
		this.delitosInvolucrados = delitosInvolucrados;
	}

	public String getDescripcionEstadoCaso() {
		return descripcionEstadoCaso;
	}

	public void setDescripcionEstadoCaso(String descripcionEstadoCaso) {
		this.descripcionEstadoCaso = descripcionEstadoCaso;
	}

	public Date getFechaCondena() {
		return fechaCondena;
	}

	public void setFechaCondena(Date fechaCondena) {
		this.fechaCondena = fechaCondena;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		return "ProveedorAntecedenteRepresentanteLegal{" +
				"idProveedorAntecedenteRepresentanteLegal=" + idProveedorAntecedenteRepresentanteLegal +
				", proveedorRepresentanteLegal=" + proveedorRepresentanteLegal +
				", numeroAntecedente='" + numeroAntecedente + '\'' +
				", delitosInvolucrados='" + delitosInvolucrados + '\'' +
				", descripcionEstadoCaso='" + descripcionEstadoCaso + '\'' +
				", fechaCondena=" + fechaCondena +
				", tipo='" + tipo + '\'' +
				'}';
	}
}