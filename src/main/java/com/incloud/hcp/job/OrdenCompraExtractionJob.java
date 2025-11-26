package com.incloud.hcp.job;

import com.incloud.hcp.repository.ProveedorRepository;
import com.incloud.hcp.service.extractor.ExtractorOCService;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;


@Component
public class OrdenCompraExtractionJob {
    private final AtomicBoolean enabled = new AtomicBoolean(true);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //private JCOOrdenCompraPublicacionService jcoOrdenCompraPublicacionService;
    private ExtractorOCService extractorOCService;

    @Autowired
    public OrdenCompraExtractionJob(ExtractorOCService extractorOCService) {
        this.extractorOCService = extractorOCService;
    }

    // extraccion de ordenes de compra
//    @Scheduled(fixedRate = 4 * 60 * 1000 , initialDelay = 5 * 60 * 1000)
//    public void runOrdenCompraExtractor() {
//        if (enabled.get()) {
//            try {
//                long l = System.currentTimeMillis();
//                logger.info("Inicio Ejecucion de Job Extraccion de Ordenes de Compra. Fecha y hora: " + DateUtils.getCurrentTimestamp());
//                if(DateUtils.getCurrentHourOfDay() == 0) {
//                    this.extractorOCService.extraerOC(DateUtils.getFechaInicioAsStringByDiasAtras(1), DateUtils.getFechaActualAsStringPattern(DateUtils.STANDARD_DATE_FORMAT), false);
//                }
//                else{
//                    this.extractorOCService.extraerOC(DateUtils.getFechaActualAsStringPattern(DateUtils.STANDARD_DATE_FORMAT), DateUtils.getFechaActualAsStringPattern(DateUtils.STANDARD_DATE_FORMAT), false);
//                }
//                logger.info("Fin Ejecucion de Job Extraccion de Ordenes de Compra. Tiempo Total: " + (System.currentTimeMillis() - l) / 1000 + " segundos");
//            }
//            catch(Exception e){
//                String error = Utils.obtieneMensajeErrorException(e);
//                throw new RuntimeException(error);
//            }
//        }
//    }

    /*@Scheduled(fixedRate = 4 * 60 * 1000 , initialDelay = 5 * 60 * 1000)
    public void runOrdenCompraExtractor() {
        if (enabled.get()) {
            try {
                long l = System.currentTimeMillis();
                logger.error("Inicio Ejecucion de Job Extraccion de Ordenes de Compra. Fecha y hora: " + DateUtils.getCurrentTimestamp());
                if(DateUtils.getCurrentHourOfDay() == 0) {
                    jcoOrdenCompraPublicacionService.extraerOrdenCompraListRFC(DateUtils.getFechaInicioAsSapStringByDiasAtras(1), DateUtils.getFechaActualAsSapString(), true);
                }
                else{
                    jcoOrdenCompraPublicacionService.extraerOrdenCompraListRFC(DateUtils.getFechaActualAsSapString(), DateUtils.getFechaActualAsSapString(), true);
                }
                logger.error("Fin Ejecucion de Job Extraccion de Ordenes de Compra. Tiempo Total: " + (System.currentTimeMillis() - l) / 1000 + " segundos");
            }
            catch(Exception e){
                String error = Utils.obtieneMensajeErrorException(e);
                throw new RuntimeException(error);
            }
        }
    }*/

    public boolean toggle() {
        enabled.set(!enabled.get());
        return enabled.get();
    }

    public boolean current() {
        return enabled.get();
    }
}