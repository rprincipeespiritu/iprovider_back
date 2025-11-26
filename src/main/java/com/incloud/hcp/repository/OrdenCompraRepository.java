package com.incloud.hcp.repository;

import com.incloud.hcp.domain.CotizacionDetalle;
import com.incloud.hcp.domain.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Integer> {

    @Query("SELECT oc FROM OrdenCompra oc where oc.isActive = '1' and oc.estadoSap <> 'S'")
    List<OrdenCompra> getAllActive();

    @Query("SELECT oc FROM OrdenCompra oc where oc.isActive = '1' and oc.id = ?1 and oc.estadoSap <> 'S'")
    OrdenCompra getOrdenCompraById(Integer idOrdenCompra);

    @Query("SELECT oc FROM OrdenCompra oc where oc.isActive = '1' and oc.estadoSap <> 'S' and oc.fechaRegistro between ?1 and ?2 and oc.proveedorRuc = ?3")
    List<OrdenCompra> getOrdenCompraByFechaRegistroBetweenAndProveedorRuc(Date fechaInicio, Date fechaFin, String proveedorRuc);

    @Query("SELECT oc FROM OrdenCompra oc where oc.isActive = '1' and oc.estadoSap <> 'S' and oc.fechaRegistro between ?1 and ?2"            )
    List<OrdenCompra> getOrdenCompraByFechaRegistroBetween(Date fechaInicio, Date fechaFin);

    @Query("SELECT oc FROM OrdenCompra oc where oc.id= ?1 and oc.isActive = '1' and oc.estadoSap <> 'S'")
    Optional<OrdenCompra> findByIdAndIsActive(Integer idOrdenCompra);
    @Query("SELECT oc FROM OrdenCompra oc where oc.numeroOrdenCompra = ?1 and oc.isActive = '1' ")
    Optional<OrdenCompra> getOrdenCompraActivaByNumero(String numeroOrdenCompra);

    OrdenCompra findTopByNumeroOrdenCompraAndIsActive(String numeroOrdenCompra, String isActive);


    @Query("SELECT oc FROM OrdenCompra oc where oc.numeroOrdenCompra like CONCAT('%',?1,'%') and oc.isActive = '1'")
    List<OrdenCompra> getOrdenCompraActivaByNumero1(String numeroOrdenCompra);

    @Query("SELECT oc FROM OrdenCompra oc where oc.numeroOrdenCompra like CONCAT('%',?1,'%') and oc.isActive = '1' AND oc.proveedorRuc=?2")
    List<OrdenCompra> getOrdenCompraActivaByNumeroProveedor(String numeroOrdenCompra,String rucProveedor);

    @Query("SELECT oc FROM OrdenCompra oc where oc.numeroOrdenCompra = ?1 and oc.isActive = '1' and oc.estadoSap <> 'S'")
    Optional<OrdenCompra> getOrdenCompraLiberadaByNumero(String numeroOrdenCompra);

    @Query("SELECT oc FROM OrdenCompra oc where oc.numeroOrdenCompra = ?1 and oc.isActive = '1' and oc.estadoSap <> 'S' and oc.idEstadoOrdenCompra not in (4,5)")
    Optional<OrdenCompra> getOrdenCompraLiberadaActivaValidaByNumero(String numeroOrdenCompra);

    @Query("SELECT oc FROM OrdenCompra oc WHERE oc.numeroOrdenCompra IS NULL OR oc.numeroOrdenCompra = ''")
    List<OrdenCompra> getOrdenCompraSinNumeroDeOrden();

    @Query(nativeQuery = true,
            value = "SELECT c2.ID_LICITACION FROM ORDEN_COMPRA oc" +
                    " LEFT JOIN CCOMPARATIVO_PROVEEDOR cp ON oc.ID_ORDEN_COMPRA = cp.ID_ORDEN_COMPRA" +
                    " LEFT JOIN CCOMPARATIVO c2 ON cp.ID_CCOMPARATIVO = c2.ID_CCOMPARATIVO" +
                    " WHERE oc.ID_ORDEN_COMPRA = ?1")
    Integer getIdLicitacionByOrdenCompra(Integer idOrdenCompra);

    OrdenCompra findByNumeroOrdenCompra(String nroOrdenCompra);
}