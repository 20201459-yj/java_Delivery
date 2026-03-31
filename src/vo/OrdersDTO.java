package vo;

import java.sql.Time;
import java.time.LocalDateTime;

public class OrdersDTO {
    int od_no;
    String od_customer;
    String od_store;
    String od_content;
    int od_menu;
    int od_state;

    int od_duration;
    LocalDateTime od_start;

    public OrdersDTO() {
    }

    public OrdersDTO(String od_customer, String od_store,
            String od_content, int od_menu, int od_state) {
        this.od_customer = od_customer;
        this.od_store = od_store;
        this.od_content = od_content;
        this.od_menu = od_menu;
        this.od_state = od_state;
    }

    public int getOd_no() {
        return this.od_no;
    }

    public void setOd_no(int od_no) {
        this.od_no = od_no;
    }

    public String getOd_customer() {
        return this.od_customer;
    }

    public void setOd_customer(String od_customer) {
        this.od_customer = od_customer;
    }

    public String getOd_store() {
        return this.od_store;
    }

    public void setOd_store(String od_store) {
        this.od_store = od_store;
    }

    public String getOd_content() {
        return this.od_content;
    }

    public void setOd_content(String od_content) {
        this.od_content = od_content;
    }

    public int getOd_menu() {
        return this.od_menu;
    }

    public void setOd_menu(int od_menu) {
        this.od_menu = od_menu;
    }

    public int getOd_state() {
        return this.od_state;
    }

    public void setOd_state(int od_state) {
        this.od_state = od_state;
    }

    public int getOd_duration() {
        return this.od_duration;
    }

    public void setOd_duration(int od_duration) {
        this.od_duration = od_duration;
    }

    public LocalDateTime getOd_start() {
        return this.od_start;
    }

    public void setOd_start(LocalDateTime od_start) {
        this.od_start = od_start;
    }

}
