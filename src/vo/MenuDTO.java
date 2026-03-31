package vo;

public class MenuDTO {
    int menu_no;
    String menu_store;
    String menu_name;
    int menu_price;

    private int od_duration;

    public int getOd_duration() {
        return this.od_duration;
    }

    public void setOd_duration(int od_duration) {
        this.od_duration = od_duration;
    }

    public MenuDTO() {
    }

    public MenuDTO(int menu_no, String menu_store, String menu_name, int menu_price) {
        this.menu_name = menu_name;
        this.menu_price = menu_price;
    }

    public int getMenu_no() {
        return this.menu_no;
    }

    public void setMenu_no(int menu_no) {
        this.menu_no = menu_no;
    }

    public String getMenu_store() {
        return this.menu_store;
    }

    public void setMenu_store(String menu_store) {
        this.menu_store = menu_store;
    }

    public String getMenu_name() {
        return this.menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public int getMenu_price() {
        return this.menu_price;
    }

    public void setMenu_price(int menu_price) {
        this.menu_price = menu_price;
    }

}
