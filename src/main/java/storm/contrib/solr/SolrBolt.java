package storm.contrib.solr;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.tuple.Tuple;


import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import org.apache.solr.client.solrj.SolrServerException;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichBolt;

/**
 * A Bolt for recording input tuples to Solr. Subclasses are expected to
 * provide the logic behind mapping input tuples to Solr objects.
 * 
 *
 * @author Arian Pasquali <arian@arianpasquali.com>
 *
 */
public abstract class SolrBolt extends BaseRichBolt {
	
	private OutputCollector collector;
	private SolrServer solrServer;
	private final String solrAddress;

	/**
	 * @param solrAddress The full URL address where Solr is running.
	 */
	protected SolrBolt(String solrAddress) {
		this.solrAddress = solrAddress;
	}
	
	@Override
	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
		
		this.collector = collector;
		try {
			this.solrServer = new HttpSolrServer(this.solrAddress);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void execute(Tuple input) {
		if (shouldActOnInput(input)) {
			
			SolrInputDocument document = getSolrInputDocumentForInput(input);
			
			if (document != null) {
				
				try {
					
					solrServer.add( document);
					collector.ack(input);
				} catch (SolrServerException e) {
					e.printStackTrace();
					collector.fail(input);
				} catch (IOException e) {
					
					e.printStackTrace();
					collector.fail(input);
				}
			}
		} else {
			collector.ack(input);
		}
	}

	/**
	 * Decide whether or not this input tuple should trigger a Solr write.
	 *
	 * @param input the input tuple under consideration
	 * @return {@code true} iff this input tuple should trigger a Solr write
	 */
	public abstract boolean shouldActOnInput(Tuple input);
	
	/**
	 * Returns the SolrInputDocument to store in Solr for the specified input tuple.
	 * 
	 * @param input the input tuple under consideration
	 * @return the SolrInputDocument to be written to Solr
	 */
	public abstract SolrInputDocument getSolrInputDocumentForInput(Tuple input);
	
	@Override
	public void cleanup() {
	}

}
