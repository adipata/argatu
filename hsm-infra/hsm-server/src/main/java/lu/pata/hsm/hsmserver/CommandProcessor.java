package lu.pata.hsm.hsmserver;

import lu.pata.hsm.hsmlib.ServerCommand;
import lu.pata.hsm.hsmlib.ServerCommandResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class CommandProcessor {
    Logger log= LoggerFactory.getLogger(CommandProcessor.class);

    public ServerCommandResponse process (ServerCommand command){
        ServerCommandResponse response=new ServerCommandResponse();

        try {
            switch (command.getServerCommandType()) {
                case GEN_SSL_CERT:
                    response.setData(((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
                    break;
                default:
                    response.setIsError(true);
                    response.setErrorMessage("Unknown command:" + command.getServerCommandType().toString());
            }
        } catch (Exception ex){
            log.error(ex.getMessage());
            response.setIsError(true);
            response.setErrorMessage(ex.getMessage());
        }
        response.setServerCommandType(command.getServerCommandType());
        return response;
    }
}

