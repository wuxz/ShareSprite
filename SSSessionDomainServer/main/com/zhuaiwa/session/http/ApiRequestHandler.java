package com.zhuaiwa.session.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.Service;
import com.google.protobuf.TextFormat;
import com.zhuaiwa.api.Common;
import com.zhuaiwa.api.Common.ApiHeader;
import com.zhuaiwa.api.Common.ApiMessage;
import com.zhuaiwa.api.Common.ApiRequest;
import com.zhuaiwa.api.Common.ApiResponse;
import com.zhuaiwa.api.Common.ApiType;
import com.zhuaiwa.api.Common.SSAccount;
import com.zhuaiwa.api.Common.SSResultCode;
import com.zhuaiwa.api.Rpc.GetProfileRequest;
import com.zhuaiwa.api.Rpc.GetProfileResponse;
import com.zhuaiwa.api.Rpc.GetTorrentRequest;
import com.zhuaiwa.api.Rpc.GetTorrentResponse;
import com.zhuaiwa.api.SSSessionDomain.ActivateAccountRequest;
import com.zhuaiwa.api.SSSessionDomain.ForgetPasswordRequest;
import com.zhuaiwa.api.netty.Authenticateable;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.netty.exception.InvalidRpcRequestException;
import com.zhuaiwa.api.netty.exception.NoSuchServiceException;
import com.zhuaiwa.api.netty.exception.NoSuchServiceMethodException;
import com.zhuaiwa.api.netty.server.NettyRpcServerChannelUpstreamHandler;
import com.zhuaiwa.api.util.ApiExtensionHelper;
import com.zhuaiwa.api.util.SSIdUtils;
import com.zhuaiwa.session.DataDomainSvcFactory;
import com.zhuaiwa.session.SessionManager;
import com.zhuaiwa.session.SessionManager.Session;
import com.zhuaiwa.session.service.MessageService_v_1_0;
import com.zhuaiwa.session.service.UserService_v_1_0;
import com.zhuaiwa.session.util.FreemarkerUtil;
import com.zhuaiwa.util.JsonFormat;

// @Sharable()
public class ApiRequestHandler extends NettyRpcServerChannelUpstreamHandler
{
	private static byte[] defaultIcon = null;

	private static Logger LOG = LoggerFactory
			.getLogger(ApiRequestHandler.class);

	private static MessageService_v_1_0 messageServices;

	private static String proxyHtml = "";

	private static UserService_v_1_0 userServices;
	static
	{
		userServices = new UserService_v_1_0(
				DataDomainSvcFactory.getBlockingService());
		messageServices = new MessageService_v_1_0(
				DataDomainSvcFactory.getBlockingService());
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					"./template/proxy.html")));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			reader.close();
			proxyHtml = sb.toString();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int size = 0;
			FileInputStream r = new FileInputStream(new File(
					"./template/avatar.jpg"));
			while ((size = r.read(buffer)) > 0)
			{
				baos.write(buffer, 0, size);
			}
			r.close();
			defaultIcon = baos.toByteArray();
			baos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void call(ChannelHandlerContext ctx, final MessageEvent e,
			String requestJson, final boolean close)
	{
		ApiMessage.Builder builder = ApiMessage.newBuilder();
		try
		{
			JsonFormat.merge(requestJson,
					ApiExtensionHelper.getExtensionRegistry(), builder);
			ApiMessage apiMessage = builder.build();
			// LOG.debug(TextFormat.printToString(apiMessage));

			ApiHeader header = apiMessage.getHeader();

			final NettyRpcController controller = new NettyRpcController();
			controller.setChannelHandlerContext(ctx);
			controller.setApiHeader(header);
			ApiRequest request = apiMessage.getExtension(Common.request);
			controller.setApiRequest(request);
			controller.setApiResponse(ApiResponse.newBuilder());
			try
			{
				final String serviceName = request.getService();
				final String methodName = request.getMethod();
				Integer version = header.hasVersion() ? header.getVersion()
						: null;
				// LOG.debug("Received request: " + header.getSeq() +
				// ", service: " + serviceName + ", method: " + methodName +
				// ", version: "
				// + version);

				Service service = getService(serviceName, version);
				if (service == null)
				{
					throw new NoSuchServiceException(header, request,
							serviceName);
				}
				else if (service.getDescriptorForType().findMethodByName(
						methodName) == null)
				{
					throw new NoSuchServiceMethodException(header, request,
							methodName);
				}

				MethodDescriptor methodDescriptor = service
						.getDescriptorForType().findMethodByName(methodName);
				final Message methodRequest = request
						.getExtension(ApiExtensionHelper
								.<Message> getRequestByMethodName(request
										.getMethod()));
				if (methodRequest == null)
				{
					throw new InvalidRpcRequestException(header, request,
							methodName);
				}

				final long startTime = System.currentTimeMillis();
				RpcCallback<Message> callback = !header.hasSeq() ? null
						: new RpcCallback<Message>()
						{
							@Override
							public void run(Message methodResponse)
							{

								long endTime = System.currentTimeMillis();
								long spendTime = endTime - startTime;
								if (spendTime > 1000)
								{
									LOG.info(controller.getApiHeader().getSeq()
											+ ": " + spendTime);
									LOG.info(methodRequest
											.getDescriptorForType().getName());
									LOG.info(TextFormat
											.printToString(methodRequest));
									if (methodResponse != null)
									{
										LOG.info(methodResponse
												.getDescriptorForType()
												.getName());
										LOG.info(TextFormat
												.printToString(methodResponse));
									}
								}

								// LOG.info("Send response: seq=" +
								// controller.getApiHeader().getSeq() +
								// ", code=" + controller.getCode()
								// + ", reason=" + controller.errorText());

								ApiHeader.Builder responseApiHeader = ApiHeader
										.newBuilder();
								if (controller.getApiHeader().hasSeq())
								{
									responseApiHeader.setSeq(controller
											.getApiHeader().getSeq());
								}
								if (controller.getApiHeader().hasVersion())
								{
									responseApiHeader.setVersion(controller
											.getApiHeader().getVersion());
								}
								if (controller.getApiHeader().hasSid())
								{
									responseApiHeader.setSid(controller
											.getApiHeader().getSid());
								}
								responseApiHeader
										.setType(ApiType.API_TYPE_RESPONSE
												.getNumber());// 包类型

								ApiResponse.Builder responseApiResponse = controller
										.getApiResponse();
								if (methodResponse == null)
								{
									responseApiResponse
											.setCode(SSResultCode.RC_NO_RESPONSE
													.getNumber());
									// responseApiResponse.setCode(controller.getCode());
									if (controller.errorText() != null)
									{
										responseApiResponse
												.setReason(controller
														.errorText());
									}
									else
									{
										responseApiResponse.setReason("未知");
									}
								}
								else
								{
									// LOG.info(TextFormat.printToString(methodResponse));
									responseApiResponse.setCode(controller
											.getCode());
									if (controller.errorText() != null)
									{
										responseApiResponse
												.setReason(controller
														.errorText());
									}
									responseApiResponse
											.setExtension(
													ApiExtensionHelper
															.getResponseByMethodName(methodName),
													methodResponse);
								}

								ApiMessage.Builder responseApiMessage = ApiMessage
										.newBuilder();
								responseApiMessage.setHeader(responseApiHeader);
								responseApiMessage.setExtension(
										Common.response,
										responseApiResponse.build());
								String responseStr = JsonFormat
										.printToString(responseApiMessage
												.build());
								LOG.debug("responseContent:" + responseStr);
								sendHttpResponse(e, responseStr, close);
								return;
							}
						};

				// LOG.info(TextFormat.printToString(methodRequest));

				String sid = controller.getApiHeader().getSid();
				Session session = SessionManager.getInstance().getSession(sid);
				if (session != null)
				{
					session.lastActiveTime = System.currentTimeMillis();
				}

				Authenticateable auth = this.authenticateMap.get(service);
				if (auth != null)
				{
					// if (service instanceof Authenticateable)
					// {
					// Authenticateable auth = (Authenticateable) service;
					if (!auth.authenticate(methodDescriptor, controller,
							methodRequest, callback))
					{
						callException(controller, ctx, e, close, new Exception(
								"未登录"));
						return;
					}
				}

				service.callMethod(methodDescriptor, controller, methodRequest,
						callback);
			}
			catch (NoSuchServiceException e1)
			{
				callException(controller, ctx, e, close, e1);
			}
			catch (NoSuchServiceMethodException e1)
			{
				callException(controller, ctx, e, close, e1);
			}
			catch (InvalidRpcRequestException e1)
			{
				callException(controller, ctx, e, close, e1);
			}
		}
		catch (com.zhuaiwa.util.JsonFormat.ParseException e1)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("请求JSON格式错误", e1);
			}
			callException(null, ctx, e, close, e1);
		}
	}

	public void callException(NettyRpcController controller,
			ChannelHandlerContext ctx, final MessageEvent e,
			final boolean close, Throwable exception)
	{
		ApiHeader.Builder responseApiHeader = ApiHeader.newBuilder();
		responseApiHeader.setType(ApiType.API_TYPE_RESPONSE.getNumber());// 包类型

		ApiResponse.Builder responseApiResponse;
		if (controller != null)
		{
			if (controller.getApiHeader().hasSeq())
			{
				responseApiHeader.setSeq(controller.getApiHeader().getSeq());
			}
			if (controller.getApiHeader().hasVersion())
			{
				responseApiHeader.setVersion(controller.getApiHeader()
						.getVersion());
			}
			if (controller.getApiHeader().hasSid())
			{
				responseApiHeader.setSid(controller.getApiHeader().getSid());
			}
			responseApiResponse = controller.getApiResponse();
			if (exception instanceof NoSuchServiceException)
			{
				responseApiResponse.setCode(
						SSResultCode.RC_NO_SUCH_SERVICE.getNumber()).setReason(
						"不存在的服务");
			}
			else if (exception instanceof NoSuchServiceMethodException)
			{
				responseApiResponse.setCode(
						SSResultCode.RC_NO_SUCH_METHOD.getNumber()).setReason(
						"不存在的方法");
			}
			else if (exception instanceof InvalidRpcRequestException)
			{
				responseApiResponse.setCode(
						SSResultCode.RC_REQUEST_INVALID.getNumber()).setReason(
						"无效的请求");
			}
			else
			{
				responseApiResponse.setCode(controller.getCode());
				if ((exception != null) && (exception.getMessage() != null))
				{
					responseApiResponse.setReason(controller.errorText());
				}
			}
		}
		else
		{
			responseApiResponse = ApiResponse.newBuilder();
			responseApiResponse.setCode(SSResultCode.RC_REQUEST_INVALID
					.getNumber());
			if (exception instanceof com.zhuaiwa.util.JsonFormat.ParseException)
			{
				responseApiResponse.setReason("请求JSON格式错误");
			}
			else
			{
				responseApiResponse.setReason("请求错误");
			}
		}
		ApiMessage.Builder responseApiMessage = ApiMessage.newBuilder();
		responseApiMessage.setHeader(responseApiHeader);
		responseApiMessage.setExtension(Common.response,
				responseApiResponse.build());
		String responseStr = JsonFormat.printToString(responseApiMessage
				.build());
		LOG.debug("responseContent:" + responseStr);
		sendHttpResponse(e, responseStr, close);
		return;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
	{
		// e.getCause().printStackTrace();
		LOG.debug("断开链接", e.getCause());
		// sendError(e.getChannel(),HttpResponseStatus.INTERNAL_SERVER_ERROR,e.getCause().getMessage(),true);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception
	{
		// LOG.info("messageReceived...");
		HttpRequest httpRequest = (HttpRequest) e.getMessage();

		if (LOG.isDebugEnabled())
		{
			StringBuffer sb = new StringBuffer();
			sb.append("\nRequest URI:" + httpRequest.getUri());
			if (!httpRequest.getHeaderNames().isEmpty())
			{
				for (String name : httpRequest.getHeaderNames())
				{
					for (String value : httpRequest.getHeaders(name))
					{
						sb.append("\nHeader: " + name + " = " + value + "");
					}
				}
			}
			// LOG.debug(sb.toString());
		}

		boolean close = HttpHeaders.Values.CLOSE.equalsIgnoreCase(httpRequest
				.getHeader(HttpHeaders.Names.CONNECTION))
				|| (httpRequest.getProtocolVersion().equals(
						HttpVersion.HTTP_1_0) && !HttpHeaders.Values.KEEP_ALIVE
						.equalsIgnoreCase(httpRequest
								.getHeader(HttpHeaders.Names.CONNECTION)));
		// boolean close = true;

		String uri = httpRequest.getUri();
		if (uri.startsWith("/do/"))
		{
			QueryStringDecoder queryStringDecoder = new QueryStringDecoder(
					httpRequest.getUri());
			Map<String, List<String>> params = queryStringDecoder
					.getParameters();
			final NettyRpcController controller = new NettyRpcController();
			controller.setChannelHandlerContext(ctx);
			if (uri != null)
			{
				if (uri.startsWith("/do/activateAccount"))
				{// 激活帐号（点击帐号激活邮件中的链接）
					String userid;
					String code;
					try
					{
						userid = params.get("userid").get(0);
						code = params.get("code").get(0);
					}
					catch (Exception e1)
					{
						sendError(e.getChannel(),
								HttpResponseStatus.INTERNAL_SERVER_ERROR,
								"请求参数不正确", close);
						return;
					}

					ActivateAccountRequest.Builder requestBuilder = ActivateAccountRequest
							.newBuilder();
					Common.SSId.Builder ssidBuilder = Common.SSId.newBuilder();
					ssidBuilder.setId(userid);
					ssidBuilder.setDomain(SSIdUtils.SSIdDomain.userid.name());
					requestBuilder.setUser(ssidBuilder.build());
					requestBuilder.setCode(code);

					SSAccount account = userServices.activateAccount(
							controller, requestBuilder.build(), null);
					if (controller.failed())
					{
						sendHttpResponse(e, FreemarkerUtil.getFailedOutput(
								userServices.getNickname(userid), "激活账号",
								controller.getCode(), controller.errorText()),
								close);
					}
					else
					{
						sendHttpResponse(
								e,
								FreemarkerUtil
										.getActivateAccountSuccessOutput(SSIdUtils.getId(
												account.getAliasIdListList(),
												SSIdUtils.SSIdDomain.email)),
								close);
					}
				}
				else if (uri.startsWith("/do/forgetPassword"))
				{// 请求发送验证码。（没用用到）
					Common.SSId.Builder ssidBuilder = Common.SSId.newBuilder();
					String userid = params.get("userid").get(0);
					ssidBuilder.setId(userid);
					ssidBuilder
							.setDomain(userid.indexOf('@') > 0 ? SSIdUtils.SSIdDomain.email
									.name() : SSIdUtils.SSIdDomain.phone.name());
					ForgetPasswordRequest.Builder requestBuilder = ForgetPasswordRequest
							.newBuilder();
					requestBuilder.setUser(ssidBuilder.build());
					Common.SSAccount account = userServices.forgetPassword(
							controller, requestBuilder.build(), null);
					if (controller.failed())
					{
						sendHttpResponse(e, FreemarkerUtil.getFailedOutput(
								userServices.getNickname(userid), "忘记密码",
								controller.getCode(), controller.errorText()),
								close);
					}
					else
					{
						sendHttpResponse(e,
								FreemarkerUtil.getForgetPasswordFormOutput(
										account.getUserid(),
										account.getSecurityCode()), close);
					}
				}
				else if (uri.startsWith("/do/checkForgetPassword"))
				{// 请求校验验证码，发回重置密码的页面。（点击重置密码邮件中的链接，验证码正确后跳转至输入新密码界面）
					String userid;
					String code;
					try
					{
						userid = params.get("userid").get(0);
						code = params.get("code").get(0);
					}
					catch (Exception e1)
					{
						sendError(e.getChannel(),
								HttpResponseStatus.INTERNAL_SERVER_ERROR,
								"请求参数不正确", close);
						return;
					}

					Common.SSId.Builder ssidBuilder = Common.SSId.newBuilder();
					ssidBuilder.setId(userid);
					ssidBuilder.setDomain(SSIdUtils.SSIdDomain.userid.name());
					ForgetPasswordRequest.Builder requestBuilder = ForgetPasswordRequest
							.newBuilder();
					requestBuilder.setUser(ssidBuilder.build());
					userServices.checkForgetPassword(controller,
							requestBuilder.build(), code, null);
					if (controller.failed())
					{
						sendHttpResponse(e, FreemarkerUtil.getFailedOutput(
								userServices.getNickname(userid), "忘记密码",
								controller.getCode(), controller.errorText()),
								close);
					}
					else
					{
						sendHttpResponse(e,
								FreemarkerUtil.getForgetPasswordFormOutput(
										userid, params.get("code").get(0)),
								close);
					}
				}
				else if (uri.startsWith("/do/getTorrent"))
				{
					String info_hash;
					try
					{
						info_hash = params.get("info_hash").get(0);
					}
					catch (Exception e1)
					{
						sendError(e.getChannel(),
								HttpResponseStatus.INTERNAL_SERVER_ERROR,
								"请求参数不正确", close);
						return;
					}
					GetTorrentResponse resp = messageServices.getTorrent(
							controller,
							GetTorrentRequest.newBuilder().setKey(info_hash)
									.build());
					if (controller.failed() || !resp.hasValue())
					{
						sendError(e.getChannel(), HttpResponseStatus.NOT_FOUND,
								"Torrent不存在", close);
					}
					else
					{
						ByteString torrent = resp.getValue();
						sendX(e, torrent.toByteArray(),
								"application/x-bittorrent; charset=UTF-8",
								close);
					}
				}
				else if (uri.startsWith("/do/getIcon"))
				{
					String userid;
					try
					{
						userid = params.get("userid").get(0);
					}
					catch (Exception e1)
					{
						sendError(e.getChannel(),
								HttpResponseStatus.INTERNAL_SERVER_ERROR,
								"请求参数不正确", close);
						return;
					}
					GetProfileResponse resp = userServices.getProfile(
							controller, GetProfileRequest.newBuilder()
									.addUseridList(userid).build());
					if (controller.failed() || (resp.getProfileListCount() < 1))
					{
						// 返回默认头像
						if (defaultIcon != null)
						{
							sendX(e, defaultIcon, "image/jpeg", close);
						}
						else
						{
							sendError(e.getChannel(),
									HttpResponseStatus.NOT_FOUND, "ICON不存在",
									close);
						}
					}
					else
					{
						byte[] b = new byte[0];
						if (resp.getProfileList(0).hasIcon())
						{
							ByteString icon = resp.getProfileList(0).getIcon();
							b = icon.toByteArray();
						}
						if (b.length == 0)
						{
							sendX(e, defaultIcon, "image/jpeg", close);
						}
						else
						{
							sendX(e, b, "image/jpeg", close);
						}

					}
				}
				else if (uri.startsWith("/do/getServerList"))
				{
					// 请求URL：http://api.baiku.cn/do/getServerList?userid=&email=&phone=&version=
					// 响应会话域服务器列表
					try
					{
						LOG.info(params.toString());
					}
					catch (Exception e1)
					{
					}
					String result = "[{\"ip\":\"59.151.117.236\",\"port\":8000,\"ssl\":false},{\"ip\":\"59.151.117.236\",\"port\":8001,\"ssl\":true}]";
					sendX(e, result.getBytes(), "application/json", close);
				}
				else
				{
					sendError(e.getChannel(), HttpResponseStatus.NOT_FOUND,
							"资源不存在", close);
				}
			}
		}
		else if (uri.startsWith("/api"))
		{// JSON RPC
			if (!httpRequest.isChunked())
			{
				ChannelBuffer content = httpRequest.getContent();
				if (content != null)
				{
					String requestContent = content.toString(Charset
							.forName("UTF-8"));
					LOG.debug("requestContent:" + requestContent);
					call(ctx, e, requestContent, close);
				}
				else
				{
					sendError(e.getChannel(), HttpResponseStatus.BAD_REQUEST,
							"无请求内容体", close);
				}
			}
		}
		else if (uri.equals("/proxy.html"))
		{// ajax跨域代理页面
			sendHttpResponse(e, proxyHtml, close);
		}
	}

	public void sendError(Channel channel, HttpResponseStatus status,
			String desc, boolean close)
	{

		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
				status);
		ChannelBuffer buf = ChannelBuffers.copiedBuffer(status.toString()
				+ " : " + desc, Charset.forName("UTF-8"));
		response.setContent(buf);
		response.setHeader(HttpHeaders.Names.CONTENT_TYPE,
				"text/html; charset=UTF-8");
		ChannelFuture future = channel.write(response);
		if (close)
		{
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	public void sendHttpResponse(final MessageEvent e, String responseStr,
			boolean close)
	{
		ChannelBuffer buf = ChannelBuffers.copiedBuffer(responseStr,
				Charset.forName("UTF-8"));
		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
				HttpResponseStatus.OK);
		response.setContent(buf);
		response.setHeader(HttpHeaders.Names.CONTENT_TYPE,
				"text/html; charset=UTF-8");
		response.setHeader(HttpHeaders.Names.CONTENT_LENGTH,
				String.valueOf(buf.readableBytes()));
		ChannelFuture future = e.getChannel().write(response);
		if (close)
		{
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	public void sendX(final MessageEvent e, byte[] bs, String contentType,
			boolean close)
	{
		ChannelBuffer buf = ChannelBuffers.copiedBuffer(bs);
		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
				HttpResponseStatus.OK);
		response.setContent(buf);
		response.setHeader(HttpHeaders.Names.CONTENT_TYPE, contentType);
		response.setHeader(HttpHeaders.Names.CONTENT_LENGTH,
				String.valueOf(buf.readableBytes()));
		ChannelFuture future = e.getChannel().write(response);
		if (close)
		{
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
}
