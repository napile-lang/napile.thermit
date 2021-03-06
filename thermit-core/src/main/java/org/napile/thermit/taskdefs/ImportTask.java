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

import org.napile.thermit.BuildException;
import org.napile.thermit.ProjectHelper;
import org.napile.thermit.Project;
import org.napile.thermit.ProjectHelperRepository;
import org.napile.thermit.Task;
import org.napile.thermit.types.Resource;
import org.napile.thermit.types.ResourceCollection;
import org.napile.thermit.types.resources.FileProvider;
import org.napile.thermit.types.resources.FileResource;
import org.napile.thermit.types.resources.URLResource;
import org.napile.thermit.types.resources.Union;
import org.napile.thermit.util.FileUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

/**
 * Task to import another build file into the current project.
 * <p>
 * It must be 'top level'. On execution it will read another Ant file
 * into the same Project.
 * </p>
 * <p>
 * <b>Important</b>: Trying to understand how relative file references
 * resolved in deep/complex build hierarchies - such as what happens
 * when an imported file imports another file can be difficult. Use absolute references for
 * enhanced build file stability, especially in the imported files.
 * </p>
 * <p>Examples:</p>
 * <pre>
 * &lt;import file="../common-targets.xml"/&gt;
 * </pre>
 * <p>Import targets from a file in a parent directory.</p>
 * <pre>
 * &lt;import file="${deploy-platform}.xml"/&gt;
 * </pre>
 * <p>Import the project defined by the property <code>deploy-platform</code>.</p>
 *
 * @ant.task category="control"
 * @since Ant1.6
 */
public class ImportTask extends Task
{
	private String file;
	private boolean optional;
	private String targetPrefix;
	private String prefixSeparator = ".";
	private final Union resources = new Union();
	private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

	public ImportTask()
	{
		resources.setCache(true);
	}

	/**
	 * sets the optional attribute
	 *
	 * @param optional if true ignore files that are not present,
	 *                 default is false
	 */
	public void setOptional(boolean optional)
	{
		this.optional = optional;
	}

	/**
	 * the name of the file to import. How relative paths are resolved is still
	 * in flux: use absolute paths for safety.
	 *
	 * @param file the name of the file
	 */
	public void setFile(String file)
	{
		// I don't think we can use File - different rules
		// for relative paths.
		this.file = file;
	}

	/**
	 * The prefix to use when prefixing the imported target names.
	 *
	 * @since Ant 1.8.0
	 */
	public void setAs(String prefix)
	{
		targetPrefix = prefix;
	}

	/**
	 * The separator to use between prefix and target name, default is
	 * ".".
	 *
	 * @since Ant 1.8.0
	 */
	public void setPrefixSeparator(String s)
	{
		prefixSeparator = s;
	}

	/**
	 * The resource to import.
	 *
	 * @since Ant 1.8.0
	 */
	public void add(ResourceCollection r)
	{
		resources.add(r);
	}

	public void execute()
	{
		if(file == null && resources.size() == 0)
		{
			throw new BuildException("import requires file attribute or" + " at least one nested resource");
		}
		if(getOwningTarget() == null || !"".equals(getOwningTarget().getName()))
		{
			throw new BuildException("import only allowed as a top-level task");
		}

		ProjectHelper helper = (ProjectHelper) getProject().
				getReference(ProjectHelper.PROJECTHELPER_REFERENCE);

		if(helper == null)
		{
			// this happens if the projecthelper was not registered with the project.
			throw new BuildException("import requires support in ProjectHelper");
		}

		Vector<Object> importStack = helper.getImportStack();

		if(importStack.size() == 0)
		{
			// this happens if thermit is used with a project
			// helper that doesn't set the import.
			throw new BuildException("import requires support in ProjectHelper");
		}

		if(getLocation() == null || getLocation().getFileName() == null)
		{
			throw new BuildException("Unable to get location of import task");
		}

		Union resourcesToImport = new Union(getProject(), resources);
		Resource fromFileAttribute = getFileAttributeResource();
		if(fromFileAttribute != null)
		{
			resources.add(fromFileAttribute);
		}
		for(Resource r : resourcesToImport)
		{
			importResource(helper, r);
		}
	}

	private void importResource(ProjectHelper helper, Resource importedResource)
	{
		Vector<Object> importStack = helper.getImportStack();

		getProject().log("Importing file " + importedResource + " from " + getLocation().getFileName(), Project.MSG_VERBOSE);

		if(!importedResource.isExists())
		{
			String message = "Cannot find " + importedResource + " imported from " + getLocation().getFileName();
			if(optional)
			{
				getProject().log(message, Project.MSG_VERBOSE);
				return;
			}
			else
			{
				throw new BuildException(message);
			}
		}

		File importedFile = null;
		FileProvider fp = importedResource.as(FileProvider.class);
		if(fp != null)
		{
			importedFile = fp.getFile();
		}

		if(!isInIncludeMode() && (importStack.contains(importedResource) || (importedFile != null && importStack.contains(importedFile))))
		{
			getProject().log("Skipped already imported file:\n   " + importedResource + "\n", Project.MSG_VERBOSE);
			return;
		}

		// nested invokations are possible like an imported file
		// importing another one
		String oldPrefix = ProjectHelper.getCurrentTargetPrefix();
		boolean oldIncludeMode = ProjectHelper.isInIncludeMode();
		String oldSep = ProjectHelper.getCurrentPrefixSeparator();
		try
		{
			String prefix;
			if(isInIncludeMode() && oldPrefix != null && targetPrefix != null)
			{
				prefix = oldPrefix + oldSep + targetPrefix;
			}
			else if(targetPrefix != null)
			{
				prefix = targetPrefix;
			}
			else
			{
				prefix = oldPrefix;
			}
			setProjectHelperProps(prefix, prefixSeparator, isInIncludeMode());

			ProjectHelper subHelper = ProjectHelperRepository.getInstance().getProjectHelperForBuildFile(importedResource);

			// push current stacks into the sub helper
			subHelper.getImportStack().addAll(helper.getImportStack());
			subHelper.getExtensionStack().addAll(helper.getExtensionStack());
			getProject().addReference(ProjectHelper.PROJECTHELPER_REFERENCE, subHelper);

			subHelper.parse(getProject(), importedResource);

			// push back the stack from the sub helper to the main one
			getProject().addReference(ProjectHelper.PROJECTHELPER_REFERENCE, helper);
			helper.getImportStack().clear();
			helper.getImportStack().addAll(subHelper.getImportStack());
			helper.getExtensionStack().clear();
			helper.getExtensionStack().addAll(subHelper.getExtensionStack());
		}
		catch(BuildException ex)
		{
			throw ProjectHelper.addLocationToBuildException(ex, getLocation());
		}
		finally
		{
			setProjectHelperProps(oldPrefix, oldSep, oldIncludeMode);
		}
	}

	private Resource getFileAttributeResource()
	{
		// Paths are relative to the build file they're imported from,
		// *not* the current directory (same as entity includes).

		if(file != null)
		{
			File buildFile = new File(getLocation().getFileName()).getAbsoluteFile();
			if(buildFile.exists())
			{
				File buildFileParent = new File(buildFile.getParent());
				File importedFile = FILE_UTILS.resolveFile(buildFileParent, file);
				return new FileResource(importedFile);
			}
			// maybe this import tasks is inside an imported URL?
			try
			{
				URL buildFileURL = new URL(getLocation().getFileName());
				URL importedFile = new URL(buildFileURL, file);
				return new URLResource(importedFile);
			}
			catch(MalformedURLException ex)
			{
				log(ex.toString(), Project.MSG_VERBOSE);
			}
			throw new BuildException("failed to resolve " + file + " relative to " + getLocation().getFileName());
		}
		return null;
	}

	/**
	 * Whether the task is in include (as opposed to import) mode.
	 * <p/>
	 * <p>In include mode included targets are only known by their
	 * prefixed names and their depends lists get rewritten so that
	 * all dependencies get the prefix as well.</p>
	 * <p/>
	 * <p>In import mode imported targets are known by an adorned as
	 * well as a prefixed name and the unadorned target may be
	 * overwritten in the importing build file.  The depends list of
	 * the imported targets is not modified at all.</p>
	 *
	 * @since Ant 1.8.0
	 */
	protected final boolean isInIncludeMode()
	{
		return "include".equals(getTaskType());
	}

	/**
	 * Sets a bunch of Thread-local ProjectHelper properties.
	 *
	 * @since Ant 1.8.0
	 */
	private static void setProjectHelperProps(String prefix, String prefixSep, boolean inIncludeMode)
	{
		ProjectHelper.setCurrentTargetPrefix(prefix);
		ProjectHelper.setCurrentPrefixSeparator(prefixSep);
		ProjectHelper.setInIncludeMode(inIncludeMode);
	}
}
