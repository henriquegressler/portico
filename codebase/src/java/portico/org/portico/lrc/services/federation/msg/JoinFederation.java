/*
 *   Copyright 2008 The Portico Project
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
package org.portico.lrc.services.federation.msg;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.portico.lrc.model.ObjectModel;
import org.portico.lrc.model.RTIPolicy;
import org.portico.utils.messaging.PorticoMessage;

public class JoinFederation extends PorticoMessage
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final long serialVersionUID = 98121116105109L;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String federateName;
	private String federationName;
	private List<ObjectModel> joinModules; // parsed version of object FOM modules below

	private transient List<URL> fomModules;
	private transient ObjectModel fom;
	private transient RTIPolicy rtiPolicy;
	protected Logger logger;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	public JoinFederation()
	{
		super();
		this.setImmediateProcessingFlag( true );
		this.joinModules = new ArrayList<ObjectModel>();
		this.fomModules = new ArrayList<URL>();
		this.rtiPolicy = null;
		this.logger = Logger.getLogger( "portico.lrc.services.federation.msg.JoinFederation" );
	}

	public JoinFederation( String federationName, String federateName )
	{
		this();
		this.federateName = federateName;
		this.federationName = federationName;
	}

	public JoinFederation( String federationName, String federateName, URL[] fomModules )
	{
		this( federationName, federateName );
		if( fomModules != null )
		{
			for( URL module : fomModules )
			{
				this.fomModules.add( module );
				System.out.println( "syso >>>>> Adding FOM Module: " + module );
				this.logger.info( "Adding FOM Module: " + module );
			}

			try
			{
				File policyFile = new File( "RTIpolicy.xml" );
				if( policyFile.exists() )
				{
					this.rtiPolicy = RTIPolicy.getInstance( policyFile.getPath() );
				}
				else
				{
					this.logger.warn( "RTIPolicy.xml not found" );
				}
			}
			catch( Exception e )
			{
				this.logger.error( "Problem loading RTIPolicy.xml", e );
			}
		}
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public String getFederateName()
	{
		return federateName;
	}

	public void setFederateName( String federateName )
	{
		this.federateName = federateName;
	}

	public String getFederationName()
	{
		return federationName;
	}

	public void setFederationName( String federationName )
	{
		this.federationName = federationName;
	}

	@Override
	public boolean isImmediateProcessingRequired()
	{
		return true;
	}

	/**
	 * Returns a list of all the FOM modules that this federate is trying to join with.
	 */
	public List<ObjectModel> getJoinModules()
	{
		return this.joinModules;
	}

	public void addJoinModule( ObjectModel module )
	{
		if( module != null )
			this.joinModules.add( module );
	}

	//////////////////////////////////////////////////
	/// Transient Properties /////////////////////////
	//////////////////////////////////////////////////	
	public ObjectModel getFOM()
	{
		return this.fom;
	}

	public void setFOM( ObjectModel fom )
	{
		this.fom = fom;
	}

	public List<URL> getFomModules()
	{
		return this.fomModules;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
