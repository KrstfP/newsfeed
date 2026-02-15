package com.krstf.newsfeed.port.inbound.dto;

import com.krstf.newsfeed.domain.models.AnalysisRequest;
import org.springframework.stereotype.Service;

@Service
public interface RequestMapper {
    RequestDto toDto(AnalysisRequest analysisRequest);
}
