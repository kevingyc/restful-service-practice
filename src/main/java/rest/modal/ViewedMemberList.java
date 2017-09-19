package rest.modal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Kevingyc on 9/12/2017.
 */
@XmlRootElement(name="MemberList")
public class ViewedMemberList {

    @XmlElement
    private List<ViewedMember> members;

    public List<ViewedMember> getMembers() {
        return members;
    }

    public void setMembers(List<ViewedMember> members) {
        this.members = members;
    }
}
