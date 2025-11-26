package com.incloud.hcp.repository;

import com.incloud.hcp.domain.OrdenCompraAdjunto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenCompraAdjuntoRepository extends JpaRepository<OrdenCompraAdjunto, Integer> {

    List<OrdenCompraAdjunto> findByNumeroOc(String numeroOc);
}
