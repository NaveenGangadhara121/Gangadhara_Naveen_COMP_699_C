package ecommerce.sprinboot.customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class CustomerConfiguration extends WebSecurityConfigurerAdapter {

    // Provide user details service
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomerServiceConfig();  // Assuming CustomerServiceConfig is implemented
    }

    // Password encoder to hash passwords securely
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication provider to link user details and password encoder
    @Bean
    public DaoAuthenticationProvider provider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }

    // Configure the authentication manager to use the provider
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(provider());
    }

    // Configure HTTP security for your application
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // Disable CSRF for testing purposes
                .authorizeRequests()
                .antMatchers("/", "/home", "/login").permitAll()  // Allow unauthenticated access to these URLs
                .antMatchers("/wishlist/**").authenticated()  // Require authentication for wishlist
                .antMatchers("/customer/*").hasAuthority("CUSTOMER")  // Only allow "CUSTOMER" role to access customer pages
                .and()
                .formLogin()
                .loginPage("/login")  // Custom login page
                .loginProcessingUrl("/do-login")  // Login URL for form submission
                .defaultSuccessUrl("/")  // Redirect to the homepage after successful login
                .and()
                .logout()
                .invalidateHttpSession(true)  // Invalidate session on logout
                .clearAuthentication(true)  // Clear authentication
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))  // Custom logout URL
                .logoutSuccessUrl("/login?logout")  // Redirect to login page after successful logout
                .permitAll();
    }
}
