package lu.pata.hsm.hsmconsole.remote;

import lu.pata.hsm.hsmlib.ServerCommand;
import lu.pata.hsm.hsmlib.ServerCommandResponse;

public interface CommandDispatcher {
    void setUser(String user,String password);
    ServerCommandResponse cmd(ServerCommand command);
}
