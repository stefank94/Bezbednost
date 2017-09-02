package app.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .antMatcher("/**").authorizeRequests().antMatchers("/api/user/login",
            "/api/user/logged", "/api/user/register" , "/webjars/**", "/static/**", "/",
                "/certificates/**", "/crl/**", "/api/crl/pliz", "/api/cert/cerFile/**").permitAll()
            .antMatchers("/api/issuer/create", "/api/request/getAllSubmittedRequests",
                    "/api/request/approve", "/api/request/reject", "/api/crl/**", "/api/admin/registration")
                    .hasAuthority("ADMIN")
            .anyRequest().authenticated()
            .and().exceptionHandling()
            .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"))
            .and().logout().logoutUrl("/api/user/logout")
            .deleteCookies("JSESSIONID").logoutSuccessUrl("/").permitAll()
            .and().csrf().disable();
    }

}
