package com.incloud.hcp.repository;

import com.incloud.hcp.domain.ConstanciaDetraccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConstanciaDetraccionRepository extends JpaRepository<ConstanciaDetraccion, Integer> {

    boolean existsByNroOperacion(Long nroOperacion);
}
