package com.ivanskyi.smartcareerseeker.controller;

import com.ivanskyi.smartcareerseeker.service.DjinniService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/djinni")
public class DjinniController {

    private final DjinniService djinniService;

    public DjinniController(DjinniService djinniService) {
        this.djinniService = djinniService;
    }

    @GetMapping("/apply-for-new-vacancies")
    public void applyForNewVacancies() {
        djinniService.startApplyingProcess();
    }
}
