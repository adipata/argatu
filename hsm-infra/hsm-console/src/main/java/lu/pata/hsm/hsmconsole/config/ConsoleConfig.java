package lu.pata.hsm.hsmconsole.config;


import lu.pata.hsm.hsmconsole.remote.CommandDispatcher;
import lu.pata.hsm.hsmconsole.remote.JmxDispatcher;
import lu.pata.hsm.hsmconsole.remote.RestDispatcher;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class ConsoleConfig {
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

}
