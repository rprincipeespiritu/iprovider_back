package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name="proveedor_accionistas_pariente_pep")
public class ProveedorAccionistasParientePep extends BaseDomain implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_PROVEEDOR_ACCIONISTAS_PARIENTE_PEP")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idProveedorAccionistasParientePep;

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

	@Column(name="FECHA_FIN_PARIENTE")
	private String fechaFinPariente;

	@Column(name="ID_NACIONALIDAD_PARIENTE")
	private String nacionalidadPariente;

	@Column(name="DNI_PARIENTE")
	private String dniPariente;


	@ManyToOne
	@JoinColumn(name="ID_PROVEEDOR_ACCIONISTAS_ASOCIADOS")
	private ProveedorAccionistasAsociados proveedorAccionistasAsociados;

	public Integer getIdProveedorAccionistasParientePep() {
		return idProveedorAccionistasParientePep;
	}

	public void setIdProveedorAccionistasParientePep(Integer idProveedorAccionistasParientePep) {
		this.idProveedorAccionistasParientePep = idProveedorAccionistasParientePep;
	}

	public ProveedorAccionistasAsociados getProveedorAccionistasAsociados() {
		return proveedorAccionistasAsociados;
	}

	public void setProveedorAccionistasAsociados(ProveedorAccionistasAsociados proveedorAccionistasAsociados) {
		this.proveedorAccionistasAsociados = proveedorAccionistasAsociados;
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

	public String getFechaFinPariente() {
		return fechaFinPariente;
	}

	public void setFechaFinPariente(String fechaFinPariente) {
		this.fechaFinPariente = fechaFinPariente;
	}

	public String getNacionalidadPariente() {
		return nacionalidadPariente;
	}

	public void setNacionalidadPariente(String nacionalidadPariente) {
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
		return "ProveedorAccionistasParientePep{" +
				"idProveedorAccionistasParientePep=" + idProveedorAccionistasParientePep +
				", vinculoParental='" + vinculoParental + '\'' +
				", nombresParientePep='" + nombresParientePep + '\'' +
				", cargoEjercidoPariente='" + cargoEjercidoPariente + '\'' +
				", entidadPariente='" + entidadPariente + '\'' +
				", fechaInicioFinPariente='" + fechaInicioFinPariente + '\'' +
				", fechaFinPariente='" + fechaFinPariente + '\'' +
				", nacionalidadPariente='" + nacionalidadPariente + '\'' +
				", dniPariente='" + dniPariente + '\'' +
				", proveedorAccionistasAsociados=" + proveedorAccionistasAsociados +
				'}';
	}
}