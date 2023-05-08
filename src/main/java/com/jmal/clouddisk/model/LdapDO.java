package com.jmal.clouddisk.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author jmal
 * @Description LdapDO
 * @date 2023/5/8 18:00
 */
@Data
@Document(collection = "ldapConfig")
public class LdapDO {

    /**
     * 服务器host
     */
    String ldapHost;
    /**
     * 端口号
     */
    String port;
    /**
     * 用户组
     */
    String group;
    /**
     * baseDN
     */
    String baseDN;
    /**
     * LDAP服务器中对应个人用户名的字段
     */
    String loginName;

}
