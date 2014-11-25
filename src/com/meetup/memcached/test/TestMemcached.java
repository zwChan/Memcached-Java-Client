/**
 * Copyright (c) 2008 Greg Whalin
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the BSD license
 *
 * This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 *
 * You should have received a copy of the BSD License along with this
 * library.
 *
 * @author greg whalin <greg@meetup.com> 
 */
package com.meetup.memcached.test;

import com.meetup.memcached.*;
import org.apache.log4j.*;

public class TestMemcached  {  
	public static void main(String[] args) {
		      // memcached should be running on port 11211 but NOT on 11212

		BasicConfigurator.configure();
		String[] servers = { 
				"127.0.0.1:11211",
		/*		"192.168.112.56:11211",
				"192.168.112.56:11212",
				"192.168.112.56:11213",
				"192.168.112.56:11214",
				"192.168.112.56:11215",
				"192.168.112.56:11216",
				"192.168.112.56:11217",
				"192.168.112.56:11218",
				"192.168.112.56:11219",
		*/
				};
		SockIOPool pool = SockIOPool.getInstance();
		pool.setServers( servers );
		pool.setFailover( true );
		pool.setInitConn( 1 ); 
		pool.setMinConn( 1 );
		pool.setMaxConn( 2148 );
		pool.setMaintSleep( 30 );
		pool.setNagle( false );
		pool.setSocketTO( 3000 );
		pool.setAliveCheck( false );
		pool.initialize();

		MemcachedClient mcc = new MemcachedClient();

		// turn off most memcached client logging:
		com.meetup.memcached.Logger.getLogger( MemcachedClient.class.getName() ).setLevel(4);;;
/*
		for ( int i = 0; i < 10000; i++ ) {
			boolean success = mcc.set( "" + i, "Hello!" );
			String result = (String)mcc.get( "" + i );
			System.out.println( String.format( "set( %d ): %s", i, success ) );
			System.out.println( String.format( "get( %d ): %s", i, result ) );
		}
*/
		System.out.println( "\n\t -- sleeping --\n" );
		
		
/*		
		try { Thread.sleep( 10000 ); } catch ( Exception ex ) { }

		for ( int i = 0; i < 100000; i++ ) {
			boolean success = mcc.set( "" + i, "Hello!" );
			String result = (String)mcc.get( "" + i );
			System.out.println( String.format( "set( %d ): %s", i, success ) );
			System.out.println( String.format( "get( %d ): %s", i, result ) );
			try { Thread.sleep( 10000 ); } catch ( Exception ex ) { }
		}
*/
		String val= "123 567";
		boolean result = mcc.set( "aaa",val);
		System.out.println( String.format( "set( %d ): %s", 1, result ) );

		
	}
}
