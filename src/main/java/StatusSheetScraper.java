import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.parser.pdf.PDFParser;
import org.xml.sax.SAXException;

import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Pattern;
public class StatusSheetScraper {

    //common 3 letter words (should all be capital)
    private static Set<String> c3l = Set.of("THE","AND");

    //common 4 letter words (should all be capital)
    private static Set<String> c4l = new HashSet<>();

    //takes pdf and filters out all the non-course code information
    public static Set<String> filter(String pdf) {
        Scanner scn = new Scanner(pdf);

        //get past start junk
        for(int i = 0; i < 19;i++) scn.nextLine();

        Set<String> ccs = new HashSet<>();

        //keep lastmajor at larger scope so we can have a major be on a different line than the numbers it's associated with
        String lastmajor = "";
        while(scn.hasNextLine()) {
            String l = scn.nextLine();
            if(Pattern.matches(".*[A-Z]{4}.*\\d{3}.*",l) || Pattern.matches(".*[A-Z]{3}.*\\d{3}.*",l)) lastmajor = get_course_codes_from_line(l,ccs,lastmajor);
            //special: reli 211/212 case, CHEM 111 and 113, CHEM 112 and 114, CHEM 111/113
            //could be cool to maintain 2 sets -> a courses taken and a courses to take set
            //with less time though it might be best to just have a set with all courses on status sheet in it
            //I think we should unhighlight if it's on some schedule though
        }
        scn.close();
        return ccs;
    }

    public static Set<String> scrape() throws IOException, TikaException, SAXException {
        BodyContentHandler boch = new BodyContentHandler();
        Metadata md = new Metadata();
        //bs_comp_status_sheet_2022.pdf vs bs_dsci_status_sheet_2022.pdf
        String status_sheet = "bs_comp_status_sheet_2022.pdf";

        FileInputStream fis = new FileInputStream(status_sheet);
        ParseContext pcont = new ParseContext();

        PDFParser pdfp = new PDFParser();
        pdfp.parse(fis,boch,md,pcont);

        //GeneralUtils.input(boch.toString());

        return filter(boch.toString());
    }

    public static String get_course_codes_from_line(String line, Set<String> ccs,String lastmajor) {
        //course code finder
        Scanner ccf = new Scanner(line);

        //course
        StringBuilder c = new StringBuilder();
        //keep track of last major recorded for weird cases
        while(ccf.hasNext()) {
            String n = ccf.next();
            //if we're starting a new course code, do it
            if((Pattern.matches("[A-Z]{4}",n) && !c4l.contains(n)) || (Pattern.matches("[A-Z]{3}",n) && !c3l.contains(n))) {
                c.append(n);
                lastmajor = n;
            }
            //if we're ending a course code, do it
            else if(Pattern.matches(".*\\d{3}.*",n)) {
                //normal case: append number to major
                if(n.length() == 3 && !c.isEmpty()) {
                    c.append(' ').append(n);
                    ccs.add(c.toString());
                }
                //weird case of numbers split by words + space: append number to last major seen
                else if(n.length() == 3) {
                    c.append(lastmajor).append(' ').append(n);
                    ccs.add(c.toString());
                }
                //weird case of numbers split by '/': append both numbers to major
                else if(!c.isEmpty()) {
                    ccs.add(c + " " + n.substring(0,3));
                    if(!n.substring(4).isEmpty()) ccs.add(c + " " + n.substring(4));
                }
                //where c has no course code and we have a nnn/nnn situation: append both numbers to last major seen
                else {
                    ccs.add(lastmajor + " " + n.substring(0,3));
                    if(!n.substring(4).isEmpty()) ccs.add(lastmajor + " " + n.substring(4));
                }
                c = new StringBuilder();
            }
        }
        ccf.close();
        return lastmajor;
    }

    public static void main(String[] args) throws IOException, TikaException, SAXException {
        ArrayList<String> all = new ArrayList<>();

        String[] allstrs;

        //if(args[0].equalsIgnoreCase("cs"))
        allstrs = new String[] {"comp 141","comp 155","comp 220","comp 222","comp 230","comp 244","comp 314","comp 325","comp 342","comp 205","comp 340","comp 350","comp 422","comp 424","comp 448","comp 451","comp 452",
                "comp 390","comp 401","comp 402","comp 435","comp 441","comp 442","comp 445","comp 446","comp 447","comp 475","robo 302","dsci 431","dsci 450","huma 102","huma 200",
                "huma 202","huma 301","huma 303","reli 211","reli 212","writ 101","econ 120","hist 120","hist 141","hist 204","pols 101","psyc 101","psyc 200","soci 101","soci 103","socw 101","phye 100","math 161","math 162",
                "math 213","math 214","math 222","stat 331","phys 101","phys 102","chem 105","chem 111","chem 113","chem 112","chem 114","biol 101","biol 102"};

        //TODO: if have time add dsci status sheet capability
        /*else if(args[0].equalsIgnoreCase("ds"))

        allstrs = new String[] {"huma 102","huma 200","huma 202","huma 301","huma 303","dsci 201","dsci 431","dsci 450","comp 141","comp 155","comp 220","comp 222","comp 244","comp 435","comp 233","comp 445","mngt 310",
                    "mngt 314","stat 412","comp 402","comp 435","comp 441","comp 442","comp 445","comp 446","comp 447","comp 475","robo 302","dsci 431","dsci 450","huma 102","huma 200",
                    "huma 202","huma 301","huma 303","reli 211","reli 212","writ 101","econ 120","hist 120","hist 141","hist 204","pols 101","psyc 101","psyc 200","soci 101","soci 103","socw 101","phye 100","math 161","math 162",
                    "math 213","math 214","math 222","stat 331","phys 101","phys 102","chem 105","chem 111","chem 113","chem 112","chem 114","biol 101","biol 102"};

        else {
            Main.afl.println("Error with command line args: must enter 'cs' or 'ds'");
            return;
        }*/


        for(String s : allstrs) all.add(s.toUpperCase());
        Collections.sort(all);

        BodyContentHandler boch = new BodyContentHandler();
        Metadata md = new Metadata();
        //bs_comp_status_sheet_2022.pdf vs bs_dsci_status_sheet_2022.pdf
        String status_sheet = "bs_comp_status_sheet_2022.pdf";

        FileInputStream fis = new FileInputStream(status_sheet);
        ParseContext pcont = new ParseContext();

        PDFParser pdfp = new PDFParser();
        pdfp.parse(fis,boch,md,pcont);

        //GeneralUtils.input(boch.toString());

        Set<String> filecontent = scrape();

        //get rid of 'biol ','chem ','phys ' (could say if len is less than 7, but let's try to find a better way)

        for(String s : all) Main.afl.printf("%s in filtered -> %b\n",s,filecontent.remove(s));
        ArrayList<String> fcsorted = new ArrayList<>(filecontent);
        Collections.sort(fcsorted);
        for(String s : fcsorted) Main.afl.println(s);

        /*Set<String> ccs = new HashSet<>();
        get_course_codes_from_line("fjdsklafjdsleiown fjdiei jfiewo ifjeow jfeiwo COMP 101 or 141 eiw woe jdn 19 fnek credits",ccs);
        Main.afl.println(ccs);
        get_course_codes_from_line("Tom hindale grax HIST 101 fjdk shame 21",ccs);
        Main.afl.println(ccs);
        get_course_codes_from_line("marly sand mist ECON 101/211 strange ART 300 fdje ieow.....",ccs);
        Main.afl.println(ccs);*/

        /*Scanner pdfscn = new Scanner(boch.toString());
        while(pdfscn.hasNext()) {
            String s = "[A-Z][A-Z][A-Z][A-Z] \\d\\d\\d";
        }

        //add slf4j-log4j12.jar to the classpath
        Main.afl.println("Content of PDF");
        Main.afl.println("----------------------------------------------------------------");
        Main.afl.println(boch);
        Main.afl.println("----------------------------------------------------------------\n");
        Main.afl.println("Metadata names");
        Main.afl.println("----------------------------------------------------------------");
        for(String s : md.names()) Main.afl.println(s);
        Main.afl.println("----------------------------------------------------------------");
        Main.afl.println(filter(boch.toString()));*/
    }
}