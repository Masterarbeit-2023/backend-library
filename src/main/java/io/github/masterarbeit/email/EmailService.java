package io.github.masterarbeit.email;


import io.github.masterarbeit.generator.config.ProviderEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


    @Autowired
    AwsEmailService awsEmailService;

    @Autowired
    AzureEmailService azureEmailService;

    @Autowired
    GoogleEmailService googleEmailService;

    @Value("email-provider")
    ProviderEnum emailProvider;

    public void sendMail(String from, String to, String subject, String body) {
        switch (emailProvider) {
            case AZURE -> azureEmailService.sendEmail(from, to, subject, body);
            case AWS -> awsEmailService.sendEmail(from, to, subject, body);
            case GOOGLE -> googleEmailService.sendEmail(from, to, subject, body);
        }
    }
}
