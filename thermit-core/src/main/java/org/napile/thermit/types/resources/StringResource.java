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
package org.napile.thermit.types.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FilterOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.napile.thermit.BuildException;
import org.napile.thermit.Project;
import org.napile.thermit.types.Resource;
import org.napile.thermit.types.Reference;

/**
 * Exposes a string as a Resource.
 *
 * @since Ant 1.7
 */
public class StringResource extends Resource
{

	/**
	 * Magic number
	 */
	private static final int STRING_MAGIC = Resource.getMagicNumber("StringResource".getBytes());

	private String encoding = null;

	/**
	 * Default constructor.
	 */
	public StringResource()
	{
	}

	/**
	 * Construct a StringResource with the supplied value.
	 *
	 * @param value the value of this StringResource.
	 */
	public StringResource(String value)
	{
		this(null, value);
	}

	/**
	 * Construct a StringResource with the supplied project and value,
	 * doing property replacement against the project if non-null.
	 *
	 * @param project the owning Project.
	 * @param value   the value of this StringResource.
	 */
	public StringResource(Project project, String value)
	{
		setProject(project);
		setValue(project == null ? value : project.replaceProperties(value));
	}

	/**
	 * Enforce String immutability.
	 *
	 * @param s the new name/value for this StringResource.
	 */
	public synchronized void setName(String s)
	{
		if(getName() != null)
		{
			throw new BuildException(new ImmutableResourceException());
		}
		super.setName(s);
	}

	/**
	 * The value attribute is a semantically superior alias for the name attribute.
	 *
	 * @param s the String's value.
	 */
	public synchronized void setValue(String s)
	{
		setName(s);
	}

	/**
	 * Synchronize access.
	 *
	 * @return the name/value of this StringResource.
	 */
	public synchronized String getName()
	{
		return super.getName();
	}

	/**
	 * Get the value of this StringResource, resolving to the root reference if needed.
	 *
	 * @return the represented String.
	 */
	public synchronized String getValue()
	{
		return getName();
	}

	/**
	 * The exists attribute tells whether a resource exists.
	 *
	 * @return true if this resource exists.
	 */
	public boolean isExists()
	{
		return getValue() != null;
	}

	/**
	 * Add nested text to this resource.
	 * Properties will be expanded during this process.
	 *
	 * @param text text to use as the string resource
	 * @since Ant 1.7.1
	 */
	public void addText(String text)
	{
		checkChildrenAllowed();
		setValue(getProject().replaceProperties(text));
	}

	/**
	 * Set the encoding to be used for this StringResource.
	 *
	 * @param s the encoding name.
	 */
	public synchronized void setEncoding(String s)
	{
		checkAttributesAllowed();
		encoding = s;
	}

	/**
	 * Get the encoding used by this StringResource.
	 *
	 * @return the encoding name.
	 */
	public synchronized String getEncoding()
	{
		return encoding;
	}

	/**
	 * Get the size of this Resource.
	 *
	 * @return the size, as a long, 0 if the Resource does not exist (for
	 *         compatibility with java.io.File), or UNKNOWN_SIZE if not known.
	 */
	public synchronized long getSize()
	{
		return isReference() ? ((Resource) getCheckedRef()).getSize() : getContent().length();
	}

	/**
	 * Get the hash code for this Resource.
	 *
	 * @return hash code as int.
	 */
	public synchronized int hashCode()
	{
		if(isReference())
		{
			return getCheckedRef().hashCode();
		}
		return super.hashCode() * STRING_MAGIC;
	}

	/**
	 * Get the string. See {@link #getContent()}
	 *
	 * @return the string contents of the resource.
	 * @since Ant 1.7
	 */
	public String toString()
	{
		return String.valueOf(getContent());
	}

	/**
	 * Get an InputStream for the Resource.
	 *
	 * @return an InputStream containing this Resource's content.
	 * @throws IOException                   if unable to provide the content of this
	 *                                       Resource as a stream.
	 * @throws UnsupportedOperationException if InputStreams are not
	 *                                       supported for this Resource type.
	 */
	public synchronized InputStream getInputStream() throws IOException
	{
		if(isReference())
		{
			return ((Resource) getCheckedRef()).getInputStream();
		}
		String content = getContent();
		if(content == null)
		{
			throw new IllegalStateException("unset string value");
		}
		return new ByteArrayInputStream(encoding == null ? content.getBytes() : content.getBytes(encoding));
	}

	/**
	 * Get an OutputStream for the Resource.
	 *
	 * @return an OutputStream to which content can be written.
	 * @throws IOException                   if unable to provide the content of this
	 *                                       Resource as a stream.
	 * @throws UnsupportedOperationException if OutputStreams are not
	 *                                       supported for this Resource type.
	 */
	public synchronized OutputStream getOutputStream() throws IOException
	{
		if(isReference())
		{
			return ((Resource) getCheckedRef()).getOutputStream();
		}
		if(getValue() != null)
		{
			throw new ImmutableResourceException();
		}
		return new StringResourceFilterOutputStream();
	}

	/**
	 * Overrides the super version.
	 *
	 * @param r the Reference to set.
	 */
	public void setRefid(Reference r)
	{
		if(encoding != null)
		{
			throw tooManyAttributes();
		}
		super.setRefid(r);
	}

	/**
	 * Get the content of this StringResource. See {@link #getValue()}
	 *
	 * @return a String or null if there is no value.
	 */
	protected synchronized String getContent()
	{
		return getValue();
	}

	/**
	 * This method is only for use by our private helper output stream.
	 * It contains specific logic for expanding properties.
	 *
	 * @param output the output
	 */
	private void setValueFromOutputStream(String output)
	{
		String value;
		if(getProject() != null)
		{
			value = getProject().replaceProperties(output);
		}
		else
		{
			value = output;
		}
		setValue(value);
	}

	private class StringResourceFilterOutputStream extends FilterOutputStream
	{
		private final ByteArrayOutputStream baos;

		public StringResourceFilterOutputStream()
		{
			super(new ByteArrayOutputStream());
			baos = (ByteArrayOutputStream) out;
		}

		public void close() throws IOException
		{
			super.close();
			String result = encoding == null ? baos.toString() : baos.toString(encoding);

			StringResource.this.setValueFromOutputStream(result);
		}
	}
}
