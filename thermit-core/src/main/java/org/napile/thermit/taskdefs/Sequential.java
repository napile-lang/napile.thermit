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
package org.napile.thermit.taskdefs;

import java.util.Iterator;
import java.util.Vector;

import org.napile.thermit.BuildException;
import org.napile.thermit.Task;
import org.napile.thermit.TaskContainer;

import org.napile.thermit.property.LocalProperties;

/**
 * Sequential is a container task - it can contain other Ant tasks. The nested
 * tasks are simply executed in sequence. Sequential's primary use is to support
 * the sequential execution of a subset of tasks within the {@link Parallel Parallel Task}
 * <p/>
 * <p>
 * The sequential task has no attributes and does not support any nested
 * elements apart from Ant tasks. Any valid Ant task may be embedded within the
 * sequential task.</p>
 *
 * @ant.task category="control"
 * @since Ant 1.4
 */
public class Sequential extends Task implements TaskContainer
{

	/**
	 * Optional Vector holding the nested tasks
	 */
	private Vector nestedTasks = new Vector();

	/**
	 * Add a nested task to Sequential.
	 * <p/>
	 *
	 * @param nestedTask Nested task to execute Sequential
	 *                   <p/>
	 */
	public void addTask(Task nestedTask)
	{
		nestedTasks.addElement(nestedTask);
	}

	/**
	 * Execute all nestedTasks.
	 *
	 * @throws BuildException if one of the nested tasks fails.
	 */
	public void execute() throws BuildException
	{
		LocalProperties localProperties = LocalProperties.get(getProject());
		localProperties.enterScope();
		try
		{
			for(Iterator i = nestedTasks.iterator(); i.hasNext(); )
			{
				Task nestedTask = (Task) i.next();
				nestedTask.perform();
			}
		}
		finally
		{
			localProperties.exitScope();
		}
	}
}
