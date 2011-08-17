package org.springframework.util.messagepack;

import com.sun.jmx.remote.internal.ArrayQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.msgpack.MessagePack;
import org.msgpack.MessagePackObject;
import org.msgpack.Template;
import org.msgpack.template.builder.BeansTemplateBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Josh Long
 */
@SuppressWarnings("unchecked")
public abstract class MessagePackUtils {

//	static private Log log = LogFactory.getLog(MessagePackUtils.class);

	private static final BeansTemplateBuilder beansTemplateBuilder = new BeansTemplateBuilder();

	private static final Log log = LogFactory.getLog(MessagePackUtils.class.getName());

	private static <T> Collection<T> buildReplacementCollectionForOriginalProperty(Collection<T> in) throws Throwable {

		int size = in.size();
		if (in.getClass().isInterface()) {

			if (in.getClass().equals(Set.class)) {
				return new HashSet<T>(size);
			}
			if (in.getClass().equals(List.class)) {
				return new ArrayList<T>(size);
			}
			if (in.getClass().equals(Queue.class)) {
				return new ArrayQueue<T>(size);
			}

		} else {
			return in.getClass().newInstance();
		}

		// it's impossible to get here, right?
		Assert.isInstanceOf(Collection.class, in, "this is not a known Collection");

		return null;
	}

	private static Object convertMessagePackObject(Object input, Class<?> clzz) {
		if (input instanceof MessagePackObject) {
			MessagePackObject messagePackObject = (MessagePackObject) input;
			return messagePackObject.convert(clzz);
		}
		return null;
	}


	/**
	 * this shoud ideally do something smart like recursively walk the object tree, but for now let's worry about top level stuff ....
	 */
	public static <T> T remapResult(T result) throws Throwable {
		Class<?> clazzOfT = result.getClass();

		if (ReflectionUtils.isUninterestingClass(clazzOfT)) {
			return result;
		}

		PropertyDescriptor descriptor[] = BeanUtils.getPropertyDescriptors(clazzOfT);
		for (PropertyDescriptor pd : descriptor) {
			Method readMethod = pd.getReadMethod();
			Method writeMethod = pd.getWriteMethod();
			Class<?> readerReturnClazz = readMethod.getReturnType();
			if (readerReturnClazz.isPrimitive() || MessagePackObject.class.isAssignableFrom(readerReturnClazz)) {
				// do nothing
			} else if (Collection.class.isAssignableFrom(readerReturnClazz)) {
				Collection values = (Collection) readMethod.invoke(result);
				Class[] genericClasses = TypeUtils.getGenericTypesForReturnValue(readMethod);
				Collection destination = buildReplacementCollectionForOriginalProperty(values);
				for (Object srcObject : values) {
					destination.add(convertMessagePackObject(srcObject, genericClasses[0]));
				}
				writeMethod.invoke(result, destination);
			} else {
				// its a generic object in the type registry somewhere
			}
		}

		return result ;
	}


	public static void registerClass(Class<?> clazz, boolean serializeJavaBeanProperties) {

		if (ReflectionUtils.isUninterestingClass(clazz)) {
			return;
		}

		if (serializeJavaBeanProperties) {
			Template template = beansTemplateBuilder.buildTemplate(clazz);
			MessagePack.register(clazz, template);
		} else {
			MessagePack.register(clazz);
		}
	}

	/**
	 * Scans a given class and looks at all methods, return parameters, etc., and automatically registers them.
	 * First, this does not (for obvious reasons) register primitives, or JDK classes, or even java.util.* classes, since
	 * there's no possible use case for those and since - in the common case - the serialization already supports them, anyway.
	 *
	 * @param clzz      the interface to scan for types
	 * @param serialize whether or not the JavaBean types should be serialized using javabeans conventions or the public class property.
	 */
	public static void findAndRegisterAllClassesRelatedToClass(final Class<?> clzz, final boolean serialize) {
		ReflectionUtils.crawlJavaBeanObjectGraph(clzz, new ReflectionUtils.ClassTraversalCallback() {
			@Override
			public void doWithClass(Class<?> foundClass) {
				if (log.isDebugEnabled()) {
					log.debug("found " + foundClass.getName() + ".");
				}
				MessagePackUtils.registerClass(foundClass, serialize);
			}
		});
	}



}
