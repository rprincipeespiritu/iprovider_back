package com.incloud.hcp.repository;

import com.incloud.hcp.domain.ActaSustentoDetalle;
import com.incloud.hcp.domain.ModificacionSolped;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ModificacionSolpedRepository extends JpaRepository<ModificacionSolped,Integer> {

    @Query("SELECT m FROM ModificacionSolped m WHERE m.nroSolped = ?1 ORDER BY m.idModificacionSolped DESC ")
    List<ModificacionSolped> findByNroSolped(Integer opSolicitudCompra);

    @Transactional
    @Modifying
    @Query("UPDATE  ModificacionSolped  m SET m.estadoSolped = ?1 WHERE m.nroSolped =?2")
    void modificarEstadoSolped(String flag,Integer nroSolped);

    @Query("SELECT m FROM ModificacionSolped m WHERE m.nroSolped = ?1 AND m.posicionSolped =?2 ORDER BY m.idModificacionSolped DESC ")
    List<ModificacionSolped> findByNroSolpedPosicion(Integer opSolicitudCompra,String posicion);

    @Query("SELECT m FROM ModificacionSolped m WHERE m.estadoSolped = '2' ORDER BY m.idModificacionSolped DESC ")
    List<ModificacionSolped> finbyEstado2();
}
