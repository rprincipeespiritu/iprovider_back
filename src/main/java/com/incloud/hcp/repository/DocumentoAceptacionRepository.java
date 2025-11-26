package com.incloud.hcp.repository;

import com.incloud.hcp.domain.DocumentoAceptacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoAceptacionRepository extends JpaRepository<DocumentoAceptacion, Integer> {

    @Query("SELECT da FROM DocumentoAceptacion da where da.idEstadoDocumentoAceptacion not in (4) and da.fechaEmision between ?1 and ?2 and da.proveedorRuc = ?3")
    List<DocumentoAceptacion> getDocumentoAceptacionByFechaRegistroBetweenAndProveedorRuc(Date fechaInicio, Date fechaFin, String proveedorRuc);

    @Query("SELECT da FROM DocumentoAceptacion da where da.idEstadoDocumentoAceptacion not in (4) and da.fechaEmision between ?1 and ?2")
    List<DocumentoAceptacion> getDocumentoAceptacionByFechaRegistroBetween(Date fechaInicio, Date fechaFin);

    Optional<DocumentoAceptacion> findByNumeroDocumentoAceptacion(String numeroDocumentoAceptacion);

    @Query("SELECT da.id FROM DocumentoAceptacion da where da.numeroDocumentoAceptacion = ?1")
    Integer getIdDocumentoAceptacionByNumero (String numeroDocumentoAceptacion);

    @Query("SELECT da.id FROM DocumentoAceptacion da where da.numeroDocumentoAceptacion = ?1 and da.idEstadoDocumentoAceptacion = ?2")
    Integer getIdDocumentoAceptacionByNumeroAndEstado (String numeroDocumentoAceptacion, Integer idEstadoDocumentoAceptacion);

    @Query("SELECT da.id, da.idEstadoDocumentoAceptacion FROM DocumentoAceptacion da where da.numeroDocumentoAceptacion = ?1")
    List<Object[]> getIdDocumentoAceptacionAndEstadoByNumero (String numeroDocumentoAceptacion);

    @Query("SELECT da.id, da.idEstadoDocumentoAceptacion FROM DocumentoAceptacion da where da.numeroDocumentoAceptacion = ?1 and da.numeroRecepcion = ?2")
    List<Object[]> getIdDocumentoAceptacionAndEstadoByNumeroAndDocumento (String numeroDocumentoAceptacion,String materialDocument);

    @Query("SELECT da.id FROM DocumentoAceptacion da where da.numeroRecepcion = ?1")
    Integer getIdDocumentoAceptacionByNumeroRecepcion (String numeroDocumentoAceptacion);


    @Query("SELECT da FROM DocumentoAceptacion da where da.idTipoDocumentoAceptacion=?1 and da.id = ?2")
    DocumentoAceptacion getDocumentoAceptacionById(Integer idTipoDocumentoAceptacion, Integer idDocumentoAceptacion);

    @Transactional
    @Modifying
    @Query("update DocumentoAceptacion da set da.idEstadoDocumentoAceptacion =?1 where da.id = ?2")
    int updateEstadoDocumentoAceptacionById(int idEstadoDocumentoAceptacion, int idDocumentoAceptacion);


    @Query("SELECT da FROM DocumentoAceptacion da where da.actaSustento.idActaSustento = ?1")
    List<DocumentoAceptacion> getByActaSustento(Integer idActaSustento);

    @Query("SELECT da FROM DocumentoAceptacion da where da.idOrdenCompra = ?1")
    List<DocumentoAceptacion> getIdDocumentoAceptacionByIdOrdenCompra (Integer idOrdenCompra);
}