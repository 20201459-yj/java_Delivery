package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import vo.CustomerDTO;
import vo.MenuDTO;
import vo.StoreDTO;

public class EtcDAO {
    String url = "jdbc:mysql://localhost:3306/javadelivery";
    String user = "root";
    String pass = "yoonjae1102@";

    public Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, user, pass);
        return connection;
    }

    public String who(String id, String pw) {
        String sql = "select ac_uid from account where ac_id = ? and ac_pw = ?";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, pw);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ac_uid").substring(0, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isExist(String id) { // 중복 ID 확인하는 메소드
        String sql = "select * from account where ac_id = ?";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public MenuDTO getMenu(int menu_no) {
        String sql = "select * from menu where menu_no = ?";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, menu_no);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MenuDTO menu = new MenuDTO();
                    menu.setMenu_no(menu_no);
                    menu.setMenu_store(rs.getString("menu_store"));
                    menu.setMenu_name(rs.getString("menu_name"));
                    menu.setMenu_price(rs.getInt("menu_price"));

                    return menu;
                }
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
