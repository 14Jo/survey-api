package com.example.surveyapi.domain.survey.domain.question;

import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "survey_id", nullable = false)
    private Long surveyId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private QuestionType type = QuestionType.SINGLE_CHOICE;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "is_required", nullable = false)
    private boolean isRequired = false;

    public static Question create(
        Long surveyId,
        String content,
        QuestionType type,
        int displayOrder,
        boolean isRequired
    ) {
        Question question = new Question();

        question.surveyId = surveyId;
        question.content = content;
        question.type = type;
        question.displayOrder = displayOrder;
        question.isRequired = isRequired;

        return question;
    }
}
