package org.springframework.util.messagepack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.*;

/**
 * TODO we need to crawl the entire object graph and make sure that every type that can be registered with MessagePack is indeed registered
 */
public abstract class MessagePackReflectionUtils {

	private static Log log = LogFactory.getLog(MessagePackReflectionUtils.class);

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
			Collections.addAll(classes, MessagePackReflectionUtils.getGenericTypesForReturnValue(method));
			return classes;
		}

		private ReflectionUtils.MethodFilter nonObjectMethodsFilter = new ReflectionUtils.MethodFilter() {
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
			final String javaLang = "java";
			ReflectionUtils.MethodCallback mc = new ReflectionUtils.MethodCallback() {
				@Override
				public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
					Collection<Class> classesToVisit = findClassesToVisit(method);
					for (Class c : classesToVisit) {
						boolean isVoid = c == (Void.class) || c.equals(Void.class);
						Package pkg = c.getPackage();
						boolean jdkType = pkg != null && ((pkg.getName()) + "").startsWith(javaLang);
						boolean collectionType = Collection.class.isAssignableFrom(c);

						if (!isVoid && !jdkType && !collectionType && !c.isPrimitive()) {
							if (!shouldSkip(c)) {
								if (!toVisit.contains(c)) {
									toVisit.add(c);
									doCrawl(c, toVisit, callback);
									callback.doWithClass(c);
								}
							}

						}
					}
				}
			};
			ReflectionUtils.doWithMethods(clzz, mc, nonObjectMethodsFilter);
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


}
