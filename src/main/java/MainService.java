import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: hellodk
 * @description main service
 * @date: 2022/8/24 12:17
 */

public class MainService {
    public static void main(String[] args) {
        MainService main = new MainService();
        main.entry();
    }

    private String pddGoodsUri(String url) {
        String[] split = url.split("&");
        String[] goods = url.split("\\?");
        String param = "";
        if (split.length == 1) {
            return split[0];
        }
        for (int i = 0; i < split.length; i++) {
            String item = split[i];
            if (item.startsWith("goods") && item.contains("goods_id=")) {
                param = item;
                break;
            }
        }
        return goods[0].concat("?").concat(param);
    }

    private String biliUri(String url) {
        String uri = url;
        int httpsIndex = uri.indexOf("https");
        String text = uri.substring(0, httpsIndex);
        uri = uri.substring(httpsIndex);
        if (uri.contains("b23.tv")) {
            HttpResponse resp = HttpRequest.head(uri).timeout(20000).execute();
            String location = resp.header("Location");
            uri = location;
        }
        return (text.length() == 0 ? "" : (text + " ")) + removeParams(uri);
    }

    private String jdUri(String url) {
        return removeParams(url);
    }

    private String removeParams(String uri) {
        String[] split = uri.split("\\?");
        return split[0];
    }

    private String taobaoUri(String url) {
        String uri = url;
        if (uri.contains("taobao.com")) {
            return getTaobaocomUri(uri);
        }
        if (containsChinese(uri)) {
            int start = uri.indexOf("https");
            int end = uri.indexOf(" ");
            final String originUri = uri;
            uri = uri.substring(start, end);
            String text = originUri.substring(end);
            return text + " " + getTaobaoDesktopUrl(uri);
        }
        else {
            uri = uri.trim();
            return getTaobaoDesktopUrl(uri);
        }
    }

    private String getTaobaoDesktopUrl(String shortUrl) {
        String result = HttpRequest.get(shortUrl).timeout(20000).execute().body();
        String regex = "var url = '([^\\r\\n]*)';";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            result = matcher.group(1);
        }
        return getTaobaocomUri(result);


    }

    private String getTaobaocomUri(String url) {
        String uri = url;
        String[] split = uri.split("&");
        if (split.length == 1) {
            return split[0];
        }
        String[] id = uri.split("\\?");
        String param = "";
        for (int i = 0; i < split.length; i++) {
            String item = split[i];
            if (item.startsWith("id") && item.contains("id=")) {
                param = item;
                break;
            }
        }
        return id[0].concat("?").concat(param);
    }

    private void entry() {
        String uri = getSysClipboard();
        String site = "";
        if (uri.contains("yangkeduo.com")) {
            site = "pdd";
        }
        else if (uri.contains("jd.com")) {
            site = "jd";
        }
        else if (uri.contains("taobao.com") || uri.contains("tb.cn")) {
            site = "taobao";
        }
        else if (uri.contains("bilibili.com") || uri.contains("b23.tv")) {
            site = "bili";
        }
        // TODO 支持更多网站

        if (ObjectUtil.isEmpty(site)) {
            System.out.println(uri + " not supported at the moment.");
        }
        String result;
        switch (site) {
            case "pdd":
                result = pddGoodsUri(uri);
                break;
            case "jd":
                result = jdUri(uri);
                break;
            case "taobao":
                result = taobaoUri(uri);
                break;
            case "bili":
                result = biliUri(uri);
                break;
            default:
                //System.exit(0);
                return;
        }
        setSysClipboard(result);
        System.out.println("your new clipboard text is: " + result);
    }

    private void setSysClipboard(String myString) {
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    private String getSysClipboard() {
        String result = "";
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tr = clip.getContents(null);
        if (tr != null) {
            // 检查是文本类型再处理，其他类型不处理
            if (tr.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    result = (String) tr.getTransferData(DataFlavor.stringFlavor);
                }
                catch (UnsupportedFlavorException | IOException ex) {
                    System.out.println(ex);
                }
            }
            else {
                System.out.println("only deal with text.");
            }
        }
        else {
            System.out.println("Transferable is null!");
        }
        System.out.println("your original clipboard text is: " + result);
        return result;
    }

    private Boolean containsChinese(String str) {
        if (str == null) {
            return false;
        }
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }
}
