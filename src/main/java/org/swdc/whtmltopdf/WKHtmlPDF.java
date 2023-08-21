package org.swdc.whtmltopdf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.ours.common.PackageResources;
import org.swdc.ours.common.PlatformLoader;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class WKHtmlPDF {

    private static Logger logger = LoggerFactory.getLogger(WKHtmlPDF.class);

    private static List<String> arch64 = Arrays.asList(
            "amd64","x64","x86_64"
    );

    public static boolean load(File assetFolder) {
        if (!assetFolder.exists()){
            if(!assetFolder.mkdirs()) {
                return false;
            }
        }
        File target = new File(assetFolder.getAbsolutePath() + File.separator + "wkhtmltox");
        if (!target.exists()) {
            if(!target.mkdirs()) {
                return false;
            }
            String osArch = System.getProperty("os.arch");
            if (arch64.contains(osArch.toLowerCase())) {
                osArch = "x64";
            }
            String resourcePath = "";
            String osName = System.getProperty("os.name").trim().toLowerCase();
            if (osName.contains("windows")) {
                resourcePath = "windows-";
            } else if (osName.contains("mac")) {
                resourcePath = "macos-";
            } else {
                resourcePath = "linux-";
            }
            resourcePath = "wkhtmltox/" +  resourcePath + osArch + ".7z";
            logger.info("extracting native libraries....");
            if(!PackageResources.extract7ZipFromModule(WKHtmlPDF.class,target,resourcePath)) {
                return false;
            }
        }
        try {
            PlatformLoader loader = new PlatformLoader();
            loader.load(new File(target.getAbsolutePath() + File.separator + "WKHtmlPDF.xml"));
            return true;
        } catch (Exception e) {
            logger.error("failed to load library",e);
            return false;
        }

    }

}
