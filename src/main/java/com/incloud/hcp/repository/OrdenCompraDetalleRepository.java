package com.incloud.hcp.repository;

import com.incloud.hcp.domain.LicitacionDetalle;
import com.incloud.hcp.domain.OrdenCompraDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenCompraDetalleRepository extends JpaRepository<OrdenCompraDetalle, Integer> {

    @Query("SELECT ocd FROM OrdenCompraDetalle ocd where ocd.idOrdenCompra = ?1 and ocd.ordenCompra.estadoSap in('L','1','A', '05') ")
    List<OrdenCompraDetalle> getAllByIdOrdenCompraLiberada(Integer idOrdenCompra);

    @Query("SELECT ocd FROM OrdenCompraDetalle ocd where ocd.idOrdenCompra = ?1")
    List<OrdenCompraDetalle> getAllByIdOrdenCompra(Integer idOrdenCompra);

    @Query("SELECT ocd FROM OrdenCompraDetalle ocd where ocd.idLicitacionDetalle = ?1 order by ocd.id desc")
    List<OrdenCompraDetalle> getAllByIdLicitacionDetalle(Integer idLicitacionDetalle);

    Optional<OrdenCompraDetalle> findByNumeroOrdenCompraAndPosicionAndIdOrdenCompra(String numeroOrdenCompra, String posicion, Integer idOrdenCompra);


    @Query("SELECT ocd FROM OrdenCompraDetalle ocd where ocd.opSolicitudCompra = ?1 and ocd.posicion=?2 and ocd.numeroOrdenCompra <> '' ")
    List<OrdenCompraDetalle> findBySolpedPosicion(Integer opSolicitudCompra, String posicion);

    @Query("SELECT ocd FROM OrdenCompraDetalle ocd where ocd.numeroOrdenCompra = ?1 ORDER BY  ocd.id asc ")
    List<OrdenCompraDetalle> findByNumeroOrdenCompra(String nroOc);
}