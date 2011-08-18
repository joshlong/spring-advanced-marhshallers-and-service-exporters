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
