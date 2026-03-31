package frame;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import db.StoreDAO;
import vo.MenuDTO;
import vo.StoreDTO;

import java.util.ArrayList;
import java.util.List;

public class StoreMenuPanel extends JPanel {

    // ★ 1. 클래스 전체에서 loginStore를 쓰기 위해 멤버 변수로 선언
    private StoreDTO loginStore;
    private StoreDAO storeDAO = new StoreDAO();

    // 색상 상수 정의
    private final Color MINT_COLOR = new Color(42, 193, 188);
    private final Color DARK_GRAY_BTN = new Color(80, 80, 80);

    // 버튼 필드
    private JButton btnOrder;
    private JButton btnMenu;
    private JButton btnReview;
    private JButton btnLogout;
    public JButton btnBack;

    public StoreMenuPanel(StoreDTO loginStore) {
        // ★ 2. 생성자로 전달받은 로그인 정보를 멤버 변수에 저장
        // 이렇게 하면 아래의 addMenuRow 메서드에서도 this.loginStore를 쓸 수 있습니다.
        this.loginStore = loginStore;

        // 1. 패널 기본 설정
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 2. 상단 네비게이션
        JPanel navPanel = new JPanel(new GridLayout(1, 4));
        btnOrder = new JButton("주문접수");
        styleNavButton(btnOrder, false);
        btnMenu = new JButton("메뉴관리");
        styleNavButton(btnMenu, true);
        btnReview = new JButton("리뷰관리");
        styleNavButton(btnReview, false);
        btnLogout = new JButton("로그아웃");
        styleNavButton(btnLogout, false);
        navPanel.add(btnOrder);
        navPanel.add(btnMenu);
        navPanel.add(btnReview);
        navPanel.add(btnLogout);
        add(navPanel, BorderLayout.NORTH);

        // 3. 메인 컨텐츠 영역
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- (1) 오늘 총 매출 라벨 ---
        JLabel salesLabel = new JLabel("오늘 총 매출: " + storeDAO.getTotalSell(loginStore.getSt_uid()) + "원");
        salesLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        salesLabel.setForeground(MINT_COLOR);
        salesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- (2) 새 메뉴 추가 폼 ---
        JPanel addFormPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        addFormPanel.setBackground(Color.WHITE);
        addFormPanel.setMaximumSize(new Dimension(500, 80));
        addFormPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        TitledBorder border = BorderFactory.createTitledBorder("새 메뉴 추가");
        border.setTitleFont(new Font("맑은 고딕", Font.PLAIN, 12));
        addFormPanel.setBorder(border);

        JTextField inputName = new JTextField("메뉴명", 10);
        JTextField inputPrice = new JTextField("가격", 7);
        JButton addBtn = new JButton("추가");

        addBtn.setBackground(DARK_GRAY_BTN);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addFormPanel.add(inputName);
        addFormPanel.add(inputPrice);
        addFormPanel.add(addBtn);

        // --- (3) 메뉴 리스트 패널 ---
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // DB에서 리스트를 가져와서 반복문(for)
        List<MenuDTO> menuList = storeDAO.showMenu(loginStore);
        for (MenuDTO menuDTO : menuList) {
            addMenuRow(listPanel, menuDTO.getMenu_name(), String.valueOf(menuDTO.getMenu_price()), menuDTO.getMenu_no());
        }

        // --- 전체 배치 ---
        contentPanel.add(salesLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(addFormPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(listPanel);
        contentPanel.add(Box.createVerticalGlue());

        // 메뉴 추가
        addBtn.addActionListener(e -> {
            String name = inputName.getText();
            try {
                int price = Integer.parseInt(inputPrice.getText());
                int temp_no = storeDAO.addMenu(loginStore, name, price);
                addMenuRow(listPanel, name, String.valueOf(price), temp_no);

                inputName.setText("");
                inputPrice.setText("");
                listPanel.revalidate();
                listPanel.repaint();

                JOptionPane.showMessageDialog(this, "메뉴가 추가되었습니다.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "가격은 숫자만 입력해주세요.");
            }
        });

        // 페이지 이동
        btnReview.addActionListener(e -> FrameBase.getInstance(new StoreReviewPanel(loginStore)));
        btnOrder.addActionListener(e -> FrameBase.getInstance(new StoreOrderPanel(loginStore)));
        btnLogout.addActionListener(e -> FrameBase.getInstance(new LoginPanel()));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20); // 마우스 스크롤 속도 조정
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // 세로 사이드바 두께 0으로 설정(사이드바 안 보이게)

        add(scrollPane, BorderLayout.CENTER); // 스크롤 가능하게 넣거나
    }

    // [디자인 함수] 메뉴 한 줄(Row) 추가
    private void addMenuRow(JPanel parent, String name, String priceStr, int menu_no) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(500, 60));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // 1. 왼쪽: 메뉴명 + 가격
        JLabel infoLabel = new JLabel(name + " (" + priceStr + "원)");
        infoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        infoLabel.setForeground(Color.DARK_GRAY);

        // 2. 오른쪽: 삭제 버튼만 존재
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);

        JButton deleteBtn = new JButton("삭제");
        deleteBtn.setBackground(Color.WHITE);
        deleteBtn.setForeground(Color.RED);
        deleteBtn.setBorder(new LineBorder(Color.RED));
        deleteBtn.setFocusPainted(false);

        btnPanel.add(deleteBtn);
        row.add(infoLabel, BorderLayout.CENTER);
        row.add(btnPanel, BorderLayout.EAST);
        parent.add(row);

        // ★ [삭제 버튼 이벤트] : 이곳에 직접 구현하세요
        deleteBtn.addActionListener(e -> {
            storeDAO.deleteMenu(menu_no);
            JOptionPane.showConfirmDialog(this, "메뉴가 삭제됐습니다!!", "메뉴삭제", JOptionPane.INFORMATION_MESSAGE);
            parent.remove(row);
            parent.revalidate();
            parent.repaint();// 화면에서 메뉴 삭제
        });
    }

    private void styleNavButton(JButton btn, boolean isSelected) {
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        if (isSelected) {
            btn.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        } else {
            btn.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        }
    }
}