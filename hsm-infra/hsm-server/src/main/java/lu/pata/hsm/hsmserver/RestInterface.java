package lu.pata.hsm.hsmserver;

import lu.pata.hsm.hsmlib.ServerCommand;
import lu.pata.hsm.hsmlib.ServerCommandResponse;
import lu.pata.hsm.hsmlib.ServerCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class RestInterface {
    private Logger log=LoggerFactory.getLogger(RestInterface.class);

    @Autowired
    private CommandProcessor commandProcessor;

    @Secured({UserRole.Code.ADMIN,UserRole.Code.USER})
    @RequestMapping(value = "cmd",method = RequestMethod.POST, consumes = "application/json")
    public ServerCommandResponse processCommandPost(@RequestBody ServerCommand command, HttpServletRequest request){
        return commandProcessor.process(command);
    }

    @Secured({UserRole.Code.ADMIN,UserRole.Code.USER})
    @RequestMapping(value = "cmd",method = RequestMethod.GET)
    public ServerCommandResponse processCommandGet(HttpServletRequest request){
        ServerCommand command=new ServerCommand();
        command.setServerCommandType(ServerCommandType.GET_QUOTE);
        return commandProcessor.process(command);
    }
}

