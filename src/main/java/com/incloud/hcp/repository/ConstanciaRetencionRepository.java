package com.incloud.hcp.repository;

import com.incloud.hcp.domain.ConstanciaRetencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConstanciaRetencionRepository extends JpaRepository<ConstanciaRetencion, Integer> {
    boolean existsByNroRetencion(String nroRetencion);
}
