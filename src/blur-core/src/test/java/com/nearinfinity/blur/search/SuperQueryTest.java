package com.nearinfinity.blur.search;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.util.UUID;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import com.nearinfinity.blur.lucene.index.SuperDocument;
import com.nearinfinity.blur.lucene.search.SuperQuery;
import com.nearinfinity.blur.manager.IndexManager;

public class SuperQueryTest {
	
    @Test
	public void testSimpleSuperQuery() throws CorruptIndexException, IOException, InterruptedException {
		BooleanQuery booleanQuery = new BooleanQuery();
		booleanQuery.add(wrapSuper(new TermQuery(new Term("person.name","aaron"))), Occur.MUST);
		booleanQuery.add(wrapSuper(new TermQuery(new Term("address.street","sulgrave"))), Occur.MUST);
		
		Directory directory = createIndex();
		IndexReader reader = IndexManager.warmUpPrimeDocBitSets(IndexReader.open(directory));
		printAll(new Term("person.name","aaron"),reader);
		printAll(new Term("address.street","sulgrave"),reader);
		printAll(new Term(SuperDocument.PRIME_DOC,SuperDocument.PRIME_DOC_VALUE),reader);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs topDocs = searcher.search(booleanQuery, 10);
		assertEquals(2, topDocs.totalHits);
		assertEquals("1",searcher.doc(topDocs.scoreDocs[0].doc).get(SuperDocument.ID));
		assertEquals("3",searcher.doc(topDocs.scoreDocs[1].doc).get(SuperDocument.ID));
		
	}

	private void printAll(Term term, IndexReader reader) throws IOException {
		TermDocs termDocs = reader.termDocs(term);
		while (termDocs.next()) {
			System.out.println(term + "=>" + termDocs.doc());
		}
	}

	public static Directory createIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
		Directory directory = new RAMDirectory();
		IndexWriter writer = new IndexWriter(directory, new StandardAnalyzer(Version.LUCENE_30), MaxFieldLength.UNLIMITED);
		IndexManager.replace(writer, create("1","person.name:aaron","address.street:sulgrave"));
		IndexManager.replace(writer, create("2","person.name:hannah","address.street:sulgrave"));
		IndexManager.replace(writer, create("3","person.name:aaron","address.street:sulgrave court"));
		writer.close();
		return directory;
	}

	public static SuperDocument create(String id, String... parts) {
		SuperDocument document = new SuperDocument(id);
		for (String part : parts) {
			String[] split = part.split(":");
			String value = split[1];
			String[] split2 = split[0].split("\\.");
			String superName = split2[0];
			String fieldName = split2[1];
			document.addFieldAnalyzedNoNorms(superName, UUID.randomUUID().toString(), fieldName, value);
		}
		return document;
	}

	private Query wrapSuper(Query query) {
		return new SuperQuery(query);
	}

}