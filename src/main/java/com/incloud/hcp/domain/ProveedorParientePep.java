package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name="proveedor_pariente_pep")
public class ProveedorParientePep extends BaseDomain implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_PROVEEDOR_PARIENTE_PEP")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idProveedorParientePep;

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

	@Column(name="FECHA_FIN")
	private String fechaFin;

	@Column(name="ID_NACIONALIDAD_PARIENTE")
	private String nacionalidadPariente;

	@Column(name="DNI_PARIENTE")
	private String dniPariente;


	@ManyToOne
	@JoinColumn(name="ID_PROVEEDOR_DECLARACION_JURADA")
	private ProveedorDeclaracionJurada proveedorDeclaracionJurada;

	public Integer getIdProveedorParientePep() {
		return idProveedorParientePep;
	}

	public void setIdProveedorParientePep(Integer idProveedorParientePep) {
		this.idProveedorParientePep = idProveedorParientePep;
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

	public String getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(String fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getNacionalidadPariente() {
		return nacionalidadPariente;
	}

	public void setNacionalidadPariente(String nacionalidadPariente) {
		this.nacionalidadPariente = nacionalidadPariente;
	}

	public ProveedorDeclaracionJurada getProveedorDeclaracionJurada() {
		return proveedorDeclaracionJurada;
	}

	public void setProveedorDeclaracionJurada(ProveedorDeclaracionJurada proveedorDeclaracionJurada) {
		this.proveedorDeclaracionJurada = proveedorDeclaracionJurada;
	}

	public String getDniPariente() {
		return dniPariente;
	}

	public void setDniPariente(String dniPariente) {
		this.dniPariente = dniPariente;
	}

	@Override
	public String toString() {
		return "ProveedorParientePep{" +
				"idProveedorParientePep=" + idProveedorParientePep +
				", vinculoParental='" + vinculoParental + '\'' +
				", nombresParientePep='" + nombresParientePep + '\'' +
				", cargoEjercidoPariente='" + cargoEjercidoPariente + '\'' +
				", entidadPariente='" + entidadPariente + '\'' +
				", fechaInicioFinPariente='" + fechaInicioFinPariente + '\'' +
				", fechaFin='" + fechaFin + '\'' +
				", nacionalidadPariente='" + nacionalidadPariente + '\'' +
				", proveedorDeclaracionJurada=" + proveedorDeclaracionJurada +
				", dniPariente=" + dniPariente +
				'}';
	}
}