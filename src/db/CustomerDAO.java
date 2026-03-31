package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.x.protobuf.MysqlxCrud.Order;

import vo.CustomerDTO;
import vo.OrdersDTO;
import vo.StoreDTO;
import vo.MenuDTO;

public class CustomerDAO {
    String url = "jdbc:mysql://localhost:3306/javadelivery";
    String user = "root";
    String pass = "yoonjae1102@";

    public Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, user, pass);
        return connection;
    }

    public String newUid() {
        String genUid = null;
        String sql = "select max(cast(substring(cs_uid, 2) as unsigned)) as curruid from customer";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String currid = rs.getString("curruid");
                    if (currid == null) {
                        return "C1";
                    } else {
                        return "C" + (Integer.parseInt(currid) + 1);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "C1";
    }

    public CustomerDTO getCustomer(String cs_uid) {
        String sql = "select * from customer where cs_uid = ?";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, cs_uid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CustomerDTO customer = new CustomerDTO();
                    customer.setCs_uid(cs_uid);
                    customer.setCs_name(rs.getString("cs_name"));
                    customer.setCs_address(rs.getString("cs_address"));
                    customer.setCs_phone(rs.getString("cs_phone"));
                    customer.setCs_balance(rs.getInt("cs_balance"));

                    return customer;
                }
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public boolean register(String id, String pw, CustomerDTO customer) {
        String sql = "insert into account values(?, ?, ?)";
        String newUid = newUid();
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            PreparedStatement psAC = connection.prepareStatement(sql);
            psAC.setString(1, id);
            psAC.setString(2, pw);
            psAC.setString(3, newUid);
            psAC.executeUpdate();

            sql = "insert into customer values(?, ?, ?, ?, ?)";
            PreparedStatement psCS = connection.prepareStatement(sql);
            psCS.setString(1, newUid);
            psCS.setString(2, customer.getCs_name());
            psCS.setString(3, customer.getCs_address());
            psCS.setString(4, customer.getCs_phone());
            psCS.setInt(5, 0);
            psCS.executeUpdate();

            connection.commit();
            return true;
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
            return false;
        }
    }

    public CustomerDTO login(String id, String pw) {
        CustomerDTO loginCustomer = new CustomerDTO();
        String sql = "select * from account join customer on cs_uid = ac_uid" +
                " where ac_id = ? and ac_pw = ?";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, pw);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    loginCustomer.setCs_uid(rs.getString("ac_uid"));
                    loginCustomer.setCs_name(rs.getString("cs_name"));
                    loginCustomer.setCs_address(rs.getString("cs_address"));
                    loginCustomer.setCs_phone(rs.getString("cs_phone"));
                    loginCustomer.setCs_balance(rs.getInt("cs_balance"));
                }

                return loginCustomer;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<StoreDTO> selectStore(String type) {
        List<StoreDTO> list = new ArrayList<StoreDTO>();
        String sql = "select * from store";

        switch (type) {
            case "한식":
                sql += " where st_type = '한식'";
                break;
            case "중식":
                sql += " where st_type = '중식'";
                break;
            case "일식":
                sql += " where st_type = '일식'";
                break;
            case "양식":
                sql += " where st_type = '양식'";
                break;
            case "카페":
                sql += " where st_type = '카페'";
                break;
            default:
                break;
        }

        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StoreDTO store = new StoreDTO();
                    store.setSt_uid(rs.getString("st_uid"));
                    store.setSt_name(rs.getString("st_name"));
                    store.setSt_address(rs.getString("st_address"));
                    store.setSt_phone(rs.getString("st_phone"));
                    store.setSt_type(rs.getString("st_type"));
                    store.setSt_profit(rs.getInt("st_profit"));

                    list.add(store);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 포인트 충전
    public boolean chargePoint(CustomerDTO customer, int amount) {
        String sql = "UPDATE customer SET cs_balance = cs_balance + ? WHERE cs_uid = ?";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setString(2, customer.getCs_uid());
            if (ps.executeUpdate() > 0) {
                customer.setCs_balance(customer.getCs_balance() + amount);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 포인트 사용 (결제 시 차감)
    public boolean usePoint(CustomerDTO customer, int amount) {
        String sql = "UPDATE customer SET cs_balance = cs_balance - ? WHERE cs_uid = ? AND cs_balance >= ?";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setString(2, customer.getCs_uid());
            ps.setInt(3, amount); // 잔액이 차감액보다 커야 함
            if (ps.executeUpdate() > 0) {
                customer.setCs_balance(customer.getCs_balance() - amount);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 리턴값 : -1 -> DB, JAVA 오류
    // 리턴값 : -2 -> 잔액부족
    // 리턴값 : 1 -> 주문 성공
    public int shootOrder(OrdersDTO order, CustomerDTO loginCustomer) {// 사용자가 주문을 쏨
        String sql = "select menu_price from menu where menu_no = ?";
        int price = 0;
        int balance = loginCustomer.getCs_balance();
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, order.getOd_menu());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    price = rs.getInt("menu_price");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        if (price > balance) {
            return -2;
        }

        sql = "insert into orders(od_customer, od_store, od_content, od_menu) values(?, ?, ?, ?)";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, order.getOd_customer());
            ps.setString(2, order.getOd_store());
            ps.setString(3, order.getOd_content());
            ps.setInt(4, order.getOd_menu());
            if (ps.executeUpdate() > 0) {
                usePoint(loginCustomer, price);
                loginCustomer.setCs_balance(balance - price);
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    public boolean cancelOrder(OrdersDTO order) {
        String sql = "select menu_price from menu where menu_no = ?";
        int price = 0;
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, order.getOd_menu());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    price = rs.getInt("menu_price");
                }
            } catch (Exception e) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        sql = "delete from orders where od_no = ?";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, order.getOd_no());
            if (ps.executeUpdate() > 0) {
                chargePoint(getCustomer(order.getOd_customer()), price);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public List<OrdersDTO> myOrder(int state, CustomerDTO loginCustomer) { // state에 따른 분류 보여줌
        List<OrdersDTO> list = new ArrayList<OrdersDTO>();
        String sql = "select * from orders where od_customer = ? and od_state = ?";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, loginCustomer.getCs_uid());
            ps.setInt(2, state);
            try (ResultSet rs = ps.executeQuery()) {
                OrdersDTO order = new OrdersDTO();
                order.setOd_no(rs.getInt("od_no"));
                order.setOd_customer(rs.getString("od_customer"));
                order.setOd_store(rs.getString("od_store"));
                order.setOd_content(rs.getString("od_content"));
                order.setOd_menu(rs.getInt("od_menu"));
                order.setOd_state(rs.getInt("od_state"));
                order.setOd_start(rs.getObject("od_start", LocalDateTime.class));
                order.setOd_duration(rs.getInt("od_duration"));

                list.add(order);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<MenuDTO> getMenus(String storeUid) {
        List<MenuDTO> list = new ArrayList<>();
        // menu 테이블에서 특정 가게(menu_store)의 모든 데이터를 조회하는 SQL
        String sql = "SELECT * FROM menu WHERE menu_store = ?";

        try (Connection conn = getConnection(); // 기존에 정의된 getConnection 사용
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, storeUid);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MenuDTO menu = new MenuDTO();
                    menu.setMenu_no(rs.getInt("menu_no"));
                    menu.setMenu_store(storeUid);
                    menu.setMenu_name(rs.getString("menu_name"));
                    menu.setMenu_price(rs.getInt("menu_price"));

                    list.add(menu);
                }
            }
        } catch (Exception e) {
            // 예외 발생 시 스택 트레이스 출력
            e.printStackTrace();
        }
        return list;
    }

    // 수락된 주문 목록과 소요시간 가져오기
    public List<MenuDTO> acceptMenu(int state, String st_uid) {
        List<MenuDTO> list = new ArrayList<>();
        String sql = "select * "
                + "from orders "
                + "join menu "
                + "on menu_no = od_menu "
                + "where od_state = ? and od_customer = ?"; // 수락된 주문만 받기 위함
        try (Connection connection = getConnection();
                PreparedStatement pStatement = connection.prepareStatement(sql);) {
            pStatement.setInt(1, state);
            pStatement.setString(2, st_uid);
            try (ResultSet rs = pStatement.executeQuery();) {
                while (rs.next()) {
                    MenuDTO menu = new MenuDTO();
                    menu.setMenu_no(rs.getInt("menu_no"));
                    menu.setMenu_store(rs.getString("menu_store"));
                    menu.setMenu_name(rs.getString("menu_name"));
                    menu.setMenu_price(rs.getInt("menu_price"));
                    menu.setOd_duration(rs.getInt("od_duration"));
                    list.add(menu);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 회원탈퇴
    public boolean deleteCustomer(String cs_uid) {
        String sqlReview = "DELETE FROM review WHERE rv_customer = ?";
        String sqlOrders = "DELETE FROM orders WHERE od_customer = ?";
        String sqlAccount = "DELETE FROM account WHERE ac_uid = ?";
        String sqlCustomer = "DELETE FROM customer WHERE cs_uid = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // 트랜잭션 시작

            try (
                    PreparedStatement psReview = conn.prepareStatement(sqlReview);
                    PreparedStatement psOrders = conn.prepareStatement(sqlOrders);
                    PreparedStatement psAccount = conn.prepareStatement(sqlAccount);
                    PreparedStatement psCustomer = conn.prepareStatement(sqlCustomer)) {
                // 1. 리뷰 삭제
                psReview.setString(1, cs_uid);
                psReview.executeUpdate();

                // 2. 주문 삭제
                psOrders.setString(1, cs_uid);
                psOrders.executeUpdate();

                // 3. 계정 삭제
                psAccount.setString(1, cs_uid);
                psAccount.executeUpdate();

                // 4. 고객 삭제
                psCustomer.setString(1, cs_uid);
                psCustomer.executeUpdate();

                conn.commit(); // 모든 작업 성공 시 커밋
                return true;

            } catch (Exception e) {
                conn.rollback(); // 오류 발생 시 롤백
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true); // 자동 커밋 원래대로
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<StoreDTO> selectAllStores(String keyword) {
        List<StoreDTO> list = new ArrayList<>();
        String sql = "select * from store where st_name like ?";

        try (Connection connection = getConnection();
                PreparedStatement pStatement = connection.prepareStatement(sql)) {
            pStatement.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pStatement.executeQuery()) {
                while (rs.next()) {
                    StoreDTO store = new StoreDTO();
                    store.setSt_uid(rs.getString("st_uid"));
                    store.setSt_name(rs.getString("st_name"));
                    store.setSt_address(rs.getString("st_address"));
                    store.setSt_phone(rs.getString("st_phone"));
                    store.setSt_type(rs.getString("st_type"));
                    store.setSt_profit(rs.getInt("st_profit"));

                    list.add(store);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 주문 메뉴 완료처리
    public boolean completeOrder(int menu_no) {
        String sql = "update orders set od_state = 2 " +
                "where od_menu = ? and od_state = 1 limit 1";
        try (Connection connection = getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, menu_no);
            return pstmt.executeUpdate() > 0; // 실행한 sql이 잘 반영됐는지 확인

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 즐겨찾기 상태 확인
    public boolean isFavorite(String cs_uid, String st_uid) {
        String sql = "select count(*) from favorites where cs_uid = ? and st_uid = ?";
        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, cs_uid);
            preparedStatement.setString(2, st_uid);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // 0보다 크면 즐겨찾기 된 상태
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 즐겨찾기 중복 방지용 내부 메서드
    private void executeSimpleUpdate(String sql, String cs_uid, String st_uid) {
        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, cs_uid);
            preparedStatement.setString(2, st_uid);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 즐겨찾기 토글
    public boolean toggleFavorite(String cs_uid, String st_uid) {
        // 현재 상태를 먼저 파악
        boolean currentStatus = isFavorite(cs_uid, st_uid);

        if (currentStatus) {
            // 이미 있으면 삭제
            String sql = "delete from favorites where cs_uid = ? and st_uid = ?";
            executeSimpleUpdate(sql, cs_uid, st_uid);
            return false; // 해제됨을 반환
        } else {
            // 없으면 추가
            String sql = "insert into favorites(cs_uid, st_uid) values(?, ?)";
            executeSimpleUpdate(sql, cs_uid, st_uid);
            return true; // 등록됨을 반환
        }
    }
}