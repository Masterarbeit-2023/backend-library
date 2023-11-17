package io.github.masterarbeit.email;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import org.springframework.stereotype.Service;

@Service
public class AwsEmailService {
    private final AmazonSimpleEmailService sesClient;

    public AwsEmailService() {
        this.sesClient = AmazonSimpleEmailServiceClientBuilder.standard().build();
    }

    public void sendEmail(String from, String to, String subject, String body) {
        try {
            SendEmailRequest request = new SendEmailRequest()
                    .withSource(from)
                    .withDestination(new Destination().withToAddresses(to))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withText(new Content()
                                            .withCharset("UTF-8").withData(body)))
                            .withSubject(new Content()
                                    .withCharset("UTF-8").withData(subject)));

            sesClient.sendEmail(request);
        } catch (Exception ignored) {
        }
    }
}
