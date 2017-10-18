package donwit.com.uhf;

/**
 * @author Admin
 * @version EPC实现类
 */
public class EPC {
    private String Epc;
    private String Tid;
    private String scanDate;
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

    public String getScanDate() {
        return scanDate;
    }

    public void setScanDate(String scanDate) {
        this.scanDate = scanDate;
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
                ", scanDate='" + scanDate + '\'' +
                ", IMEI='" + IMEI + '\'' +
                '}';
    }
}
