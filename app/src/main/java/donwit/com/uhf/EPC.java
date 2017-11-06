package donwit.com.uhf;

/**
 * @author Admin
 * @version EPC实现类
 */
public class EPC {
    private String Epc;
    private String Date;
    private String IMEI;

    public String getEpc() {
        return Epc;
    }

    public void setEpc(String epc) {
        Epc = epc;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }
}
