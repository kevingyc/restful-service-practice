package rest.util;

import rest.modal.Member;
import rest.modal.MemberList;
import rest.modal.ViewedMember;
import rest.modal.ViewedMemberList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevingyc on 9/14/2017.
 */
public class MemberConverter {

    public static ViewedMemberList convert(MemberList members) {
        assert members != null;
        assert members.getMembers().size() > 0;

        ViewedMemberList viewedMemberList = new ViewedMemberList();
        List<ViewedMember> viewedMembers = new ArrayList<ViewedMember>();

        for(Member member : members.getMembers()) {
            viewedMembers.add(convert(member));
        }

        viewedMemberList.setMembers(viewedMembers);

        return viewedMemberList;
    }

    public static ViewedMember convert(Member member) {
        assert member.getId() > -1;
        assert member.getName() != null;

        return new ViewedMember(member.getId(), member.getName());
    }
}
