//Name: Kanrawee Chiamsakul
//ID: 6188049
//Name: Tharit Chantanalertvilai
//ID: 6188068
//Name: Thanyanit Jongjitragan
//ID: 6188075
//Section: 2

import java.util.*;

public class TFIDFSearcher extends Searcher
{	
	// create a map of term and number of document in documents that contain the term
	private Map<String, Integer> df_map = new HashMap<>();

	private double corpus_size; // -> |D|
	
	public TFIDFSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/		
		// create a map of term and set of document's id in documents that contain the term
		Map<String, Set<Integer>> terms = new HashMap<>();

		for(Document doc:documents) {
			for(String term:doc.getTokens()) {
				if(terms.containsKey(term) == false) {
					terms.put(term, new TreeSet<>());
				}
				terms.get(term).add(doc.getId());
			}
		}

		for(String term : terms.keySet()) {
			df_map.put(term, terms.get(term).size());
		}
		
		corpus_size = documents.size();
		
		/***********************************************/
	}
	
	// find tf(t,x)
	private double findTF(String t, List<String> x)
	{
		if(x.contains(t) == false)
		{
			return 0;
		}
		else
		{
			return 1 + Math.log10(Collections.frequency(x, t));
		}
	}

	// find df(t,D)
	private int findDF(String t)
	{
		if(df_map.containsKey(t)) {
			return df_map.get(t);	
		}
		return 1;
	}
	
	// find idf(t,D)
	private double findIDF(String t)
	{
		return Math.log10(1 + (corpus_size / findDF(t)));
	}
	
	// find w(t,x,D)
	private double findTFIDF(String t, List<String> x)
	{
		return findTF(t, x) * findIDF(t);
	}

	// calculate TFIDF score -> Cosine(q,d)
	private double cosineSimilarity(List<Double> q_vector, List<Double> d_vector)
	{
		double numerator = 0;
		double denominator_q = 0;
		double denominator_d = 0;

		for(int i=0; i<q_vector.size(); i++)
		{
			double q_value = q_vector.get(i);
			double d_value = d_vector.get(i);

			numerator += q_value * d_value;
			denominator_q += Math.pow(q_value, 2);
			denominator_d += Math.pow(d_value, 2);
		}

		if(denominator_q == 0 || denominator_d == 0)
		{
			return Double.NaN;
		} 
		else
		{
			return numerator / (Math.sqrt(denominator_q) * Math.sqrt(denominator_d));
		}

	}

	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/
		// create results List
		List<SearchResult> res = new ArrayList<SearchResult>();

		// tokenize query
		List<String> q_tokens = super.tokenize(queryString);
		
		// loop the documents in corpus
		for(Document doc:documents)
		{
			List<Double> q_vector = new ArrayList<Double>();
			List<Double> d_vector = new ArrayList<Double>();

			// tokenize document's raw text
			List<String> raw_tokens = doc.getTokens();

			// union the tokenized query and tokenized document
			List<String> union = new ArrayList<String>();
			union.addAll(q_tokens);
			union.addAll(raw_tokens);
			union = new ArrayList<String>(new LinkedHashSet<String>(union));
			
			for(String term:union)
			{
				q_vector.add(q_tokens.contains(term) ? findTFIDF(term, q_tokens) : 0);
				d_vector.add(raw_tokens.contains(term) ? findTFIDF(term, raw_tokens) : 0);
			}
			
			// calculate TFIDF score -> Cosine(q,d)
			double score = cosineSimilarity(q_vector, d_vector);
			
			SearchResult doc_res = new SearchResult(doc, score);
			res.add(doc_res);
		}

		// sort by score
		Collections.sort(res);
		
		// return top k search results
		return (res.size() > k) ? res.subList(0, k) : res;
		// return res;
		/***********************************************/
	}
}
