package es.caib.helium.logic.config;

import es.caib.helium.logic.intf.util.GlobalProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@RequiredArgsConstructor
@Configuration
public class MailConfig {

    private final GlobalProperties globalProperties;

    @Value("${es.caib.helium.mail.host}")
    private String host;
    @Value("${es.caib.helium.mail.port}")
    private Integer port;
    @Value("${es.caib.helium.mail.username}")
    private String username;
    @Value("${es.caib.helium.mail.password}")
    private String password;
    @Value("${es.caib.helium.mail.protocol:smtp}")
    private String protocol;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setProtocol(protocol);

        // Altres propietats
        Properties props = mailSender.getJavaMailProperties();

        Properties mmailProperties = globalProperties.findByPrefix("es.caib.helium.mail.property");
        mmailProperties.entrySet().forEach(p -> {
            props.put(
                    ((String) p.getKey()).substring("es.caib.helium.mail.property.".length()),
                    p.getValue());
        });

        return mailSender;
    }

}
