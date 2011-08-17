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
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Josh Long
 */
@SuppressWarnings("unchecked")
public abstract class MessagePackUtils {

	static private Log log = LogFactory.getLog(MessagePackUtils.class);

	private static final BeansTemplateBuilder beansTemplateBuilder = new BeansTemplateBuilder();

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

		Assert.isInstanceOf(Collection.class, in, "this is not a known Collection");

		return null ;
	}

	private static Object convertMessagePackObject(Object input , Class<?> clzz ){
		if(input instanceof MessagePackObject){
			MessagePackObject messagePackObject = (MessagePackObject) input;
			return messagePackObject.convert( clzz);
		}
		return null ;
	}


	/**
	 * this shoud ideally do something smart like recursively walk the object tree, but for now let's worry about top level stuff ....
	 */
	public static <T> void expandResultIntoExpectedObjectGraph(Class<T> clzz, T result) throws Throwable {
		Assert.isInstanceOf(clzz, result);
		PropertyDescriptor descriptor[] = BeanUtils.getPropertyDescriptors(clzz);
		for (PropertyDescriptor pd : descriptor) {
			Method readMethod = pd.getReadMethod();
			Method writeMethod = pd.getWriteMethod();
			Class<?> readerReturnClazz = readMethod.getReturnType();
			if(readerReturnClazz.isPrimitive()||MessagePackObject.class.isAssignableFrom(readerReturnClazz)){
				// do nothing
			} else if (Collection.class.isAssignableFrom(readerReturnClazz)) {
				Collection values = (Collection) readMethod.invoke(result);
				Class[] genericClasses = MessagePackReflectionUtils.getGenericTypesForReturnValue(readMethod);
				Collection destination = buildReplacementCollectionForOriginalProperty(values);
				for(Object srcObject : values){
					destination.add(convertMessagePackObject(srcObject,  genericClasses[0]));
				}
				// recurse
				/*for( Object o : destination){
				  expandResultIntoExpectedObjectGraph( genericClasses[0], o  );
				}*/

				writeMethod.invoke(result ,destination);

			} else {
				// its a generic object in the type registry somewhere
			}
		}
	}

	public static void registerClass(Class<?> clazz, boolean serializeJavaBeanProperties) {
		String javaLangPackage = String.class.getPackage().getName();
		String messagePackPackage = MessagePack.class.getPackage().getName();
		String clazzName = clazz.getName();
		if (!clazzName.startsWith(javaLangPackage) && !clazzName.startsWith(messagePackPackage) && !clazz.isInterface() && !clazz.isPrimitive()) {
			if (serializeJavaBeanProperties) {
				Template template = beansTemplateBuilder.buildTemplate(clazz);
				MessagePack.register(clazz, template);
			} else {
				MessagePack.register(clazz);
			}
		}
	}

	public static void registerClassesOnInterface(Class<?> clzz, boolean serialize) {
		final Set<Class<?>> classSet = new HashSet<Class<?>>();

		ReflectionUtils.doWithMethods(clzz, new ReflectionUtils.MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				Collections.addAll(classSet, method.getParameterTypes());
				Collections.addAll(classSet, (Class<?>) method.getReturnType());
			}
		});

		for (Class<?> clazz : classSet) {
			registerClass(clazz, serialize);
		}
	}

	public static void registerClassesOnInterface(Object targetService, boolean serializeJavaBeanProperties) throws Exception {
		Class<?> clzz = targetService.getClass();
		registerClassesOnInterface(clzz, serializeJavaBeanProperties);
	}
}
