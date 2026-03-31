package frame;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.List;

import db.CustomerDAO;
import db.EtcDAO;
import db.ReviewDAO;
import vo.CustomerDTO;
import vo.MenuDTO;
import vo.ReviewDTO;
import vo.StoreDTO;

public class CustomerReviewPanel extends JPanel {
    EtcDAO etcDAO = new EtcDAO();

    public CustomerReviewPanel(CustomerDTO loginCustomer) {

        CustomerDAO customerDao = new CustomerDAO();
        ReviewDAO reviewDAO = new ReviewDAO();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- 1. 상단 타이틀 + 뒤로가기 버튼 ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(new EmptyBorder(10, 10, 10, 0));

        JLabel title = new JLabel("리뷰 관리");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        JButton backBtn = new JButton("뒤로가기 ◀️");
        styleButton(backBtn, new Color(150, 150, 150));
        backBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        backBtn.setBorder(new EmptyBorder(5, 10, 5, 0));
        backBtn.setPreferredSize(null);
        backBtn.setMaximumSize(new Dimension(100, 30));
        backBtn.addActionListener(e -> FrameBase.getInstance(new CustomerMyPagePanel(loginCustomer)));

        titlePanel.add(title, BorderLayout.WEST);
        titlePanel.add(backBtn, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // --- 2. 메인 컨텐츠 영역 (상하 2분할) ---
        JPanel mainContentPanel = new JPanel(new GridLayout(2, 1, 0, 10)); // 위아래 간격 10
        mainContentPanel.setBackground(Color.WHITE);
        mainContentPanel.setBorder(new EmptyBorder(5, 10, 10, 10));

        // =================================================================
        // [위쪽] 주문 내역 (리뷰 작성 가능한 리스트)
        // =================================================================
        JPanel topSectionPanel = new JPanel(new BorderLayout());
        topSectionPanel.setBackground(Color.WHITE);

        JLabel topTitle = new JLabel("✍️ 리뷰 작성하기 (최근 주문)");
        topTitle.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        topTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        topSectionPanel.add(topTitle, BorderLayout.NORTH);

        JPanel orderListPanel = new JPanel();
        orderListPanel.setLayout(new BoxLayout(orderListPanel, BoxLayout.Y_AXIS));
        orderListPanel.setBackground(Color.WHITE);

        // ---------------------------------------------------------------
        // ★ [DB 연동] 주문 내역 불러오기
        // ★ 사용하시는 DAO 메서드 이름에 맞춰 아래 줄을 수정해주세요.
        // ---------------------------------------------------------------
        // 예: getMyOrders, getOrderList 등 본인이 만든 메서드 호출
        List<StoreDTO> reviewList = reviewDAO.reviewableStore(loginCustomer);

        if (reviewList == null || reviewList.isEmpty()) {
            JLabel emptyLabel = new JLabel("리뷰를 작성할 주문 내역이 없습니다.");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            orderListPanel.add(Box.createVerticalStrut(20));
            orderListPanel.add(emptyLabel);
        } else {
            for (StoreDTO store : reviewList) {
                // 주문 내역 카드 생성 후 추가
                orderListPanel.add(createOrderCard(store, loginCustomer));
                orderListPanel.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane topScrollPane = new JScrollPane(orderListPanel);
        topScrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        topScrollPane.getViewport().setBackground(Color.WHITE);
        topScrollPane.getVerticalScrollBar().setUnitIncrement(20); // 마우스 스크롤 속도 조정
        topScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // 세로 사이드바 두께 0으로 설정(사이드바 안 보이게)

        topSectionPanel.add(topScrollPane, BorderLayout.CENTER);

        // =================================================================
        // [아래쪽] 내가 쓴 리뷰 리스트
        // =================================================================
        JPanel bottomSectionPanel = new JPanel(new BorderLayout());
        bottomSectionPanel.setBackground(Color.WHITE);

        JLabel bottomTitle = new JLabel("📋 내가 작성한 리뷰");
        bottomTitle.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        bottomTitle.setBorder(new EmptyBorder(10, 0, 10, 0)); // 위아래 여백
        bottomSectionPanel.add(bottomTitle, BorderLayout.NORTH);

        JPanel reviewListPanel = new JPanel();
        reviewListPanel.setLayout(new BoxLayout(reviewListPanel, BoxLayout.Y_AXIS));
        reviewListPanel.setBackground(Color.WHITE);

        // ---------------------------------------------------------------
        // ★ [DB 연동] 작성한 리뷰 불러오기 (기존 코드 유지)
        // ---------------------------------------------------------------
        ReviewDAO reviewDao = new ReviewDAO();
        List<ReviewDTO> reviews = reviewDao.getMyReview(loginCustomer);

        if (reviews == null || reviews.isEmpty()) {
            JLabel emptyLabel = new JLabel("작성한 리뷰가 없습니다.");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            reviewListPanel.add(Box.createVerticalStrut(20));
            reviewListPanel.add(emptyLabel);
        } else {
            for (ReviewDTO review : reviews) {
                reviewListPanel.add(createReviewCard(review));
                reviewListPanel.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane bottomScrollPane = new JScrollPane(reviewListPanel);
        bottomScrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        bottomScrollPane.getViewport().setBackground(Color.WHITE);
        bottomScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        bottomSectionPanel.add(bottomScrollPane, BorderLayout.CENTER);

        // 메인 패널에 상/하단 추가
        mainContentPanel.add(topSectionPanel);
        mainContentPanel.add(bottomSectionPanel);

        add(mainContentPanel, BorderLayout.CENTER);
    }

    // ---------------------------------------------------------------------
    // [UI] 상단: 주문 내역 카드
    // ---------------------------------------------------------------------
    private JPanel createOrderCard(StoreDTO store, CustomerDTO loginCustomer) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(250, 252, 255)); // 살짝 푸른 배경 (구분감)
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 240)),
                new EmptyBorder(10, 10, 10, 10)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 주문 정보 표시
        String infoText = "<html><b>식당명: " + store.getSt_name() + "</b><br>"
                + "총 주문 횟수: " + store.getOd_count() + "회</html>";
        JLabel info = new JLabel(infoText);
        info.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        // 리뷰 쓰기 버튼
        JButton writeBtn = new JButton("리뷰쓰기");
        writeBtn.setBackground(new Color(100, 180, 255));
        writeBtn.setForeground(Color.WHITE);
        writeBtn.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        writeBtn.setFocusPainted(false);
        writeBtn.setBorderPainted(false);

        // 버튼 클릭 시 리뷰 작성
        writeBtn.addActionListener(e -> {
            FrameBase.getInstance(new CustomerReviewWritePanel(loginCustomer, store.getSt_uid()));
        });

        card.add(info, BorderLayout.CENTER);
        card.add(writeBtn, BorderLayout.EAST);

        return card;
    }

    // ---------------------------------------------------------------------
    // [UI] 하단: 작성된 리뷰 카드 (ReviewDTO 사용 - 기존 코드)
    // ---------------------------------------------------------------------
    private JPanel createReviewCard(ReviewDTO review) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(10, 10, 10, 10)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 내용
        JLabel content = new JLabel("<html><b>메뉴: " + etcDAO.getMenu(review.getRv_menu()).getMenu_name() + "</b><br>" +
                "점수: " + review.getRv_score() + "점<br>" +
                review.getRv_content() + "</html>");

        // 삭제 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton deleteBtn = new JButton("삭제");
        deleteBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "정말 삭제하시겠습니까?", "삭제", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                ReviewDAO dao = new ReviewDAO();
                dao.deleteReview(review.getRv_no());

                // 화면 갱신
                Container parent = card.getParent();
                if (parent != null) {
                    parent.remove(card);
                    parent.revalidate();
                    parent.repaint();
                }
            }
        });

        buttonPanel.add(deleteBtn);

        card.add(content, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(300, 40));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
    }
}