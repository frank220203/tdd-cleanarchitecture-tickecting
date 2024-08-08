package frankproject.tdd_cleanarchitecture_ticketing.adapter.config;

import frankproject.tdd_cleanarchitecture_ticketing.adapter.interceptor.TokenInterceptor;
import frankproject.tdd_cleanarchitecture_ticketing.application.usecase.TokenUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TokenUsecase tokenUsecase;

    @Autowired
    public WebConfig(TokenUsecase tokenUsecase) {
        this.tokenUsecase = tokenUsecase;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenInterceptor(tokenUsecase))
                .addPathPatterns("/api/reservation/**")
                .addPathPatterns("/api/concerts/**")
                .excludePathPatterns("/api/concerts");
    }
}
