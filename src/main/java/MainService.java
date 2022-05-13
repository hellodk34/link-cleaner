import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author: hellodk
 * @description main service
 * @date: 2022/5/13 14:19
 */

public class MainService {

    public static void main(String[] args) {
        MainService mainService = new MainService();
        String clipText = mainService.getSysClipboard();
        System.out.println("your new clipboard text is: 【" + clipText + "】");
        mainService.setSysClipboard(clipText);
    }

    private String removeUrlParams(String url) {
        String result = null;
        try {
            URI uri = new URI(url);
            result = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, // ignore the query part of the input url
                    uri.getFragment()).toString();
        }
        catch (URISyntaxException ex) {
            System.out.println("Your url is invalid!");
        }
        return result;
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
        System.out.println("the original clipboard text is: 【" + result + "】");
        return removeUrlParams(result);
    }
}
