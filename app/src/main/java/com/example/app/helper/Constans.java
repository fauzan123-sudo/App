package com.example.app.helper;

public class Constans {
    //TODO URL
    //base url
    public static final String URL              = "http://172.16.52.189/";
    public static final String BaseUrl          = "http://172.16.52.189/wengky/api/";
    //login
    public static final String URL_LOGIN        = BaseUrl + "login";
    //profile
    public static final String urlImagePegawai  = URL+"wengky/upload/image/pegawai/";
    // berita acara
    public static final String urlImageBerita   = URL+"wengky/upload/berita/";
//    scanning
//    gaji
    public static final String CekScanning      = BaseUrl+"Scan.php";
    public static final String request_url      = BaseUrl + "gambarBerita.php";
    public static final String read_absensi     = BaseUrl + "read_absensi_personal.php";
    public static final String inputToken       = BaseUrl+"cekToken.php";
    public static String URL_READ               = BaseUrl +"read.php";
    public static String gaji                   = BaseUrl +"gaji.php";
    public static String berita                 = BaseUrl +"berita_acara.php";
    public static final String URL_InserScan    = BaseUrl + "InsertAbsen.php";
    public static final String Jumlah_data      = BaseUrl+ "jumlahdata1.php";
    public static final String Total_Absensi    = BaseUrl+ "totalAbsensi.php";
    public static final String Total_Absensi2   = BaseUrl+ "totalAbsensi2.php";
    public static final String URL_Spinner      = BaseUrl+ "spinerIzin.php";
    public static final String izin             = BaseUrl+ "Perizinan.php";

    //TODO REQUEST STATIC
    public static final String CHANNEL_ID       = "channel_id";
    public static final String CHANNEL_NAME     = "channel_name";
    public static final String CHANNEL_DESC     = "channel_desc";
    public static final String TAG_SUCCESS      = "success";
    public static final String TAG_MESSAGE      = "message";
    public static final String TAG_QR           = "qr";
    public static final String TAG_JSON_OBJECT  = "json_obj_req";
}
