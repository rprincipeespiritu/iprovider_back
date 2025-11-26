package com.incloud.hcp.job;

import com.incloud.hcp.service.ProveedorService;
import com.incloud.hcp.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ProveedorExtractionJob {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    private ProveedorService proveedorService;
   
    //@Scheduled(cron = "0 12 * * * *")
    public void scheduleCargaProveedoresSap() {
        logger.error("Cron Task scheduleCargaProveedoresSap:: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            // Correo.
            this.proveedorService.extraerProveedorSap();

        } catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleCargaProveedoresSap ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
        logger.error("Cron Task Fin JOB scheduleCargaProveedoresSap");
    }



}
