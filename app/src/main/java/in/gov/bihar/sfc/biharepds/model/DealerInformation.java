package in.gov.bihar.sfc.biharepds.model;

/**
 * Created by VR on 12/29/2017.
 */

public class DealerInformation {
    private String fps_code;
    private String fps_dealer_name;
    private String fps_license_auth_number;
    private String locality_market_name;
    private String district_id;
    private String block_id;
    private String panchayat_id;
    private String is_active = "Y";

    public String getFps_code() {
        return fps_code;
    }

    public void setFps_code(String fps_code) {
        this.fps_code = fps_code;
    }

    public String getFps_dealer_name() {
        return fps_dealer_name;
    }

    public void setFps_dealer_name(String fps_dealer_name) {
        this.fps_dealer_name = fps_dealer_name;
    }

    public String getFps_license_auth_number() {
        return fps_license_auth_number;
    }

    public void setFps_license_auth_number(String fps_license_auth_number) {
        this.fps_license_auth_number = fps_license_auth_number;
    }

    public String getLocality_market_name() {
        return locality_market_name;
    }

    public void setLocality_market_name(String locality_market_name) {
        this.locality_market_name = locality_market_name;
    }

    public String getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(String district_id) {
        this.district_id = district_id;
    }

    public String getBlock_id() {
        return block_id;
    }

    public void setBlock_id(String block_id) {
        this.block_id = block_id;
    }

    public String getPanchayat_id() {
        return panchayat_id;
    }

    public void setPanchayat_id(String panchayat_id) {
        this.panchayat_id = panchayat_id;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }
}
