package com.incloud.hcp.jco.banco.service.impl;

import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.myibatis.mapper.BancoMapper;
import com.incloud.hcp.myibatis.mapper.CuentaBancariaMapper;
import com.incloud.hcp.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class BancoExtactorMapper {


    public static List<Banco> getBancoList(NodeList headerList,Integer validarId ) throws Exception {
        List<Banco> bancos = new ArrayList<>();
        if(headerList != null){
            for (int i = 0; i < headerList.getLength(); i++) {
                if (headerList.item(i).getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) headerList.item(i);
                    Banco banco = new Banco();
                    banco.setIdBanco(validarId);
                    banco.setDescripcion(Utils.getValueNodo(element, "BANKA"));
                    banco.setClaveBanco(Utils.getValueNodo(element, "BANKL"));
                    banco.setEjemploFormatoCta("99999999999999");
                    banco.setExtensionCci(23);
                    banco.setExtensionCta(18);
                    banco.setExtensionCtaMin(10);
                    String nacional = Utils.getValueNodo(element, "BANKS");
                    banco.setTipoBanco(nacional == "PE" ? "N" : "E");
                    banco.setFormatoCta(banco.getTipoBanco() == "N" ? "^[0-9]{11}$" : "");

                    bancos.add(banco);

                    validarId = validarId +1;
                }
            }
        }
        return bancos;
    }


}
