package main;

import frame.FrameBase;
import frame.LoginPanel;

public class JavaDelivery {
    public static void main(String[] args) {
        FrameBase.getInstance(new LoginPanel());
    }
}