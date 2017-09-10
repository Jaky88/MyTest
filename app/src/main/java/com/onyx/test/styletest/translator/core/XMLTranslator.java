package com.onyx.test.styletest.translator.core;

import android.util.Log;

import com.google.gson.Gson;
import com.onyx.test.styletest.translator.config.Constants;
import com.onyx.test.styletest.translator.config.Language;
import com.onyx.test.styletest.translator.entity.BaiduResult;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import okhttp3.ResponseBody;

public abstract class XMLTranslator implements Translator {

    private String filePath;

    public XMLTranslator(String filePath) {
        this.filePath = filePath;
    }


    @Override
    public void translate(final Language src, final Language target) {
        File currentPath = new File(filePath);
        Document document = openDocument(currentPath);
        parseXmlAndTranslate(document, src, target);
        writeDocument(currentPath.getParentFile().getAbsolutePath(), document, target);
    }

    private Document openDocument(File currentPath) {
        String xmlFileName = currentPath.getAbsolutePath();
        Log.d("=========", "=====openDocument=====xmlFileName=====" + xmlFileName);
        String xmlContent = loadXmlFile(xmlFileName);
        return getDocument(xmlContent);
    }

    private String loadXmlFile(String path) {
        try {
            FileInputStream fis = new FileInputStream(new File(path));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String result = "";
            String line;
            while ((line = reader.readLine()) != null) {
                result += line + "\n";
            }

            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Document getDocument(String xml) {
        if (xml != null) {
            try {
                return DocumentHelper.parseText(xml);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void parseXmlAndTranslate(Document document, Language src, Language target) {
        if (document == null) {
            Log.d("==========", "===========document is null==============");
            return;
        }
        Log.d("====", "========parseXmlAndTranslate========");
        Element rootElement = document.getRootElement();
        Iterator<Element> iterator = rootElement.elementIterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            innerTranslate(element, src, target);
        }
        Log.d("====", "========parseXmlAndTranslate==over======");
    }

    private void innerTranslate(final Element element, final Language src, final Language target) {
        String content = element.getText().toString().trim();
        if (content == null) {
            return;
        }
        BaiduResult baiduResult = null;
        try {
            ResponseBody body = getNetTranslate(initParams(content, src, target)).execute().body();
            try {
                baiduResult = new Gson().fromJson(body.string(), BaiduResult.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (baiduResult != null && baiduResult.getTrans_result() != null) {
                String ret = baiduResult.getTrans_result().get(0).dst;
                if (ret != null) {
                    Log.d("==========", "======content=="+content+"====baiduResult=========" + ret);
                    element.setText(ret);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void writeDocument(String parentPath, Document document, Language target) {
        if (document == null) {
            Log.d("====", "========writeDocument======document == null==");
            return;
        }
        File dir = new File(parentPath + File.separator + getValuesFolderName(target));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String xml = document.asXML();
        try {
            FileOutputStream fos = new FileOutputStream(new File(dir.getAbsolutePath() +
                    File.separator + new File(filePath).getName()));
            fos.write(xml.getBytes());
            fos.flush();
            fos.close();
            Log.d("====", "========writeDocument========");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getValuesFolderName(Language target) {
        return Constants.valuesFolders.get(target);
    }
}
