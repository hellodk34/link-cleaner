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

    private static final String XHS_DOMAIN = "xiaohongshu.com";

    public static void main(String[] args) {
        MainService main = new MainService();
        main.mainEntry();
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

    private String douyinUri(String url) {
        String uri = url;
        int startIndex = uri.indexOf("抖音，");
        int httpsIndex = uri.indexOf("https");
        String text = uri.substring(startIndex + 3, httpsIndex);
        uri = uri.substring(httpsIndex);
        HttpResponse resp = HttpRequest.head(uri).timeout(20000).execute();
        String location = resp.header("Location");
        String newLocation = removeParams(location);
        String regex = "\\/(\\d+)\\/";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(newLocation);
        if (m.find()) {
            String videoId = m.group(1);
            String newUriTemplate = "https://www.douyin.com/video/%s";
            String result = String.format(newUriTemplate, videoId);
            return text + result;
        }
        return url;
    }

    private String xiaohongshuUri(String text) {
        if (text.contains(XHS_DOMAIN)) {
            String[] uriSplit = text.split("\\?");
            if (uriSplit.length > 1) {
                final int length = uriSplit.length;
                StringBuilder firstPart = new StringBuilder();
                for (int i = 0; i < (length - 1); i++) {
                    firstPart.append(uriSplit[i]);
                }
                String secondPart = uriSplit[length - 1];
                String[] params = secondPart.split("\\&");
                for (String param : params) {
                    if (param.contains("xsec_token")) {
                        return firstPart.toString().concat("?").concat(param);
                    }
                }
            }
        }
        String schemePrefix = "";
        if (text.contains("http://")) {
            schemePrefix = "http://";
        }
        else if (text.contains("https://")) {
            schemePrefix = "https://";
        }
        else {
            System.out.println(text + "is not supported now.");
        }
        int httpIndex = text.lastIndexOf(schemePrefix);
        // 正则表达式匹配 URI
        String regex = "(?<=http://|https://)[\\w\\d+./?=]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        // 寻找匹配项并输出结果
        if (matcher.find()) {
            String uri = matcher.group();
            HttpResponse resp = HttpRequest.head(schemePrefix + uri).timeout(20000).execute();
            String location = resp.header("Location");
            String cleanedUri = removeParams(location);
            return text.substring(0, httpIndex) + " " + cleanedUri;
        }
        return "";
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
        int start = uri.indexOf("https");
        int end = uri.indexOf(" ");
        String goodName = uri.substring(end);
        String realUri = uri.substring(start, end);
        String result = HttpRequest.get(realUri).timeout(20000).execute().body();
        String regex = "var url = '([^\\r\\n]*)';";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            uri = matcher.group(1);
        }
        return goodName + " " + getTaobaocomUri(uri);
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

    private void mainEntry() {
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

        // 2022-11-21 13:25:07 add site douyin
        else if (uri.contains("douyin.com")) {
            site = "douyin";
        }
        // 2023-12-21 09:21:50 add site: xiaohongshu
        else if (uri.contains("xhslink.com") || uri.contains(XHS_DOMAIN)) {
            site = "xiaohongshu";
        }

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
            case "douyin":
                result = douyinUri(uri);
                break;
            case "xiaohongshu":
                result = xiaohongshuUri(uri);
                break;
            default:
                return;
        }
        setSysClipboard(result);
        System.out.println("your new clipboard text is: " + result);
    }

    private void setSysClipboard(String myString) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("linux")) {
            setClipboardUsingXClipOnLinux(myString);
        }
        else {
            // Windows、macOS 可以持久化这个内容到系统剪贴板
            StringSelection stringSelection = new StringSelection(myString);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }

    }

    private void setClipboardUsingXClipOnLinux(String text) {
        // 加入 -n 选项以避免 echo 在末尾自动添加换行
        String[] cmd = {"/bin/bash", "-c", "echo -n " + escape(text) + " | xclip -selection clipboard"};
        try {
            Runtime.getRuntime().exec(cmd);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 适当地转义文本中的特殊字符
    private String escape(String text) {
        return "'" + text.replace("'", "'\\''") + "'";
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
                    throw new RuntimeException(ex);
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
        System.out.print(System.lineSeparator());
        return result;
    }
}
