package frame;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.util.ArrayList;
import java.util.List;

import db.CustomerDAO;
import db.EtcDAO;
import db.StoreDAO;
import vo.MenuDTO;
import vo.OrdersDTO;
import vo.StoreDTO;

// ★ JFrame 대신 JPanel 상속
public class StoreOrderPanel extends JPanel {

    // 배민 민트색 정의
    private final Color MINT_COLOR = new Color(42, 193, 188);

    // 이벤트 연결을 위해 버튼을 멤버 변수로 선언
    private JButton btnOrder; // 주문접수 탭
    private JButton btnMenu; // 메뉴관리 탭
    private JButton btnReview; // 리뷰관리 탭
    private JButton btnLogout;
    JButton btnRefresh;// 새로고침 버튼
    final StoreDTO[] loginFinal = new StoreDTO[1];

    public StoreOrderPanel(StoreDTO loginStore) {
        loginFinal[0] = loginStore;

        StoreDAO storeDAO = new StoreDAO();

        // 1. 패널 기본 설정 (창 설정 제거)
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 2. 상단 네비게이션 (탭 버튼 3개)
        JPanel navPanel = new JPanel(new GridLayout(1, 4));

        btnOrder = new JButton("주문접수");
        styleNavButton(btnOrder);

        btnMenu = new JButton("메뉴관리");
        styleNavButton(btnMenu);

        btnReview = new JButton("리뷰관리");
        styleNavButton(btnReview);
        
        btnLogout = new JButton("로그아웃");
        styleNavButton(btnLogout);

        navPanel.add(btnOrder);
        navPanel.add(btnMenu);
        navPanel.add(btnReview);
        navPanel.add(btnLogout);

        // 3. 메인 컨텐츠 영역
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // 전체 여백

        // 제목 ("현재 들어온 주문")
        // 제목과 버튼을 가로로 배치할 헤더 패널 생성
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // 높이 고정 (작게)
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 왼쪽 정렬 맞춤

        // 1) 제목
        JLabel titleLabel = new JLabel("현재 들어온 주문");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        // 2) ★ 새로고침 버튼 생성
        btnRefresh = new JButton("새로고침 ↻");
        btnRefresh.setFont(new Font("맑은 고딕", Font.PLAIN, 12)); // 글자 작게
        btnRefresh.setBackground(Color.WHITE);
        btnRefresh.setForeground(Color.DARK_GRAY);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(new LineBorder(Color.LIGHT_GRAY)); // 얇은 테두리
        btnRefresh.setPreferredSize(new Dimension(80, 25)); // 버튼 크기 (작게)
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 올리면 손가락 모양

        // 헤더 패널에 부착 (왼쪽: 제목, 오른쪽: 버튼)
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        // 컨텐츠 패널에 '헤더 패널'을 추가 (원래 titleLabel 넣던 곳)
        contentPanel.add(headerPanel);

        // 빈 공간 채우기
        contentPanel.add(Box.createVerticalStrut(20));

        // ---------------------------------------------------------
        // [B] 리스트 패널 (주문 카드가 쌓이는 곳) - 갱신 영역
        // ---------------------------------------------------------
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // ★ 중요: listPanel을 멤버 변수가 아닌 지역변수로 쓰더라도
        // 람다(이벤트)에서 쓰려면 'effectively final'이어야 하므로 그대로 둡니다.

        contentPanel.add(listPanel); // 리스트 패널 추가
        contentPanel.add(Box.createVerticalGlue()); // 남는 공간 채우기

        // 4. 스크롤바 생성 및 부착
        add(navPanel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20); // 마우스 스크롤 속도 조정
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // 세로 사이드바 두께 0으로 설정(사이드바 안 보이게)
        scrollPane.setBorder(null); // 스크롤 테두리 제거 (디자인 깔끔하게)
        add(scrollPane, BorderLayout.CENTER);

        EtcDAO etcDAO = new EtcDAO();
        CustomerDAO customerDAO = new CustomerDAO();

        String[] stateName = { "대기 주문", "조리&배달 중인 주문" };
        Color[] sectionColors = { MINT_COLOR, new Color(255, 140, 0) };
        for (int i = 0; i < stateName.length; i++) {
            listPanel.add(createSectionHeader(stateName[i], sectionColors[i]));
            listPanel.add(Box.createVerticalStrut(15));

            List<OrdersDTO> orderList = storeDAO.myOrder(i, loginStore);

            OrdersDTO ordersDTO = new OrdersDTO();
            if (orderList != null && !orderList.isEmpty()) {
                for (OrdersDTO order : orderList) {
                    System.out.println("찾으려는 고객 ID: " + order.getOd_customer());
                    String menu = etcDAO.getMenu(order.getOd_menu()).getMenu_name(); // 메뉴명
                    String addr = customerDAO.getCustomer(order.getOd_customer()).getCs_address(); // 주소
                    String price = etcDAO.getMenu(order.getOd_menu()).getMenu_price() + "원"; // 가격
                    String content = order.getOd_content(); // 요청사항
                    LocalDateTime start = order.getOd_start();
                    int duration = order.getOd_duration();

                    // 카드 추가 (listPanel에 붙임)
                    if (i == 0) {
                        addOrderCard(listPanel, menu, addr, price, content, null, 0, order);

                    } else {
                        addOrderCard(listPanel, menu, addr, price, content, start, duration, order);

                    }
                }
            } else {
                JLabel emptyLabel = new JLabel("현재 내역이 없습니다.");
                emptyLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
                emptyLabel.setForeground(Color.LIGHT_GRAY);
                emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                listPanel.add(Box.createVerticalStrut(10));
                listPanel.add(emptyLabel);
                listPanel.add(Box.createVerticalStrut(10));
            }
            listPanel.add(Box.createVerticalStrut(40));
        }
        listPanel.revalidate();
        listPanel.repaint();

        // 버튼 이벤트 (기능은 직접 구현)
        btnRefresh.addActionListener(e -> {
            FrameBase.getInstance(new StoreOrderPanel(loginStore));

        });

        // btnRefresh.doClick();

        // ★ JPanel에 부착
        // add(navPanel, BorderLayout.NORTH);
        add(new JScrollPane(contentPanel), BorderLayout.CENTER); // 스크롤 기능 포함

        // 메뉴로 이동

        add(navPanel, BorderLayout.NORTH);

        btnMenu.addActionListener(e -> {
            FrameBase.getInstance(new StoreMenuPanel(loginStore));
        });

        // 리뷰로 이동
        btnReview.addActionListener(e -> {
            FrameBase.getInstance(new StoreReviewPanel(loginStore));
        });

        
        btnLogout.addActionListener(e -> FrameBase.getInstance(new LoginPanel()));

    }

    // [디자인 함수] 주문 카드 생성
    private void addOrderCard(JPanel parent, String menuName, String address, String price, String content,
            java.time.LocalDateTime orderTime, int leadTime, OrdersDTO orders) {

        StoreDAO storeDAO = new StoreDAO();

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(15, 15, 15, 15)));

        // [수정] 줄이 더 늘어날 수 있으므로 높이를 170으로 조정 (기본 틀 유지)
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 1. 왼쪽 텍스트 정보 (기본 4줄에서 시간 정보 추가를 위해 6줄로 확장)
        JPanel infoPanel = new JPanel(new GridLayout(6, 1, 0, 3));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(menuName);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));

        JLabel addrLabel = new JLabel(address);
        addrLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        String requestText = (content == null || content.isEmpty()) ? "요청사항 없음" : "요청: " + content;
        JLabel requestLabel = new JLabel(requestText);
        requestLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        requestLabel.setForeground(new Color(220, 20, 60));

        JLabel priceLabel = new JLabel(price);
        priceLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        // --- [여기서부터 추가 정보 섹션] ---

        // 주문 시각 처리 (널값이 아니면 포맷팅해서 표시)
        String timeText = "";
        if (orderTime != null) {
            timeText = "주문시각: " + orderTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        JLabel timeLabel = new JLabel(timeText);
        timeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        timeLabel.setForeground(Color.GRAY);

        // 소요 시간 처리 (0보다 크면 표시)
        String leadText = (leadTime > 0) ? "예상소요: " + leadTime + "분" : "계산불가합니다.";
        JLabel leadLabel = new JLabel(leadText);
        leadLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        leadLabel.setForeground(Color.BLUE);

        // 패널에 순서대로 추가 (기존 순서 유지 + 뒤에 추가)
        infoPanel.add(nameLabel);
        infoPanel.add(addrLabel);
        infoPanel.add(requestLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(timeLabel); // 추가
        infoPanel.add(leadLabel); // 추가

        // --- [추가 섹션 끝] ---

        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setPreferredSize(new Dimension(100, 90));
        JButton acceptBtn = new JButton("주문 수락");
        acceptBtn.setVisible(false);
        JButton rejectBtn = new JButton("거절");
        rejectBtn.setVisible(false);
        if (orderTime == null) {
            acceptBtn.setVisible(true);
            acceptBtn.setBackground(MINT_COLOR);
            acceptBtn.setForeground(Color.WHITE);
            acceptBtn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
            acceptBtn.setFocusPainted(false);
            acceptBtn.setBorderPainted(false);

            rejectBtn.setVisible(true);
            rejectBtn.setBackground(Color.WHITE);
            rejectBtn.setForeground(Color.BLACK);
            rejectBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
            rejectBtn.setFocusPainted(false);
            rejectBtn.setBorder(new LineBorder(Color.LIGHT_GRAY));

            btnPanel.add(acceptBtn);
            btnPanel.add(rejectBtn);
        } else {
            JButton cookBtn = new JButton(String.valueOf(orders.getOd_duration()) + "분 소요");
            cookBtn.setBackground(new Color(255, 140, 0));
            cookBtn.setForeground(Color.WHITE);
            cookBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            cookBtn.setFocusPainted(false);
            cookBtn.setBorderPainted(false);

            btnPanel.add(cookBtn, BorderLayout.CENTER);
        }
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.EAST);

        parent.add(card);
        parent.add(Box.createVerticalStrut(15));

        acceptBtn.addActionListener(e -> {
            StoreAcceptPanel temp = new StoreAcceptPanel(orders.getOd_content());
            int choice = JOptionPane.showConfirmDialog(this, temp, "조리시간", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "주문을 수락하셨습니다 !!");
                orders.setOd_duration(Integer.parseInt(temp.getTimeText()));
                storeDAO.receiveOrder(orders, true);
                FrameBase.getInstance(new StoreOrderPanel(this.loginFinal[0]));
            } else {

            }
            this.getParent().revalidate();
            this.getParent().repaint();

        });

        rejectBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "주문을 거절하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "주문을 거절했습니다 !!");

                storeDAO.receiveOrder(orders, false);
                parent.remove(card);
                parent.revalidate();
                parent.repaint();
            } // if
            else {

            }
        });
    }

    // [디자인 함수] 상단 탭 버튼 스타일
    private void styleNavButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));

        // '주문접수' 버튼인 경우만 굵게 표시 (현재 탭)
        if (btn.getText().equals("주문접수")) {
            btn.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        }
    }

    // [디자인 함수] 섹션 헤더 만들기 (새로 추가됨)
    private JPanel createSectionHeader(String title, Color pointColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 왼쪽 컬러 바 (포인트)
        JPanel bar = new JPanel();
        bar.setBackground(pointColor);
        bar.setPreferredSize(new Dimension(5, 0)); // 두께 5px

        // 제목 텍스트
        JLabel lbl = new JLabel("  " + title); // 앞에 공백 살짝
        lbl.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        lbl.setForeground(Color.DARK_GRAY);

        panel.add(bar, BorderLayout.WEST);
        panel.add(lbl, BorderLayout.CENTER);

        // 아래쪽 얇은 구분선
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(230, 230, 230));
        panel.add(sep, BorderLayout.SOUTH);

        return panel;
    }

}