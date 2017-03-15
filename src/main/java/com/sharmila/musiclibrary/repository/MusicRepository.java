package com.sharmila.musiclibrary.repository;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
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

	private static final Logger logger = LoggerFactory.getLogger(MusicRepository.class);

	private static final String clusterName = "sharmila";
	private static Client client;
	private static Node node;

	public static Map<String, Object> sourceMap = new HashMap<String, Object>();

	private static List<JSONObject> jsonData = new ArrayList<JSONObject>();

//	public Client getClient() {
//		// Settings settings = ImmutableSettings.settingsBuilder()
//		// .put("cluster.name", clusterName).build();
//		// client = new TransportClient(settings);
//		//
//		// client.addTransportAddress(new
//		// InetSocketTransportAddress("localhost",9200));
//		//
//		// return client;
//
//		node = nodeBuilder().clusterName("sharmila")
//				.settings(ImmutableSettings.settingsBuilder().put("node.client", true).put("number_of_shards", 1)
//						.put("client.transport.ping_timeout", "60s").put("cluster.name", clusterName).build())
//				.node();
//		client = node.client();
//
//		return client;
//	}

	
	public Client getClient() {
		Settings settings = ImmutableSettings.settingsBuilder()
		        .put("client.transport.sniff", true).put("number_of_shards", 3)
		        .put("node.client", true)
		        
				.put("client.transport.ping_timeout", "60s").put("cluster.name", clusterName).build();
	//	TransportClient client = new TransportClient(settings);
		Client client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
	
		return client;
	}
	public void create(Music music) {
		ObjectMapper mapper = new ObjectMapper(); // create once, reuse
		System.out.println("music -->" + music.getId());
		// generate json
		try {
			byte[] json = mapper.writeValueAsBytes(music);
			System.out.println(json);
			client = getClient();
			client.prepareIndex("musiclibrary", "music", music.getId()).setSource(json).execute().actionGet();
			node.close();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String delete(String id) {
		System.out.println("----" + id);
		client = getClient();
		DeleteResponse response = client.prepareDelete("musiclibrary", "music", id).execute().actionGet();
		System.out.println("response" + response);
		return response.toString();
	}

	public void update(Music music) throws IOException {

		ObjectMapper mapper = new ObjectMapper();

		UpdateResponse response = null;
		try {
			byte[] json = mapper.writeValueAsBytes(music);
			client = getClient();
			UpdateRequest updateRequest = new UpdateRequest("musiclibrary", "music", "AVqtnsWk9qFlJEYqmZG3")
					.doc(jsonBuilder().startObject().field("singer", music.getSinger())
							.field("composer", music.getComposer()).field("title", music.getTitle()).endObject());
			client.update(updateRequest).get();

			// UpdateRequest request = new UpdateRequest("musiclibrary",
			// "music", "AVqtnu8J9qFlJEYqmZG4").doc(json);
			// client.prepareUpdate();
			// response=client.update(request).actionGet();
			node.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getById(String id) {

		client = getClient();
		GetResponse getResponse = client.prepareGet("musiclibrary", "music", "AVqtnu8J9qFlJEYqmZG4").execute()
				.actionGet();
		node.close();

		return getResponse.toString();
	}

	@JsonProperty("parameters")
	@XmlElement(required = true)
	public void bulkTest(List<Music> music) {
		client = getClient();
		ObjectMapper mapper = new ObjectMapper();

		try {
			byte[] json = mapper.writeValueAsBytes(music);
			BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {

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
			}).setBulkActions(1000)

					.setFlushInterval(TimeValue.timeValueSeconds(5)).setConcurrentRequests(1)

					.build();

			bulkProcessor.add(new IndexRequest("musiclibrary", "music").source(json));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<Map<String, Object>> searchAll() {
		client = getClient();

		SearchResponse response = client.prepareSearch().execute().actionGet();
		// Map<String,Object> searchResponse=new HashMap<String,Object>();
		// searchResponse.put("took", response.getTookInMillis());
		// searchResponse.put("timedout", response.isTimedOut());
		// searchResponse.put("shards", response.getTotalShards());

		SearchHit[] searchHits = response.getHits().getHits();
		List<Map<String, Object>> mapList = new ArrayList<>();

		for (SearchHit s : searchHits) {

			mapList.add(s.getSource());
			sourceMap.put("source", mapList);

		}

		return mapList;

	}

	public String search(SearchTerms keyword) {
		client = getClient();
		SearchResponse response = client.prepareSearch(keyword.getIndex()).setTypes(keyword.getType())
				.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.setQuery(QueryBuilders.termQuery("singer", keyword.getSearchTerm())).setFrom(0).setSize(60)
				.setExplain(true).get();

		Long hits = response.getHits().getTotalHits();
		return hits.toString();
	}

	public String searchScroll(SearchTerms keyword) {
		client = getClient();

		// QueryBuilders
		// queryBuilders=TermQuery("singer",keyword.getSearchTerm());
		return null;
	}

	public List<Map<String, Object>> sortByAscOrder(String fieldName) {
		client = getClient();
		SearchResponse response = client.prepareSearch("musiclibrary").setTypes("music")
				// .setQuery(QueryBuilders.termQuery("composer", composer))
				.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.addSort(SortBuilders.fieldSort(fieldName).order(SortOrder.ASC)).execute().actionGet();

		List<Map<String, Object>> mapList = new ArrayList<>();
		
		SearchHit[] searchHits = response.getHits().getHits();
		
		for (SearchHit s : searchHits) {
			
			mapList.add(s.getSource());
//			for (Map.Entry<String, Object> m : s.getSource().entrySet()) {
//				System.out.println(m.getKey() + " " + m.getValue());
//			}
		}

		return mapList;
	}

	public List<Map<String, Object>> sortByDescOrder(String fieldName) {
		client = getClient();
		SearchResponse response = client.prepareSearch("musiclibrary").setTypes("music")
				.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.addSort(SortBuilders.fieldSort(fieldName).order(SortOrder.DESC)).execute().actionGet();

		List<Map<String, Object>> mapList = new ArrayList<>();
		
		SearchHit[] searchHits = response.getHits().getHits();
		
		for (SearchHit s : searchHits) {
			
			mapList.add(s.getSource());
		}

		return mapList;
	}

	public List<Map<String, Object>> sortBy(String fieldName) {
		client = getClient();
		SearchResponse response = client.prepareSearch("musiclibrary").setTypes("album")
				.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.addSort(SortBuilders.fieldSort(fieldName).order(SortOrder.ASC)).execute().actionGet();

		List<Map<String, Object>> mapList = new ArrayList<>();
		
		SearchHit[] searchHits = response.getHits().getHits();
		
		for (SearchHit s : searchHits) {
			
			mapList.add(s.getSource());
		}

		return mapList;
	}
}
