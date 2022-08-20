/*
 * Copyright 2009 Red Hat, Inc. Red Hat licenses this file to you under the
 * Apache License, version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.zhuaiwa.session.http;

import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Service;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.SSSessionDomain.SSSessionDomainSvc;
import com.zhuaiwa.session.DataDomainSvcFactory;
import com.zhuaiwa.session.PubSubManager;
import com.zhuaiwa.session.SSSessionDomainServer;
import com.zhuaiwa.session.SSSessionDomainSvc_v_1_0;
import com.zhuaiwa.session.SessionManager;
import com.zhuaiwa.session.service.BaikuTelServiceImpl;
import com.zhuaiwa.session.service.ContactService_v_1_0;
import com.zhuaiwa.session.service.MessageService_v_1_0;
import com.zhuaiwa.session.service.UserService_v_1_0;

/**
 * HTTP版会话域
 * 
 * @author tangjun
 */
public class HttpServer
{
	private static final Logger LOG = LoggerFactory.getLogger(HttpServer.class);

	private static MBeanServer mbeanServer;

	public static void main(String[] args)
	{
		SSSessionDomainServer.registerSessionDomainApiProtobufExtension();
		// Configure the server.
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		// PubSubManager.getInstance();
		// SessionManager.getInstance();

		ApiRequestHandler handler = new ApiRequestHandler();

		// 接口
		SSSessionDomainSvc_v_1_0 svc10 = new SSSessionDomainSvc_v_1_0();

		BlockingInterface dataDomainClientSvc = DataDomainSvcFactory
				.getBlockingService();

		ContactService_v_1_0 contactService = new ContactService_v_1_0();
		contactService.setDataDomainClientSvc(dataDomainClientSvc);
		svc10.setContactService(contactService);

		BaikuTelServiceImpl baikuTelService = new BaikuTelServiceImpl();
		baikuTelService.setDataDomainClientSvc(dataDomainClientSvc);
		svc10.setBaikuTelService(baikuTelService);

		MessageService_v_1_0 messageService = new MessageService_v_1_0();
		UserService_v_1_0 userService = new UserService_v_1_0();
		messageService.setDataDomainClientSvc(dataDomainClientSvc);
		svc10.setMessageService(messageService);

		userService.setDataDomainClientSvc(dataDomainClientSvc);
		userService.setMessageService(messageService);
		messageService.setUserService(userService);
		baikuTelService.setUserService(userService);
		svc10.setUserService(userService);

		svc10.Initialize();

		// handler.registerDefaultService(SSSessionDomainSvc.newReflectiveService(svc10));
		// handler.registerVersionService(SSSessionDomainSvc.newReflectiveService(svc10),
		// 0x00010000);
		Service service = SSSessionDomainSvc.newReflectiveService(svc10);
		handler.registerDefaultService(service, svc10);
		handler.registerVersionService(service, 0x00010000, svc10);

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new HttpServerPipelineFactory(handler,
				false));

		SessionManager.getInstance().type = SessionManager.SessionType.HTTP;

		// Bind and start to accept incoming connections.
		int port = 8080;
		if (args.length > 0)
		{
			for (int i = 0; i < args.length; i++)
			{
				if (args[i].equalsIgnoreCase("-port"))
				{
					i++;
					port = Integer.valueOf(args[i]);
				}
			}
		}

		bootstrap.bind(new InetSocketAddress(port));
		LOG.info("HttpServer started, port:" + port);

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
}
