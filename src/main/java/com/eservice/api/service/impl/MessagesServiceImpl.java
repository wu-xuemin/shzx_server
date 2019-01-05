package com.eservice.api.service.impl;

import com.eservice.api.dao.MessagesMapper;
import com.eservice.api.model.messages.Messages;
import com.eservice.api.service.MessagesService;
import com.eservice.api.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
* Class Description: xxx
* @author Wilson Hu
* @date 2019/01/05.
*/
@Service
@Transactional
public class MessagesServiceImpl extends AbstractService<Messages> implements MessagesService {
    @Resource
    private MessagesMapper messagesMapper;

}
