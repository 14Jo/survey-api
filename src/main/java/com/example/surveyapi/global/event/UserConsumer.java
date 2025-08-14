package com.example.surveyapi.global.event;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.UserRepository;
import com.example.surveyapi.global.constant.RabbitConst;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(
    queues = RabbitConst.QUEUE_NAME_USER
)
public class UserConsumer {

    private final UserRepository userRepository;

    @RabbitHandler
    public void handlePointIncrease(SurveyActivateEvent event){
        try{
            log.info("설문 종료 Id - {} : ", event.getSurveyId());

            if(!event.getSurveyStatus().equals(SurveyStatus.CLOSED)){
                return;
            }
            User user = userRepository.findByIdAndIsDeletedFalse(event.getCreatorID())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

            user.increasePoint();
            userRepository.save(user);
            log.info("포인트 상승");
        }catch (Exception e){
            log.error("포인트 상승 실패 , 등급 상승 실패 : {}", e.getMessage());
        }
    }
}
