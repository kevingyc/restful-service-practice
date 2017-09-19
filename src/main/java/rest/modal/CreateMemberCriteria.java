package rest.modal;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Kevingyc on 9/14/2017.
 */
@XmlRootElement
public class CreateMemberCriteria {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
