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
 * @author Greg Whalin <greg@meetup.com> 
 */
package com.meetup.memcached.test;

import com.meetup.memcached.*;
import java.util.*;

public class MemcachedTest {

	// store results from threads
	private static Hashtable<Integer,StringBuilder> threadInfo =
		new Hashtable<Integer,StringBuilder>();
    
	/**
	 * This runs through some simple tests of the MemcacheClient.
	 *
	 * Command line args:
	 * args[0] = number of threads to spawn
	 * args[1] = number of runs per thread
	 * args[2] = size of object to store 
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

		String[] serverlist = {				
				"192.168.17.73:11211",
				"192.168.17.172:11211",
				"192.168.17.155:11211", 
				"192.168.112.60:11211",/*
				"192.168.112.56:11214",	
				"192.168.112.56:11215",
				"192.168.112.56:11216",
				"192.168.112.56:11217",
				"192.168.112.56:11218",
				"192.168.112.56:11219",*/
 };
		Integer[] weights = {1,1,1,1,1,1,1,1,1,1};

		// initialize the pool for memcache servers
		SockIOPool pool = SockIOPool.getInstance();
		pool.setServers( serverlist );	
		pool.setWeights(weights);

		pool.setInitConn(5);
		pool.setMinConn(5);
		pool.setMaxConn(50);
		pool.setMaintSleep(30);

		pool.setNagle(false);
		pool.initialize();

		int threads = Integer.parseInt("10");
		int runs = Integer.parseInt("10000");
		int size = 1024 * Integer.parseInt("10");	// how many kilobytes

		// get object to store
		int[] obj = new int[size];
		for (int i = 0; i < size; i++) {
			obj[i] = i;
		}

		String[] keys = new String[size];
		for (int i = 0; i < size; i++) {
			keys[i] = "test_key" + i;
		}

		for (int i = 0; i < threads; i++) {
			bench b = new bench(runs, i, obj, keys);
			b.start();
		}

		int i = 0;
		while (i < threads) {
			if (threadInfo.containsKey(new Integer(i))) {
				System.out.println( threadInfo.get( new Integer( i ) ) );
				i++;
			}
			else {
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		pool.shutDown();
		System.exit(1);
	}

	/** 
	 * Test code per thread. 
	 */
	private static class bench extends Thread {
		private int runs;
		private int threadNum;
		private int[] object;
		private String[] keys;
		private int size;

		public bench(int runs, int threadNum, int[] object, String[] keys) {
			this.runs = runs*100;
			this.threadNum = threadNum;
			this.object = object;
			this.keys = keys;
			this.size = object.length;
		}

		public void run() {

			StringBuilder result = new StringBuilder();

			// get client instance
			MemcachedClient mc = new MemcachedClient();
			mc.setCompressEnable(false);
			mc.setCompressThreshold(0);

			// time deletes
			long start = System.currentTimeMillis();
			/*for (int i = 0; i < runs; i++) {
				mc.delete(keys[i]);
			}*/
			long elapse = System.currentTimeMillis() - start;
			float avg = (float) elapse / runs;
			result.append("\nthread " + threadNum + ": runs: " + runs + " deletes of obj " + (size/1024) + "KB -- avg time per req " + avg + " ms (total: " + elapse + " ms)");

			// time stores	
			Random r = new Random();
			
			start = System.currentTimeMillis();
			for (int i = 0; i < runs; i++) {
				object = new int[r.nextInt(1000)+1];
				int index=i%100;//r.nextInt((100));				
				
				mc.set(keys[index], object);
				//System.out.printf("*****set****%5d,%5d,%5d*********\r\n",i,index,object.length);
				object = null;
				//System.out.printf("set deys[%d]:%s\r\n",i,keys[i]);

				index=r.nextInt((100));
				try {
					object = (int [])mc.get(keys[index]);
					int isEmpty = 1;
					if (object==null){
						//System.out.printf("*****get****%5d,%5d,%15d*********\r\n",i,index,0);
					}else {
						//System.out.printf("*****get****%5d,%5d,%15d*********\r\n",i,index,object.length);
					}
				}
				catch(Exception e)
				{
					System.out.println(e);
					System.out.printf("****exception*****%X,%x,%x*********\r\n",index,i,keys.length);					
				}
				//System.out.printf("get deys[%d]:%s\r\n",(i*33*57*109*401)%(keys.length-1)/2,keys[(i*33*57*109*401)%(keys.length-1)/2]);
			}
			elapse = System.currentTimeMillis() - start;
			avg = (float) elapse / runs;
			result.append("\nthread " + threadNum + ": runs: " + runs + " stores of obj " + (size/1024) + "KB -- avg time per req " + avg + " ms (total: " + elapse + " ms)");

			start = System.currentTimeMillis();
			/*for (int i = 0; i < runs; i++) {
				mc.get(keys[i]);
			}*/
			elapse = System.currentTimeMillis() - start;
			avg = (float) elapse / runs;
			result.append("\nthread " + threadNum + ": runs: " + runs + " gets of obj " + (size/1024) + "KB -- avg time per req " + avg + " ms (total: " + elapse + " ms)");

			threadInfo.put(new Integer(threadNum), result);
		}
	}
}
