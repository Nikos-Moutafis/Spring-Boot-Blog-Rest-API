package gr.example.blog.config;

import gr.example.blog.security.JwtAuthEntryPoint;
import gr.example.blog.security.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@SecurityScheme(
        name = "Bear Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SecurityConfig {


    private UserDetailsService userDetailsService;

    private final JwtAuthEntryPoint authEntryPoint;

    private final JwtAuthenticationFilter authenticationFilter;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthEntryPoint authEntryPoint, JwtAuthenticationFilter authenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.authEntryPoint = authEntryPoint;
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/login").permitAll()
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/api/register").permitAll()
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.DELETE, "/api/posts/{postId}").hasRole("ADMIN")
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/api/posts").hasRole("ADMIN")
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.PUT,"/api/posts/{postId}").hasRole("ADMIN")
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.DELETE, "/api/categories/{categoryId}").hasRole("ADMIN")
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST,"/api/categories").hasRole("ADMIN")
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.PUT,"/api/categories/{categoryId}").hasRole("ADMIN")
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST,"/api/posts/{postId}/comments").authenticated()
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.PUT,"/api/posts/{postId}/comments/{commentId}").authenticated()
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.DELETE,"/api/posts/{postId}/comments/{commentId}").authenticated()
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET,"/api/**").permitAll()
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/swagger-ui/**").permitAll()
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .and()
                .httpBasic()
                .and()
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
