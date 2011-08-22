/*
 * Copyright 2002-2008 the original author or authors.
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


package org.springframework.messagepack.util;

import org.springframework.util.Assert;

import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

/**
 * patterned very much after {@link org.springframework.util.TypeUtils}
 *
 * @author Josh Long
 */
abstract public class TypeUtils {


	 static public Class[] getGenericTypesForReturnValue(Method method) {
		Type t1 = method.getGenericReturnType();
		if (t1 instanceof ParameterizedType) {

			ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();

			Type[] type = genericReturnType.getActualTypeArguments();
			Class[] classes = new Class[type.length];
			int ctr = 0;
			for (Type t : type) {
				if (!(t instanceof WildcardType)) {
					Assert.isInstanceOf(Class.class, t);
					classes[ctr++] = (Class) t;
				}
			}
			return classes;
		}
		return new Class[0];
	}
}
