package com.incloud.hcp.repository;

import com.incloud.hcp.domain.ActaSustentoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActaSustentoDetalleRepository extends JpaRepository<ActaSustentoDetalle,Integer> {

    @Query("SELECT a FROM ActaSustentoDetalle a WHERE a.actaSustento.idActaSustento = ?1")
    List<ActaSustentoDetalle> findbyIdActaSustento(Integer idActaSustento);
}
