package com.incloud.hcp.repository;


import com.incloud.hcp.domain.ProveedorAntecedente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface ProveedorAntecedenteRepository extends JpaRepository<ProveedorAntecedente, Integer> {

    @Query(value = "SELECT pa FROM ProveedorAntecedente pa WHERE pa.proveedorDeclaracionJurada.idDeclaracionJurada =?1")
    List<ProveedorAntecedente> findProveedorDeclaracionJurada(Integer idDeclaracionJurada);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM ProveedorAntecedente pa WHERE pa.proveedorDeclaracionJurada.idDeclaracionJurada=?1")
    void deleteProvAntecedentes(Integer idProveedorDeclaracion);
}
