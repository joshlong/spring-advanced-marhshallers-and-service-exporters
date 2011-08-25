package org.springframework.thrift;

import org.apache.avro.Schema;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/**
 * Provides a consistent way to return a {@link org.apache.avro.Schema} object which is crucial for every use of Avro
 *
 * @author Josh Long
 */
public class SchemaFactoryBean implements FactoryBean<Schema>, InitializingBean {


	private String schema;
	private Schema result;
	private Log log = LogFactory.getLog(getClass());
	private Resource location;
	private volatile boolean setup = false;
	private final Object lock = new Object();

	public SchemaFactoryBean(String schema) {
		this.schema = schema;
		Assert.notNull(this.schema, "the 'schema' must not be null");
		try {
			afterPropertiesSet();
		} catch (Exception e) {
			log.error("something went wrong in trying to construct the Schema ");
		}

	}

	public SchemaFactoryBean(Resource location) {
		this.location = location;
		Assert.notNull(this.location, "the 'location' must not be null");
		try {
			afterPropertiesSet();
		} catch (Exception e) {
			log.error("something went wrong in trying to construct the Schema ");
		}
	}

	/**
	 * <P> points to a location on (URL, file, etc.) for the file to be read in
	 * <P> this option is mutually exclusive with the {@link #schema schema property}.
	 *
	 * @param location the location of a <CODE>.avpr</CODE> (or otherwise) file to read in
	 */
	public void setLocation(Resource location) {
		this.location = location;
	}

	/**
	 * <p> The schema itself might be defined inline, in the XML or Java configuration.
	 * <p>this option is mutually exclusive with the {@link #location location property}
	 *
	 * @param schema the string for the schema itself.
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}

	@Override
	public Schema getObject() throws Exception {
		afterPropertiesSet();
		return this.result;
	}

	@Override
	public Class<?> getObjectType() {
		return Schema.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		synchronized (this.lock) {
			if (this.setup) {
				return;
			}

			boolean inline = StringUtils.hasText(this.schema);
			boolean external = location != null  ;
			boolean somethingsProvided = inline || external;

			Assert.isTrue(somethingsProvided, "the 'location' property or the 'schema' property must be set (but not both)");

			if (!StringUtils.hasText(schema)) {
				if (log.isDebugEnabled()) {
					log.debug("the 'validate' property is ignored if you do not provide a schema JSON string defintion inline");
				}
			}

			if (inline) {
				result = Schema.parse(this.schema, true);
			} else {
				result = Schema.parse(this.location.getInputStream());
			}
			this.setup = true;
		}
	}
}
