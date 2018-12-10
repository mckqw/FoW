
import java.io.Serializable;

import com.fow.handlers.User;

public class requestUser implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1293546561716041248L;
	private int requestId;
	private User user;
	public requestUser(int rid, User u) {
		requestId = rid;
		user = u;
	}
	
	public int getRequestId() {
		return requestId;
	}	
	
	public User getUser() {
		return user;
	}
}
