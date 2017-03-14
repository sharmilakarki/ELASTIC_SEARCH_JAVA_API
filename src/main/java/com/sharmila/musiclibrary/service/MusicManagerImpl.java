package com.sharmila.musiclibrary.service;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sharmila.musiclibrary.api.MusicManager;
import com.sharmila.musiclibrary.api.domain.Music;
import com.sharmila.musiclibrary.api.domain.SearchTerms;
import com.sharmila.musiclibrary.repository.MusicRepository;


@Service
public class MusicManagerImpl implements MusicManager{

	private static final Logger logger=LoggerFactory.getLogger(MusicManagerImpl.class);
	@Autowired
	private MusicRepository musicRepository;
	
	
	@Override
	public void create(Music music) {
		musicRepository.create(music);
		
	}

	@Override
	public String delete(String id) {
		
		return musicRepository.delete(id);
	}

	@Override
	public void update(Music music) {
		try {
			musicRepository.update(music);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public String getById(String id) {
		
		return musicRepository.getById(id);
	}

	

	@Override
	public void bulkTest(List<Music> music) {
		
		 musicRepository.bulkTest(music);
	}

	@Override
	public String search(SearchTerms keyword) {
		
		return musicRepository.search(keyword);
	}

	@Override
	public String searchScroll(SearchTerms keyword) {
		
		return musicRepository.searchScroll(keyword);
	}

	@Override
	public String searchAll() {
		
		return musicRepository.searchAll();
	}
	
	
	
}
