package com.incloud.hcp.rest;

import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.service.cmiscf.CmisBaseService;
import com.incloud.hcp.service.cmiscf.bean.CmisBean;
import com.incloud.hcp.service.cmiscf.bean.CmisFolder;
import com.incloud.hcp.service.cmiscf.bean.CmisFolderSession;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/cmis-resource")
public class CmisResource {

    @Autowired
    private CmisBaseService cmisBaseServicecf;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/document")
    public ResponseEntity<Resource> getDocument(@RequestParam(value = "archivoId") String archivoID,
                                                @RequestParam(value = "nameFolder") String nameFolder) throws IOException {

        CmisObject objectByPath = cmisBaseServicecf.getDocumentByFolderAndId(nameFolder, archivoID);

        if (objectByPath instanceof Document) {
            ContentStream contentStream = ((Document) objectByPath).getContentStream();
            String fileName = contentStream.getFileName();
            String mimeType = contentStream.getMimeType();
            InputStream stream = contentStream.getStream();

            byte[] bytes = FileCopyUtils.copyToByteArray(stream);
            ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);

            long length = bytes.length;

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Cache-Control", "no-cache, no-store, must-revalidate");
            httpHeaders.add("Pragma", "no-cache");
            httpHeaders.add("Expires", "0");
            httpHeaders.setContentLength(length);
            httpHeaders.setContentDispositionFormData("attachment", fileName);

            logger.info("File metadata. fileName: {}, length: {}, mimeType: {}", fileName, length, mimeType);

            return ResponseEntity.ok()
                    .headers(httpHeaders)
                    .contentLength(length)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(byteArrayResource);
        }

        throw new PortalException("No se ha encontrado archivo...");
    }

    @GetMapping(path = "/cmis")
    public ResponseEntity<List<CmisBean>> getAll(@RequestParam(value = "nameFolder") String nameFolder) {
        CmisObject objectByPath = cmisBaseServicecf.getFolder(nameFolder);
        List<CmisBean> docs = new ArrayList<>();

        if (objectByPath instanceof Folder) {
            ItemIterable<CmisObject> children = ((Folder) objectByPath).getChildren();
            for (CmisObject cmisObject : children) {
                docs.add(new CmisBean(cmisObject.getName(), cmisObject.getId()));
            }
        }
        if (objectByPath instanceof Document) {
            docs.add(new CmisBean(objectByPath.getName(), objectByPath.getId()));
        }
        return new ResponseEntity<>(docs, HttpStatus.OK);
    }

}
