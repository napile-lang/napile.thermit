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

package org.napile.thermit.types.selectors;

import java.io.File;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import org.napile.thermit.BuildException;
import org.napile.thermit.Project;
import org.napile.thermit.types.DataType;
import org.napile.thermit.types.selectors.modifiedselector.ModifiedSelector;

/**
 * This is the base class for selectors that can contain other selectors.
 *
 * @since 1.5
 */
public abstract class BaseSelectorContainer extends BaseSelector implements SelectorContainer
{

	private Vector<FileSelector> selectorsList = new Vector<FileSelector>();

	/**
	 * Default constructor.
	 */
	public BaseSelectorContainer()
	{
	}

	/**
	 * Indicates whether there are any selectors here.
	 *
	 * @return true if there are selectors
	 */
	public boolean hasSelectors()
	{
		dieOnCircularReference();
		return !(selectorsList.isEmpty());
	}

	/**
	 * Gives the count of the number of selectors in this container
	 *
	 * @return the number of selectors
	 */
	public int selectorCount()
	{
		dieOnCircularReference();
		return selectorsList.size();
	}

	/**
	 * Returns the set of selectors as an array.
	 *
	 * @param p the current project
	 * @return an array of selectors
	 */
	public FileSelector[] getSelectors(Project p)
	{
		dieOnCircularReference();
		FileSelector[] result = new FileSelector[selectorsList.size()];
		selectorsList.copyInto(result);
		return result;
	}

	/**
	 * Returns an enumerator for accessing the set of selectors.
	 *
	 * @return an enumerator for the selectors
	 */
	public Enumeration<FileSelector> selectorElements()
	{
		dieOnCircularReference();
		return selectorsList.elements();
	}

	/**
	 * Convert the Selectors within this container to a string. This will
	 * just be a helper class for the subclasses that put their own name
	 * around the contents listed here.
	 *
	 * @return comma separated list of Selectors contained in this one
	 */
	public String toString()
	{
		dieOnCircularReference();
		StringBuilder buf = new StringBuilder();
		Enumeration<FileSelector> e = selectorElements();
		while(e.hasMoreElements())
		{
			buf.append(e.nextElement().toString());
			if(e.hasMoreElements())
			{
				buf.append(", ");
			}
		}
		return buf.toString();
	}

	/**
	 * Add a new selector into this container.
	 *
	 * @param selector the new selector to add
	 */
	public void appendSelector(FileSelector selector)
	{
		selectorsList.addElement(selector);
		setChecked(false);
	}

	/**
	 * <p>This implementation validates the container by calling
	 * verifySettings() and then validates each contained selector
	 * provided that the selector implements the validate interface.
	 * </p>
	 * <p>Ordinarily, this will validate all the elements of a selector
	 * container even if the isSelected() method of some elements is
	 * never called. This has two effects:</p>
	 * <ul>
	 * <li>Validation will often occur twice.
	 * <li>Since it is not required that selectors derive from
	 * BaseSelector, there could be selectors in the container whose
	 * error conditions are not detected if their isSelected() call
	 * is never made.
	 * </ul>
	 */
	public void validate()
	{
		verifySettings();
		dieOnCircularReference();
		String errmsg = getError();
		if(errmsg != null)
		{
			throw new BuildException(errmsg);
		}
		Enumeration<FileSelector> e = selectorElements();
		while(e.hasMoreElements())
		{
			Object o = e.nextElement();
			if(o instanceof BaseSelector)
			{
				((BaseSelector) o).validate();
			}
		}
	}


	/**
	 * Method that each selector will implement to create their selection
	 * behaviour. This is what makes SelectorContainer abstract.
	 *
	 * @param basedir  the base directory the scan is being done from
	 * @param filename the name of the file to check
	 * @param file     a java.io.File object for the filename that the selector
	 *                 can use
	 * @return whether the file should be selected or not
	 */
	public abstract boolean isSelected(File basedir, String filename, File file);


    /* Methods below all add specific selectors */

	/**
	 * add a "Select" selector entry on the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addSelector(SelectSelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * add an "And" selector entry on the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addAnd(AndSelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * add an "Or" selector entry on the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addOr(OrSelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * add a "Not" selector entry on the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addNot(NotSelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * add a "None" selector entry on the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addNone(NoneSelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * add a majority selector entry on the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addMajority(MajoritySelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * add a selector date entry on the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addDate(DateSelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * add a selector size entry on the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addSize(SizeSelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * add a selector filename entry on the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addFilename(FilenameSelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * add an extended selector entry on the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addCustom(ExtendSelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * add a contains selector entry on the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addContains(ContainsSelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * add a present selector entry on the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addPresent(PresentSelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * add a depth selector entry on the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addDepth(DepthSelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * add a depends selector entry on the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addDepend(DependSelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * adds a different selector to the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addDifferent(DifferentSelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * adds a type selector to the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addType(TypeSelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * add a regular expression selector entry on the selector list
	 *
	 * @param selector the selector to add
	 */
	public void addContainsRegexp(ContainsRegexpSelector selector)
	{
		appendSelector(selector);
	}

	/**
	 * add the modified selector
	 *
	 * @param selector the selector to add
	 * @since thermit 1.6
	 */
	public void addModified(ModifiedSelector selector)
	{
		appendSelector(selector);
	}

	public void addReadable(ReadableSelector r)
	{
		appendSelector(r);
	}

	public void addWritable(WritableSelector w)
	{
		appendSelector(w);
	}

	/**
	 * add an arbitrary selector
	 *
	 * @param selector the selector to add
	 * @since Ant 1.6
	 */
	public void add(FileSelector selector)
	{
		appendSelector(selector);
	}

	protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException
	{
		if(isChecked())
		{
			return;
		}
		if(isReference())
		{
			super.dieOnCircularReference(stk, p);
		}
		else
		{
			for(FileSelector fileSelector : selectorsList)
			{
				if(fileSelector instanceof DataType)
				{
					pushAndInvokeCircularReferenceCheck((DataType) fileSelector, stk, p);
				}
			}
			setChecked(true);
		}
	}
}
