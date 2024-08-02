package goormthon.team28.startup_valley.service;

import goormthon.team28.startup_valley.constants.Constants;
import goormthon.team28.startup_valley.domain.User;
import goormthon.team28.startup_valley.exception.CommonException;
import goormthon.team28.startup_valley.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

//    @Async
    public boolean sendMail(User user, String targetEmail, String body) {

        log.info("이메일 전송 진입");

        // subject: 스타트업 밸리 문의 항목
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(Constants.EMAIL_TARGET);
        message.setSubject(Constants.EMAIL_SUBJECT);
        message.setText(body + "\n\n" +
                "문의한 사용자 이메일 : " + targetEmail +
                "\n문의한 사용자 정보 : " + user.toString()
        );

        try {
            log.info("이메일 전송 중");
            javaMailSender.send(message);
        } catch (MailException e){
            log.info("이메일 터짐");
            throw new CommonException(ErrorCode.MAIL_ERROR);
        }

        log.info("이메일 전송 완료");

        return true;
    }
}
