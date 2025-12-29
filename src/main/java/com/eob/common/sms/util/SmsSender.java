package com.eob.common.sms.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoEmptyResponseException;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.exception.NurigoUnknownException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Component
public class SmsSender {

    private final DefaultMessageService messageService;

    // 생성자에서 Solapi SDK 초기화
    public SmsSender(@Value("${coolsms.api-key}") String apiKey, @Value("${coolsms.api-secret}") String apiSecret) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.solapi.com");
    }

    /**
     * 문자 발송
     */
    public void sendSms(String to, String from, String text) {
        Message message = new Message();
        message.setTo(to); // 수신번호
        message.setFrom(from); // 발신번호 (Solapi 콘솔에 등록된 번호)
        message.setText(text); // 문자 내용

        try {
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException | NurigoEmptyResponseException | NurigoUnknownException e) {
            e.printStackTrace();
        }
    }

}
