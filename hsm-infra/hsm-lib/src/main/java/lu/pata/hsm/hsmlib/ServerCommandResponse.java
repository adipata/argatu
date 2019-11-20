package lu.pata.hsm.hsmlib;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServerCommandResponse {
    private ServerCommandType serverCommandType;
    private Boolean isError=false;
    private String errorMessage;
    private String data;
}
