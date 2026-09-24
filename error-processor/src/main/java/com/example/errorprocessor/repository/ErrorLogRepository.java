package com.example.errorprocessor.repository;

import com.example.errorprocessor.entity.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLog,Long> {
    List<ErrorLog> findByStatusCode(int statusCode);
    List<ErrorLog> findByErrorType(String errorType);
    long countByStatusCode(int statusCode);
    List<ErrorLog> findByTimestampBetween(
            String from,
            String to
    );
}
