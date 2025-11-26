package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.io.Serializable;
import java.util.Date;


/**
 * The persistent class for the banco database table.
 * 
 */
@Entity
@Table(name="proveedor_antecedentes")
public class ProveedorAntecedente extends BaseDomain implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_PROVEEDOR_ANTECEDENTE")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idProveedorAntecedente;

	@ManyToOne
	@JoinColumn(name="ID_PROVEEDOR_DECLARACION_JURADA")
	private ProveedorDeclaracionJurada proveedorDeclaracionJurada;


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


	public Integer getIdProveedorAntecedente() {
		return idProveedorAntecedente;
	}

	public void setIdProveedorAntecedente(Integer idProveedorAntecedente) {
		this.idProveedorAntecedente = idProveedorAntecedente;
	}

	public ProveedorDeclaracionJurada getProveedorDeclaracionJurada() {
		return proveedorDeclaracionJurada;
	}

	public void setProveedorDeclaracionJurada(ProveedorDeclaracionJurada proveedorDeclaracionJurada) {
		this.proveedorDeclaracionJurada = proveedorDeclaracionJurada;
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

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
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

	@Override
	public String toString() {
		return "ProveedorAntecedente{" +
				"idProveedorAntecedente=" + idProveedorAntecedente +
				", proveedorDeclaracionJurada=" + proveedorDeclaracionJurada +
				", numeroAntecedente='" + numeroAntecedente + '\'' +
				", delitosInvolucrados='" + delitosInvolucrados + '\'' +
				", fechaCondena='" + fechaCondena + '\'' +
				", descripcionEstadoCaso='" + descripcionEstadoCaso + '\'' +
				", tipo='" + tipo + '\'' +
				'}';
	}
}