package com.incloud.hcp.service.cmiscf.impl;

import com.google.gson.Gson;
import com.incloud.hcp.domain.*;
import com.incloud.hcp.dto.*;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.exception.ServiceException;
import com.incloud.hcp.myibatis.mapper.ParametroMapper;
import com.incloud.hcp.repository.*;
import com.incloud.hcp.service.cmiscf.CmisBaseService;
import com.incloud.hcp.service.cmiscf.bean.CmisFile;
import com.incloud.hcp.service.cmiscf.bean.CmisFolder;
import com.incloud.hcp.service.cmiscf.bean.CmisFolderSession;
import com.incloud.hcp.service.cmiscf.bean.CmisToken;
import com.incloud.hcp.service.cmiscf.models.FileMultipart;
import com.incloud.hcp.util.DateUtils;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrador on 26/09/2017.
 */
@Profile("devlocal")
@Component("cmisServiceCF")
public class CmisDevLocalBaseServiceImpl implements CmisBaseService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());



    @Autowired
    private ParametroMapper parametroMapper;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());;

    private HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }

    private SimpleClientHttpRequestFactory getClientHttpRequestFactory()
    {
        SimpleClientHttpRequestFactory clientHttpRequestFactory
                = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactory.setConnectTimeout(600000);

        //Read timeout
        clientHttpRequestFactory.setReadTimeout(600000);
        return clientHttpRequestFactory;
    }

    @Autowired
    private ProveedorAdjuntoSunatRepository proveedorAdjuntoSunatRepository;

    @Autowired
    private ProveedorCatalogoRepository proveedorCatalogoRepository;

    @Autowired
    private MtrTipoDocumentoRepository mtrTipoDocumentoRepository;

    @Autowired
    private InformacionNoticiaRepository informacionNoticiaRepository;

    @Autowired
    private ProveedorHomologacionRepository proveedorHomologacionRepository;

    @Autowired
    private LicitacionAdjuntoRepository licitacionAdjuntoRepository;

    @Autowired
    private CotizacionAdjuntoRepository cotizacionAdjuntoRepository;

    @Autowired
    private LicitacionAdjuntoRespuestaRepository licitacionAdjuntoRespuestaRepository;

    @Autowired
    private AdjuntoActaSustentoRepository adjuntoActaSustentoRepository;

    @Autowired
    private LogTransaccionRepository logTransaccionRepository;


    public String getToken() throws Exception {
        return null;

    }

    public List<Repository> getListaRepositoryToken() throws Exception {
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();

        List<Repository> repositories = null;

        return repositories;
    }


    private Session getSessionRepositoryToken() throws Exception {
        List<Repository> repositories = this.getListaRepositoryToken();
        Repository repository = repositories.get(0);
        Session session = repository.createSession();
        return session;
    }

    public CmisFolder createFolder(String nameFolder) throws Exception {
        Session session = this.getSessionRepositoryToken();
        Folder root = session.getRootFolder();
        ItemIterable<CmisObject> children = root.getChildren();
        CmisFolder cmisFolder = new CmisFolder();
        for (CmisObject cmisObject : children) {
            if (cmisObject.getName().equals(nameFolder)) {
                cmisFolder.setId(cmisObject.getId());
                cmisFolder.setMensaje("La carpeta " + nameFolder + " existe!");
                return cmisFolder;
            }
        }
        Map<String, String> newFolderProps = new HashMap<>();
        newFolderProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        newFolderProps.put(PropertyIds.NAME, nameFolder);

        Folder newFolder = root.createFolder(newFolderProps);
        cmisFolder.setId(newFolder.getId());
        cmisFolder.setMensaje("Se ha creado un folder en " + newFolder.getPath());
        return cmisFolder;

    }

    public CmisFolder SubCreateFolder(String mainfolderId,String nameFolder) throws Exception {
        Session session = this.getSessionRepositoryToken();
        Folder root = session.getRootFolder();
        ItemIterable<CmisObject> children = root.getChildren();
        CmisFolder cmisFolder = new CmisFolder();

        Map<String, String> newFolderProps = new HashMap<>();
        newFolderProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        newFolderProps.put(PropertyIds.NAME, nameFolder);
        newFolderProps.put(PropertyIds.OBJECT_ID,mainfolderId);

        Folder newFolder = root.createFolder(newFolderProps);
        cmisFolder.setId(newFolder.getId());
        cmisFolder.setMensaje("Se ha creado un folder en " + newFolder.getPath());
        return cmisFolder;

    }

    private CmisFolderSession obtenerFolderSession(String nameFolder) throws Exception {
        Session session = this.getSessionRepositoryToken();
        Folder root = session.getRootFolder();
        ItemIterable<CmisObject> children = root.getChildren();
        CmisFolder cmisFolder = new CmisFolder();
        CmisFolderSession cmisFolderSession = new CmisFolderSession();
        cmisFolderSession.setSession(session);
        for (CmisObject cmisObject : children) {
            if (cmisObject.getName().equals(nameFolder)) {
                cmisFolder.setId(cmisObject.getId());
                cmisFolder.setMensaje("La carpeta " + nameFolder + " existe!");
                cmisFolderSession.setCmisFolder(cmisFolder);
                return cmisFolderSession;
            }
        }
        Map<String, String> newFolderProps = new HashMap<>();
        newFolderProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        newFolderProps.put(PropertyIds.NAME, nameFolder);

        Folder newFolder = root.createFolder(newFolderProps);
        cmisFolder.setId(newFolder.getId());
        cmisFolder.setMensaje("Se ha creado un folder en " + newFolder.getPath());
        cmisFolderSession.setCmisFolder(cmisFolder);
        return cmisFolderSession;

    }
    public Object crearDocumentoCuentaBancaria(AdjuntoCargaDto adjuntoCargaDto, Proveedor p) throws Exception {
        Proveedor proveedor = proveedorRepository.getOne(p.getIdProveedor());
        LogTransaccion logTransaccion = new LogTransaccion();
        //obtener tipo de documento
        try {
            MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento(adjuntoCargaDto.getCodigoDocumento());
        }catch (Exception e){
            throw new Exception("Error no existe codigo :"+adjuntoCargaDto.getCodigoDocumento());
        }
        //Enviar data con base64
        String accessToken = getAutenticacionRs();
        Object dataResponse = getCrearDocumento(accessToken,proveedor,adjuntoCargaDto);
        System.out.println("");
        return  dataResponse;

    }
    public Object crearDocumentoCuentaBancaria(Integer id,AdjuntoCargaDto adjuntoCargaDto, Proveedor p) throws Exception {
        Proveedor proveedor = proveedorRepository.getOne(p.getIdProveedor());
        LogTransaccion logTransaccion = new LogTransaccion();
        //obtener tipo de documento
        try {
            MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento(adjuntoCargaDto.getCodigoDocumento());
        }catch (Exception e){
            throw new Exception("Error no existe codigo :"+adjuntoCargaDto.getCodigoDocumento());
        }
        //Enviar data con base64
        String accessToken = getAutenticacionRs();
        System.out.println("");
        return  "dataResponse";

    }

    public CmisFile createDocumento(String nameFolder, MultipartFile file, String fileName) throws Exception {

        return null;
    }

    public CmisFile createDocumento(String nameFolder, MultipartFile file) throws Exception {
        CmisFolderSession cmisFolderSession = this.obtenerFolderSession(nameFolder);
        Session session = cmisFolderSession.getSession();
        CmisFolder cmisFolder = cmisFolderSession.getCmisFolder();

        String extension = Optional.ofNullable(file.getOriginalFilename())
                .map(s -> s.split("\\."))
                .filter(s -> s.length > 0)
                .map(s -> "." + s[s.length - 1])
                .orElse("");
        log.error("createDocumento 00 extension: " + extension);
        String fileNewName = file.getOriginalFilename().replace(extension, "") ;
        log.error("createDocumento 01 fileNewName: " + fileNewName);

        //***********************************************
        String original = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝßàáâãäåæçèéêëìíîïðñòóôõöøùúûüýÿ";
        String ascii = "AAAAAAACEEEEIIIIDNOOOOOOUUUUYBaaaaaaaceeeeiiiionoooooouuuuyy";

        //String nombreFinal =fileNewName;
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            fileNewName = fileNewName.replace(original.charAt(i), ascii.charAt(i));
        }
        log.error("createDocumento 02 fileNewName: " + fileNewName);
        fileNewName = fileNewName.replaceAll("[^a-zA-Z0-9]", "");
        log.error("createDocumento 03 fileNewName: " + fileNewName);
        log.error("createDocumento 04: " + extension + " fileName: " + fileNewName);
        fileNewName = fileNewName + extension;
        log.error("createDocumento 05 fileNewName: " + fileNewName);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, fileNewName);
        Folder folder = (Folder) session.getObject(cmisFolder.getId());

        InputStream stream = new ByteArrayInputStream(file.getBytes());
        ContentStream contentStream = session.getObjectFactory()
                .createContentStream(fileNewName, file.getSize(), file.getContentType(), stream);
        Document doc = null;
        try {
            doc = folder.createDocument(properties, contentStream, VersioningState.MAJOR);
        }
        catch (Exception ex) {
            ItemIterable<CmisObject> children = folder.getChildren();
            for (CmisObject cmisObject : children) {
                if (cmisObject.getName().equals(fileNewName)) {
                    doc = (Document) session.getObject(cmisObject.getId());
                    doc.delete(true);
                    doc = folder.createDocument(properties, contentStream, VersioningState.MAJOR);
                    break;
                }
            }
        }

        CmisFile cmisFile = new CmisFile();
        cmisFile.setId(doc.getId());
        cmisFile.setName(file.getOriginalFilename());
        cmisFile.setNameFinal(fileNewName);
        cmisFile.setExtension(extension);
        cmisFile.setCarpetaId(folder.getId());
        cmisFile.setNombreFolder(nameFolder);
        cmisFile.setType(file.getContentType());
        cmisFile.setSize(file.getSize());
        StringBuilder path = new StringBuilder("/");
        path.append(session.getRootFolder().getId());
        path.append("/root/");
        path.append(folder.getName());
        path.append("/");
        path.append(fileNewName);
        cmisFile.setUrl(path.toString());

        return cmisFile;
    }

    public CmisFile createDocumento(String nameFolder, MultipartFile file,String folderName,String fileName) throws Exception {
        CmisFolderSession cmisFolderSession = this.obtenerFolderSession(nameFolder);
        Session session = cmisFolderSession.getSession();
        CmisFolder cmisFolder = cmisFolderSession.getCmisFolder();
        FilePart partFile = null;

        String extension = Optional.ofNullable(file.getOriginalFilename())
                .map(s -> s.split("\\."))
                .filter(s -> s.length > 0)
                .map(s -> "." + s[s.length - 1])
                .orElse("");
        log.error("createDocumento 00 extension: " + extension);
        String fileNewName = file.getOriginalFilename().replace(extension, "");
        log.error("createDocumento 01 fileNewName: " + fileNewName);

        //***********************************************
        String original = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝßàáâãäåæçèéêëìíîïðñòóôõöøùúûüýÿ";
        String ascii = "AAAAAAACEEEEIIIIDNOOOOOOUUUUYBaaaaaaaceeeeiiiionoooooouuuuyy";

        //String nombreFinal =fileNewName;
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            fileNewName = fileNewName.replace(original.charAt(i), ascii.charAt(i));
        }

        //EAAR fecha
        String fechaAutogenerada = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        fileNewName = fileNewName + "_" + fechaAutogenerada;
        String fileNameDb = fileNewName;
        String extensionDb = extension.replace(".", "");
        //
        log.error("createDocumento 02 fileNewName: " + fileNewName);
        fileNewName = fileNewName.replaceAll("[^a-zA-Z0-9]", "");
        log.error("createDocumento 03 fileNewName: " + fileNewName);
        log.error("createDocumento 04: " + extension + " fileName: " + fileNewName);
        fileNewName = fileNewName + extension;
        log.error("createDocumento 05 fileNewName: " + fileNewName);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, fileNewName);
        Folder folder = (Folder) session.getObject(cmisFolder.getId());

        InputStream stream = new ByteArrayInputStream(file.getBytes());
        ContentStream contentStream = session.getObjectFactory()
                .createContentStream(fileNewName, file.getSize(), file.getContentType(), stream);
        Document doc = null;
        try {
            doc = folder.createDocument(properties, contentStream, VersioningState.MAJOR);
        } catch (Exception ex) {
            ItemIterable<CmisObject> children = folder.getChildren();
            for (CmisObject cmisObject : children) {
                if (cmisObject.getName().equals(fileNewName)) {
                    doc = (Document) session.getObject(cmisObject.getId());
                    doc.delete(true);
                    doc = folder.createDocument(properties, contentStream, VersioningState.MAJOR);
                    break;
                }
            }
        }

        CmisFile cmisFile = new CmisFile();
        cmisFile.setId(doc.getId());
        cmisFile.setName(fileNameDb);
        cmisFile.setNameFinal(fileNewName);
        cmisFile.setExtension(extensionDb);
        cmisFile.setCarpetaId(folder.getId());
        cmisFile.setNombreFolder(nameFolder);
        //cmisFile.setType(file.getContentType());
        cmisFile.setType(extensionDb);
        cmisFile.setSize(file.getSize());
        StringBuilder path = new StringBuilder("/");
        path.append(session.getRootFolder().getId());
        path.append("/root/");
        path.append(folderName);
        path.append("/");
        path.append(folder.getName());
        path.append("/");
        path.append(fileNewName);
        cmisFile.setUrl(path.toString());

        return cmisFile;
    }


    public FileMultipart getDocumentMultipartByIdAdjunto(String archivoId, String extensionFile, String nameFile) throws Exception {
        FileMultipart fileMultipart = new FileMultipart();

        //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Session session = this.getSessionRepositoryToken();
        Document doc = (Document) session.getObject(archivoId);
        ContentStream contentStream = doc.getContentStream();
        if (contentStream != null) {
            InputStream inputStream = contentStream.getStream();
            String file = contentStream.getFileName();
            Long tam = contentStream.getLength();
            String mime = contentStream.getMimeType();
            byte[] bytes = IOUtils.toByteArray(inputStream);

            fileMultipart.setNombreDocumento(file.substring(0,file.lastIndexOf(".")));
            fileMultipart.setExtension(file.substring(file.lastIndexOf("."))+1);
            fileMultipart.setTipo(mime);
            fileMultipart.setBytes(bytes);

            //String content = getContentAsString(contentStream);
            //outputStream.write(bytes);

        }

        return fileMultipart;
    }
    public ByteArrayOutputStream getDocumento(String archivoId) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Session session = this.getSessionRepositoryToken();
        Document doc = (Document) session.getObject(archivoId);
        ContentStream contentStream = doc.getContentStream();
        if (contentStream != null) {
            InputStream inputStream = contentStream.getStream();
            String fila = contentStream.getFileName();
            Long tam = contentStream.getLength();
            String mime = contentStream.getMimeType();
            byte[] bytes = IOUtils.toByteArray(inputStream);

            //String content = getContentAsString(contentStream);
            outputStream.write(bytes);

        }
        return outputStream;

    }

    public CmisFile createDocumentoFromBytes(String nameFolder, String extension, String fileNewName, String contentType, ByteArrayOutputStream file) throws Exception {
        CmisFolderSession cmisFolderSession = this.obtenerFolderSession(nameFolder);
        Session session = cmisFolderSession.getSession();
        CmisFolder cmisFolder = cmisFolderSession.getCmisFolder();

        //***********************************************
        String original = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝßàáâãäåæçèéêëìíîïðñòóôõöøùúûüýÿ";
        String ascii = "AAAAAAACEEEEIIIIDNOOOOOOUUUUYBaaaaaaaceeeeiiiionoooooouuuuyy";

        //String nombreFinal =fileNewName;
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            fileNewName = fileNewName.replace(original.charAt(i), ascii.charAt(i));
        }
        log.error("createDocumento 02 fileNewName: " + fileNewName);
        fileNewName = fileNewName.replaceAll("[^a-zA-Z0-9]", "");
        log.error("createDocumento 03 fileNewName: " + fileNewName);
        log.error("createDocumento 04: " + extension + " fileName: " + fileNewName);
        fileNewName = fileNewName + extension;
        log.error("createDocumento 05 fileNewName: " + fileNewName);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, fileNewName);
        Folder folder = (Folder) session.getObject(cmisFolder.getId());

        InputStream stream = new ByteArrayInputStream(file.toByteArray());
        ContentStream contentStream = session.getObjectFactory()
                .createContentStream(fileNewName, file.size(), contentType, stream);
        Document doc = null;
        try {
            doc = folder.createDocument(properties, contentStream, VersioningState.MAJOR);
        }
        catch (Exception ex) {
            ItemIterable<CmisObject> children = folder.getChildren();
            for (CmisObject cmisObject : children) {
                if (cmisObject.getName().equals(fileNewName)) {
                    doc = (Document) session.getObject(cmisObject.getId());
                    doc.delete(true);
                    doc = folder.createDocument(properties, contentStream, VersioningState.MAJOR);
                    break;
                }
            }
        }

        CmisFile cmisFile = new CmisFile();
        cmisFile.setId(doc.getId());
        cmisFile.setName(fileNewName);
        cmisFile.setNameFinal(fileNewName);
        cmisFile.setExtension(extension);
        cmisFile.setCarpetaId(folder.getId());
        cmisFile.setNombreFolder(nameFolder);
        cmisFile.setType(contentType);
        cmisFile.setSize(Integer.valueOf(file.size()).longValue());
        StringBuilder path = new StringBuilder("/");
        path.append(session.getRootFolder().getId());
        path.append("/root/");
        path.append(folder.getName());
        path.append("/");
        path.append(fileNewName);
        cmisFile.setUrl(path.toString());

        return cmisFile;
    }

    @Override
    public List<CmisFile> updateFileAndMoveVerificar(List<CmisFile> files, String nameFolder) {
        try {
            log.error("Actualización de version del archivo");
            CmisFolderSession cmisFolderSession = this.obtenerFolderSession(nameFolder);
            Session session = cmisFolderSession.getSession();
            CmisFolder cmisFolder = cmisFolderSession.getCmisFolder();

            //InitialContext ctx = new InitialContext();
            //EcmService ecmSvc = (EcmService) ctx.lookup(LOOKUP_NAME);

            //Session openCmisSession = ecmSvc.connect(repositoryName, repositoryKey);

            List<CmisFile> newFiles = new ArrayList<>();
            Optional.ofNullable(files)
                    .ifPresent(l -> l.stream()
                            .peek(item -> log.error("Actualizando el archivo id " + item.getId()))
                            .filter(item -> session.exists(item.getId())) //openCmisSession
                            .forEach(item -> {
                                FileableCmisObject object = (FileableCmisObject) session.getObject(item.getId());
                                CmisObject sourceFolderId = object.getParents().get(0);
                                CmisObject targetFolderId = session.getObject(cmisFolder.getId());//folderId
                                Folder folder = (Folder) session.getObject(cmisFolder.getId());//folderId

                                if (!sourceFolderId.getId().equals(targetFolderId.getId())) {

                                    CmisObject fileMove = object.move(sourceFolderId, targetFolderId);

                                    StringBuilder path = new StringBuilder("/");
                                    path.append(session.getRootFolder().getId());
                                    path.append("/root/");
                                    path.append(folder.getName());
                                    path.append("/");
                                    path.append(fileMove.getName());
                                    item.setUrl(path.toString());

                                    newFiles.add(item);
                                    log.error("ARCHIVO MOVIDO !!!: ");
                                    Map<String, Object> properties = new HashMap<>();
                                    properties.put(PropertyIds.DESCRIPTION, "REGISTRADO");
                                    fileMove.updateProperties(properties, true);
                                }
                            }));
            return newFiles;
        } catch (Exception ex) {
            log.error("Error al actualizar la version de los archivos", ex);
            throw new ServiceException("Error al actualizar la version de los archivos");
        }
    }

    @Override
    public void deleteFiles(List<String> archivoIds, String nameFolder) {
        try {
            log.debug("Actualización de version del archivo");
            /*InitialContext ctx = new InitialContext();
            EcmService ecmSvc = (EcmService) ctx.lookup(LOOKUP_NAME);

            Session openCmisSession = ecmSvc.connect(repositoryName, repositoryKey);*/

            CmisFolderSession cmisFolderSession = this.obtenerFolderSession(nameFolder);
            Session session = cmisFolderSession.getSession();
            CmisFolder cmisFolder = cmisFolderSession.getCmisFolder();
            Optional.ofNullable(archivoIds)
                    .ifPresent(l -> l.stream()
                            .peek(id -> log.debug("Actualizando el archivo id " + id))
                            .filter(session::exists)
                            .forEach(id -> {
                                Document doc = (Document) session.getObject(id);
                                doc.delete();
                            }));

        } catch (Exception ex) {
            log.error("Error al elilminar los archivos", ex);
            throw new PortalException("Error al elilminar los archivos");
        }
    }



    @Override
    public void updateFileLastVersionTrue(List<String> filesId, String nameFolder) {
        /*if (isDev) {
            return;
        }*/
        try {
            log.debug("Actualización de version del archivo");
            /*InitialContext ctx = new InitialContext();
            EcmService ecmSvc = (EcmService) ctx.lookup(LOOKUP_NAME);

            Session openCmisSession = ecmSvc.connect(repositoryName, repositoryKey);*/
            CmisFolderSession cmisFolderSession = this.obtenerFolderSession(nameFolder);
            Session session = cmisFolderSession.getSession();
            CmisFolder cmisFolder = cmisFolderSession.getCmisFolder();
            Optional.ofNullable(filesId)
                    .ifPresent(l -> l.stream()
                            .peek(id -> log.debug("Actualizando el archivo id " + id))
                            .filter(session::exists)
                            .forEach(id -> {
                                Document doc = (Document) session.getObject(id);
                                Map<String, Object> properties = new HashMap<>();
                                properties.put(PropertyIds.DESCRIPTION, "REGISTRADO");
                                doc.updateProperties(properties, true);
                            }));

        } catch (Exception ex) {
            log.error("Error al actualizar la version de los archivos", ex);
            throw new PortalException("Error al actualizar la vserion de los archivos");
        }
    }

    private static String getContentAsString(ContentStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        //Reader reader = new InputStreamReader(stream.getStream(), "UTF-8");
        Reader reader = new InputStreamReader(stream.getStream());
        try {
            final char[] buffer = new char[4 * 1024];
            int b;
            while (true) {
                b = reader.read(buffer, 0, buffer.length);
                if (b > 0) {
                    sb.append(buffer, 0, b);
                } else if (b == -1) {
                    break;
                }
            }
        } finally {
            reader.close();
        }

        return sb.toString();
    }

    @Override
    public CmisObject getDocumentByFolderAndId(String nameFolder, String archivoId) {
        CmisObject objectByPath = null;
        try {
            CmisFolderSession cmisFolderSession = this.obtenerFolderSession(nameFolder);
            Session session = cmisFolderSession.getSession();

            objectByPath = session.getObject(archivoId);

        } catch (Exception ex) {
            log.error("Error al obtener el documento", ex);
            throw new PortalException("Error al obtener el documento");
        }

        return objectByPath;
    }

    @Override
    public CmisObject getFolder(String nameFolder) {
        CmisObject objectByPath = null;
        try {
            CmisFolderSession cmisFolderSession = this.obtenerFolderSession(nameFolder);
            Session session = cmisFolderSession.getSession();
            CmisFolder cmisFolder = cmisFolderSession.getCmisFolder();

            objectByPath = session.getObject(cmisFolder.getId());

        } catch (Exception ex) {
            log.error("Error al obtener Folder", ex);
            throw new PortalException("Error al obtener Folder");
        }

        return objectByPath;
    }


    public Object crearDocumentoSunat(AdjuntoCargaDto adjuntoCargaDto, Proveedor p) throws Exception {


        LogTransaccion logTransaccion = new LogTransaccion();
        //obtener tipo de documento
        try {
            MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento(adjuntoCargaDto.getCodigoDocumento());
            log.error("crearDocumentoSunat 01: "+mtrTipoDocumento);
        }catch (Exception e){
            throw new Exception("Error no existe codigo :"+adjuntoCargaDto.getCodigoDocumento());
        }
        List<ProveedorAdjuntoSunat> proveedorAdjuntoSunats =
                this.proveedorAdjuntoSunatRepository
                        .getbyIdProveedorAndMtrTipoDocumento(
                                adjuntoCargaDto.getIdProveedor(),
                                adjuntoCargaDto.getCodigoDocumento());

        //FICHA DEL PROVEEDOR
        List<ProveedorAdjuntoSunat> proveedorFicha =
                this.proveedorAdjuntoSunatRepository
                        .getbyIdProveedorAndMtrTipoDocumento(
                                adjuntoCargaDto.getIdProveedor(),
                                "FDP");
        log.error("crearDocumentoSunat 02");
        //crear la ficha si no existe
        if (proveedorFicha.isEmpty()){

            //datos del archivo
            String base64 = adjuntoCargaDto.getBase64File();
            String fileExt = adjuntoCargaDto.getFileExtencion();
            String codigoT = adjuntoCargaDto.getCodigoDocumento();
            String nombreD = adjuntoCargaDto.getNombreDocumento();
            log.error("crearDocumentoSunat 03");
            adjuntoCargaDto.setBase64File("");
            adjuntoCargaDto.setFileExtencion("");
            adjuntoCargaDto.setCodigoDocumento("FDP");//Ficha de Datos del Proveedor
            adjuntoCargaDto.setNombreDocumento("Ficha de Datos del Proveedor");
            String accessToken = getAutenticacionRs();
            Object dataResponse = getCrearDocumento(accessToken,p,adjuntoCargaDto);
            log.error("crearDocumentoSunat 04: "+dataResponse);
            //si la data existe crea el documento Sunat
            if(!dataResponse.equals(null)){
                //Enviar data con base64
                String accessToken2 = getAutenticacionRs();
                //asignar valores del archivo
                adjuntoCargaDto.setBase64File(base64);
                adjuntoCargaDto.setFileExtencion(fileExt);
                adjuntoCargaDto.setCodigoDocumento(codigoT);
                adjuntoCargaDto.setNombreDocumento(nombreD);
                Object dataResponse2 = getCrearDocumento(accessToken2,p,adjuntoCargaDto);
                log.error("crearDocumentoSunat 05: "+dataResponse2);
                System.out.println("");
                return  dataResponse2;
            }

            return  dataResponse;
        }
        System.out.println("");

        //crear documento
        if(proveedorAdjuntoSunats.isEmpty()){
            //Enviar data con base64
            String accessToken = getAutenticacionRs();
            Object dataResponse = getCrearDocumento(accessToken,p,adjuntoCargaDto);
            System.out.println("");
            log.error("crearDocumentoSunat 06: "+dataResponse);
            return  dataResponse;

        }else{
            //Enviar data con base64
            String accessToken = getAutenticacionRs();
            Object dataResponse = getCrearDocumento(accessToken,p,adjuntoCargaDto);
            System.out.println("");
            log.error("crearDocumentoSunat 07: "+dataResponse);
            return  dataResponse;
//            ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
//            responseCrearDocumentoDto.setMensaje("Ya existe el adjunto para el proveedor, código Documento :"+ adjuntoCargaDto.getCodigoDocumento());
//            responseCrearDocumentoDto.setStatus("-1");
//            return  responseCrearDocumentoDto;
        }
    }

    public Object getDocumentoLicitacion(LicitacionAdjuntoBase64Dto item, Licitacion licitacionGen) throws Exception {
        System.out.println("");
        //obtener tipo de documento
        try {
            MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento(item.getCodigoDocumento());
        }catch (Exception e){
            throw new Exception("Error no existe codigo :"+item.getCodigoDocumento());
        }


        MtrTipoDocumento mtrTipoDocumentoItem = mtrTipoDocumentoRepository.findByCodigoTipoDocumento(item.getCodigoDocumento());

        List<LicitacionAdjunto> licitacionAdjuntos =
                this.licitacionAdjuntoRepository
                        .getbyIdLicitacionAndMtrTipoDocumento
                                (licitacionGen.getIdLicitacion(),mtrTipoDocumentoItem.getId());


        //FICHA DEL Licitacion
        MtrTipoDocumento mtrTipoDocumentoFicha = mtrTipoDocumentoRepository.findByCodigoTipoDocumento("FDCS");
        List<LicitacionAdjunto> fichaLicitacion =
                this.licitacionAdjuntoRepository
                        .getbyIdLicitacionAndMtrTipoDocumento
                                (licitacionGen.getIdLicitacion(),mtrTipoDocumentoFicha.getId());
        //crear la ficha si no existe
        if (fichaLicitacion.isEmpty()){

            //datos del archivo
            String base64 = item.getBase64File();
            String fileExt = item.getFileExtencion();
            String codigoT = item.getCodigoDocumento();
            String nombreD = item.getNombreDocumento();

            item.setBase64File("");
            item.setFileExtencion("");
            item.setCodigoDocumento("FDCS");
            item.setNombreDocumento("Ficha de Concurso -Licitación");
            Object dataResponse = null;

            //enviar a crear si se cae la ficha de datos
            boolean es = false;
            for (int i = 0; i < 3; i++) {
                try{
                    dataResponse = crearDocumentoLicitacion(item,licitacionGen);
                    es = true;
                }catch (Exception e){
                    Thread.sleep(500);
                    e.printStackTrace();
                    System.out.println("continuar ejecucion....");
                    continue;
                }
                if(es){
                    break;
                }
            }

            //si la data existe crea el documento Licitacion
            if(!dataResponse.equals(null)){
                //Enviar data con base64
                String accessToken2 = getAutenticacionRs();
                //asignar valores del archivo
                item.setBase64File(base64);
                item.setFileExtencion(fileExt);
                item.setCodigoDocumento(codigoT);
                item.setNombreDocumento(nombreD);
                Object dataResponse2 = crearDocumentoLicitacion(item,licitacionGen);
                System.out.println("");
                return  dataResponse2;
            }

            return  dataResponse;
        }
        System.out.println("");

        //crear documento
        if(licitacionAdjuntos.isEmpty()){
            //Enviar data con base64
            Object dataResponse = crearDocumentoLicitacion(item,licitacionGen);
            System.out.println("");
            return  dataResponse;

        }else{
            //Enviar data con base64
            Object dataResponse = crearDocumentoLicitacion(item,licitacionGen);
            System.out.println("");
            return  dataResponse;
        }
    }

    public Object getDocumentoCotizacion(CotizacionAdjuntoBase64Dto item, Cotizacion cotizacionResult, Licitacion licitacion) throws Exception {
        System.out.println("");
        //obtener tipo de documento
        try {
            MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento(item.getMtrTipoDocumento());
        }catch (Exception e){
            throw new Exception("Error no existe codigo :"+item.getMtrTipoDocumento());
        }

        //FICHA DEL Licitacion
        //        List<LicitacionAdjunto> fichaLicitacion =
        //                this.licitacionAdjuntoRepository
        //                        .getbyIdLicitacionAndMtrTipoDocumento
        //                                (licitacionGen.getIdLicitacion(),"FDCS");
        //crear la ficha si no existe
        //Enviar data con base64
        Object dataResponse = crearDocumentoCotizacion(item,cotizacionResult,licitacion);
        System.out.println("");
        return  dataResponse;
    }

    public Object crearDocumentoCatalogo(AdjuntoCargaDto adjuntoCargaDto, Proveedor p) throws Exception {
        LogTransaccion logTransaccion = new LogTransaccion();
        //obtener tipo de documento
        try {
            MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento(adjuntoCargaDto.getCodigoDocumento());
        }catch (Exception e){
            throw new Exception("Error no existe codigo :"+adjuntoCargaDto.getCodigoDocumento());
        }
        //Enviar data con base64
        String accessToken = getAutenticacionRs();
        Object dataResponse = getCrearDocumentoCatalogo(accessToken,p,adjuntoCargaDto);
        System.out.println("");
        return  dataResponse;

    }

    public Object crearDocumentoFichaActaSustento(AdjuntoActaSustentoBase64Dto sustentoBase64Dto, ActaSustento actaSustento) throws Exception {

        //obtener tipo de documento
        MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento("FESH");
        LogTransaccion logTransaccion = new LogTransaccion();
        if(!mtrTipoDocumento.isIdSet()){
            ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
            responseCrearDocumentoDto.setMensaje("No existe el código :"+ sustentoBase64Dto.getCodigoDocumento());
            responseCrearDocumentoDto.setStatus("-1");
            return  responseCrearDocumentoDto;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_XML.toString());
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.setBearerAuth(this.getAutenticacionRs());

        //Obtenemos el URL
        String urlCrearDocumento = parametroMapper.getAutentificacion("CrearDocumentoURL");
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        Date fechaActual = DateUtils.obtenerFechaHoraActual();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        //xml
        String xmlString = "<Formulario xmlns:i='http://www.w3.org/2001/XMLSchema-instance' xmlns='http://schemas.datacontract.org/2004/07/webAuthentic.Models'>";
        xmlString+="<Acta_Sustentos>"+actaSustento.getNumeroActaSustento()+"</Acta_Sustentos>";
        xmlString+="<Codigo_Aceptacion>"+actaSustento.getCodigoSapEm() != null ? actaSustento.getCodigoSapEm() : actaSustento.getCodigoSapHes() != null ? actaSustento.getCodigoSapHes() : actaSustento.getNumeroActaSustento() +"</Codigo_Aceptacion>";
        xmlString+="<Codigo_Concurso>"+actaSustento.getNumeroActaSustento()+"</Codigo_Concurso>";
        xmlString+="<Contacto_Comercial></Contacto_Comercial>";
        xmlString+="<Correo_Electronico></Correo_Electronico>";
        xmlString+="<Creador></Creador>";
        xmlString+="<Direccion></Direccion>";


        //Enviar Base64
        if(!sustentoBase64Dto.getBase64File().isEmpty()){
            xmlString+="<Docbase64>"+sustentoBase64Dto.getBase64File()+"</Docbase64>";
        }

        xmlString+="<Fecha_Creacion>"+dateFormat.format(fechaActual) +"</Fecha_Creacion>";
        xmlString+="<Fecha_Finalizacion></Fecha_Finalizacion>";
        xmlString+="<Fecha_Inicio></Fecha_Inicio>";
        xmlString+="<FileExtencion>"+sustentoBase64Dto.getFileExtencion().toLowerCase()+"</FileExtencion>";
        xmlString+="<Nombre_Concurso></Nombre_Concurso>";
        xmlString+="<Nombre_Documento>"+mtrTipoDocumento.getDescripcion()+"</Nombre_Documento>";
        xmlString+="<Pais></Pais>";
        xmlString+="<Pedido>"+actaSustento.getOrdenCompra().getNumeroOrdenCompra()+"</Pedido>";
        xmlString+="<Provincia></Provincia>";
        xmlString+="<RUC>000</RUC>";
        xmlString+="<Razon_Social></Razon_Social>";
        xmlString+="<Region></Region>";
        xmlString+="<Representante_Legal></Representante_Legal>";
        xmlString+="<Telefono_Fijo></Telefono_Fijo>";
        xmlString+="<Telefono_Movil></Telefono_Movil>";
        xmlString+="<Tipo_Persona></Tipo_Persona>";
        xmlString+="<Tipo_Proveedor></Tipo_Proveedor>";

        xmlString += "</Formulario>";
        log.info(xmlString);

        HttpEntity<String> request = new HttpEntity<String>(xmlString, headers);
        ResponseEntity<Object> respuesta;

        try{
            respuesta = restTemplate.exchange
                    (urlCrearDocumento, HttpMethod.POST,  request, Object.class);
        }catch (Exception ex){
            logTransaccion.setEnvioTrama(xmlString);
            logTransaccion.setRespuestaCodigo(ex.getMessage());
            logTransaccion.setTipoRegistro("Acta sustento");
            logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
            this.logTransaccionRepository.save(logTransaccion);
            throw new Exception("Error de conexion del servicio  http://200.4.228.156:8083/api/crear_documento :"+ex.getMessage() );
        }
        //obtener getbody
        ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
        logTransaccion.setEnvioTrama(xmlString);
        logTransaccion.setRespuestaCodigo(respuesta.getBody().toString());
        logTransaccion.setTipoRegistro("Acta sustento");
        logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
        this.logTransaccionRepository.save(logTransaccion);
        if(respuesta.getBody().equals("Error: No existe tipo documental")){
            responseCrearDocumentoDto.setStatus("-1");
            responseCrearDocumentoDto.setMensaje("Registro Fallido "+respuesta.getBody().toString());
            return responseCrearDocumentoDto;
        }
//        Object respuestaBody = respuesta.getBody();
//        Gson g = new Gson();
//        responseCrearDocumentoDto =  g.fromJson(respuestaBody.toString(), ResponseCrearDocumentoDto.class);

        String documentoOnBase = String.valueOf(respuesta.getBody());
        documentoOnBase = documentoOnBase.replace("{","");
        documentoOnBase = documentoOnBase.replace("IDDocumento:","");
        documentoOnBase = documentoOnBase.replace("}","");
        responseCrearDocumentoDto.setIDDocumento(Integer.valueOf(documentoOnBase.trim()));
        //Registrar el la tabla Adjunto Acta sustento
        if(!responseCrearDocumentoDto.getIDDocumento().equals("") || !responseCrearDocumentoDto.getIDDocumento().equals(null)  ){

            AdjuntoActaSustento n = new AdjuntoActaSustento();
            n.setArchivo(responseCrearDocumentoDto.getIDDocumento().toString());
            n.setArchivoNombre(mtrTipoDocumento.getDescripcion());
            n.setArchivoTipo(mtrTipoDocumento.getCodigoTipoDocumento());
            n.setActaSustento(actaSustento);
            n.setTipoDocumento(mtrTipoDocumento);
            this.adjuntoActaSustentoRepository.save(n);
        }

        responseCrearDocumentoDto.setStatus("1");
        responseCrearDocumentoDto.setMensaje("Registro Exitoso!");
        return responseCrearDocumentoDto;
    }




    @Override
    public Object getObtenerArchivoExterno(String archivoId, String fileExtencion) throws Exception {

        String accessToken = getAutenticacionRs();
        Object dataResponse = getObtenerDocumento(accessToken,archivoId);
        LogTransaccion  logTransaccion = new LogTransaccion();
        logTransaccion.setRespuestaTexto(dataResponse.toString());
        logTransaccion.setRespuestaCodigo(accessToken);
        logTransaccion.setTipoRegistro("Archivo-externo");
        logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
        this.logTransaccionRepository.save(logTransaccion);

        return dataResponse;
    }


    public String getAutenticacionRs() throws Exception {
        LogTransaccion  logTransaccion = new LogTransaccion();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString());
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        //Obtenemos el URLAutentificacion
        String urlAutentificacion = parametroMapper.getAutentificacion("AutentificacionURL");
        String usernameAutentificacion = parametroMapper.getAutentificacion("AutentificacionUSERNAME");
        String passAutentificacion = parametroMapper.getAutentificacion("AutentificacionPASSWORD");
        String granTypeAutentificacion = parametroMapper.getAutentificacion("AutentificacionGRANT_TYPE");

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("username", usernameAutentificacion);
        requestBody.add("password", passAutentificacion);
        requestBody.add("grant_type", granTypeAutentificacion);

        logTransaccion.setLogUsuario("username : "+usernameAutentificacion+" | " +"password : "+passAutentificacion +" | "+ "grant_type : "+granTypeAutentificacion);

        HttpEntity formEntity = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);
        ResponseEntity<TokenDto> autentificacion = null;

        try{
            autentificacion = restTemplate.exchange
                    //(url, HttpMethod.POST, new HttpEntity<String>(createHeaders(USERNAME, PASSWORD)), CgaUserDto.class);
                            (urlAutentificacion, HttpMethod.POST,  formEntity, TokenDto.class);
        }catch (Exception ex){
            logTransaccion.setRespuestaCodigo(ex.getMessage());
            logTransaccion.setTipoRegistro("AutenticacionError");
            logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
            this.logTransaccionRepository.save(logTransaccion);
            throw new Exception("Error de conexion del servicio  " + urlAutentificacion);
        }


        TokenDto login = autentificacion.getBody();
        System.out.println("");
        logTransaccion.setRespuestaCodigo(autentificacion.getBody().getAccess_token());
        logTransaccion.setTipoRegistro("Autenticacion");
        logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
        this.logTransaccionRepository.save(logTransaccion);
        return login.getAccess_token();
    }


    public Object getCrearDocumento(String accessToken,Proveedor p,AdjuntoCargaDto dataRequest) throws Exception {
        LogTransaccion logTransaccion = new LogTransaccion();
        //obtener tipo de documento
        MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento(dataRequest.getCodigoDocumento());

        if(!mtrTipoDocumento.isIdSet()){
            ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
            responseCrearDocumentoDto.setMensaje("No existe el código :"+ dataRequest.getCodigoDocumento());
            responseCrearDocumentoDto.setStatus("-1");
            return  responseCrearDocumentoDto;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_XML.toString());
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.setBearerAuth(accessToken);

        //Obtenemos el URL
        String urlCrearDocumento = parametroMapper.getAutentificacion("CrearDocumentoURL");
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        Date fechaActual = DateUtils.obtenerFechaHoraActual();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");

        String direccion = "string";
        if(Optional.ofNullable(p.getDireccionFiscal()).isPresent() ){
            if(!p.getDireccionFiscal().equals(""))
            {
                direccion = p.getDireccionFiscal();
            }
        }

        //xml
        String xmlString = "<Formulario xmlns:i='http://www.w3.org/2001/XMLSchema-instance' xmlns='http://schemas.datacontract.org/2004/07/webAuthentic.Models'>";
        xmlString+="<Acta_Sustentos></Acta_Sustentos>";
        xmlString+="<Codigo_Aceptacion>0</Codigo_Aceptacion>";
        xmlString+="<Codigo_Concurso>0</Codigo_Concurso>";
        xmlString+="<Contacto_Comercial>"+p.getContacto()+"</Contacto_Comercial>";
        xmlString+="<Correo_Electronico>"+p.getEmail()+"</Correo_Electronico>";
        xmlString+="<Creador>"+p.getRazonSocial()+"</Creador>";
        xmlString+="<Direccion>"+direccion+"</Direccion>";

        //Enviar Base64
        if(!dataRequest.getBase64File().isEmpty()){
            xmlString+="<Docbase64>"+dataRequest.getBase64File()+"</Docbase64>";
        }else{
            xmlString+="<Docbase64></Docbase64>";
        }

        xmlString+="<Fecha_Creacion>"+dateFormat.format(fechaActual) +"</Fecha_Creacion>";
        xmlString+="<Fecha_Finalizacion></Fecha_Finalizacion>";
        xmlString+="<Fecha_Inicio></Fecha_Inicio>";
        xmlString+="<FileExtencion>"+dataRequest.getFileExtencion().toLowerCase()+"</FileExtencion>";
        xmlString+="<Nombre_Concurso></Nombre_Concurso>";
        xmlString+="<Nombre_Documento>"+mtrTipoDocumento.getDescripcion()+"</Nombre_Documento>";

        String pais="string";
        if(Optional.ofNullable(p.getPais()).isPresent()){
            pais = p.getPais().getDescripcion();
        }
        xmlString+="<Pais>"+pais+"</Pais>";

        xmlString+="<Pedido></Pedido>";
        String provincia="string";
        if(Optional.ofNullable(p.getProvincia()).isPresent()){
            provincia = p.getProvincia().getDescripcion();
        }
        xmlString+="<Provincia>"+provincia+"</Provincia>";
        xmlString+="<RUC>"+p.getRuc()+"</RUC>";
        xmlString+="<Razon_Social>"+p.getRazonSocial()+"</Razon_Social>";
        String region="string";
        if(Optional.ofNullable(p.getRegion()).isPresent()){
            region = p.getRegion().getDescripcion();
        }
        xmlString+="<Region>"+region+"</Region>";
        xmlString+="<Representante_Legal>"+p.getNombreRepresentanteLegal()+"</Representante_Legal>";
        xmlString+="<Telefono_Fijo>"+p.getTelefono()+"</Telefono_Fijo>";
        xmlString+="<Telefono_Movil>"+p.getCelular()+"</Telefono_Movil>";
        xmlString+="<Tipo_Persona>"+p.getTipoPersona()+"</Tipo_Persona>";
        xmlString+="<Tipo_Proveedor>"+p.getTipoProveedor().getDescripcion()+"</Tipo_Proveedor>";

        xmlString += "</Formulario>";

        log.info(xmlString);

        HttpEntity<String> request = new HttpEntity<String>(xmlString, headers);
        ResponseEntity<Object> respuesta = null;

        try{
            respuesta = restTemplate.exchange
                            (urlCrearDocumento, HttpMethod.POST,  request, Object.class);
        }catch (Exception ex){
            logTransaccion.setEnvioTrama(xmlString);
            logTransaccion.setRespuestaCodigo(ex.getMessage());
            logTransaccion.setTipoRegistro("Adjuntos sunat");
            this.logTransaccionRepository.save(logTransaccion);
            throw new Exception("Error al subir el archivo  servicio  http://200.4.228.156:8083/api/crear_documento :"+ex.toString() );
        }
        //obtener getbody
        ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
        logTransaccion.setEnvioTrama(xmlString);
        logTransaccion.setRespuestaCodigo(respuesta.getBody().toString());
        logTransaccion.setTipoRegistro("Adjuntos sunat");
        this.logTransaccionRepository.save(logTransaccion);
        if(respuesta.getBody().equals("Error: No existe tipo documental")){
            responseCrearDocumentoDto.setStatus("-1");
            responseCrearDocumentoDto.setMensaje("Registro Fallido "+respuesta.getBody().toString());
            return responseCrearDocumentoDto;
        }
        Object respuestaBody = respuesta.getBody();
        Gson g = new Gson();
        try{
            responseCrearDocumentoDto =  g.fromJson(respuestaBody.toString(), ResponseCrearDocumentoDto.class);
        }catch (Exception e){
            throw new PortalException(respuestaBody.toString());
        }

        //Registrar el la tabla Proveedor Adjunto Sunat
        if(!responseCrearDocumentoDto.getIDDocumento().equals("") || !responseCrearDocumentoDto.getIDDocumento().equals(null)  ){

            ProveedorAdjuntoSunat  proveedorAdjuntoSunat = new ProveedorAdjuntoSunat();
            proveedorAdjuntoSunat.setArchivoId(responseCrearDocumentoDto.getIDDocumento().toString());
            proveedorAdjuntoSunat.setArchivoNombre(dataRequest.getNombreDocumento());
            proveedorAdjuntoSunat.setRutaAdjunto("archivo_"+responseCrearDocumentoDto.getIDDocumento().toString());
            if(dataRequest.getFileExtencion().isEmpty()){
                dataRequest.setFileExtencion("fichaDeDatos");
            }
            proveedorAdjuntoSunat.setArchivoTipo(dataRequest.getFileExtencion());
            proveedorAdjuntoSunat.setMtrTipoDocumento(mtrTipoDocumento);
            proveedorAdjuntoSunat.setIdProveedor(p);
            ProveedorAdjuntoSunat resultProveedorAdjuntoSunats=  this.proveedorAdjuntoSunatRepository.save(proveedorAdjuntoSunat);
            return resultProveedorAdjuntoSunats;
        }

        responseCrearDocumentoDto.setStatus("1");
        responseCrearDocumentoDto.setMensaje("Registro Exitoso!");
        return responseCrearDocumentoDto;
    }

    public Object getCrearDocumentoCatalogo(String accessToken,Proveedor p,AdjuntoCargaDto dataRequest) throws Exception {
        LogTransaccion logTransaccion = new LogTransaccion();
        //obtener tipo de documento
        MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento(dataRequest.getCodigoDocumento());

        if(!mtrTipoDocumento.isIdSet()){
            ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
            responseCrearDocumentoDto.setMensaje("No existe el código :"+ dataRequest.getCodigoDocumento());
            responseCrearDocumentoDto.setStatus("-1");
            return  responseCrearDocumentoDto;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_XML.toString());
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.setBearerAuth(accessToken);

        //Obtenemos el URL
        String urlCrearDocumento = parametroMapper.getAutentificacion("CrearDocumentoURL");
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        Date fechaActual = DateUtils.obtenerFechaHoraActual();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        //xml
        String xmlString = "<Formulario xmlns:i='http://www.w3.org/2001/XMLSchema-instance' xmlns='http://schemas.datacontract.org/2004/07/webAuthentic.Models'>";
        xmlString+="<Acta_Sustentos></Acta_Sustentos>";
        xmlString+="<Codigo_Aceptacion></Codigo_Aceptacion>";
        xmlString+="<Codigo_Concurso></Codigo_Concurso>";
        xmlString+="<Contacto_Comercial>"+p.getContacto()+"</Contacto_Comercial>";
        xmlString+="<Correo_Electronico>"+p.getEmail()+"</Correo_Electronico>";
        xmlString+="<Creador></Creador>";
        xmlString+="<Direccion>"+p.getDireccionFiscal()+"</Direccion>";


        //Enviar Base64
        if(!dataRequest.getBase64File().isEmpty()){
            xmlString+="<Docbase64>"+dataRequest.getBase64File()+"</Docbase64>";
        }

        xmlString+="<Fecha_Creacion>"+dateFormat.format(fechaActual) +"</Fecha_Creacion>";
        xmlString+="<Fecha_Finalizacion></Fecha_Finalizacion>";
        xmlString+="<Fecha_Inicio></Fecha_Inicio>";
        xmlString+="<FileExtencion>"+dataRequest.getFileExtencion().toLowerCase()+"</FileExtencion>";
        xmlString+="<Nombre_Concurso></Nombre_Concurso>";
        xmlString+="<Nombre_Documento>"+mtrTipoDocumento.getDescripcion()+"</Nombre_Documento>";

        String pais="string";
        if(Optional.ofNullable(p.getPais()).isPresent()){
            pais = p.getPais().getDescripcion();
        }
        xmlString+="<Pais>"+pais+"</Pais>";


        xmlString+="<Pedido></Pedido>";
        String provincia="string";
        if(Optional.ofNullable(p.getProvincia()).isPresent()){
            provincia = p.getProvincia().getDescripcion();
        }
        xmlString+="<Provincia>"+provincia+"</Provincia>";
        xmlString+="<RUC>"+p.getRuc()+"</RUC>";
        xmlString+="<Razon_Social>"+p.getRazonSocial()+"</Razon_Social>";

        String region="string";
        if(Optional.ofNullable(p.getRegion()).isPresent()){
            region = p.getRegion().getDescripcion();
        }
        xmlString+="<Region>"+region+"</Region>";
        xmlString+="<Representante_Legal>"+p.getNombreRepresentanteLegal()+"</Representante_Legal>";
        xmlString+="<Telefono_Fijo>"+p.getTelefono()+"</Telefono_Fijo>";
        xmlString+="<Telefono_Movil>"+p.getTelefonoMovil()+"</Telefono_Movil>";
        xmlString+="<Tipo_Persona>"+p.getTipoPersona()+"</Tipo_Persona>";
        xmlString+="<Tipo_Proveedor>"+p.getTipoProveedor().getDescripcion()+"</Tipo_Proveedor>";

        xmlString += "</Formulario>";
        log.info(xmlString);

        HttpEntity<String> request = new HttpEntity<String>(xmlString, headers);
        ResponseEntity<Object> respuesta;

        try{
            respuesta = restTemplate.exchange
                    (urlCrearDocumento, HttpMethod.POST,  request, Object.class);
        }catch (Exception ex){
            ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
            logTransaccion.setEnvioTrama(xmlString);
            logTransaccion.setRespuestaCodigo(ex.getMessage());
            logTransaccion.setTipoRegistro("Catalogo");
            this.logTransaccionRepository.save(logTransaccion);
            throw new Exception("Error de conexion del servicio  http://200.4.228.156:8083/api/crear_documento :"+ex.getMessage() );
        }
        //obtener getbody
        ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
        logTransaccion.setEnvioTrama(xmlString);
        logTransaccion.setRespuestaCodigo(respuesta.getBody().toString());
        logTransaccion.setTipoRegistro("Catalogo");
        this.logTransaccionRepository.save(logTransaccion);
        if(respuesta.getBody().equals("Error: No existe tipo documental")){
            responseCrearDocumentoDto.setStatus("-1");
            responseCrearDocumentoDto.setMensaje("Registro Fallido "+respuesta.getBody().toString());
            return responseCrearDocumentoDto;
        }
        Object respuestaBody = respuesta.getBody();
        Gson g = new Gson();
        responseCrearDocumentoDto =  g.fromJson(respuestaBody.toString(), ResponseCrearDocumentoDto.class);

        //Registrar el la tabla Proveedor Catalogo
        if(!responseCrearDocumentoDto.getIDDocumento().equals("") || !responseCrearDocumentoDto.getIDDocumento().equals(null)  ){

            ProveedorCatalogo proveedorCatalogo = new ProveedorCatalogo();
            proveedorCatalogo.setArchivoId(responseCrearDocumentoDto.getIDDocumento().toString());
            proveedorCatalogo.setArchivoNombre(dataRequest.getNombreDocumento());
            proveedorCatalogo.setRutaCatalogo("archivo_"+responseCrearDocumentoDto.getIDDocumento().toString());
            proveedorCatalogo.setArchivoTipo(dataRequest.getFileExtencion());
            proveedorCatalogo.setProveedor(p);
            this.proveedorCatalogoRepository.save(proveedorCatalogo);

        }

        responseCrearDocumentoDto.setStatus("1");
        responseCrearDocumentoDto.setMensaje("Registro Exitoso!");
        return responseCrearDocumentoDto;
    }

    public Object getObtenerDocumento(String accessToken,String archivoId) throws Exception {
        LogTransaccion logTransaccion = new LogTransaccion();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_XML.toString());
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.setBearerAuth(accessToken);

        //Obtenemos el URLConsultarDocumento
        String ConsultarDocumento = parametroMapper.getAutentificacion("ConsultarDocumentoURL");
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        //xml
        String xmlString = "<Documento xmlns:i='http://www.w3.org/2001/XMLSchema-instance' xmlns='http://schemas.datacontract.org/2004/07/webAuthentic.Models'>";
        xmlString+= "<DDocumento>";
        xmlString+=   "<Documento.CDocumento>";
        xmlString+=    "<IdDocumento>"+archivoId+"</IdDocumento>";
        xmlString+=   "</Documento.CDocumento>";
        xmlString+= "</DDocumento>";
        xmlString+= "</Documento>";

        HttpEntity<String> request = new HttpEntity<String>(xmlString, headers);
        ResponseEntity<Object> respuesta;

        try{
            respuesta = restTemplate.exchange
                    (ConsultarDocumento, HttpMethod.POST,  request, Object.class);
        }catch (Exception ex){
            logTransaccion.setEnvioTrama(xmlString);
            logTransaccion.setRespuestaCodigo(ex.getMessage());
            logTransaccion.setTipoRegistro("Obtener-archivo");
            this.logTransaccionRepository.save(logTransaccion);
            throw new Exception("Error de conexion del servicio  http://200.4.228.156:8081/consultar_documento :"+ex.getMessage() );
        }
        logTransaccion.setEnvioTrama(xmlString);
        logTransaccion.setRespuestaCodigo(respuesta.getBody().toString());
        logTransaccion.setTipoRegistro("Obtener-archivo");
        this.logTransaccionRepository.save(logTransaccion);
        Object respuestaBody = respuesta.getBody();
        Gson g = new Gson();
        String replaceString =respuestaBody.toString().replace("{ documento: ","{ documento:'");
        replaceString = replaceString.replace("}","'}");
        System.out.println("");
        ResponseAdjuntoDocumentoDto adjuntoDocumentoDto =  g.fromJson(replaceString, ResponseAdjuntoDocumentoDto.class);

        System.out.println("");
        //Decode data on other side, by processing encoded data
        byte[] valueDecoded = Base64.decodeBase64(adjuntoDocumentoDto.getDocumento());

        adjuntoDocumentoDto.setDocumento(new String(valueDecoded));

        return valueDecoded;
    }


    public Object crearDocumentoNoticia(AdjuntoNoticiaDto adjuntoNoticiaDto, InformacionNoticia n) throws Exception {
        LogTransaccion logTransaccion = new LogTransaccion();
        //obtener tipo de documento
        MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento(adjuntoNoticiaDto.getCodigoTipoDocumento());

        if (mtrTipoDocumento == null) {
            ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
            responseCrearDocumentoDto.setMensaje("No existe el código :"+ adjuntoNoticiaDto.getCodigoTipoDocumento());
            responseCrearDocumentoDto.setStatus("-1");
            return  responseCrearDocumentoDto;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_XML.toString());
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.setBearerAuth(this.getAutenticacionRs());

        //Obtenemos el URL
        String urlCrearDocumento = parametroMapper.getAutentificacion("CrearDocumentoURL");
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        Date fechaActual = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        //xml
        String xmlString = "<Formulario xmlns:i='http://www.w3.org/2001/XMLSchema-instance' xmlns='http://schemas.datacontract.org/2004/07/webAuthentic.Models'>";
        xmlString+="<Acta_Sustentos></Acta_Sustentos>";
        xmlString+="<Codigo_Aceptacion></Codigo_Aceptacion>";
        xmlString+="<Codigo_Concurso></Codigo_Concurso>";
        xmlString+="<Contacto_Comercial></Contacto_Comercial>";
        xmlString+="<Correo_Electronico></Correo_Electronico>";
        xmlString+="<Creador></Creador>";
        xmlString+="<Direccion></Direccion>";


        //Enviar Base64
        if(!adjuntoNoticiaDto.getBase64File().isEmpty()){
            xmlString+="<Docbase64>"+adjuntoNoticiaDto.getBase64File()+"</Docbase64>";
        }

        xmlString+="<Fecha_Creacion>"+dateFormat.format(fechaActual) +"</Fecha_Creacion>";
        xmlString+="<Fecha_Finalizacion></Fecha_Finalizacion>";
        xmlString+="<Fecha_Inicio></Fecha_Inicio>";
        xmlString+="<FileExtencion>"+adjuntoNoticiaDto.getFileExtencion().toLowerCase()+"</FileExtencion>";
        xmlString+="<Nombre_Concurso></Nombre_Concurso>";
        xmlString+="<Nombre_Documento>"+mtrTipoDocumento.getDescripcion()+"</Nombre_Documento>";
        xmlString+="<Pais></Pais>";
        xmlString+="<Pedido></Pedido>";
        xmlString+="<Provincia></Provincia>";
        xmlString+="<RUC>000</RUC>";
        xmlString+="<Razon_Social></Razon_Social>";
        xmlString+="<Region></Region>";
        xmlString+="<Representante_Legal></Representante_Legal>";
        xmlString+="<Telefono_Fijo></Telefono_Fijo>";
        xmlString+="<Telefono_Movil></Telefono_Movil>";
        xmlString+="<Tipo_Persona></Tipo_Persona>";
        xmlString+="<Tipo_Proveedor></Tipo_Proveedor>";

        xmlString += "</Formulario>";
        log.info(xmlString);

        HttpEntity<String> request = new HttpEntity<String>(xmlString, headers);
        ResponseEntity<Object> respuesta;

        try{
            respuesta = restTemplate.exchange
                    (urlCrearDocumento, HttpMethod.POST,  request, Object.class);
        }catch (Exception ex){
            logTransaccion.setEnvioTrama(xmlString);
            logTransaccion.setRespuestaCodigo(ex.getMessage());
            logTransaccion.setTipoRegistro("Noticia");
            logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
            this.logTransaccionRepository.save(logTransaccion);
            throw new Exception("Error de conexion del servicio  http://200.4.228.156:8083/api/crear_documento :"+ex.getMessage() );
        }
        //obtener getbody
        ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
        logTransaccion.setEnvioTrama(xmlString);
        logTransaccion.setRespuestaCodigo(respuesta.getBody().toString());
        logTransaccion.setTipoRegistro("Noticia");
        logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
        this.logTransaccionRepository.save(logTransaccion);
        if(respuesta.getBody().equals("Error: No existe tipo documental")){
            responseCrearDocumentoDto.setStatus("-1");
            responseCrearDocumentoDto.setMensaje("Registro Fallido "+respuesta.getBody().toString());
            return responseCrearDocumentoDto;
        }
        Object respuestaBody = respuesta.getBody();
        Gson g = new Gson();
        responseCrearDocumentoDto =  g.fromJson(respuestaBody.toString(), ResponseCrearDocumentoDto.class);

        //Registrar el la tabla Proveedor Catalogo
        if(!responseCrearDocumentoDto.getIDDocumento().equals("") || !responseCrearDocumentoDto.getIDDocumento().equals(null)  ){

            n.setArchivoId(responseCrearDocumentoDto.getIDDocumento().toString());
            n.setArchivoNombre(adjuntoNoticiaDto.getNombreDocumento());
            n.setArchivoTipo(adjuntoNoticiaDto.getFileExtencion());
            this.informacionNoticiaRepository.save(n);
        }

        responseCrearDocumentoDto.setStatus("1");
        responseCrearDocumentoDto.setMensaje("Registro Exitoso!");
        return responseCrearDocumentoDto;
    }


    public Object crearDocumentoHomologacion(AdjuntoHomologacionDto adjuntoHomologacionDto, ProveedorHomologacion proveedorHomologacion)  throws Exception {
        log.info("ENTRADA ADJUNTO HOMOLOGACIÓN- " + adjuntoHomologacionDto);
        log.info("ENTRADA PROVEEDOR HOMOLOGACIÓN- " + proveedorHomologacion);

        LogTransaccion logTransaccion = new LogTransaccion();
        //obtener tipo de documento
        MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento(adjuntoHomologacionDto.getCodigoTipoDocumento());
        if(!mtrTipoDocumento.isIdSet()){
            ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
            responseCrearDocumentoDto.setMensaje("No existe el código :"+ adjuntoHomologacionDto.getCodigoTipoDocumento());
            responseCrearDocumentoDto.setStatus("-1");
            return  responseCrearDocumentoDto;
        }



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_XML.toString());
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.setBearerAuth(this.getAutenticacionRs());

        //Obtenemos el URL
        String urlCrearDocumento = parametroMapper.getAutentificacion("CrearDocumentoURL");
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        Date fechaActual = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        //xml
        String xmlString = "<Formulario xmlns:i='http://www.w3.org/2001/XMLSchema-instance' xmlns='http://schemas.datacontract.org/2004/07/webAuthentic.Models'>";
        xmlString+="<Acta_Sustentos></Acta_Sustentos>";
        xmlString+="<Codigo_Aceptacion></Codigo_Aceptacion>";
        xmlString+="<Codigo_Concurso></Codigo_Concurso>";
        xmlString+="<Contacto_Comercial></Contacto_Comercial>";
        xmlString+="<Correo_Electronico></Correo_Electronico>";
        xmlString+="<Creador></Creador>";
        xmlString+="<Direccion></Direccion>";


        //Enviar Base64
        if(!adjuntoHomologacionDto.getBase64File().isEmpty()){
            xmlString+="<Docbase64>"+adjuntoHomologacionDto.getBase64File()+"</Docbase64>";
        }

        xmlString+="<Fecha_Creacion>"+dateFormat.format(fechaActual) +"</Fecha_Creacion>";
        xmlString+="<Fecha_Finalizacion></Fecha_Finalizacion>";
        xmlString+="<Fecha_Inicio></Fecha_Inicio>";
        xmlString+="<FileExtencion>"+adjuntoHomologacionDto.getFileExtencion().toLowerCase()+"</FileExtencion>";
        xmlString+="<Nombre_Concurso></Nombre_Concurso>";
        xmlString+="<Nombre_Documento>"+mtrTipoDocumento.getDescripcion()+"</Nombre_Documento>";
        xmlString+="<Pais></Pais>";
        xmlString+="<Pedido></Pedido>";
        xmlString+="<Provincia></Provincia>";
        xmlString+="<RUC>000</RUC>";
        xmlString+="<Razon_Social></Razon_Social>";
        xmlString+="<Region></Region>";
        xmlString+="<Representante_Legal></Representante_Legal>";
        xmlString+="<Telefono_Fijo></Telefono_Fijo>";
        xmlString+="<Telefono_Movil></Telefono_Movil>";
        xmlString+="<Tipo_Persona></Tipo_Persona>";
        xmlString+="<Tipo_Proveedor></Tipo_Proveedor>";

        xmlString += "</Formulario>";
        log.info("TRAMA" + xmlString);

        HttpEntity<String> request = new HttpEntity<String>(xmlString, headers);
        ResponseEntity<Object> respuesta;



        try{
            respuesta = restTemplate.exchange
                    (urlCrearDocumento, HttpMethod.POST,  request, Object.class);
        }catch (Exception ex){
            logTransaccion.setEnvioTrama(xmlString);
            logTransaccion.setRespuestaCodigo(ex.getMessage());
            logTransaccion.setTipoRegistro("Homologacion");
            logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
            this.logTransaccionRepository.save(logTransaccion);
            throw new Exception("Error de conexion del servicio  http://200.4.228.156:8083/api/crear_documento :"+ex.getMessage() );
        }
        //obtener getbody
        ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
        logTransaccion.setEnvioTrama(xmlString);
        logTransaccion.setRespuestaCodigo(respuesta.getBody().toString());
        logTransaccion.setTipoRegistro("Homologacion");
        this.logTransaccionRepository.save(logTransaccion);
        if(respuesta.getBody().equals("Error: No existe tipo documental")){
            responseCrearDocumentoDto.setStatus("-1");
            responseCrearDocumentoDto.setMensaje("Registro Fallido "+respuesta.getBody().toString());
            return responseCrearDocumentoDto;
        }
        Object respuestaBody = respuesta.getBody();
        Gson g = new Gson();
        responseCrearDocumentoDto =  g.fromJson(respuestaBody.toString(), ResponseCrearDocumentoDto.class);

        //Registrar el la tabla Proveedor Catalogo
        if(!responseCrearDocumentoDto.getIDDocumento().equals("") || !responseCrearDocumentoDto.getIDDocumento().equals(null)  ){

            proveedorHomologacion.setArchivoId(responseCrearDocumentoDto.getIDDocumento().toString());
            proveedorHomologacion.setArchivoNombre(adjuntoHomologacionDto.getNombreDocumento());
            proveedorHomologacion.setArchivoTipo(adjuntoHomologacionDto.getFileExtencion());
            this.proveedorHomologacionRepository.save(proveedorHomologacion);
        }

        responseCrearDocumentoDto.setStatus("1");
        responseCrearDocumentoDto.setMensaje("Registro Exitoso!");
        log.info("RESPUESTA - " + responseCrearDocumentoDto);
        return responseCrearDocumentoDto;
    }

    public Object crearDocumentoLicitacion(LicitacionAdjuntoBase64Dto item, Licitacion licitacionGen) throws Exception  {

        //obtener tipo de documento
        MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento(item.getCodigoDocumento());
        LogTransaccion logTransaccion = new LogTransaccion();
        if(!mtrTipoDocumento.isIdSet()){
            ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
            responseCrearDocumentoDto.setMensaje("No existe el código :"+ item.getCodigoDocumento());
            responseCrearDocumentoDto.setStatus("-1");
            return  responseCrearDocumentoDto;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_XML.toString());
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.setBearerAuth(this.getAutenticacionRs());

        //Obtenemos el URL
        String urlCrearDocumento = parametroMapper.getAutentificacion("CrearDocumentoURL");
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        Date fechaActual = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        //xml
        String xmlString = "<Formulario xmlns:i='http://www.w3.org/2001/XMLSchema-instance' xmlns='http://schemas.datacontract.org/2004/07/webAuthentic.Models'>";
        xmlString+="<Acta_Sustentos></Acta_Sustentos>";
        xmlString+="<Codigo_Aceptacion></Codigo_Aceptacion>";
        xmlString+="<Codigo_Concurso>"+licitacionGen.getNroLicitacion()+"</Codigo_Concurso>";
        xmlString+="<Contacto_Comercial></Contacto_Comercial>";
        xmlString+="<Correo_Electronico></Correo_Electronico>";
        xmlString+="<Creador>"+licitacionGen.getUsuarioCreacion()+"</Creador>";
        xmlString+="<Direccion></Direccion>";


        //Enviar Base64
        if(!item.getBase64File().isEmpty()){
            xmlString+="<Docbase64>"+item.getBase64File()+"</Docbase64>";
        }

        xmlString+="<Fecha_Creacion>"+dateFormat.format(fechaActual) +"</Fecha_Creacion>";
        xmlString+="<Fecha_Finalizacion>"+dateFormat.format(licitacionGen.getFechaCierreRecepcionOferta())+"</Fecha_Finalizacion>";
        xmlString+="<Fecha_Inicio>"+dateFormat.format(licitacionGen.getFechaInicioRecepcionOferta())+"</Fecha_Inicio>";
        xmlString+="<FileExtencion>"+item.getFileExtencion().toLowerCase()+"</FileExtencion>";
        xmlString+="<Nombre_Concurso>"+licitacionGen.getNroLicitacion()+"</Nombre_Concurso>";
        xmlString+="<Nombre_Documento>"+mtrTipoDocumento.getDescripcion()+"</Nombre_Documento>";
        xmlString+="<Pais></Pais>";
        xmlString+="<Pedido></Pedido>";
        xmlString+="<Provincia></Provincia>";
        xmlString+="<RUC></RUC>";
        xmlString+="<Razon_Social></Razon_Social>";
        xmlString+="<Region></Region>";
        xmlString+="<Representante_Legal></Representante_Legal>";
        xmlString+="<Telefono_Fijo></Telefono_Fijo>";
        xmlString+="<Telefono_Movil></Telefono_Movil>";
        xmlString+="<Tipo_Persona></Tipo_Persona>";
        xmlString+="<Tipo_Proveedor></Tipo_Proveedor>";

        xmlString += "</Formulario>";
        log.info(xmlString);

        HttpEntity<String> request = new HttpEntity<String>(xmlString, headers);
        ResponseEntity<Object> respuesta;

        try{
            respuesta = restTemplate.exchange
                    (urlCrearDocumento, HttpMethod.POST,  request, Object.class);
        }catch (Exception ex){
            logTransaccion.setEnvioTrama(xmlString);
            logTransaccion.setRespuestaCodigo(ex.getMessage());
            logTransaccion.setTipoRegistro("Licitacion");
            logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
            this.logTransaccionRepository.save(logTransaccion);
            throw new Exception("Error de conexion del servicio  http://200.4.228.156:8083/api/crear_documento :"+ex.getMessage() );
        }
        //obtener getbody
        ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
        logTransaccion.setEnvioTrama(xmlString);
        logTransaccion.setTipoRegistro("Licitacion");
        logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
        logTransaccion.setRespuestaCodigo(respuesta.getBody().toString());
        this.logTransaccionRepository.save(logTransaccion);
        if(respuesta.getBody().equals("Error: No existe tipo documental")){
            responseCrearDocumentoDto.setStatus("-1");
            responseCrearDocumentoDto.setMensaje("Registro Fallido "+respuesta.getBody().toString());
            return responseCrearDocumentoDto;
        }

        String documentoOnBase = String.valueOf(respuesta.getBody());
        documentoOnBase = documentoOnBase.replace("{","");
        documentoOnBase = documentoOnBase.replace("IDDocumento:","");
        documentoOnBase = documentoOnBase.replace("}","");
        responseCrearDocumentoDto.setIDDocumento(Integer.valueOf(documentoOnBase.trim()));
        //        Object respuestaBody = respuesta.getBody();
        //        Gson g = new Gson();
        //        responseCrearDocumentoDto =  g.fromJson(respuestaBody.toString(), ResponseCrearDocumentoDto.class);

        //Registrar el la tabla Proveedor Catalogo
        if(!responseCrearDocumentoDto.getIDDocumento().equals("") || !responseCrearDocumentoDto.getIDDocumento().equals(null)  ){

            LicitacionAdjunto  licitacionAdjunto = new LicitacionAdjunto();
            licitacionAdjunto.setArchivoId(responseCrearDocumentoDto.getIDDocumento().toString());
            licitacionAdjunto.setArchivoNombre(item.getNombreDocumento());
            licitacionAdjunto.setArchivoTipo(item.getFileExtencion());
            licitacionAdjunto.setDescripcion(item.getNombreDocumento());
            licitacionAdjunto.setRutaAdjunto(item.getNombreDocumento());
            licitacionAdjunto.setMtrTipoDocumento(mtrTipoDocumento);
            licitacionAdjunto.setLicitacion(licitacionGen);
            LicitacionAdjunto ladj =this.licitacionAdjuntoRepository.save(licitacionAdjunto);
        }

        responseCrearDocumentoDto.setStatus("1");
        responseCrearDocumentoDto.setMensaje("Registro Exitoso!");
        return responseCrearDocumentoDto;
    }

    public Object getDocumentoLicitacionRespuesta(LicitacionAdjuntoBase64Dto item, Licitacion licitacionGen)  throws Exception {

        //obtener tipo de documento
        MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento(item.getCodigoDocumento());
        LogTransaccion logTransaccion = new LogTransaccion();
        if(!mtrTipoDocumento.isIdSet()){
            ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
            responseCrearDocumentoDto.setMensaje("No existe el código :"+ item.getCodigoDocumento());
            responseCrearDocumentoDto.setStatus("-1");
            return  responseCrearDocumentoDto;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_XML.toString());
        headers.add("Accept", String.valueOf(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(this.getAutenticacionRs());

        //Obtenemos el URL
        String urlCrearDocumento = parametroMapper.getAutentificacion("CrearDocumentoURL");
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        Date fechaActual = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        //xml
        String xmlString = "<Formulario xmlns:i='http://www.w3.org/2001/XMLSchema-instance' xmlns='http://schemas.datacontract.org/2004/07/webAuthentic.Models'>";
        xmlString+="<Acta_Sustentos></Acta_Sustentos>";
        xmlString+="<Codigo_Aceptacion></Codigo_Aceptacion>";
        xmlString+="<Codigo_Concurso>"+licitacionGen.getNroLicitacion()+"</Codigo_Concurso>";
        xmlString+="<Contacto_Comercial></Contacto_Comercial>";
        xmlString+="<Correo_Electronico></Correo_Electronico>";
        xmlString+="<Creador>"+licitacionGen.getUsuarioCreacion()+"</Creador>";
        xmlString+="<Direccion></Direccion>";


        //Enviar Base64
        if(!item.getBase64File().isEmpty()){
            xmlString+="<Docbase64>"+item.getBase64File()+"</Docbase64>";
        }

        xmlString+="<Fecha_Creacion>"+dateFormat.format(fechaActual) +"</Fecha_Creacion>";
        xmlString+="<Fecha_Finalizacion>"+dateFormat.format(licitacionGen.getFechaCierreRecepcionOferta())+"</Fecha_Finalizacion>";
        xmlString+="<Fecha_Inicio>"+dateFormat.format(licitacionGen.getFechaInicioRecepcionOferta())+"</Fecha_Inicio>";
        xmlString+="<FileExtencion>"+item.getFileExtencion().toLowerCase()+"</FileExtencion>";
        xmlString+="<Nombre_Concurso>"+licitacionGen.getNroLicitacion()+"</Nombre_Concurso>";
        xmlString+="<Nombre_Documento>"+mtrTipoDocumento.getDescripcion()+"</Nombre_Documento>";
        xmlString+="<Pais></Pais>";
        xmlString+="<Pedido></Pedido>";
        xmlString+="<Provincia></Provincia>";
        xmlString+="<RUC></RUC>";
        xmlString+="<Razon_Social></Razon_Social>";
        xmlString+="<Region></Region>";
        xmlString+="<Representante_Legal></Representante_Legal>";
        xmlString+="<Telefono_Fijo></Telefono_Fijo>";
        xmlString+="<Telefono_Movil></Telefono_Movil>";
        xmlString+="<Tipo_Persona></Tipo_Persona>";
        xmlString+="<Tipo_Proveedor></Tipo_Proveedor>";

        xmlString += "</Formulario>";
        log.info(xmlString);

        HttpEntity<String> request = new HttpEntity<String>(xmlString, headers);
        ResponseEntity<Object> respuesta;

        try{
            respuesta = restTemplate.exchange
                    (urlCrearDocumento, HttpMethod.POST,  request, Object.class);
        }catch (Exception ex){
            logTransaccion.setEnvioTrama(xmlString);
            logTransaccion.setRespuestaCodigo(ex.getMessage());
            logTransaccion.setTipoRegistro("LicitacionRespuesta");
            logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
            this.logTransaccionRepository.save(logTransaccion);
            throw new Exception("Error de conexion del servicio  http://200.4.228.156:8083/api/crear_documento :"+ex.getMessage() );
        }
        //obtener getbody
        ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
        logTransaccion.setEnvioTrama(xmlString);
        logTransaccion.setRespuestaCodigo(respuesta.getBody().toString());
        logTransaccion.setTipoRegistro("LicitacionRespuesta");
        logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
        this.logTransaccionRepository.save(logTransaccion);

        if(respuesta.getBody().equals("Error: No existe tipo documental")){
            responseCrearDocumentoDto.setStatus("-1");
            responseCrearDocumentoDto.setMensaje("Registro Fallido "+respuesta.getBody().toString());
            return responseCrearDocumentoDto;
        }

//        Object respuestaBody = respuesta.getBody();
//        Gson g = new Gson();
//        responseCrearDocumentoDto =  g.fromJson(respuestaBody.toString(), ResponseCrearDocumentoDto.class);
            String documentoOnBase = String.valueOf(respuesta.getBody());
            documentoOnBase = documentoOnBase.replace("{","");
            documentoOnBase = documentoOnBase.replace("IDDocumento:","");
            documentoOnBase = documentoOnBase.replace("}","");
            responseCrearDocumentoDto.setIDDocumento(Integer.valueOf(documentoOnBase.trim()));

        //Registrar el la tabla licitacionAdjuntoRespuesta
        if(responseCrearDocumentoDto.getIDDocumento() != null ){

            LicitacionAdjuntoRespuesta  licitacionAdjuntoRespuesta = new LicitacionAdjuntoRespuesta();
            licitacionAdjuntoRespuesta.setArchivoId(responseCrearDocumentoDto.getIDDocumento().toString());
            licitacionAdjuntoRespuesta.setArchivoNombre(item.getNombreDocumento());
            licitacionAdjuntoRespuesta.setArchivoTipo(item.getFileExtencion());
            licitacionAdjuntoRespuesta.setDescripcion(item.getNombreDocumento());
            licitacionAdjuntoRespuesta.setRutaAdjunto(item.getNombreDocumento());
            licitacionAdjuntoRespuesta.setTipoDocumento(mtrTipoDocumento);
            licitacionAdjuntoRespuesta.setIdLicitacion(licitacionGen.getIdLicitacion());
            try {
                this.licitacionAdjuntoRespuestaRepository.save(licitacionAdjuntoRespuesta);
            }catch (Exception exp){
               throw new PortalException(exp.getMessage());
            }
        }

        responseCrearDocumentoDto.setStatus("1");
        responseCrearDocumentoDto.setMensaje("Registro Exitoso!");
        return responseCrearDocumentoDto;
    }

    public Object crearDocumentoCotizacion(CotizacionAdjuntoBase64Dto item, Cotizacion cotizacion,Licitacion licitacionGen) throws Exception  {

        //obtener tipo de documento
        MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento(item.getMtrTipoDocumento());
        LogTransaccion logTransaccion = new LogTransaccion();
        if(!mtrTipoDocumento.isIdSet()){
            ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
            responseCrearDocumentoDto.setMensaje("No existe el código :"+ item.getMtrTipoDocumento());
            responseCrearDocumentoDto.setStatus("-1");
            return  responseCrearDocumentoDto;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_XML.toString());
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.setBearerAuth(this.getAutenticacionRs());

        //Obtenemos el URL
        String urlCrearDocumento = parametroMapper.getAutentificacion("CrearDocumentoURL");
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        Date fechaActual = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        //xml
        String xmlString = "<Formulario xmlns:i='http://www.w3.org/2001/XMLSchema-instance' xmlns='http://schemas.datacontract.org/2004/07/webAuthentic.Models'>";
        xmlString+="<Acta_Sustentos></Acta_Sustentos>";
        xmlString+="<Codigo_Aceptacion></Codigo_Aceptacion>";
        xmlString+="<Codigo_Concurso>"+licitacionGen.getNroLicitacion()+"</Codigo_Concurso>";
        xmlString+="<Contacto_Comercial></Contacto_Comercial>";
        xmlString+="<Correo_Electronico></Correo_Electronico>";
        xmlString+="<Creador>"+licitacionGen.getUsuarioCreacion()+"</Creador>";
        xmlString+="<Direccion></Direccion>";


        //Enviar Base64
        if(!item.getBase64File().isEmpty()){
            xmlString+="<Docbase64>"+item.getBase64File()+"</Docbase64>";
        }

        xmlString+="<Fecha_Creacion>"+dateFormat.format(fechaActual) +"</Fecha_Creacion>";
        xmlString+="<Fecha_Finalizacion>"+dateFormat.format(licitacionGen.getFechaCierreRecepcionOferta())+"</Fecha_Finalizacion>";
        xmlString+="<Fecha_Inicio>"+dateFormat.format(licitacionGen.getFechaInicioRecepcionOferta())+"</Fecha_Inicio>";
        xmlString+="<FileExtencion>"+item.getArchivoTipo().toLowerCase()+"</FileExtencion>";
        xmlString+="<Nombre_Concurso>"+licitacionGen.getNroLicitacion()+"</Nombre_Concurso>";
        xmlString+="<Nombre_Documento>"+mtrTipoDocumento.getDescripcion()+"</Nombre_Documento>";
        xmlString+="<Pais></Pais>";
        xmlString+="<Pedido></Pedido>";
        xmlString+="<Provincia></Provincia>";
        xmlString+="<RUC></RUC>";
        xmlString+="<Razon_Social></Razon_Social>";
        xmlString+="<Region></Region>";
        xmlString+="<Representante_Legal></Representante_Legal>";
        xmlString+="<Telefono_Fijo></Telefono_Fijo>";
        xmlString+="<Telefono_Movil></Telefono_Movil>";
        xmlString+="<Tipo_Persona></Tipo_Persona>";
        xmlString+="<Tipo_Proveedor></Tipo_Proveedor>";

        xmlString += "</Formulario>";
        log.info(xmlString);

        log.info(xmlString);

        HttpEntity<String> request = new HttpEntity<String>(xmlString, headers);
        ResponseEntity<Object> respuesta;

        try{
            respuesta = restTemplate.exchange
                    (urlCrearDocumento, HttpMethod.POST,  request, Object.class);
        }catch (Exception ex){
            logTransaccion.setEnvioTrama(xmlString);
            logTransaccion.setRespuestaCodigo(ex.getMessage());
            logTransaccion.setTipoRegistro("CotizacionAdjunto");
            logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
            this.logTransaccionRepository.save(logTransaccion);
            throw new Exception("Error de conexion del servicio  http://200.4.228.156:8083/api/crear_documento :"+ex.getMessage() );
        }
        //obtener getbody
        ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
        logTransaccion.setEnvioTrama(xmlString);
        logTransaccion.setRespuestaCodigo(respuesta.getBody().toString());
        logTransaccion.setTipoRegistro("CotizacionAdjunto");
        logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
        this.logTransaccionRepository.save(logTransaccion);
        if(respuesta.getBody().equals("Error: No existe tipo documental")){
            responseCrearDocumentoDto.setStatus("-1");
            responseCrearDocumentoDto.setMensaje("Registro Fallido "+respuesta.getBody().toString());
            return responseCrearDocumentoDto;
        }
//        Object respuestaBody = respuesta.getBody();
//        Gson g = new Gson();
//        responseCrearDocumentoDto =  g.fromJson(respuestaBody.toString(), ResponseCrearDocumentoDto.class);
        String documentoOnBase = String.valueOf(respuesta.getBody());
        documentoOnBase = documentoOnBase.replace("{","");
        documentoOnBase = documentoOnBase.replace("IDDocumento:","");
        documentoOnBase = documentoOnBase.replace("}","");
        responseCrearDocumentoDto.setIDDocumento(Integer.valueOf(documentoOnBase.trim()));

        //Registrar el la tabla Proveedor Catalogo
        if(!responseCrearDocumentoDto.getIDDocumento().equals("") || !responseCrearDocumentoDto.getIDDocumento().equals(null)  ){

            CotizacionAdjunto  cotizacionAdjunto = new CotizacionAdjunto();
            cotizacionAdjunto.setArchivoId(responseCrearDocumentoDto.getIDDocumento().toString());
            cotizacionAdjunto.setArchivoNombre(item.getArchivoNombre());
            cotizacionAdjunto.setArchivoTipo(item.getArchivoTipo());
            cotizacionAdjunto.setDescripcion(item.getArchivoNombre());
            cotizacionAdjunto.setRutaAdjunto(item.getArchivoNombre());
            cotizacionAdjunto.setTipoDocumento(mtrTipoDocumento);
            cotizacionAdjunto.setIndCotizacion("0");
            cotizacionAdjunto.setCotizacion(cotizacion);
            CotizacionAdjunto ladj =this.cotizacionAdjuntoRepository.save(cotizacionAdjunto);
        }



        responseCrearDocumentoDto.setStatus("1");
        responseCrearDocumentoDto.setMensaje("Registro Exitoso!");
        return responseCrearDocumentoDto;
    }

    public Object crearDocumentoActaSustento(AdjuntoActaSustentoBase64Dto sustentoBase64Dto, ActaSustento actaSustento) throws Exception {

        //obtener tipo de documento
        MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento(sustentoBase64Dto.getCodigoDocumento());
        LogTransaccion logTransaccion = new LogTransaccion();
        if(!mtrTipoDocumento.isIdSet()){
            ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
            responseCrearDocumentoDto.setMensaje("No existe el código :"+ sustentoBase64Dto.getCodigoDocumento());
            responseCrearDocumentoDto.setStatus("-1");
            return  responseCrearDocumentoDto;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_XML.toString());
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.setBearerAuth(this.getAutenticacionRs());

        //Obtenemos el URL
        String urlCrearDocumento = parametroMapper.getAutentificacion("CrearDocumentoURL");
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        Date fechaActual = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        //xml
        String xmlString = "<Formulario xmlns:i='http://www.w3.org/2001/XMLSchema-instance' xmlns='http://schemas.datacontract.org/2004/07/webAuthentic.Models'>";
        xmlString+="<Acta_Sustentos>"+actaSustento.getNumeroActaSustento()+"</Acta_Sustentos>";
        xmlString+="<Codigo_Aceptacion>"+actaSustento.getNumeroActaSustento()+"</Codigo_Aceptacion>";
        xmlString+="<Codigo_Concurso>"+actaSustento.getNumeroActaSustento()+"</Codigo_Concurso>";
        xmlString+="<Contacto_Comercial></Contacto_Comercial>";
        xmlString+="<Correo_Electronico></Correo_Electronico>";
        xmlString+="<Creador></Creador>";
        xmlString+="<Direccion></Direccion>";


        //Enviar Base64
        if(!sustentoBase64Dto.getBase64File().isEmpty()){
            xmlString+="<Docbase64>"+sustentoBase64Dto.getBase64File()+"</Docbase64>";
        }

        xmlString+="<Fecha_Creacion>"+dateFormat.format(fechaActual) +"</Fecha_Creacion>";
        xmlString+="<Fecha_Finalizacion></Fecha_Finalizacion>";
        xmlString+="<Fecha_Inicio></Fecha_Inicio>";
        xmlString+="<FileExtencion>"+sustentoBase64Dto.getFileExtencion().toLowerCase()+"</FileExtencion>";
        xmlString+="<Nombre_Concurso></Nombre_Concurso>";
        xmlString+="<Nombre_Documento>"+mtrTipoDocumento.getDescripcion()+"</Nombre_Documento>";
        xmlString+="<Pais></Pais>";
        xmlString+="<Pedido>"+actaSustento.getNumeroActaSustento()+"</Pedido>";
        xmlString+="<Provincia></Provincia>";
        xmlString+="<RUC>000</RUC>";
        xmlString+="<Razon_Social></Razon_Social>";
        xmlString+="<Region></Region>";
        xmlString+="<Representante_Legal></Representante_Legal>";
        xmlString+="<Telefono_Fijo></Telefono_Fijo>";
        xmlString+="<Telefono_Movil></Telefono_Movil>";
        xmlString+="<Tipo_Persona></Tipo_Persona>";
        xmlString+="<Tipo_Proveedor></Tipo_Proveedor>";

        xmlString += "</Formulario>";
        log.info(xmlString);

        HttpEntity<String> request = new HttpEntity<String>(xmlString, headers);
        ResponseEntity<Object> respuesta;

        try{
            respuesta = restTemplate.exchange
                    (urlCrearDocumento, HttpMethod.POST,  request, Object.class);
        }catch (Exception ex){
            logTransaccion.setEnvioTrama(xmlString);
            logTransaccion.setRespuestaCodigo(ex.getMessage());
            logTransaccion.setTipoRegistro("Acta sustento");
            logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
            this.logTransaccionRepository.save(logTransaccion);
            throw new Exception("Error de conexion del servicio  http://200.4.228.156:8083/api/crear_documento :"+ex.getMessage() );
        }
        //obtener getbody
        ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
        logTransaccion.setEnvioTrama(xmlString);
        logTransaccion.setRespuestaCodigo(respuesta.getBody().toString());
        logTransaccion.setTipoRegistro("Acta sustento");
        logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
        this.logTransaccionRepository.save(logTransaccion);
        if(respuesta.getBody().equals("Error: No existe tipo documental")){
            responseCrearDocumentoDto.setStatus("-1");
            responseCrearDocumentoDto.setMensaje("Registro Fallido "+respuesta.getBody().toString());
            return responseCrearDocumentoDto;
        }
//        Object respuestaBody = respuesta.getBody();
//        Gson g = new Gson();
//        responseCrearDocumentoDto =  g.fromJson(respuestaBody.toString(), ResponseCrearDocumentoDto.class);

        String documentoOnBase = String.valueOf(respuesta.getBody());
        documentoOnBase = documentoOnBase.replace("{","");
        documentoOnBase = documentoOnBase.replace("IDDocumento:","");
        documentoOnBase = documentoOnBase.replace("}","");
        responseCrearDocumentoDto.setIDDocumento(Integer.valueOf(documentoOnBase.trim()));
        //Registrar el la tabla Adjunto Acta sustento
        if(!responseCrearDocumentoDto.getIDDocumento().equals("") || !responseCrearDocumentoDto.getIDDocumento().equals(null)  ){

            AdjuntoActaSustento n = new AdjuntoActaSustento();
            n.setArchivo(responseCrearDocumentoDto.getIDDocumento().toString());
            n.setArchivoNombre(sustentoBase64Dto.getNombreDocumento());
            n.setArchivoTipo(sustentoBase64Dto.getFileExtencion());
            n.setActaSustento(actaSustento);
            this.adjuntoActaSustentoRepository.save(n);
        }

        responseCrearDocumentoDto.setStatus("1");
        responseCrearDocumentoDto.setMensaje("Registro Exitoso!");
        return responseCrearDocumentoDto;
    }

    public String crearDocumentoPrefactura(AdjuntoPreFacturaDto base64Dto, Prefactura prefactura) throws Exception {
        LogTransaccion logTransaccion = new LogTransaccion();

        //obtener tipo de documento
        MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento(base64Dto.getCodigoTipoDocumento());

        if(!mtrTipoDocumento.isIdSet()){
            ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
            responseCrearDocumentoDto.setMensaje("No existe el código :"+ base64Dto.getCodigoTipoDocumento());
            responseCrearDocumentoDto.setStatus("-1");
            throw new  PortalException(responseCrearDocumentoDto.getMensaje());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_XML.toString());
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.setBearerAuth(this.getAutenticacionRs());

        //Obtenemos el URL
        String urlCrearDocumento = parametroMapper.getAutentificacion("CrearDocumentoURL");
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        Date fechaActual = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        //xml
        String xmlString = "<Formulario xmlns:i='http://www.w3.org/2001/XMLSchema-instance' xmlns='http://schemas.datacontract.org/2004/07/webAuthentic.Models'>";
        xmlString+="<Acta_Sustentos>"+0+"</Acta_Sustentos>";
        xmlString+="<Codigo_Aceptacion>"+0+"</Codigo_Aceptacion>";
        xmlString+="<Codigo_Concurso>"+0+"</Codigo_Concurso>";
        xmlString+="<Contacto_Comercial></Contacto_Comercial>";
        xmlString+="<Correo_Electronico></Correo_Electronico>";
        xmlString+="<Creador></Creador>";
        xmlString+="<Direccion></Direccion>";


        //Enviar Base64
        if(!base64Dto.getBase64File().isEmpty()){
            xmlString+="<Docbase64>"+base64Dto.getBase64File()+"</Docbase64>";
        }

        xmlString+="<Fecha_Creacion>"+dateFormat.format(fechaActual) +"</Fecha_Creacion>";
        xmlString+="<Fecha_Finalizacion></Fecha_Finalizacion>";
        xmlString+="<Fecha_Inicio></Fecha_Inicio>";
        xmlString+="<FileExtencion>"+base64Dto.getFileExtencion().toLowerCase()+"</FileExtencion>";
        xmlString+="<Nombre_Concurso></Nombre_Concurso>";
        xmlString+="<Nombre_Documento>"+mtrTipoDocumento.getDescripcion()+"</Nombre_Documento>";
        xmlString+="<Pais></Pais>";
        xmlString+="<Pedido>"+0+"</Pedido>";
        xmlString+="<Provincia></Provincia>";
        xmlString+="<RUC>000</RUC>";
        xmlString+="<Razon_Social></Razon_Social>";
        xmlString+="<Region></Region>";
        xmlString+="<Representante_Legal></Representante_Legal>";
        xmlString+="<Telefono_Fijo></Telefono_Fijo>";
        xmlString+="<Telefono_Movil></Telefono_Movil>";
        xmlString+="<Tipo_Persona></Tipo_Persona>";
        xmlString+="<Tipo_Proveedor></Tipo_Proveedor>";

        xmlString += "</Formulario>";
        log.info(xmlString);

        HttpEntity<String> request = new HttpEntity<String>(xmlString, headers);
        ResponseEntity<Object> respuesta;

        try{
            respuesta = restTemplate.exchange
                    (urlCrearDocumento, HttpMethod.POST,  request, Object.class);
        }catch (Exception ex){
            logTransaccion.setEnvioTrama(xmlString);
            logTransaccion.setRespuestaCodigo(ex.getMessage());
            logTransaccion.setTipoRegistro("Prefactura");
            logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
            this.logTransaccionRepository.save(logTransaccion);
            throw new Exception("Error de conexion del servicio  http://200.4.228.156:8083/api/crear_documento :"+ex.getMessage() );
        }
        //obtener getbody
        ResponseCrearDocumentoDto responseCrearDocumentoDto = new ResponseCrearDocumentoDto();
        logTransaccion.setEnvioTrama(xmlString);
        logTransaccion.setRespuestaCodigo(respuesta.getBody().toString());
        logTransaccion.setTipoRegistro("Prefactura");
        logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
        this.logTransaccionRepository.save(logTransaccion);
        if(respuesta.getBody().equals("Error: No existe tipo documental")){
            responseCrearDocumentoDto.setStatus("-1");
            responseCrearDocumentoDto.setMensaje("Registro Fallido "+respuesta.getBody().toString());
            throw new PortalException(responseCrearDocumentoDto.getMensaje());
        }
        Object respuestaBody = respuesta.getBody();
        Gson g = new Gson();
        responseCrearDocumentoDto =  g.fromJson(respuestaBody.toString(), ResponseCrearDocumentoDto.class);


        responseCrearDocumentoDto.setStatus("1");
        responseCrearDocumentoDto.setMensaje("Registro Exitoso!");
        return responseCrearDocumentoDto.getIDDocumento().toString();
    }

    @Override
    public  Object crearFichaProveedor(ProveedorInDto beanCentro) throws Exception{
        //FICHA DEL PROVEEDOR
        List<ProveedorAdjuntoSunat> proveedorFicha =
                this.proveedorAdjuntoSunatRepository
                        .getbyIdProveedorAndMtrTipoDocumento(
                                beanCentro.getIdProveedor(),
                                "FDP");
        log.error("crearDocumentoSunat 02");
        log.error("crearDocumentoSunat 03");
        AdjuntoCargaDto adjuntoCargaDto = new AdjuntoCargaDto();
        Proveedor p = proveedorRepository.getProveedorByIdProveedor(beanCentro.getIdProveedor());
        adjuntoCargaDto.setBase64File("");
        adjuntoCargaDto.setFileExtencion("");
        adjuntoCargaDto.setCodigoDocumento("FDP");//Ficha de Datos del Proveedor
        adjuntoCargaDto.setNombreDocumento("Ficha de Datos del Proveedor");
        String accessToken = getAutenticacionRs();
        Object dataResponse = getCrearDocumento(accessToken, p, adjuntoCargaDto);
        log.error("crearDocumentoSunat 04: " + dataResponse);
        //si la data existe crea el documento Sunat
        return  dataResponse;
    }

    @Override
    public CmisFile createDocumentoOc(String nameFolder, MultipartFile file) throws Exception {
        CmisFolderSession cmisFolderSession = this.obtenerFolderSession(nameFolder);
        Session session = cmisFolderSession.getSession();
        CmisFolder cmisFolder = cmisFolderSession.getCmisFolder();
        FilePart partFile = null;

        String extension = Optional.ofNullable(file.getOriginalFilename())
                .map(s -> s.split("\\."))
                .filter(s -> s.length > 0)
                .map(s -> "." + s[s.length - 1])
                .orElse("");
        log.error("createDocumento 00 extension: " + extension);
        String fileNewName = file.getOriginalFilename().replace(extension, "");
        log.error("createDocumento 01 fileNewName: " + fileNewName);

        //***********************************************
        String original = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝßàáâãäåæçèéêëìíîïðñòóôõöøùúûüýÿ";
        String ascii = "AAAAAAACEEEEIIIIDNOOOOOOUUUUYBaaaaaaaceeeeiiiionoooooouuuuyy";

        //String nombreFinal =fileNewName;
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            fileNewName = fileNewName.replace(original.charAt(i), ascii.charAt(i));
        }

        //EAAR fecha
        String fechaAutogenerada = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        fileNewName = fileNewName + "_" + fechaAutogenerada;
        String fileNameDb = fileNewName;
        String extensionDb = extension.replace(".", "");
        //
        log.error("createDocumento 02 fileNewName: " + fileNewName);
        fileNewName = fileNewName.replaceAll("[^a-zA-Z0-9]", "");
        log.error("createDocumento 03 fileNewName: " + fileNewName);
        log.error("createDocumento 04: " + extension + " fileName: " + fileNewName);
        fileNewName = fileNewName + extension;
        log.error("createDocumento 05 fileNewName: " + fileNewName);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, fileNewName);
        Folder folder = (Folder) session.getObject(cmisFolder.getId());

        InputStream stream = new ByteArrayInputStream(file.getBytes());
        ContentStream contentStream = session.getObjectFactory()
                .createContentStream(fileNewName, file.getSize(), file.getContentType(), stream);
        Document doc = null;
        try {
            doc = folder.createDocument(properties, contentStream, VersioningState.MAJOR);
        } catch (Exception ex) {
            ItemIterable<CmisObject> children = folder.getChildren();
            for (CmisObject cmisObject : children) {
                if (cmisObject.getName().equals(fileNewName)) {
                    doc = (Document) session.getObject(cmisObject.getId());
                    doc.delete(true);
                    doc = folder.createDocument(properties, contentStream, VersioningState.MAJOR);
                    break;
                }
            }
        }

        CmisFile cmisFile = new CmisFile();
        cmisFile.setId(doc.getId());
        cmisFile.setName(fileNameDb);
        cmisFile.setNameFinal(fileNewName);
        cmisFile.setExtension(extensionDb);
        cmisFile.setCarpetaId(folder.getId());
        cmisFile.setNombreFolder(nameFolder);
        //cmisFile.setType(file.getContentType());
        cmisFile.setType(extensionDb);
        cmisFile.setSize(file.getSize());
        StringBuilder path = new StringBuilder("/");
        path.append(session.getRootFolder().getId());
        path.append("/root/");
        path.append(folder.getName());
        path.append("/");
        path.append(fileNewName);
        cmisFile.setUrl(path.toString());

        return cmisFile;
    }
}
