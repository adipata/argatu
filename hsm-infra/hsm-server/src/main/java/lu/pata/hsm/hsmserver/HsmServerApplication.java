package lu.pata.hsm.hsmserver;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableMBeanExport;

import java.security.Security;

@SpringBootApplication
@EnableMBeanExport
public class HsmServerApplication {

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		SpringApplication.run(HsmServerApplication.class, args);
	}

}
