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

package org.napile.thermit.loader;

import java.util.Enumeration;
import java.io.Closeable;
import java.io.IOException;
import java.net.URL;

import org.napile.thermit.AntClassLoader;
import org.napile.thermit.Project;
import org.napile.thermit.types.Path;

/**
 * Overrides getResources which became non-final in Java5 and
 * implements Closeable
 */
public class AntClassLoader5 extends AntClassLoader implements Closeable
{
	/**
	 * Creates a classloader for the given project using the classpath given.
	 *
	 * @param parent      The parent classloader to which unsatisfied loading
	 *                    attempts are delegated. May be <code>null</code>,
	 *                    in which case the classloader which loaded this
	 *                    class is used as the parent.
	 * @param project     The project to which this classloader is to belong.
	 *                    Must not be <code>null</code>.
	 * @param classpath   the classpath to use to load the classes.
	 *                    May be <code>null</code>, in which case no path
	 *                    elements are set up to start with.
	 * @param parentFirst If <code>true</code>, indicates that the parent
	 *                    classloader should be consulted  before trying to
	 *                    load the a class through this loader.
	 */
	public AntClassLoader5(ClassLoader parent, Project project, Path classpath, boolean parentFirst)
	{
		super(parent, project, classpath, parentFirst);
	}

	/**
	 * {@inheritDoc}
	 */
	public Enumeration<URL> getResources(String name) throws IOException
	{
		return getNamedResources(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public void close()
	{
		cleanup();
	}
}
