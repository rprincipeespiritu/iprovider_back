package com.incloud.hcp.repository;

import com.incloud.hcp.domain.ActaSustentoDetalle;
import com.incloud.hcp.domain.ActaSustentoEvaluacionProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActaSustentoEvaluacionProveedorRepository extends JpaRepository<ActaSustentoEvaluacionProveedor,Integer> {

    @Query("SELECT a FROM ActaSustentoEvaluacionProveedor a WHERE a.actaSustento.idActaSustento = ?1")
    List<ActaSustentoEvaluacionProveedor> findbyIdActaSustento(Integer idActaSustento);
}
