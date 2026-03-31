package frame;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import db.CustomerDAO;
import db.EtcDAO;
import db.StoreDAO;
import vo.CustomerDTO;
import vo.StoreDTO;

// ★ JFrame 대신 JPanel 상속
public class LoginPanel extends JPanel {

    // 나중에 이벤트를 연결하기 위해 버튼을 멤버 변수로 뺌
    private JButton loginBtn;
    private JButton joinBtn;

    public LoginPanel() {

        StoreDAO storeDAO = new StoreDAO();
        EtcDAO etc = new EtcDAO();
        CustomerDAO customerDAO = new CustomerDAO();

        // --- 1. 패널 기본 설정 ---
        // 창(Frame) 설정(setTitle, setSize 등)은 제거하고
        // 패널 자체가 꽉 차게 보이도록 Layout 설정
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- 2. 메인 패널 설정 (기존 레이아웃 유지) ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(50, 40, 50, 40)); // 상하좌우 여백

        // --- 3. 로고 (텍스트) ---
        JLabel logoLabel = new JLabel("JAVA Delivery");
        logoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- 4. 입력창들 ---
        // 아이디
        JTextField idField = new JTextField();
        styleField(idField);

        // 비밀번호 (글자 가려짐)
        JPasswordField pwField = new JPasswordField();
        styleField(pwField);

        // --- 5. 버튼들 ---
        // 로그인 버튼 (민트색)
        loginBtn = new JButton("로그인");
        loginBtn.setMaximumSize(new Dimension(400, 50));
        loginBtn.setPreferredSize(new Dimension(400, 50));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setBackground(new Color(42, 193, 188)); // 민트색
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);

        // 회원가입 버튼 (투명)
        joinBtn = new JButton("회원가입");
        joinBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        joinBtn.setContentAreaFilled(false); // 배경 투명
        joinBtn.setBorderPainted(false); // 테두리 없음
        joinBtn.setForeground(Color.GRAY);
        joinBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 13));

        // --- 6. 화면에 차곡차곡 쌓기 ---
        mainPanel.add(Box.createVerticalGlue()); // 위쪽 빈 공간
        mainPanel.add(logoLabel);

        mainPanel.add(Box.createVerticalStrut(40));

        // 아이디 라벨 & 필드
        // (정렬을 위해 임시 패널을 쓰거나, 지금처럼 해도 됨. 여기선 정렬을 맞추기 위해 왼쪽 정렬 라벨을 추가하는 것이 좋으나 기존 코드 유지)
        JLabel idLabel = new JLabel("아이디");
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 중앙 정렬로 통일
        mainPanel.add(idLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(idField);

        mainPanel.add(Box.createVerticalStrut(15));

        JLabel pwLabel = new JLabel("비밀번호");
        pwLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(pwLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(pwField);

        mainPanel.add(Box.createVerticalStrut(30));

        mainPanel.add(loginBtn);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(joinBtn);

        mainPanel.add(Box.createVerticalGlue()); // 아래쪽 빈 공간

        // ★ JPanel에 mainPanel 부착
        add(mainPanel, BorderLayout.CENTER);

        // 회원가입 버튼 클릭시 회원가입 패널이동
        joinBtn.addActionListener(e -> {
            FrameBase.getInstance(new CustomerSignupPanel());
        });

        loginBtn.addActionListener(e -> {
            String inputId = idField.getText();
            String inputPass = new String(pwField.getPassword());
            if (etc.who(inputId, inputPass) != null &&
                    etc.who(inputId, inputPass).equals("S")) {
                StoreDTO loginStore = storeDAO.login(inputId, inputPass);
                if (loginStore == null) {
                    JOptionPane.showMessageDialog(this, "존재하지 않는 회원이거나 비밀번호가 일치하지 않습니다 !!", "로그인 실패",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(this, loginStore.getSt_name() + "님 환영합니다", "로그인 성공",
                        JOptionPane.INFORMATION_MESSAGE);
                FrameBase.getInstance(new StoreOrderPanel(loginStore));// 점주 주문목록으로 이동

            } else if (etc.who(inputId, inputPass) != null &&
                    etc.who(inputId, inputPass).equals("C")) {
                CustomerDTO loginCustomer = customerDAO.login(inputId, inputPass);
                if (loginCustomer == null) {
                    JOptionPane.showMessageDialog(this, "존재하지 않는 회원이거나 비밀번호가 일치하지 않습니다 !!", "로그인 실패",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(this, loginCustomer.getCs_name() + "님 환영합니다", "로그인 성공",
                        JOptionPane.INFORMATION_MESSAGE);
                FrameBase.getInstance(new CustomerMainPanel(loginCustomer));// 메인으로 이동함
            } else {
                JOptionPane.showMessageDialog(this, "존재하지 않는 회원이거나 비밀번호가 일치하지 않습니다 !!", "로그인 실패",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

        });

    }

    // 텍스트 필드 디자인 함수
    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(400, 45));
        field.setPreferredSize(new Dimension(400, 45));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setBackground(new Color(245, 245, 245));
        field.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
    }
}