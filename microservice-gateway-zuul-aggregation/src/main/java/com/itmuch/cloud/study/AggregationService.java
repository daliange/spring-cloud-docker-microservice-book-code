package com.itmuch.cloud.study;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import rx.Observable;

@Service
public class AggregationService {
	
  public static final Logger logger = LoggerFactory.getLogger(AggregationService.class);
  
  @Autowired
  private RestTemplate restTemplate;

  @HystrixCommand(fallbackMethod = "fallback")
  public Observable<User> getUserById(Long id) {
    // 创建一个被观察者
    return Observable.create(observer -> {
      // 请求用户微服务的/{id}端点
      User user = restTemplate.getForObject("http://microservice-provider-user/{id}", User.class, id);
      observer.onNext(user);
      observer.onCompleted();
    });
  }

  @HystrixCommand(fallbackMethod = "fallback")
  public Observable<User> getMovieUserByUserId(Long id) {
    return Observable.create(observer -> {
      // 请求电影微服务的/user/{id}端点
      User movieUser = restTemplate.getForObject("http://microservice-consumer-movie/user/{id}", User.class, id);
      observer.onNext(movieUser);
      observer.onCompleted();
    });
  }

  public User fallback(Long id) {
	logger.info("调用@HystrixCommand-------fallback--------");
    User user = new User();
    user.setId(-1L);
    return user;
  }
}
