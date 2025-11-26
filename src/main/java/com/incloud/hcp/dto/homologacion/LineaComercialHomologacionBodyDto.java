package com.incloud.hcp.dto.homologacion;

import com.incloud.hcp.bean.UserSessionFront;

import java.util.List;

public class LineaComercialHomologacionBodyDto {

    List<LineaComercialHomologacionDto> list;
    UserSessionFront userFront;

    public List<LineaComercialHomologacionDto> getList() {
        return list;
    }

    public void setList(List<LineaComercialHomologacionDto> list) {
        this.list = list;
    }

    public UserSessionFront getUserFront() {
        return userFront;
    }

    public void setUserFront(UserSessionFront userFront) {
        this.userFront = userFront;
    }
}
