package lu.pata.hsm.hsmconsole;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.PromptProvider;

import java.io.IOException;
import java.security.Security;

@SpringBootApplication
public class HsmConsoleApplication {

    public static void main(String[] args) {
        //Security.addProvider(new BouncyCastleProvider());
        SpringApplication.run(HsmConsoleApplication.class, args);
    }

    @Bean
    public PromptProvider myPromptProvider() {
        return () -> new AttributedString("hsm:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }
}
