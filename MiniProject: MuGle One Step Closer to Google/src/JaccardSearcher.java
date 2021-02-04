//Name: Kanrawee Chiamsakul
//ID: 6188049
//Name: Tharit Chantanalertvilai
//ID: 6188068
//Name: Thanyanit Jongjitragan
//ID: 6188075
//Section: 2

import java.util.*;

public class JaccardSearcher extends Searcher{

	public JaccardSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/
		this.documents = super.documents;
		/***********************************************/
	}

	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/

		// create results List
		List<SearchResult> res = new ArrayList<SearchResult>();
		// tokenize query
		List<String> q_tokens = tokenize(queryString);
		// remove duplicates
		q_tokens = new ArrayList<String>(new LinkedHashSet<String>(q_tokens));
		
		double score;

		// loop the documents in corpus
		for(Document doc:documents)
		{
			// tokenize document's raw text
			List<String> raw_tokens = doc.getTokens();
			// remove duplicates
			raw_tokens = new ArrayList<String>(new LinkedHashSet<String>(raw_tokens));

			// if the query or raw text is an empty string
			if(queryString == "" || raw_tokens == null)
			{
				// score = 0
				SearchResult doc_res = new SearchResult(doc, 0);
				res.add(doc_res);
			}
			else
			{
				/* intersection */
				double intersect = 0;
				// create temporary List of query tokens
				List<String> cloned_q_tokens = new ArrayList<String>(q_tokens); 
				for(String token:raw_tokens)
				{
					if(cloned_q_tokens == null) break;
					else if(cloned_q_tokens.contains(token))
					{
						intersect++;
						// remove the token if the term is matched
						cloned_q_tokens.remove(token);
					}
				}
				
				/* union */
				double union = raw_tokens.size();
				union += cloned_q_tokens.size();
				
				// calculate Jaccard score
				score = intersect/union;
				SearchResult doc_res = new SearchResult(doc, score);
				res.add(doc_res);
			}
		}
		
		// sort by score
		Collections.sort(res);
		// return top k search results
		return (new ArrayList<>(res)).subList(0, k);
		/***********************************************/
	}

}
