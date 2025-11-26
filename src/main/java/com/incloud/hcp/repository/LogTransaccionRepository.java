package com.incloud.hcp.repository;

import com.incloud.hcp.domain.LogTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LogTransaccionRepository extends JpaRepository<LogTransaccion, Integer> {

    List<LogTransaccion> findByLogUsuario(String identificador);

}