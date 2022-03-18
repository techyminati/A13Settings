package com.android.settingslib.license;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
class LicenseHtmlGeneratorFromXml {
    private final List<File> mXmlFiles;
    private final Map<String, Map<String, Set<String>>> mFileNameToLibraryToContentIdMap = new HashMap();
    private final Map<String, String> mContentIdToFileContentMap = new HashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ContentIdAndFileNames {
        final String mContentId;
        final Map<String, List<String>> mLibraryToFileNameMap = new TreeMap();

        ContentIdAndFileNames(String str) {
            this.mContentId = str;
        }
    }

    private LicenseHtmlGeneratorFromXml(List<File> list) {
        this.mXmlFiles = list;
    }

    public static boolean generateHtml(List<File> list, File file, String str) {
        return new LicenseHtmlGeneratorFromXml(list).generateHtml(file, str);
    }

    private boolean generateHtml(File file, String str) {
        Throwable e;
        PrintWriter printWriter;
        for (File file2 : this.mXmlFiles) {
            parse(file2);
        }
        if (!this.mFileNameToLibraryToContentIdMap.isEmpty() && !this.mContentIdToFileContentMap.isEmpty()) {
            PrintWriter printWriter2 = null;
            try {
                printWriter = new PrintWriter(file);
            } catch (FileNotFoundException | SecurityException e2) {
                e = e2;
            }
            try {
                generateHtml(this.mFileNameToLibraryToContentIdMap, this.mContentIdToFileContentMap, printWriter, str);
                printWriter.flush();
                printWriter.close();
                return true;
            } catch (FileNotFoundException | SecurityException e3) {
                e = e3;
                printWriter2 = printWriter;
                Log.e("LicenseGeneratorFromXml", "Failed to generate " + file, e);
                if (printWriter2 != null) {
                    printWriter2.close();
                }
                return false;
            }
        }
        return false;
    }

    private void parse(File file) {
        if (file != null && file.exists() && file.length() != 0) {
            InputStreamReader inputStreamReader = null;
            try {
                if (file.getName().endsWith(".gz")) {
                    inputStreamReader = new InputStreamReader(new GZIPInputStream(new FileInputStream(file)));
                } else {
                    inputStreamReader = new FileReader(file);
                }
                parse(inputStreamReader, this.mFileNameToLibraryToContentIdMap, this.mContentIdToFileContentMap);
                inputStreamReader.close();
            } catch (IOException | XmlPullParserException e) {
                Log.e("LicenseGeneratorFromXml", "Failed to parse " + file, e);
                if (inputStreamReader != null) {
                    try {
                        inputStreamReader.close();
                    } catch (IOException unused) {
                        Log.w("LicenseGeneratorFromXml", "Failed to close " + file);
                    }
                }
            }
        }
    }

    static void parse(InputStreamReader inputStreamReader, Map<String, Map<String, Set<String>>> map, Map<String, String> map2) throws XmlPullParserException, IOException {
        HashMap hashMap = new HashMap();
        Map<? extends String, ? extends String> hashMap2 = new HashMap<>();
        XmlPullParser newPullParser = Xml.newPullParser();
        newPullParser.setInput(inputStreamReader);
        newPullParser.nextTag();
        newPullParser.require(2, "", "licenses");
        for (int eventType = newPullParser.getEventType(); eventType != 1; eventType = newPullParser.next()) {
            if (eventType == 2) {
                if ("file-name".equals(newPullParser.getName())) {
                    String attributeValue = newPullParser.getAttributeValue("", "contentId");
                    String attributeValue2 = newPullParser.getAttributeValue("", "lib");
                    if (!TextUtils.isEmpty(attributeValue)) {
                        String trim = readText(newPullParser).trim();
                        if (!TextUtils.isEmpty(trim)) {
                            ((Set) ((Map) hashMap.computeIfAbsent(trim, LicenseHtmlGeneratorFromXml$$ExternalSyntheticLambda5.INSTANCE)).computeIfAbsent(attributeValue2, LicenseHtmlGeneratorFromXml$$ExternalSyntheticLambda4.INSTANCE)).add(attributeValue);
                        }
                    }
                } else if ("file-content".equals(newPullParser.getName())) {
                    String attributeValue3 = newPullParser.getAttributeValue("", "contentId");
                    if (!TextUtils.isEmpty(attributeValue3) && !map2.containsKey(attributeValue3) && !hashMap2.containsKey(attributeValue3)) {
                        String readText = readText(newPullParser);
                        if (!TextUtils.isEmpty(readText)) {
                            hashMap2.put(attributeValue3, readText);
                        }
                    }
                }
            }
        }
        for (Map.Entry entry : hashMap.entrySet()) {
            map.merge((String) entry.getKey(), (Map) entry.getValue(), LicenseHtmlGeneratorFromXml$$ExternalSyntheticLambda0.INSTANCE);
        }
        map2.putAll(hashMap2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Map lambda$parse$0(String str) {
        return new HashMap();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Set lambda$parse$1(String str) {
        return new HashSet();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Map lambda$parse$3(Map map, Map map2) {
        for (Map.Entry entry : map2.entrySet()) {
            map.merge((String) entry.getKey(), (Set) entry.getValue(), LicenseHtmlGeneratorFromXml$$ExternalSyntheticLambda2.INSTANCE);
        }
        return map;
    }

    private static String readText(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        StringBuffer stringBuffer = new StringBuffer();
        int next = xmlPullParser.next();
        while (next == 4) {
            stringBuffer.append(xmlPullParser.getText());
            next = xmlPullParser.next();
        }
        return stringBuffer.toString();
    }

    static void generateHtml(Map<String, Map<String, Set<String>>> map, Map<String, String> map2, PrintWriter printWriter, String str) {
        int i;
        ArrayList<String> arrayList = new ArrayList();
        arrayList.addAll(map.keySet());
        Collections.sort(arrayList);
        TreeMap treeMap = new TreeMap();
        for (Map<String, Set<String>> map3 : map.values()) {
            for (Map.Entry<String, Set<String>> entry : map3.entrySet()) {
                if (!TextUtils.isEmpty(entry.getKey())) {
                    treeMap.merge(entry.getKey(), entry.getValue(), LicenseHtmlGeneratorFromXml$$ExternalSyntheticLambda1.INSTANCE);
                }
            }
        }
        printWriter.println("<html><head>\n<style type=\"text/css\">\nbody { padding: 0; font-family: sans-serif; }\n.same-license { background-color: #eeeeee;\n                border-top: 20px solid white;\n                padding: 10px; }\n.label { font-weight: bold; }\n.file-list { margin-left: 1em; color: blue; }\n</style>\n</head><body topmargin=\"0\" leftmargin=\"0\" rightmargin=\"0\" bottommargin=\"0\">\n<div class=\"toc\">\n");
        if (!TextUtils.isEmpty(str)) {
            printWriter.println(str);
        }
        HashMap hashMap = new HashMap();
        ArrayList<ContentIdAndFileNames> arrayList2 = new ArrayList();
        if (!treeMap.isEmpty()) {
            printWriter.println("<strong>Libraries</strong>\n<ul class=\"libraries\">");
            i = 0;
            for (Map.Entry entry2 : treeMap.entrySet()) {
                Object obj = (String) entry2.getKey();
                for (String str2 : (Set) entry2.getValue()) {
                    if (!hashMap.containsKey(str2)) {
                        hashMap.put(str2, Integer.valueOf(i));
                        arrayList2.add(new ContentIdAndFileNames(str2));
                        i++;
                    }
                    printWriter.format("<li><a href=\"#id%d\">%s</a></li>\n", Integer.valueOf(((Integer) hashMap.get(str2)).intValue()), obj);
                }
            }
            printWriter.println("</ul>\n<strong>Files</strong>");
        } else {
            i = 0;
        }
        for (String str3 : arrayList) {
            for (Map.Entry<String, Set<String>> entry3 : map.get(str3).entrySet()) {
                String key = entry3.getKey();
                if (key == null) {
                    key = "";
                }
                for (String str4 : entry3.getValue()) {
                    if (!hashMap.containsKey(str4)) {
                        hashMap.put(str4, Integer.valueOf(i));
                        arrayList2.add(new ContentIdAndFileNames(str4));
                        i++;
                    }
                    int intValue = ((Integer) hashMap.get(str4)).intValue();
                    ((ContentIdAndFileNames) arrayList2.get(intValue)).mLibraryToFileNameMap.computeIfAbsent(key, LicenseHtmlGeneratorFromXml$$ExternalSyntheticLambda3.INSTANCE).add(str3);
                    if (TextUtils.isEmpty(key)) {
                        printWriter.format("<li><a href=\"#id%d\">%s</a></li>\n", Integer.valueOf(intValue), str3);
                    } else {
                        printWriter.format("<li><a href=\"#id%d\">%s - %s</a></li>\n", Integer.valueOf(intValue), str3, key);
                    }
                }
            }
        }
        printWriter.println("</ul>\n</div><!-- table of contents -->\n<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
        int i2 = 0;
        for (ContentIdAndFileNames contentIdAndFileNames : arrayList2) {
            printWriter.format("<tr id=\"id%d\"><td class=\"same-license\">\n", Integer.valueOf(i2));
            for (Map.Entry<String, List<String>> entry4 : contentIdAndFileNames.mLibraryToFileNameMap.entrySet()) {
                String key2 = entry4.getKey();
                if (TextUtils.isEmpty(key2)) {
                    printWriter.println("<div class=\"label\">Notices for file(s):</div>");
                } else {
                    printWriter.format("<div class=\"label\"><strong>%s</strong> used by:</div>\n", key2);
                }
                printWriter.println("<div class=\"file-list\">");
                Iterator<String> it = entry4.getValue().iterator();
                while (it.hasNext()) {
                    printWriter.format("%s <br/>\n", (String) it.next());
                }
                printWriter.println("</div><!-- file-list -->");
                i2++;
            }
            printWriter.println("<pre class=\"license-text\">");
            printWriter.println(map2.get(contentIdAndFileNames.mContentId));
            printWriter.println("</pre><!-- license-text -->");
            printWriter.println("</td></tr><!-- same-license -->");
        }
        printWriter.println("</table></body></html>");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ List lambda$generateHtml$5(String str) {
        return new ArrayList();
    }
}
