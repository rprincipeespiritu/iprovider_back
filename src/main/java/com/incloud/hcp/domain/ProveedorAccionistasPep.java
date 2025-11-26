package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name="proveedor_accionistas_pep")
public class ProveedorAccionistasPep extends BaseDomain implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_PROVEEDOR_ACCIONISTAS_PEP")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idProveedorAccionistasPep;


	@Column(name="CARGO_EJERCIDO")
	private String cargoEjercido;

	@Column(name="ENTIDAD")
	private String entidad;

	@Column(name="FECHA_INICIO_FIN")
	private String fechaInicioFin;

	@Column(name="FECHA_FIN")
	private String fechaFin;


	@ManyToOne
	@JoinColumn(name="ID_PROVEEDOR_ACCIONISTAS_ASOCIADOS")
	private ProveedorAccionistasAsociados proveedorAccionistasAsociados;



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

	public Integer getIdProveedorAccionistasPep() {
		return idProveedorAccionistasPep;
	}

	public void setIdProveedorAccionistasPep(Integer idProveedorAccionistasPep) {
		this.idProveedorAccionistasPep = idProveedorAccionistasPep;
	}

	public ProveedorAccionistasAsociados getProveedorAccionistasAsociados() {
		return proveedorAccionistasAsociados;
	}

	public void setProveedorAccionistasAsociados(ProveedorAccionistasAsociados proveedorAccionistasAsociados) {
		this.proveedorAccionistasAsociados = proveedorAccionistasAsociados;
	}

	@Override
	public String toString() {
		return "ProveedorAccionistasPep{" +
				"idProveedorAccionistasPep=" + idProveedorAccionistasPep +
				", cargoEjercido='" + cargoEjercido + '\'' +
				", entidad='" + entidad + '\'' +
				", fechaInicioFin='" + fechaInicioFin + '\'' +
				", fechaFin='" + fechaFin + '\'' +
				", proveedorAccionistasAsociados=" + proveedorAccionistasAsociados +
				'}';
	}
}