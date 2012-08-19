Storm Solr Integration
===========================

Integrates Storm and Apache Solr by providing a generic and configurable `backtype.storm.Bolt` 
implementation that writes Storm `Tuple` objects into Solr documents.

### Building from Source

		$ mvn install

### Usage

**Basic Usage**

The `SimpleSolrBolt` class provides a convenience constructor that takes only the URL where Solr is running as argument:	

		IRichBolt solrBolt = new SimpleSolrBolt("http://localhost:8983/solr");

The above constructor will create a `SolrBolt` that writes to the "`http://localhost:8983/solr`" Solr instance, and will try to convert 
every Tuple field into SolrInputDocument field. So make sure you have your tuple fields defined in your solr schema.xml.

For each field in the `backtype.storm.Tuple` received, the `SolrBolt` will write SolrInputField field/value pair into a SolrInputDocument.

For example, given a tuple value of:

		{id: 12345, username: "joaopedro", full_name: "João Pedro"}

Would must have these fields declared in your Solr schema.xml. Could be something like this :
	[...]
	 <fields>   
	  <field name="id"      	type="long"   	indexed="true"  stored="true"  multiValued="false" required="true"/>
	  <field name="full_name"   type="string"   indexed="true"  stored="true"  multiValued="false" /> 
	  <field name="username"    type="string"   indexed="true"  stored="true"  multiValued="false" /> 
	 </fields> 
	[...]




