package com.incloud.hcp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LicitacionProveedorPKDTO implements Serializable {
    private Integer idLicitacion;
    private Integer idProveedor;
}
