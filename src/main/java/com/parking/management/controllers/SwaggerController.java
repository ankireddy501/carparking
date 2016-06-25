package com.parking.management.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by VST on 18-06-2016.
 */
@Controller
public class SwaggerController {

    @RequestMapping("/swagger")
    public String greeting() {
        return "index";
    }
}
