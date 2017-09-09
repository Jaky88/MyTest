package com.onyx.test.styletest.translator.xmlImpl;

import android.support.annotation.Nullable;
import android.util.Log;

import com.onyx.test.styletest.translator.config.Language;
import com.onyx.test.styletest.translator.config.Constants;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lion on 2016/10/28.
 * android string.xmlImpl 字符串资源翻译器读写管理，并且调用翻译REST_API进行翻译。
 * 基本逻辑如下：
 * 1. 读取xml文件
 * 2. 遍历xml标签，逐一进行翻译
 * 3. 将翻译完成的xml保存的新的文件中
 */
public abstract class XMLTranslator implements Translator {

    private String filePath;

    public XMLTranslator(String filePath) {
        this.filePath = filePath;
    }


    @Override
    public void translate(final Language src, final Language target) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                File currentPath = new File(filePath);
                Document document = openDocument(currentPath);
                parseXmlAndTranslate(document, src, target);
                writeDocument(currentPath.getParentFile().getAbsolutePath(), document, target);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d("=========", "=====XMLTranslator=====onCompleted=====");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String str) {
                    }
                });

    }

    private Document openDocument(File currentPath) {
        String xmlFileName = currentPath.getAbsolutePath();
        Log.d("=========", "=====XMLTranslator=====xmlFileName=====" + xmlFileName);
        String xmlContent = loadXmlFile(xmlFileName);
        Log.d("=========", "=====XMLTranslator=====xmlContent=====" + xmlContent);
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
            System.out.println("=======document is null============");
            return;
        }
        Element rootElement = document.getRootElement();
        Iterator<Element> iterator = rootElement.elementIterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            Log.d("=========", "=====XMLTranslator=====element.getText()=====" + element.getText());
//            System.out.println(element.attribute("name").getValue());
//            System.out.println(element.getText());
            String result = innerTranslate(element.getText(), src, target);
            if (result != null) {
                element.setText(result);
            }
        }
    }

    //网络请求进行翻译
    private String innerTranslate(final String sourceString, final Language src, final Language target) {
        if (sourceString == null) {
            return null;
        }
        String url = onGenerateUrl(sourceString, src, target);
//        Log.d("=========", "=====XMLTranslator==innerTranslate====url======" + url);

        String result = getResult(url);
        if (result != null) return result;
        return null;
    }

    @Nullable
    private String getResult(String strUrl) {
        try {

            URL url = new URL(strUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(30000);
            httpURLConnection.setReadTimeout(30000);

            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);


            URLConnection connection = new URL(strUrl).openConnection();
            connection.connect();
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String result = "";
            String line = "";
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            Log.d("=========", "=====XMLTranslator=====result=====" + result);
            return onTranslateFinished(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeDocument(String parentPath, Document document, Language target) {
        if (document == null) {
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
            fos.close();
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
