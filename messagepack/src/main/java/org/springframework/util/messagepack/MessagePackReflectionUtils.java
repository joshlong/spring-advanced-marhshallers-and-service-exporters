package org.springframework.util.messagepack;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * TODO we need to crawl the entire object graph and make sure that every type that can be registered with MessagePack is indeed registered
 *
 */
public abstract class MessagePackReflectionUtils {

	public static Class[] getGenericTypesForReturnValue(Method method) {
		ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();

		Type[] type = genericReturnType.getActualTypeArguments();
		Class[] classes = new Class[type.length];
		int ctr = 0;
		for (Type t : type) {
			Assert.isInstanceOf(Class.class, t);
			classes[ctr++] = (Class) t;
		}
		return classes;

	}

	static interface ObjectClassTraversalCallback {
		void doWithObject(Object o);
	}


	private Collection<Class> findClassesToVisit(Method method) {
		Set<Class> classes = new HashSet<Class>();
		classes.add(method.getReturnType());
		Collections.addAll(classes, method.getParameterTypes());
		Collections.addAll(classes, MessagePackReflectionUtils.getGenericTypesForReturnValue(method));
		return classes;
	}

	public void crawlJavaBeanObjectGraph(Object src, ObjectClassTraversalCallback objectCallback) {

		final Set<Class> classesToVisit = new HashSet<Class>();

		ReflectionUtils.doWithMethods(src.getClass(), new ReflectionUtils.MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				// ok, we have a list of candidate types to visit
				Collection<Class> classesToVisit = findClassesToVisit(method);


			}
		});
	}
}
