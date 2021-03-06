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

package org.napile.thermit.util.facade;

import org.napile.thermit.types.Commandline;

/**
 * Extension of Commandline.Argument with a new attribute that chooses
 * a specific implementation of the facade.
 *
 * @since Ant 1.5
 */
public class ImplementationSpecificArgument extends Commandline.Argument
{
	private String impl;

	/**
	 * Constructor for ImplementationSpecificArgument.
	 */
	public ImplementationSpecificArgument()
	{
		super();
	}

	/**
	 * Set the implementation this argument is for.
	 *
	 * @param impl the implementation this command line argument is for.
	 */
	public void setImplementation(String impl)
	{
		this.impl = impl;
	}

	/**
	 * Return the parts this Argument consists of, if the
	 * implementation matches the chosen implementation.
	 *
	 * @param chosenImpl the implementation to check against.
	 * @return the parts if the implementation matches or an zero length
	 *         array if not.
	 * @see org.napile.thermit.types.Commandline.Argument#getParts()
	 */
	public final String[] getParts(String chosenImpl)
	{
		if(impl == null || impl.equals(chosenImpl))
		{
			return super.getParts();
		}
		else
		{
			return new String[0];
		}
	}
}
