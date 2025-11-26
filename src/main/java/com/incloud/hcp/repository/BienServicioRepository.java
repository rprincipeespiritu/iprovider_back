package com.incloud.hcp.repository;

import com.incloud.hcp.domain.BienServicio;
import com.incloud.hcp.domain.RubroBien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by USER on 17/09/2017.
 */
@Repository
public interface BienServicioRepository extends JpaRepository<BienServicio, Integer>{

    BienServicio getByCodigoSap(String codigoSap);

    List<BienServicio> findByCodigoSap (String codigoSap);

    List<BienServicio> findAllByCodigoSap (String codigoSap);

    List<BienServicio> findAllByNumeroParte (String nroParte);

    List<BienServicio> findAllByRubroBien(RubroBien rubroBien);

    BienServicio findByKardexAndCodigoSap(Integer kardex, String Codigo);

    @Query("SELECT bs.idBienServicio FROM BienServicio bs where bs.kardex = ?1")
    Integer getByCodigoSapAndKardex(Integer kardex);

    @Query("SELECT b FROM BienServicio b WHERE b.codigoSap IN (:codigos)")
    List<BienServicio> findByCodigos(@Param("codigos") List<String> codigos);


    @Query(value = "SELECT TOP 1 ID_BIEN_SERVICIO  FROM BIEN_SERVICIO  ORDER BY ID_BIEN_SERVICIO  DESC",nativeQuery = true)
    Integer findByUltimoRegistro();
}
