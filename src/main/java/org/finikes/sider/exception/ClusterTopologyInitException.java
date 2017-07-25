package org.finikes.sider.exception;

/**
 * 
 * @author Finikes.Wu
 *
 */
public class ClusterTopologyInitException extends SiderException {

	private static final long serialVersionUID = 4882107092985954457L;

	public ClusterTopologyInitException(Exception cause) {
		super(cause.getMessage(), cause);
	}

	public ClusterTopologyInitException(String message, Throwable cause) {
		super(message, cause);
	}

}
