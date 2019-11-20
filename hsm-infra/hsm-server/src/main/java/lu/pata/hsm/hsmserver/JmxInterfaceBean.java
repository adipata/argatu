package lu.pata.hsm.hsmserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import lu.pata.hsm.hsmlib.ServerCommand;
import lu.pata.hsm.hsmlib.ServerCommandResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@ManagedResource
public class JmxInterfaceBean {
    private ObjectMapper json=new ObjectMapper();

    @Autowired
    private CommandProcessor commandProcessor;

    @ManagedOperation
    public String cmd(String command) throws IOException {
        ServerCommand cmd=json.readValue(command, ServerCommand.class);
        ServerCommandResponse response=commandProcessor.process(cmd);
        return json.writeValueAsString(response);
    }
}
