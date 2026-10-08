package br.com.best2bee.alunos_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration 
public class UserConfig {

    @Bean 
    public UserDetailsService userDetailsService 
    (PasswordEncoder encoder, @Value("${API_USERNAME}") String username, @Value ("${API_PASSWORD}") String password){
        
        UserDetails admin = User.builder()
        .username(username)
        .password(encoder.encode(password))
        .roles("ADMIN")
        .build();

        return new InMemoryUserDetailsManager(admin);
    }
}
