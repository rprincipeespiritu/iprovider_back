package com.incloud.hcp.service.impl;

import com.incloud.hcp.domain.ConstanciaDetraccion;
import com.incloud.hcp.domain.ConstanciaDetraccionDetalle;
import com.incloud.hcp.dto.ConstanciaDetraccionDto;
import com.incloud.hcp.myibatis.mapper.ConstanciaDetraccionMapper;
import com.incloud.hcp.repository.ConstanciaDetraccionRepository;
import com.incloud.hcp.service.ConstanciaDetraccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import java.util.List;

@Service
public class ConstanciaDetraccionServiceImpl implements ConstanciaDetraccionService {

    @Autowired
    private ConstanciaDetraccionRepository constanciaDetraccionRepository;

    @Autowired
    private ConstanciaDetraccionMapper constanciaDetraccionMapper;
    @Override
    public String saveConstanciaDetraccion(MultipartFile file)  {
        try {
            String fileName = file.getOriginalFilename();
            if (!"text/plain".equals(file.getContentType()) || fileName == null || !fileName.toLowerCase().endsWith(".txt")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo debe ser un archivo de texto válido con extensión .txt.");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            String value;

            ConstanciaDetraccion constanciaDetraccion = new ConstanciaDetraccion();
            List<ConstanciaDetraccionDetalle> detallesDetraccion = new ArrayList<>();

            boolean isCabecera = true;
            ConstanciaDetraccionDetalle detalleDetraccion = null;

            while((line = reader.readLine()) != null){
                line = line.trim();

                if (line.isEmpty()) continue;

                if (line.startsWith("Datos de cabecera")){
                    isCabecera= true;
                    continue;
                }
                if (line.startsWith(("Datos de detalle"))){
                    isCabecera = false;
                    detalleDetraccion = null;
                    continue;
                }
                if (isCabecera){
                    if (line.startsWith("Número de operación")) {
                        value = line.split("   ",2)[1].trim().replaceAll("\"", " ");

                        constanciaDetraccion.setNroOperacion(Long.parseLong(value));
                    } else if (line.startsWith("Fecha y hora de pago") ) {
                        value = line.split("   ",2)[1].trim();
                        DateTimeFormatter formatterIn = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS");
                        LocalDateTime dateFormatterIn = LocalDateTime.parse(value, formatterIn);
                        DateTimeFormatter formatterOut = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        LocalDateTime dateFormatterOut = LocalDateTime.parse(dateFormatterIn.format(formatterOut), formatterOut);
                        System.out.println(dateFormatterOut);
                        constanciaDetraccion.setFechaPago(dateFormatterOut);
                    } else if (line.startsWith("Archivo")){
                        value = line.split("   ",2)[1].trim();
                        constanciaDetraccion.setArchivo(value);
                    } else if (line.startsWith("Lote")){
                        value = line.split("   ",2)[1].trim();
                        constanciaDetraccion.setLote(value);
                    } else if( line.startsWith("RUC del Adquiriente")){
                        value = line.split("   ",2)[1].trim();
                        constanciaDetraccion.setRucAdq(value);
                    } else if(line.startsWith("Razón Social del Adquiriente")){
                        value = line.split("   ",2)[1].trim();
                        constanciaDetraccion.setRazonSocialAdq(value);
                    } else if(line.startsWith("Número de depósitos")){
                        value = line.split("   ",2)[1].trim().replace("\"", "");
                        constanciaDetraccion.setNroDeposito(Integer.parseInt(value));
                    } else if (line.startsWith("Monto total")) {
                        value = line.split("   ",2)[1].trim().replace("\"", "");
                        constanciaDetraccion.setMontoTotal(Double.parseDouble(value));
                    }
                } else {
                    if (detalleDetraccion == null) {
                        detalleDetraccion = new ConstanciaDetraccionDetalle();
                    }

                    if (line.startsWith("Número de constancia")){
                        value = line.split("   ",2)[1].trim().replace("\"", "");
                        detalleDetraccion.setNroConstancia(Integer.parseInt(value));
                    } else if (line.startsWith("Tipo Documento del Proveedor")){
                        value = line.split("   ",2)[1].trim();
                        detalleDetraccion.setTipoDocProveedor(value);
                    } else if ( line.startsWith("Número Documento del Proveedor")){
                        value = line.split("   ",2)[1].trim();
                        detalleDetraccion.setNroDocProveedor(value);
                    } else if ( line.startsWith("Nombre/Razón Social del Proveedor")){
                        value = line.split("   ",2)[1].trim();
                        detalleDetraccion.setRazonSocialProv(value);
                    } else if ( line.startsWith("Código operación")){
                        value = line.split("   ",2)[1].trim();
                        detalleDetraccion.setCodigoOperacion(value);
                    } else if (line.startsWith("Nombre operación")) {
                        value = line.split("   ",2)[1].trim();
                        detalleDetraccion.setNombreOperacion(value);
                    } else if (line.startsWith("Código bien o servicio")) {
                        value = line.split("   ",2)[1].trim();
                        detalleDetraccion.setCodigoBienServ(value);
                    } else if (line.startsWith("Nombre bien o servicio")){
                        value = line.split("   ",2)[1].trim();
                        detalleDetraccion.setNombreBienServ(value);
                    } else if (line.startsWith("Monto depósito")){
                        value = line.split("   ",2)[1].trim().replace("\"", "");
                        detalleDetraccion.setMontoDeposito(Double.parseDouble(value));
                    } else if (line.startsWith("Periodo Tributario")){
                        value = line.split("   ",2)[1].trim();
                        detalleDetraccion.setPeriodoTributario(value);
                    } else if (line.startsWith("Tipo de Comprobante")){
                        value = line.split("   ",2)[1].trim();
                        detalleDetraccion.setTipoComprobante(value);
                    } else if (line.startsWith("Número de Comprobante")){
                        value = line.split("   ",2)[1].trim();
                        detalleDetraccion.setNroComprobante(value);
                    }

                    if (line.startsWith("Número de Comprobante")) {
                        detalleDetraccion.setConstanciaCab(constanciaDetraccion);
                        detallesDetraccion.add(detalleDetraccion);
                        detalleDetraccion = null;
                    }
                }
            }
            if(constanciaDetraccionRepository.existsByNroOperacion(constanciaDetraccion.getNroOperacion()) && constanciaDetraccion.getNroOperacion() != null){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Este archivo ya ha sido procesado");
            }

            if (constanciaDetraccion.getNroDeposito() == null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo procesar el archivo");
            }
            constanciaDetraccion.setDetalles(detallesDetraccion);
            constanciaDetraccionRepository.save(constanciaDetraccion);
            return "Archivo procesado y almacenado exitosamente.";

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<ConstanciaDetraccionDto> getListaDetracciones(String razonSocial,
                                                              String fechaInicio,
                                                              String fechaFin,
                                                              String nroComprobante,
                                                              String ruc) {
        try {
            LocalDateTime fechaInicial = fechaInicio != null && !fechaInicio.isEmpty() ? formateaFecha(fechaInicio, true): null;
            LocalDateTime fechaFinal = fechaFin != null && !fechaFin.isEmpty() ? formateaFecha(fechaFin, false):null;


            return constanciaDetraccionMapper.getListaDetracciones(razonSocial,
                    fechaInicial,
                    fechaFinal,
                    nroComprobante,
                    ruc);
        }catch (DateTimeParseException e){
            throw new IllegalArgumentException("Formato de fecha incorrecto, por favor validar");
        }

    }

    private LocalDateTime formateaFecha (String fecha, boolean isFirst) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate localDate = LocalDate.parse(fecha, formatter);
        return isFirst ?
                localDate.atStartOfDay():
                localDate.atTime(23, 59, 59);

    }
}
