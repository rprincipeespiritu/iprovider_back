package com.incloud.hcp.rest;

import com.incloud.hcp.service.InformacionNoticiaService;
import com.incloud.hcp.service.TipoInformacionNoticiaService;
import com.incloud.hcp.service.cmiscf.CmisBaseService;
import com.incloud.hcp.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.Normalizer;

/**
 * Created by MARCELO on 10/10/2017.
 */
@RestController
@RequestMapping(value = "/api/InfoNoticiaRepositorio")
public class CmisInformacionNoticiaAdjuntoRest extends GenericRest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InformacionNoticiaService informacionNoticiaService;

    @Autowired
    private CmisBaseService cmisService;

    @Autowired
    TipoInformacionNoticiaService tipoInformacionNoticiaService;

//    @RequestMapping(value = "/noticia/{idInformacionNoticia}/adjuntos",
//            method = RequestMethod.POST, produces = {
//            MediaType.APPLICATION_JSON_VALUE,
//            MediaType.APPLICATION_XML_VALUE})
//    public ResponseEntity<?> addArchivoAdjuntoNoticia(@PathVariable("idInformacionNoticia") Integer idInformacionNoticia,
//                                                      @RequestParam("file") MultipartFile file){
//        try{
//            logger.debug("Se agregara un archivo");
//            Optional<InformacionNoticia> oInfo = Optional.ofNullable(informacionNoticiaService.getInformacionNoticiaById(idInformacionNoticia));
//
//            if (oInfo.isPresent()){
//                logger.debug("Existe Informacion");
//                InformacionNoticia info = oInfo.get();
//                TipoInformacionNoticia tipo = info.getTipoInformacionNoticia();
//
//                String folderId = Optional.ofNullable(tipo.getCarpetaId())
//                        .orElseGet(() -> {
//                            String folder = reemplazarAcentos(tipo.getDescripcion());
//                            logger.debug("Se creara un folder: " + folder);
//                            String fi = cmisService.createFolder(folder);
//                            tipo.setCarpetaId(fi);
//                            logger.debug("Se guardara el ID de la carpeta: " + fi);
//                            tipoInformacionNoticiaService.actualizarCarpetaIdTipoInformacionById(tipo.getIdTipoInformacionNoticia(), fi);
//                            logger.debug("se actualizo en la informacion del proveedor");
//                            return fi;
//                        });
//                logger.debug("Se creara el documento en el repositorio");
//                CmisFile cmisFile = cmisService.createDocumento(folderId, file);
//                logger.debug("Documento Creado: " + cmisFile.getName());
//                info.setRutaAdjunto(cmisFile.getUrl());
//                info.setArchivoNombre(cmisFile.getName());
//                info.setArchivoId(cmisFile.getId());
//                informacionNoticiaService.update(info);
//                logger.debug("Informacion noticia actualizado");
//                return new ResponseEntity<Void>(HttpStatus.OK);
//            } else {
//                logger.debug("No existe Informacion de Proveedor");
//                return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
//            }
//        } catch (Exception e) {
//            Map map = new HashMap<>();
//            map.put("response", e.getMessage());
//            return new ResponseEntity<Map>(map,HttpStatus.NOT_FOUND);
//        }
//    }

    private String reemplazarAcentos(String textoConAcentos) {
        String textoSinAcentos;
        String cadenaNormalize = Normalizer.normalize(textoConAcentos, Normalizer.Form.NFD);
        textoSinAcentos = cadenaNormalize.replaceAll(Constant.PATRON_ACENTOS, "").toLowerCase();
        textoSinAcentos = textoSinAcentos.replaceAll("\\s*", "");
        return textoSinAcentos;
    }
}
