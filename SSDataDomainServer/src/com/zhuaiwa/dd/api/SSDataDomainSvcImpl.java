package com.zhuaiwa.dd.api;

import static com.zhuaiwa.dd.hector.HectorUtils.createIntegerColumn;
import static com.zhuaiwa.dd.hector.HectorUtils.createLongColumn;
import static com.zhuaiwa.dd.hector.HectorUtils.createStringColumn;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.commons.lang.StringUtils;
import org.omg.CORBA.BooleanHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.zhuaiwa.api.Common.SSAccount;
import com.zhuaiwa.api.Common.SSContact;
import com.zhuaiwa.api.Common.SSGroup;
import com.zhuaiwa.api.Common.SSId;
import com.zhuaiwa.api.Common.SSMember;
import com.zhuaiwa.api.Common.SSMessage;
import com.zhuaiwa.api.Common.SSMessageType;
import com.zhuaiwa.api.Common.SSPerson;
import com.zhuaiwa.api.Common.SSProfile;
import com.zhuaiwa.api.Common.SSResultCode;
import com.zhuaiwa.api.Common.SSShareType;
import com.zhuaiwa.api.Rpc.AddContactRequest;
import com.zhuaiwa.api.Rpc.AddContactResponse;
import com.zhuaiwa.api.Rpc.AddGroupRequest;
import com.zhuaiwa.api.Rpc.AddGroupResponse;
import com.zhuaiwa.api.Rpc.AddMemberRequest;
import com.zhuaiwa.api.Rpc.AddMemberResponse;
import com.zhuaiwa.api.Rpc.BindAccountRequest;
import com.zhuaiwa.api.Rpc.BindAccountResponse;
import com.zhuaiwa.api.Rpc.FollowRequest;
import com.zhuaiwa.api.Rpc.FollowResponse;
import com.zhuaiwa.api.Rpc.GetCommentRequest;
import com.zhuaiwa.api.Rpc.GetCommentResponse;
import com.zhuaiwa.api.Rpc.GetContactRequest;
import com.zhuaiwa.api.Rpc.GetContactResponse;
import com.zhuaiwa.api.Rpc.GetFollowerRequest;
import com.zhuaiwa.api.Rpc.GetFollowerResponse;
import com.zhuaiwa.api.Rpc.GetFollowingRequest;
import com.zhuaiwa.api.Rpc.GetFollowingResponse;
import com.zhuaiwa.api.Rpc.GetGroupRequest;
import com.zhuaiwa.api.Rpc.GetGroupResponse;
import com.zhuaiwa.api.Rpc.GetInviterRequest;
import com.zhuaiwa.api.Rpc.GetInviterResponse;
import com.zhuaiwa.api.Rpc.GetInvitingRequest;
import com.zhuaiwa.api.Rpc.GetInvitingResponse;
import com.zhuaiwa.api.Rpc.GetMemberRequest;
import com.zhuaiwa.api.Rpc.GetMemberResponse;
import com.zhuaiwa.api.Rpc.GetMessageByIdRequest;
import com.zhuaiwa.api.Rpc.GetMessageByIdResponse;
import com.zhuaiwa.api.Rpc.GetMessageByTimestampRequest;
import com.zhuaiwa.api.Rpc.GetMessageByTimestampResponse;
import com.zhuaiwa.api.Rpc.GetMessageRequest;
import com.zhuaiwa.api.Rpc.GetMessageResponse;
import com.zhuaiwa.api.Rpc.GetProfileRequest;
import com.zhuaiwa.api.Rpc.GetProfileResponse;
import com.zhuaiwa.api.Rpc.GetTorrentRequest;
import com.zhuaiwa.api.Rpc.GetTorrentResponse;
import com.zhuaiwa.api.Rpc.InviteRequest;
import com.zhuaiwa.api.Rpc.InviteResponse;
import com.zhuaiwa.api.Rpc.IsFollowerRequest;
import com.zhuaiwa.api.Rpc.IsFollowerResponse;
import com.zhuaiwa.api.Rpc.RemoveContactRequest;
import com.zhuaiwa.api.Rpc.RemoveContactResponse;
import com.zhuaiwa.api.Rpc.RemoveGroupRequest;
import com.zhuaiwa.api.Rpc.RemoveGroupResponse;
import com.zhuaiwa.api.Rpc.RemoveMemberRequest;
import com.zhuaiwa.api.Rpc.RemoveMemberResponse;
import com.zhuaiwa.api.Rpc.RemoveMessageRequest;
import com.zhuaiwa.api.Rpc.RemoveMessageResponse;
import com.zhuaiwa.api.Rpc.SendMessageRequest;
import com.zhuaiwa.api.Rpc.SendMessageResponse;
import com.zhuaiwa.api.Rpc.SetProfileRequest;
import com.zhuaiwa.api.Rpc.SetProfileResponse;
import com.zhuaiwa.api.Rpc.SetTorrentRequest;
import com.zhuaiwa.api.Rpc.SetTorrentResponse;
import com.zhuaiwa.api.Rpc.UnbindAccountRequest;
import com.zhuaiwa.api.Rpc.UnbindAccountResponse;
import com.zhuaiwa.api.Rpc.UnfollowRequest;
import com.zhuaiwa.api.Rpc.UnfollowResponse;
import com.zhuaiwa.api.SSDataDomain.CreateAccountRequest;
import com.zhuaiwa.api.SSDataDomain.CreateAccountResponse;
import com.zhuaiwa.api.SSDataDomain.GetAccountRequest;
import com.zhuaiwa.api.SSDataDomain.GetAccountResponse;
import com.zhuaiwa.api.SSDataDomain.SetAccountRequest;
import com.zhuaiwa.api.SSDataDomain.SetAccountResponse;
import com.zhuaiwa.api.SSDataDomain.SetMessageRequest;
import com.zhuaiwa.api.SSDataDomain.SetMessageResponse;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.util.SSIdUtils;
import com.zhuaiwa.dd.cmd.Command;
import com.zhuaiwa.dd.cmd.CreateCommand;
import com.zhuaiwa.dd.cmd.DeleteCommand;
import com.zhuaiwa.dd.cmd.ReadCommand;
import com.zhuaiwa.dd.cmd.UpdateCommand;
import com.zhuaiwa.dd.cmd.UpdateCommand.UpdateOperator;
import com.zhuaiwa.dd.config.DDProperties;
import com.zhuaiwa.dd.dao.AccountDao;
import com.zhuaiwa.dd.dao.FollowerDao;
import com.zhuaiwa.dd.dao.FollowingDao;
import com.zhuaiwa.dd.dao.MessageDao;
import com.zhuaiwa.dd.dao.ProfileDao;
import com.zhuaiwa.dd.domain.Account;
import com.zhuaiwa.dd.domain.Comment;
import com.zhuaiwa.dd.domain.Contact;
import com.zhuaiwa.dd.domain.EmailAccount;
import com.zhuaiwa.dd.domain.Favorite;
import com.zhuaiwa.dd.domain.FollowInfo;
import com.zhuaiwa.dd.domain.Following;
import com.zhuaiwa.dd.domain.Group;
import com.zhuaiwa.dd.domain.Group.GroupInfo;
import com.zhuaiwa.dd.domain.InBox;
import com.zhuaiwa.dd.domain.InviteInfo;
import com.zhuaiwa.dd.domain.Inviter;
import com.zhuaiwa.dd.domain.Inviting;
import com.zhuaiwa.dd.domain.Member;
import com.zhuaiwa.dd.domain.Member.MemberInfo;
import com.zhuaiwa.dd.domain.Message;
import com.zhuaiwa.dd.domain.MessageInfo;
import com.zhuaiwa.dd.domain.OutBox;
import com.zhuaiwa.dd.domain.PhoneAccount;
import com.zhuaiwa.dd.domain.Profile;
import com.zhuaiwa.dd.domain.PubBox;
import com.zhuaiwa.dd.domain.Torrent;
import com.zhuaiwa.dd.exception.DALException;
import com.zhuaiwa.dd.service.DataService;
import com.zhuaiwa.dd.service.impl.DataServiceImpl;

public class SSDataDomainSvcImpl implements DataDomainApi
{
	private static Logger logger = LoggerFactory
			.getLogger(SSDataDomainSvcImpl.class);

	private static List<String> getIdFromFollowInfo(
			List<FollowInfo> followinfo_list)
	{
		List<String> id_list = new ArrayList<String>();
		if (followinfo_list == null)
		{
			return id_list;
		}
		for (FollowInfo fi : followinfo_list)
		{
			id_list.add(fi.getUserid());
		}
		return id_list;
	}

	private static List<String> getIdFromInviteInfo(
			List<InviteInfo> inviteinfo_list)
	{
		List<String> id_list = new ArrayList<String>();
		if (inviteinfo_list == null)
		{
			return id_list;
		}
		for (InviteInfo fi : inviteinfo_list)
		{
			id_list.add(SSIdUtils.parseSingle(fi.getSsid()).getId());
		}
		return id_list;
	}

	private static List<String> getIdFromMessageInfo(
			List<MessageInfo> messageinfo_list)
	{
		List<String> id_list = new ArrayList<String>();
		if (messageinfo_list == null)
		{
			return id_list;
		}
		for (MessageInfo mi : messageinfo_list)
		{
			id_list.add(mi.getMsgid());
		}
		return id_list;
	}

	protected AccountDao accountDao;

	protected Keyspace cassandra;

	protected DataServiceImpl dataService;

	protected FollowerDao followerDao;

	protected FollowingDao followingDao;

	protected MessageDao messageDao;

	protected ProfileDao profileDao;

	protected ExecutorService repairService;

	private boolean use_new_getfollower = false;

	@Override
	public void addContact(NettyRpcController controller,
			AddContactRequest request, AddContactResponse.Builder response)
			throws DALException
	{
		CreateCommand command = new CreateCommand(cassandra).Object(
				Contact.CN_CONTACT).Where(request.getUserid());

		for (SSContact contact : request.getContactListList())
		{
			String contactid = null;
			if (contact.hasContactid())
			{
				contactid = contact.getContactid();
			}
			if ((contactid == null) || contactid.isEmpty())
			{
				contactid = DataServiceImpl.genContactId();
			}
			response.addContactidList(contactid);
			command.Insert(contactid, Contact.ContactInfo.FN_CONTACTID,
					contactid);

			for (SSId ssid : contact.getIdListList())
			{
				if (SSIdUtils.isEmpty(ssid))
				{
					continue;
				}
				if (SSIdUtils.isEmailId(ssid))
				{
					command.Insert(contactid,
							Contact.ContactInfo.FN_ALIAS_EMAIL, ssid.getId());
				}
				if (SSIdUtils.isPhoneId(ssid))
				{
					command.Insert(contactid,
							Contact.ContactInfo.FN_ALIAS_PHONE_NUMBER,
							ssid.getId());
				}
				if (SSIdUtils.isUserId(ssid))
				{
					command.Insert(contactid, Contact.ContactInfo.FN_USERID,
							ssid.getId());
				}
			}
			if (contact.hasNickname())
			{
				command.Insert(contactid,
						Contact.ContactInfo.FN_ALIAS_NICKNAME,
						contact.getNickname());
			}
		}

		command.execute();
		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void addGroup(NettyRpcController controller,
			AddGroupRequest request, AddGroupResponse.Builder response)
			throws DALException
	{

		CreateCommand command = new CreateCommand(cassandra).Object(
				Group.CN_GROUP).Where(request.getUserid());

		for (SSGroup group : request.getGroupListList())
		{

			String groupid = null;
			if (group.hasGroupid())
			{
				groupid = group.getGroupid();
			}
			if ((groupid == null) || groupid.isEmpty())
			{
				groupid = DataServiceImpl.genGroupId();
			}
			response.addGroupidList(groupid);

			command.Insert(groupid, Group.GroupInfo.FN_GROUPID, groupid);
			if (group.hasName())
			{
				command.Insert(groupid, Group.GroupInfo.FN_NAME,
						group.getName());
			}
			if (group.hasType())
			{
				command.Insert(groupid, Group.GroupInfo.FN_TYPE,
						group.getType());
			}
		}
		command.execute();

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void addMember(NettyRpcController controller,
			AddMemberRequest request, AddMemberResponse.Builder response)
			throws DALException
	{
		CreateCommand command = new CreateCommand(cassandra).Object(
				Member.CN_MEMBER).Where(request.getUserid());
		long now = System.currentTimeMillis();
		for (SSMember ssmember : request.getMemberListList())
		{
			for (String contactid : ssmember.getMembersList())
			{
				command.Insert(ssmember.getParentid(), contactid, now);
			}
		}
		command.execute();

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void bindAccount(NettyRpcController controller,
			BindAccountRequest request, BindAccountResponse.Builder response)
			throws DALException
	{
		if (SSIdUtils.isEmpty(request.getNewId()))
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("未设置绑定帐号。");
			return;
		}

		// 获取帐号
		Account account = accountDao.get(request.getUserid());
		if (account == null)
		{
			controller.setCode(SSResultCode.RC_NO_SUCH_OBJECT.getNumber());
			controller.setFailed("对象不存在");
			return;
		}

		Mutator<String> mutator = HFactory.createMutator(cassandra,
				StringSerializer.get());

		// 删除旧的索引
		String oldEmail = account.getEmail();
		if (SSIdUtils.isEmailId(request.getNewId()) && (oldEmail != null))
		{
			mutator.addDeletion(oldEmail, EmailAccount.CN_EMAIL_ACCOUNT);
		}
		String oldPhone = account.getPhoneNumber();
		if (SSIdUtils.isPhoneId(request.getNewId()) && (oldPhone != null))
		{
			mutator.addDeletion(oldPhone, PhoneAccount.CN_PHONE_ACCOUNT);
		}

		// 修改帐号的邮件或手机
		if (SSIdUtils.isEmailId(request.getNewId()))
		{
			mutator.addInsertion(
					request.getUserid(),
					Account.CN_ACCOUNT,
					createStringColumn(Account.FN_EMAIL, request.getNewId()
							.getId()));
		}
		if (SSIdUtils.isPhoneId(request.getNewId()))
		{
			mutator.addInsertion(
					request.getUserid(),
					Account.CN_ACCOUNT,
					createStringColumn(Account.FN_PHONE_NUMBER, request
							.getNewId().getId()));
		}

		// 创建新的索引
		if (SSIdUtils.isEmailId(request.getNewId()))
		{
			mutator.addInsertion(
					request.getNewId().getId(),
					EmailAccount.CN_EMAIL_ACCOUNT,
					createStringColumn(EmailAccount.FN_EMAIL, request
							.getNewId().getId()));
			mutator.addInsertion(
					request.getNewId().getId(),
					EmailAccount.CN_EMAIL_ACCOUNT,
					createStringColumn(EmailAccount.FN_USERID,
							account.getUserid()));
		}
		if (SSIdUtils.isPhoneId(request.getNewId()))
		{
			mutator.addInsertion(
					request.getNewId().getId(),
					PhoneAccount.CN_PHONE_ACCOUNT,
					createStringColumn(PhoneAccount.FN_PHONE_NUMBER, request
							.getNewId().getId()));
			mutator.addInsertion(
					request.getNewId().getId(),
					PhoneAccount.CN_PHONE_ACCOUNT,
					createStringColumn(PhoneAccount.FN_USERID,
							account.getUserid()));
		}

		try
		{
			mutator.execute();
		}
		catch (HectorException e)
		{
			throw new DALException(e);
		}
		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void createAccount(NettyRpcController controller,
			CreateAccountRequest request, CreateAccountResponse.Builder response)
			throws DALException
	{

		// 参数检查
		if (!request.hasEmail() && !request.hasPhoneNumber())
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("Email和手机号至少填写一个");
			return;
		}

		// 生成用户ID
		String userid = request.hasUserid() ? request.getUserid()
				: DataServiceImpl.genUserId();

		// 创建帐号
		Mutator<String> mutator = HFactory.createMutator(cassandra,
				StringSerializer.get());
		mutator.addInsertion(userid, Account.CN_ACCOUNT,
				createStringColumn(Account.FN_USERID, userid));
		mutator.addInsertion(
				userid,
				Account.CN_ACCOUNT,
				createLongColumn(Account.FN_REGISTER_TIME,
						System.currentTimeMillis()));
		mutator.addInsertion(userid, Account.CN_ACCOUNT,
				createIntegerColumn(Account.FN_FIRST_LOGIN, new Integer(1)));
		if (request.hasEmail())
		{
			mutator.addInsertion(userid, Account.CN_ACCOUNT,
					createStringColumn(Account.FN_EMAIL, request.getEmail()));
		}
		if (request.hasPhoneNumber())
		{
			mutator.addInsertion(
					userid,
					Account.CN_ACCOUNT,
					createStringColumn(Account.FN_PHONE_NUMBER,
							request.getPhoneNumber()));
		}

		// 创建帐号映射
		if (request.hasEmail())
		{
			mutator.addInsertion(request.getEmail(),
					EmailAccount.CN_EMAIL_ACCOUNT,
					createStringColumn(EmailAccount.FN_USERID, userid));
			mutator.addInsertion(
					request.getEmail(),
					EmailAccount.CN_EMAIL_ACCOUNT,
					createStringColumn(EmailAccount.FN_EMAIL,
							request.getEmail()));
		}

		if (request.hasPhoneNumber())
		{
			mutator.addInsertion(request.getPhoneNumber(),
					PhoneAccount.CN_PHONE_ACCOUNT,
					createStringColumn(PhoneAccount.FN_USERID, userid));
			mutator.addInsertion(
					request.getPhoneNumber(),
					PhoneAccount.CN_PHONE_ACCOUNT,
					createStringColumn(PhoneAccount.FN_PHONE_NUMBER,
							request.getPhoneNumber()));
		}

		try
		{
			mutator.execute();
		}
		catch (HectorException e)
		{
			throw new DALException(e);
		}

		// 返回用户ID
		response.setUserid(userid);
		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	public void Dispose()
	{
	}

	@Override
	public void follow(NettyRpcController controller, FollowRequest request,
			FollowResponse.Builder response) throws DALException
	{

		if (!request.hasFollowerUserid())
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("follower_userid不能为空");
			return;
		}

		if (request.getFollowingUseridListCount() == 0)
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("following_userid_list不能为空");
			return;
		}

		final String followeruserid = request.getFollowerUserid();
		final List<String> followinguseridlist = new ArrayList<String>(
				request.getFollowingUseridListList());
		final long timestamp = System.currentTimeMillis();

		try
		{
			// SliceQuery<String,String,ByteBuffer> query =
			// HFactory.createSliceQuery(cassandra, StringSerializer.get(),
			// StringSerializer.get(), ByteBufferSerializer.get());
			// query.setKey(followeruserid);
			// query.setColumnFamily(Following.CN_FOLLOWING);
			// for (String followinguserid : followinguseridlist) {
			// query.setColumnNames(followinguserid);
			// }
			// QueryResult<ColumnSlice<String, ByteBuffer>> result =
			// query.execute();
			// ColumnSlice<String, ByteBuffer> slice = (result == null ? null :
			// result.get());
			// for (HColumn<String, ByteBuffer> column : slice.getColumns()) {
			// followinguseridlist.remove(StringSerializer.get().fromByteBuffer(column.getValue()));
			// }

			// 剔除已经跟随的人
			List<String> following_list = getIdFromFollowInfo(dataService
					.isFollowing(followeruserid, followinguseridlist));
			if (following_list != null)
			{
				followinguseridlist.removeAll(following_list);
			}

			// final int exist_following_list_size = (following_list != null ?
			// following_list.size() : 0);
			// final int added_following_list_size = (followinguseridlist !=
			// null ? followinguseridlist.size() : 0);
			// final IntHolder original_following_count = new IntHolder(-1);
			// final IntHolder updated_following_count = new IntHolder(-1);

			if (followinguseridlist.size() > 0)
			{
				// 修改关注列表
				CreateCommand command = new CreateCommand(cassandra).Object(
						Following.CN_FOLLOWING).Where(followeruserid);
				for (String following : followinguseridlist)
				{
					command.Insert(following, FollowInfo.FN_USERID, following);
					command.Insert(following, FollowInfo.FN_TIMESTAMP,
							timestamp);
				}
				command.execute();

				// 修改关注者数量
				final UpdateCommand updateCommand = new UpdateCommand(cassandra)
						.Object(Profile.CN_PROFILE).Where(followeruserid);
				updateCommand.Update(Profile.FN_FOLLOWING_COUNT,
						new UpdateOperator()
						{
							@Override
							public ByteBuffer handle(String key,
									ByteBuffer supername, ByteBuffer name,
									ByteBuffer value)
							{
								Integer count = new Integer(0);
								if (value != null)
								{
									count = IntegerSerializer.get()
											.fromByteBuffer(value);
								}
								// original_following_count.value = count;
								count += followinguseridlist.size();
								// updated_following_count.value = count;
								return IntegerSerializer.get().toByteBuffer(
										count);
							}
						});
				updateCommand.execute();

			}
		}
		finally
		{
		}

		repairService.submit(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{ // begin of back task

					for (String followinguserid : followinguseridlist)
					{
						// ClusterMutex mutex = null;
						// try {
						// mutex = ClusterMutex.instance();
						// } catch (Exception e) {
						// mutex = null;
						// }
						try
						{

							// 修改跟随者列表
							// CreateCommand command =
							// new CreateCommand(cassandra)
							// .Object(Follower.CN_FOLLOWER)
							// .Where(followinguserid)
							// .Insert(followeruserid, FollowInfo.FN_USERID,
							// followeruserid)
							// .Insert(followeruserid, FollowInfo.FN_TIMESTAMP,
							// timestamp);
							// command.execute();

							followerDao.insert(followinguserid, followeruserid,
									timestamp);

							// 修改跟随者数量
							final UpdateCommand updateCommand = new UpdateCommand(
									cassandra).Object(Profile.CN_PROFILE)
									.Where(followinguserid);
							updateCommand.Update(Profile.FN_FOLLOWER_COUNT,
									new UpdateOperator()
									{
										@Override
										public ByteBuffer handle(String key,
												ByteBuffer supername,
												ByteBuffer name,
												ByteBuffer value)
										{
											if (value != null)
											{
												Integer count = IntegerSerializer
														.get().fromByteBuffer(
																value);
												count++;
												return IntegerSerializer.get()
														.toByteBuffer(count);
											}
											else
											{
												return IntegerSerializer.get()
														.toByteBuffer(
																new Integer(1));
											}
										}
									});
							updateCommand.execute();
						}
						finally
						{
						}
					}

				}
				catch (Exception e)
				{
					logger.error("", e);
				}
			}
		}); // end of back task

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void getAccount(NettyRpcController controller,
			GetAccountRequest request, GetAccountResponse.Builder response)
			throws DALException
	{

		// 根据email查询
		if ((request.getEmailCount() > 0) && (request.getEmailList() != null)
				&& (request.getEmailList().size() > 0))
		{
			List<String> useridlist = accountDao.getUseridByEmail(request
					.getEmailList());
			response.addAllUseridList(useridlist);
		}

		// 根据phone查询
		if ((request.getPhoneNumberCount() > 0)
				&& (request.getPhoneNumberList() != null)
				&& (request.getPhoneNumberList().size() > 0))
		{
			List<String> useridlist = accountDao.getUseridByPhone(request
					.getPhoneNumberList());
			response.addAllUseridList(useridlist);
		}

		// 根据name查询
		if ((request.getNameCount() > 0) && (request.getNameList() != null)
				&& (request.getNameList().size() > 0))
		{
			List<String> useridlist = accountDao.getUseridByName(request
					.getNameList());
			response.addAllUseridList(useridlist);
		}

		if (!request.hasMode() || (request.getMode() == 1))
		{
			Map<String, Account> result = accountDao.get(response
					.getUseridListList());
			for (Account account : result.values())
			{
				SSAccount.Builder ssaccount = SSAccount.newBuilder();
				DataServiceImpl.build(ssaccount, account);
				response.addAccount(ssaccount);
			}
		}

		// 根据id查询
		if (request.getUseridCount() > 0)
		{
			Map<String, Account> result = accountDao.get(request
					.getUseridList());
			for (Entry<String, Account> entry : result.entrySet())
			{
				response.addUseridList(entry.getKey());
				if (!request.hasMode() || (request.getMode() == 1))
				{
					SSAccount.Builder ssaccount = SSAccount.newBuilder();
					DataServiceImpl.build(ssaccount, entry.getValue());
					response.addAccount(ssaccount);
				}
			}
		}

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	public AccountDao getAccountDao()
	{
		return accountDao;
	}

	public Keyspace getCassandra()
	{
		return cassandra;
	}

	@Override
	public void getComment(NettyRpcController controller,
			GetCommentRequest request, GetCommentResponse.Builder response)
			throws DALException
	{
		BooleanHolder out_eol = new BooleanHolder(false);

		List<String> msgid_list = getIdFromMessageInfo(dataService.getComment(
				request.getMsgid(), request.getCursorId(), request.getCount(),
				out_eol));
		if (msgid_list == null)
		{
			msgid_list = Collections.emptyList();
			out_eol.value = true;
		}

		response.addAllMsgidList(msgid_list).setEol(out_eol.value);

		if (request.hasMode() && (request.getMode() == 1))
		{
			List<Message> messages = dataService.getMessage(msgid_list);
			if (messages != null)
			{
				for (Message message : messages)
				{
					SSMessage.Builder ssmessage_builder = SSMessage
							.newBuilder();
					DataServiceImpl.build(ssmessage_builder, message);
					response.addMsgList(ssmessage_builder);
				}
			}
		}

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void getContact(NettyRpcController controller,
			GetContactRequest request, GetContactResponse.Builder response)
			throws DALException
	{

		List<SSContact> ssContactList = dataService.getContact(request
				.getUserid());
		if (ssContactList == null)
		{
			// controller.setCode(SSResultCode.RC_NO_SUCH_OBJECT.getNumber());
			// controller.setFailed("对象不存在");
			return;
		}

		response.addAllContactList(ssContactList);
		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	public DataService getDataService()
	{
		return dataService;
	}

	@Override
	public void getFollower(NettyRpcController controller,
			GetFollowerRequest request, GetFollowerResponse.Builder response)
			throws DALException
	{
		BooleanHolder out_eol = new BooleanHolder(false);
		List<String> followerid_list = null;

		if (use_new_getfollower)
		{
			int count = (request.getCount() == Integer.MIN_VALUE ? Integer.MAX_VALUE
					: Math.abs(request.getCount()));
			boolean reversed = request.getCount() > 0;

			FollowInfo fi = null;
			if (request.hasCursorId())
			{
				fi = followerDao
						.get(request.getUserid(), request.getCursorId());
			}
			followerid_list = getIdFromFollowInfo(followerDao.getFollower(
					request.getUserid(), fi, count, reversed, out_eol));
		}
		else
		{
			followerid_list = getIdFromFollowInfo(dataService.getFollower(
					request.getUserid(), request.getCursorId(),
					request.getCount(), out_eol));
		}
		if (followerid_list == null)
		{
			followerid_list = Collections.emptyList();
			out_eol.value = true;
		}

		response.addAllFollowerUseridList(followerid_list)
				.setEol(out_eol.value);

		if (request.hasMode() && (request.getMode() == 1))
		{
			List<Profile> profiles = dataService.getProfile(followerid_list, 1);
			if (profiles != null)
			{
				for (Profile profile : profiles)
				{
					SSProfile.Builder ssprofile_builder = SSProfile
							.newBuilder();
					DataServiceImpl.build(ssprofile_builder, profile);
					response.addFollowerUserList(ssprofile_builder);
				}
			}
		}

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	public FollowerDao getFollowerDao()
	{
		return followerDao;
	}

	@Override
	public void getFollowing(NettyRpcController controller,
			GetFollowingRequest request, GetFollowingResponse.Builder response)
			throws DALException
	{
		List<String> following_list = getIdFromFollowInfo(dataService
				.getFollowing(request.getUserid()));
		// List<String> following_list =
		// dataService.getFollowing(request.getUserid());
		if (following_list == null)
		{
			following_list = Collections.emptyList();
		}
		response.addAllFollowingUseridList(following_list);

		if (request.hasMode() && (request.getMode() == 1))
		{
			List<Profile> profiles = dataService.getProfile(following_list, 1);
			if (profiles != null)
			{
				for (Profile profile : profiles)
				{
					SSProfile.Builder ssprofile_builder = SSProfile
							.newBuilder();
					DataServiceImpl.build(ssprofile_builder, profile);
					response.addFollowingUserList(ssprofile_builder);
				}
			}
		}
		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	public FollowingDao getFollowingDao()
	{
		return followingDao;
	}

	@Override
	public void getGroup(NettyRpcController controller,
			GetGroupRequest request, GetGroupResponse.Builder response)
			throws DALException
	{
		ReadCommand command = new ReadCommand(cassandra).Object(Group.class)
				.Where(request.getUserid()).Select();
		Group group = (Group) command.execute().get(request.getUserid());
		if (group == null)
		{
			// controller.setCode(SSResultCode.RC_NO_SUCH_OBJECT.getNumber());
			// controller.setFailed("对象不存在");
			return;
		}

		List<SSGroup> ssgrouplist = new ArrayList<SSGroup>();
		for (GroupInfo groupInfo : group.getGroups())
		{
			SSGroup.Builder builder = SSGroup.newBuilder();
			DataServiceImpl.build(builder, groupInfo);
			ssgrouplist.add(builder.build());
		}

		response.addAllGroupList(ssgrouplist);
		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void getInviter(NettyRpcController controller,
			GetInviterRequest request, GetInviterResponse.Builder response)
			throws DALException
	{
		List<String> inviterList = getIdFromInviteInfo(dataService
				.getInviter(request.getUserid()));
		if (inviterList == null)
		{
			inviterList = Collections.emptyList();
		}

		response.addAllInviterUseridList(inviterList);

		if (request.hasMode() && (request.getMode() == 1))
		{
			List<Profile> profiles = dataService.getProfile(inviterList, 1);
			if (profiles != null)
			{
				for (Profile profile : profiles)
				{
					SSProfile.Builder ssprofile_builder = SSProfile
							.newBuilder();
					DataServiceImpl.build(ssprofile_builder, profile);
					response.addInviterUserList(ssprofile_builder);
				}
			}
		}

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void getInviting(NettyRpcController controller,
			GetInvitingRequest request, GetInvitingResponse.Builder response)
			throws DALException
	{
		List<String> invitingList = getIdFromInviteInfo(dataService
				.getInviting(request.getUserid()));
		if (invitingList == null)
		{
			// controller.setCode(SSResultCode.RC_NO_SUCH_OBJECT.getNumber());
			// controller.setFailed("对象不存在");
			return;
		}
		response.addAllInvitingUserList(invitingList);
		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void getMember(NettyRpcController controller,
			GetMemberRequest request, GetMemberResponse.Builder response)
			throws DALException
	{
		ReadCommand command = new ReadCommand(cassandra).Object(Member.class)
				.Where(request.getUserid()).Select();
		Member member = command.<Member> execute().get(request.getUserid());
		if (member == null)
		{
			// controller.setCode(SSResultCode.RC_NO_SUCH_OBJECT.getNumber());
			// controller.setFailed("对象不存在");
			return;
		}

		List<SSMember> ssmembers = new ArrayList<SSMember>();
		for (MemberInfo memberinfo : member.getMembers())
		{
			SSMember ssmember = SSMember.newBuilder()
					.setParentid(memberinfo.getGroupid())
					.addAllMembers(memberinfo.getContacts().keySet()).build();
			ssmembers.add(ssmember);
		}
		response.addAllMemberList(ssmembers);

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void getMessage(NettyRpcController controller,
			GetMessageRequest request, GetMessageResponse.Builder response)
			throws DALException
	{
		if (request.getCount() == 0)
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("参数count不能等于0。");
			return;
		}
		BooleanHolder eol = new BooleanHolder(false);
		List<String> messageidlist = getIdFromMessageInfo(dataService
				.getMessageIdList(request.getUserid(), request.getSourceBox(),
						request.getCursorId(), request.getCount(), eol));

		if (messageidlist == null)
		{
			// controller.setCode(SSResultCode.RC_NO_SUCH_OBJECT.getNumber());
			// controller.setFailed("对象不存在");
			return;
		}
		response.addAllMsgidList(messageidlist);
		response.setEol(eol.value);

		if (request.hasMode() && (request.getMode() == 1))
		{
			List<Message> messages = dataService.getMessage(messageidlist);
			if (messages != null)
			{
				for (Message message : messages)
				{
					SSMessage.Builder ssmessage_builder = SSMessage
							.newBuilder();
					DataServiceImpl.build(ssmessage_builder, message);
					response.addMsgList(ssmessage_builder);
				}
			}
		}

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void getMessageById(NettyRpcController controller,
			GetMessageByIdRequest request,
			GetMessageByIdResponse.Builder response) throws DALException
	{
		if (request.getMsgidListCount() == 0)
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("消息ID不能为空");
			return;
		}

		List<Message> message_list = dataService.getMessage(request
				.getMsgidListList());
		if ((message_list == null) || (message_list.size() == 0))
		{
			// controller.setCode(SSResultCode.RC_NO_SUCH_OBJECT.getNumber());
			// controller.setFailed("对象不存在");
			return;
		}

		for (Message message : message_list)
		{
			SSMessage.Builder builder = SSMessage.newBuilder();
			DataServiceImpl.build(builder, message);
			response.addMsgList(builder);
		}

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void getMessageByTimestamp(NettyRpcController controller,
			GetMessageByTimestampRequest request,
			GetMessageByTimestampResponse.Builder response) throws DALException
	{
		if (request.getCount() <= 0)
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("参数count必须大于0。");
			return;
		}
		if ((request.hasStartTimestamp() && (request.getStartTimestamp() < 0))
				|| (request.hasEndTimestamp() && (request.getEndTimestamp() < 0)))
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("参数start，end必须大于等于0。");
			return;
		}
		long start = request.hasStartTimestamp() ? request.getStartTimestamp()
				: System.currentTimeMillis();
		long end = request.hasEndTimestamp() ? request.getEndTimestamp()
				: System.currentTimeMillis();

		// String cursorId = null;
		// String limit = null;
		// int count = request.getCount();
		// if (start < end) {
		// cursorId = DataServiceImpl.genMaxMessageId(start);
		// limit = DataServiceImpl.genMaxMessageId(end);
		// } else {
		// cursorId = DataServiceImpl.genMinMessageId(start);
		// limit = DataServiceImpl.genMinMessageId(end);
		// count = -count;
		// }
		//
		// List<String> messageidlist =
		// getIdFromMessageInfo(dataService.getMessageIdList(request.getUserid(),
		// request.getSourceBox(), cursorId, limit, count, null));
		// if (messageidlist == null) {
		// return;
		// }

		List<String> messageidlist = getIdFromMessageInfo(dataService
				.getMessageIdListByTimestamp(request.getUserid(),
						request.getSourceBox(), start, end, request.getCount()));
		if (messageidlist == null)
		{
			return;
		}

		response.addAllMsgidList(messageidlist);
		// response.setEol(eol.value);

		if (request.hasMode() && (request.getMode() == 1))
		{
			List<Message> messages = dataService.getMessage(messageidlist);
			if (messages != null)
			{
				for (Message message : messages)
				{
					SSMessage.Builder ssmessage_builder = SSMessage
							.newBuilder();
					DataServiceImpl.build(ssmessage_builder, message);
					response.addMsgList(ssmessage_builder);
				}
			}
		}

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	public MessageDao getMessageDao()
	{
		return messageDao;
	}

	private List<SSProfile> getProfile(List<String> userid_list, int flag)
			throws DALException
	{
		List<SSProfile> ssprofiles = null;

		List<Profile> map = dataService.getProfile(userid_list, flag);
		if (map == null)
		{
			return null;
		}

		for (Profile profile : map)
		{
			SSProfile.Builder builder = SSProfile.newBuilder();
			DataServiceImpl.build(builder, profile);
			if (ssprofiles == null)
			{
				ssprofiles = new ArrayList<SSProfile>();
			}
			ssprofiles.add(builder.build());
		}

		return ssprofiles;
	}

	@Override
	public void getProfile(NettyRpcController controller,
			GetProfileRequest request, GetProfileResponse.Builder response)
			throws DALException
	{
		List<SSProfile> ssprofiles = getProfile(request.getUseridListList(),
				request.getFlag());
		if (ssprofiles == null)
		{
			// controller.setCode(SSResultCode.RC_NO_SUCH_OBJECT.getNumber());
			// controller.setFailed("对象不存在");
			return;
		}
		response.addAllProfileList(ssprofiles);
		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	public ProfileDao getProfileDao()
	{
		return profileDao;
	}

	public ExecutorService getRepairService()
	{
		return repairService;
	}

	@Override
	public void getTorrent(NettyRpcController controller,
			GetTorrentRequest request, GetTorrentResponse.Builder response)
			throws DALException
	{
		ReadCommand command = new ReadCommand(cassandra).Object(Torrent.class)
				.Select().Where(request.getKey());
		Torrent torrent = command.<Torrent> execute().get(request.getKey());
		if ((torrent == null) || (torrent.getValue() == null)
				|| (torrent.getValue().length == 0))
		{
			// controller.setCode(SSResultCode.RC_NO_SUCH_OBJECT.getNumber());
			// controller.setFailed("对象不存在");
			return;
		}
		response.setValue(ByteString.copyFrom(torrent.getValue()));
		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	public void Initialize()
	{
		use_new_getfollower = Boolean.parseBoolean(DDProperties.getProperty(
				"dd.api.use_new_getfollower", "false"));
	}

	@Override
	public void invite(NettyRpcController controller, InviteRequest request,
			InviteResponse.Builder response) throws DALException
	{
		CreateCommand command = new CreateCommand(cassandra).Object(
				Inviting.CN_INVITING).Where(request.getInviterUserid());
		for (String inviting : request.getInvitingUserListList())
		{
			command.Insert(inviting, InviteInfo.FN_SSID, inviting);
		}
		command.execute();

		CreateCommand command2 = new CreateCommand(cassandra)
				.Object(Inviter.CN_INVITER)
				.Where(request.getInvitingUserListList().toArray(new String[0]))
				.Insert(request.getInviterUserid(), InviteInfo.FN_SSID,
						request.getInviterUserid());
		command2.execute();

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void isFollower(NettyRpcController controller,
			IsFollowerRequest request, IsFollowerResponse.Builder response)
			throws DALException
	{
		if (request.getUseridListCount() == 0)
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("要查询的用户ID不能为空");
			return;
		}

		List<String> follower_list = getIdFromFollowInfo(dataService
				.isFollower(request.getUserid(), request.getUseridListList()));
		if (follower_list == null)
		{
			follower_list = Collections.emptyList();
		}
		response.addAllFollowerUseridList(follower_list);
		if (request.hasMode() && (request.getMode() == 1))
		{
			List<String> followerid_list = new ArrayList<String>();
			followerid_list.addAll(follower_list);
			List<Profile> profiles = dataService.getProfile(followerid_list, 1);
			if (profiles != null)
			{
				for (Profile profile : profiles)
				{
					SSProfile.Builder ssprofile_builder = SSProfile
							.newBuilder();
					DataServiceImpl.build(ssprofile_builder, profile);
					response.addFollowerUserList(ssprofile_builder);
				}
			}
		}

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void removeContact(NettyRpcController controller,
			RemoveContactRequest request, RemoveContactResponse.Builder response)
			throws DALException
	{
		DeleteCommand command = new DeleteCommand(cassandra)
				.Object(Contact.CN_CONTACT)
				.Where(request.getUserid())
				.DeleteColumn(
						request.getContactidListList().toArray(new String[0]));
		command.execute();

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void removeGroup(NettyRpcController controller,
			RemoveGroupRequest request, RemoveGroupResponse.Builder response)
			throws DALException
	{
		DeleteCommand command = new DeleteCommand(cassandra)
				.Object(Group.CN_GROUP)
				.Where(request.getUserid())
				.DeleteColumn(
						request.getGroupidListList().toArray(new String[0]));
		command.execute();

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void removeMember(NettyRpcController controller,
			RemoveMemberRequest request, RemoveMemberResponse.Builder response)
			throws DALException
	{
		DeleteCommand command = new DeleteCommand(cassandra).Object(
				Member.CN_MEMBER).Where(request.getUserid());
		for (SSMember ssmember : request.getMemberListList())
		{
			if (ssmember.getMembersList().isEmpty())
			{
				command.DeleteColumn(ssmember.getParentid());
			}
			else
			{
				command.DeleteSubcolumn(ssmember.getParentid(), ssmember
						.getMembersList().toArray(new String[0]));
			}
		}
		command.execute();

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void removeMessage(NettyRpcController controller,
			RemoveMessageRequest request, RemoveMessageResponse.Builder response)
			throws DALException
	{
		if (!request.hasUserid())
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("用户ID列表不能为空");
			return;
		}
		if (request.getMsgidListCount() == 0)
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("消息ID列表不能为空");
			return;
		}
		if (!request.hasSourceBox())
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("没有指定SourceBox。");
			return;
		}

		DeleteCommand command = new DeleteCommand(cassandra);
		switch (request.getSourceBox())
		{
		case PUBBOX:
			command.Object(PubBox.CN_PUBBOX);
			break;
		case INBOX:
			command.Object(InBox.CN_INBOX);
			break;
		case OUTBOX:
			command.Object(OutBox.CN_OUTBOX);
			break;
		case FAVBOX:
			command.Object(Favorite.CN_FAVORITE);
			break;
		}
		command.Where(request.getUserid()).DeleteColumn(
				request.getMsgidListList().toArray(
						new String[request.getMsgidListCount()]));
		command.execute();

		controller.setCode(SSResultCode.RC_OK.getNumber());

	}

	/**
	 * 对于指定分享的情况，如果接收者很多将导致严重的性能问题。将来考虑采用异步方式解决。
	 */
	@Override
	public void sendMessage(NettyRpcController controller,
			SendMessageRequest request, SendMessageResponse.Builder response)
			throws DALException
	{
		// 参数检查
		SSMessage ssmessage = request.getMsg();

		if ((ssmessage.getSender() == null)
				|| SSIdUtils.isEmpty(ssmessage.getSender()))
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("没有指定发送者。");
			return;
		}

		if ((ssmessage.getShareType() == SSShareType.SHARE_TYPE_PROTECTED
				.getNumber()) && (ssmessage.getReceiverCount() == 0))
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("没有指定接收者。");
			return;
		}

		if (!SSIdUtils.isUserId(ssmessage.getSender()))
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("发送者必须用userid表示。");
			return;
		}

		long now = ssmessage.hasTimestamp() ? ssmessage.getTimestamp() : System
				.currentTimeMillis();
		String msgid = null;

		if (ssmessage.getShareType() == SSShareType.SHARE_TYPE_PUBLIC
				.getNumber())
		{
			msgid = dataService.AddMessage(ssmessage,
					SSShareType.SHARE_TYPE_PUBLIC, now);
			CreateCommand addtopubbox = new CreateCommand(cassandra)
					.Object(PubBox.CN_PUBBOX)
					.Where(ssmessage.getSender().getId())
					.Insert(msgid, MessageInfo.FN_MSGID, msgid);
			addtopubbox.execute();
		}
		else if (ssmessage.getShareType() == SSShareType.SHARE_TYPE_PROTECTED
				.getNumber())
		{
			msgid = dataService.AddMessage(ssmessage,
					SSShareType.SHARE_TYPE_PROTECTED, now);

			HashSet<String> useridlist = new HashSet<String>();
			for (SSPerson receiver : ssmessage.getReceiverList())
			{
				if (StringUtils.isNotEmpty(receiver.getUserid()))
				{
					useridlist.add(receiver.getUserid());
				}
			}

			// @消息
			if (ssmessage.getMsgType() == SSMessageType.MESSAGE_TYPE_AT
					.getNumber())
			{
				// TODO
			}
			else
			{
			}
			// 收件箱
			CreateCommand addtoinbox = new CreateCommand(cassandra)
					.Object(InBox.CN_INBOX)
					.Where(useridlist.toArray(new String[useridlist.size()]))
					.Insert(msgid, MessageInfo.FN_MSGID, msgid);
			addtoinbox.execute();
			// 发送箱
			CreateCommand addtooutbox = new CreateCommand(cassandra)
					.Object(OutBox.CN_OUTBOX)
					.Where(ssmessage.getSender().getId())
					.Insert(msgid, MessageInfo.FN_MSGID, msgid);
			addtooutbox.execute();
		}
		else if (ssmessage.getShareType() == SSShareType.SHARE_TYPE_PRIVATE
				.getNumber())
		{
			msgid = dataService.AddMessage(ssmessage,
					SSShareType.SHARE_TYPE_PRIVATE, now);

			CreateCommand addtofavorite = new CreateCommand(cassandra)
					.Object(Favorite.CN_FAVORITE)
					.Where(ssmessage.getSender().getId())
					.Insert(msgid, MessageInfo.FN_MSGID, msgid);
			addtofavorite.execute();
		}
		else
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("未知的分享类型。");
			return;
		}

		//
		// 公开的消息，建立评论的索引
		//
		if (ssmessage.getShareType() == SSShareType.SHARE_TYPE_PUBLIC
				.getNumber())
		{
			if (ssmessage.hasParentId()
					&& StringUtils.isNotEmpty(ssmessage.getParentId()))
			{
				CreateCommand addtocomment = new CreateCommand(cassandra)
						.Object(Comment.CN_COMMENT)
						.Where(ssmessage.getParentId())
						.Insert(msgid, MessageInfo.FN_MSGID, msgid);
				addtocomment.execute();
			}
		}

		response.setMsgid(msgid);
		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void setAccount(NettyRpcController controller,
			SetAccountRequest request, SetAccountResponse.Builder response)
			throws DALException
	{
		// 参数检查
		if (!request.hasAccount())
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("参数account不能为空。");
			return;
		}

		if (!request.hasUserid())
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("参数userid不能为空。");
			return;
		}

		SSAccount account = request.getAccount();
		if (account.getAliasIdListCount() > 0)
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("不允许通过此方法修改帐号的邮箱和手机号。");
			return;
		}

		// 修改帐号
		Mutator<String> mutator = HFactory.createMutator(cassandra,
				StringSerializer.get());
		if (account.hasPassword())
		{
			mutator.addInsertion(
					request.getUserid(),
					Account.CN_ACCOUNT,
					createStringColumn(Account.FN_PASSWORD,
							account.getPassword()));
		}
		if (account.hasIsActive())
		{
			mutator.addInsertion(
					request.getUserid(),
					Account.CN_ACCOUNT,
					createIntegerColumn(Account.FN_IS_ACTIVATED,
							account.getIsActive()));
		}
		if (account.hasIsFirstLogin())
		{
			mutator.addInsertion(
					request.getUserid(),
					Account.CN_ACCOUNT,
					createIntegerColumn(Account.FN_FIRST_LOGIN,
							account.getIsFirstLogin()));
		}
		if (account.hasRegisterTime())
		{
			mutator.addInsertion(
					request.getUserid(),
					Account.CN_ACCOUNT,
					createLongColumn(Account.FN_REGISTER_TIME,
							account.getRegisterTime()));
		}
		if (account.hasRole())
		{
			mutator.addInsertion(request.getUserid(), Account.CN_ACCOUNT,
					createIntegerColumn(Account.FN_ROLE, account.getRole()));
		}
		if (account.hasSecurityCode())
		{
			mutator.addInsertion(
					request.getUserid(),
					Account.CN_ACCOUNT,
					createStringColumn(Account.FN_SECURITY_CODE,
							account.getSecurityCode()));
		}
		if (account.hasSecurityCodeTime())
		{
			mutator.addInsertion(
					request.getUserid(),
					Account.CN_ACCOUNT,
					createLongColumn(Account.FN_SECURITY_CODE_TIME,
							account.getSecurityCodeTime()));
		}
		if (account.hasIsEmailHidden())
		{
			mutator.addInsertion(
					request.getUserid(),
					Account.CN_ACCOUNT,
					createIntegerColumn(Account.FN_IS_EMAIL_HIDDEN,
							account.getIsEmailHidden()));
		}
		if (account.hasIsPhoneHidden())
		{
			mutator.addInsertion(
					request.getUserid(),
					Account.CN_ACCOUNT,
					createIntegerColumn(Account.FN_IS_PHONE_HIDDEN,
							account.getIsPhoneHidden()));
		}
		if (account.hasIsEducationHidden())
		{
			mutator.addInsertion(
					request.getUserid(),
					Account.CN_ACCOUNT,
					createIntegerColumn(Account.FN_IS_EDUCATION_HIDDEN,
							account.getIsEducationHidden()));
		}
		if (account.hasIsWorkHidden())
		{
			mutator.addInsertion(
					request.getUserid(),
					Account.CN_ACCOUNT,
					createIntegerColumn(Account.FN_IS_WORK_HIDDEN,
							account.getIsWorkHidden()));
		}
		if (account.hasIsNicknameHidden())
		{
			mutator.addInsertion(
					request.getUserid(),
					Account.CN_ACCOUNT,
					createIntegerColumn(Account.FN_IS_NICKNAME_HIDDEN,
							account.getIsNicknameHidden()));
		}
		if (account.hasMessageFilter())
		{
			mutator.addInsertion(
					request.getUserid(),
					Account.CN_ACCOUNT,
					createIntegerColumn(Account.FN_MESSAGE_FILTER,
							account.getMessageFilter()));
		}
		if (account.hasShareForbiddenTime())
		{
			mutator.addInsertion(
					request.getUserid(),
					Account.CN_ACCOUNT,
					createLongColumn(Account.FN_SHARE_FORBIDDEN_TIME,
							account.getShareForbiddenTime()));
		}
		try
		{
			mutator.execute();
		}
		catch (HectorException e)
		{
			throw new DALException(e);
		}
		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	public void setAccountDao(AccountDao accountDao)
	{
		this.accountDao = accountDao;
	}

	public void setCassandra(Keyspace cassandra)
	{
		this.cassandra = cassandra;
	}

	public void setDataService(DataServiceImpl dataService)
	{
		this.dataService = dataService;
	}

	public void setFollowerDao(FollowerDao followerDao)
	{
		this.followerDao = followerDao;
	}

	public void setFollowingDao(FollowingDao followingDao)
	{
		this.followingDao = followingDao;
	}

	@Override
	public void setMessage(NettyRpcController controller,
			SetMessageRequest request, SetMessageResponse.Builder response)
			throws DALException
	{
		if (!request.hasMsgid())
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("缺少参数。");
			return;
		}
		if (!request.hasMsg())
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("缺少参数。");
			return;
		}

		SSMessage ssmessage = request.getMsg();

		// 参数检查
		if (ssmessage.hasSender())
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("不可以修改发送者。");
			return;
		}

		if (ssmessage.getReceiverCount() > 0)
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("不可以修改接收者。");
			return;
		}

		if (ssmessage.hasMsgid())
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("不可以修改消息ID。");
			return;
		}

		if (ssmessage.hasMsgType())
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("不可以修改消息类型。");
			return;
		}

		if (ssmessage.hasShareType())
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("不可以修改消息分享方式。");
			return;
		}

		if (ssmessage.hasTimestamp())
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("不可以修改消息的发送时间。");
			return;
		}

		dataService.UpdateMessage(request.getMsgid(), ssmessage);
		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	public void setMessageDao(MessageDao messageDao)
	{
		this.messageDao = messageDao;
	}

	@Override
	public void setProfile(NettyRpcController controller,
			SetProfileRequest request, SetProfileResponse.Builder response)
			throws DALException
	{
		if (StringUtils.isEmpty(request.getUserid()))
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("没有填写userid。");
			return;
		}

		SSProfile ssprofile = request.getProfile();

		Profile profile = new Profile();
		profile.setUserid(request.getUserid());
		if (ssprofile.hasNickname())
		{
			profile.setNickname(ssprofile.getNickname());
		}
		if (ssprofile.hasBirthday())
		{
			profile.setBirthday(ssprofile.getBirthday());
		}
		if (ssprofile.hasGender())
		{
			profile.setGender(ssprofile.getGender().getNumber());
		}
		if (ssprofile.hasIcon())
		{
			profile.setIcon(ssprofile.getIcon().toByteArray());
		}
		if (ssprofile.hasIconAddress())
		{
			profile.setIconAddress(ssprofile.getIconAddress());
		}
		if (ssprofile.hasIntroduction())
		{
			profile.setIntroduction(ssprofile.getIntroduction());
		}
		if (ssprofile.hasCountry())
		{
			profile.setCountry(ssprofile.getCountry());
		}
		if (ssprofile.hasProvince())
		{
			profile.setProvince(ssprofile.getProvince());
		}
		if (ssprofile.hasCity())
		{
			profile.setCity(ssprofile.getCity());
		}
		if (ssprofile.hasEducation())
		{
			profile.setEducation(ssprofile.getEducation());
		}
		if (ssprofile.hasWorkExperience())
		{
			profile.setWork_experience(ssprofile.getWorkExperience());
		}
		if (ssprofile.getFavTagsCount() > 0)
		{
			profile.setFavTags(Command.join(ssprofile.getFavTagsList()));
		}
		profileDao.insert(profile);

		// CreateCommand command =
		// new CreateCommand(cassandra)
		// .Object(Profile.CN_PROFILE)
		// .Where(request.getUserid());
		// if (ssprofile.hasNickname())
		// command.Insert(Profile.FN_NICKNAME, ssprofile.getNickname());
		// if (ssprofile.hasBirthday())
		// command.Insert(Profile.FN_BIRTHDAY, ssprofile.getBirthday());
		// if (ssprofile.hasGender())
		// command.Insert(Profile.FN_GENDER, ssprofile.getGender().getNumber());
		// if (ssprofile.hasIcon())
		// command.Insert(Profile.FN_ICON, ssprofile.getIcon().toByteArray());
		// if (ssprofile.hasIntroduction())
		// command.Insert(Profile.FN_INTRODUCTION, ssprofile.getIntroduction());
		// if (ssprofile.hasCountry())
		// command.Insert(Profile.FN_COUNTRY, ssprofile.getCountry());
		// if (ssprofile.hasProvince())
		// command.Insert(Profile.FN_PROVINCE, ssprofile.getProvince());
		// if (ssprofile.hasCity())
		// command.Insert(Profile.FN_CITY, ssprofile.getCity());
		// if (ssprofile.hasEducation())
		// command.Insert(Profile.FN_EDUCATION, ssprofile.getEducation());
		// if (ssprofile.hasWorkExperience())
		// command.Insert(Profile.FN_WORK_EXPERIENCE,
		// ssprofile.getWorkExperience());
		// if (ssprofile.getFavTagsCount()>0) {
		// command.Insert(Profile.FN_FAV_TAGS,
		// Command.join(ssprofile.getFavTagsList()));
		// }
		// command.execute();

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	public void setProfileDao(ProfileDao profileDao)
	{
		this.profileDao = profileDao;
	}

	public void setRepairService(ExecutorService repairService)
	{
		this.repairService = repairService;
	}

	@Override
	public void setTorrent(NettyRpcController controller,
			SetTorrentRequest request, SetTorrentResponse.Builder response)
			throws DALException
	{
		if (!request.hasValue())
		{
			controller.setCode(SSResultCode.RC_OK.getNumber());
			return;
		}

		CreateCommand command = new CreateCommand(cassandra,
				ConsistencyLevel.QUORUM).Object(Torrent.CN_TORRENT)
				.Where(request.getKey())
				.Insert(Torrent.FN_VALUE, request.getValue().toByteArray());
		command.execute();

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void unbindAccount(NettyRpcController controller,
			UnbindAccountRequest request, UnbindAccountResponse.Builder response)
			throws DALException
	{
		if (SSIdUtils.isEmpty(request.getOldId()))
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("未设置绑定帐号。");
			return;
		}

		Mutator<String> mutator = HFactory.createMutator(cassandra,
				StringSerializer.get());

		if (SSIdUtils.isEmailId(request.getOldId()))
		{
			// 删除旧的索引
			mutator.addDeletion(request.getOldId().getId(),
					EmailAccount.CN_EMAIL_ACCOUNT);
			// 删除帐号的邮件
			mutator.addDeletion(request.getUserid(), Account.CN_ACCOUNT,
					Account.FN_EMAIL, StringSerializer.get());
		}
		if (SSIdUtils.isPhoneId(request.getOldId()))
		{
			// 删除旧的索引
			mutator.addDeletion(request.getOldId().getId(),
					PhoneAccount.CN_PHONE_ACCOUNT);
			// 删除帐号的手机
			mutator.addDeletion(request.getUserid(), Account.CN_ACCOUNT,
					Account.FN_PHONE_NUMBER, StringSerializer.get());
		}
		try
		{
			mutator.execute();
		}
		catch (HectorException e)
		{
			throw new DALException(e);
		}
		controller.setCode(SSResultCode.RC_OK.getNumber());
	}

	@Override
	public void unfollow(NettyRpcController controller,
			UnfollowRequest request, UnfollowResponse.Builder response)
			throws DALException
	{

		if (!request.hasFollowerUserid())
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("follower_userid不能为空");
			return;
		}

		if (request.getFollowingUseridListCount() == 0)
		{
			controller.setCode(SSResultCode.RC_PARAMETER_INVALID.getNumber());
			controller.setFailed("following_userid_list不能为空");
			return;
		}

		final String followeruserid = request.getFollowerUserid();
		final List<String> followinguseridlist = new ArrayList<String>(
				request.getFollowingUseridListList());
		// ClusterMutex mutex = null;
		// try {
		// mutex = ClusterMutex.instance();
		// } catch (Exception e) {
		// mutex = null;
		// }
		try
		{

			// 剔除未关注的
			List<String> following_list = getIdFromFollowInfo(dataService
					.isFollowing(followeruserid, followinguseridlist));
			if (following_list != null)
			{
				followinguseridlist.retainAll(following_list);
			}

			if (followinguseridlist.size() > 0)
			{
				// 修改关注列表
				DeleteCommand command = new DeleteCommand(cassandra).Object(
						Following.CN_FOLLOWING).Where(followeruserid);
				command.DeleteColumn(followinguseridlist
						.toArray(new String[followinguseridlist.size()]));
				command.execute();

				// 修改关注者数量
				final UpdateCommand updateCommand = new UpdateCommand(cassandra)
						.Object(Profile.CN_PROFILE).Where(followeruserid);
				updateCommand.Update(Profile.FN_FOLLOWING_COUNT,
						new UpdateOperator()
						{
							@Override
							public ByteBuffer handle(String key,
									ByteBuffer supername, ByteBuffer name,
									ByteBuffer value)
							{
								Integer count = new Integer(0);
								if (value != null)
								{
									count = IntegerSerializer.get()
											.fromByteBuffer(value);
								}
								if (logger.isDebugEnabled())
								{
									logger.debug("original following count is "
											+ count);
								}
								count -= followinguseridlist.size();
								if (count < 0)
								{
									count = 0;
								}
								if (logger.isDebugEnabled())
								{
									logger.debug("updated following count is "
											+ count);
								}
								return IntegerSerializer.get().toByteBuffer(
										count);
							}
						});
				updateCommand.execute();
			}
		}
		finally
		{
		}

		repairService.submit(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{ // begin of back task

					for (String followinguserid : followinguseridlist)
					{
						// ClusterMutex mutex = null;
						// try {
						// mutex = ClusterMutex.instance();
						// } catch (Exception e) {
						// mutex = null;
						// }
						try
						{

							// 修改跟随者列表
							// DeleteCommand command =
							// new DeleteCommand(cassandra)
							// .Object(Follower.CN_FOLLOWER)
							// .Where(followinguserid)
							// .DeleteColumn(followeruserid);
							// command.execute();
							followerDao.delete(followinguserid, followeruserid);

							// 修改跟随者数量
							final UpdateCommand updateCommand = new UpdateCommand(
									cassandra).Object(Profile.CN_PROFILE)
									.Where(followinguserid);
							updateCommand.Update(Profile.FN_FOLLOWER_COUNT,
									new UpdateOperator()
									{
										@Override
										public ByteBuffer handle(String key,
												ByteBuffer supername,
												ByteBuffer name,
												ByteBuffer value)
										{
											if (value != null)
											{
												Integer count = IntegerSerializer
														.get().fromByteBuffer(
																value);
												if (count > 0)
												{
													count--;
												}
												return IntegerSerializer.get()
														.toByteBuffer(count);
											}
											else
											{
												return IntegerSerializer.get()
														.toByteBuffer(
																new Integer(0));
											}
										}
									});
							updateCommand.execute();
						}
						finally
						{
						}
					}

				}
				catch (Exception e)
				{
					logger.error("", e);
				}
			}
		}); // end of back task

		controller.setCode(SSResultCode.RC_OK.getNumber());
	}
}
