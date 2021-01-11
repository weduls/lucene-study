package simple;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

public class Searcher {

    public static void main(String args[]) throws IOException, ParseException {
        String indexDir = Searcher.class.getClassLoader().getResource("outputtxt").getPath();
        // parent가 포함된 document 검색
        String q = "patent";
        search(indexDir, q);
    }

    public static void search(String indexDir, String q) throws IOException, ParseException {
        Directory dir = FSDirectory.open(new File(indexDir));
        IndexSearcher is = new IndexSearcher(dir);

        // 버전별 파서와 analyzer 사용 (쿼리 생성)
        QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", new StandardAnalyzer(Version.LUCENE_30));
        Query query = parser.parse(q);

        long start = System.currentTimeMillis();
        // 검색 된 객체 10개 가져오기
        TopDocs hits = is.search(query, 10);
        long end = System.currentTimeMillis();

        System.err.println("Found" + hits.totalHits + " document (s) (in " + (end - start) + " milliseconds) that matched query '" + q + "';");

        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);
            // 필드 결과 보기
            System.out.println(doc.get("fullpath"));
        }

        is.close();
    }

}
