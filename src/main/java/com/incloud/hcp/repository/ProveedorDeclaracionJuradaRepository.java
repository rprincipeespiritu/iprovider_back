package com.incloud.hcp.repository;

import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.domain.ProveedorDeclaracionJurada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ProveedorDeclaracionJuradaRepository extends JpaRepository<ProveedorDeclaracionJurada, Integer> {

    @Query(value = "SELECT pd FROM ProveedorDeclaracionJurada pd WHERE pd.proveedor.idProveedor=?1")
    List<ProveedorDeclaracionJurada> findIdProveedor(Integer idProveedor);

    @Query(value = "SELECT pd FROM ProveedorDeclaracionJurada pd WHERE pd.proveedor.idProveedor=?1 AND pd.tipoPersona=?2")
    List<ProveedorDeclaracionJurada> findIdProveedorTipoPersona(Integer idProveedor, String pj);
}
