package com.smy.tfs.common.utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DB工具类
 *
 * @author smy
 */
@Configuration
public class DbUtil implements InitializingBean {
    /**
     * 将模板中的{}占位符替换为?
     */
    private static final Pattern PATTERN = Pattern.compile("\\{(.+?)\\}");
    private static final Pattern PATTERN2 = Pattern.compile("\\{\\{(.+?)\\}\\}");
    /**
     * 删除可选项，可选项使用[]标识
     */
    private static final Pattern PATTERN3 = Pattern.compile("\\[(.+?)\\]");
    /**
     * 数据源
     */
    private static DataSource ds = null;
    /**
     * 通用数据操作的配置，初始化时加暂，用户修改后刷新
     */
    private static JSONObject commDataConfig = null;
    private static Logger logger = LoggerFactory.getLogger(DbUtil.class);
    /**
     * 数据库url
     */
    @Value("${spring.datasource.druid.master.url}")
    private String url;
    /**
     * 数据库用户名
     */
    @Value("${spring.datasource.druid.master.username}")
    private String username;
    /**
     * 数据库密码
     */
    @Value("${spring.datasource.druid.master.password}")
    private String password;

    public static void dbExecute(String sql, Object... params) throws Exception {
        log(sql, params);
        JdbcTemplate template = new JdbcTemplate(ds);
        template.execute(sql);
    }

    public static int dbUpdate(String sql, Object... params) {
        log(sql, params);
        JdbcTemplate template = new JdbcTemplate(ds);
        return template.update(sql, params);
    }

    public static String dbQuery(String sql, Object... params) {
        log(sql, params);
        JdbcTemplate template = new JdbcTemplate(ds);
        return template.queryForObject(sql, String.class, params);
    }

    public static int dbQueryInt(String sql, Object... params) {
        log(sql, params);
        JdbcTemplate template = new JdbcTemplate(ds);
        return template.queryForObject(sql, Integer.class, params);
    }

    public static List<Map<String, Object>> dbQueryList(String sql, Object... params) {
        log(sql, params);
        JdbcTemplate template = new JdbcTemplate(ds);
        return template.queryForList(sql, params);
    }

    public static JSONArray dbQueryJson(String sql, Object... params) {
        log(sql, params);
        JdbcTemplate template = new JdbcTemplate(ds);
        List<Map<String, Object>> rows = template.queryForList(sql, params);
        JSONArray ret = new JSONArray();

        for (int i = 0; i < rows.size(); ++i) {
            Map<String, Object> row = rows.get(i);
            JSONObject obj = new JSONObject();
            for (String key : row.keySet()) {
                obj.put(key, row.get(key));
            }
            ret.add(obj);
        }
        return ret;
    }

    private static JSONObject loadCommDataConfig() {
        JSONArray rows = null;
        try {
            rows = dbQueryJson("SELECT * FROM sys_common_data");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject ret = new JSONObject();
        for (int i = 0; i < rows.size(); ++i) {
            JSONObject obj = rows.getJSONObject(i);
            ret.put(obj.getString("data_key") + "_" + obj.getString("op_type"), obj);
            obj.remove("data_key");
            obj.remove("op_type");
        }
        return ret;
    }

    public static void updateCommDataConfig() {
        commDataConfig = loadCommDataConfig();
    }

    public static JSONObject generateSql(String dataKey, String opType, JSONObject param) throws Exception {
        // 加载配置信息
        if (null == commDataConfig) {
            commDataConfig = loadCommDataConfig();
        }

        // 配置信息检查
        JSONObject obj = commDataConfig.getJSONObject(dataKey + "_" + opType);
        if (null == obj) {
            return null;
        }

        // 权限校验
        String opRole = obj.getString("op_role");
        if (null != opRole && !"[]".equals(opRole)) {
            Long[] userRole = SecurityUtils.getLoginUser().getUser().getRoleIds();
            if (!hasDuplicateElements(userRole, JSONArray.parseArray(opRole))) {
                return null;
            }
        }

        String sql = obj.getString("sql");
        if (null == sql || "".equals(sql)) {
            return generateAutoSql(dataKey, opType, param);
        }

        JSONObject ret = new JSONObject();
        // sql执行的参数
        List<Object> sqlParam = new ArrayList<Object>();

        // 将模板中的{{}}占位符替换为字符串，处理 in、like 这样的语法
        Matcher matcher2 = PATTERN2.matcher(sql);
        while (matcher2.find()) {
            String key = matcher2.group(1);
            String value = param.getString(key);
            if (value != null) {
                sql = sql.replace("{{" + key + "}}", value);
            }
        }

        // 将模板中的{}占位符替换为?
        Matcher matcher = PATTERN.matcher(sql);
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = null;

            if ("sys.user_name".equals(key)) {
                value = SecurityUtils.getAccountUserInfo();
            } else if ("sys.user_deptID".equals(key)) {
                value = SecurityUtils.getDeptId();
            } else {
                value = param.get(key);
                if (value instanceof JSONArray) {
                    value = ((JSONArray) value).toJSONString();
                }
            }
            if (value != null || "ADD".equals(opType)) {
                sql = sql.replace("{" + key + "}", "?");
                sqlParam.add(value);
            }
        }

        // 删除可选项，可选项使用[]标识
        Matcher matcher3 = PATTERN3.matcher(sql);
        while (matcher3.find()) {
            // 键名
            String key = matcher3.group(1);
            if (key.indexOf('{') >= 0) {
                sql = sql.replace("[" + key + "]", "");
            } else {
                sql = sql.replace("[" + key + "]", key);
            }
        }

        //判断是否有没有替换掉的参数
        Matcher matcher4 = PATTERN.matcher(sql);
        if (matcher4.find()) {
            throw new Exception("数据操作缺少必选参数，请联系管理员检查");
        }

        //返回结果
        ret.put("sql", sql);
        ret.put("param", sqlParam);
        return ret;
    }

    public static DataSource getDataSource() {
        return ds;
    }

    private static boolean hasDuplicateElements(Long[] array1, JSONArray array2) {
        // 使用 HashSet 来存储数组元素，自动去除重复元素
        Set<Long> set1 = new HashSet<>();
        Set<Long> set2 = new HashSet<>();

        // 将数组1的元素添加到 set1
        for (Long l : array1) {
            set1.add(l);
        }

        // 将数组2的元素添加到 set2
        for (int i = 0; i < array2.size(); i++) {
            set2.add(array2.getLong(i));
        }

        // 检查两个 set 是否有相同的元素
        set1.retainAll(set2);
        return set1.size() > 0;
    }

    /**
     * 用户未配置SQL的情况，根据用户的操作生成SQL语句
     * <p>
     * 如果想更灵活需要查询表元信息，但会牺牲一点性能：
     * - SHOW COLUMNS FROM your_database_name.your_table_name
     * - SELECT * FROM information_schema.columns WHERE table_schema = 'your_database_name' AND table_name = 'your_table_name'
     */
    private static JSONObject generateAutoSql(String dataKey, String opType, JSONObject param) throws Exception {
        if ("LIST".equals(opType)) {
            return generateListSql(dataKey, param);
        }
        if ("UPDATE".equals(opType)) {
            return generateUpdateSql(dataKey, param);
        }
        if ("ADD".equals(opType)) {
            return generateAddSql(dataKey, param);
        }
        if ("DELETE".equals(opType)) {
            return generateDeleteSql(dataKey, param);
        }
        return null;
    }

    private static JSONObject generateListSql(String dataKey, JSONObject param) {
        JSONObject ret = new JSONObject();
        List<Object> sqlParam = new ArrayList<Object>();

        String tableName = getExecuteAutoSqlTableName(dataKey, "LIST");
        Map<String, Boolean> fieldsExist = getAutoSqlTableFieldsExist(tableName, new String[]{"create_time", "update_time"});
        String sql = "SELECT * FROM " + tableName + " WHERE 1=1";
        for (String key : param.keySet()) {
            if ("pageSize".equals(key) || "pageNum".equals(key)) {
                continue;
            }

            String value = param.getString(key);
            Boolean isCreateTimeKey = key.startsWith("create_time") && fieldsExist.get("create_time") != null && fieldsExist.get("create_time");
            Boolean isUpdateTimeKey = key.startsWith("update_time") && fieldsExist.get("update_time") != null && fieldsExist.get("update_time");
            Boolean isRangeLabel = key.endsWith("_begin") || key.endsWith("_end");
            Boolean isRangeDateTime = (isCreateTimeKey || isUpdateTimeKey) && isRangeLabel;
            if (isRangeDateTime) {
                // [AND create_time >= {create_time_begin}] [AND create_time <= {create_time_end}]
                sql += (" AND " + (isCreateTimeKey ? "create_time" : "update_time") + " " + (key.endsWith("_begin") ? ">=" : "<=") + " '" + value + "'");
            } else {
                // 统一支持模糊查询 AND product_name like '%product_name%'
                sql += (" AND " + key + " like '%" + value + "%'");
            }
        }
        String priKey = getExecuteSqlPriKey(dataKey, "LIST");
        sql += " ORDER BY " + priKey + " DESC";

        ret.put("sql", sql);
        ret.put("param", sqlParam);
        return ret;
    }

    private static JSONObject generateUpdateSql(String dataKey, JSONObject param) {
        JSONObject ret = new JSONObject();
        List<Object> sqlParam = new ArrayList<Object>();

        String tableName = getExecuteAutoSqlTableName(dataKey, "UPDATE");
        Map<String, Boolean> fieldsExist = getAutoSqlTableFieldsExist(tableName, new String[]{"update_by"});
        if (fieldsExist.get("update_by")) {
            param.put("update_by", SecurityUtils.getAccountUserInfo());
        }
        String sql = "UPDATE " + tableName + " SET";
        for (String key : param.keySet()) {
            sql += ("," + key + "=?");
            sqlParam.add(param.get(key));
        }
        String priKey = getExecuteSqlPriKey(dataKey, "UPDATE");
        sql = sql.replace("SET,", "SET ");
        sql += " WHERE " + priKey + "=?";
        sqlParam.add(param.get(priKey));

        ret.put("sql", sql);
        ret.put("param", sqlParam);
        return ret;
    }

    private static JSONObject generateAddSql(String dataKey, JSONObject param) {
        JSONObject ret = new JSONObject();
        List<Object> sqlParam = new ArrayList<Object>();

        String tableName = getExecuteAutoSqlTableName(dataKey, "ADD");
        Map<String, Boolean> fieldsExist = getAutoSqlTableFieldsExist(tableName, new String[]{"create_by"});
        if (fieldsExist.get("create_by") != null && fieldsExist.get("create_by")) {
            param.put("create_by", SecurityUtils.getAccountUserInfo());
        }
        String sql = "INSERT INTO " + tableName + "(";
        String addTail = ") VALUES(";
        for (String key : param.keySet()) {
            sql += ("," + key);
            addTail += ",?";
            sqlParam.add(param.get(key));
        }
        sql += (addTail + ")");
        sql = sql.replace("(,", "(");

        ret.put("sql", sql);
        ret.put("param", sqlParam);
        return ret;
    }

    private static JSONObject generateDeleteSql(String dataKey, JSONObject param) {
        JSONObject ret = new JSONObject();
        List<Object> sqlParam = new ArrayList<Object>();
        String priKey = getExecuteSqlPriKey(dataKey, "DELETE");
        String tableName = getExecuteAutoSqlTableName(dataKey, "DELETE");

        String ids = param.getString(priKey);
        String sql = "DELETE FROM " + tableName + " WHERE " + priKey + " in (" + ids + ")";

        ret.put("sql", sql);
        ret.put("param", sqlParam);
        return ret;
    }

    /**
     * 获取通用接口SQL对应的操作单表唯一键字段名
     */
    public static String getExecuteSqlPriKey(String dataKey, String opType) {
        String commonDataKey = dataKey + "_" + opType;
        JSONObject sqlObj = commDataConfig.getJSONObject(commonDataKey);

        String priKey = sqlObj.getString("pri_key");
        if (priKey == null || "".equals(priKey)) {
            priKey = "id";
        }
        return priKey;
    }

    /**
     * 获取通用接口自动化SQL对应的操作单表名
     */
    public static String getExecuteAutoSqlTableName(String dataKey, String opType) {
        String commonDataKey = dataKey + "_" + opType;
        JSONObject sqlObj = commDataConfig.getJSONObject(commonDataKey);

        String autoSqlTable = sqlObj.getString("auto_sql_table");
        if (autoSqlTable == null || "".equals(autoSqlTable)) {
            autoSqlTable = dataKey;
        }
        return autoSqlTable;
    }

    /**
     * 获取通用接口自动化sql对应表某些字段是否存在
     * <p>
     * 也可以查询表元信息：SHOW COLUMNS FROM your_database_name.your_table_name;
     */
    public static Map<String, Boolean> getAutoSqlTableFieldsExist(String tableName, String[] fieldNames) {
        JSONArray rows = null;
        StringJoiner sj = new StringJoiner(",");
        for (String fieldName : fieldNames) {
            sj.add("'" + fieldName + "'");
        }
        String fieldNameStr = sj.toString();
        String sql = "SELECT column_name FROM information_schema.columns WHERE table_name = '" + tableName + "' AND COLUMN_NAME in (" + fieldNameStr + ");";
        try {
            rows = dbQueryJson(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Boolean> fieldsExist = new HashMap<String, Boolean>();
        for (int i = 0; i < rows.size(); ++i) {
            JSONObject obj = rows.getJSONObject(i);
            fieldsExist.put(obj.getString("COLUMN_NAME"), true);
        }
        return fieldsExist;
    }

    private static void log(String sql, Object[] params) {
        logger.debug(sql);
        String paramLog = "";
        for (int i = 0; i < params.length; ++i) {
            if (paramLog.length() == 0) {
                paramLog += params[i].toString();
            } else {
                paramLog += ("," + params[i]);
            }
        }
        logger.debug(paramLog);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initialDataSource();
    }

    /**
     * 初始化数据源
     */
    private void initialDataSource() {
        Properties pro = new Properties();
        pro.setProperty("url", url);
        pro.setProperty("username", username);
        pro.setProperty("password", password);
        try {
            ds = DruidDataSourceFactory.createDataSource(pro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
