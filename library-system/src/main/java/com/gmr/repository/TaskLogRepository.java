package com.gmr.repository;

import com.gmr.model.TaskLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface TaskLogRepository extends JpaRepository<TaskLog, Long> {
    // Check if user did specific task after a certain time (e.g., start of today)
    boolean existsByUserIdAndTaskIdAndCompletedAtAfter(Long userId, String taskId, LocalDateTime date);
}