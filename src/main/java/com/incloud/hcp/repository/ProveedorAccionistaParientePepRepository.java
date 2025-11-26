package com.incloud.hcp.repository;

import com.incloud.hcp.domain.ProveedorAccionistasParientePep;
import com.incloud.hcp.domain.ProveedorParientePep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProveedorAccionistaParientePepRepository extends JpaRepository<ProveedorAccionistasParientePep,Integer> {

    @Query(value = "SELECT ap FROM ProveedorAccionistasParientePep ap WHERE ap.proveedorAccionistasAsociados.idProveedorAccionistasAsociados = ?1")
    List<ProveedorAccionistasParientePep> findIdProveedorAccionistaPariente(Integer idProveedorAccionistasAsociados);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM ProveedorAccionistasParientePep ap WHERE ap.proveedorAccionistasAsociados.idProveedorAccionistasAsociados=?1")
    void deleteProveedorAcciAsoc(Integer idProveedorAccionista);
}
