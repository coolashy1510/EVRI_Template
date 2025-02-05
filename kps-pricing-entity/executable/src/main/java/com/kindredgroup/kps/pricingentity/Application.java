package com.kindredgroup.kps.pricingentity;

import com.kindredgroup.kps.ApplicationSkeleton;
import com.kindredgroup.kps.pricingentity.persistence.config.DatasourceConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class Application extends ApplicationSkeleton {

    public static void main(String[] args) {
        ApplicationSkeleton.run(Application.class, DatasourceConfig.class, args);
    }

}
