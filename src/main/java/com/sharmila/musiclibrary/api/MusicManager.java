package com.sharmila.musiclibrary.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.sharmila.musiclibrary.api.domain.Company;
import com.sharmila.musiclibrary.api.domain.Music;
import com.sharmila.musiclibrary.api.domain.SearchTerms;

public interface MusicManager {
	
	boolean create(Music music);
	boolean delete(String id);
	boolean update(Music music,String id) throws IOException;
	List<Map<String, Object>>  getById(String id);
	void bulkTest(List<Company> cmp);
	void bulk(String companyList);
	List<Map<String,Object>>  searchAll(String sortBy,String sortOrder,int size,int from);
	
}
