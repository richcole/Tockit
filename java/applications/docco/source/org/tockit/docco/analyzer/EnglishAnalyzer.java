package org.tockit.docco.analyzer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * This implements a basic analyzer for English.
 * 
 * It uses Porter's stemming algorithm to stem the words, otherwise it is
 * pretty much the same as the StandardAnalyzer (since it is based on that
 * code). 
 * 
 * For some reason this combination is not directly supported by
 * Lucene, despite providing all the ingredients.
 */
public class EnglishAnalyzer extends Analyzer {
	  private Set stopSet;

	  /** An array containing some common English words that are usually not
	  useful for searching. */
	  public static final String[] STOP_WORDS = StopAnalyzer.ENGLISH_STOP_WORDS;

	  /** Builds an analyzer with the default stop words ({@link #STOP_WORDS}). */
	  public EnglishAnalyzer() {
	    this(STOP_WORDS);
	  }

	  /** Builds an analyzer with the given stop words. */
	  public EnglishAnalyzer(Set stopWords) {
	    stopSet = stopWords;
	  }

	  /** Builds an analyzer with the given stop words. */
	  public EnglishAnalyzer(String[] stopWords) {
	    stopSet = StopFilter.makeStopSet(stopWords);
	  }

	  /** Builds an analyzer with the stop words from the given file.
	   * @see WordlistLoader#getWordSet(File)
	   */
	  public EnglishAnalyzer(File stopwords) throws IOException {
	    stopSet = WordlistLoader.getWordSet(stopwords);
	  }

	  /** Builds an analyzer with the stop words from the given reader.
	   * @see WordlistLoader#getWordSet(Reader)
	   */
	  public EnglishAnalyzer(Reader stopwords) throws IOException {
	    stopSet = WordlistLoader.getWordSet(stopwords);
	  }

	  /** Constructs a {@link StandardTokenizer} filtered by a {@link
	  StandardFilter}, a {@link LowerCaseFilter}, a {@link StopFilter} and
	  a {@link PorterStemFilter}. */
	  public TokenStream tokenStream(String fieldName, Reader reader) {
	    TokenStream result = new StandardTokenizer(reader);
	    result = new StandardFilter(result);
	    result = new LowerCaseFilter(result);
	    result = new StopFilter(result, stopSet);
 		return new PorterStemFilter(result);
	}
}