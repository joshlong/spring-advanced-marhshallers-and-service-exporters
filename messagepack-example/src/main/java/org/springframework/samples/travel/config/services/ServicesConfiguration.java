package org.springframework.samples.travel.config.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.samples.travel.domain.Hotel;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple class that initializes all the services including data access logic
 */
@PropertySource("/ds.standalone.properties")
@ComponentScan("org.springframework.samples.travel.services")
@EnableTransactionManagement
@Configuration
public class ServicesConfiguration {

	private Log log = LogFactory.getLog(getClass()) ;

	@Autowired private Environment environment;

	@Bean(name = "dataSource")
	@SuppressWarnings("unchecked")
	public DataSource dataSource() throws Exception {
		SimpleDriverDataSource driverManagerDataSource = new SimpleDriverDataSource();
		driverManagerDataSource.setUrl(this.environment.getProperty("ds.url"));
		driverManagerDataSource.setPassword(this.environment.getProperty("ds.password"));
		driverManagerDataSource.setUsername(this.environment.getProperty("ds.user"));
		driverManagerDataSource.setDriverClass((Class<Driver>) Class.forName(this.environment.getProperty("ds.driverClassName")));
		return driverManagerDataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager() throws Exception {
		return new JpaTransactionManager(this.entityManagerFactory().getObject());
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Exception {

		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setGenerateDdl(true);
		jpaVendorAdapter.setShowSql(true);
	   	jpaVendorAdapter.setDatabase(Database.H2);
		Map<String, String> props = new HashMap<String, String>();


		// validate or create
		props.put("hibernate.hbm2ddl.auto", "validate"); // it will not attempt to run import.sql, if set to 'validate'! be careful

		if(log.isDebugEnabled()) {
			log.debug("the 'hibernate.hbm2ddl.auto' property was set to 'validate,' which means it will not attempt to run 'import.sql' by itself. " +
					  "You should manually run the statements in 'import.sql,' yourself to ensure correct operating conditions for the application.");
		}

		LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
		localContainerEntityManagerFactoryBean.setDataSource(dataSource());
		localContainerEntityManagerFactoryBean.setJpaPropertyMap(props);


		String entityPackage = Hotel.class.getPackage().getName();
		localContainerEntityManagerFactoryBean.setPackagesToScan(new String[]{entityPackage});

		// look ma, no persistence.xml !
		return localContainerEntityManagerFactoryBean;
	}




}
