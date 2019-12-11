package lu.pata.hsm.hsmconsole.commands;

import lu.pata.hsm.hsmconsole.config.InputReader;
import lu.pata.hsm.hsmconsole.config.OutputPrinter;
import lu.pata.hsm.hsmconsole.remote.CommandDispatcher;
import lu.pata.hsm.hsmlib.ServerCommand;
import lu.pata.hsm.hsmlib.ServerCommandResponse;
import lu.pata.hsm.hsmlib.ServerCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class RemoteQuoteCommand {
    private Logger log= LoggerFactory.getLogger(RemoteQuoteCommand.class);

    @Autowired
    private OutputPrinter out;
    @Autowired
    private InputReader in;
    @Value("${server.url}")
    private String serverUrl;
    @Autowired
    private CommandDispatcher dispatcher;

    @ShellMethod("Ask for a random quote")
    public void quote(){
        ServerCommand cmd=new ServerCommand();
        cmd.setServerCommandType(ServerCommandType.GET_QUOTE);
        ServerCommandResponse r=dispatcher.cmd(cmd);
        if(!r.getIsError()) {
            out.print(r.getData());
        } else {
            log.error(r.getErrorMessage());
        }
    }

    @ShellMethod("Login to local HSM")
    public void login(){
        String user=in.prompt("Enter user","",true);
        String pass=in.prompt("Enter PIN","",false);
        dispatcher.setUser(user,pass);
    }
}
