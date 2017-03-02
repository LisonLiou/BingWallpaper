package cc.lison;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cc.lison.utils.User32Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Main {

	final static String URL_BING = "http://www.bing.com";

	public static void main(String[] args) {

		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder().url(URL_BING + "/HPImageArchive.aspx?format=xml&idx=0&n=2&mkt=zh-cn").get()
				.addHeader("cache-control", "no-cache").addHeader("unique-token", UUID.randomUUID().toString()).build();

		try {
			Response response = client.newCall(request).execute();
			String html = response.body().string();

			HtmlCleaner hc = new HtmlCleaner();
			TagNode tn = hc.clean(html);
			String xpath = "//images/image/url/text()";
			Object[] objects = tn.evaluateXPath(xpath);

			System.out.println(objects.length);

			// int screenWidth = ((int)
			// java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
			// int screenHeight = ((int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);

			for (Object obj : objects) {
				request = new Request.Builder().url(URL_BING + obj).get().addHeader("cache-control", "no-cache")
						.addHeader("unique-token", UUID.randomUUID().toString()).build();

				Response response_ = client.newCall(request).execute();
				byte[] bytes = response_.body().bytes();
				String file = obj.toString().substring(obj.toString().lastIndexOf("/") + 1);
				saveFile(bytes, "./", file);

				User32Utils.installWallpaper(file);

				// 获取屏幕分辨率，然后replace再获取
				// 1366x768
				// 1920x1080
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPatherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveFile(byte[] bfile, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath + "\\" + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
