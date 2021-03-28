package com.blackseapps.heskodkontrol.Modal;

public class Variables {



    public static String TYPE = "";
    public static boolean FRONT_CAMERA = true;
    public static boolean REFRESH = true;
    public static boolean LOADING_STATUS = false;

    public static String URL_EDEVLET_SIGN = "https://giris.turkiye.gov.tr/Giris/gir";
    public static String URL_QUERY = "https://www.turkiye.gov.tr/saglik-bakanligi-hes-kodu-sorgulama";
    public static String URL_QUERY_RESPONSE = "https://www.turkiye.gov.tr/saglik-bakanligi-hes-kodu-sorgulama?sonuc=Goster";
    public static String URL_TOKEN_SERVICE = "https://heskodu.btbnext.com/api/heskodu/login";
    public static String URL_TOKEN_SAVE_LOG = "https://heskodu.btbnext.com/api/heskodu/savelog";


    public static String E_DEVLET_SIGN_JS_CODE(String tc, String password) {
        return "javascript:document.getElementById('tridField').value='" + tc + "';" +
                "javascript:document.getElementById('egpField').value='" + password + "';" +
                "javascript:document.getElementsByName('submitButton')[0].click();" +
                "javascript:document.getElementsByName('submitButton')[0].click();" +
                "jquery:$('.loginLink')[0].click();";
    }

    public static String E_DEVLET_QUERY_JS_CODE(String hescode) {
        return "javascript:document.getElementById('hes_kodu').value='" + hescode + "';" +
                "javascript:document.forms['mainForm'].submit();";
    }


    public static String E_DEVLET_QUERY_RESPONSE_JS_CODE() {
        return "javascript:window.compact.sendData(" +
                "document.getElementsByTagName('dd')[1].innerText," +
                "document.getElementsByTagName('dd')[0].innerText," +
                "document.getElementsByTagName('dd')[2].innerText," +
                "document.getElementsByTagName('dd')[3].innerText," +
                "document.getElementsByTagName('dd')[4].innerText);";
    }

    public static String E_DEVLET_QUERY_NOT_RESPONSE_JS_CODE() {
        return "javascript:window.compact.sendData(" +
                "document.getElementsByClassName('compact').length);";
    }
}
