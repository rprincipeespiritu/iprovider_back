package com.incloud.hcp.repository;

import com.incloud.hcp.domain.ProveedorParientePep;
import com.incloud.hcp.domain.ProveedorPep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProveedorParientePepRepository extends JpaRepository<ProveedorParientePep,Integer> {

    @Query(value = "SELECT pp FROM ProveedorParientePep pp WHERE pp.proveedorDeclaracionJurada.idDeclaracionJurada=?1")
    List<ProveedorParientePep> findIdDeclaracionJurada(Integer idDeclaracionJurada);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM ProveedorParientePep pp WHERE pp.proveedorDeclaracionJurada.idDeclaracionJurada=?1")
    void deleteProvParientePep(Integer idProveedorDeclaracion);
}
