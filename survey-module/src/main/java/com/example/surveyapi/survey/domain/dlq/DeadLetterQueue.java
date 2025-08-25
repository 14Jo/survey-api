package com.example.surveyapi.survey.domain.dlq;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dead_letter_queue")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeadLetterQueue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dlq_id")
    private Long dlqId;

    @Column(name = "queue_name", nullable = false)
    private String queueName;

    @Column(name = "routing_key", nullable = false)
    private String routingKey;

    @Column(name = "message_body", columnDefinition = "TEXT", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String messageBody;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "failed_at", nullable = false)
    private LocalDateTime failedAt;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    public static DeadLetterQueue create(
        String queueName,
        String routingKey,
        String messageBody,
        String errorMessage,
        Integer retryCount
    ) {
        DeadLetterQueue dlq = new DeadLetterQueue();
        dlq.queueName = queueName;
        dlq.routingKey = routingKey;
        dlq.messageBody = messageBody;
        dlq.errorMessage = errorMessage;
        dlq.failedAt = LocalDateTime.now();
        dlq.retryCount = retryCount;
        return dlq;
    }
}