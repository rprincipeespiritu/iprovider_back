package com.incloud.hcp.repository;

import com.incloud.hcp.domain.ProveedorAntecedenteRepresentanteLegal;
import com.incloud.hcp.domain.ProveedorRepresentanteLegal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProveedorAntecedenteRepresentanteLegalRepository extends JpaRepository<ProveedorAntecedenteRepresentanteLegal, Integer> {

    @Query(value = "SELECT parl FROM ProveedorAntecedenteRepresentanteLegal parl WHERE parl.proveedorRepresentanteLegal.idProveedorRepresentanteLegal=?1")
    List<ProveedorAntecedenteRepresentanteLegal> findRepresentanteLegal(Integer idProveedorRepresentanteLegal);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM ProveedorAntecedenteRepresentanteLegal parl  WHERE parl.proveedorRepresentanteLegal.idProveedorRepresentanteLegal=?1")
    void deleteProveedorAntecRepLegal(Integer idProveedorRepresentanteLegal);
}
