package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The persistent class for the homologacion_respuesta database table.
 * 
 */
@Entity
@Table(name="pregunta_informacion_respuesta")
public class PreguntaInformacionRespuesta extends BaseDomain implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id_respuesta", unique=true, nullable=false)
	@GeneratedValue(generator = "respuesta_id_seq", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "respuesta_id_seq", sequenceName = "respuesta_id_seq", allocationSize = 1)
	private Integer idRespuesta;

	@Column(name="nro_orden", length=10)
	private String nroOrden;

	@Column(name="descripcion_respuesta" ,length=300)
	private String descripcionRespuesta;


	//uni-directional many-to-one association to Homologacion
	@ManyToOne
	@JoinColumn(name="id_pregunta_informacion", nullable=false)
	private PreguntaInformacion idPreguntaInformacion;

	public PreguntaInformacionRespuesta() {
	}

	public Integer getIdRespuesta() {
		return idRespuesta;
	}

	public void setIdRespuesta(Integer idRespuesta) {
		this.idRespuesta = idRespuesta;
	}

	public String getNroOrden() {
		return nroOrden;
	}

	public void setNroOrden(String nroOrden) {
		this.nroOrden = nroOrden;
	}

	public String getDescripcionRespuesta() {
		return descripcionRespuesta;
	}

	public void setDescripcionRespuesta(String descripcionRespuesta) {
		this.descripcionRespuesta = descripcionRespuesta;
	}

	public PreguntaInformacion getIdPreguntaInformacion() {
		return idPreguntaInformacion;
	}

	public void setIdPreguntaInformacion(PreguntaInformacion idPreguntaInformacion) {
		this.idPreguntaInformacion = idPreguntaInformacion;
	}

	@Override
	public String toString() {
		return "PreguntaInformacionRespuesta{" +
				"idRespuesta=" + idRespuesta +
				", nroOrden='" + nroOrden + '\'' +
				", descripcionRespuesta='" + descripcionRespuesta + '\'' +
				", idPreguntaInformacion=" + idPreguntaInformacion +
				'}';
	}
}