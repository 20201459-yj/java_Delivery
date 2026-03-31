package frame;

import java.awt.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.List;
import java.awt.image.BufferedImage;

import vo.CustomerDTO;
import api.NaverMap;
import vo.StoreDTO;
import db.CustomerDAO;
import vo.MenuDTO;
import vo.LocationXY;
import db.ReviewDAO;
import vo.ReviewDTO;

public class CustomerMainPanel extends JPanel {
    private int currentBannerIdx = 0;
    Color BG = new Color(245, 246, 247);

    // DB 및 API 객체 생성
    NaverMap naverMap = new NaverMap();
    CustomerDAO customerDAO = new CustomerDAO();
    ReviewDAO reviewDAO = new ReviewDAO();

    private JPanel listPanel;
    private JLabel titleLabel;
    private CustomerDTO loginUser;

    public CustomerMainPanel(CustomerDTO loginUser) {
        this.loginUser = loginUser;
        setLayout(new BorderLayout());
        setBackground(BG);
        // 식당 리스트 라벨
        titleLabel = new JLabel("내 주변 맛집");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // 식당 리스트
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        // --- 1. 헤더 (주소 + 마이페이지 버튼) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        String addr = (loginUser != null && loginUser.getCs_address() != null && !loginUser.getCs_address().isEmpty())
                ? loginUser.getCs_address()
                : "주소가 없습니다!";
        JLabel addressLabel = new JLabel("🏠" + addr + " ▼");
        addressLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        JButton myPageBtn = new JButton("마이페이지");
        styleMintButton(myPageBtn);
        // [페이지 이동 로직] FrameBase의 싱글톤 기법 활용
        myPageBtn.addActionListener(e -> {
            if (loginUser == null) {
                JOptionPane.showMessageDialog(this, "로그인이 필요합니다.");
                FrameBase.getInstance(new LoginPanel());
            } else {
                FrameBase.getInstance(new CustomerMyPagePanel(loginUser));
            }
        });

        headerPanel.add(addressLabel, BorderLayout.WEST);
        headerPanel.add(myPageBtn, BorderLayout.EAST);

        // --- 2. 스크롤 가능한 컨텐츠 바디 ---
        JPanel mainBody = new JPanel(new BorderLayout());
        mainBody.setBackground(BG);

        JPanel fixedArea = new JPanel();
        fixedArea.setLayout(new BoxLayout(fixedArea, BoxLayout.Y_AXIS));
        fixedArea.setBackground(BG);

        // 검색바
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setMaximumSize(new Dimension(500, 45));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField searchField = new JTextField("식당 검색");
        JButton searchBtn = new JButton("검색");
        styleMintButton(searchBtn);
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(searchBtn);

        // 검색바 소멸
        searchField.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(" 먹고 싶은 메뉴를 검색해보세요!")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(" 먹고 싶은 메뉴를 검색해보세요!");
                    searchField.setForeground(Color.GRAY);
                }
            }

        });
        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty() || keyword.equals(" 찾고 싶은 식당을 검색해보세요!"))
                return;

            listPanel.removeAll();
            List<StoreDTO> searchList = customerDAO.selectAllStores(keyword); // DB에서 키워드 검색

            for (StoreDTO store : searchList) {
                // 1. API를 이용한 실제 소요 시간 계산
                LocationXY storeLoc = naverMap.getLocation(store.getSt_address());
                LocationXY userLoc = naverMap.getLocation(loginUser.getCs_address());

                if (storeLoc != null && userLoc != null) {
                    // API에서 분 단위 소요 시간 추출
                    int minutes = naverMap.getDuration(storeLoc, userLoc);
                    String avgScore = getStoreAverageScore(store.getSt_uid());

                    // 2. 계산된 시간을 카드로 추가 (예: minutes + "분")
                    if (minutes > 0 && minutes <= 30) { // 30분 이내 가게만 표시
                        addRestaurantCard(listPanel, store.getSt_name(), avgScore, minutes + "분", Color.WHITE, store);
                    }
                }
            }

            listPanel.revalidate();
            listPanel.repaint();
        });

        // 이미지 슬라이드 배너
        String[] imagePaths = { "/img/coupon1.jpg", "/img/coupon2.jpg", "/img/coupon3.jpg" }; // '/'로 클래스패스 기준 절대 경로
        ImageIcon[] bannerIcons = new ImageIcon[imagePaths.length];

        for (int i = 0; i < imagePaths.length; i++) {
            URL imgURL = getClass().getResource(imagePaths[i]); // 클래스패스 기준 절대 경로
            if (imgURL != null) {
                Image img = new ImageIcon(imgURL).getImage().getScaledInstance(480, 150, Image.SCALE_SMOOTH);
                bannerIcons[i] = new ImageIcon(img);
            } else {
                System.out.println("⚠️ 이미지 못 찾음: " + imagePaths[i]);
                // 임시 빈 이미지로 UI 깨짐 방지
                BufferedImage emptyImg = new BufferedImage(480, 150, BufferedImage.TYPE_INT_ARGB);
                bannerIcons[i] = new ImageIcon(emptyImg);
            }
        }

        JLabel bannerLabel = new JLabel();
        bannerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (bannerIcons.length > 0 && bannerIcons[0] != null)
            bannerLabel.setIcon(bannerIcons[0]);

        Timer timer = new Timer(3000, e -> {
            if (bannerIcons.length > 0 && bannerIcons[0] != null) {
                currentBannerIdx = (currentBannerIdx + 1) % bannerIcons.length;
                bannerLabel.setIcon(bannerIcons[currentBannerIdx]);
            }
        });
        timer.start();

        // 카테고리 영역
        JPanel categoryPanel = new JPanel(new GridLayout(1, 5, 15, 15));
        categoryPanel.setBackground(Color.WHITE);
        categoryPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 25, 10, 25),
                BorderFactory.createLineBorder(new Color(230, 230, 230))));

        String[] categories = { "한식", "중식", "일식", "양식", "카페" };
        for (String cat : categories) {
            String emojiTemp = "";
            switch (cat) {
                case "한식":
                    emojiTemp = "🍚";
                    break;
                case "중식":
                    emojiTemp = "🍜";
                    break;
                case "일식":
                    emojiTemp = "🍣";
                    break;
                case "양식":
                    emojiTemp = "🍝";
                    break;
                case "카페":
                    emojiTemp = "☕";
                    break;

            }
            String emoji = emojiTemp;
            JButton btn = new JButton(
                    "<html><center style='line-height:1.2'>" +
                            "<span style='font-size:18px'>" + emoji + "</span><br>" +
                            "<span style='font-size:12px'>" + cat + "</span>" +
                            "</center></html>");
            btn.setBackground(Color.WHITE); // 버튼 배경
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220))); // 버튼 테두리
            btn.setFont(new Font("맑은 고딕", Font.BOLD, 12));

            btn.addActionListener(e -> {
                updateListByCategory(cat, emoji);
            });

            categoryPanel.add(btn);
        }

        // 스크롤
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(245, 246, 247));
        scrollPane.setBackground(new Color(245, 246, 247));
        scrollPane.getVerticalScrollBar().setUnitIncrement(20); // 마우스 스크롤 속도 조정
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // 세로 사이드바 두께 0으로 설정(사이드바 안 보이게)

        // 고정패널영역
        fixedArea.add(Box.createVerticalStrut(10)); // 상단 여백
        fixedArea.add(searchPanel);
        fixedArea.add(Box.createVerticalStrut(10)); // 검색 배너 간격
        fixedArea.add(bannerLabel);
        fixedArea.add(Box.createVerticalStrut(5)); // 사진-버튼 간격
        fixedArea.add(categoryPanel);
        fixedArea.add(Box.createVerticalStrut(8));
        fixedArea.add(titleLabel);
        fixedArea.add(Box.createVerticalStrut(8));

        // main
        mainBody.add(fixedArea, BorderLayout.NORTH);
        mainBody.add(scrollPane, BorderLayout.CENTER);

        // main 추가
        add(headerPanel, BorderLayout.NORTH);
        add(mainBody, BorderLayout.CENTER);

        updateListByCategory("전체", "✨");

    }

    private String getStoreAverageScore(String stUid) {
        List<ReviewDTO> reviews = reviewDAO.getReviewByStore(stUid);

        if (reviews == null || reviews.isEmpty()) {
            return "0.0"; // 리뷰가 없으면 0.0점
        }

        double total = 0;
        for (ReviewDTO r : reviews) {
            total += r.getRv_score();
        }

        double avg = total / reviews.size();
        return String.format("%.1f", avg); // 소수점 한 자리까지 포맷팅
    }
    private void updateListByCategory(String category, String emoji) {
        // 1. 로그인 및 주소 체크 (거리 계산을 위해 필수)
        if (loginUser == null || loginUser.getCs_address() == null || loginUser.getCs_address().isEmpty()) {
            JOptionPane.showMessageDialog(this, "거리 계산을 위해 내 주소 설정(로그인)이 필요합니다.");
            return;
        }

        // 타이틀 변경
        titleLabel.setText(category.equals("전체") ? "✨ 주변 맛집 리스트" : emoji + " " + category + " 맛집 리스트");

        // 2. 리스트 초기화
        listPanel.removeAll();

        // 3. NaverMap API를 활용해 30분 이내 가게만 가져오기
        // showStore 내부에서 이미 30분 이내 필터링이 완료된 리스트를 반환합니다.
        List<StoreDTO> results = naverMap.showStore(category, loginUser);

        // 4. 결과 출력
        if (results == null || results.isEmpty()) {
            JLabel emptyLabel = new JLabel("근처 30분 이내에 " + category + " 맛집이 없습니다. 😥");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createVerticalStrut(20));
            listPanel.add(emptyLabel);
        } else {
            LocationXY userLoc = naverMap.getLocation(loginUser.getCs_address());

            for (StoreDTO store : results) {
                LocationXY storeLoc = naverMap.getLocation(store.getSt_address());

                if (storeLoc != null && userLoc != null) {
                    // 실시간 소요 시간 재계산 (분 단위)
                    int minutes = naverMap.getDuration(storeLoc, userLoc);
                    String durationText = (minutes > 0) ? minutes + "분" : "계산불가";
                    String avgScore = getStoreAverageScore(store.getSt_uid());

                    // 카드 추가
                    addRestaurantCard(listPanel, store.getSt_name(), avgScore, durationText, Color.WHITE, store);
                }
            }
        }

        // 5. 화면 갱신
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void styleMintButton(JButton btn) {
        btn.setBackground(new Color(42, 193, 188));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 12));
    }

    private void addRestaurantCard(JPanel parent, String name, String star, String time, Color bgColor,
            StoreDTO store) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
                new EmptyBorder(15, 10, 15, 10)));

        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // 로그인 정보가 있을 때 DB에서 즐겨찾기 상태 확인
        boolean isFav = false;
        if (loginUser != null && store.getSt_uid() != null) {
            isFav = customerDAO.isFavorite(loginUser.getCs_uid(), store.getSt_uid());
        }

        JButton favBtn = new JButton(isFav ? "★" : "☆");
        favBtn.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        favBtn.setForeground(new Color(255, 204, 0)); // 황금색 별
        favBtn.setBorderPainted(false);
        favBtn.setContentAreaFilled(false);
        favBtn.setFocusPainted(false);

        // 즐겨찾기 클릭 이벤트 처리
        favBtn.addActionListener(e -> {
            // 만약 비로그인 상태로 클릭할 시
            if (loginUser == null) {
                JOptionPane.showMessageDialog(this, "로그인이 필요한 기능입니다.");
                return;
            }
            // DB 상태 토글 (INSERT 또는 DELETE 수행)
            boolean result = customerDAO.toggleFavorite(loginUser.getCs_uid(), store.getSt_uid());
            // 토글 결과에 따라 아이콘 변경
            favBtn.setText(result ? "★" : "☆");
        });

        /*
         * =========================
         * ★ [핵심 수정 1]
         * BoxLayout → GridLayout
         * =========================
         */
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 6)); // ★ 변경
        infoPanel.setBackground(Color.WHITE);

        JPanel nameWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        nameWrapper.setBackground(Color.WHITE);

        /*
         * =========================
         * 가게 이름 (HTML 제거)
         * =========================
         */
        JLabel nameLabel = new JLabel(name); // ★ HTML 제거
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        nameLabel.setForeground(new Color(51, 51, 51));

        nameWrapper.add(nameLabel);
        nameWrapper.add(favBtn);

        /*
         * =========================
         * ★ [핵심 수정 2]
         * 별점 / 시간 분리
         * =========================
         */
        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); // ★ 추가
        metaPanel.setBackground(Color.WHITE);

        JLabel starLabel = new JLabel("★ " + star);
        starLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        starLabel.setForeground(new Color(255, 153, 0));

        ImageIcon bikeIcon = new ImageIcon(
                new ImageIcon(
                        getClass().getClassLoader().getResource("img/motorcycle.png")).getImage()
                        .getScaledInstance(16, 16, Image.SCALE_SMOOTH));

        JLabel timeLabel = new JLabel(time, bikeIcon, JLabel.LEFT);
        timeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        timeLabel.setForeground(new Color(102, 102, 102));
        timeLabel.setIconTextGap(4);

        metaPanel.add(starLabel);
        metaPanel.add(timeLabel);

        infoPanel.add(nameWrapper);
        infoPanel.add(metaPanel);

        /*
         * =========================
         * 가게보기 버튼 (기존 유지)
         * =========================
         */
        JButton menuBtn = new JButton("가게보기");
        menuBtn.setPreferredSize(new Dimension(90, 80));
        menuBtn.setBackground(new Color(235, 248, 248));
        menuBtn.setForeground(new Color(42, 193, 188));
        menuBtn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        menuBtn.setBorderPainted(false);
        menuBtn.setFocusPainted(false);
        menuBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        menuBtn.addActionListener(e -> {
            if (store.getSt_uid() == null) {
                JOptionPane.showMessageDialog(this, "가게 상세 정보를 불러올 수 없습니다.");
            } else {
                showMenuDialog(store);
            }
        });

        /*
         * =========================
         * 조립
         * =========================
         */
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(menuBtn, BorderLayout.EAST);

        parent.add(card);
    }

    // ★ [추가] 메뉴 리스트 팝업창
    private void showMenuDialog(StoreDTO store) {
        Dialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), store.getSt_name() + " 상세 정보",
                true);
        dialog.setSize(450, 750);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout()); // 전체 레이아웃 설정

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // DB에서 메뉴 가져오기
        List<MenuDTO> menus = customerDAO.getMenus(store.getSt_uid());

        if (menus == null || menus.isEmpty()) {
            menuPanel.add(new JLabel("등록된 메뉴가 없습니다."));
        } else {
            for (MenuDTO menu : menus) {
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(Color.WHITE);
                row.setMaximumSize(new Dimension(350, 50));
                row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

                JLabel mName = new JLabel(" " + menu.getMenu_name() + " (" + menu.getMenu_price() + "원)");
                mName.setFont(new Font("맑은 고딕", Font.BOLD, 14));

                JButton addCartBtn = new JButton("담기");
                styleMintButton(addCartBtn);

                addCartBtn.addActionListener(e -> {
                    if (loginUser != null) {
                        loginUser.getCart().add(menu);
                        JOptionPane.showMessageDialog(dialog, "장바구니에 담았습니다!");
                    }
                });

                row.add(mName, BorderLayout.CENTER);
                row.add(addCartBtn, BorderLayout.EAST);
                menuPanel.add(row);
                menuPanel.add(Box.createVerticalStrut(10));
            }
        }

        JPanel reviewSection = new JPanel(new BorderLayout());
        reviewSection.setBackground(Color.WHITE);
        reviewSection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "고객 리뷰", 0, 0, new Font("맑은 고딕", Font.BOLD, 14)));

        JPanel reviewListPanel = new JPanel();
        reviewListPanel.setLayout(new BoxLayout(reviewListPanel, BoxLayout.Y_AXIS));
        reviewListPanel.setBackground(Color.WHITE);
        // 가게보기 -> 메뉴 밑에 고객 리뷰 DB연결
        ReviewDAO reviewDAO = new ReviewDAO();
        List<ReviewDTO> reviewDTO = reviewDAO.getReviewByStore(store.getSt_uid());

        if (reviewDTO.isEmpty()) {
            reviewListPanel.add(new JLabel("아직 리뷰가 없습니다."));
        } else {
            for (ReviewDTO r : reviewDTO) {
                addReviewCard(
                        reviewListPanel,
                        r.getCs_name(),
                        "⭐ " + r.getRv_score(),
                        r.getRv_content());
            }
        }

        JScrollPane reviewScroll = new JScrollPane(reviewListPanel);
        reviewScroll.setPreferredSize(new Dimension(400, 300));
        reviewScroll.getVerticalScrollBar().setUnitIncrement(20); // 숫자가 클수록 빨라집니다.
        reviewScroll.setBorder(null);
        reviewSection.add(reviewScroll, BorderLayout.CENTER);

        // 3. 전체 조립: 상단 메뉴(스크롤) + 하단 리뷰(스크롤)
        JScrollPane menuScroll = new JScrollPane(menuPanel);
        menuScroll.setBorder(null);

        dialog.add(menuScroll, BorderLayout.CENTER); // 메뉴가 메인
        dialog.add(reviewSection, BorderLayout.SOUTH); // 리뷰는 하단에 고정 혹은 배치

        // ★ [중요] 모든 조립이 끝난 후 단 한 번만 호출
        dialog.add(new JScrollPane(menuPanel));
        dialog.setVisible(true);
    }

    private void addReviewCard(JPanel parent, String user, String star, String comment) {
        // 1. 메인 카드 패널 (둥근 테두리 느낌을 위한 외곽 설정)
        JPanel card = new JPanel(new BorderLayout());
        Color reviewBg = new Color(245, 245, 245);
        card.setBackground(reviewBg);
        // 외곽 테두리: 연한 회색 실선과 내부 여백(Padding) 추가
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 235, 235), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        // 카드의 최대 높이 설정 (글자가 많아질 수 있으므로 약간 여유 있게)
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        // 2. 상단: 작성자 및 별점 영역
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(reviewBg);

        // 유저 아이디 (굵게) 및 별점 (주황색 강조)
        JLabel userLabel = new JLabel("<html><b style='color:#333333; font-size:14px;'>" + user + "</b>" +
                " <span style='color:#FF9900; font-size:10px;'>" + star + "</span></html>");

        header.add(userLabel, BorderLayout.WEST);

        // 3. 중앙: 리뷰 본문 (폰트 크기 및 색상 조절)
        JLabel commentLabel = new JLabel("<html><div style='width: 260px;'>" + comment + "</div></html>");
        commentLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        commentLabel.setForeground(new Color(60, 60, 60)); // 완전 검정보다 진회색이 세련됨

        // 구성 요소 배치
        card.add(header, BorderLayout.NORTH);
        card.add(Box.createVerticalStrut(8), BorderLayout.CENTER); // 간격 추가
        card.add(commentLabel, BorderLayout.SOUTH);

        // 부모 패널에 카드 추가 및 카드 사이 간격
        parent.add(card);
        parent.add(Box.createVerticalStrut(12));
    }

}