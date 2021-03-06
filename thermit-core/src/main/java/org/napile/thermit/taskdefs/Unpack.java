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


import java.io.File;

import org.napile.thermit.BuildException;
import org.napile.thermit.Task;
import org.napile.thermit.types.Resource;
import org.napile.thermit.types.ResourceCollection;
import org.napile.thermit.types.resources.FileProvider;
import org.napile.thermit.types.resources.FileResource;

/**
 * Abstract Base class for unpack tasks.
 *
 * @since Ant 1.5
 */

public abstract class Unpack extends Task
{
	// CheckStyle:VisibilityModifier OFF - bc
	protected File source;
	protected File dest;
	protected Resource srcResource;
	// CheckStyle:VisibilityModifier ON

	/**
	 * @param src a <code>String</code> value
	 * @ant.attribute ignore="true"
	 * @deprecated since 1.5.x.
	 *             setSrc(String) is deprecated and is replaced with
	 *             setSrc(File) to make Ant's Introspection
	 *             mechanism do the work and also to encapsulate operations on
	 *             the type in its own class.
	 */
	public void setSrc(String src)
	{
		log("DEPRECATED - The setSrc(String) method has been deprecated." + " Use setSrc(File) instead.");
		setSrc(getProject().resolveFile(src));
	}

	/**
	 * @param dest a <code>String</code> value
	 * @ant.attribute ignore="true"
	 * @deprecated since 1.5.x.
	 *             setDest(String) is deprecated and is replaced with
	 *             setDest(File) to make Ant's Introspection
	 *             mechanism do the work and also to encapsulate operations on
	 *             the type in its own class.
	 */
	public void setDest(String dest)
	{
		log("DEPRECATED - The setDest(String) method has been deprecated." + " Use setDest(File) instead.");
		setDest(getProject().resolveFile(dest));
	}

	/**
	 * The file to expand; required.
	 *
	 * @param src file to expand
	 */
	public void setSrc(File src)
	{
		setSrcResource(new FileResource(src));
	}

	/**
	 * The resource to expand; required.
	 *
	 * @param src resource to expand
	 */
	public void setSrcResource(Resource src)
	{
		if(!src.isExists())
		{
			throw new BuildException("the archive " + src.getName() + " doesn't exist");
		}
		if(src.isDirectory())
		{
			throw new BuildException("the archive " + src.getName() + " can't be a directory");
		}
		FileProvider fp = src.as(FileProvider.class);
		if(fp != null)
		{
			source = fp.getFile();
		}
		else if(!supportsNonFileResources())
		{
			throw new BuildException("The source " + src.getName() + " is not a FileSystem " + "Only FileSystem resources are" + " supported.");
		}
		srcResource = src;
	}

	/**
	 * Set the source Archive resource.
	 *
	 * @param a the archive as a single element Resource collection.
	 */
	public void addConfigured(ResourceCollection a)
	{
		if(a.size() != 1)
		{
			throw new BuildException("only single argument resource collections" + " are supported as archives");
		}
		setSrcResource(a.iterator().next());
	}

	/**
	 * The destination file or directory; optional.
	 *
	 * @param dest destination file or directory
	 */
	public void setDest(File dest)
	{
		this.dest = dest;
	}

	private void validate() throws BuildException
	{
		if(srcResource == null)
		{
			throw new BuildException("No Src specified", getLocation());
		}

		if(dest == null)
		{
			dest = new File(source.getParent());
		}

		if(dest.isDirectory())
		{
			String defaultExtension = getDefaultExtension();
			createDestFile(defaultExtension);
		}
	}

	private void createDestFile(String defaultExtension)
	{
		String sourceName = source.getName();
		int len = sourceName.length();
		if(defaultExtension != null && len > defaultExtension.length() && defaultExtension.equalsIgnoreCase(sourceName.substring(len - defaultExtension.length())))
		{
			dest = new File(dest, sourceName.substring(0, len - defaultExtension.length()));
		}
		else
		{
			dest = new File(dest, sourceName);
		}
	}

	/**
	 * Execute the task.
	 *
	 * @throws BuildException on error
	 */
	public void execute() throws BuildException
	{
		File savedDest = dest; // may be altered in validate
		try
		{
			validate();
			extract();
		}
		finally
		{
			dest = savedDest;
		}
	}

	/**
	 * Get the extension.
	 * This is to be overridden by subclasses.
	 *
	 * @return the default extension.
	 */
	protected abstract String getDefaultExtension();

	/**
	 * Do the uncompressing.
	 * This is to be overridden by subclasses.
	 */
	protected abstract void extract();

	/**
	 * Whether this task can deal with non-file resources.
	 * <p/>
	 * <p>This implementation returns false.</p>
	 *
	 * @return false for this task.
	 * @since Ant 1.7
	 */
	protected boolean supportsNonFileResources()
	{
		return false;
	}

}
