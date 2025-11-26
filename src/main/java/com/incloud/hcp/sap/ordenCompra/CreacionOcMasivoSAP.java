package com.incloud.hcp.sap.ordenCompra;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.dto.OrdenCompraMasivoDto;
import com.incloud.hcp.dto.PurchaseOrderSapItem;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CreacionOcMasivoSAP {

    @Value("${USERNAME_SAP}")
    private String USERNAME;

    @Value("${PASSWORD_SAP}")
    private String PASSWORD;
    @Value("${API_URL_SAP}")
    private String SAP_API_URL;

    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;// Declara el campo


    public CreacionOcMasivoSAP(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient();
    }

    private static final String urlPrueba = "https://my417543-api.s4hana.cloud.sap";
    private static final String PURCHASE_ORDER_ITEM_API_PATH = "/sap/opu/odata4/sap/api_purchaseorder_2/srvd_a2x/sap/purchaseorder/0001/PurchaseOrderItem";

    private Map<String, String> sendPurchaseOrderToSap(String jsonBody, String identificadorOc) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            // Construir la URL completa de la API
            String url_sap = SAP_API_URL + "/sap/opu/odata4/sap/api_purchaseorder_2/srvd_a2x/sap/purchaseorder/0001/PurchaseOrder";

            // Validar que las credenciales no sean nulas
            if (USERNAME == null || PASSWORD == null) {
                System.err.println("Error: USERNAME_SAP o PASSWORD_SAP no están configurados.");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error de configuración de credenciales SAP.");
                return resultMap;
            }

            String authString = USERNAME + ":" + PASSWORD;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            // 1. Obtener x-csrf-token y cookies
            Request getTokenRequest = new Request.Builder()
                    .url(url_sap)
                    .method("GET", null)
                    .addHeader("x-csrf-token", "fetch")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .build();

            Response getTokenResponse = client.newCall(getTokenRequest).execute();

            if (!getTokenResponse.isSuccessful()) {
                String errorBody = getTokenResponse.body() != null ? getTokenResponse.body().string() : "No body";
                System.err.println("Error al obtener x-csrf-token para OC " + identificadorOc + ". Código de respuesta: " + getTokenResponse.code() + ", Cuerpo: " + errorBody);
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al obtener x-csrf-token para " + identificadorOc + ". Código: " + getTokenResponse.code());
                return resultMap;
            }

            String csrfToken = getTokenResponse.header("x-csrf-token");
            if (csrfToken == null || csrfToken.isEmpty()) {
                System.err.println("No se pudo obtener el token CSRF para OC " + identificadorOc + ".");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudo obtener el token CSRF para " + identificadorOc);
                return resultMap;
            }

            String cookies = getTokenResponse.header("Set-Cookie");
            if (cookies == null || cookies.isEmpty()) {
                System.err.println("No se pudieron obtener las cookies para OC " + identificadorOc + ".");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudieron obtener las cookies para " + identificadorOc);
                return resultMap;
            }

            // 2. Preparar el cuerpo JSON para el POST
            okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
            // Aquí es donde se le pasa el JSON real
            okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(mediaType, jsonBody);

            // 3. Construir y ejecutar la solicitud POST
            Request postRequest = new Request.Builder()
                    .url(url_sap)
                    .addHeader("x-csrf-token", csrfToken)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .addHeader("Cookie", cookies) // Reutilizar las cookies
                    .post(requestBody) // Usar el RequestBody con el JSON
                    .build();

            Response postResponse = client.newCall(postRequest).execute();
            String responseBody = postResponse.body() != null ? postResponse.body().string() : "No body";

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});

            if (postResponse.isSuccessful()) {
                System.out.println("Orden " + identificadorOc + " creada correctamente. Código: " + postResponse.code() + ", Respuesta: " + responseBody);
                resultMap.put("status", "1");
                resultMap.put("msg", "Orden " + identificadorOc + " creada correctamente. Código: " + postResponse.code());
                String purchaseOrder = (String) responseMap.get("PurchaseOrder");
                resultMap.put("responseBody", purchaseOrder);

            } else {
                System.err.println("Error al crear Orden " + identificadorOc + ". Código: " + postResponse.code() + ", Respuesta: " + responseBody);
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al crear Orden " + identificadorOc + ". Código: " + postResponse.code());
                Map<String, Object> error = (Map<String, Object>) responseMap.get("error");
                String errorCode = (String) error.get("code");
                String errorMessage = (String) error.get("message");
                resultMap.put("responseBody", errorCode +"|" + errorMessage);
            }

        } catch (IOException e) { // IOException para problemas de red, etc.
            System.err.println("Error de red/IO al integrar con SAP para OC " + identificadorOc + ": " + e.getMessage());
            resultMap.put("status", "0");
            resultMap.put("msg", "Error de red/IO: " + e.getMessage());
        } catch (Exception e) { // Capturar otras excepciones
            System.err.println("Excepción inesperada al integrar con SAP para OC " + identificadorOc + ": " + e.getMessage());
            e.printStackTrace();
            resultMap.put("status", "0");
            resultMap.put("msg", "Excepción: " + e.getMessage());
        }
        return resultMap;
    }

    private Map<String, String> updatePurchaseOrderItemInSap(String purchaseOrderNumber, String purchaseOrderItem, String jsonBody, String identificadorOc) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("status", "0"); // Por defecto, falla
        resultMap.put("msg", "Error desconocido al actualizar ítem OC.");

        try {
            // URL para el PATCH de un ítem específico
            String url_sap = SAP_API_URL + "/sap/opu/odata4/sap/api_purchaseorder_2/srvd_a2x/sap/purchaseorder/0001/PurchaseOrder";
            String url_sap_item = SAP_API_URL + PURCHASE_ORDER_ITEM_API_PATH +
                    "/" + purchaseOrderNumber + "/" + purchaseOrderItem;

            Objects.requireNonNull(USERNAME, "USERNAME_SAP no está configurado.");
            Objects.requireNonNull(PASSWORD, "PASSWORD_SAP no está configurado.");

            String authString = USERNAME + ":" + PASSWORD;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());
            String authHeader = "Basic " + encodedAuth;

            // 1. Obtener x-csrf-token y cookies (para el PATCH, aunque a veces no es estrictamente necesario para PATCH/PUT)
            // Es buena práctica obtenerlo siempre.
            Request getTokenRequest = new Request.Builder()
                    .url(url_sap) // Usar la URL del ítem para el token
                    .method("GET", null)
                    .addHeader("x-csrf-token", "fetch")
                    .addHeader("Authorization", authHeader)
                    .build();

            Response getTokenResponse = httpClient.newCall(getTokenRequest).execute();

            if (!getTokenResponse.isSuccessful()) {
                String errorBody = getTokenResponse.body() != null ? getTokenResponse.body().string() : "No body";
                System.err.println("Error al obtener x-csrf-token para actualizar ítem " + purchaseOrderItem + " de OC " + purchaseOrderNumber + ". Código: " + getTokenResponse.code() + ", Cuerpo: " + errorBody);
                resultMap.put("msg", "Error al obtener x-csrf-token para actualizar ítem " + purchaseOrderItem + ". Código: " + getTokenResponse.code());
                return resultMap;
            }

            String csrfToken = getTokenResponse.header("x-csrf-token");
            if (csrfToken == null || csrfToken.isEmpty()) {
                System.err.println("No se pudo obtener el token CSRF para actualizar ítem " + purchaseOrderItem + " de OC " + purchaseOrderNumber + ".");
                resultMap.put("msg", "No se pudo obtener el token CSRF para actualizar ítem " + purchaseOrderItem);
                return resultMap;
            }

            String cookies = getTokenResponse.header("Set-Cookie");

            okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(mediaType, jsonBody);

            // 3. Construir y ejecutar la solicitud PATCH
            Request patchRequest = new Request.Builder()
                    .url(url_sap_item)
                    .addHeader("x-csrf-token", csrfToken)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", authHeader)
                    .addHeader("If-Match", "*")
                    .header("Cookie", cookies != null ? cookies : "")
                    .patch(requestBody) // Método PATCH
                    .build();

            Response patchResponse = httpClient.newCall(patchRequest).execute();
            String responseBody = patchResponse.body() != null ? patchResponse.body().string() : "No body";

            if (patchResponse.isSuccessful()) {
                System.out.println("Ítem " + purchaseOrderItem + " de OC " + purchaseOrderNumber + " actualizado correctamente. Código: " + patchResponse.code());
                resultMap.put("status", "1");
                resultMap.put("msg", "Ítem " + purchaseOrderItem + " de OC " + purchaseOrderNumber + " actualizado correctamente.");
            } else {
                JsonNode errorNode = objectMapper.readTree(responseBody);
                String errorCode = errorNode.path("error").path("code").asText();
                String errorMessage = errorNode.path("error").path("message").path("value").asText();
                System.err.println("Error al actualizar ítem " + purchaseOrderItem + " de OC " + purchaseOrderNumber + ". Código: " + patchResponse.code() + ", Error SAP: " + errorCode + " - " + errorMessage);
                resultMap.put("msg", "Error al actualizar ítem " + purchaseOrderItem + " de OC " + purchaseOrderNumber + ". Código: " + patchResponse.code() + ". Error SAP: " + errorCode + " - " + errorMessage);
                resultMap.put("sapErrorCode", errorCode);
                resultMap.put("sapErrorMessage", errorMessage);
            }

        } catch (IOException e) {
            System.err.println("Error de red/IO al actualizar ítem " + purchaseOrderItem + " de OC " + purchaseOrderNumber + ": " + e.getMessage());
            resultMap.put("msg", "Error de red/IO: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Excepción inesperada al actualizar ítem " + purchaseOrderItem + " de OC " + purchaseOrderNumber + ": " + e.getMessage());
            e.printStackTrace();
            resultMap.put("msg", "Excepción inesperada: " + e.getMessage());
        }
        return resultMap;
    }



    public Map<String, Map<String, String>> processAndSendAllOrdersToSap(List<OrdenCompraMasivoDto> allOrdersFromExcel) throws Exception {
        // 1. Agrupar la lista de DTOs por el identificadorOc
        Map<String, List<OrdenCompraMasivoDto>> groupedOrders = allOrdersFromExcel.stream()
                .collect(Collectors.groupingBy(
                        OrdenCompraMasivoDto::getIdentificadorOc,
                        // Usar un TreeMap para asegurar que las claves (identificadorOc) estén ordenadas
                        // Si el formato es "OC" seguido de números, usar un comparador personalizado para orden numérico
                        () -> new TreeMap<>( (oc1, oc2) -> {
                            try {
                                // Extraer el número de la cadena "OCX"
                                int num1 = Integer.parseInt(oc1.replace("OC", ""));
                                int num2 = Integer.parseInt(oc2.replace("OC", ""));
                                return Integer.compare(num1, num2);
                            } catch (NumberFormatException e) {
                                // Si el formato no es el esperado (ej. "OC-ABC"), fallback a orden alfabético
                                return oc1.compareTo(oc2);
                            }
                        }),
                        Collectors.toList()
                ));


        Map<String, Map<String, String>> allSapResponses = new TreeMap<>( (oc1, oc2) -> {
            try {
                int num1 = Integer.parseInt(oc1.replace("OC", ""));
                int num2 = Integer.parseInt(oc2.replace("OC", ""));
                return Integer.compare(num1, num2);
            } catch (NumberFormatException e) {
                return oc1.compareTo(oc2);
            }
        });
        ObjectMapper mapper = objectMapper;

        // 2. Iterar por cada grupo (cada OC única), generar su JSON y enviarla a SAP
        for (Map.Entry<String, List<OrdenCompraMasivoDto>> entry : groupedOrders.entrySet()) {
            String identificadorOc = entry.getKey();
            List<OrdenCompraMasivoDto> itemsForThisOrder = entry.getValue();

            if (itemsForThisOrder.isEmpty()) {
                System.err.println("Advertencia: No hay ítems para la orden " + identificadorOc);
                continue;
            }

            // Tomar los datos de cabecera de la primera posición (asumiendo consistencia)
            OrdenCompraMasivoDto firstItem = itemsForThisOrder.get(0);

            ObjectNode rootNode = mapper.createObjectNode();

            // Asignar campos de cabecera del JSON de SAP
            rootNode.put("PurchaseOrderType", firstItem.getClaseDocCompras());
            rootNode.put("Supplier", firstItem.getAcreedorCodigoSap());
            rootNode.put("DocumentCurrency", firstItem.getMoneda());
            rootNode.put("PurchasingGroup", firstItem.getGrupoCompras());
            rootNode.put("PurchasingOrganization", firstItem.getOrganizacionCompras());
            //rootNode.put("PaymentTerms", firstItem.getCondicionEntrega());
            rootNode.put("CompanyCode", firstItem.getSociedad());
            rootNode.put("InvoicingParty", firstItem.getAcreedorCodigoSap());
            rootNode.put("CorrespncInternalReference", firstItem.getCodigoComprador());
            rootNode.put("Language", "ES");
            rootNode.put("YY1_AreaSolicitante_PDH", firstItem.getAreaSolicitante());

            // _PurchaseOrderNote
            ArrayNode noteArray = rootNode.putArray("_PurchaseOrderNote");
            ObjectNode note = noteArray.addObject();
            note.put("TextObjectType", "F01");
            note.put("PlainLongText", firstItem.getCodigoComprador());

            // _PurchaseOrderItem
            ArrayNode itemsArray = rootNode.putArray("_PurchaseOrderItem");
            int ScheduleLine = 0;
            for (OrdenCompraMasivoDto dto : itemsForThisOrder) {
                ObjectNode itemNode = itemsArray.addObject();
                ScheduleLine++;
                itemNode.put("PurchaseRequisition", dto.getNroSolped());
                itemNode.put("PurchaseRequisitionItem", dto.getPosicionSolped());
                itemNode.put("OrderQuantity", dto.getCantidad());
                itemNode.put("Plant", dto.getCentro());
                // Este campo no está en tu DTO. Si SAP lo requiere, debes obtenerlo o hardcodearlo.
                itemNode.put("PurchaseOrderQuantityUnit", dto.getUnidadMedida());
                itemNode.put("NetPriceAmount", dto.getCostoUnitario());
                itemNode.put("DocumentCurrency", dto.getMoneda());
                //itemNode.put("TaxCode", dto.getIndicadorImpuesto());
                if(dto.getFechaEntrega() != "") {
                    ArrayNode fechaEntregaArray = itemNode.putArray("_PurchaseOrderScheduleLineTP");
                    ObjectNode fechaEntrega = fechaEntregaArray.addObject();
                    fechaEntrega.put("ScheduleLine", String.valueOf(ScheduleLine));
                    fechaEntrega.put("ScheduleLineDeliveryDate", dto.getFechaEntrega());
                }
            }


            // Convertir el ObjectNode a String JSON
            String jsonToSap = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

            // 3. Enviar el JSON generado a SAP
            Map<String, String> sapResponseForThisOrder = sendPurchaseOrderToSap(jsonToSap, identificadorOc);
            String status = sapResponseForThisOrder.get("status");
            String purcharseOrderGeneradaSap = sapResponseForThisOrder.get("responseBody");
            if ("1".equals(status) && purcharseOrderGeneradaSap != null && !purcharseOrderGeneradaSap.isEmpty()){
                System.out.println("Iniciando Actualización de items para la OC: " + purcharseOrderGeneradaSap);

                List<String> itemUpdateErrors = new ArrayList<>();
                int posicionOc = 0;
                for (OrdenCompraMasivoDto dto : itemsForThisOrder) {
                    // Construir el JSON para el PATCH del ítem
                    posicionOc+=10;
                    ObjectNode itemPatchNode = objectMapper.createObjectNode();
                    itemPatchNode.put("NetPriceAmount", dto.getCostoUnitario());
                    itemPatchNode.put("DocumentCurrency", dto.getMoneda());
                   // itemPatchNode.put("PurchaseOrderQuantityUnit","EA");

                    String itemPatchJson = objectMapper.writeValueAsString(itemPatchNode);
                    String posicionOCString = String.valueOf(posicionOc);
                    // Llamar al método de actualización del ítem
                    Map<String, String> updateResult = updatePurchaseOrderItemInSap(
                            purcharseOrderGeneradaSap,
                            posicionOCString,
                            itemPatchJson,
                            identificadorOc
                    );

                    if ("0".equals(updateResult.get("status"))) {
                        System.err.println("Fallo al actualizar ítem " + posicionOCString + " de OC " + purcharseOrderGeneradaSap + ": " + updateResult.get("msg"));
                        itemUpdateErrors.add("Item " + posicionOCString + ": ERROR - " + updateResult.get("msg"));
                    }
                }
                if (!itemUpdateErrors.isEmpty()) {
                    sapResponseForThisOrder.put("itemUpdateErrors", String.join("; ", itemUpdateErrors));
                }
            }else {
                System.err.println("La creación de la OC " + identificadorOc + " falló o no se obtuvo el número de OC. No se intentará la actualización de ítems.");
            }


            allSapResponses.put(identificadorOc, sapResponseForThisOrder);
        }

        return allSapResponses;
    }
}
