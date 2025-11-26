package com.incloud.hcp.repository;

import com.incloud.hcp.domain.OrdenCompraDetalleTexto;
import com.incloud.hcp.domain.OrdenCompraDetalleTextoRegistroInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdenCompraDetalleTextoRegistroInfoRepository extends JpaRepository<OrdenCompraDetalleTextoRegistroInfo, Integer> {


    Optional<OrdenCompraDetalleTextoRegistroInfo> findByIdOrdenCompraDetalleAndPosicion(Integer idOrdenCompraDetalle, String posicion);
}