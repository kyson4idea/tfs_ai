package com.smy.tfs.framework.tool;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.smy.tfs.common.constant.HttpStatus;
import org.springframework.beans.factory.annotation.Value;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

/**
 * 用户登陆认证,LDAP跨域认证，通过LDAP对用户进行更新
 *
 * @author smy
 */
public class LdapUtil {
    /**
     * 接口错误码字段名
     */
    public static String RET_CODE = "retCode";

    /**
     * 只要不抛出异常就是验证通过
     */
    public static JSONObject adLogin(String username, String password, String server) {
        String searchFilter = String.format("userprincipalname=%s@smyoa.com", username);
        // 定制返回属性
        String[] returnedAtts = {"description", "mobile", "mail", "samaccountname"};
        JSONObject ret = new JSONObject();

        try {
            NamingEnumeration<SearchResult> entries = search(username, password, searchFilter, returnedAtts, server);
            Attributes attributes = entries.next().getAttributes();

            recordProperties(ret, "chnName", attributes, "description");
            recordProperties(ret, "mobile", attributes, "mobile");
            recordProperties(ret, "email", attributes, "email");
            recordProperties(ret, "accountName", attributes, "samaccountname");
            ret.put(RET_CODE, HttpStatus.SUCCESS);

        } catch (AuthenticationException e) {
            ret.put(RET_CODE, HttpStatus.FORBIDDEN);
        } catch (NamingException e) {
            ret.put(RET_CODE, HttpStatus.ERROR);
        }
        return ret;
    }

    private static NamingEnumeration<SearchResult> search(String userId, String password, String searchFilter, String[] returnedAtts, String server) throws NamingException {
        Hashtable<String, String> env = new Hashtable<String, String>();
        //用户名称，cn,ou,dc 分别：用户，组，域
        env.put(Context.SECURITY_PRINCIPAL, userId.concat("@smyoa.com"));
        //用户密码 cn 的密码
        env.put(Context.SECURITY_CREDENTIALS, password);
        //url 格式：协议://ip:端口/组,域   ,直接连接到域或者组上面
        env.put(Context.PROVIDER_URL, server);
        //LDAP 工厂
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        //验证的类型     "none", "simple", "strong"
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        LdapContext ldapContext = new InitialLdapContext(env, null);
        // 域节点
        String searchBase = "DC=smyoa,DC=com";
        // 搜索控制器
        SearchControls searchCtls = new SearchControls();
        // 创建搜索控制器
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        // 设置返回属性集
        searchCtls.setReturningAttributes(returnedAtts);
        // 根据设置的域节点、过滤器类和搜索控制器搜索LDAP得到结果
        return ldapContext.search(searchBase, searchFilter, searchCtls);
    }

    private static void recordProperties(JSONObject ret, String retKey, Attributes attributes, String attrKey) throws NamingException {
        Attribute description1 = attributes.get(attrKey);
        String chnName = null;
        if (description1 != null) {
            chnName = (String) description1.get();
        }
        ret.put(retKey, chnName);
    }

    /**
     * 获取域账户所有用户的信息
     *
     * @return
     */
    public static JSONArray getAllLdapUserList(String userName, String passWd, String ldapServer) throws Exception {
        final String searchFilter = "(&(objectClass=user)(!(objectClass=computer)))";
        NamingEnumeration<SearchResult> searchResult = search(userName, passWd, searchFilter, null, ldapServer);

        JSONArray result = JSONUtil.createArray();
        while (searchResult.hasMoreElements()){
            Attributes attributes = searchResult.next().getAttributes();

            Attribute attribute = attributes.get("samaccountname");
            if (attribute != null && attribute.get()!=null && attribute.get().toString().length() == 6 ){
                JSONObject ret = new JSONObject();
                recordProperties(ret, "userId", attributes, "samaccountname");
                recordProperties(ret, "userName", attributes, "description");
                recordProperties(ret, "userPhone", attributes, "mobile");
                recordProperties(ret, "userEmail", attributes, "mail");

                result.add(ret);
            }
        }
        return result;
    }
}
