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

package org.napile.thermit.taskdefs.condition;

import org.napile.thermit.BuildException;
import org.napile.thermit.ProjectComponent;
import org.napile.thermit.types.Reference;

/**
 * Condition that tests whether a given reference has been defined.
 * <p/>
 * <p>Optionally tests whether it is of a given type/class.</p>
 *
 * @since Ant 1.6
 */
public class IsReference extends ProjectComponent implements Condition
{
	private Reference ref;
	private String type;

	/**
	 * Set the refid attribute.
	 *
	 * @param r a Reference value
	 */
	public void setRefid(Reference r)
	{
		ref = r;
	}

	/**
	 * Set the type attribute. This is optional attribute.
	 *
	 * @param type an thermit component type name
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * @return true if the reference exists and if type is set, if
	 *         the reference is the same type
	 * @throws BuildException if an error occurs
	 */
	public boolean eval() throws BuildException
	{
		if(ref == null)
		{
			throw new BuildException("No reference specified for isreference " + "condition");
		}

		String key = ref.getRefId();
		if(!getProject().hasReference(key))
		{
			return false;
		}
		else if(type == null)
		{
			return true;
		}
		else
		{
			Object o = getProject().getReference(key);
			Class typeClass = (Class) getProject().getDataTypeDefinitions().get(type);

			if(typeClass == null)
			{
				typeClass = (Class) getProject().getTaskDefinitions().get(type);
			}

			if(typeClass == null)
			{
				// don't know the type, should throw exception instead?
				return false;
			}

			return typeClass.isAssignableFrom(o.getClass());
		}
	}

}
