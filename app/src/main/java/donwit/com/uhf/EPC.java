package donwit.com.uhf;

/**
 * @author Admin
 * @version EPC实现类
 */
public class EPC {
    private String Epc;
    private String Tid;
    private String Date;
    private String IMEI;

    public String getEpc() {
        return Epc;
    }

    public void setEpc(String epc) {
        Epc = epc;
    }

    public String getTid() {
        return Tid;
    }

    public void setTid(String tid) {
        Tid = tid;
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

    @Override
    public String toString() {
        return "EPC{" +
                "Epc='" + Epc + '\'' +
                ", Tid='" + Tid + '\'' +
                ", Date='" + Date + '\'' +
                ", IMEI='" + IMEI + '\'' +
                '}';
    }
}
