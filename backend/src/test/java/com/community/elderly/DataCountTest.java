
package com.community.elderly;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@SpringBootTest(classes = ElderlyCarePlatformApplication.class)
public class DataCountTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testAllStatisticsQueries() {
        System.out.println("========== 开始执行所有统计查询测试 ==========");

        // 1. 查询总老人数
        System.out.println("\n1. 查询总老人数:");
        String sql1 = "SELECT COUNT(*) FROM sys_user WHERE role_type = 'elderly' AND is_deleted = 0";
        List&lt;Map&lt;String, Object&gt;&gt; result1 = jdbcTemplate.queryForList(sql1);
        System.out.println("   结果: " + result1.get(0).values().iterator().next());

        // 2. 查询家属总数
        System.out.println("\n2. 查询家属总数:");
        String sql2 = "SELECT COUNT(*) FROM sys_user WHERE role_type = 'family' AND is_deleted = 0";
        List&lt;Map&lt;String, Object&gt;&gt; result2 = jdbcTemplate.queryForList(sql2);
        System.out.println("   结果: " + result2.get(0).values().iterator().next());

        // 3. 查询护工总数
        System.out.println("\n3. 查询护工总数:");
        String sql3 = "SELECT COUNT(*) FROM sys_user WHERE role_type = 'caregiver' AND is_deleted = 0";
        List&lt;Map&lt;String, Object&gt;&gt; result3 = jdbcTemplate.queryForList(sql3);
        System.out.println("   结果: " + result3.get(0).values().iterator().next());

        // 4. 查询2026年1月的服务订单数
        System.out.println("\n4. 查询2026年1月的服务订单数:");
        String sql4 = "SELECT COUNT(*) FROM service_order WHERE is_deleted = 0 AND create_time BETWEEN '2026-01-01 00:00:00' AND '2026-01-31 23:59:59'";
        List&lt;Map&lt;String, Object&gt;&gt; result4 = jdbcTemplate.queryForList(sql4);
        System.out.println("   结果: " + result4.get(0).values().iterator().next());

        // 5. 查询2026年1月的紧急求助数
        System.out.println("\n5. 查询2026年1月的紧急求助数:");
        String sql5 = "SELECT COUNT(*) FROM emergency_help WHERE is_deleted = 0 AND create_time BETWEEN '2026-01-01 00:00:00' AND '2026-01-31 23:59:59'";
        List&lt;Map&lt;String, Object&gt;&gt; result5 = jdbcTemplate.queryForList(sql5);
        System.out.println("   结果: " + result5.get(0).values().iterator().next());

        // 6. 查询2026年1月的异常健康数据数
        System.out.println("\n6. 查询2026年1月的异常健康数据数:");
        String sql6 = "SELECT COUNT(*) FROM health_data WHERE health_status = 'abnormal' AND is_deleted = 0 AND monitor_time BETWEEN '2026-01-01 00:00:00' AND '2026-01-31 23:59:59'";
        List&lt;Map&lt;String, Object&gt;&gt; result6 = jdbcTemplate.queryForList(sql6);
        System.out.println("   结果: " + result6.get(0).values().iterator().next());

        // 7. 查询有健康数据的老人数（2026年1月）
        System.out.println("\n7. 查询2026年1月有健康数据的老人数:");
        String sql7 = "SELECT COUNT(DISTINCT user_id) FROM health_data WHERE is_deleted = 0 AND monitor_time BETWEEN '2026-01-01 00:00:00' AND '2026-01-31 23:59:59'";
        List&lt;Map&lt;String, Object&gt;&gt; result7 = jdbcTemplate.queryForList(sql7);
        System.out.println("   结果: " + result7.get(0).values().iterator().next());

        // 8. 查询已核销的服务订单数（2026年1月）
        System.out.println("\n8. 查询2026年1月已核销的服务订单数:");
        String sql8 = "SELECT COUNT(*) FROM service_verification sv INNER JOIN service_order so ON sv.order_id = so.id WHERE sv.verification_status = 'verified' AND so.is_deleted = 0 AND so.create_time BETWEEN '2026-01-01 00:00:00' AND '2026-01-31 23:59:59'";
        List&lt;Map&lt;String, Object&gt;&gt; result8 = jdbcTemplate.queryForList(sql8);
        System.out.println("   结果: " + result8.get(0).values().iterator().next());

        // 9. 查询已响应的紧急求助数（2026年1月）
        System.out.println("\n9. 查询2026年1月已响应的紧急求助数:");
        String sql9 = "SELECT COUNT(*) FROM emergency_help WHERE help_status IN ('handling', 'completed') AND is_deleted = 0 AND create_time BETWEEN '2026-01-01 00:00:00' AND '2026-01-31 23:59:59'";
        List&lt;Map&lt;String, Object&gt;&gt; result9 = jdbcTemplate.queryForList(sql9);
        System.out.println("   结果: " + result9.get(0).values().iterator().next());

        // 10. 查询服务评价平均分（2026年1月）
        System.out.println("\n10. 查询2026年1月的服务评价平均分:");
        String sql10 = "SELECT AVG(rating) FROM service_evaluation WHERE is_deleted = 0 AND create_time BETWEEN '2026-01-01 00:00:00' AND '2026-01-31 23:59:59'";
        List&lt;Map&lt;String, Object&gt;&gt; result10 = jdbcTemplate.queryForList(sql10);
        System.out.println("   结果: " + result10.get(0).values().iterator().next());

        System.out.println("\n========== 所有统计查询测试完成 ==========");
    }
}

