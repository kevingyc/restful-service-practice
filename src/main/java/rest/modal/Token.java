package rest.modal;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by Kevingyc on 9/12/2017.
 */
@XmlRootElement(name = "token")
public class Token implements Serializable {
    private String name;
    private long iat;
    private long exp;
    private String token;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getIat() {
        return iat;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Token(String name) {
        this.name = name;
    }

    public Token(String name, String token) {
        this(name);
        this.token = token;
    }

    @Override
    public String toString() {
        return "Token: {" +
                "name: " + this.name +
                ", iat: " + this.iat +
                ", exp: " + this.exp +
                ", token: " + this.token +
                "}";
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + name.hashCode();
        result = 31 * result + token.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!Token.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Token other = (Token) obj;

        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }

        if ((this.token == null) ? (other.token != null) : !this.token.equals(other.token)) {
            return false;
        }

        return true;
    }
}
