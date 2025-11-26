package com.incloud.hcp.repository;

import com.incloud.hcp.domain.ActaSustento;
import com.incloud.hcp.domain.MigProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MigProveedorRepository extends JpaRepository<MigProveedor, Integer> {



}
