package vo;

public class StoreDTO {
    String st_uid;
    String st_name;
    String st_address;
    String st_phone;
    String st_type;

    int st_profit;
    int od_count; // 리뷰 가능 상점 띄우는데만 사용

    String menu_name;
    int menu_price;
    int menu_no;

    public int getOd_count() {
        return this.od_count;
    }

    public void setOd_count(int od_count) {
        this.od_count = od_count;
    }

    public int getMenu_no() {
        return this.menu_no;
    }

    public void setMenu_no(int menu_no) {
        this.menu_no = menu_no;
    }

    public String getSt_type() {
        return this.st_type;
    }

    public void setSt_type(String st_type) {
        this.st_type = st_type;
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

    public StoreDTO() {
    }

    public StoreDTO(String st_uid, String st_name, String st_address, String st_phone, String st_type) {
        this.st_uid = st_uid;
        this.st_name = st_name;
        this.st_address = st_address;
        this.st_phone = st_phone;
        this.st_type = st_type;
    }

    public StoreDTO(String st_uid, String st_name, String st_address, String st_phone, String st_type, int st_profit) {
        this.st_uid = st_uid;
        this.st_name = st_name;
        this.st_address = st_address;
        this.st_phone = st_phone;
        this.st_type = st_type;
        this.st_profit = st_profit;
    }

    public String getSt_uid() {
        return this.st_uid;
    }

    public void setSt_uid(String st_uid) {
        this.st_uid = st_uid;
    }

    public String getSt_name() {
        return this.st_name;
    }

    public void setSt_name(String st_name) {
        this.st_name = st_name;
    }

    public String getSt_address() {
        return this.st_address;
    }

    public void setSt_address(String st_address) {
        this.st_address = st_address;
    }

    public String getSt_phone() {
        return this.st_phone;
    }

    public void setSt_phone(String st_phone) {
        this.st_phone = st_phone;
    }

    public int getSt_profit() {
        return this.st_profit;
    }

    public void setSt_profit(int st_profit) {
        this.st_profit = st_profit;
    }

}
