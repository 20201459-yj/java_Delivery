package frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import db.ReviewDAO;
import vo.CustomerDTO;
import vo.MenuDTO;
import vo.ReviewDTO;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerReviewWritePanel extends JPanel {
    // 메인 컬러 설정 (보내주신 이미지 참고)
    private Color mainMint = new Color(51, 197, 182); // 민트색
    private Color lightGray = new Color(245, 245, 245); // 배경 연그레이
    private Color darkGray = new Color(128, 128, 128); // 돌아가기 버튼용

    public CustomerReviewWritePanel(CustomerDTO loginCustomer, String st_uid) {
        ReviewDAO reviewDAO = new ReviewDAO();
        List<MenuDTO> reviewableMenus = reviewDAO.reviewableMenu(loginCustomer, st_uid);
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // --- 상단: 제목 및 메뉴 선택 ---
        JPanel topPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        topPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("리뷰 작성하기");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));

        JLabel subLabel = new JLabel("주문하신 메뉴를 선택해 주세요");
        subLabel.setForeground(Color.GRAY);

        // 셀렉트 박스 (JComboBox)
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (MenuDTO menu : reviewableMenus) {
            model.addElement(menu.getMenu_name());
        }
        JComboBox<String> menuSelect = new JComboBox<>(model);
        menuSelect.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        menuSelect.setBackground(Color.WHITE);

        topPanel.add(titleLabel);
        topPanel.add(subLabel);
        topPanel.add(menuSelect);
        topPanel.add(Box.createVerticalStrut(10));

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        // 1. 별점 선택 영역 (라디오 버튼)
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scorePanel.setBackground(Color.WHITE);
        scorePanel.setBorder(new EmptyBorder(10, 0, 5, 0));

        JLabel scoreLabel = new JLabel("평점: ");
        scoreLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        scorePanel.add(scoreLabel);

        // 라디오 버튼 그룹화 (하나만 선택되게)
        ButtonGroup scoreGroup = new ButtonGroup();
        JRadioButton[] scoreButtons = new JRadioButton[5];

        for (int i = 0; i < 5; i++) {
            scoreButtons[i] = new JRadioButton((i + 1) + "점");
            scoreButtons[i].setBackground(Color.WHITE);
            scoreButtons[i].setFont(new Font("맑은 고딕", Font.PLAIN, 13));
            if (i == 4)
                scoreButtons[i].setSelected(true); // 기본값 5점

            scoreGroup.add(scoreButtons[i]);
            scorePanel.add(scoreButtons[i]);
        }

        // 2. 리뷰 내용 입력
        JLabel contentLabel = new JLabel("솔직한 후기를 남겨주세요!");
        contentLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        contentLabel.setBorder(new EmptyBorder(10, 0, 5, 0));

        JTextArea reviewContent = new JTextArea();
        reviewContent.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        reviewContent.setLineWrap(true);
        reviewContent.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        JScrollPane scrollPane = new JScrollPane(reviewContent);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20); // 마우스 스크롤 속도 조정
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // 세로 사이드바 두께 0으로 설정(사이드바 안 보이게)

        // 센터 패널에 배치
        JPanel centerGrid = new JPanel(new BorderLayout());
        centerGrid.setBackground(Color.WHITE);
        centerGrid.add(scorePanel, BorderLayout.NORTH); // 별점이 위로
        centerGrid.add(scrollPane, BorderLayout.CENTER); // 입력창이 아래로

        centerPanel.add(contentLabel, BorderLayout.NORTH);
        centerPanel.add(centerGrid, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // --- 하단: 버튼 영역 ---
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setPreferredSize(new Dimension(0, 50));
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnBack = new JButton("이전으로");
        btnBack.setBackground(darkGray);
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        btnBack.setBorderPainted(false);

        JButton btnSubmit = new JButton("리뷰 등록하기");
        btnSubmit.setBackground(mainMint);
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        btnSubmit.setBorderPainted(false);

        bottomPanel.add(btnBack);
        bottomPanel.add(btnSubmit);

        add(bottomPanel, BorderLayout.SOUTH);

        btnSubmit.addActionListener(e -> {

            ReviewDTO review = new ReviewDTO();
            review.setRv_menu(reviewableMenus.get(menuSelect.getSelectedIndex()).getMenu_no());
            review.setRv_customer(loginCustomer.getCs_uid());
            review.setRv_content(reviewContent.getText());
            for (int i = 0; i < scoreButtons.length; i++) {
                if (scoreButtons[i].isSelected()) {
                    review.setRv_score(i + 1);
                }
            }
            if (reviewDAO.addReview(review)) {
                JOptionPane.showMessageDialog(this, "리뷰를 등록했습니다.", "리뷰 등록 성공", JOptionPane.INFORMATION_MESSAGE);
                FrameBase.getInstance(new CustomerReviewPanel(loginCustomer));
            } else {
                JOptionPane.showMessageDialog(this, "리뷰 등록에 실패했습니다.", "리뷰 등록 실패", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnBack.addActionListener(e -> FrameBase.getInstance(new CustomerReviewPanel(loginCustomer)));
    }

    public static void main(String[] args) {
        // 1. 테스트를 위한 가짜 로그인 정보 생성
        CustomerDTO dummyCustomer = new CustomerDTO();
        dummyCustomer.setCs_uid("C1"); // DB에 존재하는 UID를 넣거나 테스트용으로 설정

        String testStoreUid = "S1"; // 테스트용 가게 UID

        // 2. 메인 프레임 설정
        JFrame frame = new JFrame("리뷰 작성 테스트");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 700);
        frame.setLocationRelativeTo(null);

        // 3. 리뷰 패널 생성 및 추가
        // 수정하신 생성자 조건(CustomerDTO, String)에 맞춰 인자를 전달합니다.
        CustomerReviewWritePanel reviewPanel = new CustomerReviewWritePanel(dummyCustomer, testStoreUid);
        frame.add(reviewPanel);

        // 4. 화면 표시
        frame.setVisible(true);
    }
}