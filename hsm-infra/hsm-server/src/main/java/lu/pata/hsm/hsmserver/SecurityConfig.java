package lu.pata.hsm.hsmserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    Logger log= LoggerFactory.getLogger(SecurityConfig.class);

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http.authorizeRequests()
                .anyRequest().authenticated().and()
                .x509()
                .subjectPrincipalRegex("CN=(.*?)(?:,|$)")
                .authenticationUserDetailsService(authenticationUserDetailsService());
    }

    @Bean
    public AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService(){
        return preAuthenticatedAuthenticationToken -> {
            String user=(String)preAuthenticatedAuthenticationToken.getPrincipal();
            X509Certificate cert=(X509Certificate)preAuthenticatedAuthenticationToken.getCredentials();

            UserRole role=null;
            for(UserRole r:UserRole.values()) if(r.getIssuer().equals(getSubjectName(cert.getIssuerX500Principal().getName()))) role=r;

            if(role!=null)
                return new User(user, "",AuthorityUtils.createAuthorityList(role.getAuthority()));
            else {
                log.error("Could not map issuer to a user role. user: {}, issuer: {}",getSubjectName(cert.getSubjectX500Principal().getName()),getSubjectName(cert.getIssuerX500Principal().getName()));
                throw new BadCredentialsException("Could not map issuer to a user role.");
            }
        };
    }

    /**
     * Extract the string name from a certificate subject
     * Ex. "CN=name" returns "name"
     * @param subject
     * @return
     */
    private String getSubjectName(String subject){
        Pattern pattern = Pattern.compile("CN=(.*?)(?:,|$)");
        Matcher matcher = pattern.matcher(subject);
        if(matcher.matches()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

