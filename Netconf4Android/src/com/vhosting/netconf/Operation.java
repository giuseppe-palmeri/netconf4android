package com.vhosting.netconf;

import java.io.IOException;

import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.frame.RpcHandler;
import com.vhosting.netconf.frame.RpcReply;
import com.vhosting.netconf.frame.RpcReplyListener;
import com.vhosting.netconf.frame.RpcReplySpecificListener;
import com.vhosting.netconf.transport.Session;

/**
 * This class provides the basis for the construction of a specific RPC
 * operation,
 * with its own features, whose operation is conditioned by the capabilities
 * made available to the server.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public abstract class Operation
{

	/**
	 * The RPC operation reference.
	 * <b>The reference of the operation need to be enhanced in
	 * the constructor of the subclass.</b>
	 * 
	 * <pre>
	 * Example:
	 * 
	 * public class Get extends Command {
	 * 
	 * public Get(Session session) {
	 *    super(session);
	 *    
	 *    // Assignmet of operation.
	 *   operation = new Rpc(Session.BASE_1_0, "get");
	 * }
	 * 
	 * ...
	 * </pre>
	 */
	protected Rpc operation;

	/**
	 * The active session reference.
	 */
	protected Session session;

	/**
	 * Constructs an instance of the operation assigning
	 * it to an active session.
	 * A subclass must necessarily use this constructor:
	 * 
	 * <pre>
	 * super(session);
	 * </pre>
	 * 
	 * In each manufacturer as its first statement.
	 * 
	 * @param session
	 *            The active session.
	 */
	public Operation(Session session)
	{
		if (!session.isActive())
			throw new RuntimeException("The server connection is not present.");
		this.session = session;
	}


	/**
	 * Allows you to execute the operation, through the use of a RpcHandler.
	 * The execution takes place asynchronously.
	 * 
	 * The RPC reply can be intercepted using the method:
	 * 
	 * RpcHandler.setRpcReplyListener(RpcReplyListener listener);
	 * 
	 * @param handler
	 *            The RPC handler.
	 * @throws IOException
	 *             Throws this exception when the connection is no
	 *             longer active before and during the exchange of messages.
	 * @return The identifier of the message sent; useful to intercept the
	 *         associated RPC reply.
	 * @see RpcHandler#setRpcReplyListener(RpcReplyListener listener)
	 */
	public final int execute(RpcHandler handler) throws IOException {

		return handler.sendRpc(operation);
	}

	/**
	 * Allows you to execute the operation, through the use of a RpcHandler
	 * specifying a listener that will intercept the RPC reply.
	 * The execution takes place asynchronously.
	 * 
	 * @param handler
	 *            The RPC handler.
	 * @param listener
	 *            The listener that will intercept the RPC reply.
	 * @throws IOException
	 *             Throws this exception when the connection is no
	 *             longer active before and during the exchange of messages.
	 */
	public final void execute(RpcHandler handler,
			RpcReplySpecificListener listener) throws IOException {
		handler.sendRpc(operation, listener);
	}

	/**
	 * Allows you to execute the operation, through the use of a RpcHandler
	 * and waits until you get a reply.
	 * 
	 * @param handler
	 *            The RPC handler.
	 * @return The RPC reply.
	 * @throws IOException
	 *             Throws this exception when the connection is no
	 *             longer active before and during the exchange of messages.
	 */
	public final RpcReply executeSync(RpcHandler handler) throws IOException {
		return handler.sendSyncRpc(operation);

	}



	/**
	 * Represents the specific reply for the operation.
	 * @author Giuseppe Palmeri
	 */
	public abstract class Reply
	{
		/**
		 * Create a specific reply to the operation.
		 * @param reply The RPC reply.
		 */
		public Reply(RpcReply reply)
		{
			reply.load(operation);
		}
	}

}
