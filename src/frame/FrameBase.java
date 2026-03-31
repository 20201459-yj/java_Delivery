package frame;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class FrameBase extends JFrame {

    private static FrameBase instance; // 싱글톤 기법 : 객체를 단 하나만 생성

    private FrameBase(JPanel panel) { // 판넬을 기능에 따라 바꿔끼우기
        Toolkit tk = Toolkit.getDefaultToolkit();
        int x = (int) (tk.getScreenSize().getWidth()) / 2 - 250;
        int y = (int) (tk.getScreenSize().getHeight()) / 2 - 400;

        add(panel);
        setBounds(x, y, 500, 800);
        setVisible(true);
    }

    public static FrameBase getInstance(JPanel panel) {
        if (instance == null) {
            instance = new FrameBase(panel); // getInstance 메서드 호출 시, 정적변수 instance 참조변수에 객체 주소값 할당
        }

        instance.getContentPane().removeAll(); // 프레임에 있는 기존 요소 제거
        instance.getContentPane().add(panel); // 새로운 화면 추가
        instance.revalidate(); // 레이아웃 갱신
        instance.repaint(); // 화면 갱신
        instance.setVisible(true);

        return instance;
    }
}
