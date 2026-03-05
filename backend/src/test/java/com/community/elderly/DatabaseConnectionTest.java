package com.community.elderly;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ElderlyCarePlatformApplication.class)
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testConnection() throws SQLException {
        System.out.println("========== 开始测试数据库连接 ==========");
        
        assertNotNull(dataSource, "DataSource should not be null");
        
        Connection connection = dataSource.getConnection();
        assertNotNull(connection, "Connection should not be null");
        
        System.out.println("数据库连接成功！");
        System.out.println("数据库 URL: " + connection.getMetaData().getURL());
        System.out.println("数据库用户: " + connection.getMetaData().getUserName());
        System.out.println("数据库产品: " + connection.getMetaData().getDatabaseProductName());
        System.out.println("数据库版本: " + connection.getMetaData().getDatabaseProductVersion());
        
        connection.close();
        System.out.println("========== 数据库连接测试完成 ==========");
    }

    @Test
    public void testQueryTables() {
        System.out.println("========== 开始查询数据库表 ==========");
        
        String sql = "SHOW TABLES";
        List<Map<String, Object>> tables = jdbcTemplate.queryForList(sql);
        
        assertNotNull(tables, "Tables list should not be null");
        assertTrue(tables.size() > 0, "Should have at least one table");
        
        System.out.println("数据库中的表：");
        for (Map<String, Object> table : tables) {
            System.out.println("  - " + table.values().iterator().next());
        }
        
        System.out.println("========== 表查询完成 ==========");
    }

    @Test
    public void testQueryUsers() {
        System.out.println("========== 开始查询用户数据 ==========");
        
        String sql = "SELECT id, username, real_name, role_type, gender, age FROM sys_user";
        List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);
        
        assertNotNull(users, "Users list should not be null");
        assertTrue(users.size() > 0, "Should have at least one user");
        
        System.out.println("用户数据（共 " + users.size() + " 条）：");
        for (Map<String, Object> user : users) {
            System.out.println("  ID: " + user.get("id") + 
                             ", 用户名: " + user.get("username") + 
                             ", 姓名: " + user.get("real_name") + 
                             ", 角色: " + user.get("role_type") + 
                             ", 性别: " + user.get("gender") + 
                             ", 年龄: " + user.get("age"));
        }
        
        System.out.println("========== 用户数据查询完成 ==========");
    }

    @Test
    public void testQueryHealthData() {
        System.out.println("========== 开始查询健康数据 ==========");
        
        String sql = "SELECT id, user_id, heart_rate, systolic_pressure, diastolic_pressure, blood_sugar, temperature, monitor_time FROM health_data LIMIT 5";
        List<Map<String, Object>> healthDataList = jdbcTemplate.queryForList(sql);
        
        assertNotNull(healthDataList, "Health data list should not be null");
        assertTrue(healthDataList.size() > 0, "Should have at least one health data record");
        
        System.out.println("健康数据（前5条）：");
        for (Map<String, Object> healthData : healthDataList) {
            System.out.println("  ID: " + healthData.get("id") + 
                             ", 用户ID: " + healthData.get("user_id") + 
                             ", 心率: " + healthData.get("heart_rate") + 
                             ", 收缩压: " + healthData.get("systolic_pressure") + 
                             ", 舒张压: " + healthData.get("diastolic_pressure") + 
                             ", 血糖: " + healthData.get("blood_sugar") + 
                             ", 体温: " + healthData.get("temperature") + 
                             ", 监测时间: " + healthData.get("monitor_time"));
        }
        
        System.out.println("========== 健康数据查询完成 ==========");
    }

    @Test
    public void testQueryServiceOrders() {
        System.out.println("========== 开始查询服务订单 ==========");
        
        String sql = "SELECT id, user_id, service_type, order_status, is_paid, create_time FROM service_order LIMIT 5";
        List<Map<String, Object>> orders = jdbcTemplate.queryForList(sql);
        
        assertNotNull(orders, "Service orders list should not be null");
        assertTrue(orders.size() > 0, "Should have at least one service order");
        
        System.out.println("服务订单（前5条）：");
        for (Map<String, Object> order : orders) {
            System.out.println("  ID: " + order.get("id") + 
                             ", 用户ID: " + order.get("user_id") + 
                             ", 服务类型: " + order.get("service_type") + 
                             ", 订单状态: " + order.get("order_status") + 
                             ", 是否已支付: " + order.get("is_paid") + 
                             ", 创建时间: " + order.get("create_time"));
        }
        
        System.out.println("========== 服务订单查询完成 ==========");
    }

    @Test
    public void testQueryEmergencyHelp() {
        System.out.println("========== 开始查询紧急求助 ==========");
        
        String sql = "SELECT id, user_id, help_type, urgency, help_status, create_time FROM emergency_help LIMIT 5";
        List<Map<String, Object>> emergencyList = jdbcTemplate.queryForList(sql);
        
        assertNotNull(emergencyList, "Emergency help list should not be null");
        assertTrue(emergencyList.size() > 0, "Should have at least one emergency help record");
        
        System.out.println("紧急求助（前5条）：");
        for (Map<String, Object> emergency : emergencyList) {
            System.out.println("  ID: " + emergency.get("id") + 
                             ", 用户ID: " + emergency.get("user_id") + 
                             ", 求助类型: " + emergency.get("help_type") + 
                             ", 紧急程度: " + emergency.get("urgency") + 
                             ", 求助状态: " + emergency.get("help_status") + 
                             ", 创建时间: " + emergency.get("create_time"));
        }
        
        System.out.println("========== 紧急求助查询完成 ==========");
    }

    @Test
    public void testQueryAnnouncements() {
        System.out.println("========== 开始查询公告 ==========");
        
        String sql = "SELECT id, title, announcement_type, target_role, is_top, create_time FROM sys_announcement LIMIT 5";
        List<Map<String, Object>> announcements = jdbcTemplate.queryForList(sql);
        
        assertNotNull(announcements, "Announcements list should not be null");
        
        if (announcements.size() > 0) {
            System.out.println("公告（前5条）：");
            for (Map<String, Object> announcement : announcements) {
                System.out.println("  ID: " + announcement.get("id") + 
                                 ", 标题: " + announcement.get("title") + 
                                 ", 类型: " + announcement.get("announcement_type") + 
                                 ", 目标角色: " + announcement.get("target_role") + 
                                 ", 是否置顶: " + announcement.get("is_top") + 
                                 ", 创建时间: " + announcement.get("create_time"));
            }
        } else {
            System.out.println("公告表中暂无数据");
        }
        
        System.out.println("========== 公告查询完成 ==========");
    }

    @Test
    public void testAllQueries() throws SQLException {
        System.out.println("========== 开始执行所有数据库查询测试 ==========");

        testConnection();
        testQueryTables();
        testQueryUsers();
        testQueryHealthData();
        testQueryServiceOrders();
        testQueryEmergencyHelp();
        testQueryAnnouncements();

        System.out.println("========== 所有数据库查询测试完成 ==========");
    }
}
