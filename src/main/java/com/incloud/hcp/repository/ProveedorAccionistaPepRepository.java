package com.incloud.hcp.repository;

import com.incloud.hcp.domain.ProveedorAccionistasPep;
import com.incloud.hcp.domain.ProveedorPep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProveedorAccionistaPepRepository extends JpaRepository<ProveedorAccionistasPep,Integer> {

    @Query(value = "SELECT ap FROM ProveedorAccionistasPep ap WHERE ap.proveedorAccionistasAsociados.idProveedorAccionistasAsociados = ?1")
    List<ProveedorAccionistasPep> findIdProveedorAccionista(Integer idProveedorAccionistasAsociados);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM ProveedorAccionistasPep ap WHERE ap.proveedorAccionistasAsociados.idProveedorAccionistasAsociados=?1")
    void deleteProveedorAcciAsoc(Integer idProveedorAccionista);
}
