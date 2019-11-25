package lu.pata.hsm.hsmconsole.config;


import iaik.pkcs.pkcs11.wrapper.PKCS11Exception;
import lu.pata.hsm.hsmconsole.remote.CommandDispatcher;
import lu.pata.hsm.hsmconsole.remote.JmxDispatcher;
import lu.pata.hsm.hsmconsole.remote.RestDispatcher;
import lu.pata.hsm.hsmlib.hsm.Hsm;
import lu.pata.hsm.hsmlib.hsm.PkcsManager;
import lu.pata.hsm.hsmlib.hsm.Tools;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;

@Configuration
public class ConsoleConfig {
    private Logger log= LoggerFactory.getLogger(ConsoleConfig.class);

    @Bean
    public OutputPrinter outputPrinter(@Lazy Terminal terminal) {
        return new OutputPrinter(terminal);
    }

    @Bean
    public InputReader inputReader(@Lazy LineReader lineReader) {
        return new InputReader(lineReader);
    }

    @Value("${interface.type}")
    private String interfaceType;
    @Bean
    public CommandDispatcher getCommandDispatcher(){
        if(interfaceType.equals("local"))
            return new JmxDispatcher();
        else
            return new RestDispatcher();
    }

    @Value("${lib.hsm}")
    private String libHsm;
    @Value("${lib.wrapper}")
    private String libWrapper;
    @Bean(destroyMethod = "close")
    public Hsm getHsm() {
        return new Hsm(libHsm, libWrapper);
    }

    @Bean
    public Tools getTools(){
        return new Tools();
    }

    @Bean
    public PkcsManager getPkcsManager(Hsm hsm,Tools tools){
        return new PkcsManager(hsm,tools);
    }
}
