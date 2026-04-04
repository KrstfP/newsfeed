package com.krstf.newsfeed.port.outbound.ai;

import java.util.List;

public record ClusterSummary(String topic, String tldr, List<String> keypoints) {
}
