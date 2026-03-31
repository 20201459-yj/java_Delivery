package frame;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import db.ReviewDAO;
import db.StoreDAO;
import vo.ReviewDTO;
import vo.StoreDTO;

//JPanel 상속
public class StoreReviewPanel extends JPanel {

    StoreDAO storeDAO = new StoreDAO();

    // 화면 전환 이벤트를 위해 버튼을 멤버 변수로 선언
    private JButton btnOrder; // 주문접수
    private JButton btnMenu; // 메뉴관리
    private JButton btnReview; // 
    private JButton btnLogout;

    private CardLayout cardLayout = new CardLayout();
    private JPanel centerCards = new JPanel(cardLayout);
    private JScrollPane scroll; // 스크롤

    public StoreReviewPanel(StoreDTO loginStore) {

        // 패널 기본 설정 (창 설정 제거)
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단 네비게이션 (탭 3개)
        JPanel navPanel = new JPanel(new GridLayout(1, 3));

        btnOrder = new JButton("주문접수");
        styleNavButton(btnOrder, false);

        btnMenu = new JButton("메뉴관리");
        styleNavButton(btnMenu, false);

        btnReview = new JButton("리뷰관리");
        styleNavButton(btnReview, true); // 현재 화면이므로 Bold 처리

        btnLogout = new JButton("로그아웃");
        styleNavButton(btnLogout, false);

        navPanel.add(btnOrder);
        navPanel.add(btnMenu);
        navPanel.add(btnReview);
        navPanel.add(btnLogout);

        // 메인 컨텐츠 영역
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(5, 20, 20, 20));

        // Review 타이틀 (왼쪽 상단)
        JPanel reviewTitlePanel = new JPanel(new BorderLayout());
        reviewTitlePanel.setBackground(Color.WHITE);
        reviewTitlePanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel reviewTitle = new JLabel(
                "<html><span style='font-size:22px'>★</span> Review</html>");
        reviewTitle.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        reviewTitle.setHorizontalAlignment(SwingConstants.LEFT);

        reviewTitlePanel.add(reviewTitle, BorderLayout.WEST);

        // 정렬 버튼 패널
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sortPanel.setBackground(Color.WHITE);

        // 정렬 드롭다운
        String[] sortOption = { "기본순", "평점 높은순", "평점 낮은순" };
        UIManager.put("ComboBox.selectionBackground", Color.WHITE); // 강제 화이트
        UIManager.put("ComboBox.selectionForeground", Color.BLACK);
        JComboBox<String> sortBox = new JComboBox<>(sortOption);
        sortBox.setFont(new Font("맑은 고딕", Font.PLAIN, 13));

        sortBox.setBackground(Color.WHITE);
        sortBox.setOpaque(true);

        // 버튼 클릭시 회색 방지
        sortBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                // 항상 흰색
                lbl.setBackground(Color.WHITE);
                lbl.setForeground(Color.BLACK);

                // 선택 효과 완전 제거
                if (isSelected) {
                    lbl.setBackground(Color.WHITE);
                    lbl.setForeground(Color.BLACK);
                }

                return lbl;
            }
        });

        sortPanel.add(sortBox);

        contentPanel.add(sortPanel);
        contentPanel.add(Box.createVerticalStrut(5));

        // 리뷰 리스트 패널
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        // DB에서 리뷰 데이터 가져오기
        ReviewDAO reviewDAO = new ReviewDAO();
        List<ReviewDTO> reviewList = (loginStore != null) ? reviewDAO.getReviewByStore(loginStore.getSt_uid())
                : new ArrayList<ReviewDTO>();

        // 정렬을 위한 원본 데이터 리스트에 저장
        List<ReviewDTO> originalList = new ArrayList<>();
        originalList.addAll(reviewList);

        // 평균 평점 계산
        double total = 0;
        for (ReviewDTO r : reviewList) {
            total += r.getRv_score();
        }
        double avg = total / reviewList.size();

        // 평균 평점 라벨 (오른쪽 끝)
        JLabel avgLabel = new JLabel(
                "평균 ★" + String.format("%.1f", avg) + "  |  리뷰 " + reviewList.size() + "개");
        avgLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        avgLabel.setForeground(Color.GRAY);
        avgLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        reviewTitlePanel.add(avgLabel, BorderLayout.EAST);

        // 출력
        for (ReviewDTO reviewDTO : reviewList) {
            addReviewCard(listPanel,
                    "★ " + reviewDTO.getRv_score() + "점",
                    reviewDTO.getCs_name(),
                    reviewDTO.getRv_content());
        }

        sortBox.addActionListener(e -> {

            String selected = (String) sortBox.getSelectedItem();

            if (selected.equals("기본순")) {
                reviewList.clear();
                reviewList.addAll(originalList);
            } else if (selected.equals("평점 높은순")) {
                Collections.sort(reviewList, (a, b) -> b.getRv_score() - a.getRv_score());
            } else if (selected.equals("평점 낮은순")) {
                Collections.sort(reviewList, (a, b) -> a.getRv_score() - b.getRv_score());
            }

            // 리스트 다시 출력
            listPanel.removeAll();

            for (ReviewDTO reviewDTO : reviewList) {
                addReviewCard(listPanel,
                        "★ " + reviewDTO.getRv_score() + "점",
                        reviewDTO.getCs_name(),
                        reviewDTO.getRv_content());
            }

            listPanel.revalidate();
            listPanel.repaint();
            // 스크롤 항상 위에서부터 시작
            SwingUtilities.invokeLater(() -> {
                JScrollBar bar = scroll.getVerticalScrollBar();
                bar.setValue(0);
            });

        });

        // 배치

        contentPanel.add(listPanel);

        // ============================================================

        // 스크롤 속도 빠르게
        scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null); // scroll 밑줄 삭제
        scroll.getVerticalScrollBar().setUnitIncrement(20); // 마우스 스크롤 속도 조정
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // 세로 사이드바 두께 0으로 설정(사이드바 안 보이게)
        scroll.getVerticalScrollBar().setBlockIncrement(80); // 한번에 이동량
        SwingUtilities.invokeLater(() -> {
            scroll.getVerticalScrollBar().setValue(0);
        });// 위에서부터 시작

        // CardLayout 등록
        centerCards.add(scroll, "REVIEW");
        centerCards.add(new StoreOrderPanel(loginStore), "ORDER");
        centerCards.add(new StoreMenuPanel(loginStore), "MENU");

        // ============================================================

        // JPanel에 부착
        JPanel topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
        topArea.setBackground(Color.WHITE);

        topArea.add(navPanel);
        topArea.add(reviewTitlePanel);

        add(topArea, BorderLayout.NORTH);
        add(centerCards, BorderLayout.CENTER);

        // (참고) 탭 이동 리스너 예시
        btnOrder.addActionListener(e -> {
            // cardLayout.show(centerCards, "ORDER");
            // updateTabStyles(btnOrder);
            FrameBase.getInstance(new StoreOrderPanel(loginStore));
        });

        btnMenu.addActionListener(e -> {
            // cardLayout.show(centerCards, "MENU");
            // updateTabStyles(btnMenu);
            FrameBase.getInstance(new StoreMenuPanel(loginStore));
        });
        
        btnLogout.addActionListener(e -> FrameBase.getInstance(new LoginPanel()));

    }

    // [디자인 함수] 리뷰 카드 생성 (내용을 위로, 정보를 아래로 변경)
    private void addReviewCard(JPanel parent, String stars, String name, String content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(245, 245, 245)); // 연한 회색 배경
        card.setBorder(new EmptyBorder(
                15, 15, 15, 15)); // 안쪽 여백
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); // 카드 크기

        // 1. 내용 (위쪽으로 이동)
        JTextArea contentArea = new JTextArea(content);
        contentArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        contentArea.setBackground(new Color(245, 245, 245)); // 배경색 일치
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        // 내용 아래쪽에 간격을 줌 (정보와 분리)
        contentArea.setBorder(new EmptyBorder(0, 0, 10, 0));

        // 2. 정보 패널 (별점 + 닉네임) -> 아래쪽으로 이동
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(245, 245, 245));

        JLabel starLabel = new JLabel(stars);
        starLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        starLabel.setForeground(new Color(255, 153, 0)); // 별점 색상 강조 (선택사항)

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12)); // 폰트 사이즈 살짝 줄임
        nameLabel.setForeground(Color.GRAY); // 회색으로 변경하여 내용보다 덜 튀게 설정
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        infoPanel.add(starLabel, BorderLayout.WEST);
        infoPanel.add(nameLabel, BorderLayout.EAST);

        // 3. 카드에 조립 (순서 변경)
        card.add(contentArea, BorderLayout.CENTER); // 내용을 메인 공간에 배치
        card.add(infoPanel, BorderLayout.SOUTH); // 정보를 하단에 배치

        // 리스트에 추가
        parent.add(card);
        parent.add(Box.createVerticalStrut(10));
    }

    // [디자인 함수] 네비게이션 버튼 스타일
    private void styleNavButton(JButton btn, boolean isSelected) {
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));

        if (isSelected) {
            btn.setFont(new Font("맑은 고딕", Font.BOLD, 16)); // 선택됨
        } else {
            btn.setFont(new Font("맑은 고딕", Font.PLAIN, 16)); // 선택 안됨
        }
    }

    // // ★ 테스트용 메인 함수
    // public static void main(String[] args) {
    // JFrame frame = new JFrame("리뷰 관리 패널 테스트");
    // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // frame.setSize(450, 750);
    // frame.setLocationRelativeTo(null);

    // // 패널 부착
    // frame.add(new StoreReviewPanel(null));

    // frame.setVisible(true);
    // }
}