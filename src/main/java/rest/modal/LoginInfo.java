package rest.modal;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Kevingyc on 9/12/2017.
 */
@XmlRootElement
public class LoginInfo {

    private String name;
    private String pwd;

    public LoginInfo() {}

    public LoginInfo(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
    }

    public String getName() {
        return name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public String toString() {
        return "LoginInfo: {" +
                "name: " + this.name +
                ", pwd: " + this.pwd +
                "}";
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + name.hashCode();
        result = 31 * result + pwd.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!LoginInfo.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final LoginInfo other = (LoginInfo) obj;

        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }

        if ((this.pwd == null) ? (other.pwd != null) : !this.pwd.equals(other.pwd)) {
            return false;
        }

        return true;
    }

}
