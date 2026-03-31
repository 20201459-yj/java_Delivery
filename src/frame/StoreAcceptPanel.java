package frame;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class StoreAcceptPanel extends JPanel {
    private JTextField timeField;

    public StoreAcceptPanel(String customerRequest) { // 요청사항을 생성자로 받으면 더 좋습니다.
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // 요청사항 (전달받은 데이터 표시)
        JLabel reqLabel = new JLabel("<html><b>[고객 요청사항]</b><br>" + customerRequest + "</html>");
        reqLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel timePanel = new JPanel();
        timePanel.setBackground(Color.WHITE);
        timePanel.add(new JLabel("조리 예상 시간: "));

        timeField = new JTextField("50", 5);
        timeField.setHorizontalAlignment(JTextField.CENTER);
        timePanel.add(timeField);
        timePanel.add(new JLabel("분"));

        contentPanel.add(reqLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(timePanel);

        add(contentPanel, BorderLayout.CENTER);
    }

    // ★ 입력된 시간을 가져오는 Getter
    public String getTimeText() {
        return timeField.getText();
    }
}