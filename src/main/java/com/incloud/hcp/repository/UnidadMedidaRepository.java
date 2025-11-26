package com.incloud.hcp.repository;

import com.incloud.hcp.domain.UnidadMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by USER on 24/09/2017.
 */
public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Integer> {

    public UnidadMedida findByCodigoSap(String codigoSap);

    public UnidadMedida getByCodigoSap(String codigoSap);

    @Query("SELECT a FROM UnidadMedida a where a.codigoSap = ?1")
    public List<UnidadMedida> listByCodigoSap(String codigoSap);
}
