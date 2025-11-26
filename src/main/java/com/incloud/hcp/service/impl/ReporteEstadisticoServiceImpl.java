package com.incloud.hcp.service.impl;

import com.incloud.hcp.config.excel.ExcelDefault;
import com.incloud.hcp.domain.Licitacion;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.dto.estadistico.*;
import com.incloud.hcp.dto.graficos.DataGrafico;
import com.incloud.hcp.dto.graficos.DataLabel;
import com.incloud.hcp.dto.graficos.JsonGraficoSalidaDto;
import com.incloud.hcp.dto.graficos.VizProperties;
import com.incloud.hcp.enums.EstadoLicitacionEnum;
import com.incloud.hcp.myibatis.mapper.ReporteEstadisticoMapper;
import com.incloud.hcp.repository.ProveedorRepository;
import com.incloud.hcp.service.ReporteEstadisticoService;
import com.incloud.hcp.util.DateUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ReporteEstadisticoServiceImpl implements ReporteEstadisticoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // inicio mizalo
    protected final String CONFIG_TITLE_REPORTE = "com/incloud/hcp/excel/ReporteStatusPeticionOferta.xml";
    protected final String CONFIG_TITLE_REPORTE_ADJUDICACION = "com/incloud/hcp/excel/ReporteAdjudicacion.xml";
    protected final String CONFIG_TITLE_REPORTE_PARTICIPACION = "com/incloud/hcp/excel/ReporteParticipacion.xml";
    //fin mizalo

    @Autowired
    private ProveedorRepository proveedorRepository;


    @Autowired
    private ReporteEstadisticoMapper reporteEstadisticoMapper;

    public ReporteEstadisticoAdjudicacionSalidaDto reporteAdjudicacion(String ruc) throws Exception {
        ReporteEstadisticoAdjudicacionSalidaDto result = new ReporteEstadisticoAdjudicacionSalidaDto();
        Proveedor proveedor = this.proveedorRepository.getProveedorByRuc(ruc);
        if (!Optional.ofNullable(proveedor).isPresent()) {
            throw new Exception("No se encontró Proveedor con RUC: " + ruc);
        }
        result.setProveedor(proveedor);

        Date fechaActual = DateUtils.obtenerFechaActual();
        Integer anno = DateUtils.getYear(fechaActual);
        ReporteEstadisticoAdjudicacionEntradaDto bean = new ReporteEstadisticoAdjudicacionEntradaDto();
        bean.setRuc(ruc);
        bean.setAnno(anno);
        List<ReporteEstadisticoAdjudicacionDto> data =this.reporteEstadisticoMapper.reporteEstadisticoAdjudicacion(bean);
        result.setData(data);
        return result;

    }

    public ReporteEstadisticoParticipacionSalidaDto reporteParticipacion(String ruc) throws Exception {
        ReporteEstadisticoParticipacionSalidaDto result = new ReporteEstadisticoParticipacionSalidaDto();
        Proveedor proveedor = this.proveedorRepository.getProveedorByRuc(ruc);
        if (!Optional.ofNullable(proveedor).isPresent()) {
            throw new Exception("No se encontró Proveedor con RUC: " + ruc);
        }
        result.setProveedor(proveedor);

        Date fechaActual = DateUtils.obtenerFechaActual();
        Integer anno = DateUtils.getYear(fechaActual);
        ReporteEstadisticoParticipacionEntradaDto bean = new ReporteEstadisticoParticipacionEntradaDto();
        bean.setRuc(ruc);
        bean.setAnno(anno);

        List<ReporteEstadisticoParticipacionDto> data =this.reporteEstadisticoMapper.reporteEstadisticoParticipacion(bean);
        result.setData(data);

        return result;
    }

    public ReporteStatusPeticionOfertaSalidaDto reporteStatusPeticionOferta(ReporteStatusPeticionOfertaEntradaDto bean) throws Exception {
        ReporteStatusPeticionOfertaSalidaDto result = new ReporteStatusPeticionOfertaSalidaDto();
        String ruc = bean.getRuc();
        if (StringUtils.isBlank(ruc)) {
            throw new Exception("Debe ingresar RUC");
        }
        Proveedor proveedor = this.proveedorRepository.getProveedorByRuc(ruc);
        if (!Optional.ofNullable(proveedor).isPresent()) {
            throw new Exception("No se encontró Proveedor con RUC: " + ruc);
        }
        if (!Optional.ofNullable(bean.getFechaInicio()).isPresent()) {
            throw new Exception("Debe ingresar Fecha de Inicio");
        }
        if (!Optional.ofNullable(bean.getFechaFin()).isPresent()) {
            throw new Exception("Debe ingresar Fecha Final");
        }
        if  (bean.getFechaFin().before(bean.getFechaInicio())) {
            throw new Exception("Fecha Final debe ser mayor igual a la Fecha de Inicio");
        }

//        Date fechaComprobacion = DateUtils.sumarRestarDias(bean.getFechaInicio(), 20);
        Date fechaFin = DateUtils.sumarRestarDias(bean.getFechaFin(), 1);
        bean.setFechaFin(fechaFin);
//        if  (fechaComprobacion.before(fechaFin)) {
//            throw new Exception("El intervalo máximo de dias entre ambas fechas debe ser de 20 dias");
//        }

        ReporteStatusPeticionOfertaIntermedioDto beanIntermedio = new ReporteStatusPeticionOfertaIntermedioDto();
        beanIntermedio.setRuc(ruc);
        beanIntermedio.setFechaInicio(bean.getFechaInicio());
        beanIntermedio.setFechaFin(bean.getFechaFin());
        beanIntermedio.setRazonSocial(null);
        beanIntermedio.setEstadoLicitacion(null);
        beanIntermedio.setEstadoNotLicitacion(null);

        logger.error("Ingresando reporteStatusPeticionOferta 01 todos:" + beanIntermedio.toString());
        List<Licitacion> licitacionListTodos = this.reporteEstadisticoMapper.reporteStatusPeticionOferta(beanIntermedio);
        logger.error("Ingresando reporteStatusPeticionOferta 01 todos size:" + licitacionListTodos.size());
        result.setLicitacionListTodos(licitacionListTodos);

        beanIntermedio.setEstadoLicitacion(EstadoLicitacionEnum.ADJUDICADA.getCodigo());
        logger.error("Ingresando reporteStatusPeticionOferta 02 adju:" + beanIntermedio.toString());
        List<Licitacion> licitacionListAdjudicadas = this.reporteEstadisticoMapper.reporteStatusPeticionOferta(beanIntermedio);
        logger.error("Ingresando reporteStatusPeticionOferta 02 adju size :" + licitacionListAdjudicadas.size());
        result.setLicitacionListAdjudicadas(licitacionListAdjudicadas);

        ReporteStatusPeticionOfertaIntermedioDto beanIntermedio02 = new ReporteStatusPeticionOfertaIntermedioDto();
        beanIntermedio02.setRuc(ruc);
        beanIntermedio02.setFechaInicio(bean.getFechaInicio());
        beanIntermedio02.setFechaFin(bean.getFechaFin());
        beanIntermedio02.setRazonSocial("");
        beanIntermedio02.setEstadoLicitacion("");
        beanIntermedio02.setEstadoNotLicitacion(EstadoLicitacionEnum.ADJUDICADA.getCodigo());
        logger.error("Ingresando reporteStatusPeticionOferta 03 en proc:" + beanIntermedio02.toString());
        List<Licitacion> licitacionListEnProceso = this.reporteEstadisticoMapper.reporteStatusPeticionOferta(beanIntermedio02);
        result.setLicitacionListEnProceso(licitacionListEnProceso);
        logger.error("Ingresando reporteStatusPeticionOferta 03 en proc size :" + licitacionListEnProceso.size());
        return result;
    }


    public ReporteStatusPeticionOfertaSalidaDto reporteStatusPeticionOfertaTodosPaginado(
            ReporteStatusPeticionOfertaEntradaPaginadoDto bean) throws Exception {
        ReporteStatusPeticionOfertaSalidaDto result = new ReporteStatusPeticionOfertaSalidaDto();
        String ruc = bean.getRuc();
        if (StringUtils.isBlank(ruc)) {
            throw new Exception("Debe ingresar RUC");
        }
        Proveedor proveedor = this.proveedorRepository.getProveedorByRuc(ruc);
        if (!Optional.ofNullable(proveedor).isPresent()) {
            throw new Exception("No se encontró Proveedor con RUC: " + ruc);
        }
        if (!Optional.ofNullable(bean.getFechaInicio()).isPresent()) {
            throw new Exception("Debe ingresar Fecha de Inicio");
        }
        if (!Optional.ofNullable(bean.getFechaFin()).isPresent()) {
            throw new Exception("Debe ingresar Fecha Final");
        }
        if  (bean.getFechaFin().before(bean.getFechaInicio())) {
            throw new Exception("Fecha Final debe ser mayor igual a la Fecha de Inicio");
        }

        Date fechaFin = DateUtils.sumarRestarDias(bean.getFechaFin(), 1);
        ReporteStatusPeticionOfertaIntermedioDto beanIntermedio = new ReporteStatusPeticionOfertaIntermedioDto();
        beanIntermedio.setRuc(ruc);
        beanIntermedio.setFechaInicio(bean.getFechaInicio());
        beanIntermedio.setFechaFin(fechaFin);
        beanIntermedio.setRazonSocial("");
        beanIntermedio.setEstadoLicitacion("");
        beanIntermedio.setEstadoNotLicitacion("");
        Integer nroRegistros = bean.getNroRegistros();
        Integer paginaMostrar = bean.getPaginaMostrar();
        paginaMostrar = new Integer((paginaMostrar.intValue() - 1) * nroRegistros);

        beanIntermedio.setNroRegistros(nroRegistros);
        beanIntermedio.setPaginaMostrar(paginaMostrar);
        logger.error("Ingresando reporteStatusPeticionOfertaPaginado 01 :" + beanIntermedio.toString());
        List<Licitacion> licitacionListTodos = this.reporteEstadisticoMapper.reporteStatusPeticionOfertaPaginado(beanIntermedio);
        result.setLicitacionListTodos(licitacionListTodos);
        return result;
    }

    public ReporteStatusPeticionOfertaSalidaDto reporteStatusPeticionOfertaAdjuPaginado(
            ReporteStatusPeticionOfertaEntradaPaginadoDto bean) throws Exception {
        ReporteStatusPeticionOfertaSalidaDto result = new ReporteStatusPeticionOfertaSalidaDto();
        String ruc = bean.getRuc();
        if (StringUtils.isBlank(ruc)) {
            throw new Exception("Debe ingresar RUC");
        }
        Proveedor proveedor = this.proveedorRepository.getProveedorByRuc(ruc);
        if (!Optional.ofNullable(proveedor).isPresent()) {
            throw new Exception("No se encontró Proveedor con RUC: " + ruc);
        }
        if (!Optional.ofNullable(bean.getFechaInicio()).isPresent()) {
            throw new Exception("Debe ingresar Fecha de Inicio");
        }
        if (!Optional.ofNullable(bean.getFechaFin()).isPresent()) {
            throw new Exception("Debe ingresar Fecha Final");
        }
        if  (bean.getFechaFin().before(bean.getFechaInicio())) {
            throw new Exception("Fecha Final debe ser mayor igual a la Fecha de Inicio");
        }

        Date fechaFin = DateUtils.sumarRestarDias(bean.getFechaFin(), 1);
        ReporteStatusPeticionOfertaIntermedioDto beanIntermedio = new ReporteStatusPeticionOfertaIntermedioDto();
        beanIntermedio.setRuc(ruc);
        beanIntermedio.setFechaInicio(bean.getFechaInicio());
        beanIntermedio.setFechaFin(fechaFin);
        beanIntermedio.setRazonSocial("");
        beanIntermedio.setEstadoLicitacion("");
        beanIntermedio.setEstadoNotLicitacion("");
        Integer nroRegistros = bean.getNroRegistros();
        Integer paginaMostrar = bean.getPaginaMostrar();
        paginaMostrar = new Integer((paginaMostrar.intValue() - 1) * nroRegistros);
        beanIntermedio.setEstadoLicitacion(EstadoLicitacionEnum.ADJUDICADA.getCodigo());

        beanIntermedio.setNroRegistros(nroRegistros);
        beanIntermedio.setPaginaMostrar(paginaMostrar);
        List<Licitacion> licitacionListAdju = this.reporteEstadisticoMapper.reporteStatusPeticionOfertaPaginado(beanIntermedio);
        result.setLicitacionListAdjudicadas(licitacionListAdju);
        return result;
    }

    public ReporteStatusPeticionOfertaSalidaDto reporteStatusPeticionOfertaEnProcPaginado(
            ReporteStatusPeticionOfertaEntradaPaginadoDto bean) throws Exception {
        ReporteStatusPeticionOfertaSalidaDto result = new ReporteStatusPeticionOfertaSalidaDto();
        String ruc = bean.getRuc();
        if (StringUtils.isBlank(ruc)) {
            throw new Exception("Debe ingresar RUC");
        }
        Proveedor proveedor = this.proveedorRepository.getProveedorByRuc(ruc);
        if (!Optional.ofNullable(proveedor).isPresent()) {
            throw new Exception("No se encontró Proveedor con RUC: " + ruc);
        }
        if (!Optional.ofNullable(bean.getFechaInicio()).isPresent()) {
            throw new Exception("Debe ingresar Fecha de Inicio");
        }
        if (!Optional.ofNullable(bean.getFechaFin()).isPresent()) {
            throw new Exception("Debe ingresar Fecha Final");
        }
        if  (bean.getFechaFin().before(bean.getFechaInicio())) {
            throw new Exception("Fecha Final debe ser mayor igual a la Fecha de Inicio");
        }

        Date fechaFin = DateUtils.sumarRestarDias(bean.getFechaFin(), 1);
        ReporteStatusPeticionOfertaIntermedioDto beanIntermedio = new ReporteStatusPeticionOfertaIntermedioDto();
        beanIntermedio.setRuc(ruc);
        beanIntermedio.setFechaInicio(bean.getFechaInicio());
        beanIntermedio.setFechaFin(fechaFin);
        beanIntermedio.setRazonSocial("");
        beanIntermedio.setEstadoLicitacion("");
        beanIntermedio.setEstadoNotLicitacion("");
        Integer nroRegistros = bean.getNroRegistros();
        Integer paginaMostrar = bean.getPaginaMostrar();
        paginaMostrar = new Integer((paginaMostrar.intValue() - 1) * nroRegistros);
        beanIntermedio.setEstadoNotLicitacion(EstadoLicitacionEnum.ADJUDICADA.getCodigo());

        beanIntermedio.setNroRegistros(nroRegistros);
        beanIntermedio.setPaginaMostrar(paginaMostrar);
        List<Licitacion> licitacionListEnProc = this.reporteEstadisticoMapper.reporteStatusPeticionOfertaPaginado(beanIntermedio);
        result.setLicitacionListEnProceso(licitacionListEnProc);
        return result;
    }

    // inicio mizalo
    @Override
    public ReporteEstadisticoAdjudicacionSalidaDto reporteAdjudicacionAcreedor(String acreedor) throws Exception {
        ReporteEstadisticoAdjudicacionSalidaDto result = new ReporteEstadisticoAdjudicacionSalidaDto();
        Proveedor proveedor = this.proveedorRepository.getProveedorByAcreedorCodigoSap(acreedor);
        if (!Optional.ofNullable(proveedor).isPresent()) {
            throw new Exception("No se encontró Proveedor con Código Acreedor: " + acreedor);
        }
        result.setProveedor(proveedor);

        Date fechaActual = DateUtils.obtenerFechaActual();
        Integer anno = DateUtils.getYear(fechaActual);
        ReporteEstadisticoAdjudicacionEntradaDto bean = new ReporteEstadisticoAdjudicacionEntradaDto();
        bean.setRuc(proveedor.getRuc());
        bean.setAnno(anno);
        List<ReporteEstadisticoAdjudicacionDto> data =this.reporteEstadisticoMapper.reporteEstadisticoAdjudicacion(bean);
        result.setData(data);
        return result;

    }

    @Override
    public ReporteEstadisticoAdjudicacionGrupoCompraSalidaDto reporteAdjudicacionGrupoCompra(String grupoCompra) throws Exception {
        ReporteEstadisticoAdjudicacionGrupoCompraSalidaDto resultBean = new ReporteEstadisticoAdjudicacionGrupoCompraSalidaDto();

        Date fechaActual = DateUtils.obtenerFechaActual();
        Integer anno = DateUtils.getYear(fechaActual);
        ReporteEstadisticoAdjudicacionEntradaDto bean = new ReporteEstadisticoAdjudicacionEntradaDto();
        bean.setGrupoCompra(grupoCompra);
        bean.setAnno(anno);
        List<ReporteEstadisticoAdjudicacionDto> dataListMapper =this.reporteEstadisticoMapper.reporteEstadisticoAdjudicacion(bean);

        DataLabel dataLabel = new DataLabel();
        VizProperties vizProperties = new VizProperties();
        vizProperties.setDataLabel(dataLabel);
        List<DataGrafico> dataList = new ArrayList<>();

        JsonGraficoSalidaDto jsonGrafico = new JsonGraficoSalidaDto();
        jsonGrafico.setVizProperties(vizProperties);

        if (dataListMapper != null && dataListMapper.size() > 0) {
            String rucBuscar = "-1";
            int contador = 0;
            for (ReporteEstadisticoAdjudicacionDto beanMapper : dataListMapper) {
                //logger.error("reporteAdjudicacionGrupoCompra beanMapper: " + beanMapper.toString());
                if (!rucBuscar.equals(beanMapper.getRuc())) {
                    contador = 0;
                    rucBuscar = beanMapper.getRuc();
                }
                else {
                    contador++;
                }
                DataGrafico dataGrafico = new DataGrafico();
                dataGrafico.setId(contador);
                dataGrafico.setIndicador(beanMapper.getRuc() + " - " + beanMapper.getRazonSocial());
                dataGrafico.setDim0(beanMapper.getTotalAdjudicados().toString());
                dataGrafico.setDim1(beanMapper.getTotalParticipacion().toString());
                dataGrafico.setMeasure1(beanMapper.getDescripcionMes());
                dataList.add(dataGrafico);
            }
        }
        jsonGrafico.setData(dataList);


        /* Resumen */
        List<ReporteEstadisticoAdjudicacionDto> dataResumen = this.reporteEstadisticoMapper
                .reporteEstadisticoAdjudicacionResumenGrupoCompra(bean);
        resultBean.setDataResumen(dataResumen);
        resultBean.setDataDetalle(jsonGrafico);
        return resultBean;

    }

    @Override
    public SXSSFWorkbook reporteAdjudicacionExcelGrupoCompra(String ruc, String acreedor) throws Exception {
        logger.debug("Ingresando reporteAdjudicacionExcelGrupoCompra: ");
        if (StringUtils.isBlank(ruc) && StringUtils.isBlank(acreedor)) {
            throw new Exception("Debe ingresar datos correctos.");
        }
        List<ReporteEstadisticoAdjudicacionExcelGrupoCompraDto> lista =
                this.reporteAdjudicacionExcelDataGrupoCompra(ruc, acreedor);

        Optional<List<ReporteEstadisticoAdjudicacionExcelGrupoCompraDto>> oList = Optional.ofNullable(lista);
        if (!oList.isPresent()) {
            return null;
        }

        SXSSFWorkbook book = new SXSSFWorkbook(100);
        XSSFWorkbook xbook = book.getXSSFWorkbook();
        SXSSFSheet sheet = book.createSheet();
        int numberOfSheets = book.getNumberOfSheets();
        book.setSheetName(numberOfSheets - 1, "Todos");
        int nroColumnas = ExcelDefault.createTitleAndWidth(
                xbook,
                sheet,
                CONFIG_TITLE_REPORTE_ADJUDICACION,
                "Adjudicación",
                null);

        XSSFCellStyle cellStyle01 = ExcelDefault.devuelveCellStyle(xbook, new Color(0, 0, 1), new Color(226, 239, 218), false, (short) 10);
        XSSFCellStyle cellStyle02 = ExcelDefault.devuelveCellStyle(xbook, new Color(0, 0, 192), new Color(255, 255, 255), false, (short) 10);
        List<CellStyle> cellStyleList = null;
        List<CellStyle> cellStyleList01 = ExcelDefault.generarCellStyle(xbook, cellStyle01);
        List<CellStyle> cellStyleList02 = ExcelDefault.generarCellStyle(xbook, cellStyle02);
        boolean filaImpar = true;

        for (ReporteEstadisticoAdjudicacionExcelGrupoCompraDto beanLista : lista) {
            int lastRow = sheet.getLastRowNum();
            int i = lastRow < 0 ? 0 : lastRow;
            Row dataRow = sheet.createRow(i + 1);
            int contador = 0;
            if (filaImpar) {
                cellStyleList = cellStyleList01;
            } else {
                cellStyleList = cellStyleList02;
            }
            filaImpar = !filaImpar;

            ExcelDefault.setValueCell(beanLista.getAnno(), dataRow.createCell(contador), "S", cellStyleList);
            contador++;
            ExcelDefault.setValueCell(beanLista.getRuc(), dataRow.createCell(contador), "S", cellStyleList);
            contador++;
            ExcelDefault.setValueCell(beanLista.getRazonSocial(), dataRow.createCell(contador), "S", cellStyleList);
            contador++;

            ExcelDefault.setValueCell(beanLista.getTotalParticipacion01(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;
            ExcelDefault.setValueCell(beanLista.getTotalAdjudicados01(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;

            ExcelDefault.setValueCell(beanLista.getTotalParticipacion02(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;
            ExcelDefault.setValueCell(beanLista.getTotalAdjudicados02(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;

            ExcelDefault.setValueCell(beanLista.getTotalParticipacion03(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;
            ExcelDefault.setValueCell(beanLista.getTotalAdjudicados03(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;

            ExcelDefault.setValueCell(beanLista.getTotalParticipacion04(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;
            ExcelDefault.setValueCell(beanLista.getTotalAdjudicados04(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;

            ExcelDefault.setValueCell(beanLista.getTotalParticipacion05(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;
            ExcelDefault.setValueCell(beanLista.getTotalAdjudicados05(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;

            ExcelDefault.setValueCell(beanLista.getTotalParticipacion06(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;
            ExcelDefault.setValueCell(beanLista.getTotalAdjudicados06(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;

            ExcelDefault.setValueCell(beanLista.getTotalParticipacion07(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;
            ExcelDefault.setValueCell(beanLista.getTotalAdjudicados07(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;

            ExcelDefault.setValueCell(beanLista.getTotalParticipacion08(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;
            ExcelDefault.setValueCell(beanLista.getTotalAdjudicados08(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;

            ExcelDefault.setValueCell(beanLista.getTotalParticipacion09(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;
            ExcelDefault.setValueCell(beanLista.getTotalAdjudicados09(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;

            ExcelDefault.setValueCell(beanLista.getTotalParticipacion10(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;
            ExcelDefault.setValueCell(beanLista.getTotalAdjudicados10(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;

            ExcelDefault.setValueCell(beanLista.getTotalParticipacion11(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;
            ExcelDefault.setValueCell(beanLista.getTotalAdjudicados11(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;

            ExcelDefault.setValueCell(beanLista.getTotalParticipacion12(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;
            ExcelDefault.setValueCell(beanLista.getTotalAdjudicados12(), dataRow.createCell(contador), "I", cellStyleList);
            contador++;

        }
        return book;
    }

    public List<ReporteEstadisticoAdjudicacionExcelGrupoCompraDto> reporteAdjudicacionExcelDataGrupoCompra(String ruc, String acreedor) throws Exception {
        Date fechaActual = DateUtils.obtenerFechaActual();
        Integer anno = DateUtils.getYear(fechaActual);
        ReporteEstadisticoAdjudicacionEntradaDto bean = new ReporteEstadisticoAdjudicacionEntradaDto();
        Proveedor proveedor = new Proveedor();
        if (!StringUtils.isBlank(ruc) ) {
            proveedor = this.proveedorRepository.getProveedorByRuc(ruc);
            bean.setRuc(ruc);
            bean.setAnno(anno);
        }else if(!StringUtils.isBlank(acreedor)){
            proveedor = this.proveedorRepository.getProveedorByAcreedorCodigoSap(acreedor);
            if (!Optional.ofNullable(proveedor).isPresent()) {
                throw new Exception("No se encontró Proveedor con Código Acreedor: " + acreedor);
            }
            bean.setRuc(proveedor.getRuc());
            bean.setAnno(anno);
        }
        List<ReporteEstadisticoAdjudicacionDto> dataListMapper =this.reporteEstadisticoMapper.reporteEstadisticoAdjudicacion(bean);
        List<ReporteEstadisticoAdjudicacionExcelGrupoCompraDto> resultList = new ArrayList<>();

        if (dataListMapper != null && dataListMapper.size() > 0) {
            String rucBuscar = "-1";
            int contador = 0;
            ReporteEstadisticoAdjudicacionExcelGrupoCompraDto dataResult = new ReporteEstadisticoAdjudicacionExcelGrupoCompraDto();
            for (ReporteEstadisticoAdjudicacionDto beanMapper : dataListMapper) {
                if (!rucBuscar.equals(beanMapper.getRuc())) {
                    contador = 0;
                    rucBuscar = beanMapper.getRuc();
                    dataResult = new ReporteEstadisticoAdjudicacionExcelGrupoCompraDto();
                    dataResult.setAnno(anno);
                    dataResult.setRuc(beanMapper.getRuc());
                    dataResult.setRazonSocial(proveedor.getRazonSocial());
                    dataResult.setTotalAdjudicados01(0);
                    dataResult.setTotalParticipacion01(0);
                    dataResult.setTotalAdjudicados02(0);
                    dataResult.setTotalParticipacion02(0);
                    dataResult.setTotalAdjudicados03(0);
                    dataResult.setTotalParticipacion03(0);
                    dataResult.setTotalAdjudicados04(0);
                    dataResult.setTotalParticipacion04(0);
                    dataResult.setTotalAdjudicados05(0);
                    dataResult.setTotalParticipacion05(0);
                    dataResult.setTotalAdjudicados06(0);
                    dataResult.setTotalParticipacion06(0);
                    dataResult.setTotalAdjudicados07(0);
                    dataResult.setTotalParticipacion07(0);
                    dataResult.setTotalAdjudicados08(0);
                    dataResult.setTotalParticipacion08(0);
                    dataResult.setTotalAdjudicados09(0);
                    dataResult.setTotalParticipacion09(0);
                    dataResult.setTotalAdjudicados10(0);
                    dataResult.setTotalParticipacion10(0);
                    dataResult.setTotalAdjudicados11(0);
                    dataResult.setTotalParticipacion11(0);
                    dataResult.setTotalAdjudicados12(0);
                    dataResult.setTotalParticipacion12(0);
                    resultList.add(dataResult);
                } else {
                    contador++;
                }

                switch (contador) {
                    case 0:
                        dataResult.setTotalAdjudicados01(beanMapper.getTotalAdjudicados());
                        dataResult.setTotalParticipacion01(beanMapper.getTotalParticipacion());
                        break;
                    case 1:
                        dataResult.setTotalAdjudicados02(beanMapper.getTotalAdjudicados());
                        dataResult.setTotalParticipacion02(beanMapper.getTotalParticipacion());
                        break;
                    case 2:
                        dataResult.setTotalAdjudicados03(beanMapper.getTotalAdjudicados());
                        dataResult.setTotalParticipacion03(beanMapper.getTotalParticipacion());
                        break;
                    case 3:
                        dataResult.setTotalAdjudicados04(beanMapper.getTotalAdjudicados());
                        dataResult.setTotalParticipacion04(beanMapper.getTotalParticipacion());
                        break;
                    case 4:
                        dataResult.setTotalAdjudicados05(beanMapper.getTotalAdjudicados());
                        dataResult.setTotalParticipacion05(beanMapper.getTotalParticipacion());
                        break;
                    case 5:
                        dataResult.setTotalAdjudicados06(beanMapper.getTotalAdjudicados());
                        dataResult.setTotalParticipacion06(beanMapper.getTotalParticipacion());
                        break;
                    case 6:
                        dataResult.setTotalAdjudicados07(beanMapper.getTotalAdjudicados());
                        dataResult.setTotalParticipacion07(beanMapper.getTotalParticipacion());
                        break;
                    case 7:
                        dataResult.setTotalAdjudicados08(beanMapper.getTotalAdjudicados());
                        dataResult.setTotalParticipacion08(beanMapper.getTotalParticipacion());
                        break;
                    case 8:
                        dataResult.setTotalAdjudicados09(beanMapper.getTotalAdjudicados());
                        dataResult.setTotalParticipacion09(beanMapper.getTotalParticipacion());
                        break;
                    case 9:
                        dataResult.setTotalAdjudicados10(beanMapper.getTotalAdjudicados());
                        dataResult.setTotalParticipacion10(beanMapper.getTotalParticipacion());
                        break;
                    case 10:
                        dataResult.setTotalAdjudicados11(beanMapper.getTotalAdjudicados());
                        dataResult.setTotalParticipacion11(beanMapper.getTotalParticipacion());
                        break;
                    case 11:
                        dataResult.setTotalAdjudicados12(beanMapper.getTotalAdjudicados());
                        dataResult.setTotalParticipacion12(beanMapper.getTotalParticipacion());
                        break;

                }
            }
        }
        return resultList;

    }




    // fin mizalo

}
