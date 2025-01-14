/*
 *   Copyright 2009 The Portico Project
 *
 *   This file is part of portico.
 *
 *   portico is free software; you can redistribute it and/or modify
 *   it under the terms of the Common Developer and Distribution License (CDDL) 
 *   as published by Sun Microsystems. For more information see the LICENSE file.
 *   
 *   Use of this software is strictly AT YOUR OWN RISK!!!
 *   If something bad happens you do not have permission to come crying to me.
 *   (that goes for your lawyer as well)
 *
 */
package org.portico.lrc.services.saverestore.msg;

import org.portico.utils.messaging.PorticoMessage;
import org.portico2.common.messaging.MessageType;

public class RestoreFederation extends PorticoMessage
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final long serialVersionUID = 98121116105109L;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String label;
	private String reason;
	private boolean success;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	/**
	 * Creates a new restore request with it's status set to <code>true</code>.
	 */
	public RestoreFederation( String label )
	{
		this.label = label;
		this.success = true;
	}
	
	public RestoreFederation( String label, boolean successful, String failureReason )
	{
		this.label = label;
		this.success = successful;
		this.reason = failureReason;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public MessageType getType()
	{
		return MessageType.RestoreComplete;
	}

	public String getLabel()
	{
		return this.label;
	}
	
	public void setLabel( String label )
	{
		this.label = label;
	}
	
	public String getReason()
	{
		return this.reason;
	}
	
	public void setFailureReason( String reason )
	{
		this.reason = reason;
	}
	
	public String getFailureReason()
	{
		return this.reason;
	}
	
	public void setSuccessStauts( boolean status )
	{
		this.success = status;
	}
	
	public boolean getSuccessStatus()
	{
		return this.success;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
