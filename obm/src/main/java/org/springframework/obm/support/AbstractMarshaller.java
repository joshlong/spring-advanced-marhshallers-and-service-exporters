/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.obm.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.obm.Marshaller;
import org.springframework.obm.Unmarshaller;

/**
 * Simple base class to make sure we make as many things common and reusable as possible
 *
 * @author Josh Long
 */
abstract public class AbstractMarshaller<T> implements Marshaller<T>, Unmarshaller<T> {


    protected Log log = LogFactory.getLog(getClass());


}
