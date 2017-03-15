package com.sharmila.musiclibrary.api;

import java.util.List;
import java.util.Map;

import com.sharmila.musiclibrary.api.domain.Music;
import com.sharmila.musiclibrary.api.domain.SearchTerms;

public interface MusicManager {
	
	void create(Music music);
	String delete(String id);
	void update(Music music);
	String getById(String id);
	void bulkTest(List<Music> music);
	String search(SearchTerms keyword);
	String searchScroll(SearchTerms keyword);
	List<Map<String,Object>>  searchAll();
	List<Map<String,Object>> sortByAscOrder(String fieldName);
	List<Map<String,Object>> sortByDescOrder(String fieldName);
	List<Map<String,Object>> sortPrice(String fieldName);
}
