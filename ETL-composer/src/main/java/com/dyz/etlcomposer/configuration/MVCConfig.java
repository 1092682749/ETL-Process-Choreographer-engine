package com.dyz.etlcomposer.configuration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

import com.dyz.etlcomposer.utils.Constants;
import com.dyz.etlcomposer.utils.Result;
import com.dyz.etlcomposer.utils.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@Configuration
public class MVCConfig implements WebMvcConfigurer {

  private final Logger logger = LoggerFactory.getLogger(WebMvcConfigurer.class);





//  @Bean
//  public CorsFilter corsFilter() {
//    CorsConfiguration corsConfiguration = new CorsConfiguration();
//    /* 请求常用的三种配置，*代表允许所有，当时你也可以自定义属性（比如header只能带什么，只能是post方式等等）
//     */
//    corsConfiguration.addAllowedOrigin("*");
//    corsConfiguration.addAllowedHeader("*");
//    corsConfiguration.addAllowedMethod("*");
//    corsConfiguration.setAllowCredentials(true);
//
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", corsConfiguration);
//    return new CorsFilter(source);
//  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedHeaders("*").allowedMethods("*").allowedOriginPatterns("*").allowCredentials(true);
    WebMvcConfigurer.super.addCorsMappings(registry);
  }

  // 静态资源配置
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");
  }


  //使用阿里 FastJson 作为JSON MessageConverter
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
    FastJsonConfig config = new FastJsonConfig();
    config.setSerializerFeatures(SerializerFeature.WriteMapNullValue,//保留空的字段
      SerializerFeature.WriteNullStringAsEmpty,//String null -> ""
      SerializerFeature.WriteNullNumberAsZero);//Number null -> 0
    converter.setFastJsonConfig(config);
    converter.setDefaultCharset(Charset.forName("UTF-8"));
    StringHttpMessageConverter httpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
    converters.add(httpMessageConverter);
    converters.add(converter);
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.favorPathExtension(false);
  }

  //统一异常处理
  @Override
  public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
    exceptionResolvers.add(new HandlerExceptionResolver() {
      public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        Result result = new Result();
        if (e instanceof ServiceException) {//业务失败的异常，如“账号或密码错误”
          if (((ServiceException) e).code == null) {
            result.setCode(Constants.FAIL);
          } else {
            result.setCode(((ServiceException) e).code);
          }
          if (e.getMessage() == null) {
            result.setMessage(Constants.messageMap.get(((ServiceException) e).code));
          } else {
            result.setMessage(e.getMessage());
          }
          logger.info(e.getMessage());
        } else if (e instanceof NoHandlerFoundException) {
          result.setCode(Constants.NOT_FOUND);
          result.setMessage("接口 [" + request.getRequestURI() + "] 不存在");
        } else if (e instanceof ServletException) {
          result.setCode(Constants.FAIL);
          result.setMessage(e.getMessage());
        } else {
          result.setCode(Constants.INTERNAL_SERVER_ERROR);
          result.setMessage("接口 [" + request.getRequestURI() + "] 错误" + e.getMessage());
          String message;
          if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            message = String.format("接口 [%s] 出现异常，方法：%s.%s，异常摘要：%s",
              request.getRequestURI(),
              handlerMethod.getBean().getClass().getName(),
              handlerMethod.getMethod().getName(),
              e.getMessage());
          } else {
            message = e.getMessage();
          }
          logger.error(message, e);
        }
        responseResult(response, result);
        return new ModelAndView();
      }

    });
  }




  private void responseResult(HttpServletResponse response, Result result) {
//      DispatcherServlet
    response.setCharacterEncoding("UTF-8");
    response.setHeader("Content-type", "application/json;charset=UTF-8");
    response.setStatus(200);
    try {
      response.getWriter().write(JSON.toJSONString(result));
    } catch (IOException ex) {
      logger.error(ex.getMessage());
    }
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
//    registry.addInterceptor(new SessionInterceptor(cacheUtils)).addPathPatterns("/**").excludePathPatterns("/login");
//    registry.addInterceptor(new PermissionInterceptor(cacheUtils, allowIpService)).addPathPatterns("/**");

  }
}
