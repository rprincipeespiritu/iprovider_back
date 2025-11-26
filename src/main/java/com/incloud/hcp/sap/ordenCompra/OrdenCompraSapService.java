package com.incloud.hcp.sap.ordenCompra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.incloud.hcp.domain.*;
import com.incloud.hcp.dto.OrdenCompraSapDTO;
import com.incloud.hcp.dto.PurchaseOrderSapItem;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraResponseDto;
import com.incloud.hcp.repository.CondicionPagoReposity;
import com.incloud.hcp.repository.CotizacionDetalleRepository;
import com.incloud.hcp.repository.LicitacionRepository;
import com.incloud.hcp.repository.LogTransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrdenCompraSapService {

    private static final Logger logger = LoggerFactory.getLogger(OrdenCompraSapService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();  // Usado para convertir a JSON

    @Value("https://my419028-api.s4hana.cloud.sap/sap/opu/odata4/sap/api_purchaseorder_2/srvd_a2x/sap/purchaseorder/0001")
    private String sapApiUrl;

    @Value("RMJ-IPA")
    private String username;

    @Value("DzBpqubKhGleURSvZGVqNXoPt4mRCAseYehGus}t")
    private String password;


    @Autowired
    private LicitacionRepository licitacionRepository;

    @Autowired
    private CotizacionDetalleRepository cotizacionDetalleRepository;

    @Autowired
    private LogTransaccionRepository logTransaccionRepository;

    @Autowired
    private CondicionPagoReposity condicionPagoReposity;

    public OrdenCompraSapService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OrdenCompraResponseDto crearOrdenCompra(String purchaseOrderJson) {
        OrdenCompraResponseDto respuesta = new OrdenCompraResponseDto();
        String url = sapApiUrl + "/PurchaseOrder";

        // Autenticación Basic Auth
        String credentials = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        try {
            // Validar JSON de entrada
            if (purchaseOrderJson == null || purchaseOrderJson.isEmpty()) {
                respuesta.setMessageSap("El JSON de la orden de compra está vacío o nulo.");
                return respuesta;
            }
            System.out.printf("purchaseOrderJson: "+purchaseOrderJson);

            // Convertir JSON de entrada a Map
            Map<String, Object> inputMap = objectMapper.readValue(purchaseOrderJson, Map.class);

            // Convertir el Map nuevamente a JSON
            String mappedJson = objectMapper.writeValueAsString(inputMap);

            // Obtener Token CSRF
            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.set("Authorization", "Basic " + encodedAuth);
            tokenHeaders.set("x-csrf-token", "Fetch");

            HttpEntity<Void> tokenRequest = new HttpEntity<>(tokenHeaders);
            ResponseEntity<String> tokenResponse = restTemplate.exchange(
                    url, HttpMethod.GET, tokenRequest, String.class);

            if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                respuesta.setMessageSap("Error al obtener el token CSRF: " + tokenResponse.getStatusCode());
                return respuesta;
            }

            String csrfToken = tokenResponse.getHeaders().getFirst("x-csrf-token");

            // Crear la orden de compra
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + encodedAuth);
            headers.set("x-csrf-token", csrfToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(mappedJson, headers);

            ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            String responseBody = res.getBody(); // Obtener el JSON en formato String

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});

            if (responseMap.containsKey("error")) {
                //=========================================
                LogTransaccion logTransaccion = new LogTransaccion();
                logTransaccion.setEnvioTrama(mappedJson);
                logTransaccion.setRespuestaCodigo(responseBody);
                logTransaccion.setTipoRegistro("TRAMA SAP HANA OC");
                logTransaccion.setTipoTransaccion("API_OC");
                logTransaccionRepository.save(logTransaccion);
                //=========================================

                Map<String, Object> error = (Map<String, Object>) responseMap.get("error");
                String errorCode = (String) error.get("code");
                String errorMessage = (String) error.get("message");
                respuesta.setMessageSap(errorCode +"|" + errorMessage);

            }else{
                String purchaseOrder = (String) responseMap.get("PurchaseOrder");
                respuesta.setNumeroOrdenCompra(purchaseOrder);
                respuesta.setExito(true);
            }
            return respuesta;

        } catch (HttpStatusCodeException e) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String responseBody = e.getResponseBodyAsString();
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                if (jsonNode.has("error")) {
                    String errorCode = jsonNode.get("error").get("code").asText();
                    String errorMessage = jsonNode.get("error").get("message").asText();

                    respuesta.setMessageSap("Error SAP: Código " + errorCode + " - " + errorMessage);
                } else {
                    respuesta.setMessageSap("Error HTTP en SAP: " + e.getStatusCode() + " - " + responseBody);
                }
            } catch (Exception ex) {
                respuesta.setMessageSap("Error al procesar la respuesta de error de SAP: " + ex.getMessage());
            }
            return respuesta;
        } catch (Exception e) {
            respuesta.setMessageSap("Error inesperado al procesar la solicitud: {}"+ e.getMessage());
            return respuesta;
        }
    }

    public String formatearFecha(String fechaOriginal) {
        try {
            // Definir el formato de entrada (el que recibes)
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            // Definir el formato de salida (el que SAP espera)
            SimpleDateFormat formatoSalida = new SimpleDateFormat("yyyy-MM-dd");

            // Parsear la fecha original en el formato de entrada
            Date fecha = formatoEntrada.parse(fechaOriginal);
            // Retornar la fecha formateada en el formato esperado por SAP
            return formatoSalida.format(fecha);
        } catch (Exception e) {
            // Manejar cualquier error de formato
            e.printStackTrace();
            return null;
        }
    }


    public String setearOrdenCompra(Proveedor proveedor,
                                    OrdenCompra oc, List<OrdenCompraDetalle> ordenCompraDetalles,
                                    Integer idLicitacion) throws ParseException, JsonProcessingException {

        OrdenCompraResponseDto ordenCompraResponseDto = new OrdenCompraResponseDto();
        Licitacion licitacion = licitacionRepository.findById(idLicitacion).get();
        List<CotizacionDetalle> cotizacionDetalles = cotizacionDetalleRepository.findByCotizacion(licitacion, proveedor);
        OrdenCompraSapDTO ordenCompra = this.objectSetOc(proveedor, oc, ordenCompraDetalles, cotizacionDetalles, licitacion.getNombreLicitacion());

        return objectMapper.writeValueAsString(ordenCompra);

    }

    private OrdenCompraSapDTO objectSetOc(Proveedor proveedor,
                                          OrdenCompra oc,
                                          List<OrdenCompraDetalle> ordenCompraDetalles,
                                          List<CotizacionDetalle> cotizacionDetalles,
                                          String nombreLicitacion) {

        OrdenCompraSapDTO ordenCompra = new OrdenCompraSapDTO();
        ordenCompra.setPurchaseOrderType(ordenCompraDetalles.get(0).getClaseDoc());
        ordenCompra.setSupplier(proveedor.getAcreedorCodigoSap());
        ordenCompra.setPurchasingGroup("001");
        ordenCompra.setPurchasingOrganization("P100"); //siempre envia P100
        ordenCompra.setCompanyCode("P100"); //Siempre envia P100

        // Parsear la fecha
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ordenCompra.setPurchaseOrderDate(simpleDateFormat.format(ordenCompraDetalles.get(0).getFechaInicioContrato()));

        CondicionPago condicionPago = condicionPagoReposity.getById(Integer.parseInt(ordenCompraDetalles.get(0).getCondicionPago()));
        String conpago = "";
        if (condicionPago != null) {
            conpago = condicionPago.getCodigoSap();
        }
        ordenCompra.setPaymentTerms(conpago);
        ordenCompra.setLanguage("ES");

        ordenCompra.setYy1AreaSolicitantePdh("Z001"); //PENDIENTE

        // Crear los ítems de la orden
        List<PurchaseOrderSapItem> purchaseOrderItems = new ArrayList<>();

        for(OrdenCompraDetalle ocS : ordenCompraDetalles){
            PurchaseOrderSapItem item = new PurchaseOrderSapItem();
            item.setPurchaseRequisition(String.valueOf(ocS.getOpSolicitudCompra()));
            item.setPurchaseRequisitionItem(ocS.getPosicion());
            item.setOrderQuantity(ocS.getCantidad().doubleValue());
            item.setPurchaseOrderQuantityUnit(ocS.getUnidadMedidaBienServicio());
            item.setNetPriceAmount(ocS.getPrecioUnitario().doubleValue());
            item.setDocumentCurrency(oc.getCodigoMondeda());
            item.setTaxCode(ocS.getIndicadorImpuesto());
            purchaseOrderItems.add(item);
        }

        ordenCompra.setPurchaseOrderItems(purchaseOrderItems);

        return  ordenCompra;

    }

}
