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

package org.springframework.messagepack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.msgpack.MessagePack;
import org.msgpack.Template;
import org.msgpack.template.builder.BeansTemplateBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.messagepack.util.ReflectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Josh Long
 */
public class MessagePackRegistrar implements InitializingBean {
	private BeansTemplateBuilder beansTemplateBuilder = new BeansTemplateBuilder();

	private boolean serializeJavaBeanProperties = true;

	private Set<Class> classes = new HashSet<Class>();

	private Log log = LogFactory.getLog(getClass());

	public void setSerializeJavaBeanProperties(boolean serializeJavaBeanProperties) {
		this.serializeJavaBeanProperties = serializeJavaBeanProperties;
	}

	public void registerClasses(Collection<Class> clzz) {
		classes.addAll(clzz);
	}

	public void registerClasses(Class... clazz) {
		Collections.addAll(classes, clazz);
	}

	public void discoverClasses(Class clazzInterface) {
		ReflectionUtils.crawlJavaBeanObjectGraph(clazzInterface, new ReflectionUtils.ClassTraversalCallback() {
			@Override
			public void doWithClass(Class<?> foundClass) {
				if (log.isDebugEnabled()) {
					log.debug("found " + foundClass.getName() + ".");
				}

				if (ReflectionUtils.isUninterestingClass(foundClass)) {
					return;
				}
				if (!classes.contains(foundClass)) {
					classes.add(foundClass);
				}

			}
		});
	}

	protected void register() {
		for (Class foundClass : this.classes) {
			if (serializeJavaBeanProperties) {
				Template template = beansTemplateBuilder.buildTemplate(foundClass);
				MessagePack.register(foundClass, template);
			} else {
				MessagePack.register(foundClass);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		register();
	}
}
