package com.example.errorprocessor.service;

import com.example.errorprocessor.repository.ErrorLogRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ErrorStatisticsService {

    private final ErrorLogRepository errorLogRepository;

    public ErrorStatisticsService(ErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
    }

    public Map<String, Object> getStatistics() {

        long totalErrors = errorLogRepository.count();

        Map<String, Long> errorsByStatus = new LinkedHashMap<>();

        errorsByStatus.put(
                "400",
                errorLogRepository.countByStatusCode(400)
        );

        errorsByStatus.put(
                "404",
                errorLogRepository.countByStatusCode(404)
        );

        errorsByStatus.put(
                "500",
                errorLogRepository.countByStatusCode(500)
        );

        Map<String, Object> statistics = new LinkedHashMap<>();

        statistics.put("totalErrors", totalErrors);
        statistics.put("errorsByStatus", errorsByStatus);

        return statistics;
    }
}