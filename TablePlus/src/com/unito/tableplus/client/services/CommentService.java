package com.unito.tableplus.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.unito.tableplus.shared.model.Comment;

@RemoteServiceRelativePath("comment-service")
public interface CommentService extends RemoteService {

	Comment queryComment(String key);

}
