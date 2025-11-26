package com.incloud.hcp.repository;

import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.domain.ProveedorRepresentanteLegal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepresentanteLegalRepository extends JpaRepository<ProveedorRepresentanteLegal, Integer> {

    @Query(value = "SELECT pr FROM ProveedorRepresentanteLegal pr WHERE pr.proveedorDeclaracionJurada.idDeclaracionJurada=?1")
    Optional<ProveedorRepresentanteLegal> findIdDeclaracionJurada(Integer idDeclaracionJurada);

    @Query(value = "SELECT * FROM PROVEEDOR_REPRESENTANTE_LEGAL WHERE ID_PROVEEDOR_DECLARACION_JURADA=?1", nativeQuery = true)
    List<ProveedorRepresentanteLegal> findAllByIdDeclaracionJurada(Integer idDeclaracionJurada);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM ProveedorRepresentanteLegal pr WHERE pr.proveedorDeclaracionJurada.idDeclaracionJurada=?1")
    void deleteProveedorRepLegal(Integer idProveedorDeclaracion);
}
