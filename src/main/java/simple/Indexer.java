import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

public class Indexer {

    public static void main(String args[]) throws IOException {
        // 여기에 지정한 디렉터리에 색인 생성
        String indexDir = Indexer.class.getResource("/output").getPath();
        // 여기에 지정한 디렉터리에 담김 txt 파일
        String dataDir = Indexer.class.getResource("/inputtxt").getPath();

        long start = System.currentTimeMillis();
        Indexer indexer = new Indexer(indexDir);
        int numIndexed = 0;

        try {
            numIndexed = indexer.index(dataDir, new TextFilesFilter());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            indexer.close();
        }

        long end = System.currentTimeMillis();

        System.out.println("Indexing " + numIndexed + "file took " + (end - start) + " millisecond");
    }

    private IndexWriter writer;

    /**
     * IndexWriter 생성
     */
    public Indexer(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(new File(indexDir));

        // lucene 버전을 넘겨서 Analyzer 버전 선택 가능
        writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30), true, IndexWriter.MaxFieldLength.UNLIMITED);
    }

    public void close() throws IOException {
        writer.close();
    }

    public int index(String dataDir, FileFilter filter) throws Exception {
        File[] files = new File(dataDir).listFiles();

        for (File f : files) {
            if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead() && (filter == null || filter.accept(f))) {
                indexFile(f);
            }
        }

        // 색인된 문서 건수 반환
        return writer.numDocs();
    }

    private static class TextFilesFilter implements FileFilter {
        @Override
        public boolean accept(File path) {
            return path.getName().toLowerCase().endsWith(".txt");
        }
    }

    protected Document getDocument(File f) throws Exception {
        Document doc = new Document();
        // 파일 컨텐츠 필드 추가
        doc.add(new Field("content", new FileReader(f)));
        // 파일 이름 추가
        doc.add(new Field("filename", f.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        // 경로 추가
        doc.add(new Field("fullpath", f.getCanonicalPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        return doc;
    }

    public void indexFile(File file) throws Exception {
        System.out.println("Indexing " + file.getCanonicalPath());
        Document document = getDocument(file);
        writer.addDocument(document);
    }

}
