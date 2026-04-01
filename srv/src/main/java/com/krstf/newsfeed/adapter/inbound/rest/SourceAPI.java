package com.krstf.newsfeed.adapter.inbound.rest;

import com.krstf.newsfeed.port.inbound.GetSourcesUseCase;
import com.krstf.newsfeed.port.inbound.dto.SourceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SourceAPI {

    private final GetSourcesUseCase getSourcesUseCase;

    public SourceAPI(GetSourcesUseCase getSourcesUseCase) {
        this.getSourcesUseCase = getSourcesUseCase;
    }

    @GetMapping("/sources")
    public ResponseEntity<List<SourceDto>> getSources() {
        return ResponseEntity.ok(getSourcesUseCase.getSources());
    }
}
