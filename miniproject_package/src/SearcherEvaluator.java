//Name: Kanrawee Chiamsakul
//ID: 6188049
//Name: Tharit Chantanalertvilai
//ID: 6188068
//Name: Thanyanit Jongjitragan
//ID: 6188075
//Section: 2

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.FileUtils;

public class SearcherEvaluator {
	private List<Document> queries = null;				//List of test queries. Each query can be treated as a Document object.
	private  Map<Integer, Set<Integer>> answers = null;	//Mapping between query ID and a set of relevant document IDs
	
	public List<Document> getQueries() {
		return queries;
	}

	public Map<Integer, Set<Integer>> getAnswers() {
		return answers;
	}

	/**
	 * Load queries into "queries"
	 * Load corresponding documents into "answers"
	 * Other initialization, depending on your design.
	 * @param corpus
	 */
	public SearcherEvaluator(String corpus)
	{
		String queryFilename = corpus+"/queries.txt";
		String answerFilename = corpus+"/relevance.txt";
		
		//load queries. Treat each query as a document. 
		this.queries = Searcher.parseDocumentFromFile(queryFilename);
		this.answers = new HashMap<Integer, Set<Integer>>();
		//load answers
		try {
			List<String> lines = FileUtils.readLines(new File(answerFilename), "UTF-8");
			for(String line: lines)
			{
				line = line.trim();
				if(line.isEmpty()) continue;
				String[] parts = line.split("\\t");
				Integer qid = Integer.parseInt(parts[0]);
				String[] docIDs = parts[1].trim().split("\\s+");
				Set<Integer> relDocIDs = new HashSet<Integer>();
				for(String docID: docIDs)
				{
					relDocIDs.add(Integer.parseInt(docID));
				}
				this.answers.put(qid, relDocIDs);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Returns an array of 3 numbers: precision, recall, F1, computed from the top *k* search results 
	 * returned from *searcher* for *query*
	 * @param query
	 * @param searcher
	 * @param k
	 * @return
	 */
	public double[] getQueryPRF(Document query, Searcher searcher, int k)
	{
		/*********************** YOUR CODE HERE *************************/
		// retrieve the top k search results
		List<SearchResult> search_res = searcher.search(query.getRawText(), k);

		// create a set of relevant document IDs
		Set<Integer> relDocIDs = answers.get(query.getId());
		
		// find the number of true positives
		double count_tp = 0;
		for(SearchResult res:search_res)
		{
			// System.out.println(res);
			if(relDocIDs.contains(res.getDocument().getId()))
			{
				count_tp++;
			}
		}

		// find precision
		double precision = count_tp / (double) search_res.size();
		// find recall
		double recall = count_tp / (double) relDocIDs.size();
		// find F1
		double F1 = (precision == 0 && recall == 0)? 0 : (2 * precision * recall) / (double) (precision + recall);

		return new double[] {precision, recall, F1};
		/****************************************************************/
	}
	
	/**
	 * Test all the queries in *queries*, from the top *k* search results returned by *searcher*
	 * and take the average of the precision, recall, and F1. 
	 * @param searcher
	 * @param k
	 * @return
	 */
	public double[] getAveragePRF(Searcher searcher, int k)
	{
		/*********************** YOUR CODE HERE *************************/
		double Q = queries.size();

		double avg_precision = 0;
		double avg_recall = 0;
		double avg_F1 = 0;

		for(Document doc:queries)
		{
			// get precision, recall, and F1
			double[] PRF = getQueryPRF(doc, searcher, k);

			avg_precision += PRF[0];
			avg_recall += PRF[1];
			avg_F1 += PRF[2];
		}

		avg_precision /= Q;
		avg_recall /= Q;
		avg_F1 /= Q;

		return new double[] {avg_precision, avg_recall, avg_F1};
		/****************************************************************/
	}
}
