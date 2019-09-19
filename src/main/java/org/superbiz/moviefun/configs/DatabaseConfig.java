package org.superbiz.moviefun.configs;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.superbiz.moviefun.DatabaseServiceCredentials;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {


    @Bean
    public DatabaseServiceCredentials databaseServiceCredentials(@Value("#{environment.VCAP_SERVICES}") String vcapServicesJson){
        return new DatabaseServiceCredentials(vcapServicesJson);
    }

    @Bean("albumsDataSource")
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
        HikariDataSource hDataSource = new HikariDataSource();
        hDataSource.setDataSource(dataSource);
        return dataSource;
    }

    @Bean("moviesDataSource")
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
        HikariDataSource hDataSource = new HikariDataSource();
        hDataSource.setDataSource(dataSource);
        return dataSource;
    }


    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter(){
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.MYSQL);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        adapter.setGenerateDdl(true);
        adapter.setShowSql(true);
        return adapter;
    }

    @Bean("moviesLocalContainerEntityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean moviesLocalContainerEntityManagerFactoryBean(@Qualifier("moviesDataSource") DataSource dataSource, HibernateJpaVendorAdapter adapter){
        return localContainerEntityManagerFactoryBean(dataSource, adapter, "org.superbiz.moviefun.movies", "moviesPersistence" );
    }

    @Bean("albumsLocalContainerEntityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean albumsLocalContainerEntityManagerFactoryBean(@Qualifier("albumsDataSource") DataSource dataSource, HibernateJpaVendorAdapter adapter){
        return localContainerEntityManagerFactoryBean(dataSource, adapter, "org.superbiz.moviefun.albums", "albumsPersistence" );
    }

    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(DataSource dataSource, HibernateJpaVendorAdapter adapter, String scanPackage, String unitName){
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setJpaVendorAdapter(adapter);
        factoryBean.setPackagesToScan(scanPackage);
        factoryBean.setPersistenceUnitName(unitName);
        return factoryBean;
    }

    @Bean("moviesPlatformTransactionManager")
    public PlatformTransactionManager moviesPlatformTransactionManager(@Qualifier("moviesLocalContainerEntityManagerFactoryBean") EntityManagerFactory entityManagerFactory){
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }

    @Bean("albumsPlatformTransactionManager")
    public PlatformTransactionManager albumsPlatformTransactionManager(@Qualifier("albumsLocalContainerEntityManagerFactoryBean") EntityManagerFactory entityManagerFactory){
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }


}
