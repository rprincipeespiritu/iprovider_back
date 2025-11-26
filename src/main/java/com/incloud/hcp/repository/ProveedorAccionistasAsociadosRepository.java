package com.incloud.hcp.repository;

import com.incloud.hcp.domain.ProveedorAccionistasAsociados;
import com.incloud.hcp.domain.ProveedorRepresentanteLegal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProveedorAccionistasAsociadosRepository extends JpaRepository<ProveedorAccionistasAsociados, Integer> {

    @Query(value = "SELECT paa FROM ProveedorAccionistasAsociados paa WHERE paa.proveedorDeclaracionJurada.idDeclaracionJurada=?1")
    List<ProveedorAccionistasAsociados> findIdDeclaracionJurada(Integer idDeclaracionJurada);
}
