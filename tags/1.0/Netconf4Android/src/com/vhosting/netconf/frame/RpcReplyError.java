package com.vhosting.netconf.frame;


/**
 * This interface defines an RPC error as a possible reply to an RPC operation.
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public interface RpcReplyError {

	/**
	 * Get the RPC error type.
	 * @return The RPC error type elements.
	 */
	public ErrorType getErrorType();
	
	/**
	 * This listing allows you to catalog all RPC error 
	 * that can be obtained in response to an RPC operation.
	 * @author Giuseppe Palmeri
	 * 
	 */
	public enum ErrorType
	{
		/**
		 * Transport Error.
		 */
		transport {
		    public String toString() {
		        return "transport";
		    }
		},
		
		/**
		 * RPC error.
		 */
		rpc {
		    public String toString() {
		        return "rpc";
		    }
		},
		
		/**
		 * Protocol error.
		*/
		protocol {
		    public String toString() {
		        return "protocol";
		    }
		},
		
		/**
		 * Application error.
		*/
		application {
		    public String toString() {
		        return "application";
		    }
		}
    
	}
	
	/**
	 * Contains a string identifying the error condition.
	 * @return The error condition.
	 */
	public ErrorTag getErrorTag();
	
	/**
	 * This enum represents the cataloging of all possible 
	 * errors that can be found in an RPC response.
	 * @author Giuseppe Palmeri
	 *
	 */
	public enum ErrorTag
	{
		/**
		 * The request requires a resource that already in use.
		 */
		in_use {
		    public String toString() {
		        return "in-use";
		    }
		},
		
		/**
		 * The request specifies an unacceptable value for one
         * or more parameters.
		 */
		invalid_value {
		    public String toString() {
		        return "invalid-value";
		    }
		},
		
		/**
		 * The request or response (that would be generated) is too
         * large for the implementation to handle.
		 */
		too_big {
		    public String toString() {
		        return "too-big";
		    }
		},
		
		/**
		 * An attribute value is not correct; e.g., wrong type,
         * out of range, pattern mismatch.
		 */
		bad_attribute {
		    public String toString() {
		        return "bad-attribute";
		    }
		},
		
		
		/**
		 * An unexpected attribute is present.
		 */
		unknown_attribute {
		    public String toString() {
		        return "unknown-attribute";
		    }
		},
		
		/**
		 * An expected attribute is missing.
		 */
		missing_attribute {
		    public String toString() {
		        return "missing-attribute";
		    }
		},
		
		/**
		 * An expected element is missing.
		 */
		missing_element {
		    public String toString() {
		        return "missing-element";
		    }
		},
		
		/**
		 * An element value is not correct; e.g., wrong type,
         * out of range, pattern mismatch.
		 */
		bad_element {
		    public String toString() {
		        return "bad-element";
		    }
		},
		
		
		/**
		 * An unexpected element is present.
		 */
		unknown_element {
		    public String toString() {
		        return "unknown-element";
		    }
		},
		
		/**
		 * An unexpected namespace is present.
		 */
		unknown_namespace {
		    public String toString() {
		        return "unknown-namespace";
		    }
		},
		
		/**
		 * Access to the requested RPC, protocol operation,
         * or data model is denied because authorization failed.
		 */
		access_denied {
		    public String toString() {
		        return "access-denied";
		    }
		},
		
		/**
		 * Access to the requested lock is denied because the
         * lock is currently held by another entity.
		 */
		lock_denied {
		    public String toString() {
		        return "lock-denied";
		    }
		},
		
		/**
		 * Request could not be completed because of insufficient
         * resources.
		 */
		resource_denied {
		    public String toString() {
		        return "resource-denied";
		    }
		},
		
		
		/**
		 * Request to rollback some configuration change (via
         * rollback-on-error or discard-changes operations) was
         * not completed for some reason.
		 */
		rollback_failed {
		    public String toString() {
		        return "rollback-failed";
		    }
		},
		
		/**
		 * Request could not be completed because the relevant
         * data model content already exists. For example,
         * a 'create' operation was attempted on data that
         * already exists.
		 */
		data_exists {
		    public String toString() {
		        return "data-exists";
		    }
		},
		
		/**
		 * Request could not be completed because the relevant
         * data model content does not exist.  For example,
         * a 'replace' or 'delete' operation was attempted on
         * data that does not exist.
		 */
		data_missing {
		    public String toString() {
		        return "data-missing";
		    }
		},
		
		/**
		 * Request could not be completed because the requested
         * operation is not supported by this implementation.
		 */
		operation_not_supported {
		    public String toString() {
		        return "operation-not-supported";
		    }
		},
		
		/**
		 * Request could not be completed because the requested
         * operation failed for some reason not covered by
         * any other error condition.
		 */
		operation_failed {
		    public String toString() {
		        return "operation-failed";
		    }
		},
		
		/**
		 * Some part of the requested operation failed or was
         * not attempted for some reason.  Full cleanup has
         * not been performed (e.g., rollback not supported)
         * by the server.  The error-info container is used
         * to identify which portions of the application
         * data model content for which the requested operation
         * has succeeded (<ok-element>), failed (<bad-element>),
         * or not been attempted (<noop-element>).
         * 
         * 
		 */
		//@Deprecated - Può essere DEPRECATO nella versione :base:1.1
		partial_operation {
		    public String toString() {
		        return "partial-operation";
		    }
		}
	}
	
	
	/**
	 * Get the severity of the error.
	 * @return The error severity.
	 */
	public ErrorSeverity getErrorSeverity();

	/**
	 * This enum lists the types of severity of a RPC error.
	 * @author Giuseppe Palmeri
	 *
	 */
	public enum ErrorSeverity
	{
		/**
		 * The RPC error is really a mistake.
		 */
		error {
		    public String toString() {
		        return "error";
		    }
		},
		
		/**
		 * The RPC error is simply a warning.
		 */
		warning {
		    public String toString() {
		        return "warning";
		    }
		}
	}

	
	
	/**
	 * Get the custom application error tag if exists.
	 * @return The custom application error tag; null if not exists.
	 */
	public String getErrorAppTag();
	
	/**
	 * Contains the absolute XPath expression identifying
      the element path to the node that is associated with the error
      being reported in a RPC error.
	 * @return The XPath expression; null if not exists.
	 */
	public String getErrorPath();
	
	/**
	 * Get human readable information about the problem.
	 * @return The human readable information about the problem.
	 */
	public RpcReplyErrorMessage getErrorMessage();
	
	/**
	 * Get additional information about the error, if the error type requires it.
	 * @return The additional error informations; null if the error type not requires it.
	 */
	public RpcReplyErrorInfo getErrorInfo();
}
