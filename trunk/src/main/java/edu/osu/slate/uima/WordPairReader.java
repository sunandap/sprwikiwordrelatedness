package edu.osu.slate.uima;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.FileUtils;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

public class WordPairReader extends CollectionReader_ImplBase {

  /**
   * Name of configuration parameter that must be set to the path of a directory containing input
   * files.
   */
  public static final String PARAM_INPUTFILE = "InputFile";

  /**
   * Name of configuration parameter that contains the character encoding used by the input files.
   * If not specified, the default system encoding will be used.
   */
  public static final String PARAM_ENCODING = "Encoding";

  /**
   * Name of optional configuration parameter that contains the language of the documents in the
   * input directory. If specified this information will be added to the CAS.
   */
  public static final String PARAM_LANGUAGE = "Language";
  
  private ArrayList<File> mFiles;

  private String mEncoding;

  private String mLanguage;
  
  private int mCurrentIndex;
  
  @Override
  public void initialize() throws ResourceInitializationException {
    File f = new File(((String) getConfigParameterValue(PARAM_INPUTFILE)).trim());
    mEncoding  = (String) getConfigParameterValue(PARAM_ENCODING);
    mLanguage  = (String) getConfigParameterValue(PARAM_LANGUAGE);
    mCurrentIndex = 0;

    // if input directory does not exist or is not a directory, throw exception
    if (!f.exists()) {
      throw new ResourceInitializationException(ResourceConfigurationException.DIRECTORY_NOT_FOUND,
              new Object[] { PARAM_INPUTFILE, this.getMetaData().getName(), f.getPath() });
    }//end: if(!f)
    
    mFiles = new ArrayList<File>();
    mFiles.add(f);
  }
  
  public void getNext(CAS aCAS) throws IOException, CollectionException {
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new CollectionException(e);
    }

    // open input stream to file
    File file = (File) mFiles.get(mCurrentIndex++);
    String text = FileUtils.file2String(file, mEncoding);
      // put document in CAS
    jcas.setDocumentText(text);

    // set language if it was explicitly specified as a configuration parameter
    if (mLanguage != null) {
      ((DocumentAnnotation) jcas.getDocumentAnnotationFs()).setLanguage(mLanguage);
    }

    // Also store location of source document in CAS. This information is critical
    // if CAS Consumers will need to know where the original document contents are located.
    // For example, the Semantic Search CAS Indexer writes this information into the
    // search index that it creates, which allows applications that use the search index to
    // locate the documents that satisfy their semantic queries.
    SourceDocumentInformation srcDocInfo = new SourceDocumentInformation(jcas);
    srcDocInfo.setUri(file.getAbsoluteFile().toURL().toString());
    srcDocInfo.setOffsetInSource(0);
    srcDocInfo.setDocumentSize((int) file.length());
    srcDocInfo.setLastSegment(mCurrentIndex == mFiles.size());
    srcDocInfo.addToIndexes();
  }

  public void close() throws IOException {  }

  public Progress[] getProgress() {
    return new Progress[] { new ProgressImpl(mCurrentIndex, mFiles.size(), Progress.ENTITIES) };
  }

  public boolean hasNext() throws IOException, CollectionException {
    return mCurrentIndex < mFiles.size();
  }

}
