package ankokovin.fullstacktest.WebServer.Config;

import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
//@EnableTransactionManagement
public class PersistenceContext {

    //@Autowired
    //private DataSource dataSource;
//
    //@Bean
    //public DataSourceConnectionProvider connectionProvider() {
    //    return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy());
    //}
//
    //@Bean
    //public DefaultDSLContext dls() {
    //    return new DefaultDSLContext(configuration());
    //}
//
    //public DefaultConfiguration configuration() {
    //    DefaultConfiguration jooqConfig = new DefaultConfiguration();
    //    jooqConfig.set(connectionProvider());
    //    jooqConfig.set(new DefaultExecuteListenerProvider(jooqToSpringExceptionTransformer()));
    //    return jooqConfig;
    //}
//
    //@Bean
    //public JOOQToSpringExceptionTransformer jooqToSpringExceptionTransformer() {
    //    return new JOOQToSpringExceptionTransformer();
    //}

}
