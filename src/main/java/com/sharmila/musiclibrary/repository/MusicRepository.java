package com.sharmila.musiclibrary.repository;




import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.IOException;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharmila.musiclibrary.api.domain.Music;



@Component
public class MusicRepository {
	
	private static final Logger logger=LoggerFactory.getLogger(MusicRepository.class);

	private static final String clusterName="sharmila";
	private static Client client;
	private static Node node;

	
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
//			UpdateRequest updateRequest=new UpdateRequest("musiclibrary","music",music.getId())
//					.doc(jsonBuilder().startObject()
//					.field("singer",music.getSinger())
//					.field("composer",music.getComposer())
//					.field("title",music.getTitle())
//					.endObject()
//					);
//			client.update(updateRequest).get();
			String jsonData=new String(json);
			JSONObject obj=new JSONObject(jsonData);
			String singer=obj.getString("singer");
			System.out.println(singer);
			System.out.println(music.getSinger());
			UpdateRequest request = new UpdateRequest("musiclibrary", "music", "AVqtnu8J9qFlJEYqmZG4").doc(json);
			client.prepareUpdate();
			response=client.update(request).actionGet();
		//	node.close();
		} catch (   Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public String getById(String id){
	
		client=getClient();
		GetResponse getResponse=client.prepareGet("musiclibrary","music","AVqtnu8J9qFlJEYqmZG4")
				.execute().actionGet();
		//node.close();
		
		return getResponse.toString();
	}
}
