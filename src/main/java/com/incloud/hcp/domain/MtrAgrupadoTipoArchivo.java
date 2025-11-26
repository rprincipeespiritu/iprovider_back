package com.incloud.hcp.domain;

import com.google.common.base.MoreObjects;
import com.incloud.hcp.domain._framework.BaseDomain;
import com.incloud.hcp.domain._framework.Identifiable;
import com.incloud.hcp.domain._framework.IdentifiableHashBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.logging.Logger;


@Entity
@Table(name = "mtr_agrupado_tipo_archivo")
public class MtrAgrupadoTipoArchivo extends BaseDomain implements Identifiable<Integer>, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(MtrAgrupadoTipoArchivo.class.getName());

	/***************************/
	/* Atributos de la Entidad */
	/***************************/

	// Raw attributes
	private Integer id;
	private String codigoAgrupadoTipoArchivo;
	private String descripcion;

	@Override
	public String entityClassName() {
		return MtrAgrupadoTipoArchivo.class.getSimpleName();
	}

	// -- [id] ------------------------

	@Override
	@Column(name = "mtr_agrupado_tipo_archivo_id", precision = 10)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public MtrAgrupadoTipoArchivo id(Integer id) {
		setId(id);
		return this;
	}

	@Override
	@Transient
	public boolean isIdSet() {
		return id != null;
	}
	// -- [codigoAgrupadoTipoArchivo] ------------------------

	@NotEmpty(message = "{message.mtrAgrupadoTipoArchivo.codigoAgrupadoTipoArchivo.requerido}")
	@Size(max = 10, message = "{message.mtrAgrupadoTipoArchivo.codigoAgrupadoTipoArchivo.sizeMax} {max} {message.caracter}")
	@Column(name = "codigo_agrupado_tipo_archivo", nullable = false, length = 10)
	public String getCodigoAgrupadoTipoArchivo() {
		return codigoAgrupadoTipoArchivo;
	}

	public void setCodigoAgrupadoTipoArchivo(String codigoAgrupadoTipoArchivo) {
		this.codigoAgrupadoTipoArchivo = codigoAgrupadoTipoArchivo;
	}

	public MtrAgrupadoTipoArchivo codigoAgrupadoTipoArchivo(String codigoAgrupadoTipoArchivo) {
		setCodigoAgrupadoTipoArchivo(codigoAgrupadoTipoArchivo);
		return this;
	}
	// -- [descripcion] ------------------------

	@NotEmpty(message = "{message.mtrAgrupadoTipoArchivo.descripcion.requerido}")
	@Size(max = 100, message = "{message.mtrAgrupadoTipoArchivo.descripcion.sizeMax} {max} {message.caracter}")
	@Column(name = "descripcion", nullable = false, length = 100)
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public MtrAgrupadoTipoArchivo descripcion(String descripcion) {
		setDescripcion(descripcion);
		return this;
	}

	/**
	 * Apply the default values.
	 */
	public MtrAgrupadoTipoArchivo withDefaults() {
		return this;
	}

	/**
	 * Equals implementation using a business key.
	 */
	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof MtrAgrupadoTipoArchivo && hashCode() == other.hashCode());
	}

	private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

	@Override
	public int hashCode() {
		return identifiableHashBuilder.hash(log, this);
	}

	/**
	 * Construct a readable string representation for this MtrAgrupadoTipoArchivo instance.
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this) //
				.add("id", getId()) //
				.add("codigoAgrupadoTipoArchivo", getCodigoAgrupadoTipoArchivo()) //
				.add("descripcion", getDescripcion()) //
				.toString();
	}
}