package rest.service;

import io.jsonwebtoken.impl.crypto.MacProvider;
import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import rest.modal.*;
import rest.util.MemberConverter;
import rest.util.PropUtil;
import rest.util.TokenHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by Kevingyc on 9/12/2017.
 */

@Path("rest")
@Produces("application/json")
@Consumes("application/json")
public class RestfulService {

    private static final Logger log = LoggerFactory.getLogger(RestfulService.class);
    private static final String MESSAGE_NAME_NOT_FOUND = "NAME_NOT_FOUND";
    private static final String MESSAGE_NAME_OR_PWD_INCORRECT = "NAME_OR_PWD_INCORRECT";
    private static final String DEFAULT_PWD = PropUtil.getDefaultPassword();
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String FIREBASE_URL = PropUtil.getFirebaseUrl();

    private TokenHandler tokenHandler = new TokenHandler(MacProvider.generateKey());

    @POST
    public Response login(LoginInfo loginInfo) {
        log.debug("name: {}, pwd: {}", loginInfo.getName(), loginInfo.getPwd());

        // get member list
        MemberList memberList = getMemberList();
        List<Member> members = memberList.getMembers();

        boolean hasName = false;
        boolean isPwdCorrect = false;
        for(Member member : members) {
            if(member.getName().equals(loginInfo.getName())) {
                hasName = true;
                if(member.getPwd().equals(loginInfo.getPwd())) {
                    isPwdCorrect = true;
                }
            }
        }

        if(!hasName) {
            return Response.ok(MESSAGE_NAME_NOT_FOUND).build();
        }
        else if(!isPwdCorrect) {
            return Response.ok(MESSAGE_NAME_OR_PWD_INCORRECT).build();
        }

        Token token = tokenHandler.generateToken(loginInfo.getName());

        return Response.ok(token).build();
    }

    @GET
    @Path("/member")
    public Response getMemberList(@Context javax.ws.rs.core.HttpHeaders headers) {
        log.debug("get member list");

        // get member list
        MemberList memberList = getMemberList();
        String extendedJWS = "";

        // check token
        String authenticatedToken = getAuthenticatedToken(headers);
        if (StringUtils.isEmpty(authenticatedToken) || !tokenHandler.isTokenAvailable(authenticatedToken)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        else {
            // token extend
            extendedJWS = tokenHandler.extendJWS(authenticatedToken);
        }

        // convert to viewed member list
        ViewedMemberList viewedMemberList = MemberConverter.convert(memberList);

        if(viewedMemberList.getMembers() == null || viewedMemberList.getMembers().size() == 0) {
            Response.ResponseBuilder rb = Response.ok("no member.");

            return rb.header(HEADER_AUTHORIZATION, extendedJWS).build();
        }

        Response.ResponseBuilder rb = Response.ok(viewedMemberList);

        return rb.header(HEADER_AUTHORIZATION, extendedJWS).build();
    }

    @GET
    @Path("/checkLogin")
    public Response isLogin(@Context javax.ws.rs.core.HttpHeaders headers) {
        log.debug("is login?");

        String extendedJWS = "";

        // check token
        String authenticatedToken = getAuthenticatedToken(headers);
        if (StringUtils.isEmpty(authenticatedToken) || !tokenHandler.isTokenAvailable(authenticatedToken)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        else {
            // token extend
            extendedJWS = tokenHandler.extendJWS(authenticatedToken);
        }

        Response.ResponseBuilder rb = Response.ok();

        return rb.header(HEADER_AUTHORIZATION, extendedJWS).build();
    }

    @POST
    @Path("/member")
    public Response createMember(@Context javax.ws.rs.core.HttpHeaders headers, CreateMemberCriteria criteria) {
        log.debug("create member");

        // get member list
        MemberList memberList = getMemberList();
        List<Member> members = memberList.getMembers();
        String extendedJWS = "";
        // check token
        String authenticatedToken = getAuthenticatedToken(headers);
        if (StringUtils.isEmpty(authenticatedToken) || !tokenHandler.isTokenAvailable(authenticatedToken)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        else {
            // token extend
            extendedJWS = tokenHandler.extendJWS(authenticatedToken);
        }

        // check if user name exist
        if (isUserExist(criteria.getName(), memberList)) {
            Response.ResponseBuilder rb = Response.status(Response.Status.CONFLICT);

            return rb.header(HEADER_AUTHORIZATION, extendedJWS).build();
        }

        // get newest id
        long newestId = getNewestId(members);

        // append new member
        Member newMember = new Member();
        newMember.setId(++newestId);
        newMember.setName(criteria.getName());
        newMember.setPwd(DEFAULT_PWD);
        members.add(newMember);

        // write back to DB
        writeBackToDB(memberList);

        // return state code (success/failed)
        Response.ResponseBuilder rb = Response.ok("success");

        return rb.header(HEADER_AUTHORIZATION, extendedJWS).build();
    }


    private void writeBackToDB(MemberList memberList) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MemberList> httpEntity = new HttpEntity<MemberList>(memberList, httpHeaders);
        restTemplate.exchange(FIREBASE_URL, HttpMethod.PUT, httpEntity, MemberList.class);
    }

    private long getNewestId(List<Member> members) {
        long newestId = 0;
        for(Member member : members) {
            if(member.getId() > newestId) {
                newestId = member.getId();
            }
        }
        return newestId;
    }

    private boolean isUserExist(String name, MemberList memberList) {
        for(Member member : memberList.getMembers()) {
            if(member.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private String getAuthenticatedToken(@Context javax.ws.rs.core.HttpHeaders headers) {
        List<String> tokens = headers.getRequestHeader(HEADER_AUTHORIZATION);

        if(StringUtils.isEmpty(tokens)) {
            return null;
        }

        return tokens.get(0);
    }

    private MemberList getMemberList() {
        RestTemplate restTemplate = new RestTemplate();
        MemberList memberList;
        try {
            memberList = restTemplate.getForObject(FIREBASE_URL, MemberList.class);
        }
        catch (Exception e) {
            memberList = new MemberList();
            memberList.setMembers(new ArrayList<Member>());
        }

        return memberList;
    }
}