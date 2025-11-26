package com.incloud.hcp.service.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.incloud.hcp.domain.*;
import com.incloud.hcp.dto.ComprobantePagoEntradaDto;
import com.incloud.hcp.jco.comprobantePago.dto.ComprobantePagoDto;
import com.incloud.hcp.jco.comprobantePago.service.JCOComprobantePagoService;
import com.incloud.hcp.myibatis.mapper.ParametroMapper;
import com.incloud.hcp.repository.LogTransaccionRepository;
import com.incloud.hcp.repository.PrefacturaRepository;
import com.incloud.hcp.repository.ProveedorRepository;
import com.incloud.hcp.repository.SociedadRepository;
import com.incloud.hcp.service.ComprobantePagoService;
import com.incloud.hcp.service.PrefacturaService;
import com.incloud.hcp.service.ProveedorService;
import com.incloud.hcp.service._framework.BaseServiceImpl;
import com.incloud.hcp.service.notificacion.ComprobanteFactoringNotificacion;
import com.incloud.hcp.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ComprobantePagoServiceImpl extends BaseServiceImpl implements ComprobantePagoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private JCOComprobantePagoService jcoComprobantePagoService;
    private ComprobanteFactoringNotificacion comprobanteFactoringNotificacion;
    private ProveedorService proveedorService;

    @Autowired
    ProveedorRepository proveedorRepository;

    @Autowired
    SociedadRepository sociedadRepository;

    @Autowired
    private ParametroMapper parametroMapper;

    @Autowired
    private PrefacturaRepository prefacturaRepository;

    @Autowired
    private PrefacturaService prefacturaService;

    @Autowired
    private LogTransaccionRepository logTransaccionRepository;
    @Autowired
    public ComprobantePagoServiceImpl(JCOComprobantePagoService jcoComprobantePagoService,
                                      ComprobanteFactoringNotificacion comprobanteFactoringNotificacion,
                                      ProveedorService proveedorService,
                                      PrefacturaService prefacturaService) {
        this.jcoComprobantePagoService = jcoComprobantePagoService;
        this.comprobanteFactoringNotificacion = comprobanteFactoringNotificacion;
        this.proveedorService = proveedorService;
        this.prefacturaService = prefacturaService;
    }

    @Transactional(readOnly = true)
    public List<ComprobantePagoDto> getComprobantePagoList(ComprobantePagoEntradaDto bean) throws Exception {
        List<ComprobantePagoDto> listaRetorno = new ArrayList<>();
        if (!Optional.ofNullable(bean.getFechaInicio()).isPresent()) {
            throw new Exception("Debe ingresar valor de Fecha Inicio");
        }
        if (!Optional.ofNullable(bean.getFechaFin()).isPresent()) {
            throw new Exception("Debe ingresar valor de Fecha Fin");
        }

        String ruc = "";
        String codigoProv = "";

        if (Optional.ofNullable(bean.getEmail()).isPresent()) {
            Proveedor proveedor = proveedorRepository.getProveedorByEmail(bean.getEmail());
            if (Optional.ofNullable(proveedor).isPresent()) {
                ruc = proveedor.getRuc() != null ? proveedor.getRuc() : "";
                codigoProv = String.valueOf(proveedor.getAcreedorCodigoSap()) != null ? String.valueOf(proveedor.getAcreedorCodigoSap()) : "";
            }
        }
        String numeroComprobantePago = bean.getNroComprobante() != null ? bean.getNroComprobante() : "";
        String codigoSociedad = bean.getCodigoSociedad() != null ? bean.getCodigoSociedad() : "";
        String fechaInicioSapString = bean.getFechaInicio() != null ? DateUtils.utilDateToString(bean.getFechaInicio()) : "";
        String fechaFinSapString = bean.getFechaFin() != null ? DateUtils.utilDateToString(DateUtils.sumarRestarDias(bean.getFechaFin(), 1)) : "";
        Date fechaInicioPago =  bean.getFechaInicioPago();
        Date fechaFinPago = bean.getFechaFinPago() != null ? DateUtils.sumarRestarDias(bean.getFechaFinPago(), 1) : null;
        String estado = bean.getEstado() != null ? bean.getEstado() : "";
        String razonSocial = bean.getRazonSocial() != null ? bean.getRazonSocial() : "";
        String moneda = bean.getMoneda() != null ? bean.getMoneda() : "";

        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: fechaInicio = " + fechaInicioSapString);
        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: fechaFin = " + fechaFinSapString);
        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: numeroComprobantePago = " + numeroComprobantePago);
        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: codSociedad = " + codigoSociedad);
        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: email = " + bean.getEmail());
        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: ruc = " + ruc);
        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: codigoProv = " + codigoProv);

        listaRetorno = jcoComprobantePagoService
                .extraerComprobantePagoListRFC(
                        fechaInicioSapString,
                        fechaFinSapString,
                        ruc);

        /*Filtros adicionales*/

        List<ComprobantePagoDto> listaRetornoEstado = new ArrayList<>();
        /*Estado*/
        if(!estado.equals(""))
        {
            for(ComprobantePagoDto beanx: listaRetorno)
            {
                if(beanx.getEstado().contains(estado))
                {
                    listaRetornoEstado.add(beanx);
                }
            }
        }
        else
        {
            listaRetornoEstado = listaRetorno;
        }

        /*numeroComprobantePago*/
        List<ComprobantePagoDto> listaRetornoComprobantePago = new ArrayList<>();
        if(!numeroComprobantePago.equals(""))
        {
            for(ComprobantePagoDto beanx: listaRetornoEstado)
            {
                if(beanx.getNumeroComprobantePago().contains(numeroComprobantePago))
                {
                    listaRetornoComprobantePago.add(beanx);
                }
            }
        }
        else
        {
            listaRetornoComprobantePago = listaRetornoEstado;
        }

        /*razonSocial*/
        List<ComprobantePagoDto> listaRetornoRazonSocial = new ArrayList<>();
        if(!razonSocial.equals(""))
        {
            for(ComprobantePagoDto beanx: listaRetornoComprobantePago)
            {
                if(beanx.getRazonSocial().toUpperCase().contains(razonSocial.toUpperCase()))
                {
                    listaRetornoRazonSocial.add(beanx);
                }
            }
        }
        else
        {
            listaRetornoRazonSocial = listaRetornoComprobantePago;
        }

        /*Moneda*/
        List<ComprobantePagoDto> listaRetornoMoneda = new ArrayList<>();
        if(!moneda.equals(""))
        {
            for(ComprobantePagoDto beanx: listaRetornoRazonSocial)
            {
                if(beanx.getCodigoMoneda().contains(moneda))
                {
                    listaRetornoMoneda.add(beanx);
                }
            }
        }
        else
        {
            listaRetornoMoneda = listaRetornoRazonSocial;
        }


        /*Fechas de pago*/
        List<ComprobantePagoDto> listaRetornoFechasPago = new ArrayList<>();
        if(fechaInicioPago != null && fechaFinPago != null)
        {
            for(ComprobantePagoDto beanx: listaRetornoMoneda)
            {
                if(beanx.getFechaRealPago() != null) {
                    if ((beanx.getFechaRealPago().after(fechaInicioPago) && beanx.getFechaRealPago().before(fechaFinPago)) || beanx.getFechaRealPago().equals(fechaInicioPago)) {
                        listaRetornoFechasPago.add(beanx);
                    }
                }
            }
        }
        else
        {
            listaRetornoFechasPago = listaRetornoMoneda;
        }

        /*Sociedad*/
        List<ComprobantePagoDto> listaRetornoSociedad = new ArrayList<>();
        if(!codigoSociedad.equals(""))
        {
            for(ComprobantePagoDto beanx: listaRetornoFechasPago)
            {
                if(beanx.getCodigoSociedad().contains(codigoSociedad))
                {
                    listaRetornoSociedad.add(beanx);
                }
            }
        }
        else
        {
            listaRetornoSociedad = listaRetornoFechasPago;
        }

        listaRetornoSociedad.sort(Comparator.comparing(ComprobantePagoDto::getFechaContabilizacion).reversed());

        return listaRetornoSociedad;
    }

    @Transactional(readOnly = true)
    public ComprobantePagoDto setComprobanteFactoring(ComprobantePagoEntradaDto bean) throws Exception {

        if (!Optional.ofNullable(bean.getFechaInicio()).isPresent()) {
            throw new Exception("Debe ingresar valor de Fecha Inicio");
        }

//        if(!Optional.ofNullable(bean.getFechaFin()).isPresent()) {
//            throw new Exception("Debe ingresar valor de Fecha Fin");
//        }

        String numeroComprobantePago = bean.getNroComprobante() != null ? bean.getNroComprobante() : "";
        String codigoSociedad = bean.getCodigoSociedad() != null ? bean.getCodigoSociedad() : "";
        String fechaInicioSapString = bean.getFechaInicio() != null ? DateUtils.utilDateToString(bean.getFechaInicio()) : "";

        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: fechaInicio = " + fechaInicioSapString);
        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: numeroComprobantePago = " + numeroComprobantePago);
        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: codSociedad = " + codigoSociedad);

        ComprobantePagoDto comprobantePagoDto = jcoComprobantePagoService
                .setFactoryRFC(
                        numeroComprobantePago,
                        codigoSociedad,
                        String.valueOf(DateUtils.getYear(bean.getFechaInicio()))
                );

        Prefactura prefactura =  prefacturaRepository.findPrefacturaByDocumentoContable(numeroComprobantePago);

        Proveedor proveedor = proveedorService.getProveedorByRuc(prefactura.getProveedorRuc());
        String respuesta = "";
        respuesta = comprobanteFactoringNotificacion.enviar(parametroMapper.getMailSetting(), proveedor, bean.getNroComprobante());

        LogTransaccion logTransaccion = new LogTransaccion();
        logTransaccion.setEnvioTrama("comprobanteFactoringNotificacion");
        logTransaccion.setRespuestaCodigo(respuesta);
        logTransaccion.setTipoRegistro("Correo comprobanteFactoringNotificacion");
        this.logTransaccionRepository.save(logTransaccion);
        return comprobantePagoDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComprobantePagoDto> getComprobantePagoListPorFechasAndRuc(Date fechaInicio, Date fechaFin, String email, String numeroComprobantePago, String codigoSociedad) throws Exception {
        List<ComprobantePagoDto> comprobantePagoDtoList = new ArrayList<>();
        String ruc = "";
        String codigoProv = "";
        email = email != null ? email : "";
        if (!email.equals("")) {
            Proveedor proveedor = proveedorRepository.getProveedorByEmail(email);
            if (proveedor != null) {
                ruc = proveedor.getRuc() != null ? proveedor.getRuc() : "";
                codigoProv = String.valueOf(proveedor.getAcreedorCodigoSap()) != null ? String.valueOf(proveedor.getAcreedorCodigoSap()) : "";
            }
        }
        numeroComprobantePago = numeroComprobantePago != null ? numeroComprobantePago : "";
        codigoSociedad = codigoSociedad != null ? codigoSociedad : "";
        String fechaInicioSapString = fechaInicio != null ? DateUtils.utilDateToString(fechaInicio) : "";
        String fechaFinSapString = fechaFin != null ? DateUtils.utilDateToString(fechaFin) : "";

        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: fechaInicio = " + fechaInicioSapString);
        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: fechaFin = " + fechaFinSapString);
        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: numeroComprobantePago = " + numeroComprobantePago);
        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: codSociedad = " + codigoSociedad);
        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: email = " + email);
        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: ruc = " + ruc);
        logger.error("BUSQUEDA COMPROBANTES DE PAGO EN SAP [FILTRO]: codigoProv = " + codigoProv);

//        if(!numeroComprobantePago.isEmpty() || (!fechaInicioSapString.isEmpty() && !fechaFinSapString.isEmpty())) {
        if (!fechaInicioSapString.isEmpty() && !fechaFinSapString.isEmpty()) {
            comprobantePagoDtoList = jcoComprobantePagoService.extraerComprobantePagoListRFC(fechaInicioSapString, fechaFinSapString, ruc);
        }

        return comprobantePagoDtoList;
    }
}