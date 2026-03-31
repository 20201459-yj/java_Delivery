import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StoreList extends JFrame {
    private boolean isFavorite = false; // 즐겨찾기 여부(기본은 X)

    public StoreList() {
        setTitle("배달 앱");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JPanel storePanel = new JPanel();
        storePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton starBtn = new JButton("☆");
        starBtn.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        starBtn.setBorderPainted(false);
        starBtn.setContentAreaFilled(false);
        starBtn.setFocusPainted(false);

        JLabel storeName = new JLabel("맛있는 치킨집");
        storeName.setFont(new Font("맑은 고딕", Font.PLAIN, 18));

        starBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isFavorite = !isFavorite; // X -> O, O -> X

                if (isFavorite) {
                    starBtn.setText("★");
                    starBtn.setForeground(Color.ORANGE);
                } else {
                    starBtn.setText("☆");
                    starBtn.setForeground(Color.BLACK);
                }
            }
        });

        storePanel.add(starBtn);
        storePanel.add(storeName);

        add(storePanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new StoreList();
    }
}

// 즐겨찾기 (DB 연결 필요 + 기능 구현만 한 상태)