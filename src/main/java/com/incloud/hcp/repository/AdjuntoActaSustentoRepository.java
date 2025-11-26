package com.incloud.hcp.repository;

import com.incloud.hcp.domain.AdjuntoActaSustento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdjuntoActaSustentoRepository  extends JpaRepository<AdjuntoActaSustento,Integer> {

    @Query("SELECT a FROM AdjuntoActaSustento a WHERE a.actaSustento.idActaSustento = ?1")
    List<AdjuntoActaSustento> findbyIdActaSustento(Integer idActaSustento);
}
