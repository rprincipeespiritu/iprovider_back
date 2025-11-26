package com.incloud.hcp.facade.impl;

import com.incloud.hcp.domain.Homologacion;
import com.incloud.hcp.domain.HomologacionRespuesta;
import com.incloud.hcp.dto.HomologacionDto;
import com.incloud.hcp.dto.HomologacionRespuestaDto;
import com.incloud.hcp.dto.TipoHomologacionDto;
import com.incloud.hcp.enums.TipoHomologacionEnum;
import com.incloud.hcp.facade.HomologacionFacade;
import com.incloud.hcp.myibatis.mapper.HomologacionMapper;
import com.incloud.hcp.populate.Populater;
import com.incloud.hcp.service.HomologacionRespuestaService;
import com.incloud.hcp.service.HomologacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class HomologacionFacadeImpl implements HomologacionFacade {

    private HomologacionService homologacionService;
    private HomologacionRespuestaService homologacionRespuestaService;
    private Populater<Homologacion, HomologacionDto> homologacionPopulate;
    private Populater<HomologacionRespuesta, HomologacionRespuestaDto> respuestaPopulate;
    private HomologacionMapper homologacionMapper;

    @Autowired
    public void setHomologacionMapper(HomologacionMapper homologacionMapper) {
        this.homologacionMapper = homologacionMapper;
    }

    @Autowired
    @Qualifier(value = "homologacionPopulate")
    public void setHomologacionPopulate(Populater<Homologacion, HomologacionDto> homologacionPopulate) {
        this.homologacionPopulate = homologacionPopulate;
    }

    @Autowired
    @Qualifier(value = "homologacionRespuestaPopulate")
    public void setRespuestaPopulate(Populater<HomologacionRespuesta, HomologacionRespuestaDto> respuestaPopulate) {
        this.respuestaPopulate = respuestaPopulate;
    }

    @Autowired
    public void setHomologacionService(HomologacionService homologacionService) {
        this.homologacionService = homologacionService;
    }

    @Autowired
    public void setHomologacionRespuestaService(HomologacionRespuestaService homologacionRespuestaService) {
        this.homologacionRespuestaService = homologacionRespuestaService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public HomologacionDto guardar(HomologacionDto dto) {

        Optional<Homologacion> oHomologacion = Optional.ofNullable(dto.getIdHomologacion())
                .filter(id -> id > 0)
                .map(this.homologacionService::getHomologacionById);
        Homologacion homologacion;
        if (oHomologacion.isPresent()) {
            homologacion = oHomologacion.get();
            homologacion.setPregunta(dto.getPregunta());
            homologacion.setEstado(dto.getEstado());
            homologacion.setPeso(dto.getPeso());
            homologacion.setFechaModificacion(new Date());
            homologacion.setUsuarioModificacion(1);
            if(!dto.getTipo().isEmpty()){
                if(dto.getTipo().get(0).isSeleccionado()
                        && dto.getTipo().get(0).getDescripcion().equals(TipoHomologacionEnum.Adjunto)){
                    homologacion.setIndAdjunto("1");
                } else if(dto.getTipo().get(1).isSeleccionado()
                        && dto.getTipo().get(1).getDescripcion().equals(TipoHomologacionEnum.Pregunta)){
                    homologacion.setIndAdjunto("0");
                }
            }
            homologacion = this.homologacionService.guardar(homologacion);
            //Obtener respuestas existentes
            List<HomologacionRespuesta> listRespuesta = this.homologacionRespuestaService.getHomologacionRespuestaByHomologacion(homologacion.getIdHomologacion());
            int indiceNroOrden = 0;
            if(!listRespuesta.isEmpty()) {
                //ordeno listo por numero de orden
                listRespuesta = listRespuesta
                        .stream()
                        .sorted(Comparator.comparing(HomologacionRespuesta::getNroOrden))
                        .collect(Collectors.toList());
                //obtengo la respuesta con el numero de orden mayor
                Optional<HomologacionRespuesta> homologacionMayorOrden =
                        Optional.ofNullable(listRespuesta
                                .stream()
                                .max(Comparator.comparing(HomologacionRespuesta::getNroOrden))
                                .orElse(null));
                indiceNroOrden = Integer.parseInt(homologacionMayorOrden.get().getNroOrden()) + 1;
            }
            if (dto.getRespuestas() != null && !dto.getRespuestas().isEmpty()) {
                List<HomologacionRespuesta> list = dto.getRespuestas().stream()
                        .filter(resp->resp.getIdHomologacionRespuesta() == null)
                        .map(this.respuestaPopulate::toEntity)
                        .collect(Collectors.toList());
                indiceNroOrden = indiceNroOrden == 0? 1 : indiceNroOrden;
                int idRespuestas = homologacionMapper.getIdSequenceRespuesta();
                for (int i = 0; i < list.size(); i++) {
                    HomologacionRespuesta respuesta = list.get(i);
                    respuesta.setFechaCreacion(new Date());
                    respuesta.setUsuarioCreacion(1);
                    respuesta.setNroOrden((i + indiceNroOrden) + "");
                    respuesta.setHomologacion(homologacion);
                    respuesta.setIdHomologacionRespuesta(idRespuestas);
                    listRespuesta.add(this.homologacionRespuestaService.guardar(respuesta));
                    idRespuestas++;
                }
            }
            List<HomologacionRespuestaDto> listRespuestaDto = new ArrayList<>();
            Optional.ofNullable(listRespuesta)
                    .ifPresent(list -> list.stream().map(this.respuestaPopulate::toDto).forEach(listRespuestaDto::add));
            dto = this.homologacionPopulate.toDto(homologacion);
            dto.setRespuestas(listRespuestaDto);
            return dto;
        } else {
            homologacion = homologacionPopulate.toEntity(dto);
            homologacion.setEstado("1");
            homologacion.setFechaCreacion(new Date());
            homologacion.setUsuarioCreacion(1);
            homologacion.setIdHomologacion(homologacionMapper.getIdSequence());
            if(!dto.getTipo().isEmpty()){
                if(dto.getTipo().get(0).isSeleccionado()
                        && dto.getTipo().get(0).getDescripcion().equals(TipoHomologacionEnum.Adjunto)){
                    homologacion.setIndAdjunto("1");
                } else if(dto.getTipo().get(1).isSeleccionado()
                        && dto.getTipo().get(1).getDescripcion().equals(TipoHomologacionEnum.Pregunta)){
                    homologacion.setIndAdjunto("0");
                }
            }
            homologacion = this.homologacionService.guardar(homologacion);
            List<HomologacionRespuestaDto> listDto = new ArrayList<>();
            if (dto.getRespuestas() != null && !dto.getRespuestas().isEmpty()) {
                List<HomologacionRespuesta> list = dto.getRespuestas().stream()
                        .map(this.respuestaPopulate::toEntity)
                        .collect(Collectors.toList());
                int idRespuestas = homologacionMapper.getIdSequenceRespuesta();
                for (int i = 0; i < list.size(); i++) {
                    HomologacionRespuesta respuesta = list.get(i);
                    respuesta.setFechaCreacion(new Date());
                    respuesta.setUsuarioCreacion(1);
                    respuesta.setNroOrden((i + 1) + "");
                    respuesta.setHomologacion(homologacion);
                    respuesta.setIdHomologacionRespuesta(idRespuestas);
                    listDto.add(this.respuestaPopulate.toDto(this.homologacionRespuestaService.guardar(respuesta)));
                    idRespuestas++;
                }
            }
            dto = this.homologacionPopulate.toDto(homologacion);
            dto.setRespuestas(listDto);
            return dto;
        }
    }

    @Override
    public List<HomologacionDto> getListAll() {
        List<HomologacionDto> listDto = new ArrayList<>();

        Optional.ofNullable(this.homologacionService.getAll())
                .ifPresent(list -> list.stream()
                        .map(this.homologacionPopulate::toDto)
                        .forEach(homologaciondto -> {

                            List<HomologacionRespuestaDto> listRptaDto = new ArrayList<>();
                            Optional.ofNullable(this.homologacionRespuestaService.getHomologacionRespuestaByHomologacion(homologaciondto.getIdHomologacion()))
                                    .ifPresent(listRpta -> listRpta.stream()
                                            .map(this.respuestaPopulate::toDto)
                                            .forEach(listRptaDto::add));

                            homologaciondto.setRespuestas(listRptaDto);
                            listDto.add(homologaciondto);
                        }));

        return listDto;
    }

    @Override
    public HomologacionDto getHomologacionDto(Integer idHomologacion) {
        return Optional.ofNullable(this.homologacionService.getHomologacionById(idHomologacion))
                .map(this.homologacionPopulate::toDto)
                .map(dto -> {

                    List<HomologacionRespuestaDto> listRptaDto = new ArrayList<>();
                    Optional.ofNullable(this.homologacionRespuestaService.getHomologacionRespuestaByHomologacion(dto.getIdHomologacion()))
                            .ifPresent(listRpta -> listRpta.stream()
                                    .map(this.respuestaPopulate::toDto)
                                    .forEach(listRptaDto::add));

                    dto.setRespuestas(listRptaDto);
                    return dto;
                }).orElse(this.homologacionPopulate.toDto(new Homologacion()));
    }
}
