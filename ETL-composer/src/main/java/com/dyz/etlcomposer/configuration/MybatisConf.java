package com.dyz.etlcomposer.configuration;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
//import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class MybatisConf {
  @Bean("sqlSessionFactory")
//  @Primary
  public SqlSessionFactory sqlSessionFactory(@Qualifier("mainDruid") DataSource dataSource) throws Exception {
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    try {
      Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml");
    } catch (Exception e) {
      e.printStackTrace();
    }

    sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
    sqlSessionFactoryBean.setDataSource(dataSource);
    DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();

    Properties properties = new Properties();
//    properties.setProperty("Oracle", "oracle");
    properties.setProperty("mySql", "mysql");
//    properties.setProperty("SQL Server", "sqlserver");
//    databaseIdProvider.setProperties(properties);
    sqlSessionFactoryBean.setDatabaseIdProvider(databaseIdProvider);
    return sqlSessionFactoryBean.getObject();
  }

  @Bean("sqlSessionTemplate")
//  @Primary
  public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
//        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
//        sqlSessionTemplate.
    // insert null
//    sqlSessionFactory.getConfiguration().
    sqlSessionFactory.getConfiguration().setJdbcTypeForNull(JdbcType.NULL);
    return new SqlSessionTemplate(sqlSessionFactory);
  }

  /**
   * 在 MyBatis-Spring 1.0.2 之前，sqlSessionFactoryBean 和 sqlSessionTemplateBean是唯一可用的。
   * 但由于 MapperScannerConfigurer 在启动过程中比 PropertyPlaceholderConfigurer 运
   * 行得更早，经常会产生错误。基于这个原因，上述的属性已被废弃，现在建议使用 sqlSessionFactoryBeanName 和 sqlSessionTemplateBeanName 属性。
   * @return
   */
  @Bean("mainScan")
//  @Primary
  public MapperScannerConfigurer mapperScannerConfigurer() {
    MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
    mapperScannerConfigurer.setBasePackage("com.dyz.dev.dao");
    mapperScannerConfigurer.setSqlSessionTemplateBeanName("sqlSessionTemplate");
    Properties properties = new Properties();

    mapperScannerConfigurer.setProperties(properties);
    return mapperScannerConfigurer;
  }
}
