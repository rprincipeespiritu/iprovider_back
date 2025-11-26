package com.incloud.hcp.service.impl;

import com.incloud.hcp.domain.*;
import com.incloud.hcp.dto.AccionistasAsociadosDto;
import com.incloud.hcp.dto.RegistroDeclaracionJuradaDto;
import com.incloud.hcp.dto.RegistroDeclaracionJuradaPJDto;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.repository.*;
import com.incloud.hcp.service.BancoService;
import com.incloud.hcp.service.ProveedorDeclaracionJuradaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProveedorDeclaracionJuradaServiceImpl implements ProveedorDeclaracionJuradaService {

    private ProveedorDeclaracionJuradaRepository proveedorDeclaracionJuradaRepository;
    private ProveedorAntecedenteRepository proveedorAntecedenteRepository;
    private ProveedorRepresentanteLegalRepository proveedorRepresentanteLegalRepository;
    private ProveedorAntecedenteRepresentanteLegalRepository proveedorAntecedenteRepresentanteLegalRepository;
    private ProveedorAccionistasAsociadosRepository proveedorAccionistasAsociadosRepository;
    private ProveedorPepRepository proveedorPepRepository;
    private ProveedorParientePepRepository proveedorParientePepRepository;
    private ProveedorAccionistaPepRepository proveedorAccionistaPepRepository;
    private ProveedorAccionistaParientePepRepository proveedorAccionistaParientePepRepository;
    private ProveedorRepository proveedorRepository;

    @Autowired
    public void setProveedorDeclaracionJuradaRepository(ProveedorDeclaracionJuradaRepository proveedorDeclaracionJuradaRepository,
                                                        ProveedorAntecedenteRepository proveedorAntecedenteRepository,
                                                        ProveedorRepresentanteLegalRepository proveedorRepresentanteLegalRepository,
                                                        ProveedorAntecedenteRepresentanteLegalRepository proveedorAntecedenteRepresentanteLegalRepository,
                                                        ProveedorAccionistasAsociadosRepository proveedorAccionistasAsociadosRepository,
                                                        ProveedorPepRepository proveedorPepRepository,
                                                        ProveedorParientePepRepository proveedorParientePepRepository,
                                                        ProveedorAccionistaPepRepository proveedorAccionistaPepRepository,
                                                        ProveedorAccionistaParientePepRepository proveedorAccionistaParientePepRepository,
                                                        ProveedorRepository proveedorRepository) {
        this.proveedorDeclaracionJuradaRepository = proveedorDeclaracionJuradaRepository;
        this.proveedorAntecedenteRepository = proveedorAntecedenteRepository;
        this.proveedorRepresentanteLegalRepository = proveedorRepresentanteLegalRepository;
        this.proveedorAntecedenteRepresentanteLegalRepository = proveedorAntecedenteRepresentanteLegalRepository;
        this.proveedorAccionistasAsociadosRepository = proveedorAccionistasAsociadosRepository;
        this.proveedorPepRepository =proveedorPepRepository;
        this.proveedorParientePepRepository=proveedorParientePepRepository;
        this.proveedorAccionistaPepRepository= proveedorAccionistaPepRepository;
        this.proveedorAccionistaParientePepRepository =proveedorAccionistaParientePepRepository;
        this.proveedorRepository = proveedorRepository;
    }


    @Override
    @Transactional
    public ProveedorDeclaracionJurada create(RegistroDeclaracionJuradaDto bean) throws Exception {

        ProveedorDeclaracionJurada declaracionJurada1;
        try {
            List<ProveedorDeclaracionJurada> declaracionJuradaList =
                    proveedorDeclaracionJuradaRepository.findIdProveedor(bean.getProveedorDeclaracionJurada().getProveedor().getIdProveedor());

            if(declaracionJuradaList != null) {
                eliminacionProveedorAll(declaracionJuradaList);
                bean.getProveedorDeclaracionJurada().setIdDeclaracionJurada(null);
            }
            declaracionJurada1= proveedorDeclaracionJuradaRepository.save(bean.getProveedorDeclaracionJurada());
            //CREAR ANTECEDENTES PERSONA NATURAL
            if(bean.getProveedorAntecedenteList().size() > 0){
                bean.getProveedorAntecedenteList().forEach(item->{
                    item.setProveedorDeclaracionJurada(declaracionJurada1);
                    proveedorAntecedenteRepository.save(item);
                });
            }
            //CREAR PEP Y PEP PARIENTE
            if(bean.getProveedorPepList().size() > 0){
                bean.getProveedorPepList().forEach(item->{
                    item.setProveedorDeclaracionJurada(declaracionJurada1);
                    proveedorPepRepository.save(item);
                });
            }
            if(bean.getProveedorParientePepList().size() > 0){
                bean.getProveedorParientePepList().forEach(item->{
                    item.setProveedorDeclaracionJurada(declaracionJurada1);
                    proveedorParientePepRepository.save(item);
                });
            }


        }catch (Exception ex){
            ex.printStackTrace();
            throw new PortalException("Error => "+ ex.getMessage());
        }
        return declaracionJurada1;
    }

    private void eliminacionProveedorAll(List<ProveedorDeclaracionJurada> declaracionJuradaList) {
        declaracionJuradaList.forEach(a -> {

            List<ProveedorRepresentanteLegal> listPRL = proveedorRepresentanteLegalRepository.findAllByIdDeclaracionJurada(a.getIdDeclaracionJurada());
            listPRL.forEach(b -> {
                proveedorAntecedenteRepresentanteLegalRepository.deleteProveedorAntecRepLegal(b.getIdProveedorRepresentanteLegal());
                proveedorRepresentanteLegalRepository.deleteProveedorRepLegal(a.getIdDeclaracionJurada());
            });
            List<ProveedorAccionistasAsociados> listAA = proveedorAccionistasAsociadosRepository.findIdDeclaracionJurada(a.getIdDeclaracionJurada());
            listAA.forEach(c -> {
                proveedorAccionistaParientePepRepository.deleteProveedorAcciAsoc(c.getIdProveedorAccionistasAsociados());
                proveedorAccionistaPepRepository.deleteProveedorAcciAsoc(c.getIdProveedorAccionistasAsociados());
                proveedorAccionistasAsociadosRepository.deleteById(c.getIdProveedorAccionistasAsociados());
            });
            proveedorAntecedenteRepository.deleteProvAntecedentes(a.getIdDeclaracionJurada());
            proveedorParientePepRepository.deleteProvParientePep(a.getIdDeclaracionJurada());
            proveedorPepRepository.deleteProvPep(a.getIdDeclaracionJurada());
            //proveedorDeclaracionJuradaRepository.deleteProvDeclJuradaByIdProveedor(a.getProveedor().getIdProveedor());
            proveedorDeclaracionJuradaRepository.deleteById(a.getIdDeclaracionJurada());
        });
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor=Exception.class)
    public ProveedorDeclaracionJurada personaJuridicaCreate(RegistroDeclaracionJuradaPJDto bean) throws Exception{
        ProveedorDeclaracionJurada declaracionJurada1;

        try {
            List<ProveedorDeclaracionJurada> declaracionJuradaList =
                    proveedorDeclaracionJuradaRepository.findIdProveedor(bean.getProveedorDeclaracionJurada().getProveedor().getIdProveedor());

            if(declaracionJuradaList != null) {
                eliminacionProveedorAll(declaracionJuradaList);
                bean.getProveedorDeclaracionJurada().setIdDeclaracionJurada(null);
            }
            /*
            if (bean.getProveedorDeclaracionJurada().getDepartamentoOficina().getIdUbigeo() == null ||
                    bean.getProveedorDeclaracionJurada().getPaisOficina().getIdUbigeo() == null) {
                throw new PortalException("En caso de desear grabar parcialmente el Tab de Persona Jurídica, completar los campos obligatorios.");
            } else {

                declaracionJurada1 = proveedorDeclaracionJuradaRepository.save(bean.getProveedorDeclaracionJurada());
            }
            */
            // mizalo 17-02-2023
            declaracionJurada1 = proveedorDeclaracionJuradaRepository.save(bean.getProveedorDeclaracionJurada());

            /*Actualizamos datos de proveedor*/
//            if(bean.getProveedorDeclaracionJurada().getPais() != null)
//            {
//                Proveedor proveedor = bean.getProveedorDeclaracionJurada().getProveedor();
//                proveedor.setPais(bean.getProveedorDeclaracionJurada().getPais());
//                proveedor.setRegion(bean.getProveedorDeclaracionJurada().getDepartamento());
//                proveedor.setProvincia(bean.getProveedorDeclaracionJurada().getProvincia());
//                proveedor.setDistrito(bean.getProveedorDeclaracionJurada().getDistrito());
//
//                this.proveedorRepository.save(proveedor);
//            }

            // proveedor Antecedente Sociedad List
            if(declaracionJurada1.getIdDeclaracionJurada() != null){

                List<ProveedorAntecedente> proveedorAntecedentes = proveedorAntecedenteRepository.findProveedorDeclaracionJurada(declaracionJurada1.getIdDeclaracionJurada());
                if(proveedorAntecedentes != null)
                {
                    proveedorAntecedentes.forEach(item->{
                        proveedorAntecedenteRepository.delete(item);
                    });
                }

                ProveedorDeclaracionJurada finalDeclaracionJurada = declaracionJurada1;
                if( bean.getProveedorAntecedenteSociedadList() !=null){
                    bean.getProveedorAntecedenteSociedadList().forEach(item->{
                        item.setProveedorDeclaracionJurada(finalDeclaracionJurada);
                        item.setIdProveedorAntecedente(null);
                        proveedorAntecedenteRepository.save(item);
                    });
                }


                List<ProveedorPep> proveedorPeps = proveedorPepRepository.findIdDeclaracionJurada(declaracionJurada1.getIdDeclaracionJurada());
                if(proveedorPeps != null)
                {
                    proveedorPeps.forEach(item->{
                        proveedorPepRepository.delete(item);
                    });
                }

                //Registrar PEP Representante legal Persona Juridica
                if(bean.getProveedorPepRepresentanteList() != null){
                    bean.getProveedorPepRepresentanteList().forEach(item->{
                        item.setProveedorDeclaracionJurada(declaracionJurada1);
                        item.setIdProveedorPep(null);
                        proveedorPepRepository.save(item);
                    });
                }


                List<ProveedorParientePep> proveedorParientePeps = proveedorParientePepRepository.findIdDeclaracionJurada(declaracionJurada1.getIdDeclaracionJurada());
                if(proveedorParientePeps != null)
                {
                    proveedorParientePeps.forEach(item->{
                        proveedorParientePepRepository.delete(item);
                    });
                }

                // registrar ParientePep representante legal Sociedad
                if(bean.getProveedorParientePepRepresentanteList() != null){
                    bean.getProveedorParientePepRepresentanteList().forEach(item->{
                        item.setProveedorDeclaracionJurada(declaracionJurada1);
                        item.setIdProveedorParientePep(null);
                        proveedorParientePepRepository.save(item);
                    });
                }
            }

            // proveedor Representante Legal
            if(declaracionJurada1.getIdDeclaracionJurada() != null){
                ProveedorRepresentanteLegal proveedorRepresentanteLegal = bean.getProveedorRepresentanteLegal();
                proveedorRepresentanteLegal.setIdProveedorRepresentanteLegal(null);
                proveedorRepresentanteLegal.setProveedorDeclaracionJurada(declaracionJurada1);
                ProveedorRepresentanteLegal responseRepresentanteLegal= proveedorRepresentanteLegalRepository.save(proveedorRepresentanteLegal);


                List<ProveedorAntecedenteRepresentanteLegal> proveedorAntecedenteRepresentanteLegals = proveedorAntecedenteRepresentanteLegalRepository.findRepresentanteLegal(responseRepresentanteLegal.getIdProveedorRepresentanteLegal());
                if(proveedorAntecedenteRepresentanteLegals != null)
                {
                    proveedorAntecedenteRepresentanteLegals.forEach(item->{
                        proveedorAntecedenteRepresentanteLegalRepository.delete(item);
                    });
                }

                //Proveedor AntecedenteRepresentante Legal
                if(responseRepresentanteLegal.getIdProveedorRepresentanteLegal() != null){
                    if(bean.getProveedorAntecedenteRepresentanteList()!=null){
                        bean.getProveedorAntecedenteRepresentanteList().forEach(item->{
                            System.out.println("");
                            item.setProveedorRepresentanteLegal(responseRepresentanteLegal);
                            //item.setIdProveedorAntecedenteRepresentanteLegal(null);
                            proveedorAntecedenteRepresentanteLegalRepository.save(item);
                        });
                    }
                }
            }

            //Proveedor Accionistas Asociados
            if(declaracionJurada1.getIdDeclaracionJurada() != null){

                List<ProveedorAccionistasAsociados> proveedorAccionistasAsociadosList = proveedorAccionistasAsociadosRepository.findIdDeclaracionJurada(declaracionJurada1.getIdDeclaracionJurada());
                if(proveedorAccionistasAsociadosList != null)
                {
                    proveedorAccionistasAsociadosList.forEach(item->{

                        List<ProveedorAccionistasPep> proveedorAccionistasPepList = proveedorAccionistaPepRepository.findIdProveedorAccionista(item.getIdProveedorAccionistasAsociados());
                        if(proveedorAccionistasPepList != null)
                        {
                            proveedorAccionistasPepList.forEach(item2->{
                                proveedorAccionistaPepRepository.delete(item2);
                            });
                        }

                        List<ProveedorAccionistasParientePep> proveedorAccionistasParientePepList = proveedorAccionistaParientePepRepository.findIdProveedorAccionistaPariente(item.getIdProveedorAccionistasAsociados());
                        if(proveedorAccionistasParientePepList != null)
                        {
                            proveedorAccionistasParientePepList.forEach(item2->{
                                proveedorAccionistaParientePepRepository.delete(item2);
                            });
                        }

                        proveedorAccionistasAsociadosRepository.delete(item);
                    });
                }

                if(bean.getProveedorAccionistasAsociadosList() !=null){
                    bean.getProveedorAccionistasAsociadosList().forEach(item->{
                        if(item.getProveedorAccionistasAsociado() != null){
                            ProveedorAccionistasAsociados accionistasAsociados =item.getProveedorAccionistasAsociado();
                            accionistasAsociados.setProveedorDeclaracionJurada(declaracionJurada1);
                            accionistasAsociados.setIdProveedorAccionistasAsociados(null);
                            ProveedorAccionistasAsociados responseAccionistasAsociados = proveedorAccionistasAsociadosRepository.save(accionistasAsociados);


                            //CREAR PEP PARA ACCIONISTA
                            if(item.getProveedorAccionistasPepList()!= null){
                                item.getProveedorAccionistasPepList().forEach(accionistaPep->{
                                    accionistaPep.setProveedorAccionistasAsociados(responseAccionistasAsociados);
                                    accionistaPep.setIdProveedorAccionistasPep(null);
                                    proveedorAccionistaPepRepository.save(accionistaPep);
                                });
                            }
                            //CREAR PARIENTE PEP PARA ACCIONISTA
                            if(item.getProveedorAccionistasParientePepList() != null){
                                item.getProveedorAccionistasParientePepList().forEach(accionistaParientePep->{
                                    accionistaParientePep.setProveedorAccionistasAsociados(responseAccionistasAsociados);
                                    accionistaParientePep.setIdProveedorAccionistasParientePep(null);
                                    proveedorAccionistaParientePepRepository.save(accionistaParientePep);
                                });
                            }


                        }
                    });
                }
            }



        }catch (Exception ex){
            ex.printStackTrace();
            throw new PortalException("Error => "+ ex.getMessage());
        }
        return declaracionJurada1;
    }


    public RegistroDeclaracionJuradaPJDto consultarpj(Integer idProveedor)throws Exception{

        RegistroDeclaracionJuradaPJDto registroDeclaracionJuradaPJDto = new RegistroDeclaracionJuradaPJDto();
        List<ProveedorDeclaracionJurada> declaracionJurada = proveedorDeclaracionJuradaRepository.findIdProveedor(idProveedor);
        if(declaracionJurada.size() > 0){

            final ProveedorDeclaracionJurada[] declaracionJurada1 = {declaracionJurada.get(0)};
            final Integer[] id = {0};

            declaracionJurada.forEach(item->{
                if(item.getIdDeclaracionJurada()> id[0])
                {
                    id[0] = item.getIdDeclaracionJurada();
                    declaracionJurada1[0] = item;
                }
            });

            registroDeclaracionJuradaPJDto.setProveedorDeclaracionJurada(declaracionJurada1[declaracionJurada1.length-1]);
            //ANTECEDENTES
            registroDeclaracionJuradaPJDto.setProveedorAntecedenteSociedadList(this.proveedorAntecedenteRepository.findProveedorDeclaracionJurada(declaracionJurada1[0].getIdDeclaracionJurada()));
            //REPRESENTANTE LEGAL
            Optional<ProveedorRepresentanteLegal> representanteLegal =proveedorRepresentanteLegalRepository.findIdDeclaracionJurada(declaracionJurada1[0].getIdDeclaracionJurada());

            if(representanteLegal.isPresent()) {
                registroDeclaracionJuradaPJDto.setProveedorRepresentanteLegal(representanteLegal.get());

                //REPRESNETANTE ANTECENDENTES
                registroDeclaracionJuradaPJDto.setProveedorAntecedenteRepresentanteList(
                        proveedorAntecedenteRepresentanteLegalRepository.findRepresentanteLegal(representanteLegal.get().getIdProveedorRepresentanteLegal()));
                //PEP REGISTRO REPRESENTANTE
                registroDeclaracionJuradaPJDto.setProveedorPepRepresentanteList(
                        proveedorPepRepository.findIdDeclaracionJurada(declaracionJurada1[0].getIdDeclaracionJurada()));
                //PARIENTE PEP
                registroDeclaracionJuradaPJDto.setProveedorParientePepRepresentanteList(
                        proveedorParientePepRepository.findIdDeclaracionJurada(declaracionJurada1[0].getIdDeclaracionJurada()));
                //ACCIONISTAS
                List<ProveedorAccionistasAsociados> accionistasAsociados =
                        proveedorAccionistasAsociadosRepository.findIdDeclaracionJurada(declaracionJurada1[0].getIdDeclaracionJurada());
                List<AccionistasAsociadosDto> asociadosDtos = new ArrayList<>();
                accionistasAsociados.forEach(accionista->{
                    AccionistasAsociadosDto accionistasAsociadosDto = new AccionistasAsociadosDto();
                    accionistasAsociadosDto.setProveedorAccionistasAsociado(accionista);
                    //PARIENTE LIST
                    accionistasAsociadosDto.setProveedorAccionistasPepList(
                            proveedorAccionistaPepRepository.findIdProveedorAccionista(accionista.getIdProveedorAccionistasAsociados()));
                    //PARIENTE PEP LIST ACCIONISTA
                    accionistasAsociadosDto.setProveedorAccionistasParientePepList(
                            proveedorAccionistaParientePepRepository.findIdProveedorAccionistaPariente(accionista.getIdProveedorAccionistasAsociados()));
                    asociadosDtos.add(accionistasAsociadosDto);
                });
                registroDeclaracionJuradaPJDto.setProveedorAccionistasAsociadosList(asociadosDtos);
            }
            return registroDeclaracionJuradaPJDto;
        }
        return registroDeclaracionJuradaPJDto;
    }


    public RegistroDeclaracionJuradaDto consultarpn(Integer idProveedor) throws Exception{
        RegistroDeclaracionJuradaDto registroDeclaracionJuradaDto = new RegistroDeclaracionJuradaDto();
        List<ProveedorDeclaracionJurada> declaracionJurada = proveedorDeclaracionJuradaRepository.findIdProveedor(idProveedor);



        if(declaracionJurada.size()> 0){
            final ProveedorDeclaracionJurada[] declaracionJurada1 = {declaracionJurada.get(0)};
            final Integer[] id = {0};

            declaracionJurada.forEach(item->{
                if(item.getIdDeclaracionJurada()> id[0])
                {
                    id[0] = item.getIdDeclaracionJurada();
                    declaracionJurada1[0] = item;
                }
            });

            //declaracion jurada
            registroDeclaracionJuradaDto.setProveedorDeclaracionJurada(declaracionJurada1[declaracionJurada1.length-1]);
            //antecedentes
            registroDeclaracionJuradaDto.setProveedorAntecedenteList(
                    proveedorAntecedenteRepository.findProveedorDeclaracionJurada(declaracionJurada1[0].getIdDeclaracionJurada()));
            //proveedor Pep
            registroDeclaracionJuradaDto.setProveedorPepList(
                    proveedorPepRepository.findIdDeclaracionJurada(declaracionJurada1[0].getIdDeclaracionJurada()));
            // proveddor pariente pep
            registroDeclaracionJuradaDto.setProveedorParientePepList(
                    proveedorParientePepRepository.findIdDeclaracionJurada(declaracionJurada1[0].getIdDeclaracionJurada()));
            return  registroDeclaracionJuradaDto;
        }
        return  registroDeclaracionJuradaDto;
    }
}
