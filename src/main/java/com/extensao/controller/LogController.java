package com.extensao.controller;

import com.extensao.dto.LogDtos.LogResponse;
import com.extensao.service.LogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping
    public List<LogResponse> listar() {
        return logService.listar().stream().map(LogResponse::from).toList();
    }
}
