package com.incloud.hcp.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incloud.hcp.domain.Moneda;
import com.incloud.hcp.dto.FacturaSapDto;
import com.incloud.hcp.repository.MonedaRepository;
import com.incloud.hcp.service.MonedaService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class MonedaServiceImpl implements MonedaService {
    private MonedaRepository monedaRepository;

    @Autowired
    public void setMonedaRepository(MonedaRepository monedaRepository) {
        this.monedaRepository = monedaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Moneda> getListAll() {
        return monedaRepository.findAll();
    }
}