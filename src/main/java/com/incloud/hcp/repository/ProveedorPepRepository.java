package com.incloud.hcp.repository;

import com.incloud.hcp.domain.ActaSustentoDetalle;
import com.incloud.hcp.domain.ProveedorPep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProveedorPepRepository extends JpaRepository<ProveedorPep,Integer> {

    @Query(value = "SELECT pp FROM ProveedorPep pp WHERE pp.proveedorDeclaracionJurada.idDeclaracionJurada=?1")
    List<ProveedorPep> findIdDeclaracionJurada(Integer idDeclaracionJurada);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM ProveedorPep pp WHERE pp.proveedorDeclaracionJurada.idDeclaracionJurada=?1")
    void deleteProvPep(Integer idProveedorDeclaracion);
}
