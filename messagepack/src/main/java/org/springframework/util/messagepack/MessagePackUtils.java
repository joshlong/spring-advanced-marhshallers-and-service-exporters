package org.springframework.util.messagepack;

import org.msgpack.MessagePack;
import org.msgpack.Template;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.template.builder.BeansTemplateBuilder;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Josh Long
 */
public abstract class MessagePackUtils {

	private static final BeansTemplateBuilder beansTemplateBuilder = new BeansTemplateBuilder();

	public static void registerClass(Class<?> clazz, boolean serializeJavaBeanProperties) {
		String javaLangPackage = String.class.getPackage().getName();
		String messagePackPackage = MessagePack.class.getPackage().getName();

		String clazzName = clazz.getName();
		if (!clazzName.startsWith(javaLangPackage) &&
			!clazzName.startsWith(messagePackPackage) &&
			!clazz.isInterface() &&
			!clazz.isPrimitive()) {

			if (serializeJavaBeanProperties) {
				Template template = beansTemplateBuilder.buildTemplate(clazz);
				MessagePack.register(clazz, template);
			} else {
				MessagePack.register(clazz);
			}
		}
	}

	public static void registerClassesOnInterface(Class<?> clzz , boolean serialize){
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

	public static void  registerClassesOnInterface (Object targetService, boolean serializeJavaBeanProperties) throws Exception {

		Class<?> clzz =targetService.getClass() ;

		registerClassesOnInterface(clzz, serializeJavaBeanProperties);

	}
}
