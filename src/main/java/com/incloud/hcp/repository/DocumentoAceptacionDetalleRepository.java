package com.incloud.hcp.repository;

import com.incloud.hcp.domain.DocumentoAceptacionDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoAceptacionDetalleRepository extends JpaRepository<DocumentoAceptacionDetalle, Integer> {

    @Query("SELECT dad FROM DocumentoAceptacionDetalle dad where dad.idDocumentoAceptacion = ?1")
    List<DocumentoAceptacionDetalle> getDocumentoAceptacionDetalleListByIdDocumentoAceptacion(Integer idDocumentoAceptaction);

    @Query("SELECT dad FROM DocumentoAceptacionDetalle dad where dad.idDocumentoAceptacion = ?1 and dad.idEstadoDocumentoAceptacionDetalle not in (4,5)")
    List<DocumentoAceptacionDetalle> getDocumentoAceptacionDetalleNoAnuladasListByIdDocumentoAceptacion(Integer idDocumentoAceptaction);

    List<DocumentoAceptacionDetalle> findDocumentoAceptacionDetalleByIdDocumentoAceptacion(Integer idDocumentoAceptaction);

    Optional<DocumentoAceptacionDetalle> findByNumeroDocumentoAceptacionAndNumeroItem(String numeroDocumentoAceptacion, Integer numeroItem);

    Optional<DocumentoAceptacionDetalle> findByNumeroDocumentoAceptacionAndPosicionOrdenCompraAndCantidadAceptadaCliente(String numeroDocumentoAceptacion, String posicionOrdenCompra, BigDecimal cantidadAceptadaCliente);

    @Query("SELECT dad FROM DocumentoAceptacionDetalle dad where dad.idDocumentoAceptacion = ?1")
    List<DocumentoAceptacionDetalle> getByIdDocumentoAceptacion(Integer idDocumentoAceptacion);

    @Modifying
    @Query("UPDATE DocumentoAceptacionDetalle dad SET dad.numeroDocumentoAceptacion= ?1  WHERE dad.documentoAceptacion.id =?2")
    void insertNumeroAceptacion(String numero,Integer id);

    @Query("SELECT dad FROM DocumentoAceptacionDetalle dad where dad.actaSustentoDetalle.idActaSustentoDetalle = ?1")
    DocumentoAceptacionDetalle getByIdActaSustentoDetalle(Integer idActaSustentoDetalle);
}