package com.incloud.hcp.rest;

import com.incloud.hcp.repository.PreRegistroProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pre-registro-proveedor")
public class Pre {

    @Autowired
    PreRegistroProveedorRepository  preRegistroProveedorRepository;


    @GetMapping("/last")
    public String obtenerPreProveedorLast(){
        return this.preRegistroProveedorRepository.findTop1ByOrderByIdRegistroDesc().getRuc();
    }
}
