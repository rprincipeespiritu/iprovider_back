package com.incloud.hcp.ws.proveedor.service.builder;

import com.google.gson.Gson;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.ws.enums.MonedaCompraEnum;
import com.incloud.hcp.ws.enums.TipoDocumentoEnum;
import com.incloud.hcp.ws.enums.TipoPersonaEnum;
import com.incloud.hcp.ws.enums.TipoTelefonoEnum;
import com.incloud.hcp.ws.proveedor.dto.ProveedorGSDto;
import com.incloud.hcp.ws.util.GSConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ProveedorRegistroGSBuilder {

    private Proveedor proveedor;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static ProveedorRegistroGSBuilder newBuilder(Proveedor proveedor) {
        return new ProveedorRegistroGSBuilder(proveedor);
    }

    private ProveedorRegistroGSBuilder (Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public String build() {
        ProveedorGSDto dto = new ProveedorGSDto();
        Gson gson = new Gson();

        dto.setIdEmpresa(GSConstant.CODIGO_EMPRESA_SILVESTRE);
        dto.setId_Agenda(proveedor.getRuc());
        dto.setRazonSocial(proveedor.getRazonSocial());
        dto.setTipoPersona(proveedor.getTipoPersona().equals("N") ? TipoPersonaEnum.NATURAL.getValor() : TipoPersonaEnum.JURIDICO.getValor());
        dto.setTipoDocumento(TipoDocumentoEnum.RUC.getValor());
        dto.setNroDocumento(proveedor.getRuc());
        dto.setPrimerNombre("");
        dto.setSegundoNombre("");
        dto.setAgendaNombreCorto("");
        dto.setApellidoPaterno("");
        dto.setApellidoMaterno("");
        dto.setRuc(proveedor.getRuc());
        dto.setWebPage("");
        dto.setId_MonedaCpra(proveedor.getMoneda().getCodigoMoneda().equals("PEN") ? MonedaCompraEnum.SOLES.getValor() : MonedaCompraEnum.DOLARES.getValor());
        dto.setDiasCredito(null); //
        dto.setAgenteRetenedorIGV(0); //
        dto.setAgentePercepcion(0); //
        dto.setPrincipalContribuyente(0); //
        dto.setFechaNacimiento("");
        dto.setNombreComercial(proveedor.getRazonSocial());//
        dto.setCondicionHabido(proveedor.getIndHabidoSunat().equals("1") ? 1 : 0);
        dto.setDireccionProveedor(proveedor.getDireccionFiscal());
        dto.setDireccionNumero(null); //
        dto.setDireccionInterior(null); //

        String pais = Optional.ofNullable(proveedor.getPais())
                .map(tp -> tp.getCodigoUbigeoSap())
                .map(c -> c.trim()).get();
        String region = Optional.ofNullable(proveedor.getRegion())
                .map(tp -> Optional.ofNullable(tp.getCodigoUbigeoSap()).orElse(""))
                .map(c -> c.trim()).get();
        String provincia = Optional.ofNullable(proveedor.getProvincia())
                .map(tp -> Optional.ofNullable(tp.getCodigoUbigeoSap()).orElse(""))
                .map(c -> c.trim()).get();
        String distrito = Optional.ofNullable(proveedor.getDistrito())
                .map(tp -> Optional.ofNullable(tp.getCodigoUbigeoSap()).orElse(""))
                .map(c -> c.trim()).get();

        dto.setPaisSunat(pais);
        dto.setDepartamentoSunat(region);
        dto.setProvinciaSunat(provincia);
        dto.setDistritoSunat(distrito);
        if(proveedor.getTelefono().isEmpty()){
            dto.setTipoTelefono(TipoTelefonoEnum.CELULAR.getValor());
            dto.setNumeroTelefono(proveedor.getTelefonoMovil());
        } else {
            dto.setTipoTelefono(TipoTelefonoEnum.OFICINA.getValor());
            dto.setNumeroTelefono(proveedor.getTelefono());
        }
        dto.setEmail(proveedor.getEmail());

        return gson.toJson(dto);
    }

}
