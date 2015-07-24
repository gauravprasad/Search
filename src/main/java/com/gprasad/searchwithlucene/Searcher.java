/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gprasad.searchwithlucene;

import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author gq6pras
 */
public class Searcher {

    private static IndexSearcher indexSearcher;
    private static Query query;

    public static void main(String[] args) throws Exception {
        if (args == null || args.length < 3) {
            throw new Exception("NEED TO PASS THE RIGHT PARAMETER . 1st need to be IndexDirectory, 2nd need to be search query and 3rd Max Hits");
        }
        String indexPath = args[0];
        String queryString = args[1];
        int maxHit = Integer.parseInt(args[2]);
        createIndexSearcher(indexPath);
        prepareQuery(queryString);
        performSearch(maxHit);
                
    }

    private static void createIndexSearcher(String indexPath) throws IOException {
        Directory directory = FSDirectory.open(Paths.get(indexPath));
        DirectoryReader directoryReader = DirectoryReader.open(directory);
        indexSearcher = new IndexSearcher(directoryReader);
    }

    private static void prepareQuery(String queryString) throws ParseException {
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser queryParser = new QueryParser("contents", analyzer);
        query = queryParser.parse(queryString);
    }
    private static void performSearch(int maxHit) throws IOException
    {
        TopDocs topDocs = indexSearcher.search(query, maxHit);
        
        for(ScoreDoc scoreDoc : topDocs.scoreDocs)
        {
            Document document = indexSearcher.doc(scoreDoc.doc);
            String dataFilePath = document.get("filename");
            System.out.println("File Containg Searched Word : "+dataFilePath);
        }
    }
}
