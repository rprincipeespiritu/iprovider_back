package com.incloud.hcp.repository;

import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.domain.EstadoCivil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EstadoCivilRepository extends JpaRepository<EstadoCivil, Integer> {


}
