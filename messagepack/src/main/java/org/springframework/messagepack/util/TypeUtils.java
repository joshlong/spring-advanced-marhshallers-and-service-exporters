package org.springframework.messagepack.util;

import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/***
 * patterned very much after {@link org.springframework.util.TypeUtils}
 *
 * @author Josh Long
 */
abstract public class TypeUtils {


	public static Class[] getGenericTypesForReturnValue(Method method) {
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
