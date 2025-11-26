package com.incloud.hcp.repository;

import com.incloud.hcp.domain.OrdenCompraDetalleTexto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdenCompraDetalleTextoRepository extends JpaRepository<OrdenCompraDetalleTexto, Integer> {

    Optional<OrdenCompraDetalleTexto> findByIdOrdenCompraDetalleAndPosicion(Integer idOrdenCompraDetalle, String posicion);


}