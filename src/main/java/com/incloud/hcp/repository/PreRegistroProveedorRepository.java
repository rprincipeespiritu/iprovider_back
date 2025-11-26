package com.incloud.hcp.repository;

import com.incloud.hcp.domain.PreRegistroProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrador on 04/09/2017.
 */
public interface PreRegistroProveedorRepository extends JpaRepository<PreRegistroProveedor, Integer> {

    @Query("SELECT b FROM PreRegistroProveedor b WHERE b.estado=?1 order by b.ruc")
    List<PreRegistroProveedor> findByEstado(String estado);

    @Query("SELECT b FROM PreRegistroProveedor b WHERE b.idHcp= ?1")
    PreRegistroProveedor getPreRegistroProveedorByIdHcp(String idHcp);

    @Query("SELECT b FROM PreRegistroProveedor b WHERE b.email= ?1")
    PreRegistroProveedor getPreRegistroByEmailProv(String email);

    @Query("SELECT p FROM PreRegistroProveedor p WHERE p.ruc= ?1")
    PreRegistroProveedor getByRuc(String ruc);

    @Modifying
    @Query("DELETE FROM PreRegistroProveedor p WHERE p.idRegistro = ?1")
    void deleteByIdRegistro(Integer idRegistro);

    @Query(nativeQuery = true, value = "SELECT * FROM PRE_REGISTRO_PROVEEDOR ORDER BY ID_REGISTRO DESC LIMIT 1")
    PreRegistroProveedor findTop1ByOrderByIdRegistroDesc();

    @Query("SELECT b FROM PreRegistroProveedor b WHERE b.email= ?1")
    List<PreRegistroProveedor> findByEmail(String email);
}
