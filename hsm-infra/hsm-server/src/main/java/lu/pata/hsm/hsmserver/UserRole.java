package lu.pata.hsm.hsmserver;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ADMIN(Code.ADMIN,"kms-admin"),
    USER(Code.USER,"kms-user");

    private final String authority;
    private final String issuer;

    UserRole(String authority, String issuer) {
        this.authority = authority;
        this.issuer=issuer;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public String getIssuer(){
        return issuer;
    }

    public class Code {
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String USER = "ROLE_USER";
    }
}

