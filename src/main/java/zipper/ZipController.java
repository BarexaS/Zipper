package zipper;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/")
public class ZipController {

    @RequestMapping("/")
    public String onIndex() {
        return "index";
    }

    @RequestMapping(value = "/do.zip", method = RequestMethod.POST)
    public String zipping(@RequestParam MultipartFile[] files, @RequestParam("zipName") String zipName, HttpServletResponse resp) throws IOException {
        System.out.println(zipName);
        File zip = new File(zipName+".zip");
        collectingZip(files, zip);

        resp.setContentType("application/zip");
        resp.setContentLengthLong(zip.length());

        OutputStream os = resp.getOutputStream();
        FileInputStream is = new FileInputStream(zip);

        byte[] buffer = new byte[1024*4];
        int bytesRead = -1;
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }

        is.close();
        os.close();
        return "index";
    }

    private void collectingZip(MultipartFile[] files, File zip) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip))) {
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                System.out.println(fileName);
                ZipEntry ze = new ZipEntry(fileName);
                zos.putNextEntry(ze);
                zos.write(file.getBytes());
                zos.closeEntry();
            }
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}