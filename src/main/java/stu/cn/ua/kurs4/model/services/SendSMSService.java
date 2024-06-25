package stu.cn.ua.kurs4.model.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class SendSMSService {

    @Autowired
    private Environment env;

    public void sendSMS(String to, String body){
        Twilio.init(env.getProperty("ACCOUNT_SID"), env.getProperty("AUTH_TOKEN"));

        Message message = Message
                .creator(
                        new PhoneNumber(to),
                        new PhoneNumber(env.getProperty("from")),
                        body
                )
                .create();

        System.out.println(message.getSid());
    }
}
