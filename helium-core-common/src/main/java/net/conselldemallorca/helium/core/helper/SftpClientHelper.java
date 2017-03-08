/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import net.conselldemallorca.helium.core.util.GlobalProperties;

/**
 * Helper per a obtenir els clients de serveis web externs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class SftpClientHelper {

	private String SFTPHOST;
	private int SFTPPORT;
	private String SFTPUSER;
	private String SFTPPASS;
	
	private Session session;
	private Channel channel;
	private ChannelSftp channelSftp;
    
	public ChannelSftp openSicerSftpConnection() throws Exception {
		GlobalProperties properties = GlobalProperties.getInstance(); 
		
		SFTPHOST = properties.getProperty("app.sicer.sftp.host");
        SFTPPORT = Integer.parseInt(properties.getProperty("app.sicer.sftp.port"));
        SFTPUSER = properties.getProperty("app.sicer.sftp.user");
        SFTPPASS = properties.getProperty("app.sicer.sftp.password");
        
		return openSftpConnection();
	}
	
	public void closeConnection() {
		channelSftp.exit();
        System.out.println("sftp Channel exited.");
        channel.disconnect();
        System.out.println("Channel disconnected.");
        session.disconnect();
        System.out.println("Host Session disconnected.");
	}
	
	private ChannelSftp openSftpConnection() throws Exception {
        System.out.println("preparing the host information for sftp.");
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            System.out.println("Host connected.");
            channel = session.openChannel("sftp");
            channel.connect();
            System.out.println("sftp channel opened and connected.");
            channelSftp = (ChannelSftp) channel;
        } catch (Exception ex) {
             System.out.println("Exception found while tranfer the response.");
             ex.printStackTrace();
             throw ex;
        }
        
        return channelSftp;
	} 

}
