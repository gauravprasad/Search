/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gprasad.searchwithlucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author gprasad
 */
public class Indexer {

    private static IndexWriter writer;
    private static Long count = 0l;
    private static Long totalFile = 0l;
    private static Long totalDir = 0l;

    public static void main(String[] str) throws Exception {

        if (str == null || str.length != 2) {
            throw new Exception(" Check the Argument Passed ... !!!\nNEED TO PASS THE INDEX & DATA FILE DIR DETAILS");
        }
        try {
            String indexPath = str[0];
            String docPath = str[1];
            createIndex(indexPath);
            getFileFromDIR(new File(docPath));
            writer.commit();
            writer.close();
            System.out.println(">> TOTAL FILE INDEXED : " + count);
            System.out.println(">> TOTAL FILE IN DATADIR : " + totalFile);
            System.out.println(">> TOTAL FOLDER IN DATADIR : " + totalDir);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createIndex(String indexPath) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(OpenMode.CREATE);
        writer = new IndexWriter(dir, indexWriterConfig);
    }

    private static void getFileFromDIR(File fileCheck) throws IOException {
        File file = null;
        File[] files = fileCheck.listFiles();
        if (files != null) {
            for (File newFile : files) {
                if (newFile.isDirectory()) {
                    totalDir++;
                    getFileFromDIR(newFile);
                } else if (newFile.isFile()) {
                    fileToIndexDoc(newFile);
                }
            }
        }
        return;
    }

    private static void fileToIndexDoc(File file) throws IOException {
        System.out.println(">> EXT : " + getFileExtension(file));
        totalFile++;
        if (getFileExtension(file).equalsIgnoreCase("java")) {
            System.out.println(">> File Indexed : " + file.getCanonicalPath());
            Document doc = new Document();
            doc.add(new Field("contents", new FileReader(file)));
            doc.add(new Field("filename", file.getCanonicalPath(), Field.Store.YES, Field.Index.ANALYZED));
            writer.addDocument(doc);
            count++;
        }
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
}
