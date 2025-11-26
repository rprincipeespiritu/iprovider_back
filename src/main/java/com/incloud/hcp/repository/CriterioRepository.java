package com.incloud.hcp.repository;

import com.incloud.hcp.domain.ActaSustento;
import com.incloud.hcp.domain.Criterio;
import com.incloud.hcp.domain.CriteriosBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CriterioRepository extends JpaRepository<Criterio, Integer> {



    @Query("SELECT c FROM Criterio c  WHERE c.tipoCriterio=?1")
    List<Criterio> getCriterioByTipoCriterio(String tipoCriterio);


}
