package com.zhuaiwa.session;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Service;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.SSSessionDomain;
import com.zhuaiwa.api.SSSessionDomain.SSSessionDomainSvc;
import com.zhuaiwa.api.netty.server.NettyRpcServer;
import com.zhuaiwa.api.util.ApiExtensionHelper;
import com.zhuaiwa.session.service.ContactService_v_1_0;
import com.zhuaiwa.session.service.MessageService_v_1_0;
import com.zhuaiwa.session.service.UserService_v_1_0;

public class SSSessionDomainServer
{
	static private boolean hasRegisterSessionDomainApiProtobufExtension = false;

	private static Logger logger = LoggerFactory
			.getLogger(SSSessionDomainServer.class);

	public static void main(String[] args)
	{

		SSSessionDomainServer daemon = new SSSessionDomainServer();
		String pidFile = System.getProperty("pidfile");

		try
		{
			daemon.init(args);

			if (pidFile != null)
			{
				new File(pidFile).deleteOnExit();
			}

			if (System.getProperty("foreground") == null)
			{
				// System.out.close();
				// System.err.close();
			}

			daemon.start();
		}
		catch (Throwable e)
		{
			String msg = "Exception encountered during startup.";
			logger.error(msg, e);

			// try to warn user on stdout too, if we haven't already detached
			System.out.println(msg);
			e.printStackTrace();

			System.exit(3);
		}

	}

	static public void registerSessionDomainApiProtobufExtension()
	{
		if (hasRegisterSessionDomainApiProtobufExtension)
		{
			return;
		}
		hasRegisterSessionDomainApiProtobufExtension = true;
		ApiExtensionHelper.registerProto(SSSessionDomain.getDescriptor(),
				SSSessionDomain.class);
	}

	private String host;

	private MBeanServer mbeanServer;

	private int port = 8000;

	private NettyRpcServer server;

	private NettyRpcServer ssl_server;

	private SSSessionDomainSvc_v_1_0 svc10;

	public void destroy()
	{
		svc10.Dispose();
	}

	public void init(String[] args)
	{
		if (args.length > 0)
		{
			for (int i = 0; i < args.length; i++)
			{
				if (args[i].equalsIgnoreCase("-host"))
				{
					i++;
					host = args[i];
				}
				else if (args[i].equalsIgnoreCase("-port"))
				{
					i++;
					port = Integer.valueOf(args[i]);
				}
			}
		}

		registerSessionDomainApiProtobufExtension();

		// rpc
		server = new NettyRpcServer(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		ssl_server = new NettyRpcServer(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()), true);

		// 接口
		svc10 = new SSSessionDomainSvc_v_1_0();

		BlockingInterface dataDomainClientSvc = DataDomainSvcFactory
				.getBlockingService();

		ContactService_v_1_0 contactService = new ContactService_v_1_0();
		contactService.setDataDomainClientSvc(dataDomainClientSvc);
		svc10.setContactService(contactService);

		MessageService_v_1_0 messageService = new MessageService_v_1_0();
		UserService_v_1_0 userService = new UserService_v_1_0();
		messageService.setDataDomainClientSvc(dataDomainClientSvc);
		svc10.setMessageService(messageService);

		userService.setDataDomainClientSvc(dataDomainClientSvc);
		userService.setMessageService(messageService);
		messageService.setUserService(userService);
		svc10.setUserService(userService);

		svc10.Initialize();

		// server.registerDefaultService(SSSessionDomainSvc.newReflectiveService(svc10));
		// server.registerVersionService(SSSessionDomainSvc.newReflectiveService(svc10),
		// 0x00010000);
		// ssl_server.registerDefaultService(SSSessionDomainSvc.newReflectiveService(svc10));
		// ssl_server.registerVersionService(SSSessionDomainSvc.newReflectiveService(svc10),
		// 0x00010000);

		Service service = SSSessionDomainSvc.newReflectiveService(svc10);
		server.registerDefaultService(service, svc10);
		server.registerVersionService(service, 0x00010000, svc10);
		// ssl_server.registerDefaultService(service, svc10);
		// ssl_server.registerVersionService(service, 0x00010000, svc10);

		// specify the following parameter for JVM:
		// -Dcom.sun.management.jmxremote=SSSessionDomainServer, then we can use
		// JConsole
		// to monitor the MBeans.
		mbeanServer = ManagementFactory.getPlatformMBeanServer();
		try
		{
			mbeanServer
					.registerMBean(
							SessionManager.getInstance(),
							new ObjectName(
									"com.zhuaiwa.session.SessionManager:default=stats"));
			mbeanServer.registerMBean(PubSubManager.getInstance(),
					new ObjectName(
							"com.zhuaiwa.session.PubSubManager:default=stats"));
		}
		catch (MalformedObjectNameException e)
		{
			e.printStackTrace();
		}
		catch (InstanceAlreadyExistsException e)
		{
			e.printStackTrace();
		}
		catch (MBeanRegistrationException e)
		{
			e.printStackTrace();
		}
		catch (NotCompliantMBeanException e)
		{
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
	}

	public void start()
	{
		logger.info("SSDataDomainServer starting up...");
		InetSocketAddress address = null;
		if (host == null)
		{
			address = new InetSocketAddress(port);
		}
		else
		{
			address = new InetSocketAddress(host, port);
		}
		InetSocketAddress ssl_address = null;
		if (host == null)
		{
			ssl_address = new InetSocketAddress(port + 1);
		}
		else
		{
			ssl_address = new InetSocketAddress(host, port + 1);
		}
		server.serve(address);
		ssl_server.serve(ssl_address);
		logger.info("SessionDomainServer started! port:" + port + ", ssl port:"
				+ (port + 1));
	}

	public void stop()
	{
	}

}
