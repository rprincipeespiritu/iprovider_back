package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.io.Serializable;


@Entity
@Table(name="proveedor_pep")
public class ProveedorPep extends BaseDomain implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_PROVEEDOR_PEP")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idProveedorPep;


	@Column(name="CARGO_EJERCIDO")
	private String cargoEjercido;

	@Column(name="ENTIDAD")
	private String entidad;

	@Column(name="FECHA_INICIO_FIN")
	private String fechaInicioFin;

	@Column(name="FECHA_FIN")
	private String fechaFin;


	@ManyToOne
	@JoinColumn(name="ID_PROVEEDOR_DECLARACION_JURADA")
	private ProveedorDeclaracionJurada proveedorDeclaracionJurada;

	public Integer getIdProveedorPep() {
		return idProveedorPep;
	}

	public void setIdProveedorPep(Integer idProveedorPep) {
		this.idProveedorPep = idProveedorPep;
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

	public String getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(String fechaFin) {
		this.fechaFin = fechaFin;
	}

	public ProveedorDeclaracionJurada getProveedorDeclaracionJurada() {
		return proveedorDeclaracionJurada;
	}

	public void setProveedorDeclaracionJurada(ProveedorDeclaracionJurada proveedorDeclaracionJurada) {
		this.proveedorDeclaracionJurada = proveedorDeclaracionJurada;
	}

	@Override
	public String toString() {
		return "ProveedorPep{" +
				"idProveedorPep=" + idProveedorPep +
				", cargoEjercido='" + cargoEjercido + '\'' +
				", entidad='" + entidad + '\'' +
				", fechaInicioFin='" + fechaInicioFin + '\'' +
				", fechaFin='" + fechaFin + '\'' +
				", proveedorDeclaracionJurada=" + proveedorDeclaracionJurada +
				'}';
	}
}