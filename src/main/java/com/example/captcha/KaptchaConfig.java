package com.example.captcha.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha captchaProducer() {
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        
        Properties props = new Properties();
        // الإعدادات الأساسية
        props.setProperty("kaptcha.border", "no");
        props.setProperty("kaptcha.textproducer.font.color", "black");
        props.setProperty("kaptcha.image.width", "200");
        props.setProperty("kaptcha.image.height", "60");
        props.setProperty("kaptcha.textproducer.font.size", "40");
        props.setProperty("kaptcha.session.key", "captchaCode");
        props.setProperty("kaptcha.textproducer.char.length", "5");
        props.setProperty("kaptcha.textproducer.font.names", "Arial");
        props.setProperty("kaptcha.noise.color", "black");
        props.setProperty("kaptcha.background.clear.from", "white");
        props.setProperty("kaptcha.background.clear.to", "white");
        
        Config config = new Config(props);
        kaptcha.setConfig(config);
        
        return kaptcha;
    }
}