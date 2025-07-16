package com.smy.tfs.framework.interceptor.impl;

import cn.hutool.core.util.ReflectUtil;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.sql.Connection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DeleteTimeInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String originalSql = boundSql.getSql();
        String tableName = extractTableNames(originalSql);
        //过滤名字不是以ticket_开头的表
        if (ObjectHelper.isEmpty(tableName) || !tableName.startsWith("ticket_")) {
            // 继续执行
            return invocation.proceed();
        }
        Statement statement = CCJSqlParserUtil.parse(originalSql);
        if (!(statement instanceof Select) && !(statement instanceof Update)) {
            // 继续执行
            return invocation.proceed();
        }
        // 这里原有的sql语句添加WHERE条件(delete_time is null),并返回新的sql语句;如果本身已经有这个条件,则返回原有的sql语句.
        String newSql = addWhere(originalSql,statement);
        ReflectUtil.setFieldValue(boundSql, "sql", newSql);
        // 继续执行
        return invocation.proceed();
    }

    private String addWhere(String originalSql, Statement statement) throws JSQLParserException {
        String newSql = originalSql;
        String deleteTimeSql = " delete_time is null";
        // 使用 JSqlParser 解析 SQL，并拼接 WHERE 条件
        if (statement instanceof Select) {
            Select select = (Select) statement;
            if (ObjectHelper.isEmpty(select.getSelectBody())) return newSql;
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
            Expression where = plainSelect.getWhere();
            if (ObjectHelper.isEmpty(where)) {
                plainSelect.setWhere(CCJSqlParserUtil.parseCondExpression(deleteTimeSql));
            } else {
                if (isIncludeDeleteTime(where.toString())) return newSql;
                plainSelect.setWhere(new AndExpression(where, CCJSqlParserUtil.parseCondExpression(deleteTimeSql)));
            }
            newSql = plainSelect.toString();
        } else if (statement instanceof Update) {
            Update update = (Update) statement;
            Expression where = update.getWhere();
            if (ObjectHelper.isEmpty(where)) {
                update.setWhere(CCJSqlParserUtil.parseCondExpression(deleteTimeSql));
            } else {
                if (isIncludeDeleteTime(where.toString())) return newSql;
                update.setWhere(new AndExpression(where, CCJSqlParserUtil.parseCondExpression(deleteTimeSql)));
            }
            newSql = update.toString();
        }
        return newSql;
    }

    private Boolean isIncludeDeleteTime(String whereStr) {
        Pattern pattern = Pattern.compile("\\s*delete_time\\s+IS\\s+NULL", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(whereStr);
        if (matcher.find()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static void main(String[] args) throws JSQLParserException {


    }


    /**
     * 根据sql提取select和update类型的表名
     *
     * @param sql
     * @return
     */
    private String extractTableNames(String sql) {
        String tableName = StringUtils.EMPTY;
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            if (statement instanceof Select) {
                Select select = (Select) statement;
                tableName = extractTableNamesFromPlainSelect(select);
            } else if (statement instanceof Update) {
                Update update = (Update) statement;
                tableName = extractTableNamesFromUpdate(update);
            }
        } catch (JSQLParserException e) {
            // 无法解析的SQL语句，忽略或记录日志
            log.error(e.getMessage());
        }
        return tableName;
    }

    private String extractTableNamesFromPlainSelect(Select select) {
        if (ObjectHelper.isEmpty(select.getSelectBody())) return null;
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        FromItem fromItem = plainSelect.getFromItem();
        if (ObjectHelper.isNotEmpty(fromItem)) {
            if (fromItem instanceof Table) {
                Table table = (Table) fromItem;
                log.info("Table Name: " + table.getName());
                return table.getName();
            } else {
                log.info("The FROM clause does not contain a simple table reference.");
            }
        }
        return null;
    }

    private String extractTableNamesFromUpdate(Update update) {
        if (ObjectHelper.isNotEmpty(update.getTable())) {
            return update.getTable().getName();
        }
        return null;
    }

}
