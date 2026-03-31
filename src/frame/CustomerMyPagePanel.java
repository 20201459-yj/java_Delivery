package frame;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.net.URL;
import java.awt.image.BufferedImage;

import db.CustomerDAO;
import vo.CustomerDTO;
import vo.MenuDTO;
import vo.OrdersDTO;

public class CustomerMyPagePanel extends JPanel {

    private JLabel balanceLabel; // 잔액 업데이트를 위해 전역 변수로 분리

    public CustomerMyPagePanel(CustomerDTO loginUser) {
        CustomerDAO customerDAO = new CustomerDAO();
        // MainPanel의 static 변수에서 유저 정보를 가져옴

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 메인 컨텐츠 패널 (스크롤 가능하도록)
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(0, 5, 5, 5));

        // --- 마이페이지 타이틀 ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setMaximumSize(new Dimension(1000, 35)); // width

        JLabel title = new JLabel("🗂️ Mypage");
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(7, 10, 0, 0)); // 왼쪽 여백

        // --- 설정 버튼 추가 ---
        JButton settingsBtn = new JButton("...");
        settingsBtn.setFont(new Font("맑은고딕", Font.PLAIN, 30));
        settingsBtn.setMargin(new Insets(0, 1, 1, 1));
        settingsBtn.setFocusPainted(false);
        settingsBtn.setBorderPainted(false);
        settingsBtn.setBackground(Color.WHITE);
        settingsBtn.setForeground(Color.black);
        settingsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsBtn.setPreferredSize(new Dimension(60, 10));

        // 클릭 이벤트 → 설정 패널로 이동
        settingsBtn.addActionListener(e -> {
            FrameBase.getInstance(new CustomerOptionPanel(loginUser));
        });

        // 타이틀 왼쪽, 설정 버튼 오른쪽 배치
        titlePanel.add(title, BorderLayout.WEST);
        titlePanel.add(settingsBtn, BorderLayout.EAST);

        content.add(titlePanel);
        content.add(Box.createVerticalStrut(5));

        // --- 로그인 상태일 때 UI ---
        if (loginUser != null) {

            // 2. 환영 메시지 및 정보
            // 왼쪽： 환영 메세지
            JPanel welcomePanel = new JPanel(new BorderLayout());
            welcomePanel.setBackground(Color.WHITE);
            welcomePanel.setMaximumSize(new Dimension(1000, 35));

            JLabel welcomeLabel = new JLabel(loginUser.getCs_name() + "님, 환영합니다!");
            welcomeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            welcomeLabel.setBorder(new EmptyBorder(0, 10, 0, 0)); // 왼쪽 여백
            welcomePanel.add(welcomeLabel, BorderLayout.WEST);
            // 주문내역 + 리뷰관리 버튼 담을 패널
            JPanel btnGroupPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            btnGroupPanel.setBackground(Color.WHITE);

            // 오른쪽: 주문내역 버튼
            JButton orderBtn = new JButton("주문내역");
            orderBtn.setBackground(new Color(42, 193, 188));
            orderBtn.setForeground(Color.WHITE);
            orderBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            orderBtn.setFocusPainted(false);
            orderBtn.setBorderPainted(false);
            orderBtn.setPreferredSize(new Dimension(100, 25));

            // 주문내역 아래에 리뷰 버튼 생성
            JButton reviewBtn = new JButton("리뷰관리");
            reviewBtn.setBackground(new Color(42, 193, 188));
            reviewBtn.setForeground(Color.WHITE);
            reviewBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            reviewBtn.setFocusPainted(false);
            reviewBtn.setBorderPainted(false);
            reviewBtn.setPreferredSize(new Dimension(100, 25));

            // 패널에 두 버튼 추가
            btnGroupPanel.add(orderBtn);
            btnGroupPanel.add(reviewBtn);

            // welcomePanel의 오른쪽에 버튼 그룹 패널 추가
            welcomePanel.add(btnGroupPanel, BorderLayout.EAST);

            // 버튼 클릭 시 BottomTimerOrderPanel 호출
            orderBtn.addActionListener(e -> {
                FrameBase.getInstance(new CustomerConfirmPanel(loginUser));
            });

            reviewBtn.addActionListener(e -> {
                FrameBase.getInstance(new CustomerReviewPanel(loginUser));
            });

            content.add(welcomePanel);
            content.add(Box.createVerticalStrut(0));

            // use 이미지
            ImageIcon userIcon = null;
            try {
                URL imgUrl = getClass().getResource("/img/user.jpg");
                if (imgUrl != null) {
                    userIcon = new ImageIcon(imgUrl);
                    Image img = userIcon.getImage().getScaledInstance(130, 130, Image.SCALE_SMOOTH);
                    userIcon = new ImageIcon(img);
                } else {
                    System.out.println("user.jpg 이미지를 찾을 수 없습니다.");
                    // UI 깨짐 방지
                    userIcon = new ImageIcon(new BufferedImage(130, 130, BufferedImage.TYPE_INT_ARGB));
                }

                JLabel userLabel = new JLabel(userIcon);
                userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                JPanel iconPanel = new JPanel();
                iconPanel.setBackground(Color.WHITE);
                iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.Y_AXIS));
                iconPanel.setMaximumSize(new Dimension(1000, 150));
                iconPanel.add(Box.createVerticalStrut(10));
                iconPanel.add(userLabel);
                iconPanel.add(Box.createVerticalStrut(5));

                content.add(iconPanel);

            } catch (Exception e) {
                e.printStackTrace(); // 에러 로그 출력
                // 빈 패널 추가
                JPanel iconPanel = new JPanel();
                iconPanel.setBackground(Color.WHITE);
                iconPanel.setMaximumSize(new Dimension(1000, 150));
                content.add(iconPanel);
            }

            // --- UID 표시 ---
            JLabel uidLabel = new JLabel(loginUser.getCs_name() + "님 계정");
            uidLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            uidLabel.setForeground(Color.GRAY);
            uidLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            content.add(uidLabel); // ← 여기서 content에 추가
            content.add(Box.createVerticalStrut(1));

            // 연락처 + 룰렛 버튼 패널
            JPanel contactPanel = new JPanel(new GridBagLayout());
            contactPanel.setBackground(Color.WHITE);
            contactPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            GridBagConstraints gbc = new GridBagConstraints();

            // 1. 연락처 (중앙)
            JLabel infoLabel = new JLabel("연락처: " + loginUser.getCs_phone());
            infoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            infoLabel.setForeground(Color.GRAY);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0; // 남는 공간 채움 → 중앙
            gbc.anchor = GridBagConstraints.EAST;
            gbc.fill = GridBagConstraints.NONE;
            gbc.insets = new Insets(0, 0, 0, 70); // 오른쪽 80px 여백
            contactPanel.add(infoLabel, gbc);

            // 2. 룰렛 버튼 (오른쪽 끝)
            JButton rouletteBtn = new JButton("랜덤룰렛");
            rouletteBtn.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            rouletteBtn.setBackground(new Color(255, 153, 0));
            rouletteBtn.setForeground(Color.WHITE);
            rouletteBtn.setFocusPainted(false);
            rouletteBtn.setBorderPainted(false);
            rouletteBtn.setPreferredSize(new Dimension(100, 25));

            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 0; // 버튼은 공간 차지 X
            gbc.anchor = GridBagConstraints.EAST;
            gbc.insets = new Insets(0, 0, 0, 0); // 버튼 여백 없음
            contactPanel.add(rouletteBtn, gbc);

            rouletteBtn.addActionListener(e -> {
                int[] rouletteValues = { 0, 50, 100, 200 };
                Color[] colors = { Color.LIGHT_GRAY, Color.YELLOW, Color.ORANGE, Color.CYAN };
                int n = rouletteValues.length;

                JDialog dialog = new JDialog((Frame) null, "룰렛 돌리는 중...", true);
                dialog.setSize(350, 400);
                dialog.setLayout(new BorderLayout());
                dialog.setLocationRelativeTo(this);

                final double[] angle = { 0 };

                // 1. 랜덤으로 당첨 인덱스 결정
                int finalIndex = (int) (Math.random() * n);
                double sectorSize = 2 * Math.PI / n;

                // 2. 시계 방향 최종 각도 계산
                double extraRotation = 2 * Math.PI * 3;
                double targetAngle = extraRotation + (2 * Math.PI - (finalIndex * sectorSize)) - (sectorSize / 2);

                JPanel wheelPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        int w = getWidth();
                        int h = getHeight();
                        int diameter = Math.min(w, h) - 100;
                        int cx = w / 2;
                        int cy = h / 2;

                        // --- 판 회전 로직 ---
                        g2.rotate(angle[0], cx, cy); // 애니메이션 회전 값
                        g2.rotate(-Math.PI / 2, cx, cy); // 12시 방향을 0도로 맞춤

                        for (int i = 0; i < n; i++) {
                            // 섹터 배경
                            g2.setColor(colors[i % colors.length]);
                            g2.fillArc(cx - diameter / 2, cy - diameter / 2, diameter, diameter,
                                    (int) Math.toDegrees(i * sectorSize), (int) Math.toDegrees(sectorSize) + 1);
                        }

                        // 텍스트는 별도로 그리기 (회전 없이)
                        g2.dispose();
                        g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        for (int i = 0; i < n; i++) {
                            g2.setColor(Color.BLACK);
                            g2.setFont(new Font("맑은 고딕", Font.BOLD, 18));

                            // 현재 회전 상태를 고려한 텍스트 위치 계산
                            double textAngle = angle[0] - Math.PI / 2 + i * sectorSize + sectorSize / 2;
                            int radius = diameter / 3;
                            int x = cx + (int) (radius * Math.cos(textAngle));
                            int y = cy + (int) (radius * Math.sin(textAngle));

                            String text = String.valueOf(rouletteValues[i]);
                            int tw = g2.getFontMetrics().stringWidth(text);
                            int th = g2.getFontMetrics().getAscent();

                            // 텍스트는 항상 정방향으로
                            g2.drawString(text, x - tw / 2, y + th / 2);
                        }

                        g2.dispose();

                        // --- 고정 바늘 그리기 ---
                        g2 = (Graphics2D) g;
                        g2.setColor(Color.RED);
                        int[] px = { cx - 10, cx + 10, cx };
                        int[] py = { cy - diameter / 2 - 20, cy - diameter / 2 - 20, cy - diameter / 2 + 5 };
                        g2.fillPolygon(px, py, 3);
                    }
                };

                dialog.add(wheelPanel, BorderLayout.CENTER);

                // 3. 애니메이션 로직
                Timer timer = new Timer(20, null);
                final int[] step = { 0 };
                final int totalSteps = 80;

                timer.addActionListener(ev -> {
                    step[0]++;
                    double t = (double) step[0] / totalSteps;
                    // Ease-out (점점 느려지게)
                    angle[0] = targetAngle * (1 - Math.pow(1 - t, 3));

                    wheelPanel.repaint();

                    if (step[0] >= totalSteps) {
                        timer.stop();

                        Timer delay = new Timer(1000, e2 -> {
                            int finalPoint = rouletteValues[finalIndex];
                            CustomerDAO dao = new CustomerDAO();
                            boolean success = dao.chargePoint(loginUser, finalPoint);

                            if (success) {
                                loginUser.setCs_balance(loginUser.getCs_balance() + finalPoint); // 이거떔에 포인트 두배로 들어감
                                balanceLabel.setText("현재 잔액: " + loginUser.getCs_balance() + " P");
                                JOptionPane.showMessageDialog(dialog, "당첨 결과: " + finalPoint + " P!", "룰렛 결과",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                            dialog.dispose();
                        });
                        delay.setRepeats(false);
                        delay.start();
                    }
                });

                timer.start();
                dialog.setVisible(true);
            });

            content.add(contactPanel);
            content.add(Box.createVerticalStrut(5));

            // 3. 잔액 표시 및 포인트 충전
            JPanel pointPanel = new JPanel();
            pointPanel.setBackground(Color.WHITE);
            pointPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            pointPanel.setMaximumSize(new Dimension(500, 60));

            balanceLabel = new JLabel("현재 잔액: " + loginUser.getCs_balance() + " P");
            balanceLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
            balanceLabel.setForeground(new Color(0, 102, 204));

            JButton chargeBtn = new JButton("충전하기");
            chargeBtn.setBackground(new Color(42, 193, 188)); // 배민 민트색
            chargeBtn.setForeground(Color.WHITE);
            chargeBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            chargeBtn.setBorderPainted(false);

            // 포인트 충전 로직
            chargeBtn.addActionListener(e -> {
                String input = JOptionPane.showInputDialog(this, "충전할 포인트 금액을 입력하세요:", "포인트 충전",
                        JOptionPane.PLAIN_MESSAGE);
                if (input != null && !input.isEmpty()) {
                    try {
                        int amount = Integer.parseInt(input);
                        if (amount <= 0)
                            throw new NumberFormatException();

                        CustomerDAO dao = new CustomerDAO();
                        boolean success = dao.chargePoint(loginUser, amount);

                        if (success) {
                            balanceLabel.setText("현재 잔액: " + loginUser.getCs_balance() + " P");
                            JOptionPane.showMessageDialog(this, amount + " 포인트가 충전되었습니다!", "충전 완료",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, "충전에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "유효한 숫자만 입력 가능합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            pointPanel.add(balanceLabel);
            pointPanel.add(Box.createHorizontalStrut(10));
            pointPanel.add(chargeBtn);

            content.add(pointPanel);
            content.add(Box.createVerticalStrut(30));

            JButton clear = new JButton("장바구니 비우기");
            clear.setBackground(new Color(42, 193, 188));
            clear.setForeground(Color.WHITE);
            clear.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            clear.setFocusPainted(false);
            clear.setBorderPainted(false);
            clear.setPreferredSize(new Dimension(100, 25));
            clear.setAlignmentX(Component.CENTER_ALIGNMENT);

            clear.addActionListener(e -> {
                loginUser.getCart().clear();
                FrameBase.getInstance(new CustomerMyPagePanel(loginUser));
            });
            // 4. 장바구니 리스트
            JLabel cartTitle = new JLabel("🛒" + "내 장바구니");
            cartTitle.setFont(new Font("Segoe UI Emoji" + "맑은 고딕", Font.BOLD, 18));
            cartTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            content.add(cartTitle);
            content.add(clear);
            content.add(Box.createVerticalStrut(10));

            List<MenuDTO> cart = loginUser.getCart();
            if (cart == null || cart.isEmpty()) {
                JLabel emptyLabel = new JLabel("장바구니가 비어 있습니다.");
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                content.add(emptyLabel);
            } else {
                for (MenuDTO menu : cart) {
                    JPanel itemPanel = new JPanel(new BorderLayout());
                    itemPanel.setMaximumSize(new Dimension(400, 40));
                    itemPanel.setBorder(new LineBorder(new Color(230, 230, 230), 1));
                    itemPanel.setBackground(Color.WHITE);
                    itemPanel.add(new JLabel("  " + menu.getMenu_name()), BorderLayout.WEST);
                    itemPanel.add(new JLabel(menu.getMenu_price() + "원  "), BorderLayout.EAST);

                    content.add(itemPanel);
                    content.add(Box.createVerticalStrut(5));
                }

                // 총 금액 계산
                int totalAmount = cart.stream().mapToInt(MenuDTO::getMenu_price).sum();
                content.add(Box.createVerticalStrut(15));
                JLabel totalLabel = new JLabel("장바구니 총 금액: " + totalAmount + "원");
                totalLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
                totalLabel.setForeground(Color.RED);
                totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                content.add(totalLabel);
            }

        } else {
            // --- 로그인 정보가 없을 때 ---
            JLabel subTitle = new JLabel("로그인 정보가 없습니다.");
            subTitle.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
            subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            content.add(subTitle);

            JButton loginGoBtn = new JButton("로그인하러 가기");
            loginGoBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            loginGoBtn.addActionListener(e -> FrameBase.getInstance(new LoginPanel()));
            content.add(Box.createVerticalStrut(20));
            content.add(loginGoBtn);
        }

        // 스크롤 적용
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20); // 마우스 스크롤 속도 조정
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // 세로 사이드바 두께 0으로 설정(사이드바 안 보이게)
        add(scrollPane, BorderLayout.CENTER);

        // 5. 하단 홈으로 돌아가기 버튼
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        bottomPanel.setBackground(Color.WHITE);

        // 5-1. 주문하기 버튼 생성 및 설정
        JButton orderBtn = new JButton("주문하기");
        orderBtn.setBackground(new Color(42, 193, 188)); // 민트색
        orderBtn.setForeground(Color.WHITE);
        orderBtn.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        orderBtn.setPreferredSize(new Dimension(0, 60)); // 높이 확보
        orderBtn.setBorderPainted(false);
        orderBtn.setFocusPainted(false);

        // 5-2. 홈으로 돌아가기 버튼 생성 및 설정
        JButton backBtn = new JButton("홈으로 돌아가기");
        backBtn.setBackground(new Color(150, 150, 150)); // 구분하기 위해 회색 계열 추천 (선택사항)
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        backBtn.setPreferredSize(new Dimension(0, 60));
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);

        bottomPanel.add(backBtn);
        bottomPanel.add(orderBtn);

        add(bottomPanel, BorderLayout.SOUTH);
        // 홈 버튼 클릭 시 장바구니 초기화 여부는 기존 MyPagePanel1 로직을 따름
        backBtn.addActionListener(e -> {
            // 만약 홈으로 갈 때 장바구니를 비우고 싶다면 아래 주석 해제
            FrameBase.getInstance(new CustomerMainPanel(loginUser));
        });

        orderBtn.addActionListener(e -> {
            List<MenuDTO> list = loginUser.getCart();
            if (list.size() == 0) {
                JOptionPane.showMessageDialog(this, "장바구니가 비어있습니다.", "주문 실패", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String comment = JOptionPane.showInputDialog(this, "주문 요청 사항 : ", "주문 요청 사항",
                    JOptionPane.PLAIN_MESSAGE);
            int success = 0;
            int fail = 0;
            int nebalance = 0;
            boolean[] complete = new boolean[list.size()];
            for (MenuDTO menu : list) {
                OrdersDTO order = new OrdersDTO();
                order.setOd_customer(loginUser.getCs_uid());
                order.setOd_store(menu.getMenu_store());
                order.setOd_content(comment);
                order.setOd_menu(menu.getMenu_no());

                int result = customerDAO.shootOrder(order, loginUser);
                if (result == 1) {
                    success++;
                    complete[list.indexOf(menu)] = true;
                } else if (result == -2) {
                    nebalance++;
                    complete[list.indexOf(menu)] = false;
                } else {
                    fail++;
                    complete[list.indexOf(menu)] = false;
                }
            }

            for (int i = complete.length - 1; i >= 0; i--) {
                if (complete[i]) {
                    list.remove(i);
                }
            }

            JOptionPane.showMessageDialog(this,
                    "주문 성공 : " + success +
                            "주문 실패 : " + fail +
                            "잔액 부족 : " + nebalance,
                    "주문 결과", JOptionPane.INFORMATION_MESSAGE);
            if (fail == 0 && nebalance == 0) {
                FrameBase.getInstance(new CustomerMainPanel(loginUser));
            } else {
                FrameBase.getInstance(new CustomerMyPagePanel(loginUser));
            }
        });
    }
}