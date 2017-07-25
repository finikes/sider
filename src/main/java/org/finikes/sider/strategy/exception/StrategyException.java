package org.finikes.sider.strategy.exception;

/**
 * 
 * @author Finikes.Wu
 *
 */
public class StrategyException extends RuntimeException {

	private static final long serialVersionUID = -9070064597660809994L;

	public StrategyException(String cause) {
		super(cause);
	}

	public StrategyException(Throwable cause) {
		super(cause);
	}
}
