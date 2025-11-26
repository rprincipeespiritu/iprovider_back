package com.incloud.hcp.repository;

import com.incloud.hcp.domain.OrdenCompraDetalleTexto;
import com.incloud.hcp.domain.OrdenCompraDetalleTextoMaterialAmpliado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdenCompraDetalleTextoMaterialAmpliadoRepository extends JpaRepository<OrdenCompraDetalleTextoMaterialAmpliado, Integer> {


   Optional<OrdenCompraDetalleTextoMaterialAmpliado> findByIdOrdenCompraDetalleAndPosicion(Integer idOrdenCompraDetalle, String posicion);
}