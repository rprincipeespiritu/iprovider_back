package com.incloud.hcp.repository;

import com.incloud.hcp.domain.AreaCompras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AreaComprasRepository extends JpaRepository<AreaCompras, Integer> {

    //@Query("SELECT p FROM area_compras p")
    //List<AreaCompras> findAllAreaCompras();

    public List<AreaCompras> findAllByOrderByCodigoAsc();


    @Query("SELECT p FROM AreaCompras p  WHERE p.codigo=:codigo")
    AreaCompras findByCodigo(String codigo);

}
