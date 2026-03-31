package vo;

import java.util.ArrayList;
import java.util.List;

public class CustomerDTO {
    String cs_uid;
    String cs_name;
    String cs_address;
    String cs_phone;
    int cs_balance;
    private List<MenuDTO> cart = new ArrayList<MenuDTO>(); // 장바구니 리스트 추가

    public List<MenuDTO> getCart() {
        return this.cart;
    }

    public void setCart(List<MenuDTO> cart) {
        this.cart = cart;
    }

    public CustomerDTO() {
    }

    public CustomerDTO(String cs_uid, String cs_name, String cs_address, String cs_phone) {
        this.cs_uid = cs_uid;
        this.cs_name = cs_name;
        this.cs_address = cs_address;
        this.cs_phone = cs_phone;
    }

    public CustomerDTO(String cs_uid, String cs_name, String cs_address, String cs_phone, int cs_balance) {
        this.cs_uid = cs_uid;
        this.cs_name = cs_name;
        this.cs_address = cs_address;
        this.cs_phone = cs_phone;
        this.cs_balance = cs_balance;
    }

    public String getCs_uid() {
        return this.cs_uid;
    }

    public void setCs_uid(String cs_uid) {
        this.cs_uid = cs_uid;
    }

    public String getCs_name() {
        return this.cs_name;
    }

    public void setCs_name(String cs_name) {
        this.cs_name = cs_name;
    }

    public String getCs_address() {
        return this.cs_address;
    }

    public void setCs_address(String cs_address) {
        this.cs_address = cs_address;
    }

    public String getCs_phone() {
        return this.cs_phone;
    }

    public void setCs_phone(String cs_phone) {
        this.cs_phone = cs_phone;
    }

    public int getCs_balance() {
        return this.cs_balance;
    }

    public void setCs_balance(int cs_balance) {
        this.cs_balance = cs_balance;
    }

    // 로그인이 되어있는지 확인하는 편의 메서드
    public boolean isLoggedIn() {
        return this.cs_uid != null && !this.cs_uid.isEmpty();
    }

}
