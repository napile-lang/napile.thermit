/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.napile.thermit.taskdefs.rmic;

import org.napile.thermit.types.Commandline;

/**
 * Run rmic in a new process with -Xnew set.
 * This switches rmic to use a new compiler, one that doesnt work in-process
 * on thermit on java1.6.
 * see: <a href="http://issues.napile.org/bugzilla/show_bug.cgi?id=38732">
 * http://issues.napile.org/bugzilla/show_bug.cgi?id=38732</a>
 */
public class XNewRmic extends ForkingSunRmic
{

	/**
	 * the name of this adapter for users to select
	 */
	public static final String COMPILER_NAME = "xnew";

	/**
	 * No-arg constructor.
	 */
	public XNewRmic()
	{
	}

	/**
	 * Create a normal command line, then with -Xnew at the front
	 *
	 * @return a command line that hands off to thw
	 */
	protected Commandline setupRmicCommand()
	{
		String[] options = new String[]{
				"-Xnew"
		};
		Commandline commandline = super.setupRmicCommand(options);
		return commandline;
	}

}
