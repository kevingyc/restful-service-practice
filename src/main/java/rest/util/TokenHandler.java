package rest.util;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.modal.Token;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Kevingyc on 9/19/2017.
 */
public class TokenHandler {

    private static final Logger log = LoggerFactory.getLogger(TokenHandler.class);
    private Key key;

    public TokenHandler(Key key) {
        this.key = key;
    }

    public Token generateToken(String name) {
        assert name != null;

        Token token = new Token(name);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date iat = cal.getTime();
        cal.add(Calendar.HOUR_OF_DAY, 1);
        Date exp = cal.getTime();

        token.setIat(iat.getTime());
        token.setExp(exp.getTime());

        String compactJws = generateJWS(name, iat, exp);

        token.setToken(compactJws);
        return token;
    }

    public boolean isTokenAvailable(String token) {
        assert token != null;

        try {
            Claims claims =
                    Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();

            Date exp = claims.getExpiration();
            Date now = new Date();

            return now.compareTo(exp) < 0;
        }
        catch (SignatureException e) {
            log.error("jwt is untrusted.");
        }
        catch (ExpiredJwtException e) {
            log.error("token expired for id : " + e.getClaims().getId());
        }
        catch (Exception e) {
            log.error("wrong jwt.");
        }

        return false;
    }

    private String generateJWS(String subject, Date iat, Date exp) {

        return Jwts.builder()
                .setIssuedAt(iat)
                .setExpiration(exp)
                .setSubject(subject)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public String extendJWS(String originalJWS) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date iat = cal.getTime();
        cal.add(Calendar.HOUR_OF_DAY, 1);
        Date exp = cal.getTime();

        try {
            Claims claims =
                    Jwts.parser().setSigningKey(key).parseClaimsJws(originalJWS).getBody();

            return generateJWS(claims.getSubject(), iat, exp);

        }
        catch(ExpiredJwtException e) {
            log.debug("token expired for id : " + e.getClaims().getId());
            return generateJWS(e.getClaims().getSubject(), iat, exp);
        }
        catch (SignatureException e) {
            log.error("jwt is untrusted.");
        }
        catch (Exception e) {
            log.error("wrong jwt.");
        }

        return originalJWS;
    }
}
