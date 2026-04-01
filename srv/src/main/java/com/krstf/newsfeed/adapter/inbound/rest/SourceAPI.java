package com.krstf.newsfeed.adapter.inbound.rest;

import com.krstf.newsfeed.port.inbound.AddSourceUseCase;
import com.krstf.newsfeed.port.inbound.GetSourcesUseCase;
import com.krstf.newsfeed.port.inbound.dto.CreateSourceRequest;
import com.krstf.newsfeed.port.inbound.dto.SourceDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SourceAPI {

    private final GetSourcesUseCase getSourcesUseCase;
    private final AddSourceUseCase addSourceUseCase;
    private final CurrentUser currentUser;

    public SourceAPI(GetSourcesUseCase getSourcesUseCase, AddSourceUseCase addSourceUseCase, CurrentUser currentUser) {
        this.getSourcesUseCase = getSourcesUseCase;
        this.addSourceUseCase = addSourceUseCase;
        this.currentUser = currentUser;
    }

    @GetMapping("/sources")
    public ResponseEntity<List<SourceDto>> getSources() {
        return ResponseEntity.ok(getSourcesUseCase.getSources(currentUser.getUserId()));
    }

    @PostMapping("/sources")
    public ResponseEntity<SourceDto> addSource(@Valid @RequestBody CreateSourceRequest request) {
        return ResponseEntity.status(201).body(addSourceUseCase.addSource(request, currentUser.getUserId()));
    }
}
