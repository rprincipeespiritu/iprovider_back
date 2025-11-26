package com.incloud.hcp.service.cmiscf;

import com.incloud.hcp.domain.*;
import com.incloud.hcp.dto.*;
import com.incloud.hcp.service.cmiscf.bean.CmisFile;
import com.incloud.hcp.service.cmiscf.bean.CmisFolder;
import com.incloud.hcp.service.cmiscf.models.FileMultipart;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by Administrador on 26/09/2017.
 */
public interface CmisBaseService {

    String getToken() throws Exception;

    List<Repository> getListaRepositoryToken() throws Exception;

    CmisFolder createFolder(String nameFolder) throws Exception;

    CmisFolder SubCreateFolder(String folderMain,String newFloder) throws Exception;

    CmisFile createDocumento(String nameFolder, MultipartFile file) throws Exception;

    CmisFile createDocumento(String nameFolder, MultipartFile file,String folder,String fileName) throws Exception;

    CmisFile createDocumento(String nameFolder, MultipartFile file, String fileName) throws Exception;

    ByteArrayOutputStream getDocumento(String archivoId) throws Exception;

    FileMultipart getDocumentMultipartByIdAdjunto(String archivoId, String extensionFile, String nameFile) throws Exception;

    CmisFile createDocumentoFromBytes(String nameFolder, String extension, String fileNewName, String contentType, ByteArrayOutputStream file) throws Exception;

    List<CmisFile> updateFileAndMoveVerificar(List<CmisFile> files, String folderId);

    void deleteFiles(List<String> archivoIds, String nameFolder);

    void updateFileLastVersionTrue(List<String> filesId, String nameFolder);

    CmisObject getDocumentByFolderAndId(String nameFolder, String archivoId);

    CmisObject getFolder(String nameFolder);

    Object crearDocumentoSunat(AdjuntoCargaDto adjuntoCargaDto, Proveedor p) throws Exception;
    Object crearDocumentoCuentaBancaria(Integer id,AdjuntoCargaDto adjuntoCargaDto, Proveedor p) throws Exception;
    Object crearDocumentoCatalogo(AdjuntoCargaDto adjuntoCargaDto, Proveedor p) throws Exception;

    Object getObtenerArchivoExterno(String archivoId, String fileExtencion) throws Exception;

    Object crearDocumentoNoticia(AdjuntoNoticiaDto adjuntoNoticiaDto, InformacionNoticia n) throws Exception;

    String getAutenticacionRs() throws Exception;

    Object crearDocumentoHomologacion(AdjuntoHomologacionDto adjuntoHomologacionDto, ProveedorHomologacion proveedorHomologacion) throws Exception;

    Object crearDocumentoLicitacion(LicitacionAdjuntoBase64Dto item, Licitacion licitacionGen)throws Exception;

    Object getDocumentoLicitacion(LicitacionAdjuntoBase64Dto item, Licitacion licitacionGen) throws Exception;

    Object getDocumentoCotizacion(CotizacionAdjuntoBase64Dto item, Cotizacion cotizacionResult, Licitacion licitacion) throws Exception;

    Object getDocumentoLicitacionRespuesta(LicitacionAdjuntoBase64Dto item, Licitacion licitacion)  throws Exception;

    Object crearDocumentoActaSustento(AdjuntoActaSustentoBase64Dto sustentoBase64Dto, ActaSustento actaSustento) throws Exception;

    Object crearDocumentoFichaActaSustento(AdjuntoActaSustentoBase64Dto sustentoBase64Dto, ActaSustento actaSustento) throws Exception;

    String crearDocumentoPrefactura(AdjuntoPreFacturaDto base64Dto, Prefactura prefactura) throws Exception;

    Object crearFichaProveedor(ProveedorInDto beanCentro)throws Exception;

    CmisFile createDocumentoOc(String nameFolder, MultipartFile file) throws Exception;
}
