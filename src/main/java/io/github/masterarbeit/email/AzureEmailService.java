package io.github.masterarbeit.email;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AzureEmailService {

    private final EmailClient emailClient;

    public AzureEmailService(@Value("${azure.communication.service.connection-string}") String connectionString) {
        this.emailClient = new EmailClientBuilder().connectionString(connectionString).buildClient();
    }

    public void sendEmail(String from, String to, String subject, String body) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setSenderAddress(from);
        emailMessage.setSubject(subject);
        emailMessage.setToRecipients(to);
        emailMessage.setBodyPlainText(body);

        // Send the email
        emailClient.beginSend(emailMessage);
    }
}
