package com.incloud.hcp.repository;

import com.incloud.hcp.bean.LicitacionResponse;
import com.incloud.hcp.domain.Licitacion;
import com.incloud.hcp.domain.LicitacionDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by USER on 24/08/2017.
 */
public interface LicitacionDetalleRepository extends JpaRepository<LicitacionDetalle, Integer> {

    List<LicitacionDetalle> findByLicitacionOrderByDescripcion(Licitacion licitacion);

    @Query("select u FROM LicitacionDetalle u where u.licitacion = ?1 order by u.descripcion, u.solicitudPedido, u.posicionSolicitudPedido, u.idLicitacionDetalle")
    List<LicitacionDetalle> findByLicitacionOrdenado(Licitacion licitacion);


    List<LicitacionDetalle> findByLicitacion(Licitacion licitacion);

    @Query("select u FROM LicitacionDetalle u where u.licitacion.idLicitacion = ?1 order by u.solicitudPedido, u.posicionSolicitudPedido, u.descripcion")
    List<LicitacionDetalle> findByIdLicitacionOrdenado(Integer idLicitacion);

    @Modifying
    @Query("DELETE FROM LicitacionDetalle d WHERE d.licitacion.idLicitacion= ?1")
    void deleteDetailByLicitacion(Integer idLicitacion);

    @Query("select u FROM LicitacionDetalle u where u.licitacion.idLicitacion = ?1 and  u.idProveedor=?2")
    List<LicitacionDetalle> findByIdLicitacionOrdenadoProveedor(Integer idLicitacion, String idProveedor);

    @Query("SELECT COUNT(ld.idLicitacionDetalle) FROM LicitacionDetalle ld WHERE ld.indAdjudicada = 'X' AND ld.licitacion.idLicitacion = ?1 AND ld.idProveedor = ?2")
    Integer countAdjudicacionDeLicitacionPorProveedor(Integer idLicitacion, String idProveedor);

    @Query("SELECT ld FROM LicitacionDetalle ld WHERE ld.solicitudPedido=?1 and ld.posicionSolicitudPedido=?2")
    List<LicitacionDetalle> findBySolicitudPedidoAndPosicionSolicitudPedido(String codSolped, String posicion);
}