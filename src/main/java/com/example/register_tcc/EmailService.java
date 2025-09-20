package com.example.register_tcc;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    public void enviarEmail(String para, String assunto, String texto) {
        Email from = new Email("contato@predicaodiabete.xyz"); // Mude para o seu e-mail de remetente autenticado
        Email to = new Email(para);
        Content content = new Content("text/plain", texto);
        Mail mail = new Mail(from, assunto, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
