package com.zhuaiwa.dd.api;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhuaiwa.api.Common.SSAccount;
import com.zhuaiwa.api.Common.SSMessage;
import com.zhuaiwa.api.Common.SSProfile;
import com.zhuaiwa.api.Rpc.BindAccountRequest;
import com.zhuaiwa.api.Rpc.RemoveMessageRequest;
import com.zhuaiwa.api.Rpc.SendMessageRequest;
import com.zhuaiwa.api.Rpc.SetProfileRequest;
import com.zhuaiwa.api.Rpc.SetProfileResponse.Builder;
import com.zhuaiwa.api.Rpc.UnbindAccountRequest;
import com.zhuaiwa.api.SSDataDomain.SetMessageRequest;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.netty.server.JMXEnabledThreadPoolExecutor;
import com.zhuaiwa.dd.domain.Account;
import com.zhuaiwa.dd.domain.Message;
import com.zhuaiwa.dd.domain.Profile;
import com.zhuaiwa.dd.exception.DALException;
import com.zhuaiwa.dd.service.impl.DataServiceImpl;
import com.zhuaiwa.session.search.Indexer;

public class SSDataDomainSvcWithIndexer extends SSDataDomainSvcImpl {
    private static Logger logger = LoggerFactory.getLogger(SSDataDomainSvcWithIndexer.class);
    protected Indexer indexer = Indexer.getInstance();
    JMXEnabledThreadPoolExecutor executor = new JMXEnabledThreadPoolExecutor(
            "Indexer",
            1, Integer.MAX_VALUE,
            30, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(1024*64),
            new ThreadPoolExecutor.CallerRunsPolicy());
    
    private void updateUser(final String userid) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                Account account = accountDao.get(userid);
                SSAccount.Builder ssaccount = SSAccount.newBuilder();
                DataServiceImpl.build(ssaccount, account);
                
                Profile profile = profileDao.get(userid);
                SSProfile.Builder ssprofile = SSProfile.newBuilder();
                DataServiceImpl.build(ssprofile, profile);
                
                indexer.updateUser(ssaccount.build(), ssprofile.build());
                } catch (Exception e) {
                    logger.error("Error occurs on updating user " + userid, e);
                }
            }
        });
    }
    
    private void updateMessage(final String msgid) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                Message msg = messageDao.get(msgid);
                SSMessage.Builder ssmessage = SSMessage.newBuilder();
                DataServiceImpl.build(ssmessage, msg);
                
                indexer.updateMessage(ssmessage.build());
                } catch (Exception e) {
                    logger.error("Error occurs on updating message " + msgid, e);
                }
            }
        });
    }
    
    @Override
    public void setProfile(NettyRpcController controller, SetProfileRequest request, Builder response) throws DALException {
        super.setProfile(controller, request, response);
        updateUser(request.getUserid());
    }
    
    @Override
    public void bindAccount(NettyRpcController controller, BindAccountRequest request, com.zhuaiwa.api.Rpc.BindAccountResponse.Builder response)
            throws DALException {
        super.bindAccount(controller, request, response);
        updateUser(request.getUserid());
    }
    
    @Override
    public void unbindAccount(NettyRpcController controller, UnbindAccountRequest request, com.zhuaiwa.api.Rpc.UnbindAccountResponse.Builder response)
            throws DALException {
        super.unbindAccount(controller, request, response);
        updateUser(request.getUserid());
    }
    
    @Override
    public void sendMessage(NettyRpcController controller, SendMessageRequest request, com.zhuaiwa.api.Rpc.SendMessageResponse.Builder response)
            throws DALException {
        super.sendMessage(controller, request, response);
        if (!request.getMsg().hasMsgid()) {
            try {
            indexer.addMessage(response.getMsgid(), request.getMsg());
            } catch (Exception e) {
                logger.error("Error occurs on adding message " + response.getMsgid(), e);
            }
        } else {
            updateMessage(request.getMsg().getMsgid());
        }
    }
    
    @Override
    public void setMessage(NettyRpcController controller, SetMessageRequest request, com.zhuaiwa.api.SSDataDomain.SetMessageResponse.Builder response)
            throws DALException {
        super.setMessage(controller, request, response);
        updateMessage(request.getMsgid());
    }
    
    @Override
    public void removeMessage(NettyRpcController controller, RemoveMessageRequest request, com.zhuaiwa.api.Rpc.RemoveMessageResponse.Builder response)
            throws DALException {
        super.removeMessage(controller, request, response);
        for (final String msgid : request.getMsgidListList()) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                    indexer.removeMessage(msgid);
                    } catch (Exception e) {
                        logger.error("Error occurs on removing message " + msgid, e);
                    }
                }
            });
        }
    }
}
