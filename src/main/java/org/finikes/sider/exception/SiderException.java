package org.finikes.sider.exception;

/**
 * 
 * @author Finikes.Wu
 *
 */
public class SiderException extends RuntimeException {

	private static final long serialVersionUID = -3084162056550514261L;

	public SiderException(String cause) {
		super(cause);
	}

	public SiderException(String message, Throwable cause) {
		super("External events cause sider-anomalies: " + message, cause);
	}

	public SiderException(Throwable cause) {
		super(cause);
	}
}
