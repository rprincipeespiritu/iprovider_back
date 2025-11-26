package com.incloud.hcp.repository;

import com.incloud.hcp.domain.ActaSustento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActaSustentoRepository extends JpaRepository<ActaSustento, Integer> {

    @Query("SELECT coalesce(max(ac.numeroActaSustento), 0) FROM ActaSustento ac")
    Integer getMaxNroActaSustento();



    @Query("SELECT a FROM ActaSustento a WHERE  a.estado in(3,2,4,5) ORDER BY a.numeroActaSustento DESC")
    List<ActaSustento> getListActaSustentoByFiltroPaginadoProveedor();

}
