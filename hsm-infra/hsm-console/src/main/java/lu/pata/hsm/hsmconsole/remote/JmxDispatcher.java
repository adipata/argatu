package lu.pata.hsm.hsmconsole.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import lu.pata.hsm.hsmlib.ServerCommand;
import lu.pata.hsm.hsmlib.ServerCommandResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class JmxDispatcher implements CommandDispatcher {
    private Logger log= LoggerFactory.getLogger(JmxDispatcher.class);
    private ObjectMapper json=new ObjectMapper();

    @Override
    public void setUser(String user, String password) {

    }

    @Override
    public ServerCommandResponse cmd(ServerCommand command) {
        ServerCommandResponse serverCommandResponse=null;

        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
        for (VirtualMachineDescriptor desc : vms) {
            if(desc.displayName().equals("lu.pata.hsm.hsmserver.HsmServerApplication")){
                try {
                    VirtualMachine vm;
                    try {
                        vm = VirtualMachine.attach(desc);
                    } catch (AttachNotSupportedException e) {
                        log.error("JMX attach error: "+e.getMessage());
                        continue;
                    }
                    Properties props = vm.getAgentProperties();
                    String connectorAddress = props.getProperty("com.sun.management.jmxremote.localConnectorAddress");
                    if (connectorAddress == null) {
                        continue;
                    }
                    JMXServiceURL url = new JMXServiceURL(connectorAddress);
                    JMXConnector connector = JMXConnectorFactory.connect(url);
                    try {
                        MBeanServerConnection mbeanConn = connector.getMBeanServerConnection();
                        ObjectName beanName=new ObjectName("lu.pata.hsm.hsmserver:name=jmxInterfaceBean,type=JmxInterfaceBean");
                        //Set<ObjectName> beanSet = mbeanConn.queryNames(beanName, null);
                        //MBeanInfo mbi = mbeanConn.getMBeanInfo(beanName);
                        Object  opParams[] = { json.writeValueAsString(command) };

                        String  opSig[] = { String.class.getName()};
                        String r=(String)mbeanConn.invoke(beanName,"cmd",opParams,opSig);
                        serverCommandResponse=json.readValue(r, ServerCommandResponse.class);
                    } catch (ReflectionException | InstanceNotFoundException | MBeanException | MalformedObjectNameException e) {
                        log.error("JMX method invoke error: "+e.getMessage());
                    } finally {
                        connector.close();
                    }
                }catch(IOException ex){
                    log.error("JMX communication error: "+ex.getMessage());
                }
            }
        }

        if(serverCommandResponse==null) {
            serverCommandResponse=new ServerCommandResponse();
            serverCommandResponse.setIsError(true);
            serverCommandResponse.setErrorMessage("Could not obtain data from local hsm-server. Is the server started?");
        }

        return serverCommandResponse;
    }
}

