package com.sharmila.musiclibrary.repository;




import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder; 
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import org.elasticsearch.index.query.FilterBuilders.*;
import org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.common.xcontent.XContentFactory.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.lucene.search.TermQuery;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharmila.musiclibrary.api.domain.Music;
import com.sharmila.musiclibrary.api.domain.SearchTerms;




@Component
public class MusicRepository {
	
	private static final Logger logger=LoggerFactory.getLogger(MusicRepository.class);

	private static final String clusterName="sharmila";
	private static Client client;
	private static Node node;

	private static List<JSONObject> jsonData=new ArrayList<JSONObject>();
	
	public Client getClient(){
//		Settings settings = ImmutableSettings.settingsBuilder()
//		        .put("cluster.name", clusterName).build();
//		 client =    new TransportClient(settings);
//		
//		client.addTransportAddress(new InetSocketTransportAddress("localhost",9200));
//
//		return client;
		
		 node = nodeBuilder().clusterName("sharmila").settings(ImmutableSettings.settingsBuilder()
				 .put("node.client", true)
				 .put("number_of_shards",1)
				 .put("client.transport.ping_timeout", "60s")
				  .put("cluster.name", clusterName).build()).node();
		 client = node.client();
		 
		return client;
	}
	
	
	public void create(Music music) {
		ObjectMapper	 mapper = new ObjectMapper(); // create once, reuse
		System.out.println("music -->"+music.getId());
		// generate json
		try {
			byte[] json = mapper.writeValueAsBytes(music);
			System.out.println(json);
			client=getClient();
			client.prepareIndex("musiclibrary","music").setSource(json).execute().actionGet();
			node.close();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String delete(String id){
		System.out.println("----"+id);
		client=getClient();
		DeleteResponse response = client.prepareDelete("musiclibrary", "music", id)
		        .execute()
		        .actionGet();
		System.out.println("response"+response);
		return response.toString();
	}
	
	

	
	
	public void update(Music music) throws IOException{
		
		ObjectMapper	 mapper = new ObjectMapper();
		
		UpdateResponse response=null;
		try {
			byte[] json = mapper.writeValueAsBytes(music);
			client=getClient();
			UpdateRequest updateRequest=new UpdateRequest("musiclibrary","music","AVqtnsWk9qFlJEYqmZG3")
					.doc(jsonBuilder().startObject()
					.field("singer",music.getSinger())
					.field("composer",music.getComposer())
					.field("title",music.getTitle())
					.endObject()
					);
			client.update(updateRequest).get();
		
//			UpdateRequest request = new UpdateRequest("musiclibrary", "music", "AVqtnu8J9qFlJEYqmZG4").doc(json);
//			client.prepareUpdate();
//			response=client.update(request).actionGet();
			node.close();
		} catch (   Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public String getById(String id){
	
		client=getClient();
		GetResponse getResponse=client.prepareGet("musiclibrary","music","AVqtnu8J9qFlJEYqmZG4")
				.execute().actionGet();
		node.close();
		
		return getResponse.toString();
	}
	


	@JsonProperty( "parameters" )
	@XmlElement( required = true )
	public void bulkTest(List<Music> music){
		client=getClient();
		ObjectMapper mapper=new ObjectMapper();
		
		try{
			byte[] json=mapper.writeValueAsBytes(music);
			BulkProcessor bulkProcessor = BulkProcessor.builder(
			        client,  
			        new BulkProcessor.Listener() {
						
						@Override
						public void beforeBulk(long executionId, BulkRequest request) {
							System.out.println("---- before bulk");
							
						}
						
						@Override
						public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
							System.out.println("---- after bulk");
							
						}
						
						@Override
						public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
							System.out.println("---- after 1 bulk");
							
						}
					})
			        .setBulkActions(1000) 
			       
			        .setFlushInterval(TimeValue.timeValueSeconds(5)) 
			        .setConcurrentRequests(1) 
			        
			        .build();
			
			
			bulkProcessor.add(new IndexRequest("musiclibrary","music").source(json));
		}catch(Exception e){
			e.printStackTrace();
		}	
		
	}
	
	public String searchAll(){
		client=getClient();
		SearchResponse response=client.prepareSearch().execute().actionGet();
		Long hits=response.getHits().getTotalHits();
		return hits.toString();
		
	}
	
	public String search(SearchTerms keyword){
		client=getClient();
		SearchResponse response=client.prepareSearch(keyword.getIndex())
				.setTypes(keyword.getType())
				.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.setQuery(QueryBuilders.termQuery("singer", keyword.getSearchTerm()))
				  
		        .setFrom(0).setSize(60).setExplain(true)
		        .get();
		
		Long hits=response.getHits().getTotalHits();
				return hits.toString();
	}
	
	
	public String searchScroll(SearchTerms keyword){
		client=getClient();
		
	//	QueryBuilders queryBuilders=TermQuery("singer",keyword.getSearchTerm());
		return null;
	}
}
