package frame;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.List;
import db.CustomerDAO;
import db.ReviewDAO;
import vo.CustomerDTO;
import vo.ReviewDTO;

public class CustomerOptionPanel extends JPanel {

    public CustomerOptionPanel(CustomerDTO loginUser) {
        ReviewDAO reviewDAO = new ReviewDAO();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- 1. 타이틀 + 뒤로가기 버튼 ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(new EmptyBorder(10, 10, 10, 0));

        // 타이틀
        JLabel title = new JLabel("환경설정");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        // 뒤로가기 버튼 + 이모지
        JButton backBtn = new JButton("뒤로가기 ◀️");
        styleButton(backBtn, new Color(150, 150, 150));
        backBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        backBtn.setBorder(new EmptyBorder(5, 10, 5, 0)); // 버튼 안쪽 여백
        backBtn.setPreferredSize(null); // 텍스트에 맞게 크기 자동 조절
        backBtn.setMaximumSize(new Dimension(100, 30)); // 최대 너비 제한
        backBtn.addActionListener(e -> FrameBase.getInstance(new CustomerMyPagePanel(loginUser)));

        // 타이틀과 버튼 배치
        titlePanel.add(title, BorderLayout.WEST);
        titlePanel.add(backBtn, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH); 



        // --- 3. 로그아웃 / 회원탈퇴 버튼 ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        JButton logoutBtn = new JButton("로그아웃");
        styleButton(logoutBtn, new Color(42, 193, 188));
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "로그아웃 하시겠습니까?", "로그아웃", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                FrameBase.getInstance(new LoginPanel());
            }
        });

        JButton deleteBtn = new JButton("회원탈퇴");
        styleButton(deleteBtn, new Color(220, 50, 50));
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "정말 회원탈퇴 하시겠습니까?", "회원탈퇴", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                CustomerDAO dao = new CustomerDAO();
                boolean success = dao.deleteCustomer(loginUser.getCs_uid());
                if (success) {
                    JOptionPane.showMessageDialog(this, "회원탈퇴 완료");
                    FrameBase.getInstance(new LoginPanel());
                } else {
                    JOptionPane.showMessageDialog(this, "회원탈퇴 실패", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // [수정된 부분] 버튼들을 수직 중앙으로 밀어넣기 위해 Glue 추가
        buttonPanel.add(Box.createVerticalGlue()); 
        buttonPanel.add(logoutBtn);
        buttonPanel.add(Box.createVerticalStrut(15)); // 버튼 사이 간격 조금 넓힘
        buttonPanel.add(deleteBtn);
        buttonPanel.add(Box.createVerticalGlue());

        // --- 5. 메인 조립 ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        centerPanel.add(buttonPanel, BorderLayout.CENTER); 

        add(centerPanel, BorderLayout.CENTER);

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
