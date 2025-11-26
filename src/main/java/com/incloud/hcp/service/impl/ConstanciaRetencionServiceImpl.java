package com.incloud.hcp.service.impl;

import com.incloud.hcp.domain.ConstanciaDetraccionDetalle;
import com.incloud.hcp.domain.ConstanciaRetencion;
import com.incloud.hcp.domain.ConstanciaRetencionDetalle;

import com.incloud.hcp.dto.ConstanciaRetencionDto;
import com.incloud.hcp.dto.ConstanciaRetencionProveedorDto;
import com.incloud.hcp.myibatis.mapper.ConstanciaRetencionMaper;
import com.incloud.hcp.myibatis.mapper.ParametroMapper;
import com.incloud.hcp.repository.ConstanciaRetencionRepository;
import com.incloud.hcp.service.ConstanciaRetencionService;
import com.incloud.hcp.service.notificacion.EnvioCorreoConstanciaRetencion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ConstanciaRetencionServiceImpl implements ConstanciaRetencionService {

    @Autowired
    private ConstanciaRetencionRepository constanciaRetencionRepository;
    @Autowired
    private ConstanciaRetencionMaper constanciaRetencionMaper;
    @Autowired
    private EnvioCorreoConstanciaRetencion envioCorreoConstanciaRetencion;
    @Autowired
    private ParametroMapper parametroMapper;

    @Override
    public String saveConstanciaRetenciones(MultipartFile file) {
        try{
            String fileName = file.getOriginalFilename();

            if (fileName == null || !fileName.toLowerCase().endsWith(".xml")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo debe ser un archivo de texto válido con extensión .xml");
            }

            InputStream inputStream = file.getInputStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document documento = builder.parse(inputStream);

            documento.getDocumentElement().normalize();
            ConstanciaRetencion constanciaRetencion = new ConstanciaRetencion();
            List<ConstanciaRetencionDetalle> detalles = new ArrayList<>();


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String nroRetencion = "";
            String fechaDocumento = "";
            String nombreEmisor = "";
            String rucEmisor = "";
            String nombreProveedor = "";
            String rucProveedor = "";
            String codigoRetencion = "";
            String porcentajeRetencion = "";
            String totalRetencion = "";
            String valorTotal = "";

            NodeList retencionCab = documento.getElementsByTagName("Retention");
            NodeList emisor = documento.getElementsByTagName("cac:AgentParty");
            NodeList receptor = documento.getElementsByTagName("cac:ReceiverParty");
            NodeList listaDetalles = documento.getElementsByTagName("sac:SUNATRetentionDocumentReference");
            //mapeo
            for (int i = 0; i < retencionCab.getLength(); i++) {
                Node nodo = retencionCab.item(i);
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;

                    // Leer los elementos específicos
                    nroRetencion = elemento.getElementsByTagName("cbc:ID").item(2).getTextContent();
                    fechaDocumento = elemento.getElementsByTagName("cbc:IssueDate").item(0).getTextContent();
                    codigoRetencion = elemento.getElementsByTagName("sac:SUNATRetentionSystemCode").item(0).getTextContent();
                    porcentajeRetencion = elemento.getElementsByTagName("sac:SUNATRetentionSystemCode").item(0).getTextContent();
                    totalRetencion = elemento.getElementsByTagName("cbc:TotalInvoiceAmount").item(0).getTextContent();
                    valorTotal = elemento.getElementsByTagName("sac:SUNATTotalPaid").item(0).getTextContent();
                }

            }

            for (int i = 0; i < emisor.getLength(); i++) {
                Node nodo = emisor.item(i);

                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;

                    // Leer los elementos específicos
                    rucEmisor = elemento.getElementsByTagName("cac:PartyIdentification").item(0).getTextContent().trim();
                    nombreEmisor = elemento.getElementsByTagName("cac:PartyName").item(0).getTextContent().trim();

                }
            }


            for (int i = 0; i < receptor.getLength(); i++) {
                Node nodo = receptor.item(i);

                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;

                    // Leer los elementos específicos
                    rucProveedor = elemento.getElementsByTagName("cac:PartyIdentification").item(0).getTextContent().trim();
                    nombreProveedor = elemento.getElementsByTagName("cac:PartyName").item(0).getTextContent().trim();

                }
            }

            //set valores
            constanciaRetencion.setNroRetencion(nroRetencion);
            constanciaRetencion.setFechaDoc(LocalDate.parse(fechaDocumento,formatter));
            constanciaRetencion.setNombreEmisor(nombreEmisor);
            constanciaRetencion.setRucEmisor(rucEmisor);
            constanciaRetencion.setNombreProveedor(nombreProveedor);
            constanciaRetencion.setRucProveedor(rucProveedor);
            constanciaRetencion.setCodigoRetencionSistema(codigoRetencion);
            constanciaRetencion.setPorcentajeRetencion(Double.valueOf(porcentajeRetencion));
            constanciaRetencion.setTotalPagoRetencion(new BigDecimal(totalRetencion));
            constanciaRetencion.setMontoTotal(new BigDecimal(valorTotal));

            if(constanciaRetencionRepository.existsByNroRetencion(constanciaRetencion.getNroRetencion()) && constanciaRetencion.getNroRetencion() != null){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Este archivo ya ha sido procesado");
            }

            //detalles
            for (int i = 0; i < listaDetalles.getLength(); i++) {
                Node nodo = listaDetalles.item(i);
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;
                    ConstanciaRetencionDetalle detalleRetencion = new ConstanciaRetencionDetalle();
                    String nroFactura = elemento.getElementsByTagName("cbc:ID").item(0).getTextContent();
                    detalleRetencion.setNroFactura(nroFactura);
                    String totalFactura = elemento.getElementsByTagName("cbc:TotalInvoiceAmount").item(0).getTextContent();
                    detalleRetencion.setTotalFactura(new BigDecimal(totalFactura));
                    String montoPagado = elemento.getElementsByTagName("cbc:PaidAmount").item(0).getTextContent();
                    detalleRetencion.setMontoPagado(new BigDecimal(montoPagado));
                    String retencion = elemento.getElementsByTagName("sac:SUNATRetentionAmount").item(0).getTextContent();
                    detalleRetencion.setRetencion(Double.valueOf(retencion));
                    String netoPagado = elemento.getElementsByTagName("sac:SUNATNetTotalPaid").item(0).getTextContent();
                    detalleRetencion.setNetoPagado(new BigDecimal(netoPagado));
                    String monedaRecibida = elemento.getElementsByTagName("cbc:SourceCurrencyCode").item(0).getTextContent();
                    detalleRetencion.setMonedaRecibida(monedaRecibida);
                    String monedaConvertida = elemento.getElementsByTagName("cbc:TargetCurrencyCode").item(0).getTextContent();
                    detalleRetencion.setMonedaConvertida(monedaConvertida);
                    String tasaCambio = elemento.getElementsByTagName("cbc:CalculationRate").item(0).getTextContent();
                    detalleRetencion.setTasaCambio(Double.valueOf(tasaCambio));

                    detalleRetencion.setDatosCabecera(constanciaRetencion);

                    detalles.add(detalleRetencion);

                }


            }
            constanciaRetencion.setDetallesCostancia(detalles);
            constanciaRetencionRepository.save(constanciaRetencion);

            return "Los datos se han almacenado correctamente";


        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }

    }

    @Transactional
    @Override
    public String enviarCorreoConstanciaRetencion() {
        String respuesta = "";

        List<ConstanciaRetencionProveedorDto> dataConstanciaProveedores = constanciaRetencionMaper.obtenerListaProveedores();

        for (ConstanciaRetencionProveedorDto dataProveedor: dataConstanciaProveedores){
            respuesta = this.envioCorreoConstanciaRetencion.enviar(parametroMapper.getMailSetting(), dataProveedor);

            if("correo EnvioCorreoConstanciaRetencion enviado".equals(respuesta)){
                Optional<ConstanciaRetencion> constanciaRetencion = this.constanciaRetencionRepository.findById(dataProveedor.getIdConstanciaRetencion());
                if (constanciaRetencion.isPresent()){
                    ConstanciaRetencion constanciaRetencion1 =  constanciaRetencion.get();
                    constanciaRetencion1.setEnviaCorreo(true);
                    constanciaRetencionRepository.save(constanciaRetencion1);
                }
            }

        }

        return respuesta;
    }

    @Override
    public List<ConstanciaRetencionDto> obtenerListaRetenciones(String nombreProveedor, String rucProveedor, String fechaInicio, String fechaFin, String nroRetencion) {
        try{
            LocalDate  inicioFecha = this.covertirFecha(fechaInicio);
            LocalDate finFecha = this.covertirFecha(fechaFin);
            return constanciaRetencionMaper.obtenerDatosRetenciones(nombreProveedor, rucProveedor, inicioFecha, finFecha, nroRetencion);
        }catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha incorrecto, por favor validar");
        }
    }

    private LocalDate covertirFecha(String fecha){
        if (fecha == null || fecha.isEmpty()) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return  LocalDate.parse(fecha, formatter);
    }
}
