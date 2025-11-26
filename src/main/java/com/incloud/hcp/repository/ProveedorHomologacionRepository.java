package com.incloud.hcp.repository;

import com.incloud.hcp.domain.Homologacion;
import com.incloud.hcp.domain.ProveedorHomologacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Created by MARCELO on 23/10/2017.
 */
public interface ProveedorHomologacionRepository extends JpaRepository<ProveedorHomologacion, Integer> {

    public List<ProveedorHomologacion> findByHomologacion(Homologacion homologacion);

 	@Modifying
    @Query("DELETE FROM ProveedorHomologacion p WHERE p.proveedor.idProveedor= ?1")
    void deleteRespuestaByIdProveedor(Integer idProveedor);

    @Query("SELECT p FROM ProveedorHomologacion p WHERE p.proveedor.idProveedor= ?1 AND p.homologacion.idHomologacion =?2")
    Optional<ProveedorHomologacion> consultarProveedorHomolagacion(Integer idProveedor, Integer idHomologacion);

    @Query("SELECT p FROM ProveedorHomologacion p WHERE p.proveedor.idProveedor= ?1 ")
    Optional<ProveedorHomologacion> consultarProveedorHomolagacionById(Integer idProveedor);

    @Query("SELECT p FROM ProveedorHomologacion p WHERE p.proveedor.idProveedor= ?1")
    List<ProveedorHomologacion> consultarRespuestasProveedorHomologacion(Integer idProveedor);
}
