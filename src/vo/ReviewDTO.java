package vo;

public class ReviewDTO {
    int rv_no;
    int rv_menu;
    int rv_score;
    String rv_content;
    String rv_customer;

    String cs_name;
    String menu_name;

    public String getRv_customer() {
        return this.rv_customer;
    }

    public void setRv_customer(String rv_customer) {
        this.rv_customer = rv_customer;
    }

    public String getCs_name() {
        return this.cs_name;
    }

    public void setCs_name(String cs_name) {
        this.cs_name = cs_name;
    }

    public String getMenu_name() {
        return this.menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public ReviewDTO(int rv_no, int rv_menu, int rv_score, String rv_content, String cs_name, String menu_name) {
        this.rv_no = rv_no;
        this.rv_menu = rv_menu;
        this.rv_score = rv_score;
        this.rv_content = rv_content;
        this.cs_name = cs_name;
        this.menu_name = menu_name;
    }

    public ReviewDTO() {
    }

    public int getRv_no() {
        return this.rv_no;
    }

    public void setRv_no(int rv_no) {
        this.rv_no = rv_no;
    }

    public int getRv_menu() {
        return this.rv_menu;
    }

    public void setRv_menu(int rv_menu) {
        this.rv_menu = rv_menu;
    }

    public int getRv_score() {
        return this.rv_score;
    }

    public void setRv_score(int rv_score) {
        this.rv_score = rv_score;
    }

    public String getRv_content() {
        return this.rv_content;
    }

    public void setRv_content(String rv_content) {
        this.rv_content = rv_content;
    }

}
