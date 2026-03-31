
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.x.protobuf.MysqlxCrud.Order;

import api.NaverMap;
import vo.CustomerDTO;
import vo.MenuDTO;
import vo.OrdersDTO;
import vo.OrdersDTO;
import vo.ReviewDTO;
import vo.StoreDTO;

public class StoreDAO {
    String url = "jdbc:mysql://localhost:3306/javadelivery";
    String user = "root";
    String pass = "yoonjae1102@";
    NaverMap naver = new NaverMap();

    public Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, user, pass);
        return connection;
    }

    public String newUid() {
        String genUid = null;
        String sql = "select max(cast(substring(st_uid, 2) as unsigned)) as curruid from store";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String currid = rs.getString("curruid");
                    if (currid == null) {
                        return "S1";
                    } else {
                        return "S" + (Integer.parseInt(currid) + 1);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "S1";
    }

    public StoreDTO getStore(String st_uid) {
        String sql = "select * from store where st_uid = ?";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, st_uid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StoreDTO store = new StoreDTO();
                    store.setSt_uid(st_uid);
                    store.setSt_name(rs.getString("st_name"));
                    store.setSt_address(rs.getString("st_address"));
                    store.setSt_phone(rs.getString("st_phone"));
                    store.setSt_type(rs.getString("st_type"));
                    store.setSt_profit(rs.getInt("st_profit"));

                    return store;
                }
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public boolean register(String id, String pw, StoreDTO store) {
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

            sql = "insert into store values(?, ?, ?, ?, ?, ?)";
            PreparedStatement psST = connection.prepareStatement(sql);
            psST.setString(1, newUid);
            psST.setString(2, store.getSt_name());
            psST.setString(3, store.getSt_address());
            psST.setString(4, store.getSt_phone());
            psST.setString(5, store.getSt_type());
            psST.setInt(6, 0);
            psST.executeUpdate();

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

    public StoreDTO login(String id, String pw) {
        StoreDTO loginStore = new StoreDTO();
        String sql = "select * from account join store on st_uid = ac_uid" +
                " where ac_id = ? and ac_pw = ?";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, pw);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    loginStore.setSt_uid(rs.getString("ac_uid"));
                    loginStore.setSt_name(rs.getString("st_name"));
                    loginStore.setSt_address(rs.getString("st_address"));
                    loginStore.setSt_phone(rs.getString("st_phone"));
                    loginStore.setSt_type(rs.getString("st_type"));
                    loginStore.setSt_profit(rs.getInt("st_profit"));
                }
                System.out.println(loginStore.getSt_name());
                System.out.println(loginStore.getSt_address());
                return loginStore;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addMenu(StoreDTO loginStore, String menuName, int menuPrice) {
        String sql = "insert into menu (menu_store, menu_name, menu_price) values (?, ?, ?)";
        try (Connection connection = getConnection();
                PreparedStatement pstmt = connection
                        .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, loginStore.getSt_uid());
            pstmt.setString(2, menuName);
            pstmt.setInt(3, menuPrice);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void deleteMenu(int menu_no) {
        try (Connection connection = getConnection();
                PreparedStatement pstmt = connection.prepareStatement("delete from menu where menu_no = ?");) {
            pstmt.setInt(1, menu_no);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MenuDTO> showMenu(StoreDTO loginStore) {
        List<MenuDTO> list = new ArrayList<MenuDTO>();

        try (Connection connection = getConnection();
                PreparedStatement pstmt = connection
                        .prepareStatement("select * from menu where menu_store = ? ")) {
            pstmt.setString(1, loginStore.getSt_uid());
            try (ResultSet rs = pstmt.executeQuery();) {
                while (rs.next()) {
                    MenuDTO menuDTO = new MenuDTO();
                    menuDTO.setMenu_no(rs.getInt("menu_no"));
                    menuDTO.setMenu_name(rs.getString("menu_name"));
                    menuDTO.setMenu_price(rs.getInt("menu_price"));
                    list.add(menuDTO);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ReviewDTO> showReview(StoreDTO loginStore) {
        List<ReviewDTO> list = new ArrayList<ReviewDTO>();

        try (Connection connection = getConnection();
                PreparedStatement pstmt = connection
                        .prepareStatement("select cs_name, rv_score, rv_content , menu_name " +
                                "from review " +
                                "join menu  on menu_no = rv_menu " +
                                "join store on st_uid = menu_store " +
                                "join customer on rv_customer = cs_uid " +
                                "where st_uid = ? ");) {
            pstmt.setString(1, loginStore.getSt_uid());
            try (ResultSet rs = pstmt.executeQuery();) {
                while (rs.next()) {
                    ReviewDTO review = new ReviewDTO();
                    review.setCs_name(rs.getString("cs_name"));
                    review.setRv_score(rs.getInt("rv_score"));
                    review.setRv_content(rs.getString("rv_content"));
                    review.setMenu_name(rs.getString("menu_name"));
                    list.add(review);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<OrdersDTO> myOrder(int state, StoreDTO loginStore) { // state에 따른 분류 보여줌
        List<OrdersDTO> list = new ArrayList<OrdersDTO>();
        String sql = "select * from orders where od_store = ? and od_state = ?";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, loginStore.getSt_uid());
            ps.setInt(2, state);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String getAddress(String uid) {
        String sql = null;
        if (uid != null && uid.substring(0, 1).equals("C")) {
            sql = "select cs_address from customer where cs_uid = ?";
        } else if (uid != null && uid.substring(0, 1).equals("S")) {
            sql = "select st_address from store where st_uid = ?";
        }

        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // -1 : 일반 오류
    // 1 : 수락 성공
    // -2 : 삭제 성공
    public int receiveOrder(OrdersDTO order, boolean receive) {
        String sql = null;
        String store = getAddress(order.getOd_store());
        String customer = getAddress(order.getOd_customer());
        if (receive) {
            sql = "update orders set od_state = 1, od_start = now(), od_duration = ? where od_no = ?";
        } else {
            sql = "delete from orders where od_no = ?";
        }

        int duration = naver.getDuration(naver.getLocation(store), naver.getLocation(customer));
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            boolean success = false;
            if (receive) {
                ps.setInt(1, duration + order.getOd_duration());
                ps.setInt(2, order.getOd_no());
                return (ps.executeUpdate() > 0) ? 1 : -1;
            } else {
                ps.setInt(1, order.getOd_no());
                return (ps.executeUpdate() > 0) ? -2 : -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void completeOrder(int od_no) {

    }

    // 가게의 총 판매액 반환
    // -1 : 오류
    public int getTotalSell(String st_uid) {
        String sql = "select sum(menu_price) as sell "
                + "from orders "
                + "join menu on menu_no = od_menu "
                + "where od_store = ?";
        try (Connection connection = getConnection();
                PreparedStatement pStatement = connection.prepareStatement(sql)) {
            pStatement.setString(1, st_uid);
            try (ResultSet rs = pStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("sell");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
