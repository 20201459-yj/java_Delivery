package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import vo.CustomerDTO;
import vo.MenuDTO;
import vo.ReviewDTO;
import vo.StoreDTO;

public class ReviewDAO {
    String url = "jdbc:mysql://localhost:3306/javadelivery";
    String user = "root";
    String pass = "yoonjae1102@";

    public Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, user, pass);
        return connection;
    }

    // 리뷰 등록하기
    // 메뉴 id, 평점, 멘트를 리뷰테이블에 등록
    // 매개변수로 메뉴id, 평점, 멘트 필요
    public boolean addReview(ReviewDTO review) {
        try (Connection connection = getConnection();
                PreparedStatement pStatement = connection
                        .prepareStatement(
                                "insert into review(rv_customer, rv_menu, rv_score, rv_content) values(?, ?, ?, ?)");) {
            pStatement.setString(1, review.getRv_customer());
            pStatement.setInt(2, review.getRv_menu());
            pStatement.setInt(3, review.getRv_score());
            pStatement.setString(4, review.getRv_content());
            return pStatement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public List<ReviewDTO> storeReview(String st_uid) {
        List<ReviewDTO> list = new ArrayList<ReviewDTO>();
        String sql = "select * from review join menu on rv_menu = menu_no where menu_store = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, st_uid);
            try (ResultSet rs = ps.executeQuery()) {
                ReviewDTO review = new ReviewDTO();
                review.setRv_no(rs.getInt("rv_no"));
                review.setRv_no(rs.getInt("rv_menu"));
                review.setRv_score(rs.getInt("rv_score"));
                review.setRv_customer(rs.getString("rv_customer"));
                review.setRv_content(rs.getString("rv_content"));

                list.add(review);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 특정 고객 리뷰 불러오기
    // 특정 고객이 작성한 모든 리뷰를 리스트에 저장 후 반환
    // 매개변수 고객 UID 필요
    // rv_customer : customer 테이블의 외래키
    public List<ReviewDTO> getMyReview(CustomerDTO customer) {
        List<ReviewDTO> list = new ArrayList<ReviewDTO>();
        try (Connection connection = getConnection();
                PreparedStatement pStatement = connection
                        .prepareStatement("select * from review where rv_customer = ?");) {
            pStatement.setString(1, customer.getCs_uid());
            try (ResultSet rs = pStatement.executeQuery();) {
                while (rs.next()) {
                    ReviewDTO review = new ReviewDTO();
                    review.setRv_no(rs.getInt("rv_no"));
                    review.setRv_menu(rs.getInt("rv_menu"));
                    review.setRv_score(rs.getInt("rv_score"));
                    review.setRv_content(rs.getString("rv_content"));
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

    // --- 리뷰 삭제 ---
    public boolean deleteReview(int rv_no) {
        String sql = "DELETE FROM review WHERE rv_no = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rv_no);
            return ps.executeUpdate() > 0; // 삭제 성공 시 true 반환
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<StoreDTO> reviewableStore(CustomerDTO customer) {
        List<StoreDTO> list = new ArrayList<StoreDTO>();
        String sql = "select od_store, st_name, count(*) as od_count from orders join store on st_uid = od_store"
                + " where od_customer = ? and od_state = 2 group by od_store";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, customer.getCs_uid());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StoreDTO store = new StoreDTO();
                    store.setSt_uid(rs.getString("od_store"));
                    store.setSt_name(rs.getString("st_name"));
                    store.setOd_count(rs.getInt("od_count"));

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

    public List<MenuDTO> reviewableMenu(CustomerDTO customer, String st_uid) {
        List<MenuDTO> list = new ArrayList<MenuDTO>();
        String sql = "select menu_no, menu_name from orders join menu on od_menu = menu_no"
                + " where od_customer = ? and od_state = 2 and od_store = ? group by menu_no";
        try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, customer.getCs_uid());
            ps.setString(2, st_uid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MenuDTO menu = new MenuDTO();
                    menu.setMenu_no(rs.getInt("menu_no"));
                    menu.setMenu_name(rs.getString("menu_name"));

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

    public List<ReviewDTO> getReviewByStore(String storeId) {
        List<ReviewDTO> list = new ArrayList<ReviewDTO>();
        String sql = "select rv_score, rv_content, cs_name from review " +
                "join menu on rv_menu = menu_no " +
                "join customer on rv_customer = cs_uid where menu_store = ? " +
                "order by rv_no desc";
        try (Connection connection = getConnection();
                PreparedStatement pStatement = connection.prepareStatement(sql)) {
            pStatement.setString(1, storeId);
            try (ResultSet rs = pStatement.executeQuery();) {
                while (rs.next()) {
                    ReviewDTO reviewDTO = new ReviewDTO();
                    reviewDTO.setRv_score(rs.getInt("rv_score"));
                    reviewDTO.setRv_content(rs.getString("rv_content"));
                    reviewDTO.setCs_name(rs.getString("cs_name"));
                    list.add(reviewDTO);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
