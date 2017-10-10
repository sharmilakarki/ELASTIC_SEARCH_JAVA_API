package com.sharmila.musiclibrary.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Repository;

import com.sharmila.esclient.ElasticSearch;

@Repository
public class RandomRepository {

	
	private static List<Map<String, String>> countryLocationList = new ArrayList<>();
	private static Map<String, String> countryLocationMap = new HashMap<>();
	public static Map<String, Object> sourceMap = new HashMap<String, Object>();

	private Client client = ElasticSearch.CLIENT.getInstance();

	public List<Map<String, String>> getCompanyLocation() {

		

		SearchResponse response = client.prepareSearch("musiclibrary").setTypes("new").execute().actionGet();

		SearchHit[] searchHits = response.getHits().getHits();

		for (SearchHit s : searchHits) {

			for (Map.Entry<String, Object> e : s.getSource().entrySet()) {
				if(e.getKey().equals("company_linkedin_name") ){
					System.out.println(" company name "+e.getValue());
				}
				if (e.getKey().equals("company_linkedin_state")) {
					// System.out.println("============"+
					// e.getValue().toString());
					if (e.getValue() == null) {
						countryLocationMap.put("state", "");
						 System.out.println("location : null ");
						
					} else {
						
						countryLocationMap.put("", e.getValue().toString());
						 System.out.println("location "+e.getValue().toString());
						countryLocationList.add(countryLocationMap);
					}

				}

			}

		}

		

		return countryLocationList;
	}
}
