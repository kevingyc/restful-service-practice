package rest.modal;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by Kevingyc on 9/12/2017.
 */
@XmlRootElement(name="ViewedMember")
public class ViewedMember implements Serializable {

    private long id;
    private String name;

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

    public ViewedMember(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "ViewedMember: {" +
                "id: " + this.id +
                ", name: " + this.name +
                "}";
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + new Long(id).hashCode();
        result = 31 * result + name.hashCode();
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

        final ViewedMember other = (ViewedMember) obj;

        if (this.id != other.id) {
            return false;
        }

        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }

        return true;
    }
}
