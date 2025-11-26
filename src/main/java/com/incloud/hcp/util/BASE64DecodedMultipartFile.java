package com.incloud.hcp.util;

import com.itextpdf.io.codec.Base64;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BASE64DecodedMultipartFile implements MultipartFile {

    private final byte[] imgContent;
    private final String header;

    public BASE64DecodedMultipartFile(byte[] imgContent, String header) {
        this.imgContent = imgContent;
        this.header = header.split(";")[0];
    }

    @Override
    public String getName() {
        return System.currentTimeMillis() + Math.random() + "." + header.split("/")[1];
    }

    @Override
    public String getOriginalFilename() {
        return System.currentTimeMillis() + (int) Math.random() * 10000 + "." + header.split("/")[1];
    }

    @Override
    public String getContentType() {
        return header.split(":")[1];
    }

    @Override
    public boolean isEmpty() {
        return imgContent == null || imgContent.length == 0;
    }

    @Override
    public long getSize() {
        return imgContent.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return imgContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(imgContent);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        new FileOutputStream(dest).write(imgContent);
    }

    public static MultipartFile base64ToMultipart(String base64) {
       // try {
            String[] baseStrs = base64.split(",");

            //BASE64Decoder decoder = new BASE64Decoder();

            byte[] b = new byte[0];
            b = Base64.decode(baseStrs[0]);

            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            return new BASE64DecodedMultipartFile(b, baseStrs[0]);
       // } catch (IOException e) {

           // e.printStackTrace();
          //  return null;
        //}
    }

    public  static String  mimesType(String ext){
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(".json","application/json");
        properties.put(".mp4","video/mp4");
        properties.put(".rar","application/x-rar-compressed");
        properties.put(".xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        properties.put(".xlsm","application/vnd.ms-excel.sheet.macroEnabled.12");
        properties.put(".docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        properties.put(".pptx","application/vnd.openxmlformats-officedocument.presentationml.presentation");
        properties.put(".webm","video/webm");
        properties.put(".3dm","x-world/x-3dmf");
        properties.put(".3dmf","x-world/x-3dmf");
        properties.put(".a","application/octet-stream");
        properties.put(".aab","application/x-authorware-bin");
        properties.put(".aam","application/x-authorware-map");
        properties.put(".aas","application/x-authorware-seg");
        properties.put(".abc","text/vnd.abc");
        properties.put(".acgi","text/html");
        properties.put(".afl","video/animaflex");
        properties.put(".ai","application/postscript");
        properties.put(".aif","audio/aiff");
        properties.put(".aifc","audio/aiff");
        properties.put(".aim","application/x-aim");
        properties.put(".aip","text/x-audiosoft-intra");
        properties.put(".ani","application/x-navi-animation");
        properties.put(".aos","application/x-nokia-9000-communicator-add-on-software");
        properties.put(".aps","application/mime");
        properties.put(".arc","application/octet-stream");
        properties.put(".arj","application/arj");
        properties.put(".art","image/x-jg");
        properties.put(".asf","video/x-ms-asf");
        properties.put(".asm","text/x-asm");
        properties.put(".asp","text/asp");
        properties.put(".asx","video/x-ms-asf");
        properties.put(".au","audio/basic");
        properties.put(".avi","application/x-troff-msvideo");
        properties.put(".avs","video/avs-video");
        properties.put(".bcpio","application/x-bcpio");
        properties.put(".bin","application/mac-binary");
        properties.put(".bm","image/bmp");
        properties.put(".bmp","image/bmp");
        properties.put(".boo","application/book");
        properties.put(".book","application/book");
        properties.put(".boz","application/x-bzip2");
        properties.put(".bsh","application/x-bsh");
        properties.put(".bz","application/x-bzip");
        properties.put(".bz2","application/x-bzip2");
        properties.put(".c","text/plain");
        properties.put(".c++","text/plain");
        properties.put(".cat","application/vnd.ms-pki.seccat");
        properties.put(".cc","text/plain");
        properties.put(".ccad","application/clariscad");
        properties.put(".cco","application/x-cocoa");
        properties.put(".cdf","application/cdf");
        properties.put(".cha","application/x-chat");
        properties.put(".chat","application/x-chat");
        properties.put(".class","application/java");
        properties.put(".com","application/octet-stream");
        properties.put(".conf","text/plain");
        properties.put(".cpio","application/x-cpio");
        properties.put(".cpp","text/x-c");
        properties.put(".cpt","application/mac-compactpro");
        properties.put(".crl","application/pkcs-crl");
        properties.put(".crt","application/pkix-cert");
        properties.put(".csh","application/x-csh");
        properties.put(".css","text/css");
        properties.put(".cxx","text/plain");
        properties.put(".dcr","application/x-director");
        properties.put(".deepv","application/x-deepv");
        properties.put(".def","text/plain");
        properties.put(".der","application/x-x509-ca-cert");
        properties.put(".dif","video/x-dv");
        properties.put(".dir","application/x-director");
        properties.put(".dl","video/dl");
        properties.put(".doc","application/msword");
        properties.put(".dot","application/msword");
        properties.put(".dp","application/commonground");
        properties.put(".drw","application/drafting");
        properties.put(".dump","application/octet-stream");
        properties.put(".dv","video/x-dv");
        properties.put(".dvi","application/x-dvi");
        properties.put(".dwf","model/vnd.dwf");
        properties.put(".dwg","application/acad");
        properties.put(".dxf","image/x-dwg");
        properties.put(".dxr","application/x-director");
        properties.put(".el","text/x-script.elisp");
        properties.put(".elc","application/x-elc");
        properties.put(".env","application/x-envoy");
        properties.put(".eps","application/postscript");
        properties.put(".es","application/x-esrehber");
        properties.put(".etx","text/x-setext");
        properties.put(".evy","application/envoy");
        properties.put(".exe","application/octet-stream");
        properties.put(".f","text/plain");
        properties.put(".f77","text/x-fortran");
        properties.put(".f90","text/plain");
        properties.put(".fdf","application/vnd.fdf");
        properties.put(".fif","application/fractals");
        properties.put(".flo","image/florian");
        properties.put(".flx","text/vnd.fmi.flexstor");
        properties.put(".fmf","video/x-atomic3d-feature");
        properties.put(".for","text/plain");
        properties.put(".frl","application/freeloader");
        properties.put(".funk","audio/make");
        properties.put(".g","text/plain");
        properties.put(".g3","image/g3fax");
        properties.put(".gif","image/gif");
        properties.put(".gl","video/gl");
        properties.put(".gsd","audio/x-gsm");
        properties.put(".gsm","audio/x-gsm");
        properties.put(".gsp","application/x-gsp");
        properties.put(".gss","application/x-gss");
        properties.put(".gtar","application/x-gtar");
        properties.put(".gz","application/x-compressed");
        properties.put(".gzip","application/x-gzip");
        properties.put(".h","text/plain");
        properties.put(".hdf","application/x-hdf");
        properties.put(".help","application/x-helpfile");
        properties.put(".hgl","application/vnd.hp-hpgl");
        properties.put(".hh","text/plain");
        properties.put(".hlb","text/x-script");
        properties.put(".hlp","application/x-helpfile");
        properties.put(".hpg","application/vnd.hp-hpgl");
        properties.put(".hpgl","application/vnd.hp-hpgl");
        properties.put(".hqx","application/binhex");
        properties.put(".hta","application/hta");
        properties.put(".htc","text/x-component");
        properties.put(".htm","text/html");
        properties.put(".html","text/html");
        properties.put(".htmls","text/html");
        properties.put(".htt","text/webviewhtml");
        properties.put(".htx","text/html");
        properties.put(".ice","x-conference/x-cooltalk");
        properties.put(".ico","image/x-icon");
        properties.put(".idc","text/plain");
        properties.put(".ief","image/ief");
        properties.put(".iefs","image/ief");
        properties.put(".iges","application/iges");
        properties.put(".igs","application/iges");
        properties.put(".ima","application/x-ima");
        properties.put(".imap","application/x-httpd-imap");
        properties.put(".inf","application/inf");
        properties.put(".ins","application/x-internett-signup");
        properties.put(".ip","application/x-ip2");
        properties.put(".isu","video/x-isvideo");
        properties.put(".it","audio/it");
        properties.put(".iv","application/x-inventor");
        properties.put(".ivr","i-world/i-vrml");
        properties.put(".ivy","application/x-livescreen");
        properties.put(".jam","audio/x-jam");
        properties.put(".jav","text/plain");
        properties.put(".java","text/plain");
        properties.put(".jcm","application/x-java-commerce");
        properties.put(".jfif","image/jpeg");
        properties.put(".jfif-tbnl","image/jpeg");
        properties.put(".jpe","image/jpeg");
        properties.put(".jpeg","image/jpeg");
        properties.put(".jpg","image/jpeg");
        properties.put(".jps","image/x-jps");
        properties.put(".js","application/javascript");
        properties.put(".jut","image/jutvision");
        properties.put(".lam","audio/x-liveaudio");
        properties.put(".latex","application/x-latex");
        properties.put(".lhx","application/octet-stream");
        properties.put(".list","text/plain");
        properties.put(".log","text/plain");
        properties.put(".lst","text/plain");
        properties.put(".lsx","text/x-la-asf");
        properties.put(".ltx","application/x-latex");
        properties.put(".m","text/x-m");
        properties.put(".m1v","video/mpeg");
        properties.put(".m2a","audio/mpeg");
        properties.put(".m2v","video/mpeg");
        properties.put(".m3u","audio/x-mpequrl");
        properties.put(".man","application/x-troff-man");
        properties.put(".map","application/x-navimap");
        properties.put(".mar","text/plain");
        properties.put(".mbd","application/mbedlet");
        properties.put(".mc$","application/x-magic-cap-package-1.0");
        properties.put(".mcd","application/mcad");
        properties.put(".mcf","text/mcf");
        properties.put(".mcp","application/netmc");
        properties.put(".me","application/x-troff-me");
        properties.put(".mht","message/rfc822");
        properties.put(".mhtml","message/rfc822");
        properties.put(".mid","application/x-midi");
        properties.put(".midi","application/x-midi");
        properties.put(".mif","application/x-frame");
        properties.put(".mime","message/rfc822");
        properties.put(".mjf","audio/x-vnd.audioexplosion.mjuicemediafile");
        properties.put(".mjpg","video/x-motion-jpeg");
        properties.put(".mm","application/base64");
        properties.put(".mme","application/base64");
        properties.put(".mod","audio/mod");
        properties.put(".moov","video/quicktime");
        properties.put(".mov","video/quicktime");
        properties.put(".movie","video/x-sgi-movie");
        properties.put(".mp2","audio/mpeg");
        properties.put(".mpc","application/x-project");
        properties.put(".mpe","video/mpeg");
        properties.put(".mpeg","video/mpeg");
        properties.put(".mpg","audio/mpeg");
        properties.put(".mpga","audio/mpeg");
        properties.put(".mpp","application/vnd.ms-project");
        properties.put(".mpt","application/x-project");
        properties.put(".mpv","application/x-project");
        properties.put(".mpx","application/x-project");
        properties.put(".mrc","application/marc");
        properties.put(".ms","application/x-troff-ms");
        properties.put(".mv","video/x-sgi-movie");
        properties.put(".my","audio/make");
        properties.put(".mzz","application/x-vnd.audioexplosion.mzz");
        properties.put(".nap","image/naplps");
        properties.put(".naplps","image/naplps");
        properties.put(".nc","application/x-netcdf");
        properties.put(".ncm","application/vnd.nokia.configuration-message");
        properties.put(".nif","image/x-niff");
        properties.put(".niff","image/x-niff");
        properties.put(".nix","application/x-mix-transfer");
        properties.put(".nsc","application/x-conference");
        properties.put(".nvd","application/x-navidoc");
        properties.put(".o","application/octet-stream");
        properties.put(".oda","application/oda");
        properties.put(".omc","application/x-omc");
        properties.put(".omcd","application/x-omcdatamaker");
        properties.put(".omcr","application/x-omcregerator");
        properties.put(".p","text/x-pascal");
        properties.put(".p10","application/pkcs10");
        properties.put(".p7a","application/x-pkcs7-signature");
        properties.put(".p7c","application/pkcs7-mime");
        properties.put(".p7r","application/x-pkcs7-certreqresp");
        properties.put(".p7s","application/pkcs7-signature");
        properties.put(".part","application/pro_eng");
        properties.put(".pas","text/pascal");
        properties.put(".pbm","image/x-portable-bitmap");
        properties.put(".pcl","application/vnd.hp-pcl");
        properties.put(".pct","image/x-pict");
        properties.put(".pcx","image/x-pcx");
        properties.put(".pdb","chemical/x-pdb");
        properties.put(".pdf","application/pdf");
        properties.put(".pgm","image/x-portable-greymap");
        properties.put(".pic","image/pict");
        properties.put(".pict","image/pict");
        properties.put(".pkg","application/x-newton-compatible-pkg");
        properties.put(".pko","application/vnd.ms-pki.pko");
        properties.put(".pl","text/x-script.perl");
        properties.put(".plx","application/x-pixclscript");
        properties.put(".pm","text/x-script.perl-module");
        properties.put(".pm4","application/x-pagemaker");
        properties.put(".pm5","application/x-pagemaker");
        properties.put(".png","image/png");
        properties.put(".pov","model/x-pov");
        properties.put(".ppa","application/vnd.ms-powerpoint");
        properties.put(".ppm","image/x-portable-pixmap");
        properties.put(".pps","application/mspowerpoint");
        properties.put(".ppz","application/mspowerpoint");
        properties.put(".pre","application/x-freelance");
        properties.put(".prt","application/pro_eng");
        properties.put(".ps","application/postscript");
        properties.put(".psd","application/octet-stream");
        properties.put(".pvu","paleovu/x-pv");
        properties.put(".pwz","application/vnd.ms-powerpoint");
        properties.put(".py","text/x-script.phyton");
        properties.put(".pyc","application/x-bytecode.python");
        properties.put(".qcp","audio/vnd.qcelp");
        properties.put(".qd3","x-world/x-3dmf");
        properties.put(".qd3d","x-world/x-3dmf");
        properties.put(".qif","image/x-quicktime");
        properties.put(".qt","video/quicktime");
        properties.put(".qtc","video/x-qtc");
        properties.put(".qti","image/x-quicktime");
        properties.put(".qtif","image/x-quicktime");
        properties.put(".ra","audio/x-pn-realaudio");
        properties.put(".ram","audio/x-pn-realaudio");
        properties.put(".ras","application/x-cmu-raster");
        properties.put(".rast","image/cmu-raster");
        properties.put(".rexx","text/x-script.rexx");
        properties.put(".rf","image/vnd.rn-realflash");
        properties.put(".rgb","image/x-rgb");
        properties.put(".rm","application/vnd.rn-realmedia");
        properties.put(".rmi","audio/mid");
        properties.put(".rmm","audio/x-pn-realaudio");
        properties.put(".rmp","audio/x-pn-realaudio");
        properties.put(".rnx","application/vnd.rn-realplayer");
        properties.put(".roff","application/x-troff");
        properties.put(".rp","image/vnd.rn-realpix");
        properties.put(".rpm","audio/x-pn-realaudio-plugin");
        properties.put(".rt","text/richtext");
        properties.put(".rtf","application/rtf");
        properties.put(".rtx","application/rtf");
        properties.put(".rv","video/vnd.rn-realvideo");
        properties.put(".s","text/x-asm");
        properties.put(".s3m","audio/s3m");
        properties.put(".saveme","application/octet-stream");
        properties.put(".sbk","application/x-tbook");
        properties.put(".scm","application/x-lotusscreencam");
        properties.put(".sdml","text/plain");
        properties.put(".sdp","application/sdp");
        properties.put(".sdr","application/sounder");
        properties.put(".sea","application/sea");
        properties.put(".set","application/set");
        properties.put(".sgm","text/sgml");
        properties.put(".sgml","text/sgml");
        properties.put(".sh","text/x-script.sh");
        properties.put(".shtml","text/html");
        properties.put(".sid","audio/x-psid");
        properties.put(".sit","application/x-sit");
        properties.put(".skd","application/x-koan");
        properties.put(".skm","application/x-koan");
        properties.put(".skp","application/x-koan");
        properties.put(".skt","application/x-koan");
        properties.put(".sl","application/x-seelogo");
        properties.put(".smi","application/smil");
        properties.put(".smil","application/smil");
        properties.put(".snd","audio/basic");
        properties.put(".sol","application/solids");
        properties.put(".spc","application/x-pkcs7-certificates");
        properties.put(".spl","application/futuresplash");
        properties.put(".spr","application/x-sprite");
        properties.put(".sprite","application/x-sprite");
        properties.put(".src","application/x-wais-source");
        properties.put(".ssi","text/x-server-parsed-html");
        properties.put(".ssm","application/streamingmedia");
        properties.put(".sst","application/vnd.ms-pki.certstore");
        properties.put(".step","application/step");
        properties.put(".stl","application/sla");
        properties.put(".stp","application/step");
        properties.put(".sv4cpio","application/x-sv4cpio");
        properties.put(".sv4crc","application/x-sv4crc");
        properties.put(".svr","application/x-world");
        properties.put(".swf","application/x-shockwave-flash");
        properties.put(".t","application/x-troff");
        properties.put(".talk","text/x-speech");
        properties.put(".tar","application/x-tar");
        properties.put(".tbk","application/toolbook");
        properties.put(".tcsh","text/x-script.tcsh");
        properties.put(".tex","application/x-tex");
        properties.put(".texi","application/x-texinfo");
        properties.put(".texinfo","application/x-texinfo");
        properties.put(".text","application/plain");
        properties.put(".tgz","application/x-compressed");
        properties.put(".tif","image/tiff");
        properties.put(".tr","application/x-troff");
        properties.put(".tsi","audio/tsp-audio");
        properties.put(".tsp","application/dsptype");
        properties.put(".tsv","text/tab-separated-values");
        properties.put(".turbot","image/florian");
        properties.put(".txt","text/plain");
        properties.put(".uil","text/x-uil");
        properties.put(".uni","text/uri-list");
        properties.put(".unis","text/uri-list");
        properties.put(".unv","application/i-deas");
        properties.put(".uri","text/uri-list");
        properties.put(".uris","text/uri-list");
        properties.put(".ustar","application/x-ustar");
        properties.put(".uu","application/octet-stream");
        properties.put(".uue","text/x-uuencode");
        properties.put(".vcd","application/x-cdlink");
        properties.put(".vcs","text/x-vcalendar");
        properties.put(".vda","application/vda");
        properties.put(".vdo","video/vdo");
        properties.put(".vew","application/groupwise");
        properties.put(".viv","video/vivo");
        properties.put(".vivo","video/vivo");
        properties.put(".vmd","application/vocaltec-media-desc");
        properties.put(".vmf","application/vocaltec-media-file");
        properties.put(".voc","audio/voc");
        properties.put(".vos","video/vosaic");
        properties.put(".vox","audio/voxware");
        properties.put(".vqe","audio/x-twinvq-plugin");
        properties.put(".vqf","audio/x-twinvq");
        properties.put(".vql","audio/x-twinvq-plugin");
        properties.put(".vrml","application/x-vrml");
        properties.put(".vrt","x-world/x-vrt");
        properties.put(".vsd","application/x-visio");
        properties.put(".vst","application/x-visio");
        properties.put(".vsw","application/x-visio");
        properties.put(".w60","application/wordperfect6.0");
        properties.put(".w61","application/wordperfect6.1");
        properties.put(".w6w","application/msword");
        properties.put(".wav","audio/wav");
        properties.put(".wb1","application/x-qpro");
        properties.put(".wbmp","image/vnd.wap.wbmp");
        properties.put(".web","application/vnd.xara");
        properties.put(".wiz","application/msword");
        properties.put(".wk1","application/x-123");
        properties.put(".wmf","windows/metafile");
        properties.put(".wml","text/vnd.wap.wml");
        properties.put(".wmlc","application/vnd.wap.wmlc");
        properties.put(".wmls","text/vnd.wap.wmlscript");
        properties.put(".wmlsc","application/vnd.wap.wmlscriptc");
        properties.put(".word","application/msword");
        properties.put(".wp","application/wordperfect");
        properties.put(".wp5","application/wordperfect");
        properties.put(".wp6","application/wordperfect");
        properties.put(".wpd","application/wordperfect");
        properties.put(".wq1","application/x-lotus");
        properties.put(".wri","application/mswrite");
        properties.put(".wrl","model/vrml");
        properties.put(".wrz","model/vrml");
        properties.put(".wsc","text/scriplet");
        properties.put(".wsrc","application/x-wais-source");
        properties.put(".wtk","application/x-wintalk");
        properties.put(".xbm","image/x-xbitmap");
        properties.put(".xdr","video/x-amt-demorun");
        properties.put(".xgz","xgl/drawing");
        properties.put(".xif","image/vnd.xiff");
        properties.put(".xl","application/excel");
        properties.put(".xla","application/excel");
        properties.put(".xlb","application/excel");
        properties.put(".xlc","application/excel");
        properties.put(".xld","application/excel");
        properties.put(".xlk","application/excel");
        properties.put(".xll","application/excel");
        properties.put(".xlm","application/excel");
        properties.put(".xls","application/excel");
        properties.put(".xlt","application/excel");
        properties.put(".xlv","application/excel");
        properties.put(".xlw","application/excel");
        properties.put(".xm","audio/xm");
        properties.put(".xml","application/xml");
        properties.put(".xmz","xgl/movie");
        properties.put(".xpix","application/x-vnd.ls-xpix");
        properties.put(".xpm","image/x-xpixmap");
        properties.put(".x-png","image/png");
        properties.put(".xsr","video/x-amt-showrun");
        properties.put(".xwd","image/x-xwd");
        properties.put(".xyz","chemical/x-pdb");
        properties.put(".z","application/x-compress");
        properties.put(".zip","application/zip");
        properties.put(".zoo","application/octet-stream");
        properties.put(".zsh","text/x-script.zsh");

         String val = (String)properties.get("."+ext);
         return val;
    }

}
