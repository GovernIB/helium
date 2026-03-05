package es.caib.helium.back.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HeliumController {

    @GetMapping
    public String getOneIndex() {
        return "index";
    }
    @GetMapping(value = {"/", "/index"})
    public String getIndex() {
        return "index";
    }


    @GetMapping("/menu")
    public String getMenu() {
        return "menu";
    }

}
