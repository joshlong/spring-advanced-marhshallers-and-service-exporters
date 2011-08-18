package org.springframework.messagepack.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.msgpack.MessagePack;
import org.springframework.core.GenericCollectionTypeResolver;

import java.lang.reflect.Method;
import java.util.*;

/**
 * This code crawls the entire object graph, looking for interesting objects that are found on your service interface. It ignores
 * obviously incorrect objects like objects from MesssagePack itself (which shouldnt be registered), and objects from the JDK that
 * already are registered (like 'int') or shouldn't be (like 'void')
 *
 * @author Josh Long
 */
public abstract class ReflectionUtils {

	private static Log log = LogFactory.getLog(ReflectionUtils.class);


	/**
	 * Callback for the {@link TypeUtils#getGenericTypesForReturnValue(java.lang.reflect.Method)} method.
	 */
	public static interface ClassTraversalCallback {
		void doWithClass(Class<?> o);
	}

	/**
	 * Utility object to help traverse objects to "discover" any related types.
	 */
	private static class ObjectClassTraverser {

		private Set<String> prefixesToAvoid = new HashSet<String>();

		private ObjectClassTraverser(Set<String> prefixesToAvoid) {
			this.prefixesToAvoid = prefixesToAvoid;
		}

		private Collection<Class> findClassesToVisit(Method method) {
			Set<Class> classes = new HashSet<Class>();
			classes.add(method.getReturnType());
			Collections.addAll(classes, method.getParameterTypes());
			Collections.addAll(classes, TypeUtils.getGenericTypesForReturnValue(method));
			return classes;
		}

		private org.springframework.util.ReflectionUtils.MethodFilter nonObjectMethodsFilter = new org.springframework.util.ReflectionUtils.MethodFilter() {
			private List<Method> methods = Arrays.asList(Object.class.getDeclaredMethods());
			private String packageForJavaLang = String.class.getPackage().getName();

			@Override
			public boolean matches(Method method) {

				if (log.isDebugEnabled()) {
					log.debug(method.toGenericString());
					log.debug("is found? : " + methods.contains(method));
				}

				return !method.getDeclaringClass().getPackage().getName().equalsIgnoreCase(packageForJavaLang)
						       && !methods.contains(method);
			}
		};

		private boolean shouldSkip(Class<?> clzz) {
			String className = clzz.getName();
			for (String p : this.prefixesToAvoid) {
				if (className.startsWith(p)) {
					return true;
				}
			}
			return false;
		}


		protected void doCrawl(Class<?> clzz, final Set<Class> toVisit, final ClassTraversalCallback callback) {
			org.springframework.util.ReflectionUtils.MethodCallback mc = new org.springframework.util.ReflectionUtils.MethodCallback() {
				@Override
				public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
					Collection<Class> classesToVisit = findClassesToVisit(method);
					for (Class c : classesToVisit) {
						if (!isUninterestingClass(c) && !shouldSkip(c)) {
							if (!toVisit.contains(c)) {
								toVisit.add(c);
								doCrawl(c, toVisit, callback);
								callback.doWithClass(c);
							}
						}
					}
				}
			};
			org.springframework.util.ReflectionUtils.doWithMethods(clzz, mc, nonObjectMethodsFilter);
		}
	}

	public static void crawlJavaBeanObjectGraph(Class<?> src, ClassTraversalCallback objectCallback, Set<String> classNamePrefixesToAvoid) {
		final Set<Class> classesToVisit = new HashSet<Class>();
		ObjectClassTraverser classTraverser = new ObjectClassTraverser(classNamePrefixesToAvoid);
		classTraverser.doCrawl(src, classesToVisit, objectCallback);
	}

	public static void crawlJavaBeanObjectGraph(Class<?> src, ClassTraversalCallback objectCallback) {
		crawlJavaBeanObjectGraph(src, objectCallback, new HashSet<String>());
	}


	/**
	 * is the class a unique class likely to be worth registering?
	 *
	 * @param clazz
	 * @return
	 */
	public static boolean isUninterestingClass(Class<?> clazz) {
		String javaPackage = "java";
		String messagePackPackage = MessagePack.class.getPackage().getName();
		String clazzName = clazz.getName();
		return !(!clazzName.startsWith(javaPackage) && !clazzName.startsWith(messagePackPackage) &&
				         !clazz.isInterface() && !clazz.isPrimitive() && !clazz.isArray() &&
				         !clazzName.startsWith(MessagePack.class.getPackage().getName()));
	}

}
