package com.incloud.hcp.repository;

import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.domain.ProveedorAdjuntoCuentaBancaria;
import com.incloud.hcp.domain.ProveedorCuentaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProveedorCuentaBancariaRepository extends JpaRepository<ProveedorCuentaBancaria, Integer> {

}
