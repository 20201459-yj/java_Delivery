package frame;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import api.NaverMap;
import db.EtcDAO;
import db.StoreDAO;
import vo.StoreDTO;

public class StoreSignupPanel extends JPanel {

    // 배민 민트색
    private final Color MINT_COLOR = new Color(42, 193, 188);

    // 버튼 필드 (private으로 내부 접근)
    private JButton btnCustomer;
    private JButton btnOwner;
    private JButton btnJoin;
    private JButton cancelBtn;
    private JButton idCheckBtn; // 중복확인
    private JButton addressCheckBtn; // 주소검색

    // 입력 필드들
    private JTextField nameField;
    private JTextField idField;
    private JPasswordField pwField;
    private JPasswordField pwCheckField;
    private JTextField addressField;
    private JTextField phoneField; // 전화번호

    // 업종 선택 라디오 버튼
    private JRadioButton rbKorean, rbChinese, rbJapanese, rbWestern, rbCafe;
    private ButtonGroup categoryGroup;

    final boolean[] valid = new boolean[8];
    final boolean[] able = new boolean[1];

    public void able() {
        able[0] = true;
        for (boolean v : valid) {
            able[0] &= v;
        }
        btnJoin.setEnabled(able[0]);
    }

    public StoreSignupPanel() {
        for (int i = 0; i < valid.length; i++) {
            valid[i] = false;
        }
        StoreDAO store = new StoreDAO();

        // 1. 패널 기본 설정
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 2. 메인 패널 설정
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // 3. 타이틀
        JLabel titleLabel = new JLabel("회원가입");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 4. [탭 버튼 패널]
        JPanel typePanel = new JPanel(new GridLayout(1, 2, 5, 0));
        typePanel.setMaximumSize(new Dimension(400, 45));
        typePanel.setBackground(Color.WHITE);

        btnCustomer = new JButton("개인 회원");
        styleTabButton(btnCustomer, false);

        btnOwner = new JButton("식당 점주");
        styleTabButton(btnOwner, true);

        typePanel.add(btnCustomer);
        typePanel.add(btnOwner);

        // 5. 컴포넌트 초기화
        nameField = new JTextField();
        styleField(nameField);

        idField = new JTextField();
        styleField(idField);
        idCheckBtn = new JButton("중복확인");
        styleSmallButton(idCheckBtn);

        pwField = new JPasswordField();
        styleField(pwField);

        pwCheckField = new JPasswordField();
        styleField(pwCheckField);

        addressField = new JTextField();
        styleField(addressField);
        addressCheckBtn = new JButton("주소확인");
        styleSmallButton(addressCheckBtn);

        phoneField = new JTextField();
        styleField(phoneField);

        // 업종 선택 라디오 버튼 초기화
        categoryGroup = new ButtonGroup();
        rbKorean = new JRadioButton("한식", true);
        rbChinese = new JRadioButton("중식");
        rbJapanese = new JRadioButton("일식");
        rbWestern = new JRadioButton("양식");
        rbCafe = new JRadioButton("카페");

        styleRadioButton(rbKorean);
        styleRadioButton(rbChinese);
        styleRadioButton(rbJapanese);
        styleRadioButton(rbWestern);
        styleRadioButton(rbCafe);

        categoryGroup.add(rbKorean);
        categoryGroup.add(rbChinese);
        categoryGroup.add(rbJapanese);
        categoryGroup.add(rbWestern);
        categoryGroup.add(rbCafe);

        // 6. 하단 버튼
        btnJoin = new JButton("가입하기");
        btnJoin.setMaximumSize(new Dimension(400, 50));
        btnJoin.setPreferredSize(new Dimension(400, 50));
        btnJoin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnJoin.setBackground(MINT_COLOR);
        btnJoin.setForeground(Color.WHITE);
        btnJoin.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        btnJoin.setFocusPainted(false);
        btnJoin.setBorderPainted(false);
        btnJoin.setEnabled(false);

        cancelBtn = new JButton("취소");
        cancelBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelBtn.setContentAreaFilled(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setForeground(Color.GRAY);
        cancelBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 13));

        // 7. 화면 배치
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        mainPanel.add(typePanel);
        mainPanel.add(Box.createVerticalStrut(20));

        addLabel(mainPanel, "가게 이름");
        mainPanel.add(nameField);
        mainPanel.add(Box.createVerticalStrut(15));

        addLabelWithButton(mainPanel, "아이디", idCheckBtn);
        mainPanel.add(idField);
        mainPanel.add(Box.createVerticalStrut(15));

        addLabel(mainPanel, "비밀번호");
        mainPanel.add(pwField);
        mainPanel.add(Box.createVerticalStrut(15));

        addLabel(mainPanel, "비밀번호 확인");
        mainPanel.add(pwCheckField);
        mainPanel.add(Box.createVerticalStrut(15));

        addLabelWithButton(mainPanel, "가게 주소", addressCheckBtn);
        mainPanel.add(addressField);
        mainPanel.add(Box.createVerticalStrut(15));

        addLabel(mainPanel, "전화번호");
        mainPanel.add(phoneField);
        mainPanel.add(Box.createVerticalStrut(15));

        addLabel(mainPanel, "업종 선택");
        mainPanel.add(createCategoryPanel());
        mainPanel.add(Box.createVerticalStrut(30));

        mainPanel.add(btnJoin);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(cancelBtn);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel, BorderLayout.CENTER);

        // ==========================================================
        // ★ 이벤트 리스너 작성 구역
        // ==========================================================

        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (nameField.getText().length() >= 2 && nameField.getText().length() <= 30) {
                    valid[0] = true;
                } else {
                    valid[0] = false;
                }
                able();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (nameField.getText().length() >= 2 && nameField.getText().length() <= 30) {
                    valid[0] = true;
                } else {
                    valid[0] = false;
                }
                able();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        idField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (idField.getText().length() >= 4 && idField.getText().length() <= 30) {
                    valid[1] = true;
                } else {
                    valid[1] = false;
                }
                valid[2] = false;
                idField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                able();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (idField.getText().length() >= 4 && idField.getText().length() <= 30) {
                    valid[1] = true;
                } else {
                    valid[1] = false;
                }
                valid[2] = false;
                idField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                able();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        idCheckBtn.addActionListener(e -> {
            EtcDAO etc = new EtcDAO();
            String idString = idField.getText();
            boolean check = etc.isExist(idString);
            if (idString.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID를 입력하세요 !!", "입력오류", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (check) {
                JOptionPane.showConfirmDialog(this, "이미 사용중인 ID입니다 !!", "중복 ID", JOptionPane.WARNING_MESSAGE);
                return;
            } else {
                JOptionPane.showConfirmDialog(this, "사용가능한 ID입니다 !!", "중복ID", JOptionPane.OK_OPTION);
                valid[2] = true;
                idField.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                able();
            }
        });

        pwField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (String.valueOf(pwField.getPassword()).length() >= 4
                        && String.valueOf(pwField.getPassword()).length() <= 30) {
                    valid[3] = true;
                } else {
                    valid[3] = false;
                }
                able();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (String.valueOf(pwField.getPassword()).length() >= 4
                        && String.valueOf(pwField.getPassword()).length() <= 30) {
                    valid[3] = true;
                } else {
                    valid[3] = false;
                }
                able();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        pwCheckField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String pw = String.valueOf(pwField.getPassword());
                String pc = String.valueOf(pwCheckField.getPassword());
                if (pw.equals(pc)) {
                    valid[4] = true;
                    pwCheckField.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                } else {
                    valid[4] = false;
                    pwCheckField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                }
                able();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String pw = String.valueOf(pwField.getPassword());
                String pc = String.valueOf(pwCheckField.getPassword());
                if (pw.equals(pc)) {
                    valid[4] = true;
                    pwCheckField.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                } else {
                    valid[4] = false;
                    pwCheckField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                }
                able();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        addressField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (addressField.getText().length() > 0) {
                    valid[5] = true;
                } else {
                    valid[5] = false;
                }
                valid[6] = false;
                addressField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                able();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (addressField.getText().length() > 0) {
                    valid[5] = true;
                } else {
                    valid[5] = false;
                }
                valid[6] = false;
                addressField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                able();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        // 2. 주소 검색
        addressCheckBtn.addActionListener(e -> {
            NaverMap naver = new NaverMap();
            String addressString = addressField.getText();
            boolean check = naver.isValidAddress(addressString);
            if (addressString.isEmpty()) {
                JOptionPane.showMessageDialog(this, "주소를 입력하세요 !!", "입력오류", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (check) {
                JOptionPane.showConfirmDialog(this, "주소 확인이 완료되었습니다!!", "주소확인", JOptionPane.OK_OPTION);
                valid[6] = true;
                addressField.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                able();
                return;
            } else {
                JOptionPane.showConfirmDialog(this, "없는 주소입니다 !!", "주소확인", JOptionPane.WARNING_MESSAGE);
            }
        });

        phoneField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (phoneField.getText().length() > 0) {
                    valid[7] = true;
                } else {
                    valid[7] = false;
                }
                able();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (phoneField.getText().length() > 0) {
                    valid[7] = true;
                } else {
                    valid[7] = false;
                }
                able();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        // 3. 가입하기 버튼
        btnJoin.addActionListener(e -> {

            // 여기에 로직 작성
            String inputId = idField.getText();
            String inputAddress = addressField.getText();
            String inputName = nameField.getText();
            String inputPass = new String(pwField.getPassword());
            String inputPhone = phoneField.getText();
            String inputPass2 = new String(pwField.getPassword());
            String st_type = ""; // 값을 담을 변수

            if (rbKorean.isSelected()) {
                st_type = "한식";
            } else if (rbChinese.isSelected()) {
                st_type = "중식";
            } else if (rbJapanese.isSelected()) {
                st_type = "일식";
            } else if (rbWestern.isSelected()) {
                st_type = "양식";
            } else if (rbCafe.isSelected()) {
                st_type = "카페";
            }

            if (inputId.isEmpty() || inputAddress.isEmpty() || inputName.isEmpty() || inputPass.isEmpty()
                    || inputPass2.isEmpty() || inputPhone.isEmpty()) {
                JOptionPane.showConfirmDialog(this, "공란 없이 모두 입력해주세요 !! ", "회원가입", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // 비밀번호 불일치
            if (!inputPass.equals(inputPass2)) {
                JOptionPane.showConfirmDialog(this, "비밀번호가 일치하지 않습니다", "입력오류", JOptionPane.WARNING_MESSAGE);
                return;
            }
            StoreDTO storeDTO = new StoreDTO(inputId, inputName, inputAddress, inputPhone, st_type);
            store.register(inputId, inputPass, storeDTO);
            JOptionPane.showMessageDialog(this, inputName + "점주님 회원가입이 되었습니다. 축하합니다!! ");
            FrameBase.getInstance(new LoginPanel());
        });

        // 4. 취소 버튼
        cancelBtn.addActionListener(e -> {
            FrameBase.getInstance(new LoginPanel());
        });

        // 5. 고객 회원가입 전환 버튼
        btnCustomer.addActionListener(e -> {
            FrameBase.getInstance(new CustomerSignupPanel());
        });
    }

    // ================= [UI 관련 메서드들] =================

    private JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setMaximumSize(new Dimension(400, 30));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(rbKorean);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(rbChinese);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(rbJapanese);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(rbWestern);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(rbCafe);

        return panel;
    }

    private void styleRadioButton(JRadioButton rb) {
        rb.setBackground(Color.WHITE);
        rb.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        rb.setFocusPainted(false);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void addLabelWithButton(JPanel parentPanel, String labelText, JButton smallBtn) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(400, 25));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 12));

        panel.add(label, BorderLayout.WEST);
        panel.add(smallBtn, BorderLayout.EAST);

        parentPanel.add(panel);
        parentPanel.add(Box.createVerticalStrut(5));
    }

    private void styleSmallButton(JButton btn) {
        btn.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        btn.setBackground(Color.WHITE);
        btn.setForeground(MINT_COLOR);
        btn.setBorder(new LineBorder(MINT_COLOR, 1));
        btn.setPreferredSize(new Dimension(70, 22));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleTabButton(JButton btn, boolean isSelected) {
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btn.setFocusPainted(false);
        if (isSelected) {
            btn.setBackground(MINT_COLOR);
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(MINT_COLOR));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.GRAY);
            btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }
    }

    private void addLabel(JPanel panel, String text) {
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setMaximumSize(new Dimension(400, 20));
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        labelPanel.add(label, BorderLayout.WEST);
        panel.add(labelPanel);
        panel.add(Box.createVerticalStrut(5));
    }

    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(400, 45));
        field.setPreferredSize(new Dimension(400, 45));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setBackground(new Color(245, 245, 245));
        field.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
    }
}