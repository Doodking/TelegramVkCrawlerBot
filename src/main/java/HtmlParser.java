import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HtmlParser {
    private static List<String> imagesLinks = new ArrayList<>();
    private static List<String> imgs = new ArrayList<>();

    public static List<String> getPhotosFromVk(String id) throws IOException {
        imagesLinks.clear();
        imgs.clear();
        Document doc = Jsoup.connect("https://vk.com/" + id).get();
        String link = doc.getElementsByClass("module_header").select("a[href]").attr("href");
        Document doc1 = Jsoup.connect("https://vk.com" + link).get();
        Elements el = doc1.getElementsByClass("photos_row").select("a[href]");
        el.forEach(element -> imagesLinks.add("https://vk.com" + element.attr("href")));
        for(String links : imagesLinks){
            Document document = Jsoup.connect(links).get();
            Elements element = document.getElementsByTag("head").select("meta[value]");
            for(Element e : element){
                if(e.attr("value").startsWith("https") && e.attr("value").endsWith("jpg")){
                    imgs.add(e.attr("value"));
                }
            }
        }
        File file1 = new File("data/" + id);
        file1.mkdir();
        for(int i=0; i < imgs.size(); i++){
            URL url = new URL(imgs.get(i));
            BufferedImage bf = ImageIO.read(url);
            File file = new File("data/" + id + "/img" + i + ".jpg");
            ImageIO.write(bf, "jpg", file);
        }
        return imgs;



    }


    public static String rate(List imgs){
        int x = imgs.size();
        Random r = new Random();
        Integer random = r.nextInt(imgs.size());
        return (String) imgs.get((int) (Math.random() * ++x));
    }
}
