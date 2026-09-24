package com.example.errorprocessor.controller;

import com.example.errorprocessor.entity.ErrorLog;
import com.example.errorprocessor.exception.ErrorNotFoundException;
import com.example.errorprocessor.repository.ErrorLogRepository;
import com.example.errorprocessor.service.ErrorStatisticsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/errors")
public class ErrorLogController {

    private final ErrorStatisticsService errorStatisticsService;
    private final ErrorLogRepository errorLogRepository;

    public ErrorLogController(
            ErrorLogRepository errorLogRepository,
            ErrorStatisticsService errorStatisticsService) {

        this.errorLogRepository = errorLogRepository;
        this.errorStatisticsService = errorStatisticsService;
    }

    @GetMapping
    public Page<ErrorLog> getAllErrors(Pageable pageable) {

        return errorLogRepository.findAll(pageable);
    }
    @GetMapping("/stats")
    public Map<String, Object> getStatistics() {
        return errorStatisticsService.getStatistics();
    }
    @GetMapping("/{id}")
    public ErrorLog getErrorById(@PathVariable Long id) {
        return errorLogRepository.findById(id)
                .orElseThrow(() -> new ErrorNotFoundException(
                        "Error not found with id: " + id
                ));
    }
    @GetMapping("/status/{statusCode}")
    public List<ErrorLog> getErrorByStatusCode(@PathVariable int statusCode){
        return errorLogRepository.findByStatusCode(statusCode);
    }
    @GetMapping("/type/{errorType}")
    public List<ErrorLog> getErrorsByType(
            @PathVariable String errorType) {

        return errorLogRepository.findByErrorType(errorType);
    }
    @GetMapping("/filter")
    public List<ErrorLog> getErrorsBetween(
            @RequestParam String from,
            @RequestParam String to) {

        return errorLogRepository.findByTimestampBetween(from, to);
    }
}