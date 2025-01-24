package hr.adriaticanimation.saf_planner.utils;

import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JwtKeyProvider {

    private final byte[] secretKey;

    public JwtKeyProvider() {
        this.secretKey = Jwts.SIG.HS256
                .key()
                .build()
                .getEncoded();
    }
}
