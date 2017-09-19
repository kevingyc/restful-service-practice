package rest.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * Created by Kevingyc on 9/13/2017.
 */
public class Member implements Serializable {

    @JsonProperty("ID")
    private long id;

    private String name;

    private String pwd;

    public Member() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public String toString() {
        return "Member: {" +
                "id: " + this.id +
                ", name: " + this.name +
                ", pwd: " + this.pwd +
                "}";
    }
}
