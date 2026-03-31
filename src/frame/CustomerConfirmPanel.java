package frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import db.CustomerDAO;
import db.StoreDAO;
import vo.CustomerDTO;
import vo.MenuDTO;

public class CustomerConfirmPanel extends JPanel {

    CustomerDAO customerDAO = new CustomerDAO();
    StoreDAO storeDAO = new StoreDAO();
    private CustomerDTO loginUser;

    public CustomerConfirmPanel(CustomerDTO loginUser) {
        this.loginUser = loginUser;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== 상단 민트색 헤더(statusLabel 삽입) =====
        JPanel topHeader = new JPanel();
        topHeader.setLayout(new BoxLayout(topHeader, BoxLayout.Y_AXIS));
        topHeader.setBackground(new Color(42, 193, 188));
        topHeader.setBorder(new EmptyBorder(30, 0, 30, 0));

        JLabel statusLabel = new JLabel("나의 주문 진행 상태");
        statusLabel.setFont(new Font("맑은 고딕", Font.BOLD, 26)); // 기존보다 크게
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topHeader.add(statusLabel);
        // ===== 상단 민트색 헤더(statusLabel 삽입) =====

        // ===== 전체 영역을 위아래로 나눌 메인 컨테이너 =====
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(new EmptyBorder(10, 20, 10, 20));
        // ==============================================

        // ===== menuContainer0 파트 => od_state = 0 =====
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBackground(Color.WHITE);

        JLabel waitingTitle = new JLabel("주문 확인 중");
        waitingTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        waitingTitle.setBorder(new EmptyBorder(0, 5, 10, 0));
        waitingTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        waitingTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel menuContainer0 = new JPanel();
        setupContainerDesign(menuContainer0);

        topSection.add(waitingTitle);
        topSection.add(menuContainer0);
        // ===============================================

        // ===== menuContainer1 파트 => od_state = 1 =====
        JPanel bottomSection = new JPanel();
        bottomSection.setLayout(new BoxLayout(bottomSection, BoxLayout.Y_AXIS));
        bottomSection.setBackground(Color.WHITE);

        JLabel doneTitle = new JLabel("주문 수령 완료");
        doneTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        doneTitle.setBorder(new EmptyBorder(0, 5, 10, 0));
        doneTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        doneTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // 두 번째 컨테이너 (상태 1)
        JPanel menuContainer1 = new JPanel();
        setupContainerDesign(menuContainer1); // 동일한 디자인 설정 적용

        bottomSection.add(doneTitle);
        bottomSection.add(menuContainer1);

        // 2. 메인 컨테이너에 각각 추가 (GridLayout이라서 정확히 반반 차지)

        // 패널 스크롤 추가
        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        mainContainer.add(topSection);
        mainContainer.add(Box.createVerticalStrut(30));
        mainContainer.add(bottomSection);

        List<MenuDTO> menuList = customerDAO.acceptMenu(0, loginUser.getCs_uid());
        List<MenuDTO> acceptList = customerDAO.acceptMenu(1, loginUser.getCs_uid());
        if (menuList.isEmpty()) {
            addEmptyLabel(menuContainer0, "주문한 메뉴가 없습니다.");
        } else {
            for (MenuDTO menu : menuList) {
                menuContainer0.add(createMenuRow(
                        storeDAO.getStore(menu.getMenu_store()).getSt_name(),
                        menu.getMenu_name(),
                        menu.getOd_duration(),
                        menu.getMenu_no(), 0));
                menuContainer0.add(Box.createVerticalStrut(15)); // 간격 넓힘
            }
            menuContainer0.revalidate();
            menuContainer0.repaint();
        }
        if (acceptList.isEmpty()) {
            addEmptyLabel(menuContainer1, "수락된 메뉴가 없습니다.");
        } else {
            for (MenuDTO accept : acceptList) {
                menuContainer1.add(createMenuRow(
                        storeDAO.getStore(accept.getMenu_store()).getSt_name(),
                        accept.getMenu_name(),
                        accept.getOd_duration(),
                        accept.getMenu_no(), 1));
                menuContainer1.add(Box.createVerticalStrut(15)); // 간격 넓힘
            }
            menuContainer1.revalidate();
            menuContainer1.repaint();
        }

        // ======================== 3. 하단 바 ========================
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(250, 250, 250));
        footer.setBorder(new EmptyBorder(15, 20, 15, 20));

        JButton mainBtn = new JButton("마이페이지로 돌아가기");
        mainBtn.setPreferredSize(new Dimension(200, 50)); // 버튼 크기 확대
        mainBtn.setBackground(new Color(60, 60, 60));
        mainBtn.setForeground(Color.WHITE);
        mainBtn.setFont(new Font("맑은 고딕", Font.BOLD, 15)); // 버튼 폰트 확대
        mainBtn.addActionListener(e -> FrameBase.getInstance(new CustomerMyPagePanel(loginUser)));

        footer.add(mainBtn, BorderLayout.CENTER); // 중앙 배치로 변경

        add(topHeader, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private void setupContainerDesign(JPanel container) {
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(248, 248, 248));
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        container.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    private JPanel createMenuRow(String st_name, String store_menu, int od_duration, int menu_no, int state) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        card.setPreferredSize(new Dimension(0, 90));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // 1. 가게명 (왼쪽 상단)
        JLabel storeLabel = new JLabel(st_name);
        storeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(storeLabel, gbc);

        // 2. 메뉴명 (왼쪽 하단)
        JLabel menuLabel = new JLabel(store_menu);
        menuLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(menuLabel, gbc);

        // 3. 소요 시간 (오른쪽)

        String times = "";
        if (state == 0) {
            times = "수락 대기중";
        } else {
            times = od_duration + "분 소요";
        }
        JLabel timeLabel = new JLabel(times);
        timeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        timeLabel.setForeground(new Color(255, 102, 102));

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        card.add(timeLabel, gbc);

        // 4. 수령 버튼 (오른쪽 하단)
        if (state == 1) { // od_state가 1일 때만 수령 버튼 표시
            JButton receiveBtn = new JButton("수령");
            receiveBtn.setPreferredSize(new Dimension(80, 32));
            receiveBtn.setBackground(new Color(42, 193, 188));
            receiveBtn.setForeground(Color.WHITE);
            receiveBtn.setFont(new Font("맑은 고딕", Font.BOLD, 13));

            receiveBtn.addActionListener(e -> {
                if (customerDAO.completeOrder(menu_no)) {
                    JOptionPane.showMessageDialog(this, "맛있게 드세요!", "수령 완료", JOptionPane.INFORMATION_MESSAGE);
                    FrameBase.getInstance(new CustomerConfirmPanel(loginUser));
                } else {
                    JOptionPane.showMessageDialog(this, "수령에 실패했습니다. 다시 시도해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                    FrameBase.getInstance(new CustomerConfirmPanel(loginUser));
                }
            });

            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.insets = new Insets(8, 4, 0, 4);
            card.add(receiveBtn, gbc);
        }
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }

    private void addEmptyLabel(JPanel container, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        label.setForeground(Color.GRAY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        container.add(Box.createVerticalGlue());
        container.add(label);
        container.add(Box.createVerticalGlue());
    }

}