package com.incloud.hcp.rest;

import com.incloud.hcp.bean.UserSession;
import com.incloud.hcp.rest._framework.AppRest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/current-session")
public class CurrentSessionRest extends AppRest {


    @GetMapping(value = "/logged-user", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserSession> getCurrentSession() {
        UserSession userSession = this.getUserSession();
        return new ResponseEntity<>(userSession, HttpStatus.OK);
    }
}
