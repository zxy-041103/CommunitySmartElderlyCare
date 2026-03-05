
import java.sql.*;

public class CheckDatabaseData {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/smart_elderly_care?useUnicode=true&amp;characterEncoding=utf8&amp;serverTimezone=Asia/Shanghai&amp;useSSL=false&amp;allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "weilaikeqi999";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("========== 数据库连接成功 ==========");

            // 1. 查询总老人数
            System.out.println("\n1. 查询总老人数:");
            String sql1 = "SELECT COUNT(*) FROM sys_user WHERE role_type = 'elderly' AND is_deleted = 0";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql1)) {
                if (rs.next()) {
                    System.out.println("   结果: " + rs.getInt(1));
                }
            }

            // 2. 查询2026年1月的服务订单数
            System.out.println("\n2. 查询2026年1月的服务订单数:");
            String sql2 = "SELECT COUNT(*) FROM service_order WHERE is_deleted = 0 AND create_time BETWEEN '2026-01-01 00:00:00' AND '2026-01-31 23:59:59'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql2)) {
                if (rs.next()) {
                    System.out.println("   结果: " + rs.getInt(1));
                }
            }

            // 3. 查询2026年1月的紧急求助数
            System.out.println("\n3. 查询2026年1月的紧急求助数:");
            String sql3 = "SELECT COUNT(*) FROM emergency_help WHERE is_deleted = 0 AND create_time BETWEEN '2026-01-01 00:00:00' AND '2026-01-31 23:59:59'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql3)) {
                if (rs.next()) {
                    System.out.println("   结果: " + rs.getInt(1));
                }
            }

            // 4. 查询2026年1月的异常健康数据数
            System.out.println("\n4. 查询2026年1月的异常健康数据数:");
            String sql4 = "SELECT COUNT(*) FROM health_data WHERE health_status = 'abnormal' AND is_deleted = 0 AND monitor_time BETWEEN '2026-01-01 00:00:00' AND '2026-01-31 23:59:59'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql4)) {
                if (rs.next()) {
                    System.out.println("   结果: " + rs.getInt(1));
                }
            }

            // 5. 查询家属总数
            System.out.println("\n5. 查询家属总数:");
            String sql5 = "SELECT COUNT(*) FROM sys_user WHERE role_type = 'family' AND is_deleted = 0";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql5)) {
                if (rs.next()) {
                    System.out.println("   结果: " + rs.getInt(1));
                }
            }

            // 6. 查询护工总数
            System.out.println("\n6. 查询护工总数:");
            String sql6 = "SELECT COUNT(*) FROM sys_user WHERE role_type = 'caregiver' AND is_deleted = 0";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql6)) {
                if (rs.next()) {
                    System.out.println("   结果: " + rs.getInt(1));
                }
            }

            // 7. 查看服务订单的详细信息
            System.out.println("\n7. 服务订单详细信息（前10条）:");
            String sql7 = "SELECT id, order_no, user_id, service_type, create_time FROM service_order ORDER BY id LIMIT 10";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql7)) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();
                while (rs.next()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i <= columnCount; i++) {
                        if (i > 1) sb.append(", ");
                        sb.append(meta.getColumnName(i)).append(": ").append(rs.getString(i));
                    }
                    System.out.println("   " + sb.toString());
                }
            }

            // 8. 查看sys_user表的所有角色
            System.out.println("\n8. sys_user表的所有角色分布:");
            String sql8 = "SELECT role_type, COUNT(*) as count FROM sys_user WHERE is_deleted = 0 GROUP BY role_type";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql8)) {
                while (rs.next()) {
                    System.out.println("   " + rs.getString("role_type") + ": " + rs.getInt("count"));
                }
            }

        } catch (SQLException e) {
            System.out.println("数据库操作失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

