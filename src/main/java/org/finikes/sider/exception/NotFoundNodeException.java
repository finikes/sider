package org.finikes.sider.exception;

/**
 * 
 * @author Finikes.Wu
 *
 */
public class NotFoundNodeException extends SiderException {

	private static final long serialVersionUID = -4720041526490991090L;

	public NotFoundNodeException(String cause) {
		super(cause);
	}

	public NotFoundNodeException(long slot) {
		super("annot operate on a node that does not exist. slot = " + slot);
	}

}
