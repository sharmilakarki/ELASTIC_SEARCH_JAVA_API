package com.sharmila.musiclibrary.repository;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharmila.esclient.ElasticSearch;
import com.sharmila.musiclibrary.api.domain.Music;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Component
public class MusicRepository {

	private static final Logger logger = LoggerFactory.getLogger(MusicRepository.class);

	
	public static Map<String, Object> sourceMap = new HashMap<String, Object>();
	private static List<JSONObject> jsonData = new ArrayList<JSONObject>();

	private Client client = ElasticSearch.CLIENT.getInstance();
	public boolean create(Music music) {
		ObjectMapper mapper = new ObjectMapper(); // create once, reuse
		boolean value = false;
		// generate json
		try {
			byte[] json = mapper.writeValueAsBytes(music);
			System.out.println(json);
			
			IndexResponse response = client.prepareIndex("musiclibrary", "music").setSource(json).execute().actionGet();

			value = response.isCreated();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	public boolean delete(String id) {
		System.out.println("----" + id);
		
		DeleteResponse response = client.prepareDelete("musiclibrary", "music", id).execute().actionGet();

		return response.isFound();
	}

	public boolean update(Music music, String id) throws IOException {

		ObjectMapper mapper = new ObjectMapper();

		boolean value = false;
		try {
			byte[] json = mapper.writeValueAsBytes(music);
			

			IndexRequest indexRequest = new IndexRequest("musiclibrary", "music", id).source(jsonBuilder().startObject()
					.field("singer", music.getSinger()).field("composer", music.getComposer())
					.field("title", music.getTitle()).field("modifiedDate", music.getModifiedDate()).endObject());
			UpdateRequest updateRequest = new UpdateRequest("musiclibrary", "music", id).doc(jsonBuilder().startObject()
					.field("singer", music.getSinger()).field("composer", music.getComposer())
					.field("title", music.getTitle()).field("modifiedDate", music.getModifiedDate()).endObject())
					.upsert(indexRequest);

			// UpdateRequest updateRequest = new UpdateRequest("musiclibrary",
			// "music", id)
			// .doc(jsonBuilder().startObject()
			// .field("singer", music.getSinger())
			// .field("composer", music.getComposer())
			// .field("title", music.getTitle())
			// .field("modifiedDate",music.getModifiedDate())
			// .endObject());
			UpdateResponse response = client.update(updateRequest).get();

			value = response.isCreated();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	public List<Map<String, Object>> getById(String id) {

		
		GetResponse response = client.prepareGet("musiclibrary", "music", id).execute().actionGet();

		List<Map<String, Object>> mapList = new ArrayList<>();

		Map<String, Object> result = response.getSource();
		mapList.add(result);
		return mapList;

	}

	public List<Map<String, Object>> searchAll(String sortBy, String sortOrder, int size, int from) {
		
		SearchResponse response = null;
		SortOrder srtOrder;
		if (sortOrder.equalsIgnoreCase("ASC")) {
			srtOrder = SortOrder.ASC;
		}

		else {
			srtOrder = SortOrder.DESC;
		}

		System.out.println(
				"Repository---->> sort by " + sortBy + " sort order " + sortOrder + " size " + size + " from " + from);

		response = client.prepareSearch("musiclibrary").setTypes("music").addSort(sortBy, srtOrder).setSize(size)
				.setFrom(from).execute().actionGet();

		System.out.println(response.getHits().getTotalHits());
		SearchHit[] searchHits = response.getHits().getHits();
		List<Map<String, Object>> mapList = new ArrayList<>();

		for (SearchHit s : searchHits) {
			System.out.println();
			mapList.add(s.getSource());
			for (Map.Entry<String, Object> e : s.getSource().entrySet()) {
				System.out.println(e.getKey() + " " + e.getValue());
			}
			sourceMap.put("source", mapList);

		}

		return mapList;

	}

	
	public void bulk(String companyList){
		JSONParser parser = new JSONParser();
		
		Object obj;
		try {
			obj = parser.parse(companyList);
			
			 org.json.simple.JSONArray array = (org.json.simple.JSONArray)obj;
			 System.out.println(array.get(0));
			
			 BulkRequestBuilder reqBuilder=client.prepareBulk();
			 byte[] json;
			 for(int i=0;i<array.size();i++){
				
				try {
					json = new ObjectMapper().writeValueAsBytes(array.get(i));
					 reqBuilder.add(client.prepareIndex("musiclibrary","new").setSource(json));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			 }
			 reqBuilder.execute().actionGet();
		}catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
	
	public List<Map<String,String>> getCompanyLocation(){
		
	
		
		SearchResponse response = client.prepareSearch("musiclibrary").setTypes("new")
				.execute().actionGet();

		System.out.println(response.getHits().getTotalHits());
		SearchHit[] searchHits = response.getHits().getHits();
		List<Map<String, Object>> mapList = new ArrayList<>();
		Map<String,String> countryLocationMap=new HashMap<>();
		for (SearchHit s : searchHits) {
			System.out.println();
			mapList.add(s.getSource());
			for (Map.Entry<String, Object> e : s.getSource().entrySet()) {
				System.out.println(e.getKey() + " " + e.getValue());
				if(e.getKey().equals("company_linkedin_country")){
					countryLocationMap.put(e.getKey(), e.getValue().toString());
				}
			}
			sourceMap.put("source", mapList);

		}
		return null;
	}
		
}
