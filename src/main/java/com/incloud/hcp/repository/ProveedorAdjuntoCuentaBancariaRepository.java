package com.incloud.hcp.repository;

import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.domain.ProveedorAdjuntoCuentaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProveedorAdjuntoCuentaBancariaRepository extends JpaRepository<ProveedorAdjuntoCuentaBancaria, Integer> {

    List<ProveedorAdjuntoCuentaBancaria> findAllByIdProveedor(Proveedor proveedor);
}
