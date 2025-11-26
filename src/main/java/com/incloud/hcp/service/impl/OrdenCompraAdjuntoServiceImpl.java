package com.incloud.hcp.service.impl;

import com.incloud.hcp.domain.OrdenCompra;
import com.incloud.hcp.domain.OrdenCompraAdjunto;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.domain.Usuario;
import com.incloud.hcp.enums.OrdenCompraAdjuntoEnum;
import com.incloud.hcp.repository.OrdenCompraAdjuntoRepository;
import com.incloud.hcp.repository.OrdenCompraRepository;
import com.incloud.hcp.repository.ProveedorRepository;
import com.incloud.hcp.repository.UsuarioRepository;
import com.incloud.hcp.service.OrdeCompraAdjuntoService;
import com.incloud.hcp.service.notificacion.ContactoPublicadaOCNotificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenCompraAdjuntoServiceImpl implements OrdeCompraAdjuntoService {

    @Autowired
    OrdenCompraRepository ordenCompraRepository;

    @Autowired
    OrdenCompraAdjuntoRepository ordenCompraAdjuntoRepository;

    @Autowired
    ContactoPublicadaOCNotificacion notificacion;

    @Autowired
    ProveedorRepository proveedorRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Override
    public List<OrdenCompraAdjunto> getAdjuntosByOc(String numeroOc) {

        OrdenCompra ordenCompra = ordenCompraRepository.findByNumeroOrdenCompra(numeroOc);

        if(ordenCompra.getNumeroOrdenCompra() == null){
            throw new RuntimeException("No se encontro esta orden de compra");
        }


        return ordenCompraAdjuntoRepository.findByNumeroOc(numeroOc);
    }

    @Override
    public String createAdjuntoOc(List<OrdenCompraAdjunto> ordenCompraAdjuntoList) {
        for (OrdenCompraAdjunto ordenCompraAdjunto: ordenCompraAdjuntoList){
            ordenCompraAdjunto.setEstado(OrdenCompraAdjuntoEnum.PENDIENTE.getCodigo());
            ordenCompraAdjuntoRepository.save(ordenCompraAdjunto);
        }
        OrdenCompra ordenCompra = ordenCompraRepository.findByNumeroOrdenCompra(ordenCompraAdjuntoList.get(0).getNumeroOc());
        Usuario usuario = usuarioRepository.findByCodigoSap(ordenCompra.getCompradorUsuarioSap());
        notificacion.enviarCreacionDocAdjuntos(ordenCompra,usuario, "generado");


        return "Documentos registrados exitosamente!";
    }

    @Override
    public String updateAdjuntoOc(List<OrdenCompraAdjunto> ordenCompraAdjuntoList) {
        for (OrdenCompraAdjunto ordenCompraAdjunto: ordenCompraAdjuntoList){
            OrdenCompraAdjunto optionalOrdeCompraAdjunto = ordenCompraAdjuntoRepository.findById(ordenCompraAdjunto.getId())
                    .orElseThrow(()-> new RuntimeException("No se encuentra este documento"));
            if (optionalOrdeCompraAdjunto.getEstado().equals(OrdenCompraAdjuntoEnum.APROBADA.getCodigo())){
                throw new RuntimeException("Los documentos ya fueron aprobados, no se pueden modificar");

            }
            optionalOrdeCompraAdjunto.setRutaAdjunto(ordenCompraAdjunto.getRutaAdjunto());
            optionalOrdeCompraAdjunto.setArchivoTipo(ordenCompraAdjunto.getArchivoTipo());
            optionalOrdeCompraAdjunto.setTipoDoc(ordenCompraAdjunto.getTipoDoc());
            optionalOrdeCompraAdjunto.setArchivoId(ordenCompraAdjunto.getArchivoId());
            optionalOrdeCompraAdjunto.setArchivoNombre(ordenCompraAdjunto.getArchivoNombre());
            optionalOrdeCompraAdjunto.setEstado(OrdenCompraAdjuntoEnum.PENDIENTE.getCodigo());

            ordenCompraAdjuntoRepository.save(optionalOrdeCompraAdjunto);
        }
        OrdenCompra ordenCompra = ordenCompraRepository.findByNumeroOrdenCompra(ordenCompraAdjuntoList.get(0).getNumeroOc());
        Usuario usuario = usuarioRepository.findByCodigoSap(ordenCompra.getCompradorUsuarioSap());
        notificacion.enviarCreacionDocAdjuntos(ordenCompra,usuario, "modificado");

        return "Actualización realizada con éxito!";
    }

    @Override
    public String updateStatus(List<OrdenCompraAdjunto> ordenCompraAdjuntoList) {
        for (OrdenCompraAdjunto ordenCompraAdjunto: ordenCompraAdjuntoList){
            OrdenCompraAdjunto optionalOrdeCompraAdjunto = ordenCompraAdjuntoRepository.findById(ordenCompraAdjunto.getId())
                    .orElseThrow(()-> new RuntimeException("No se encuentra este documento"));
            if (optionalOrdeCompraAdjunto.getEstado().equals(OrdenCompraAdjuntoEnum.PENDIENTE.getCodigo())) {
                if (ordenCompraAdjunto.getEstado().equals(OrdenCompraAdjuntoEnum.APROBADA.getCodigo())){
                    optionalOrdeCompraAdjunto.setEstado(OrdenCompraAdjuntoEnum.APROBADA.getCodigo());
                }else if (ordenCompraAdjunto.getEstado().equals(OrdenCompraAdjuntoEnum.RECHAZADA.getCodigo())){
                    optionalOrdeCompraAdjunto.setEstado(OrdenCompraAdjuntoEnum.RECHAZADA.getCodigo());
                }
                ordenCompraAdjuntoRepository.save(optionalOrdeCompraAdjunto);
            }else {
                throw new RuntimeException("Los documentos ya fueron aprobados y/o rechazados, no se pueden volver a aprobar o rechazar");
            }

            OrdenCompra ordenCompra = ordenCompraRepository.findByNumeroOrdenCompra(ordenCompraAdjuntoList.get(0).getNumeroOc());
            String tipo = ordenCompraAdjuntoList.get(0).getEstado().equals("APROBADA")? "aprobado": "rechazado";
            Proveedor proveedor = proveedorRepository.getProveedorByAcreedorCodigoSap(ordenCompra.getProveedorCodigoSap());
            Usuario usuario = new Usuario();
            usuario.setEmail(proveedor.getEmail());
            usuario.setNombre(proveedor.getRazonSocial());
            usuario.setApellido("");
            notificacion.enviarCreacionDocAdjuntos(ordenCompra,usuario, tipo);



        }
        return "Estado actualizado correctamente";
    }
}
