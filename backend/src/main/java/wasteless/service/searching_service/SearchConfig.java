package wasteless.service.searching_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class SearchConfig {
    @Bean
    public UserSearchImpl userSearchService() {
        return new UserSearchImpl();
    }

    @Bean
    public BusinessSearchImpl businessSearchService() {
        return new BusinessSearchImpl();
    }

    @Bean
    public SaleItemSearchImpl saleItemSearchService() { return new SaleItemSearchImpl(); }
}
