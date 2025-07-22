package com.example.surveyapi.domain.survey.domain.choice;

import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Choice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long choiceId;

    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    public static Choice create(
        Long questionId,
        String content,
        int displayOrder
    ) {
        Choice choice = new Choice();

        choice.questionId = questionId;
        choice.content = content;
        choice.displayOrder = displayOrder;

        return choice;
    }
} 