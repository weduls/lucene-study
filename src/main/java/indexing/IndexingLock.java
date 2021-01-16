package indexing;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import util.TestUtil;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class IndexingLock {

    private static Directory dir;
    private static File indexDir;

    public static void main(String args[]) throws IOException {
        setUp();
        testWriteLock();
    }

    protected static void setUp() throws IOException {
        indexDir = new File(System.getProperty("java.io.tmpdir", "tmp") + System.getProperty("file.separator") + "index");
        dir = FSDirectory.open(indexDir);
    }

    // writer1이 indexing dir 사용하고 있어서 writer2가 접근하려 하면 색인 락에 의해서 LockObtainFailedException이 발생
    public static void testWriteL을ock() throws IOException {
        IndexWriter writer1 = new IndexWriter(dir, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
        IndexWriter writer2 = null;

        try {
            writer2 = new IndexWriter(dir, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
            fail("we should never reach this point");
        } catch (LockObtainFailedException e) {
            e.printStackTrace();
        } finally {
            writer1.close();
            assertNull(writer2);
            TestUtil.rmDir(indexDir);
        }
    }

}
