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

import org.napile.thermit.types.Resource;

/**
 * Extends Proxy interface for XSLT processors.
 *
 * @see XSLTProcess
 * @since Ant 1.7
 */
public interface XSLTLiaison3 extends XSLTLiaison2
{
	/**
	 * sets the stylesheet to use as a resource
	 *
	 * @param stylesheet the stylesheet to use as a resource
	 * @throws Exception if the stylesheet cannot be loaded
	 */
	void setStylesheet(Resource stylesheet) throws Exception;
}
