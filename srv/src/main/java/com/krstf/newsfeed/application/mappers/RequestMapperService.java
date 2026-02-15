package com.krstf.newsfeed.application.mappers;

import com.krstf.newsfeed.domain.models.AnalysisRequest;
import com.krstf.newsfeed.port.inbound.dto.RequestDto;
import com.krstf.newsfeed.port.inbound.dto.RequestMapper;
import org.springframework.stereotype.Service;

@Service
public class RequestMapperService implements RequestMapper {
    @Override
    public RequestDto toDto(AnalysisRequest analysisRequest) {
        return new RequestDto(analysisRequest.getId().toString(), analysisRequest.getArticleId().toString(), analysisRequest.getStatus().name());
    }
}
